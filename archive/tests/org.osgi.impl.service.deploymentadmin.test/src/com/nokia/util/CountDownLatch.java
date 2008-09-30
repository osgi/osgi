package com.nokia.util;
public class CountDownLatch {
	
	private int count;

	public CountDownLatch(int count) {
		this.count = count;
	}

	public synchronized void CountDown() {
		--count;
		if (count <= 0)
			notifyAll();
	}
	
	public synchronized void await() {
		while (count > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}
	
	public synchronized boolean await(long time) {
		if (count > 0)
			try {
				wait(time);
			} catch (InterruptedException e) {
			}
		return count <= 0;
	}
	
}
