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

package org.osgi.framework.hooks.service;

import java.util.Arrays;
import java.util.ListResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * OSGi Framework Service Listener Hook Service.
 * 
 * Bundles registering this service will be called during service listener
 * addition and removal. Service hooks are not called for service operations on
 * other service hooks.
 * 
 * This hook cannot modify the service listener addition and removal operations.
 * It just receives notifications about service listener addition and removal
 * operations.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ListenerHook {
	/**
	 * Add listener hook method. This method is called during service listener
	 * addition. It is also called once after this listener hook service has
	 * been registered to provide the current list of ServiceListeners.
	 * 
	 * @param listeners A list of listeners which are listening to service
	 *        events.
	 */
	void add(Listener[] listeners);

	/**
	 * Remove listener hook method. This method is called during service
	 * listener removal.
	 * 
	 * @param listeners A list of listeners which are no longer listening to
	 *        service events.
	 */
	void remove(Listener[] listeners);

	/**
	 * A Service Listener. This immutable class encapsulates a ServiceListener
	 * and the bundle which registered it. 
	 * 
	 * @Immutable
	 */
	public static class Listener {
		private final BundleContext		context;
		private final ServiceListener	listener;

		/**
		 * Create a Listener. 
		 * @param context The context of the bundle which added the listener.
		 * @param listener The ServiceListener object.
		 */
		public Listener(BundleContext context, ServiceListener listener) {
			this.context = context;
			this.listener = listener;
		}

		/**
		 * Return the context of the bundle which added the listener.
		 * @return The context of the bundle which added the listener.
		 */
		public BundleContext getContext() {
			return context;
		}

		/**
		 * Return the service listener.
		 * @return The service listener.
		 */
		public ServiceListener getServiceListener() {
			return listener;
		}
	}

	/**
	 * A Service Listener with a filter. This immutable class encapsulates a ServiceListener,
	 * the bundle which registered it and the filter with which it was registered. 
	 * 
	 * @Immutable
	 */
	public static class FilteredListener extends Listener {
		private final String	filter;

		/**
		 * Create a FilteredListener. 
		 * @param context The context of the bundle which added the listener.
		 * @param listener The ServiceListener object.
		 * @param filter The filter with which the listener was added.
		 */
		public FilteredListener(BundleContext context,
				ServiceListener listener, String filter) {
			super(context, listener);
			this.filter = filter;
		}

		/**
		 * Return the filter with which the listener was added.
		 * @return The filter with which the listener was added.
		 */
		public String getFilter() {
			return filter;
		}
	}

	/**
	 * A Service Listener for names services. This immutable class encapsulates a ServiceListener,
	 * the bundle which registered it and the names of the services upon which it listens. 
	 * 
	 * @Immutable
	 */
	public static class NamedListener extends Listener {
		private final String[]	names;

		/**
		 * Create a NamedListener. 
		 * @param context The context of the bundle which added the listener.
		 * @param listener The ServiceListener object.
		 * @param names The names of the services upon which listener listens.
		 */
		public NamedListener(BundleContext context, ServiceListener listener,
				String[] names) {
			super(context, listener);
			this.names = (String[]) names.clone();
		}

		/**
		 * Return the names of the services upon which listener listens.
		 * @return The names of the services upon which listener listens.
		 */
		public String[] getNames() {
			return (String[]) names.clone();
		}
	}
}
