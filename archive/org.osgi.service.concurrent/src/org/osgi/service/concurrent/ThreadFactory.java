/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.concurrent;

/**
 * A Thread Factory service which creates new threads on demand.
 * 
 * <p>
 * A Thread Factory implementation can be passed to the framework instance when
 * it is created. If none is provided, then the framework instance must create a
 * default Thread Factory. The default Thread Factory must create all threads in
 * the same <code>ThreadGroup</code>.
 * 
 * <p>
 * The framework instance must use the provided Thread Factory, or the default
 * Thread Factory if none is provided, to create new threads and must also
 * register it as a service for bundles to use to create new threads.
 * 
 * <p>
 * It is recommended that all bundles use this service to create new threads
 * instead of directly calling <code> new Thread()</code>.
 * 
 * @version $Revision$
 * @TheadSafe
 */
public interface ThreadFactory {
	/**
	 * Creates a new Thread with the specified Runnable. Thread properties, such
	 * as daemon status, name and priority, must be set, if desired, before
	 * starting the newly created thread.
	 * 
	 * @param runnable A Runnable to be executed by the new thread.
	 * @return The newly created thread.
	 */
	Thread newThread(Runnable runnable);
}
