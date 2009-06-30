/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.reflect;

/**
 * Metadata for a registration listener interested in service registration and
 * unregistration events.
 */
public interface RegistrationListener {

	/**
	 * The component instance that will receive registration and unregistration
	 * events. The returned value must reference a {@link Target} either
	 * directly or indirectly. The return type will be a {@link Target}
	 * instance.
	 * 
	 * Defined in the <code>registration-listener</code> child element.
	 * 
	 * @return The registration listener target.
	 */
	Target getListenerComponent();

	/**
	 * The name of the method to invoke on the registration listener when the
	 * associated service is registered with the service registry. This method
	 * is also be called with the initial state when the listener is actuated.
	 * 
	 * Defined in the <code>registration-method</code> attribute.
	 * 
	 * @return The registration callback method name.
	 */
	String getRegistrationMethod();

	/**
	 * The name of the method to invoke on the listener component when the
	 * exported service is unregistered from the service registry. This method
	 * can also be called with the initial state when the listener is actuated.
	 * 
	 * Defined in the <code>unregistration-method</code> attribute.
	 * 
	 * @return the unregistration callback method name.
	 */
	String getUnregistrationMethod();

}
