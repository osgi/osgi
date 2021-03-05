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
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 244           [MEGTCK][DMT] Implements the investigates after feedback.
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class TestNonAtomicPluginActivator implements BundleActivator {

	private ServiceRegistration<DataPlugin>	servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = DmtConstants.OSGi_ROOT + "/non_atomic_plugin";
	
	public static final String INTERIOR_NODE = ROOT + "/interior";
	
	public static final String INEXISTENT_NODE = ROOT + "/inexistent";
	
	public static final String LEAF_NODE = ROOT + "/leaf";
	
	public static final String INEXISTENT_LEAF_NODE = ROOT + "/inexistent_leaf";
	
	private TestNonAtomicPlugin testNonAtomicPlugin;
	
	public TestNonAtomicPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testNonAtomicPlugin = new TestNonAtomicPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { ROOT });
		servReg = bc.registerService(DataPlugin.class, testNonAtomicPlugin,
				props);
		System.out.println("TestReadOnlyPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
