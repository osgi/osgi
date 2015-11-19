package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public enum PushbackPolicyOption { 
	
	FIXED {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> value;
		}
	}, ON_FULL_FIXED {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> q.remainingCapacity() == 0 ? value : 0;
		}
	}, ON_FULL_EXPONENTIAL {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			AtomicInteger backoffCount = new AtomicInteger(0);
			return q -> {
				if(q.remainingCapacity() == 0) { 
					return value << backoffCount.getAndIncrement();
				}
				backoffCount.set(0);
				return 0;
			};

		}
	}, ON_FULL_CLOSE {
		@Override
		public
		<T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> q.remainingCapacity() == 0 ? -1 : 0;
		}
	}, LINEAR {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> {
				long remainingCapacity = (long)q.remainingCapacity();
				long used = (long)q.size();
				return (value * used) / (used + remainingCapacity);
			};
		}
	};
	
	public abstract <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value);
	
}