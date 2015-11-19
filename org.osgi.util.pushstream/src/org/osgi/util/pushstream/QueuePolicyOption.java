package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;

public enum QueuePolicyOption { 
	DISCARD_OLDEST {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue,event) -> {
				while(!queue.offer(event)) {
					queue.poll();
				}
			};
		}
	}, BLOCK {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue,event) -> {
				try {
					queue.put(event);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}
	}, FAIL {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue,event) -> queue.add(event);
		}
	};
	
	public abstract <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy();

}