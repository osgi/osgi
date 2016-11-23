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

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;

/**
 * The 'pid' attribute of the <Designate> element within the metadata XML
 * schema is now optional like the 'factoryPid' attribute. At least one of
 * these attributes must be present, however. If both are present, an
 * implementation should ignore the 'pid' attribute.
 */
public class Bug1852Test extends MetaTypeTest {
	private final String[] FACTORY_PIDS = new String[] {
			"com.acme.factory.1",
			"com.acme.factory.2",
			"com.acme.factory.3",
			"com.acme.factory.4"
	};
	private final String[] PIDS = new String[] {
			"com.acme.singleton.1"
	};
	
	private Bundle bundle;
	private MetaTypeInformation mti;
	private ServiceReference<MetaTypeService>	ref;
	
	public void test() {
		String[] factoryPids = mti.getFactoryPids();
		assertEquals("Incorrect factory pids", FACTORY_PIDS.length, factoryPids.length);
		assertTrue("Incorrect factory pids", Arrays.asList(factoryPids).containsAll(Arrays.asList(FACTORY_PIDS)));
		String[] pids = mti.getPids();
		assertEquals("Incorrect pids", PIDS.length, pids.length);
		assertTrue("Incorrect pids", Arrays.asList(pids).containsAll(Arrays.asList(PIDS)));
	}
	
	protected void setUp() throws Exception {
		ref = getContext().getServiceReference(MetaTypeService.class);
		bundle = getTestBundle();
		bundle.start();
		MetaTypeService mts = getContext().getService(ref);
		mti = mts.getMetaTypeInformation(bundle);
	}
	
	protected Bundle getTestBundle() throws Exception {
		return install("tb4.jar");
	}

	protected void tearDown() throws Exception {
		getContext().ungetService(ref);
		bundle.stop();
		bundle.uninstall();
	}
}
