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

@ThirdAnnotation(foo = "foobar3")
public @interface OverrideAnnotation {
	//
}

@Capability(namespace = "testOverriding", name = "override", version = "1.5")
@Requirement(namespace = "testOverriding", name = "override", version = "1.5")
@interface FirstAnnotation {

	// Not an override as this is an attribute
	@Attribute
	String name();

	@Attribute
	String foo();

	@Directive("x-top-name")
	String fizz();
}

@FirstAnnotation(name = "First", foo = "bar1", fizz = "buzz1")
@interface SecondAnnotation {
	// Not an override as this is a directive
	@Directive("x-name")
	String name();

	// Override, even though the member name is different
	@Directive("x-top-name")
	String middle();
}

@SecondAnnotation(name = "Second", middle = "fizzbuzz2")
@interface ThirdAnnotation {
	// Overrides and changes type
	@Attribute
	String[] foo();
}
