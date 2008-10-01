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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;

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
	 * Initial listeners hook method. This method is called when the hook is
	 * first registered to provide the hook with the set of service listeners
	 * which were had been added prior to the hook being registered. This method
	 * is only called once. However, due to the timing of other bundles adding
	 * or removing service listeners, calls to {@link #added} or {@link #removed
	 * } may occur before the call to this method.
	 * 
	 * @param listeners An array of listeners which are listening to service
	 * 	events.
	 */

	void initial(Listener[] listeners);

	/**
	 * Add listener hook method. This method is called during service listener
	 * addition. This method will be called once for each service listener added
	 * after this hook had been registered.
	 * 
	 * @param listener A listener which is now listening to service events.
	 */
	void added(Listener listener);

	/**
	 * Remove listener hook method. This method is called during service
	 * listener removal. This method will be called once for each service
	 * listener removed after this hook had been registered.
	 * 
	 * @param listener A listener which is no longer listening to service
	 * 	events.
	 */
	void removed(Listener listener);

	/**
	 * A Service Listener wrapper. This immutable class encapsulates a {@link
	 * ServiceListener} and the bundle which added it and the filter with which
	 * it was added. Objects of this type are created by the framework and
	 * passed to the {@link ListenerHook}.
	 * 
	 * @Immutable
	 */
	public static class Listener {
		private final BundleContext		context;
		private final ServiceListener	listener;
		private final String			filter;

		/**
		 * Create a Service Listener wrapper.
		 * 
		 * @param context The context of the bundle which added the listener.
		 * @param listener The ServiceListener object.
		 * @param filter The filter with which the listener was added.
		 */
		public Listener(BundleContext context, ServiceListener listener,
				String filter) {
			this.context = context;
			this.listener = listener;
			this.filter = filter;
		}

		/**
		 * Return the context of the bundle which added the listener.
		 * 
		 * @return The context of the bundle which added the listener.
		 */
		public BundleContext getBundleContext() {
			return context;
		}

		/**
		 * Return the service listener.
		 * 
		 * @return The service listener.
		 */
		public ServiceListener getServiceListener() {
			return listener;
		}

		/**
		 * Return the filter with which the listener was added.
		 * 
		 * @return The filter with which the listener was added. This may be
		 * 	<code>null</code> if the listener was added without a filter.
		 */
		public String getFilter() {
			return filter;
		}
	}
}
