package org.osgi.util.pushstream;

import static org.osgi.util.pushstream.PushEvent.EventType.CLOSE;
import static org.osgi.util.pushstream.PushEvent.EventType.DATA;
import static org.osgi.util.pushstream.PushEvent.EventType.ERROR;

/**
 * An AsyncEvent is an immutable object that is transferred through a
 * communication channel to push information to a downstream consumer. The event
 * has three different subtypes:
 * <ul>
 * <li>Data – Provides access to a typed data element in the stream
 * <li>Close – The stream is closed. After receiving this event, no more events
 * will follow and and the consumer must assume the stream is dead.
 * <li>Error – The upstream ran into an irrecoverable problem and is sending the
 * reason downstream. No more events will follow after this event
 * </ul>
 *
 * @param <T>
 *            The associated Data type
 */
public final class PushEvent<T> {

	public static enum EventType { DATA, ERROR, CLOSE };
	
	private final T data;
	private final Exception failure;
	private final EventType type;
	
	private PushEvent(T data, Exception failure, EventType type) {
		this.data = data;
		this.failure = failure;
		this.type = type;
	}

	/**
	 * Get the type of this event
	 * @return
	 */
	public EventType getType() {
		return type;
	}
	
	/**
	 * Return the data for this event
	 * or throw an exception
	 * 
	 * @return the data payload
	 */
	public T getData() throws IllegalStateException {
		switch(type) {
			case DATA : return data;
			default : throw new IllegalStateException("Not a data event: " + type);
		}
	}

	/**
	 * Return the error or throw an exception if this is not an error type
	 * 
	 * @return the exception
	 */
	public Exception getFailure() {
		switch(type) {
			case ERROR : return failure;
			default : throw new IllegalStateException("Not a failure event: " + type);
		}
	}

	/**
	 * Answer if no more events will follow after this event.
	 * 
	 * @return true if a data event, otherwise false.
	 */
	public boolean isTerminal() {
		switch(type) {
			case DATA : return false;
			default : return true;
		}
	}

	/**
	 * Create a new data event
	 * 
	 * @param payload
	 *            The payload
	 * @return
	 */
	public static <T> PushEvent<T> data(T payload) {
		return new PushEvent<T>(payload, null, DATA);
	}

	/**
	 * Create a new error event
	 * 
	 * @param e
	 *            The error
	 * @return a new error event with the given error
	 */
	public static <T> PushEvent<T> error(Exception e) {
		return new PushEvent<T>(null, e, ERROR);
	}

	/**
	 * Create a new close event.
	 * 
	 * @return A close event
	 */
	public static <T> PushEvent<T> close() {
		return new PushEvent<T>(null, null, CLOSE);
	}

	/**
	 * Convenience to cast a close/error event to another payload type. Since
	 * the payload type is not needed for these events this is harmless. This
	 * therefore allows you to forward the close/error event downstream without
	 * creating anew event.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <X> PushEvent<X> nodata() {
		switch(type) {
			case DATA : throw new IllegalStateException("This is a data event");
			default : return (PushEvent<X>) this;
		}
	}
}
