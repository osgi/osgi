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

import static java.util.Optional.ofNullable;
import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class UnbufferedPushStreamImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
	extends AbstractPushStreamImpl<T> implements PushStream<T> {
	
	protected final Function<PushEventConsumer<T>,AutoCloseable>	connector;
	
	protected final AtomicReference<AutoCloseable>					upstream	= new AtomicReference<AutoCloseable>();
	
	UnbufferedPushStreamImpl(PushStreamProvider psp,
			PushStreamExecutors executors,
			Function<PushEventConsumer<T>,AutoCloseable> connector) {
		super(psp, executors);
		this.connector = connector;
	}

	@Override
	protected boolean close(PushEvent<T> event, boolean sendDownStreamEvent) {
		if (super.close(event, sendDownStreamEvent)) {
			upstreamClose(event);
			return true;
		}
		return false;
	}

	@Override
	protected void upstreamClose(PushEvent< ? > close) {
		ofNullable(upstream.getAndSet(() -> {
			// This block doesn't need to do anything, but the presence
			// of the Closable is needed to prevent duplicate begins
		})).ifPresent(c -> {
				try {
					c.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
	}

	@Override
	protected boolean begin() {
		if(closed.compareAndSet(BUILDING, STARTED)) {
			AutoCloseable toClose = connector.apply(this::handleEvent);
			if(!upstream.compareAndSet(null,toClose)) {
				//TODO log that we tried to connect twice...
				try {
					toClose.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (closed.get() == CLOSED
					&& upstream.compareAndSet(toClose, null)) {
				// We closed before setting the upstream - close it now
				try {
					toClose.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
}
