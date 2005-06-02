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
 * 03/05/2005   Leonardo Barros
 * 38           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.application.tb2.MegletDescriptor.GetProperties;
import org.osgi.test.cases.application.tb2.MegletDescriptor.Launch;
import org.osgi.test.cases.application.tb2.MegletDescriptor.Lock;
import org.osgi.test.cases.application.tb2.MegletDescriptor.Schedule;
import org.osgi.test.cases.application.tb2.MegletDescriptor.Unlock;
import org.osgi.test.cases.application.tb2.MegletHandle.Destroy;
import org.osgi.test.cases.application.tb2.MegletHandle.GetApplicationDescriptor;
import org.osgi.test.cases.application.tb2.MegletHandle.GetInstanceID;
import org.osgi.test.cases.application.tb2.MegletHandle.GetState;
import org.osgi.test.cases.application.tb2.MegletHandle.Resume;
import org.osgi.test.cases.application.tb2.MegletHandle.Suspend;
import org.osgi.test.cases.application.tb2.ScheduledApplication.GetArguments;
import org.osgi.test.cases.application.tb2.ScheduledApplication.GetEventFilter;
import org.osgi.test.cases.application.tb2.ScheduledApplication.GetTopic;
import org.osgi.test.cases.application.tb2.ScheduledApplication.IsRecurring;
import org.osgi.test.cases.application.tb2.ScheduledApplication.Remove;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TB2Service;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.UseCases.LifecycleStates;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class Activator implements BundleActivator, TB2Service {
	private ServiceRegistration servReg;

	private ApplicationTestControl tbc;

	public void start(BundleContext bc) throws Exception {
		servReg = bc.registerService(TB2Service.class.getName(), this, null);
		System.out.println("TB2Service activated.");
	}

	public void stop(BundleContext arg0) throws Exception {
		servReg.unregister();
	}

	public TestInterface[] getTestClasses(DefaultTestBundleControl tbc) {
		return new TestInterface[] {
				new Lock((ApplicationTestControl) tbc),
				new Unlock((ApplicationTestControl) tbc),
				new GetApplicationDescriptor((ApplicationTestControl) tbc),
				new GetInstanceID((ApplicationTestControl) tbc),
				new GetState((ApplicationTestControl) tbc),
				new Suspend((ApplicationTestControl) tbc),
				new GetProperties((ApplicationTestControl) tbc),
				new Launch((ApplicationTestControl) tbc),
				new Resume((ApplicationTestControl) tbc),
				new Destroy((ApplicationTestControl) tbc),
				new Schedule((ApplicationTestControl) tbc),
				new GetTopic((ApplicationTestControl) tbc),
				new GetEventFilter((ApplicationTestControl) tbc),
				new IsRecurring((ApplicationTestControl) tbc),
				new GetArguments((ApplicationTestControl) tbc),
				new org.osgi.test.cases.application.tb2.ScheduledApplication.GetApplicationDescriptor(
						(ApplicationTestControl) tbc),
				new Remove((ApplicationTestControl) tbc),
				new LifecycleStates((ApplicationTestControl) tbc)};
	}

}
