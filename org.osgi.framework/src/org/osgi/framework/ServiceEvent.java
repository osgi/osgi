/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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

import org.osgi.framework.bundle.AllServiceListener;
import org.osgi.framework.bundle.ServiceListener;

/**
 * An event from the Framework describing a service lifecycle change.
 * <p>
 * <code>ServiceEvent</code> objects are delivered to
 * <code>ServiceListener</code>s and <code>AllServiceListener</code>s when a
 * change occurs in this service's lifecycle. A type code is used to identify
 * the event type for future extendability.
 * 
 * <p>
 * OSGi Alliance reserves the right to extend the set of types.
 * 
 * @Immutable
 * @see ServiceListener
 * @see AllServiceListener
 * @version $Revision$
 */

public class ServiceEvent {
	static final long					serialVersionUID	= 8792901483909409299L;
	/**
	 * Reference to the service that had a change occur in its lifecycle.
	 */
	private final ServiceReference< ? >	reference;

	/**
	 * Type of service lifecycle change.
	 */
	private final Type					type;

	/**
	 * Creates a new service event object.
	 * 
	 * @param type The event type.
	 * @param reference A <code>ServiceReference</code> object to the service
	 *        that had a lifecycle change.
	 */
	public ServiceEvent(Type type, ServiceReference< ? > reference) {
		this.reference = reference;
		this.type = type;
	}

	/**
	 * Returns a reference to the service that had a change occur in its
	 * lifecycle.
	 * <p>
	 * This reference is the source of the event.
	 * 
	 * @return Reference to the service that had a lifecycle change.
	 */
	public ServiceReference< ? > getServiceReference() {
		return reference;
	}

	/**
	 * Returns the type of event. The event type values are:
	 * <ul>
	 * <li>{@link #REGISTERED}</li>
	 * <li>{@link #MODIFIED}</li>
	 * <li>{@link #MODIFIED_ENDMATCH}</li>
	 * <li>{@link #UNREGISTERING}</li>
	 * </ul>
	 * 
	 * @return Type of service lifecycle change.
	 */

	public Type getType() {
		return type;
	}

	public static enum Type {
		/**
		 * This service has been registered.
		 * <p>
		 * This event is synchronously delivered <strong>after</strong> the
		 * service has been registered with the Framework.
		 * 
		 * <p>
		 * The value of <code>REGISTERED</code> is 0x00000001.
		 * 
		 * @see BundleContext#registerService(String[],Object,java.util.Map)
		 */
		REGISTERED(0x00000001),
		/**
		 * The properties of a registered service have been modified.
		 * <p>
		 * This event is synchronously delivered <strong>after</strong> the
		 * service properties have been modified.
		 * 
		 * <p>
		 * The value of <code>MODIFIED</code> is 0x00000002.
		 * 
		 * @see ServiceRegistration#setProperties
		 */
		MODIFIED(0x00000002),
		/**
		 * This service is in the process of being unregistered.
		 * <p>
		 * This event is synchronously delivered <strong>before</strong> the
		 * service has completed unregistering.
		 * 
		 * <p>
		 * If a bundle is using a service that is <code>UNREGISTERING</code>,
		 * the bundle should release its use of the service when it receives
		 * this event. If the bundle does not release its use of the service
		 * when it receives this event, the Framework will automatically release
		 * the bundle's use of the service while completing the service
		 * unregistration operation.
		 * 
		 * <p>
		 * The value of UNREGISTERING is 0x00000004.
		 * 
		 * @see ServiceRegistration#unregister
		 * @see BundleContext#ungetService
		 */
		UNREGISTERING(0x00000004),
		/**
		 * The properties of a registered service have been modified and the new
		 * properties no longer match the listener's filter.
		 * <p>
		 * This event is synchronously delivered <strong>after</strong> the
		 * service properties have been modified. This event is only delivered
		 * to listeners which were added with a non-<code>null</code> filter
		 * where the filter matched the service properties prior to the
		 * modification but the filter does not match the modified service
		 * properties.
		 * 
		 * <p>
		 * The value of <code>MODIFIED_ENDMATCH</code> is 0x00000008.
		 * 
		 * @see ServiceRegistration#setProperties
		 * @since 1.5
		 */
		MODIFIED_ENDMATCH(0x00000008);
		private final int	value;

		Type(int value) {
			this.value = value;
		}

		/**
		 * @return value
		 */
		public int getValue() {
			return value;
		}

	}

}
