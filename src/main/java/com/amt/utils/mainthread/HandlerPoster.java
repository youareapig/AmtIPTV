package com.amt.utils.mainthread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.LinkedList;
import java.util.Queue;

public class HandlerPoster extends Handler {

	private final int ASYNC = 1;

	private final int SYNC = 2;

	private final Queue<Runnable> asyncPool;

	private final Queue<SyncPost> syncPool;

	private final int maxMillisInsideHandleMessage;

	private boolean asyncActive;//执行状态。避免重复发送消息导致消息队列过多

	private boolean syncActive;//执行状态。避免重复发送消息导致消息队列过多

	HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
		super(looper);
		this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
		asyncPool = new LinkedList<Runnable>();
		syncPool = new LinkedList<SyncPost>();
	}

	void dispose() {
		this.removeCallbacksAndMessages(null);
		this.asyncPool.clear();
		this.syncPool.clear();
	}

	void async(Runnable runnable) throws Exception {
		synchronized (asyncPool) {
			asyncPool.offer(runnable);
			//判断当前是否有异步任务正在执行
			if (!asyncActive) {
				asyncActive = true;
				if (!sendMessage(obtainMessage(ASYNC))) {
					throw new Exception("Could not send handler message");
				}
			}
		}
	}

	void sync(SyncPost post) throws Exception {
		synchronized (syncPool) {
			syncPool.offer(post);
			//判断当前是否有同步任务正在执行
			if (!syncActive) {
				syncActive = true;
				if (!sendMessage(obtainMessage(SYNC))) {
					throw new Exception("Could not send handler message");
				}
			}
		}
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == ASYNC) {
			boolean rescheduled = false;
			try {
				//当执行完一个任务后就判断一次是否超过时间限制，如果超过，那么不管队列中的任务是否执行完成都退出，同时发起一个新的消息到Handler循环队列
				//在while部分，使用poll从队列取出一个任务，判断是否为空，如果为空进入队列同步块；然后再取一次，再次判断。
				//如果恰巧在进入同步队列之前有新的任务来了，那么第二次取到的当然就不是 NULL也就会继续执行下去。
				//反之，如果还是为空；那么重置当前队列的状态为false,同时跳出循环。
				long started = SystemClock.uptimeMillis();
				while (true) {
					Runnable runnable = asyncPool.poll();
					if (runnable == null) {
						synchronized (asyncPool) {
							// Check again, this time in synchronized
							runnable = asyncPool.poll();
							if (runnable == null) {
								asyncActive = false;
								return;
							}
						}
					}
					runnable.run();
					long timeInMethod = SystemClock.uptimeMillis() - started;
					if (timeInMethod >= maxMillisInsideHandleMessage) {
						if (!sendMessage(obtainMessage(ASYNC))) {
							throw new Exception("Could not send handler message");
						}
						rescheduled = true;
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				asyncActive = rescheduled;
			}
		} else if (msg.what == SYNC) {
			boolean rescheduled = false;
			try {
				long started = SystemClock.uptimeMillis();
				while (true) {
					SyncPost post = syncPool.poll();
					if (post == null) {
						synchronized (syncPool) {
							// Check again, this time in synchronized
							post = syncPool.poll();
							if (post == null) {
								syncActive = false;
								return;
							}
						}
					}
					post.run();
					long timeInMethod = SystemClock.uptimeMillis() - started;
					if (timeInMethod >= maxMillisInsideHandleMessage) {
						if (!sendMessage(obtainMessage(SYNC))) {
							throw new Exception("Could not send handler message");
						}
						rescheduled = true;
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				syncActive = rescheduled;
			}
		} else
			super.handleMessage(msg);
	}
}
