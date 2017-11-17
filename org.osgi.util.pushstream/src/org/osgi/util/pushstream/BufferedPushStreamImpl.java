/*
 * Copyright (c) OSGi Alliance (2015, 2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.util.pushstream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.CLOSED;
import static org.osgi.util.pushstream.PushEventConsumer.ABORT;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.osgi.util.promise.PromiseFactory;

class BufferedPushStreamImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		extends UnbufferedPushStreamImpl<T,U> implements PushStream<T> {
	
	private final U eventQueue;
	
	private final Semaphore semaphore;
	
	private final QueuePolicy<T, U> queuePolicy;

	private final PushbackPolicy<T, U> pushbackPolicy;
	
	/**
	 * Indicates that a terminal event has been received, that we should stop
	 * collecting new events, and that we must drain the buffer before
	 * continuing
	 */
	private final AtomicBoolean			softClose	= new AtomicBoolean();

	private final int					parallelism;

	BufferedPushStreamImpl(PushStreamProvider psp,
			PromiseFactory promiseFactory, U eventQueue, int parallelism,
			QueuePolicy<T,U> queuePolicy,
			PushbackPolicy<T,U> pushbackPolicy,
			Function<PushEventConsumer<T>,AutoCloseable> connector) {
		super(psp, promiseFactory, connector);
		this.eventQueue = eventQueue;
		this.parallelism = parallelism;
		this.semaphore = new Semaphore(parallelism);
		this.queuePolicy = queuePolicy;
		this.pushbackPolicy = pushbackPolicy;
	}

	@Override
	protected long handleEvent(PushEvent< ? extends T> event) {

		// If we have already been soft closed, or hard closed then abort
		if (!softClose.compareAndSet(false, event.isTerminal())
				|| closed.get() == CLOSED) {
			return ABORT;
		}

		try {
			queuePolicy.doOffer(eventQueue, event);
			long backPressure = pushbackPolicy.pushback(eventQueue);
			if(backPressure < 0) {
				close();
				return ABORT;
			}
			if(semaphore.tryAcquire()) {
				startWorker();
			}
			return backPressure;
		} catch (Exception e) {
			close(PushEvent.error(e));
			return ABORT;
		}
	}

	private void startWorker() {
		promiseFactory.executor().execute(() -> {
			try {
				PushEvent< ? extends T> event;
				while ((event = eventQueue.poll()) != null) {
					if (event.isTerminal()) {
						// Wait for the other threads to finish
						semaphore.acquire(parallelism - 1);
					}

					long backpressure = super.handleEvent(event);
					if(backpressure < 0) {
						close();
						return;
					} else if(backpressure > 0) {
						promiseFactory.scheduledExecutor().schedule(
								this::startWorker, backpressure,
								MILLISECONDS);
						return;
					}
				}
				// Only release this now the queue is empty
				semaphore.release();
			} catch (Exception e) {
				close(PushEvent.error(e));
			}
			if(eventQueue.peek() != null && semaphore.tryAcquire()) {
				try {
					startWorker();
				} catch (Exception e) {
					close(PushEvent.error(e));
				}
			}
		});
		
	}
}
