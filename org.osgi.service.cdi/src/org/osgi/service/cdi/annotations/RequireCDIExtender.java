/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.cdi.CDIConstants.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Requirement;

/**
 * This annotation can be used to require the CDI Component Runtime extender. It
 * can be used directly, or as a meta-annotation.
 *
 * @author $Id$
 */
@Documented
@Retention(CLASS)
@Target({
		TYPE, PACKAGE
})
@Requirement(
		namespace = EXTENDER_NAMESPACE,
		name = CDI_CAPABILITY_NAME,
		version = CDI_SPECIFICATION_VERSION)
public @interface RequireCDIExtender {

	/**
	 * Specify CDI bean descriptor file paths to be searched on the
	 * {@code Bundle-ClassPath}. For example:
	 * <p>
	 * <code>
	 * {@code @}RequireCDIExtender(beans = "META-INF/beans.xml")
	 * </code>
	 *
	 * @return CDI bean descriptor file paths
	 */
	@Attribute
	String[] beans() default {};

	/**
	 * Specify service filters matching the extensions required by the CDI
	 * bundle.
	 * <p>
	 * {@code javax.enterprise.inject.spi.Extension} services are expected to be
	 * published with the service property "{@code osgi.cdi.extension}" whose
	 * value is the name of the extension. In that case, an example could be:
	 * <p>
	 * <code>
	 * {@code @}RequireCDIExtender(extensions = "(osgi.cdi.extension=Foo)")
	 * </code>
	 *
	 * @return service filters matching the extensions required by the CDI
	 *         bundle
	 */
	@Attribute
	String[] extensions() default {};

	/**
	 * Specify OSGi Beans Description file paths to be searched on the
	 * {@code Bundle-ClassPath}. The default value when unspecified is
	 * {@code OSGI-INF/cdi/osgi-beans.xml}. For example:
	 * <p>
	 * <code>
	 * {@code @}RequireCDIExtender(osgi_beans = "META-INF/beans.xml")
	 * </code>
	 *
	 * @return OSGi Beans Description file paths
	 */
	@Attribute("osgi.beans")
	String[] osgi_beans() default {};

}
