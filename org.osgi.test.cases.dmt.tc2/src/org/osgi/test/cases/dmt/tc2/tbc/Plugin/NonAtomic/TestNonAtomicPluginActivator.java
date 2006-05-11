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
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 244           [MEGTCK][DMT] Implements the investigates after feedback.
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import info.dmtree.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class TestNonAtomicPluginActivator implements BundleActivator {

	private ServiceRegistration servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = "./OSGi/non_atomic_plugin";
	
	public static final String INTERIOR_NODE = ROOT + "/interior";
	
	public static final String INEXISTENT_NODE = ROOT + "/inexistent";
	
	public static final String LEAF_NODE = ROOT + "/leaf";
	
	public static final String INEXISTENT_LEAF_NODE = ROOT + "/inexistent_leaf";
	
	private TestNonAtomicPlugin testNonAtomicPlugin;
	
	public TestNonAtomicPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testNonAtomicPlugin = new TestNonAtomicPlugin(tbc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName() };
		servReg = bc.registerService(ifs, testNonAtomicPlugin, props);
		System.out.println("TestReadOnlyPlugin activated.");
	}

	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
