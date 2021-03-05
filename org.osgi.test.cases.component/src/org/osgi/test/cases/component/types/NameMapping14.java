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


package org.osgi.test.cases.component.types;

public @interface NameMapping14 {
	String PREFIX_ = "pre.";

	String myProperty143();

	String $new();

	String my$$prop();

	String dot_prop();

	String another__prop();

	String three___prop();

	String four_$__prop();

	String five_$_prop();

	String six$_$prop();

	String seven$$_$prop();
}
