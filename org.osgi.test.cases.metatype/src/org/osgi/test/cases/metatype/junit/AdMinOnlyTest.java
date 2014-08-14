/*
* Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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
 * than or equal to the specified minimum.
 * 
 * AttributeDefinition.BOOLEAN is not applicable because a minimum value has no
 * meaning.
 * 
 * AttributeDefinition.BIGDECIMAL and AttributeDefinition.BIGINTEGER are not
 * applicable because they are deprecated.
 */
public class AdMinOnlyTest extends MetaTypeTest {
	private Bundle bundle;
	private ServiceReference ref;
	
	private final Map ads = new HashMap();
	
	/**
	 * AttributeDefinition.STRING
	 */
	public void testString() {
		assertAttributeGood((AttributeDefinition)ads.get("string"), "1234");
		assertAttributeGood((AttributeDefinition)ads.get("string"), "12345");
		assertAttributeBad((AttributeDefinition)ads.get("string"), "123");
	}
	
	/**
	 * AttributeDefinition.LONG
	 */
	public void testLong() {
		assertAttributeGood((AttributeDefinition)ads.get("long"), "1000");
		assertAttributeGood((AttributeDefinition)ads.get("long"), "1001");
		assertAttributeBad((AttributeDefinition)ads.get("long"), "999");
	}
	
	/**
	 * AttributeDefinition.DOUBLE
	 */
	public void testDouble() {
		assertAttributeGood((AttributeDefinition)ads.get("double"), "3.141592653589");
		assertAttributeGood((AttributeDefinition)ads.get("double"), "3.141592653590");
		assertAttributeBad((AttributeDefinition)ads.get("double"), "3.141592653588");
	}
	
	/**
	 * AttributeDefinition.FLOAT
	 */
	public void testFloat() {
		assertAttributeGood((AttributeDefinition)ads.get("float"), "3.1415");
		assertAttributeGood((AttributeDefinition)ads.get("float"), "3.1416");
		assertAttributeBad((AttributeDefinition)ads.get("float"), "3.1414");
	}
	
	/**
	 * AttributeDefinition.INTEGER
	 */
	public void testInteger() {
		assertAttributeGood((AttributeDefinition)ads.get("integer"), "100");
		assertAttributeGood((AttributeDefinition)ads.get("integer"), "101");
		assertAttributeBad((AttributeDefinition)ads.get("integer"), "99");
	}
	
	/**
	 * AttributeDefinition.BYTE
	 */
	public void testByte() {
		assertAttributeGood((AttributeDefinition)ads.get("byte"), "1");
		assertAttributeGood((AttributeDefinition)ads.get("byte"), "2");
		assertAttributeBad((AttributeDefinition)ads.get("byte"), "0");
	}
	
	/**
	 * AttributeDefinition.CHAR
	 */
	public void testChar() {
		assertAttributeGood((AttributeDefinition)ads.get("char"), "P");
		assertAttributeGood((AttributeDefinition)ads.get("char"), "Q");
		assertAttributeBad((AttributeDefinition)ads.get("char"), "O");
	}
	
	/**
	 * AttributeDefinition.SHORT
	 */
	public void testShort() {
		assertAttributeGood((AttributeDefinition)ads.get("short"), "10");
		assertAttributeGood((AttributeDefinition)ads.get("short"), "11");
		assertAttributeBad((AttributeDefinition)ads.get("short"), "9");
	}
	
	/**
	 * AttributeDefinition.PASSWORD
	 */
	public void testPassword() {
		assertAttributeGood((AttributeDefinition)ads.get("password"), "12345678");
		assertAttributeGood((AttributeDefinition)ads.get("password"), "123456789");
		assertAttributeBad((AttributeDefinition)ads.get("password"), "1234567");
	}
	
	protected void setUp() throws Exception {
		ref = getContext().getServiceReference(MetaTypeService.class.getName());
		bundle = getTestBundle();
		bundle.start();
		MetaTypeService mts = (MetaTypeService)getContext().getService(ref);
		MetaTypeInformation mti = mts.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("minOnly", null);
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
