/*
 * Copyright (c) OSGi Alliance (2015, 2016). All Rights Reserved.
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

import static org.osgi.util.pushstream.PushEvent.EventType.*;

/**
 * A PushEvent is an immutable object that is transferred through a
 * communication channel to push information to a downstream consumer. The event
 * has three different types:
 * <ul>
 * <li>{@link EventType#DATA} – Provides access to a typed data element in the
 * stream.
 * <li>{@link EventType#CLOSE} – The stream is closed. After receiving this
 * event, no more events will follow.
 * <li>{@link EventType#ERROR} – The stream ran into an unrecoverable problem
 * and is sending the reason downstream. The stream is closed and no more events
 * will follow after this event.
 * </ul>
 *
 * @param <T> The payload type of the event.
 * @Immutable
 */
public abstract class PushEvent<T> {

	/**
	 * The type of a {@link PushEvent}.
	 */
	public static enum EventType {
		/**
		 * A data event forming part of the stream
		 */
		DATA,
		/**
		 * An error event that indicates streaming has failed and that no more
		 * events will arrive
		 */
		ERROR,
		/**
		 * An event that indicates that the stream has terminated normally
		 */
		CLOSE
	}

	/**
	 * Package private default constructor.
	 */
	PushEvent() {}

	/**
	 * Get the type of this event.
	 * 
	 * @return The type of this event.
	 */
	public abstract EventType getType();

	/**
	 * Return the data for this event.
	 * 
	 * @return The data payload.
	 * @throws IllegalStateException if this event is not a
	 *             {@link EventType#DATA} event.
	 */
	public T getData() throws IllegalStateException {
		throw new IllegalStateException(
				"Not a DATA event, the event type is " + getType());
	}

	/**
	 * Return the error that terminated the stream.
	 * 
	 * @return The error that terminated the stream.
	 * @throws IllegalStateException if this event is not an
	 *             {@link EventType#ERROR} event.
	 */
	public Exception getFailure() throws IllegalStateException {
		throw new IllegalStateException(
				"Not an ERROR event, the event type is " + getType());
	}

	/**
	 * Answer if no more events will follow after this event.
	 * 
	 * @return {@code false} if this is a data event, otherwise {@code true}.
	 */
	public boolean isTerminal() {
		return true;
	}

	/**
	 * Create a new data event.
	 * 
	 * @param <T> The payload type.
	 * @param payload The payload.
	 * @return A new data event wrapping the specified payload.
	 */
	public static <T> PushEvent<T> data(T payload) {
		return new DataEvent<T>(payload);
	}

	/**
	 * Create a new error event.
	 * 
	 * @param <T> The payload type.
	 * @param e The error.
	 * @return A new error event with the specified error.
	 */
	public static <T> PushEvent<T> error(Exception e) {
		return new ErrorEvent<T>(e);
	}

	/**
	 * Create a new close event.
	 * 
	 * @param <T> The payload type.
	 * @return A new close event.
	 */
	public static <T> PushEvent<T> close() {
		return new CloseEvent<T>();
	}

	/**
	 * Convenience to cast a close/error event to another payload type. Since
	 * the payload type is not needed for these events this is harmless. This
	 * therefore allows you to forward the close/error event downstream without
	 * creating anew event.
	 * 
	 * @param <X> The new payload type.
	 * @return The current error or close event mapped to a new payload type.
	 * @throws IllegalStateException if the event is a {@link EventType#DATA}
	 *             event.
	 */
	public <X> PushEvent<X> nodata() throws IllegalStateException {
		@SuppressWarnings("unchecked")
		PushEvent<X> result = (PushEvent<X>) this;
		return result;
	}

	static final class DataEvent<T> extends PushEvent<T> {
		private final T data;

		DataEvent(T data) {
			this.data = data;
		}

		@Override
		public T getData() throws IllegalStateException {
			return data;
		}

		@Override
		public EventType getType() {
			return DATA;
		}

		@Override
		public boolean isTerminal() {
			return false;
		}

		@Override
		public <X> PushEvent<X> nodata() throws IllegalStateException {
			throw new IllegalStateException("This event is a DATA event");
		}
	}

	static final class ErrorEvent<T> extends PushEvent<T> {
		private final Exception error;

		ErrorEvent(Exception error) {
			this.error = error;
		}

		@Override
		public Exception getFailure() {
			return error;
		}

		@Override
		public EventType getType() {
			return ERROR;
		}
	}

	static final class CloseEvent<T> extends PushEvent<T> {
		@Override
		public EventType getType() {
			return CLOSE;
		}
	}
}
