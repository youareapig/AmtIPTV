package com.amt.player;

import android.os.Handler;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyEventInfoQueue {
	
	private int size;
	
	private Queue<Object> queue;

	public MyEventInfoQueue(int size) {
		this.size = size;
		queue = new LinkedBlockingQueue<Object>(size);
	}
	
	public void put(Object obj) {
		if (obj != null) {
			queue.offer(obj);
		}
	}

	public Object get() {
		if (queue.size() <= 0) {
			return null;
		}
		Object o = queue.peek();
		if (queue.size() >= 1) {
			queue.remove();
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (queue.size() > 0) {
						queue.remove();
					}
				}
			}, 1000);
		}
		return o;
		
	}

	public boolean isEmpty() {
		return queue.isEmpty();
		
	}
	
	public void removeAll() {
		Iterator<Object> i = queue.iterator();
		while(i.hasNext()) {
			queue.remove(i.next());
		}
	}
}
