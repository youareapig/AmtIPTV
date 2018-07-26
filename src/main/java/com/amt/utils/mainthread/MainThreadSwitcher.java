package com.amt.utils.mainthread;

import android.os.Looper;

/**
 * 网上找到的关于子线程切换到主线程的代码,实现子线程任意时刻切换到主线程，并可选择地阻塞子线程。
 * 此方案避免handler满天飞的情况。
 * 参考资料：http://c.jinhusns.com/cms/c-884
 * Created by DonWZ on 2016-10-18
 */
public class MainThreadSwitcher {
	private static HandlerPoster mainPoster = null;

	private static HandlerPoster getMainPoster() {
		if (mainPoster == null) {
			synchronized (MainThreadSwitcher.class) {
				if (mainPoster == null) {
					mainPoster = new HandlerPoster(Looper.getMainLooper(), 500);//限制主线程单次运行时间
				}
			}
		}
		return mainPoster;
	}

	/**
	 * Asynchronously.
	 * The child thread asynchronous run relative to the main thread,
	 * not blocking the child thread
	 * @param runnable
	 *            Runnable Interface
	 */
	public static void runOnMainThreadAsync(Runnable runnable) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			runnable.run();
			return;
		}
		try {
			getMainPoster().async(runnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Synchronously.
	 * The child thread relative thread synchronization operation,
	 * blocking the child thread,
	 * thread for the main thread to complete
	 * @param runnable
	 *            Runnable Interface
	 */
	public static void runOnMainThreadSync(Runnable runnable) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			runnable.run();
			return;
		}
		SyncPost poster = new SyncPost(runnable);
		try {
			getMainPoster().sync(poster);
		} catch (Exception e) {
			e.printStackTrace();
		}
		poster.waitRun();
	}

	public static void dispose() {
		if (mainPoster != null) {
			mainPoster.dispose();
			mainPoster = null;
		}
	}
}
