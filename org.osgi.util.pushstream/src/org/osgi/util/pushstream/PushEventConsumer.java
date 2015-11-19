package org.osgi.util.pushstream;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * An Async Event Consumer asynchronously receives Data events until it receives
 * either a Close or Error event.
 * 
 * @param <T>
 *            The type for the event payload
 */
@ConsumerType
@FunctionalInterface
public interface PushEventConsumer<T> {

	/**
	 * If ABORT is used as return value, the sender should close the channel all
	 * the way to the upstream source. The ABORT will not guarantee that no
	 * more events are delivered since this is impossible in a concurrent
	 * environment. The consumer should accept subsequent events and close/clean
	 * up when the Close or Error event is received.
	 * 
	 * Though ABORT has the value -1, any value less than 0 will act as an
	 * abort.
	 */
	long	ABORT		= -1;

	/**
	 * A 0 indicates that the consumer is willing to receive subsequent events
	 * at full speeds.
	 * 
	 * Any value more than 0 will indicate that the consumer is becoming
	 * overloaded and wants a delay of the given milliseconds before the next
	 * event is sent. This allows the consumer to pushback the event delivery
	 * speed.
	 */
	long	CONTINUE	= 0;

	/**
	 * Accept an event from a source. Events can be delivered on multiple
	 * threads simultaneously. However, Close and Error events are the last
	 * events received, no more events must be sent after them.
	 * 
	 * @param event
	 *            The event
	 * @return less than 0 means abort, 0 means continue, more than 0 means
	 *         delay ms
	 */
	long accept(PushEvent<? extends T> event) throws Exception;

}
