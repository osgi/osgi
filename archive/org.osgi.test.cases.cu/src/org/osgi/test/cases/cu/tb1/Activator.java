/*
 * $Header$
 * 
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

package org.osgi.test.cases.cu.tb1;

import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.cu.*;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;

/**
 * Bundle that registers 3 different Control Units used by the test control.
 * These CUs simulate a positioning module with gyroscope and tachometer data. 
 * The hip CU has two children (hip.gyro, hip.tacho)
 * @version $Revision$
 */
public class Activator implements BundleActivator {
	private ServiceRegistration regHip;
	private ServiceRegistration regGyro;
	private ServiceRegistration regTacho;
	
	public void start(BundleContext context) throws Exception {
		Hashtable p = new Hashtable();
		
		// Register hip Control Unit
		p.put(ControlUnitConstants.TYPE, "hip");
		p.put(ControlUnitConstants.ID, "hip.id");
		regHip = context.registerService(ManagedControlUnit.class.getName(), new HipModule(), p);
		
		// Register hip.gyro Control Unit
		p.clear();
		p.put(ControlUnitConstants.TYPE, "hip.gyro");
		p.put(ControlUnitConstants.ID, "hip.gyro.id");
		p.put(ControlUnitConstants.PARENT_TYPE, "hip");
		p.put(ControlUnitConstants.PARENT_ID, "hip.id");
		regGyro = context.registerService(ManagedControlUnit.class.getName(), new HipGyro(), p);
		
		// Register hip.tacho Control Unit 
		p.clear();
		p.put(ControlUnitConstants.TYPE, "hip.tacho");
		p.put(ControlUnitConstants.ID, "hip.tacho.id");
		p.put(ControlUnitConstants.PARENT_TYPE, "hip");
		p.put(ControlUnitConstants.PARENT_ID, "hip.id");
		regTacho = context.registerService(ManagedControlUnit.class.getName(), new HipTacho(), p);
	}
	
	public void stop(BundleContext context) throws Exception {
		if (regHip != null)
			regHip.unregister();
		if (regGyro != null)
			regGyro.unregister();
		if (regTacho!= null)
			regTacho.unregister();
	}
}
