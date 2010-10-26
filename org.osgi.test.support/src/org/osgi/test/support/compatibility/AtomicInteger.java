package org.osgi.test.support.compatibility;

public class AtomicInteger {
	int	value;

	public AtomicInteger(int value) {
		set(value);
	}

	public AtomicInteger() {
	}

	public final synchronized int get() {
		return value;
	}

	public final synchronized int set(int value) {
		int old = this.value;
		this.value = value;
		return old;
	}

	public final int getAndSet(int newValue) {
		for (;;) {
			int current = get();
			if (compareAndSet(current, newValue))
				return current;
		}
	}

	public synchronized boolean compareAndSet(int old, int next) {
		if (value == old) {
			value = next;
			return true;
		}
		return false;
	}

	public final synchronized int addAndGet(int value) {
		this.value += value;
		return this.value;
	}

	public final int decrementAndGet() {
		return addAndGet(-1);
	}

	public final int getAndAdd(int value) {
		int old = this.value;
		this.value += value;
		return old;
	}

	public final int getAndDecrement() {
		return getAndAdd(-1);
	}

	public final int getAndIncrement() {
		return getAndAdd(+1);
	}

	public final int incrementAndGet() {
		return addAndGet(+1);
	}

	public int intValue() {
		return this.value;
	}
}
