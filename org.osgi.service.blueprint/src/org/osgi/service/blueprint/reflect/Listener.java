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
 * Metadata for a listener interested in service bind and unbind events for a service
 * reference.
 */
public interface Listener {

	/**
	 * The component instance that will receive bind and unbind
	 * events. The returned value must reference a TargetListenerComponent
	 * either directly or indirectly.  The return type will be either
     * a RefMetadata instance or an TargetListenerComponent instance.
	 *
	 * Defined in the <code>ref</code> attribute or inlined component.
	 *
	 * @return the listener component reference.
	 */
	Target getListenerComponent();

	/**
	 * The name of the method to invoke on the listener component when
	 * a matching service is bound to the reference
	 *
	 * @return the bind callback method name.
	 */
	String getBindMethodName();

	/**
	 * The name of the method to invoke on the listener component when
	 * a service is unbound from the reference.
	 *
	 * @return the unbind callback method name.
	 */
	String getUnbindMethodName();
}
