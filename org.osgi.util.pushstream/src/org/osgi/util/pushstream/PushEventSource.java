package org.osgi.util.pushstream;

import java.io.Closeable;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * An event source. An event source can open a channel between a source and a
 * consumer. Once the channel is opened (even before it returns) the source can
 * send events to the consumer.
 *
 * A source should stop sending and automatically close the channel when sending
 * an event returns a negative value, see {@link PushEventConsumer#ABORT}.
 * Values that are larger than 0 should be treated as a request to delay the
 * next events with those number of milliseconds.
 * 
 * @param <T>
 *            The payload type
 */
@ConsumerType
@FunctionalInterface
public interface PushEventSource<T> {

	/**
	 * Open the asynchronous channel between the source and the consumer. The
	 * call returns a Closeable. This closeable can be closed, this should close
	 * the channel, including sending a Close event if the channel was not
	 * already closed. The closeable must be able to be closed multiple times without
	 * sending more than one Close events.
	 * 
	 * @param aec
	 *            the consumer (not null)
	 * @return a Closeable to 
	 */
	Closeable open(PushEventConsumer<? super T> aec) throws Exception;
}
