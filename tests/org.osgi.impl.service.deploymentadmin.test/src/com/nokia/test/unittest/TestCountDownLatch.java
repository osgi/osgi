package com.nokia.test.unittest;

import com.nokia.util.CountDownLatch;

import junit.framework.TestCase;

public class TestCountDownLatch extends TestCase {
	
	private int[]          check = new int[1];
	private boolean        checkTimeout = true;
	private CountDownLatch latchStart;
	private CountDownLatch latchStop;
	
	private class Other extends Thread {
		
		private String name;
		private long   timeout;
		
		public Other(String name) {
			this(name, 0);
		}
		
		public Other(String name, long timeout) {
			this.name = name;
			this.timeout = timeout;
		}
		
		public void run() {
			super.run();
			if (timeout <= 0) {
				latchStart.await();
				decCheck();
			} else 
				checkTimeout = latchStart.await(timeout);
			latchStop.CountDown();
		}
		
	}
	
	private void decCheck() {
		synchronized(check) {
			--check[0];
		}
	}

	private void incCheck() {
		synchronized(check) {
			++check[0];
		}
	}
	
	private void resetCheck() {
		synchronized(check) {
			check[0] = 0;
		}
	}
	
	private synchronized int getCheck() {
		synchronized(check) {
			return check[0];
		}
	}
	
	public void test01() {
		resetCheck();
		latchStart = new CountDownLatch(1);
		latchStop = new CountDownLatch(1);
		new Other("1").start();
		synchronized (this) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				incCheck();
				latchStart.CountDown();
			}
			latchStop.await();
			assertTrue(getCheck() == 0);
		}
	}
	
	public void test02() {
		resetCheck();
		latchStart = new CountDownLatch(2);
		latchStop = new CountDownLatch(1);
		new Other("1").start();
		new Other("2").start();
		synchronized (this) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				incCheck();
				incCheck();
				latchStart.CountDown();
				latchStart.CountDown();
			}
			latchStop.await();
			assertTrue(getCheck() == 0);
		}
	}

	public void test03() {
		latchStart = new CountDownLatch(1);
		latchStop = new CountDownLatch(1);
		new Other("1", 100).start();
		synchronized (this) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				latchStart.CountDown();
			}
			latchStop.await();
			assertTrue(!checkTimeout);
		}
	}
	
}
