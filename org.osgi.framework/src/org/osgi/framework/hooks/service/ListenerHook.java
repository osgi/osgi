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

package org.osgi.framework.hooks.service;

import java.util.Collection;

import org.osgi.framework.BundleContext;

/**
 * OSGi Framework Service Listener Hook Service.
 * 
 * <p>
 * Bundles registering this service will be called during service listener
 * addition and removal.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public interface ListenerHook {
	/**
	 * Added listeners hook method. This method is called to provide the hook
	 * implementation with information on newly added service listeners. This
	 * method will be called as service listeners are added while this hook is
	 * registered. Also, immediately after registration of this hook, this
	 * method will be called to provide the current collection of service
	 * listeners which had been added prior to the hook being registered.
	 * 
	 * @param listeners A collection of {@link ListenerInfo}s for newly added
	 *        service listeners which are now listening to service events.
	 *        Attempting to add to or remove from the collection will result in
	 *        an <code>UnsupportedOperationException</code>. The collection is
	 *        not synchronized.
	 */
	void added(Collection/* <? extends ListenerInfo> */listeners);

	/**
	 * Removed listeners hook method. This method is called to provide the hook
	 * implementation with information on newly removed service listeners. This
	 * method will be called as service listeners are removed while this hook is
	 * registered.
	 * 
	 * @param listeners A collection of {@link ListenerInfo}s for newly removed
	 *        service listeners which are no longer listening to service events.
	 *        Attempting to add to or remove from the collection will result in
	 *        an <code>UnsupportedOperationException</code>. The collection is
	 *        not synchronized.
	 */
	void removed(Collection/* <? extends ListenerInfo> */listeners);

	/**
	 * Information about a Service Listener. This interface describes the bundle
	 * which added the Service Listener and the filter with which it was added.
	 * 
	 * @ThreadSafe
	 */
	public interface ListenerInfo {
		/**
		 * Return the context of the bundle which added the listener.
		 * 
		 * @return The context of the bundle which added the listener.
		 */
		BundleContext getBundleContext();

		/**
		 * Return the filter string with which the listener was added.
		 * 
		 * @return The filter string with which the listener was added. This may
		 *         be <code>null</code> if the listener was added without a
		 *         filter.
		 */
		String getFilter();
		
		/**
		 * Equality is based on the bundle and the filter. This hash code must 
		 * return the same hashcode for two info objects that have the same
		 * bundle and filter string (literally).
		 */
		int hashCode();
		
		/**
		 * Equality is based on the bundle and the filter.
		 */
		boolean equals(Object other);
	}
}
