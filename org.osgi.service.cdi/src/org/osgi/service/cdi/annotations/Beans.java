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

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that build tooling must be included the specified
 * classes in the {@code osgi.cdi} {@code beans} list.
 *
 * @author $ID$
 */
@Documented
@Retention(CLASS)
@Target(PACKAGE)
public @interface Beans {

	/**
	 * Specify the list of classes from the current package. Specifying no value
	 * (or an empty array) indicates to include all classes in the package.
	 */
	Class< ? >[] value() default {};
}
