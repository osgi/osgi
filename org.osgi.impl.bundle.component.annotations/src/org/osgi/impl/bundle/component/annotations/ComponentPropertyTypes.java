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
package org.osgi.impl.bundle.component.annotations;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;

@ComponentPropertyType
@interface NameMappingComponentPropertyType {
	String PREFIX_ = "pre.";

	String myProperty143() default "default.myProperty143";

	String $new() default "default.new";

	String my$$prop() default "default.my$prop";

	String dot_prop() default "default.dot.prop";

	String another__prop() default "default.another_prop";

	String three___prop() default "default.three_.prop";

	String four_$__prop() default "default.four._prop";

	String five_$_prop() default "default.five..prop";

	String six$_$prop() default "default.six-prop";

	String seven$$_$prop() default "default.seven$.prop";
}

@interface IgnoredAnnotation {
	String ignored() default "bad";
}

/**
 *
 *
 */
@Component(name = "testComponentPropertyTypes")
@NameMappingComponentPropertyType(myProperty143 = "specified.myProperty143", $new = "specified.new", my$$prop = "specified.my$prop", dot_prop = "specified.dot.prop", another__prop = "specified.another_prop", three___prop = "specified.three_.prop", four_$__prop = "specified.four._prop", five_$_prop = "specified.five..prop", six$_$prop = "specified.six-prop", seven$$_$prop = "specified.seven$.prop")
@ServiceDescription("Test case for Component Property Type annotations")
@ServiceVendor("OSGi Alliance")
@ServiceRanking(42)
@IgnoredAnnotation
public class ComponentPropertyTypes {
	/**
	 */
	@Activate
	private void activate() {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}
}
