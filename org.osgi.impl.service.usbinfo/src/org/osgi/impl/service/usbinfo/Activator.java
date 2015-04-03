/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.usbinfo;

import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.step.TestStep;

public class Activator implements BundleActivator {

	private static BundleContext	context		= null;

	private ServiceRegistration		testStepReg	= null;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {

		Activator.context = context;

		USBTracker.getInstance().open();

		TestStep testStep = new TestStepImpl();
		Dictionary prop = new Properties();
		prop.put(Constants.SERVICE_PID, "org.osgi.service.usbinfo");
		testStepReg = context.registerService(TestStep.class.getName(), testStep, prop);
	}

	public void stop(BundleContext context) throws Exception {

		if (testStepReg != null) {
			testStepReg.unregister();
		}

		USBTracker.getInstance().close();

		Activator.context = null;
	}
}
