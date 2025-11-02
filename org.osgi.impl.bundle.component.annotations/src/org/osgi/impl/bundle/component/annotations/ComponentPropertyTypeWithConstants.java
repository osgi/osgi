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
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Test case for component property type with constants.
 * This tests the scenario described in https://github.com/osgi/osgi/issues/648
 * where constants are used as annotation attribute values.
 */
public class ComponentPropertyTypeWithConstants {

	/**
	 * Constants class to hold constant values used in annotations.
	 */
	public static class Constants {
		public static final String	DEFAULT_STRING_VALUE	= "default.constant.value";
		public static final String	SPECIFIED_STRING_VALUE	= "specified.constant.value";
		public static final int		DEFAULT_INT_VALUE		= 100;
		public static final int		SPECIFIED_INT_VALUE		= 200;
		public static final String	PREFIX_VALUE			= "constant.prefix.";
	}

	/**
	 * Component property type annotation that uses constants in its default
	 * values.
	 */
	@ComponentPropertyType
	@interface PropertyTypeWithConstants {
		String PREFIX_ = Constants.PREFIX_VALUE;

		String stringProperty() default Constants.DEFAULT_STRING_VALUE;

		int intProperty() default Constants.DEFAULT_INT_VALUE;

		String anotherProperty() default "literal.default.value";
	}

	/**
	 * Component using the property type with constant values specified.
	 */
	@Component(name = "testComponentPropertyTypeWithConstants")
	@PropertyTypeWithConstants(stringProperty = Constants.SPECIFIED_STRING_VALUE, intProperty = Constants.SPECIFIED_INT_VALUE, anotherProperty = "literal.specified.value")
	public static class TestComponent {
		@Activate
		private void activate() {
			System.out.println("Component with property type constants activated!");
		}

		@Deactivate
		private void deactivate() {
			System.out.println("Component with property type constants deactivated!");
		}
	}
}
