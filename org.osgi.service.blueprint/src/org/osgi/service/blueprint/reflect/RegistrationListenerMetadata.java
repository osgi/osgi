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
package org.osgi.service.blueprint.reflect;

/**
 * Metadata for a listener interested in service registration and unregistration
 * events for an exported service.
 */
public interface RegistrationListenerMetadata {

	/**
	 * The component instance that will receive registration and unregistration 
	 * events. The returned value must reference a component and therefore be
	 * either a ComponentValue, ReferenceValue, or ReferenceNameValue.
	 * 
	 * @return the listener component reference.
	 */
	Value getListenerComponent();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * the exported service is registered with the service registry.
	 * 
	 * @return the registration callback method name.
	 */
	String getRegistrationMethodName();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * the exported service is unregistered from the service registry.
	 * 
	 * @return the unregistration callback method name.
	 */
	String getUnregistrationMethodName();
	
}
