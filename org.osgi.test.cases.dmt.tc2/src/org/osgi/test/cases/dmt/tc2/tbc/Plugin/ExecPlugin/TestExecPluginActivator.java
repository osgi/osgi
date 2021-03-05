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
 * Feb 22, 2005  Andre Assad
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin;


import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestExecPluginActivator implements BundleActivator {

	private ServiceRegistration< ? >	servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = DmtConstants.OSGi_ROOT +  "/data_plugin";
	
	public static final String INEXISTENT_NODE_NAME = "inexistent";
	
	public static final String INEXISTENT_NODE = ROOT +"/" +INEXISTENT_NODE_NAME ;
	
	public static final String INTERIOR_NODE_NAME = "Interior";
	
	public static final String INTERIOR_NODE = ROOT +"/" + INTERIOR_NODE_NAME;
	
	public static final String INTERIOR_NODE_COPY = ROOT +"/copy";
	
	public static final String INTERIOR_NODE2_COPY = ROOT +"/copy2";
	
	public static final String INTERIOR_NODE2 = ROOT +"/interior2";
	
	public static final String INTERIOR_NODE_WITH_NULL_VALUES = ROOT + "/nullValues";
	
	public static final String INTERIOR_NODE_WITH_TWO_CHILDREN = ROOT + "/two_children";
	
	public static final String CHILD_INTERIOR_NODE = INTERIOR_NODE +"/child";
	
	public static final String INEXISTENT_LEAF_NODE_NAME = INTERIOR_NODE_NAME + "/inexistent_leaf";
	
	public static final String INEXISTENT_LEAF_NODE = INTERIOR_NODE +"/inexistent_leaf";
	
    public static final String LEAF_NAME = "leaf" ;
    
	public static final String LEAF_RELATIVE = INTERIOR_NODE_NAME + "/" + LEAF_NAME ;
	
	public static final String LEAF_NODE = INTERIOR_NODE + "/" + LEAF_NAME ;
	
	public static final String RENAMED_NODE_NAME = "NewNode";
	
	public static final String RENAMED_NODE = ROOT + "/" + RENAMED_NODE_NAME;
	
    public static final String INEXISTENT_INTERIOR_AND_LEAF_NODES = ROOT + "/testA/testB/testC";
    
    public static final String INEXISTENT_INTERIOR_NODES = ROOT + "/testD/testE";
    
	private TestExecPlugin testExecPlugin;
	
	public TestExecPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testExecPlugin = new TestExecPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { ROOT });
		props.put("execRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName(),ExecPlugin.class.getName(), MountPlugin.class.getName() };
		servReg = bc.registerService(ifs, testExecPlugin, props);
		System.out.println("TestDataPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
