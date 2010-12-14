/*
* Copyright (c) OSGi Alliance (2010). All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/ 
package org.osgi.test.cases.metatype.junit;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.test.support.OSGiTestCase;

public abstract class MetaTypeTest extends OSGiTestCase {
	public void assertAttributeBad(AttributeDefinition ad, String value) {
		assertTrue("Value '" + value + "' should not have been valid", ad.validate(value).length() > 0);
	}
	
	public void assertAttributeGood(AttributeDefinition ad, String value) {
		assertEquals("Value '" + value + "' should have been valid", 0, ad.validate(value).length());
	}
}
