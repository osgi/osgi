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

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.cdi.CDIConstants.CDI_CAPABILITY_NAME;
import static org.osgi.service.cdi.CDIConstants.CDI_SPECIFICATION_VERSION;

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
	 * 
	 * <pre>
	 * &#64;RequireCDIExtender(descriptor = "META-INF/beans.xml")
	 * </pre>
	 *
	 * @return CDI bean descriptor file paths.
	 */
	@Attribute
	String[] descriptor() default "META-INF/beans.xml";

	/**
	 * Specify OSGi Beans classes to be used by the CDI container. For example:
	 * <p>
	 * 
	 * <pre>
	 * &#64;RequireCDIExtender(beans = {com.foo.BarImpl.class, com.foo.impl.BazImpl.class})
	 * </pre>
	 *
	 * @return OSGi Beans classes to be used by the CDI container.
	 */
	@Attribute
	Class< ? >[] beans() default {};

}
