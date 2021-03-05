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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * AttributeDefinition.validate(String) must verify an attribute is less than 
 * or equal to the specified maximum.
 * 
 * AttributeDefinition.BOOLEAN is not applicable because a maximum value has no
 * meaning.
 * 
 * AttributeDefinition.BIGDECIMAL and AttributeDefinition.BIGINTEGER are not
 * applicable because they are deprecated.
 */
public class AdMaxOnlyTest extends MetaTypeTest {
	private Bundle bundle;
	private ServiceReference<MetaTypeService>	ref;
	
	private final Map<String,AttributeDefinition>	ads	= new HashMap<>();
	
	/**
	 * AttributeDefinition.STRING
	 */
	public void testString() {
		assertAttributeGood(ads.get("string"), "1234");
		assertAttributeBad(ads.get("string"), "12345");
		assertAttributeGood(ads.get("string"), "123");
	}
	
	/**
	 * AttributeDefinition.LONG
	 */
	public void testLong() {
		assertAttributeGood(ads.get("long"), "1000");
		assertAttributeBad(ads.get("long"), "1001");
		assertAttributeGood(ads.get("long"), "999");
	}
	
	/**
	 * AttributeDefinition.DOUBLE
	 */
	public void testDouble() {
		assertAttributeGood(ads.get("double"), "3.141592653589");
		assertAttributeBad(ads.get("double"), "3.141592653590");
		assertAttributeGood(ads.get("double"), "3.141592653588");
	}
	
	/**
	 * AttributeDefinition.FLOAT
	 */
	public void testFloat() {
		assertAttributeGood(ads.get("float"), "3.1415");
		assertAttributeBad(ads.get("float"), "3.1416");
		assertAttributeGood(ads.get("float"), "3.1414");
	}
	
	/**
	 * AttributeDefinition.INTEGER
	 */
	public void testInteger() {
		assertAttributeGood(ads.get("integer"), "100");
		assertAttributeBad(ads.get("integer"), "101");
		assertAttributeGood(ads.get("integer"), "99");
	}
	
	/**
	 * AttributeDefinition.BYTE
	 */
	public void testByte() {
		assertAttributeGood(ads.get("byte"), "1");
		assertAttributeBad(ads.get("byte"), "2");
		assertAttributeGood(ads.get("byte"), "0");
	}
	
	/**
	 * AttributeDefinition.CHAR
	 */
	public void testChar() {
		assertAttributeGood(ads.get("char"), "P");
		assertAttributeBad(ads.get("char"), "Q");
		assertAttributeGood(ads.get("char"), "O");
	}
	
	/**
	 * AttributeDefinition.SHORT
	 */
	public void testShort() {
		assertAttributeGood(ads.get("short"), "10");
		assertAttributeBad(ads.get("short"), "11");
		assertAttributeGood(ads.get("short"), "9");
	}
	
	/**
	 * AttributeDefinition.PASSWORD
	 */
	public void testPassword() {
		assertAttributeGood(ads.get("password"), "12345678");
		assertAttributeBad(ads.get("password"), "123456789");
		assertAttributeGood(ads.get("password"), "1234567");
	}
	
	protected void setUp() throws Exception {
		ref = getContext().getServiceReference(MetaTypeService.class);
		bundle = getTestBundle();
		bundle.start();
		MetaTypeService mts = getContext().getService(ref);
		MetaTypeInformation mti = mts.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("maxOnly", null);
		AttributeDefinition[] lads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		for (int i = 0; i < lads.length; i++) {
			this.ads.put(lads[i].getID(), lads[i]);
		}
	}

	protected Bundle getTestBundle() throws Exception {
		return install("tb3.jar");
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(ref);
		bundle.stop();
		bundle.uninstall();
	}
}
