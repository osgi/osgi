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

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.service.cdi.CDIConstants;

/**
 * This annotation can be used to require the CDI Portable Extension. It can be
 * used directly, or as a meta-annotation.
 *
 * @author $Id$
 */
@Documented
@Repeatable(RequireCDIExtensions.class)
@Retention(CLASS)
@Target({
		TYPE, PACKAGE
})
@Requirement(
		namespace = CDIConstants.CDI_EXTENSION_PROPERTY)
public @interface RequireCDIExtension {

	/**
	 * Specify a required CDI portable extension. For example:
	 * <p>
	 * <code>
	 * {@code @}RequireCDIExtension(name = "JavaJTA")
	 * </code>
	 *
	 * @return CDI portable extension name
	 */
	@Attribute(CDIConstants.CDI_EXTENSION_PROPERTY)
	String name();

	/**
	 * Specify the version of the required CDI portable extension. For example:
	 * <p>
	 * <code>
	 * {@code @}RequireCDIExtension(name = "JavaJTA", version = "1.2")
	 * </code>
	 *
	 * @return version of the required CDI portable extension
	 */
	@Attribute
	String version() default "";

}
