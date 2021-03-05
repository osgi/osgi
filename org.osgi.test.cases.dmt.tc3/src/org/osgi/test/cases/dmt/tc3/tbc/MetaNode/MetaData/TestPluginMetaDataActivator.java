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

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestPluginMetaDataActivator implements BundleActivator {

	private ServiceRegistration<DataPlugin>		servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = DmtConstants.OSGi_ROOT + "/metadata_plugin";
	
	public static final String INEXISTENT_NODE_NAME = "inexistent_node";
	public static final String INEXISTENT_NODE = ROOT +"/" + INEXISTENT_NODE_NAME;
	
	public static final String INEXISTENT_NODE_INVALID_NAME = ROOT +"/invalid_name";
	
	public static final String INTERIOR_NODE_STRING = "interior";
	public static final String INTERIOR_NODE = ROOT +"/" + INTERIOR_NODE_STRING;
	
	public static final String INEXISTENT_LEAF_NODE_STRING = "inexistent_leaf";
	public static final String INEXISTENT_LEAF_NODE = INTERIOR_NODE +"/" + INEXISTENT_LEAF_NODE_STRING;
	
	public static final String LEAF_NODE_STRING = "leaf"; 
	public static final String LEAF_NODE = INTERIOR_NODE +"/"+ LEAF_NODE_STRING;

	public static final String INEXISTENT_LEAF_NODE_INVALID_NAME = INTERIOR_NODE +"/leaf_invalid_name";
    
    public static final String RENAMED_NODE_STRING = "rename";
    
    public static final String RENAMED_NODE_IS_AN_INTERIOR_NODE = ROOT +"/" + INEXISTENT_NODE_NAME;
	
	public static TestPluginMetaDataMetaNode metaNodeDefault;
	
	private TestPluginMetaData testPluginMetaData;
	
	public TestPluginMetaDataActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testPluginMetaData = new TestPluginMetaData(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { ROOT });
		props.put("execRootURIs", new String[] { ROOT });
		servReg = bc.registerService(DataPlugin.class, testPluginMetaData,
				props);
		System.out.println("TestPluginMetaData activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
