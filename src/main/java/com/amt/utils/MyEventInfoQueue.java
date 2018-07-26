package com.amt.utils;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zw on 2017/5/20
 */
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
		return queue.poll();
		
	}

	public boolean isEmpty() {
		return queue.isEmpty();
		
	}
	
	public void removeAll() {
		Iterator<Object> i = queue.iterator();
		while(i.hasNext()) {
			queue.remove();
		}
	}
}
