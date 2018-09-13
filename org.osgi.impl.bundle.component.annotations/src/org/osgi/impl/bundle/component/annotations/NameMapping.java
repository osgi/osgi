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
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

@interface NameMappingType {
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

@interface SingleElement {
	String PREFIX_ = "single.";

	String value() default "default.single.single.element";
}

@interface Marker {
	String PREFIX_ = "marker.";
}

/**
 *
 *
 */
@Component(name = "testNameMapping")
public class NameMapping {
	/**
	 */
	@Activate
	private void activate(NameMappingType arg) {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Modified
	private void modified(SingleElement arg) {
		System.out.println("Changed World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate(Marker arg) {
		System.out.println("Goodbye World!");
	}
}


