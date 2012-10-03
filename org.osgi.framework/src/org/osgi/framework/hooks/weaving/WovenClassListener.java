/*
 * Copyright (c) OSGi Alliance (2000, 2011). All Rights Reserved.
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

package org.osgi.framework.hooks.weaving;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServicePermission;


/**
 * OSGi Framework Woven Class Listener Service.
 * <p>
 * Bundles registering this service will receive notifications whenever a
 * {@link WovenClass woven class} undergoes a state transition to an immutable
 * {@link WovenClass#getState() state}. Implementers will therefore be unable to
 * modify the woven class in contrast with {@link WeavingHook weaving hooks}.
 * <p>
 * Note that a woven class in the {@link WovenClass#TRANSFORMED transformed}
 * state allows listeners to observe the modified {@link WovenClass#getBytes()
 * byte codes} before the class has been {@link WovenClass#DEFINED defined} as
 * well as the additional {@link WovenClass#getDynamicImports() dynamic imports}
 * before the {@link WovenClass#getBundleWiring() bundle wiring} has been
 * updated.
 * <p>
 * Woven class listeners are synchronously {@link #modified(WovenClass) called}
 * during woven class lifecycle processing. The woven class lifecycle processing
 * will not proceed until all woven class listeners are done.
 * <p>
 * If the Java runtime environment supports permissions, the caller must have
 * {@link ServicePermission ServicePermission[WovenClassListener,REGISTER]} in
 * order to
 * {@link BundleContext#registerService(Class, Object, java.util.Dictionary)
 * register} a listener.
 * 
 * @ThreadSafe
 * @since 1.1
 * @version $Id$
 */

public interface WovenClassListener {
	/**
	 * Receives notification that a {@link WovenClass woven class} has undergone
	 * a lifecycle change.
	 * <p>
	 * If this method throws any exception, the Framework must log the exception
	 * but otherwise ignore it.
	 * 
	 * @param wovenClass The immutable woven class that underwent a lifecycle
	 *        change.
	 */
	public void modified(WovenClass wovenClass);
}
