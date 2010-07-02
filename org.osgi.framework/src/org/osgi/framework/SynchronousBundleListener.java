/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package org.osgi.framework;

/**
 * A synchronous {@code BundleEvent} listener.
 * {@code SynchronousBundleListener} is a listener interface that may be
 * implemented by a bundle developer. When a {@code BundleEvent} is
 * fired, it is synchronously delivered to a
 * {@code SynchronousBundleListener}. The Framework may deliver
 * {@code BundleEvent} objects to a
 * {@code SynchronousBundleListener} out of order and may concurrently
 * call and/or reenter a {@code SynchronousBundleListener}.
 * <p>
 * A {@code SynchronousBundleListener} object is registered with the
 * Framework using the {@link BundleContext#addBundleListener} method.
 * {@code SynchronousBundleListener} objects are called with a
 * {@code BundleEvent} object when a bundle has been installed, resolved,
 * starting, started, stopping, stopped, updated, unresolved, or uninstalled.
 * <p>
 * Unlike normal {@code BundleListener} objects,
 * {@code SynchronousBundleListener}s are synchronously called during
 * bundle lifecycle processing. The bundle lifecycle processing will not proceed
 * until all {@code SynchronousBundleListener}s have completed.
 * {@code SynchronousBundleListener} objects will be called prior to
 * {@code BundleListener} objects.
 * <p>
 * {@code AdminPermission[bundle,LISTENER]} is required to add or remove
 * a {@code SynchronousBundleListener} object.
 * 
 * @since 1.1
 * @see BundleEvent
 * @ThreadSafe
 * @version $Id$
 */

public interface SynchronousBundleListener extends BundleListener {
	// This is a marker interface
}
