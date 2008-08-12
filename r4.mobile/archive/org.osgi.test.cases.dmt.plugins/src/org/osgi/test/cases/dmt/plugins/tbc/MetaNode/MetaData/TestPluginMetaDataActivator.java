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

package org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestPluginMetaDataActivator implements BundleActivator {

	private ServiceRegistration servReg;
	
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

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testPluginMetaData = new TestPluginMetaData(tbc);
		Hashtable props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { ROOT });
		props.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName() };
		servReg = bc.registerService(ifs, testPluginMetaData, props);
		System.out.println("TestPluginMetaData activated.");
	}

	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
