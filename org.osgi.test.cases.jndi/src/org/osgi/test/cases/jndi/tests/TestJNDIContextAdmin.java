/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.jndi.tests;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.provider.CTReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/** 
 * 
 * A set of methods to test the functionality of the JNDIContextAdmin interface
 * 
 * @version $Revision$ $Date$
 * 
 */

public class TestJNDIContextAdmin extends DefaultTestBundleControl {

	public void testGetObjectInstanceWithFactoryName() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Create a reference object we can use for testing.
		CTReference ref = new CTReference(String.class.getName(), CTObjectFactory.class.getName());
		
	}
}
