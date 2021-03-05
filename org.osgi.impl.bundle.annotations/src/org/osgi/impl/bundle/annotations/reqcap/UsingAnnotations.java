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

@DirectRequirementAnnotation
@IndirectRequirementAnnotation
@DefaultOptionsAnnotation
@AttributesAnnotation(//
		value = "value", //
		directive = "directiveValue", //
		longAttr = 42, //
		stringAttr = "stringValue", //
		doubleAttr = 4.2, //
		longList = {
				2, 3, 4
		}, //
		stringList = {
				"one", "two", "three"
		}, //
		doubleList = {
				2.3, 3.4, 4.5
		} //
)
@AttributesDefaultsAnnotation
@DirectCapabilityAnnotation
@IndirectCapabilityAnnotation
@OverrideAnnotation
public class UsingAnnotations {
	//
}
