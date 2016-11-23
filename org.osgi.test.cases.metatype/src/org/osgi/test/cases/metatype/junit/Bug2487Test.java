/*
* Copyright (c) OSGi Alliance (2010, 2014). All Rights Reserved.
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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * Options specified by the <Option> element are exclusive.
 * 
 * Default values must be valid or otherwise ignored.
 */
public class Bug2487Test extends MetaTypeTest {
	private Bundle bundle;
	private MetaTypeInformation mti;
	private ServiceReference<MetaTypeService>	ref;
	
	public void testInvalidDefaultIgnored() {
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("car.1", null);
		AttributeDefinition ad = findAttributeDefinitionById("11", ocd);
		assertNull("Invalid default values must be ignored",
				ad.getDefaultValue());
	}

	public void testOptionsExclusive() {
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("car.1", null);
		AttributeDefinition ad = findAttributeDefinitionById("11", ocd);
		assertAttributeGood(ad, "white");
		assertAttributeGood(ad, "black");
		assertAttributeBad(ad, "purple");
	}
	
	protected void setUp() throws Exception {
		ref = getContext().getServiceReference(MetaTypeService.class);
		bundle = getTestBundle();
		bundle.start();
		MetaTypeService mts = getContext().getService(ref);
		mti = mts.getMetaTypeInformation(bundle);
	}

	protected Bundle getTestBundle() throws Exception {
		return install("bug2487.jar");
	}

	protected void tearDown() throws Exception {
		getContext().ungetService(ref);
		bundle.uninstall();
	}
}
