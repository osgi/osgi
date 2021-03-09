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
package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Directive;
import org.osgi.annotation.bundle.Requirement;

@Requirement(namespace = "testDirectlyAnnotatedAttributesDefaults")
@Capability(namespace = "testDirectlyAnnotatedAttributesDefaults")
public @interface AttributesDefaultsAnnotation {
	@Attribute("attr")
	String value() default "";

	@Directive("x-directive")
	String directive() default "";

	@Attribute
	long longAttr() default 0;

	@Attribute
	String stringAttr() default "";

	@Attribute
	double doubleAttr() default 0;

	@Attribute
	long[] longList() default {};

	@Attribute
	String[] stringList() default {};

	@Attribute
	double[] doubleList() default {};
}
