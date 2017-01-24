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

package org.osgi.service.cdi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to define a property that will be applied to the bean when
 * registered as an OSGi service.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ServiceProperty {

	/**
	 * The key or name of the property.
	 *
	 * @return the key
	 */
	String key();

	/**
	 * The value of the property.
	 *
	 * @return the value
	 */
	String value();

	/**
	 * The type of the property.
	 *
	 * @return the type
	 */
	PropertyType type() default PropertyType.String;
}
