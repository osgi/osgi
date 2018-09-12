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

import org.osgi.impl.bundle.component.annotations.PropertyOrdering14.ConfigB;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 *
 *
 */
@Component(name = "testPropertyOrdering14", properties = "OSGI-INF/vendor.properties", property = {
		"configC=configC", "stringC=config/stringC"
})
@ConfigB
public class PropertyOrdering14 {

	public @interface Config1 {
		String config1() default "config1";

		String string1() default "config/string1";

		String string2() default "config/string1";

		String string3() default "config/string1";

		String string4() default "config/string1";

		String string5() default "config/string1";

		String string6() default "config/string1";

		String string7() default "config/string1";

		String string8() default "config/string1";

		String string9() default "config/string1";

		String stringA() default "config/string1";

		String stringB() default "config/string1";

		String stringC() default "config/string1";
	}

	public @interface Config2 {
		String config2() default "config2";

		String string2() default "config/string2";

		String string3() default "config/string2";

		String string4() default "config/string2";

		String string5() default "config/string2";

		String string6() default "config/string2";

		String string7() default "config/string2";

		String string8() default "config/string2";

		String string9() default "config/string2";

		String stringA() default "config/string2";

		String stringB() default "config/string2";

		String stringC() default "config/string2";
	}

	public @interface Config3 {
		String config3() default "config3";

		String string3() default "config/string3";

		String string4() default "config/string3";

		String string5() default "config/string3";

		String string6() default "config/string3";

		String string7() default "config/string3";

		String string8() default "config/string3";

		String string9() default "config/string3";

		String stringA() default "config/string3";

		String stringB() default "config/string3";

		String stringC() default "config/string3";
	}

	public @interface Config4 {
		String config4() default "config4";

		String string4() default "config/string4";

		String string5() default "config/string4";

		String string6() default "config/string4";

		String string7() default "config/string4";

		String string8() default "config/string4";

		String string9() default "config/string4";

		String stringA() default "config/string4";

		String stringB() default "config/string4";

		String stringC() default "config/string4";
	}

	public @interface Config5 {
		String config5() default "config5";

		String string5() default "config/string5";

		String string6() default "config/string5";

		String string7() default "config/string5";

		String string8() default "config/string5";

		String string9() default "config/string5";

		String stringA() default "config/string5";

		String stringB() default "config/string5";

		String stringC() default "config/string5";
	}

	public @interface Config6 {
		String config6() default "config6";

		String string6() default "config/string6";

		String string7() default "config/string6";

		String string8() default "config/string6";

		String string9() default "config/string6";

		String stringA() default "config/string6";

		String stringB() default "config/string6";

		String stringC() default "config/string6";
	}

	public @interface Config7 {
		String config7() default "config7";

		String string7() default "config/string7";

		String string8() default "config/string7";

		String string9() default "config/string7";

		String stringA() default "config/string7";

		String stringB() default "config/string7";

		String stringC() default "config/string7";
	}

	public @interface Config8 {
		String config8() default "config8";

		String string8() default "config/string8";

		String string9() default "config/string8";

		String stringA() default "config/string8";

		String stringB() default "config/string8";

		String stringC() default "config/string8";
	}

	public @interface Config9 {
		String config9() default "config9";

		String string9() default "config/string9";

		String stringA() default "config/string9";

		String stringB() default "config/string9";

		String stringC() default "config/string9";
	}

	public @interface ConfigA {
		String configA() default "configA";

		String stringA() default "config/stringA";

		String stringB() default "config/stringA";

		String stringC() default "config/stringA";
	}

	@ComponentPropertyType
	public @interface ConfigB {
		String configB() default "configB";

		String stringB() default "config/stringB";

		String stringC() default "config/stringB";
	}

	/**
	 */
	@Activate
	public PropertyOrdering14(Config1 c1, Config2 c2) {/**/}

	@Activate
	private Config3	c3;

	@Activate
	private Config4	c4;

	/**
	 */
	@Activate
	private void activate1(Config5 c5, Config6 c6) {/**/}

	/**
	 */
	@Modified
	private void modified2(Config7 c7, Config8 c8) {/**/}

	/**
	 */
	@Deactivate
	private void deactivate3(Config9 c9, ConfigA cA) {/**/}

}
