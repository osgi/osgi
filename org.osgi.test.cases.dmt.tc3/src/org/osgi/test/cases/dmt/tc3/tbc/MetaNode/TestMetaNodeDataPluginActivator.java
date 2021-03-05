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

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

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
 * @author Andre Assad
 * 
 */
public class TestMetaNodeDataPluginActivator implements BundleActivator {

	private ServiceRegistration< ? >	servReg;
	
	public static final String ROOT = DmtConstants.OSGi_ROOT + "/meta_node";
    
    public static final String NODE_WITHOUT_METANODE = ROOT +"/node";
    
    public static final String INEXISTENT_NODE_WITHOUT_METANODE = ROOT +"/inex_node";
	
    public static final String INEXISTENT_NODE_NAME = "inexistent";
    
	public static final String INEXISTENT_NODE = ROOT +"/" + INEXISTENT_NODE_NAME ;
    
    public static final String INEXISTENT_LEAF_NODE_NAME = "inexistent_leaf";
    
    public static final String INEXISTENT_LEAF_NODE = ROOT +"/" + INEXISTENT_LEAF_NODE_NAME;
    
    public static final String INEXISTENT_LEAF_NODE_WITHOUT_METANODE = ROOT +"/inex_leaf";
    
    public static final String PARENT_OF_NODE_THAT_CANNOT_BE_DELETED = ROOT +"/parent";
    
    public static final String NODE_CANNOT_BE_DELETED = PARENT_OF_NODE_THAT_CANNOT_BE_DELETED +"/cannot";
    
    public static final String LEAF_NODE = ROOT +"/leaf";
    
    public static final String INTERIOR_NODE_NAME = "interior";
    
    public static final String INTERIOR_NODE = ROOT +"/"+ INTERIOR_NODE_NAME;
    
    public static final String INEXISTENT_NODE_WITHOUT_PERMISSIONS_NAME = "without";
    
    public static final String INEXISTENT_NODE_WITHOUT_PERMISSIONS = ROOT +"/" + INEXISTENT_NODE_WITHOUT_PERMISSIONS_NAME ;
    
    public static final String PERMANENT_INTERIOR_NODE = ROOT +"/permanent";
    
    public static final String PERMANENT_INEXISTENT_NODE_NAME = "inex_permanent";
    
    public static final String PERMANENT_INEXISTENT_NODE = ROOT +"/" + PERMANENT_INEXISTENT_NODE_NAME;
    
    public static final String INTERIOR_NODE_WITHOUT_GET_PERMISSION = ROOT +"/withoutget";
    
	private DmtTestControl tbc;
    
	private TestMetaNodeDataPlugin testMetaNodeDataPlugin;

    public TestMetaNodeDataPluginActivator(DmtTestControl tbc) {
        this.tbc=tbc;
    }
	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testMetaNodeDataPlugin = new TestMetaNodeDataPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { ROOT });
        props.put("execRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName(),ExecPlugin.class.getName() };
		servReg = bc.registerService(ifs, testMetaNodeDataPlugin, props);
		System.out.println("TestMetaNodeDataPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
