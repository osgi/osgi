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

package org.osgi.test.cases.clusterinfo.secure.junit;

import java.net.URL;
import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.clusterinfo.FrameworkManager;
import org.osgi.service.clusterinfo.NodeStatus;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class ClusterInfoSecurityTestCase extends OSGiTestCase {

	public void testPermissionDenied() throws Exception {
		BundleContext ctx = getContext();

		ServiceTracker<NodeStatus,NodeStatus> st = new ServiceTracker<NodeStatus,NodeStatus>(
				ctx,
				ctx.createFilter("(&(objectClass=" + NodeStatus.class.getName()
						+ ")(osgi.clusterinfo.tags=foo))"),
				null);
		st.open();

		Collection<ServiceReference<FrameworkManager>> srefs = ctx
				.getServiceReferences(FrameworkManager.class,
						"(objectClass=" + NodeStatus.class.getName() + ")");
		assertEquals(1, srefs.size());
		FrameworkManager fm = ctx.getService(srefs.iterator().next());

		URL tbURL = getClass().getClassLoader().getResource("tb1.jar");
		BundleDTO dto = fm.installBundle(tbURL.toString());

		try {
			fm.startBundle(dto.id);
			assertNull("Bundle should not have permission to add tag",
					st.getService());
		} finally {
			fm.uninstallBundle(dto.id);
		}
	}

	public void testPermissionOk() throws Exception {
		BundleContext ctx = getContext();

		ServiceTracker<NodeStatus,NodeStatus> st = new ServiceTracker<NodeStatus,NodeStatus>(
				ctx,
				ctx.createFilter("(&(objectClass=" + NodeStatus.class.getName()
						+ ")(osgi.clusterinfo.tags=foo))"),
				null);
		st.open();

		Collection<ServiceReference<FrameworkManager>> srefs = ctx
				.getServiceReferences(FrameworkManager.class,
						"(objectClass=" + NodeStatus.class.getName() + ")");
		assertEquals(1, srefs.size());
		FrameworkManager fm = ctx.getService(srefs.iterator().next());

		assertNull("Precondition", st.getService());

		URL tbURL = getClass().getClassLoader().getResource("tb2.jar");
		BundleDTO dto = fm.installBundle(tbURL.toString());

		try {
			fm.startBundle(dto.id);

			assertNotNull(
					"The service with added custom properties should be there now",
					st.waitForService(5000));
		} finally {
			fm.uninstallBundle(dto.id);
		}
	}
}


