/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.cdi;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cdi.annotations.FactoryComponent;
import org.osgi.service.cdi.annotations.PID;

/**
 * Defines CDI constants.
 *
 * @author $Id$
 */
@ProviderType
public class CDIConstants {
	private CDIConstants() {
		// non-instantiable
	}

	/**
	 * Capability name for CDI Integration.
	 * <p>
	 * Used in {@code Provide-Capability} and {@code Require-Capability} manifest
	 * headers with the {@code osgi.extender} namespace. For example:
	 *
	 * <pre>
	 * Require-Capability: osgi.extender; «
	 *  filter:="(&amp;(osgi.extender=osgi.cdi)(version&gt;=1.0)(!(version&gt;=2.0)))"
	 * </pre>
	 */
	public static final String	CDI_CAPABILITY_NAME					= "osgi.cdi";

	/**
	 * Special string representing the name of a Component.
	 *
	 * <p>
	 * This string can be used with {@link PID#value() PID} OR
	 * {@link FactoryComponent#value() factory PID} to specify the name of the
	 * component.
	 * <p>
	 * For example:
	 *
	 * <pre>
	 * &#64;PID(CDI_COMPONENT_NAME)
	 * </pre>
	 */
	public static final String	CDI_COMPONENT_NAME					= "$";

	/**
	 * The attribute of the CDI extender requirement declaring the container's id.
	 *
	 * <pre>
	 * Require-Capability: osgi.extender; «
	 *  filter:="(&amp;(osgi.extender=osgi.cdi)(version&gt;=1.0)(!(version&gt;=2.0)))"; «
	 *  container.id="my.container"
	 * </pre>
	 */
	public static final String	CDI_CONTAINER_ID					= "container.id";

	/**
	 * The key used for the container id service property in services provided by
	 * CCR.
	 */
	public static final String	CDI_CONTAINER_ID_PROPERTY			= "osgi.cdi." + CDI_CONTAINER_ID;

	/**
	 * A service property applied to
	 * {@code javax.enterprise.inject.spi.Extension} services, whose value is
	 * the name of the extension.
	 */
	public static final String	CDI_EXTENSION_PROPERTY				= "osgi.cdi.extension";

	/**
	 * Compile time constant for the Specification Version of CDI Integration.
	 * <p>
	 * Used in {@code Version} and {@code Requirement} annotations. The value of
	 * this compile time constant will change when the specification version of
	 * CDI Integration is updated.
	 */
	public static final String	CDI_SPECIFICATION_VERSION			= "1.0.0";

	/**
	 * The '{@code descriptor}' attribute on the CDI extender requirement.
	 * <p>
	 * The value of this attribute is a list of bean CDI bean descriptor file
	 * paths to be searched on the {@code Bundle-ClassPath}. For example:
	 *
	 * <pre>
	 * Require-Capability: osgi.extender; «
	 *  filter:="(&amp;(osgi.extender=osgi.cdi)(version&gt;=1.0)(!(version&gt;=2.0)))"; «
	 *  descriptor:List&lt;String&gt;="META-INF/beans.xml"
	 * </pre>
	 */
	public static final String	REQUIREMENT_DESCRIPTOR_ATTRIBUTE	= "descriptor";

	/**
	 * The '{@code beans}' attribute on the CDI extender requirement.
	 * <p>
	 * The value of this attribute is a list of bean class names that will be
	 * processed by CCR. The default value is an empty list. For example:
	 *
	 * <pre>
	 * Require-Capability: osgi.extender; «
	 *  filter:="(&amp;(osgi.extender=osgi.cdi)(version&gt;=1.0)(!(version&gt;=2.0)))"; «
	 *  beans:List&lt;String&gt;="com.acme.Foo,com.acme.bar.Baz"
	 * </pre>
	 */
	public static final String	REQUIREMENT_BEANS_ATTRIBUTE			= "beans";

}
