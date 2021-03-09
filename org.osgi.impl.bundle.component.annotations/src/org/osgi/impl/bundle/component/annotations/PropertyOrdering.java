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
package org.osgi.impl.bundle.component.annotations;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 *
 *
 */
@Component(name = "testPropertyOrdering", properties = "OSGI-INF/vendor.properties", property = {
		"config4=config4",
		"string4=config/string4"})
public class PropertyOrdering {
	public @interface Config1 {
		String config1() default "config1";
		String string1() default "config/string1";
		String string2() default "config/string1";
		String string3() default "config/string1";
		String string4() default "config/string1";
		String[]stringarray1() default {"config/1stringarray1", "config/2stringarray1"};
		boolean boolean1() default true;
		boolean[]booleanarray1() default {true, false};
		char char1() default '@';
		char[]chararray1() default {'@', '+'};
		byte byte1() default 2;
		byte[]bytearray1() default {2, -3};
		short short1() default 1034;
		short[]shortarray1() default {1034, -1043};
		int int1() default 123456;
		int[]intarray1() default {123456, -234567};
		long long1() default 9876543210L;
		long[]longarray1() default {9876543210L, -987654321L};
		float float1() default 3.14F;
		float[]floatarray1() default {3.14F, -4.56F};
		double double1() default 2.1D;
		double[]doublearray1() default {2.1D, -1.2D};
		Class<?>class1() default TestEnum.class;
		Class<?>[]classarray1() default {TestEnum.class, Object.class};
		TestEnum enum1() default TestEnum.ITEM1;
		TestEnum[]enumarray1() default {TestEnum.ITEM1, TestEnum.ITEM2};
	}

	public @interface Config2 {
		String config2() default "config2";
		String string2() default "config/string2";
		String string3() default "config/string2";
		String string4() default "config/string2";
	}

	public @interface Config3 {
		String config3() default "config3";
		String string3() default "config/string3";
		String string4() default "config/string3";
	}

	/**
	 */
	@Activate
	private void activate1(Config1 c) {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Modified
	private void modified2(Config2 c) {
		System.out.println("Modified World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate3(Config3 c) {
		System.out.println("Goodbye World!");
	}

}
