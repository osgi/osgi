/*
 * Copyright (c) OSGi Alliance (2008, 2010). All Rights Reserved.
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

package org.osgi.multiplexer;

/**
 * This is a simple API intended to allow multiple frameworks in the same JVM to
 * "share" JVM singletons like the URLStreamHandlerFactory and the
 * ContentHandlerFactory. Supporting this API makes heavy use of potentially
 * JVM-specific reflection to manage both the JVM singleton instance and any
 * cached state associated with previously set singletons.
 * <p>
 * Since each multiplexer could be associated with classes loaded by a different
 * classloader, the methods on this interface would have to be discovered and
 * invoked via reflection, rather than by casting or instanceof tests.
 * <p>
 * The presence and support of this interface (as determined by the
 * {@code supportsOSGiMultiplexing} method) implies support of the
 * following multiplexing algorithm for a given JVM singleton.
 * 
 * <h4>Initialization:</h4>
 * <ul>
 * <li>Create a new multiplexer for the given JVM singleton, e.g. an
 * implementation of a URLStreamHandlerFactory that also implements the
 * MultiplexSupport interface.</li>
 * <li>Retrieve the current JVM Singleton-- Does it support multiplexing?
 * <ul>
 * <li>If yes, get (and store) the legacy singleton from the JVM singleton
 * multiplexer in the new multiplexer, and append the new multiplexer to the
 * double-linked list.</li>
 * <li>If no, store the current singleton in the new multiplexer as the
 * "Legacy singleton", and use Java reflection to reset the JVM singleton
 * instance and clear any cached state before setting the new multiplexer as the
 * JVM singleton.</li>
 * </ul>
 * </li>
 * </ul>
 * <h4>Termination:</h4>
 * <ul>
 * <li>When a framework is cleaning up, it will need to "remove" its multiplexer
 * from the double-linked list, in a process that is the reverse of what happens
 * during initialization.
 * <ul>
 * <li>If the multiplexer being cleaned up is registered as the JVM singleton,
 * then it needs to set a new singleton: either the "next" multiplexer in the
 * chain if other multiplexers exist, or the legacy singleton, if it exists.</li>
 * <li>If the multiplexer being cleaned up is not the JVM singleton, it needs
 * only to update the previous and next links of its neighbors in order to
 * remove itself from the list.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <h4>Determining the context:</h4>
 * <p>
 * When a method is called on a multiplexing JVM singleton, the singleton (and
 * any subsequently called multiplexers) must decide whether or not they are the
 * correct instance for the method call. If the current multiplexer determines
 * that it cannot handle the current method call, it should delegate to the next
 * multiplexer in the chain. The last multiplexer in the chain should delegate
 * method calls to the legacy singleton if the calling context doesn't match.
 * 
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface MultiplexSupport {

	/**
	 * This method allows a framework to test a pre-registered JVM singleton to
	 * see if a) the singleton is aware of the OSGi multiplexing algorithm (the
	 * method is present), and if so, b) if it can support multiplexing in the
	 * current environment.
	 * <p>
	 * Singleton multiplexing is heavily dependent on potentially JVM-specific
	 * reflection, so it is possible that a framework could find itself in an
	 * environment in which it can't support multiplexing, in which case, the
	 * implementation of this method would return false.
	 * 
	 * @return true if multiplexing supported in the current JVM, false otherwise.
	 */
	boolean supportsOSGiMultiplexing();

	/**
	 * Obtain a reference to the pre-existing non-multiplexing JVM singleton, 
	 * if present.  As new multiplexers are added, getLegacySingleton() should be called
	 * on the current (multiplexing) singleton, and the returned value (if not null), 
	 * should be saved and used for delegation.
	 */
	Object getLegacySingleton();

	/**
	 * @return the next multiplexer in the chain, or null if not set.
	 */
	Object getNextMultiplexer();

	/**
	 * Set the next multiplexer in the chain.
	 * @param multiplexer The next multiplexer in the double-linked list.
	 */
	void setNextMultiplexer(Object multiplexer);

	/**
	 * @return the previous multiplexer in the chain, or null if not set. 
	 */
	Object getPrevMultiplexer();

	/**
	 * Set the previous multiplexer in the chain.
	 * @param multiplexer The previous multiplexer in the double-linked list.
	 */
	void setPrevMultiplexer(Object multiplexer);
}
