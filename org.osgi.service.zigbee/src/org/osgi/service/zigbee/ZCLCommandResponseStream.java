/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

package org.osgi.service.zigbee;

import org.osgi.util.function.Predicate;

/**
 * This type represents a stream of responses to a broadcast operation. It can
 * be closed by the client using the {@link #close} method is called.
 * 
 * The {@link ZCLCommandResponseStream} is used to process a stream of responses
 * from a ZigBee network. Responses are consumed by registering a handler with
 * {@link #forEach(Predicate)}. Responses received before a handler is
 * registered are buffered until a handler is registered, or until the close
 * method is called.
 * 
 * A handler consumes events returning <code>true</code> to continue delivery.
 * At some point the ZigBee service invocation will terminate event delivery by
 * sending a close event (a {@link ZCLCommandResponse} which returns
 * <code>true</code> from {@link ZCLCommandResponse#isEnd()}. After a close
 * event the handler function will be dereferenced.
 */
public interface ZCLCommandResponseStream {

	/**
	 * Closes this response, indicating that no further responses are needed.
	 * 
	 * Any buffered responses will be discarded, and a close event will be sent
	 * to a handler if it is registered.
	 * 
	 */
	void close();

	/**
	 * Registers a handler that will be called for each of the received
	 * responses. Only one handler may be registered. Any responses that arrive
	 * before a handler is registered will be buffered and pushed into the
	 * handler when it is registered.
	 * 
	 * If the handler returns <code>false</code> from its accept method then the
	 * handler will be released and no further events will be delivered. Any
	 * remaining buffered events will be discarded, and this object marked as
	 * closed.
	 * 
	 * If the handler does not close the stream early then the ZigBee service
	 * implementation will eventually send a close event.
	 * 
	 * @param handler A handler to process ZCLCommandResponse objects
	 * @throws IllegalStateException if a handler has already been registered,
	 *         or if this object has been closed (a {@link ZCLCommandResponse}
	 *         which returns <code>true</code> from
	 *         {@link ZCLCommandResponse#isEnd()}. After a close event the
	 *         handler function will be dereferenced.
	 *
	 */
	void forEach(Predicate< ? super ZCLCommandResponse> handler);
}
