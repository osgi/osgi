/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestMetaNodeDataPluginActivator implements BundleActivator {

	private ServiceRegistration servReg;
	
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
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testMetaNodeDataPlugin = new TestMetaNodeDataPlugin(tbc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { ROOT });
        props.put("execRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName(),ExecPlugin.class.getName() };
		servReg = bc.registerService(ifs, testMetaNodeDataPlugin, props);
		System.out.println("TestMetaNodeDataPlugin activated.");
	}

	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
