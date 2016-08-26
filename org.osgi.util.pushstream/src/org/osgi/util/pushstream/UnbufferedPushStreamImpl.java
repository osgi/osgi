package org.osgi.util.pushstream;

import static java.util.Optional.ofNullable;
import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class UnbufferedPushStreamImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
	extends AbstractPushStreamImpl<T> implements PushStream<T> {
	
	protected final Function<PushEventConsumer<T>,AutoCloseable>	connector;
	
	protected final AtomicReference<AutoCloseable>					upstream	= new AtomicReference<AutoCloseable>();
	
	UnbufferedPushStreamImpl(PushStreamProvider psp,
			Executor executor, ScheduledExecutorService scheduler,
			Function<PushEventConsumer<T>,AutoCloseable> connector) {
		super(psp, executor, scheduler);
		this.connector = connector;
	}

	@Override
	protected boolean close(PushEvent<T> event) {
		if(super.close(event)) {
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
			return true;
		}
		return false;
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
