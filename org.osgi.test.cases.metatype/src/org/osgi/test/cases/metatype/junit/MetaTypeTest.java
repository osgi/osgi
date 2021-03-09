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
package org.osgi.test.cases.metatype.junit;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.test.support.OSGiTestCase;

public abstract class MetaTypeTest extends OSGiTestCase {
	protected void assertAttributeBad(AttributeDefinition ad, String value) {
		assertTrue("Value '" + value + "' should not have been valid", ad.validate(value).length() > 0);
	}
	
	protected void assertAttributeGood(AttributeDefinition ad, String value) {
		assertEquals("Value '" + value + "' should have been valid", 0, ad.validate(value).length());
	}

	protected AttributeDefinition findAttributeDefinitionById(String id,
			ObjectClassDefinition ocd) {
		AttributeDefinition[] ads = ocd
				.getAttributeDefinitions(ObjectClassDefinition.ALL);
		if (ads == null)
			return null;
		for (int i = 0; i < ads.length; i++)
			if (ads[i].getID().equals(id))
				return ads[i];
		return null;
	}
}
