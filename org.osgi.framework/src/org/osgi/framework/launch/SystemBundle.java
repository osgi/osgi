/*
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
package org.osgi.framework.launch;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The System Bundle for a Framework instance.
 * 
 * The <i>main</i> class of a framework implementation must implement this
 * interface. The instantiator of the framework implementation class then has a
 * System Bundle object and can then use the methods of this interface to manage
 * and control the created framework instance.
 * 
 * <p>
 * The <i>main</i> class of a framework implementation must provide a public
 * constructor that takes a single argument of type <code>Map</code>. This
 * configuration argument provides this System Bundle with framework properties
 * to configure the framework instance. The framework instance must also examine
 * the system properties for framework properties which are not set in the
 * configuration argument. If framework properties are not provided by the
 * configuration argument or the system properties, this System Bundle must use
 * some reasonable default configuration appropriate for the current VM. For
 * example, the system packages for the current execution environment should be
 * properly exported. The configuration argument may be <code>null</code>.
 * 
 * <p>
 * A newly constructed System Bundle must be in the {@link Bundle#INSTALLED
 * INSTALLED} state.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface SystemBundle extends Bundle {
	/**
	 * Initialize this System Bundle. After calling this method, this System
	 * Bundle must be in the {@link Bundle#STARTING STARTING} state and must
	 * have a valid Bundle Context.
	 * 
	 * <p>
	 * During intialization, this System Bundle must also register
	 * {@link org.osgi.service.concurrent.ThreadFactory ThreadFactory} and
	 * {@link org.osgi.service.concurrent.AsyncExecutor AsyncExecutor} services.
	 * Implementations of these services may be passed to this System Bundle in
	 * the configuration argument using the service names as keys. If
	 * implementations of these services are not passed in the configuration
	 * argument, then this System Bundle must create and use a default
	 * implementation of each service.
	 * 
	 * <p>
	 * This System Bundle will not actually be started until <code>start</code>
	 * is called. If this System Bundle is not initialized called prior to
	 * calling <code>start</code>, then the <code>start</code> method must
	 * initialize this System Bundle prior to starting.
	 * 
	 * <p>
	 * This method does nothing if called when this System Bundle is in the
	 * {@link Bundle#STARTING STARTING}, {@link Bundle#ACTIVE ACTIVE} or
	 * {@link Bundle#STOPPING STOPPING} states.
	 * 
	 * @throws BundleException If this System Bundle could not be initialized.
	 */
	void init() throws BundleException;

	/**
	 * Wait until this System Bundle has completely stopped. The
	 * <code>stop</code> and <code>update</code> methods on a System Bundle
	 * performs an asynchronous stop of the System Bundle. This method can be
	 * used to wait until the asynchronous stop of this System Bundle has
	 * completed. This method will only wait if called when this System Bundle
	 * is in the {@link Bundle#STARTING STARTING}, {@link Bundle#ACTIVE ACTIVE},
	 * or {@link Bundle#STOPPING STOPPING} states. Otherwise it will return
	 * immediately.
	 * 
	 * @param timeout Maximum number of milliseconds to wait until this System
	 *        Bundle has completely stopped. A value of zero will wait
	 *        indefinitely.
	 * @throws InterruptedException If another thread interrupted the current
	 *         thread before or while the current thread was waiting for this
	 *         System Bundle to completely stop. The <i>interrupted status</i>
	 *         of the current thread is cleared when this exception is thrown.
	 * @throws IllegalArgumentException If the value of timeout is negative.
	 */
	void waitForStop(long timeout) throws InterruptedException;
}
