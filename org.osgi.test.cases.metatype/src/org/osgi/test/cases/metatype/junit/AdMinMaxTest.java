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
 * AttributeDefinition.validate(String) must verify an attribute is greater 
 * than or equal to the specified minimum and less than or equal to the 
 * specified maximum.
 * 
 * AttributeDefinition.BOOLEAN is not applicable because neither minimum nor
 * maximum value has no meaning.
 * 
 * AttributeDefinition.BIGDECIMAL and AttributeDefinition.BIGINTEGER are not
 * applicable because they are deprecated.
 */
public class AdMinMaxTest extends MetaTypeTest {
	private Bundle bundle;
	private ServiceReference<MetaTypeService>		ref;
	
	private final Map<String,AttributeDefinition>	ads	= new HashMap<>();
	
	/**
	 * AttributeDefinition.STRING
	 */
	public void testString() {
		// min
		assertAttributeGood(ads.get("string"), "1234");
		assertAttributeGood(ads.get("string"), "12345");
		assertAttributeBad(ads.get("string"), "123");
		// max
		assertAttributeGood(ads.get("string"), "12345678");
		assertAttributeGood(ads.get("string"), "1234567");
		assertAttributeBad(ads.get("string"), "123456789");
	}
	
	/**
	 * AttributeDefinition.LONG
	 */
	public void testLong() {
		// min
		assertAttributeGood(ads.get("long"), "1000");
		assertAttributeGood(ads.get("long"), "1001");
		assertAttributeBad(ads.get("long"), "999");
		// max
		assertAttributeGood(ads.get("long"), "2000");
		assertAttributeGood(ads.get("long"), "1999");
		assertAttributeBad(ads.get("long"), "2001");
	}
	
	/**
	 * AttributeDefinition.DOUBLE
	 */
	public void testDouble() {
		// min
		assertAttributeGood(ads.get("double"), "3.141592653589");
		assertAttributeGood(ads.get("double"), "3.141592653590");
		assertAttributeBad(ads.get("double"), "3.141592653588");
		// max
		assertAttributeGood(ads.get("double"), "6.283185307178");
		assertAttributeGood(ads.get("double"), "6.283185307177");
		assertAttributeBad(ads.get("double"), "6.283185307179");
	}
	
	/**
	 * AttributeDefinition.FLOAT
	 */
	public void testFloat() {
		// min
		assertAttributeGood(ads.get("float"), "3.1415");
		assertAttributeGood(ads.get("float"), "3.1416");
		assertAttributeBad(ads.get("float"), "3.1414");
		// max
		assertAttributeGood(ads.get("float"), "6.283");
		assertAttributeGood(ads.get("float"), "6.282");
		assertAttributeBad(ads.get("float"), "6.2831");
	}
	
	/**
	 * AttributeDefinition.INTEGER
	 */
	public void testInteger() {
		// min
		assertAttributeGood(ads.get("integer"), "100");
		assertAttributeGood(ads.get("integer"), "101");
		assertAttributeBad(ads.get("integer"), "99");
		// max
		assertAttributeGood(ads.get("integer"), "200");
		assertAttributeGood(ads.get("integer"), "199");
		assertAttributeBad(ads.get("integer"), "201");
	}
	
	/**
	 * AttributeDefinition.BYTE
	 */
	public void testByte() {
		// min
		assertAttributeGood(ads.get("byte"), "1");
		assertAttributeGood(ads.get("byte"), "2");
		assertAttributeBad(ads.get("byte"), "0");
		// max
		assertAttributeGood(ads.get("byte"), "10");
		assertAttributeGood(ads.get("byte"), "9");
		assertAttributeBad(ads.get("byte"), "11");
	}
	
	/**
	 * AttributeDefinition.CHAR
	 */
	public void testChar() {
		// min
		assertAttributeGood(ads.get("char"), "P");
		assertAttributeGood(ads.get("char"), "Q");
		assertAttributeBad(ads.get("char"), "O");
		// max
		assertAttributeGood(ads.get("char"), "p");
		assertAttributeGood(ads.get("char"), "o");
		assertAttributeBad(ads.get("char"), "q");
	}
	
	/**
	 * AttributeDefinition.SHORT
	 */
	public void testShort() {
		// min
		assertAttributeGood(ads.get("short"), "10");
		assertAttributeGood(ads.get("short"), "11");
		assertAttributeBad(ads.get("short"), "9");
		// max
		assertAttributeGood(ads.get("short"), "20");
		assertAttributeGood(ads.get("short"), "19");
		assertAttributeBad(ads.get("short"), "21");
	}
	
	/**
	 * AttributeDefinition.PASSWORD
	 */
	public void testPassword() {
		// min
		assertAttributeGood(ads.get("password"), "12345678");
		assertAttributeGood(ads.get("password"), "123456789");
		assertAttributeBad(ads.get("password"), "1234567");
		// max
		assertAttributeGood(ads.get("password"), "1234567890AB");
		assertAttributeGood(ads.get("password"), "1234567890A");
		assertAttributeBad(ads.get("password"), "1234567890ABC");
	}
	
	protected void setUp() throws Exception {
		ref = getContext().getServiceReference(MetaTypeService.class);
		bundle = getTestBundle();
		bundle.start();
		MetaTypeService mts = getContext().getService(ref);
		MetaTypeInformation mti = mts.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("minMax", null);
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
