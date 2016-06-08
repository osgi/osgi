package org.osgi.util.pushstream;

import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.*;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

class IntermediatePushStreamImpl<T> extends AbstractPushStreamImpl<T>
		implements PushStream<T> {
	
	private final AbstractPushStreamImpl< ? > previous;
	
	IntermediatePushStreamImpl(PushStreamProvider psp,
			Executor executor, ScheduledExecutorService scheduler,
			AbstractPushStreamImpl< ? > previous) {
		super(psp, executor, scheduler);
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
	
}
