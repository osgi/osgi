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


package org.osgi.impl.bundle.metatype.annotations;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(id = "testNameMapping")
public @interface NameMapping {
	String myProperty143();

	String $new();

	String my$$prop();

	String dot_prop();

	String _secret();

	String another__prop();

	String three___prop();

	String four_$__prop();

	String five_$_prop();
}
