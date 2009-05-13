/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 15/04/2005   Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetDescription;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetMonitorableNames;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetRunningJobs;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetStatusVariable;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetStatusVariableNames;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.GetStatusVariables;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.ResetStatusVariable;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.StartJob;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.StartScheduledJob;
import org.osgi.test.cases.monitor.tb1.MonitorAdmin.SwitchEvents;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TB1Service;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author Alexandre Santos
 *
 */
public class Activator implements BundleActivator, TB1Service {

	private ServiceRegistration servReg;
	
	public void start(BundleContext bc) throws Exception {
		
		servReg = bc.registerService(TB1Service.class.getName(), this, null);
		System.out.println("TB1Service activated.");
	}

	public void stop(BundleContext arg0) throws Exception {
		servReg.unregister();
	}

	public TestInterface[] getTestClasses(DefaultTestBundleControl tbc) {
		return new TestInterface[] {
				new GetDescription((MonitorTestControl) tbc),
				new GetMonitorableNames((MonitorTestControl) tbc),
				new GetRunningJobs((MonitorTestControl) tbc),
				new GetStatusVariable((MonitorTestControl) tbc),
				new GetStatusVariableNames((MonitorTestControl) tbc),
				new GetStatusVariables((MonitorTestControl) tbc),
				new ResetStatusVariable((MonitorTestControl) tbc),
				new StartJob((MonitorTestControl) tbc),
				new StartScheduledJob((MonitorTestControl) tbc),
				new SwitchEvents((MonitorTestControl) tbc)
		};
	}
	
}
