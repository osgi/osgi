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

import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.*;

import org.osgi.util.promise.PromiseFactory;

class IntermediatePushStreamImpl<T> extends AbstractPushStreamImpl<T>
		implements PushStream<T> {
	
	private final AbstractPushStreamImpl< ? > previous;
	
	IntermediatePushStreamImpl(PushStreamProvider psp,
			PromiseFactory promiseFactory,
			AbstractPushStreamImpl< ? > previous) {
		super(psp, promiseFactory);
		this.previous = previous;
	}

	@Override
	protected boolean begin() {
		if(closed.compareAndSet(BUILDING, STARTED)) {
			beginning();
			previous.begin();
			return true;
		}
		return false;
	}

	protected void beginning() {
		// The base implementation has nothing to do, but
		// this method is used in windowing
	}

	@Override
	protected void upstreamClose(PushEvent< ? > close) {
		if (closed.get() != CLOSED) {
			close(close.nodata(), false);
		}
		previous.upstreamClose(close);
	}
	
}
