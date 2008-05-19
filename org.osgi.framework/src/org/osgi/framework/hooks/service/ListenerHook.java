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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;

/**
 * OSGi Framework Service Listener Hook Service.
 * 
 * <p>
 * Bundles registering this service will be called during service listener
 * addition and removal. Service hooks are not called for service operations on
 * other service hooks.
 * 
 * <p>
 * This hook cannot modify the service listener addition and removal operations.
 * It just receives notifications about service listener addition and removal
 * operations.
 * 
 * <p>
 * If the Java Runtime Environment supports permissions, the service listeners
 * provided to this hook will be filtered. This hook will only be presented with
 * service listeners added by bundles for which the bundle registering this hook
 * has <code>AdminPermission[listening bundle,SERVICE_HOOK]</code>.
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
	 * or removing service listeners, this method may be not be called prior to
	 * <code>add</code> or <code>remove</code>.
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
	 * @param listener A listeners which is no longer listening to service
	 * 	events.
	 */
	void removed(Listener listener);

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
		 * 
		 * @param context The context of the bundle which added the listener.
		 * @param listener The ServiceListener object.
		 */
		public Listener(BundleContext context, ServiceListener listener) {
			this.context = context;
			this.listener = listener;
		}

		/**
		 * Return the context of the bundle which added the listener.
		 * 
		 * @return The context of the bundle which added the listener.
		 */
		public BundleContext getContext() {
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
	}

	/**
	 * A Service Listener with a filter. This immutable class encapsulates a
	 * ServiceListener, the bundle which registered it and the filter with which
	 * it was registered.
	 * 
	 * @Immutable
	 */
	public static class FilteredListener extends Listener {
		private final String	filter;

		/**
		 * Create a FilteredListener.
		 * 
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
		 * 
		 * @return The filter with which the listener was added.
		 */
		public String getFilter() {
			return filter;
		}
	}

	/**
	 * A Service Listener for names services. This immutable class encapsulates
	 * a ServiceListener, the bundle which registered it and the names of the
	 * services upon which it listens.
	 * 
	 * @Immutable
	 */
	public static class NamedListener extends Listener {
		private final String[]	names;

		/**
		 * Create a NamedListener.
		 * 
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
		 * 
		 * @return The names of the services upon which listener listens.
		 */
		public String[] getNames() {
			return (String[]) names.clone();
		}
	}
}
