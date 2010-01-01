/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.test.cases.jpa.junit;

import org.osgi.framework.Bundle;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class JPATestCase extends DefaultTestBundleControl {

	public void testPersistenceClass() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("defaultPersistenceBundle.jar");
		
	}
	
	public void testPersistenceClassWithMap() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceBundle.jar");
		
	}
	
}
