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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================

 */

package org.osgi.test.cases.dmt.tc3.tbc.ExecPlugin;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 *         Updated by Steffen Druesedow (steffen.druesedow@telekom.de): 
 *         The test case attempts to open a session directly on the node
 *         that is going to be executed - therefore this node has to be present.
 *         In order to make the test cases work again, a "dataRootURI" was added
 *         to this registration.
 *         (It worked before, because an additional
 *         OverlappingExecPluginActivator was registered with a matching
 *         dataRootURI, which is de-activated now because the overlapping topic
 *         changed completely with RFC141).
 * 
 */
public class TestExecPluginActivator implements BundleActivator {

	private ServiceRegistration< ? >	servReg;

	private DmtTestControl tbc;

	public static final String ROOT = DmtConstants.OSGi_ROOT + "/exec_plugin";

	public static final String INTERIOR_NODE_EXCEPTION = ROOT + "/exception";

	private TestExecPlugin testExecPlugin;

	public TestExecPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testExecPlugin = new TestExecPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("execRootURIs", new String[] { ROOT });
		props.put("dataRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName(),
				ExecPlugin.class.getName() };
		servReg = bc.registerService(ifs, testExecPlugin, props);
		System.out.println("TestExecPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
