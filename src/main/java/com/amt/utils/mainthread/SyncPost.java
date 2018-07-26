package com.amt.utils.mainthread;

public class SyncPost {

	boolean end = false;

	Runnable runnable;

	SyncPost(Runnable runnable) {
		this.runnable = runnable;
	}

	public void run() {
		//进入同步块，然后调用Runnable接口的run方法。同时在执行完成后将end重置为true;
		//然后调用this.notifyAll();通知等待的部分可以继续了，当然有这样的情况；假如在进入该同步块的时候子线程还未执行到this.wait();部分呢？
		//所以我们为此准备了end和try。
		synchronized (this) {
			runnable.run();
			end = true;
			try {
				this.notifyAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void waitRun() {
		//首先判断状态，如果状态已经变了，那么证明子线程执行到此处时，主线程已经执行了void_run()。
		//所以也就不用进入同步块进行等待了。反之进入等待直到主线程调用this.notifyAll();
		if (!end) {
			synchronized (this) {
				if (!end) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
