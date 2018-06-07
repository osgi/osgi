/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.test.cases.metatype.junit;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Namespace;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class CapabilitiesTestCase extends OSGiTestCase {
	private static final Version							VERSION	= new Version(
			1, 4, 0);
	private static final String									PACKAGE	= "org.osgi.service.metatype";
	private static final String									NAME	= "osgi.metatype";
	private ServiceTracker<MetaTypeService, MetaTypeService>	mtsTracker;
	private BundleWiring										wiring;

	public CapabilitiesTestCase() {
	}

	protected void setUp() throws Exception {
		super.setUp();
		mtsTracker = new ServiceTracker<MetaTypeService, MetaTypeService>(getContext(), MetaTypeService.class.getName(),
				null);
		mtsTracker.open();
		assertEquals("Wrong number of MetaTypeServices", 1, mtsTracker.size());
		Bundle impl = mtsTracker.getServiceReference().getBundle();
		assertNotNull("MetaTypeService implementation bundle not found", impl);
		wiring = impl.adapt(BundleWiring.class);
		assertNotNull("No BundleWiring available for SCR bundle", wiring);
	}

	protected void tearDown() throws Exception {
		mtsTracker.close();
		super.tearDown();
	}

	public void testExtenderCapability() throws Exception {
		final String namespace = ExtenderNamespace.EXTENDER_NAMESPACE;
		boolean found = false;
		List<BundleCapability> extenders = wiring.getCapabilities(namespace);
		for (BundleCapability extender : extenders) {
			if (NAME.equals(extender.getAttributes().get(namespace))) {
				found = true;
				assertEquals(namespace + " capability version wrong", VERSION,
						extender.getAttributes().get(ExtenderNamespace.CAPABILITY_VERSION_ATTRIBUTE));
				String uses = extender.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull(namespace + " capability uses directive missing", uses);
				boolean usefound = uses.indexOf(PACKAGE) >= 0;
				assertTrue(namespace + " capability missing " + PACKAGE + " in uses", usefound);
				break;
			}
		}
		assertTrue("missing " + namespace + " capability for " + NAME, found);
	}

	public void testImplementationCapability() throws Exception {
		final String namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE;
		boolean found = false;
		List<BundleCapability> extenders = wiring.getCapabilities(namespace);
		for (BundleCapability extender : extenders) {
			if (NAME.equals(extender.getAttributes().get(namespace))) {
				found = true;
				assertEquals(namespace + " capability version wrong", VERSION,
						extender.getAttributes().get(ImplementationNamespace.CAPABILITY_VERSION_ATTRIBUTE));
				String uses = extender.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull(namespace + " capability uses directive missing", uses);
				boolean usefound = uses.indexOf(PACKAGE) >= 0;
				assertTrue(namespace + " capability missing " + PACKAGE + " in uses", usefound);
				break;
			}
		}
		assertTrue("missing " + namespace + " capability for " + NAME, found);
	}

	public void testServiceCapability() throws Exception {
		final String namespace = ServiceNamespace.SERVICE_NAMESPACE;
		boolean found = false;
		List<BundleCapability> services = wiring.getCapabilities(namespace);
		for (BundleCapability service : services) {
			@SuppressWarnings("unchecked")
			List<String> objectClass = (List<String>) service.getAttributes()
					.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			if (objectClass.contains(MetaTypeService.class.getName())) {
				found = true;
				String uses = service.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull(namespace + " capability uses directive missing", uses);
				boolean usefound = uses.indexOf(PACKAGE) >= 0;
				assertTrue(namespace + " capability missing " + PACKAGE + " in uses", usefound);
				break;
			}
		}
		assertTrue("missing " + namespace + " capability for " + MetaTypeService.class.getName(), found);
	}

}
