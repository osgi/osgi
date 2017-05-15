/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.test.cases.clusterinfo.junit;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.clusterinfo.FrameworkManager;
import org.osgi.service.clusterinfo.FrameworkNodeStatus;
import org.osgi.test.support.OSGiTestCase;

public class ClusterInfoTestCase extends OSGiTestCase {
	public void testFrameworkNodeStatusProperties() {
		BundleContext ctx = getContext();
		ServiceReference<FrameworkNodeStatus> sref = ctx
				.getServiceReference(FrameworkNodeStatus.class);
		Map<String,Object> props = new HashMap<>();

		for (String key : sref.getPropertyKeys()) {
			props.put(key, sref.getProperty(key));
		}

		assertNotNull(props.get("id"));
		assertNotNull(props.get("cluster"));
		assertTrue(props.get("endpoint") != null
				|| props.get("private.endpoint") != null);
		assertNotNull(props.get("version"));

		assertEquals(ctx.getProperty("org.osgi.framework.version"),
				props.get("org.osgi.framework.version"));
		assertEquals(ctx.getProperty("org.osgi.framework.processor"),
				props.get("org.osgi.framework.processor"));
		assertEquals(ctx.getProperty("org.osgi.framework.os.name"),
				props.get("org.osgi.framework.os.name"));
		assertEquals(ctx.getProperty("org.osgi.framework.os.version"),
				props.get("org.osgi.framework.os.version"));

		assertEquals(ctx.getProperty("java.version"),
				props.get("java.version"));
		assertEquals(ctx.getProperty("java.runtime.version"),
				props.get("java.runtime.version"));
		assertEquals(ctx.getProperty("java.specification.version"),
				props.get("java.specification.version"));
		assertEquals(ctx.getProperty("java.vm.version"),
				props.get("java.vm.version"));
    }

	public void testFrameworkManager() throws Exception {
		BundleContext ctx = getContext();
		Collection<ServiceReference<FrameworkManager>> srefs = ctx
				.getServiceReferences(
				FrameworkManager.class,
				"(objectClass=" + FrameworkNodeStatus.class.getName() + ")");
		assertNotNull(srefs);

		assertEquals("One framework manager expected", 1, srefs.size());
		FrameworkManager fm = ctx.getService(srefs.iterator().next());

		URL tb1URL = getClass().getClassLoader().getResource("tb1.jar");
		BundleDTO dto = fm.installBundle(tb1URL.toString());
		BundleDTO dto2 = fm.getBundle(dto.id);
		assertBundleDTOEquals(dto, dto2);

		assertEquals("A custom header",
				fm.getBundleHeaders(dto.id).get("Customer-Header").trim());
		boolean foundDTO = false;
		for (BundleDTO bdto : fm.getBundles()) {
			if (bdto.id == dto.id) {
				assertBundleDTOEquals(bdto, dto);
				foundDTO = true;
			}
		}
		assertTrue(foundDTO);

		assertEquals("Precondition", 0,
				fm.getServiceReferences(
						"(&(objectClass=java.lang.String)(testbundle=tb1))")
						.size());
		fm.startBundle(dto.id);
		// TODO startbundle with options
		
		Collection<ServiceReferenceDTO> bsrefs = fm.getServiceReferences(
				"(&(objectClass=java.lang.String)(testbundle=tb1))");
		assertEquals(1, bsrefs.size());
		ServiceReferenceDTO sdto = bsrefs.iterator().next();
		assertEquals(dto.id, sdto.bundle);
		assertEquals("some value", sdto.properties.get("some.property"));

		assertServiceReferenceDTOEquals(sdto, fm.getServiceReference(sdto.id));

		boolean foundService = false;
		for (ServiceReferenceDTO srdto : fm.getServiceReferences()) {
			if (srdto.id == sdto.id) {
				assertServiceReferenceDTOEquals(sdto, srdto);
				foundService = true;
			}
		}
		assertTrue(foundService);

		assertEquals("Precondition", 0,
				fm.getServiceReferences(
						"(&(objectClass=java.lang.String)(testbundle=tb2))")
						.size());

		URL tb2URL = getClass().getClassLoader().getResource("tb2.jar");
		BundleDTO udto = fm.updateBundle(dto.id, tb2URL.toString());
		// TODO updateBundle(long)
		assertEquals("Service from pre-update bundle should be gone", 0,
				fm.getServiceReferences(
						"(&(objectClass=java.lang.String)(testbundle=tb1))")
						.size());
		Collection<ServiceReferenceDTO> usrefs = fm.getServiceReferences(
				"(&(objectClass=java.lang.String)(testbundle=tb2))");
		assertEquals(1, usrefs.size());

		// TODO check udto
		// TODO stopBundle
		// TODO uninstallBundle
	}

	private void assertBundleDTOEquals(BundleDTO dto, BundleDTO dto2) {
		assertEquals(dto.id, dto2.id);
		assertEquals(dto.lastModified, dto2.lastModified);
		assertEquals(dto.state, dto2.state);
		assertEquals(dto.symbolicName, dto2.symbolicName);
		assertEquals(dto.version, dto2.version);
	}

	private void assertServiceReferenceDTOEquals(ServiceReferenceDTO dto,
			ServiceReferenceDTO dto2) {
		assertEquals(dto.id, dto2.id);
		assertEquals(dto.bundle, dto2.bundle);
		assertServicePropMapEquals(dto.properties, dto2.properties);
		assertTrue(Arrays.equals(dto.usingBundles, dto2.usingBundles));
	}

	private void assertServicePropMapEquals(Map<String,Object> p1,
			Map<String,Object> p2) {
		assertEquals(p1.size(), p2.size());

		for (Map.Entry<String,Object> entry : p1.entrySet()) {
			Object v1 = entry.getValue();
			Object v2 = p2.get(entry.getKey());
			if (v1 instanceof Object[]) {
				assertTrue(Arrays.equals((Object[]) v1, (Object[]) v2));
			} else {
				assertEquals(v1, v2);
			}
		}
	}
}


