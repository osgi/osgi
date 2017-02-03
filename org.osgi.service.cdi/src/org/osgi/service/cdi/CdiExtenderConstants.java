/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.cdi;

/**
 * Defines CDI Extender constants.
 */
public class CdiExtenderConstants {
	private CdiExtenderConstants() {
		// non-instantiable
	}

	/**
	 * The name used for the CDI extender in the osgi.extender namespace.
	 */
	public static final String	CDI_EXTENDER						= "osgi.cdi";

	/**
	 * The key of the CdiContainer state service property.
	 */
	public static final String	CDI_EXTENDER_CONTAINER_STATE		= "osgi.cdi.container.state";

	/**
	 * The namespace for CDI extensions.
	 */
	public static final String	CDI_EXTENSION						= "osgi.cdi.extension";

	/**
	 * The 'beans' attribute on the CDI extender requirement.
	 *
	 * <p>
	 * The value of this attribute is a comma delimited list of bean CDI bean
	 * descriptor files to be searched on the <code>Bundle-ClassPath</code>.
	 */
	public static final String	REQUIREMENT_BEANS_ATTRIBUTE			= "beans";

	/**
	 * The 'osgi-beans' attribute on the CDI extender requirement.
	 *
	 * <p>
	 * The value of this attribute is the name of the OSGi Beans Description
	 * file. The default value when unspecified is
	 * <code>OSGI-INF/cdi/osgi-beans.xml</code>.
	 */
	public static final String	REQUIREMENT_OSGI_BEANS_ATTRIBUTE	= "osgi-beans";

}
