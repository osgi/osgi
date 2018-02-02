/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.basedriver;

import java.io.InputStream;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.zigbee.basedriver.configuration.ConfigurationFileReader;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.impl.service.zigbee.util.teststep.TestStepForZigBeeImpl;
import org.osgi.test.support.step.TestStep;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 * 
 * @author $Id$
 */
public class ZigBeeBaseDriver {

	private static final String		TAG				= ZigBeeBaseDriver.class.getName();

	BundleContext					bc				= null;

	private ConfigurationFileReader	conf;

	private ServiceRegistration		sRegTestStep	= null;

	/**
	 * This constructor creates the ZigBeeBaseDriver object based on the
	 * controller and the BundleContext object.
	 * 
	 * @param bc
	 */
	public ZigBeeBaseDriver(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * This method starts the base driver. It registers also the TestStep
	 * service.
	 * 
	 * @throws InvalidSyntaxException
	 */
	public void start() throws InvalidSyntaxException {
		Logger.d(TAG, "Start the base driver.");

		TestStepForZigBeeImpl testStep = new TestStepForZigBeeImpl(this);
		sRegTestStep = bc.registerService(TestStep.class.getName(), testStep, null);
	}

	/**
	 * This method stops the base driver. And unregisters with controller for
	 * getting notifications.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {

		ZigBeeHostImpl host = conf.getZigBeeHost();

		host.deactivate();

		if (this.sRegTestStep != null) {
			sRegTestStep.unregister();
		}

		Logger.d(TAG, "Stop the base driver.");
	}

	/**
	 * Loads the configuration file. This file contains information about
	 * 'simulated' devices. The file contains information that have to be filled
	 * by whom is running the CT against its own implementation of this
	 * specification. These information must be aligned with the ZigBee
	 * characteristics of the real ZigBee devices used to test a real
	 * implementation.
	 * 
	 * <p>
	 * The root directory of the CT project contains a template of this file
	 * that is also passed to this method.
	 * 
	 * @param is The InputStream where to read the configuration file.
	 * @throws Exception
	 */

	public void loadConfigurationFile(InputStream is) throws Exception {
		conf = ConfigurationFileReader.getInstance(is);

		/*
		 * the configuration file contains the minimum ZCL Header size, that is
		 * a number that can be found inside the ZCL cluster library
		 * specification document.
		 */

		ZCLFrameImpl.minHeaderSize = conf.getHeaderMinSize();
		ZCLFrameImpl.maxHeaderSize = conf.getHeaderMaxSize();

		ZigBeeHostImpl host = conf.getZigBeeHost();

		host.setContext(bc);

		/*
		 * Activate the ZigBeeHost. We need to set the isStarted status of the
		 * ZigBeeHost to true or false according to the previous status of the
		 * ZigBeeHost (before a restart of the bundle). If passed true, the
		 * ZigBeeHost implementation will start to register itself and its
		 * ZigBeeNodes and ZigBee endpoints.
		 */
		host.activate(true);
	}
}
