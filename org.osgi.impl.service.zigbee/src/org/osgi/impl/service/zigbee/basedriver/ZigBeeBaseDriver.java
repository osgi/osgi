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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.zigbee.basedriver.configuration.ConfigurationFileReader;
import org.osgi.impl.service.zigbee.basedriver.configuration.NetworkAttributeIds;
import org.osgi.impl.service.zigbee.event.ZigBeeEventImpl;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.impl.service.zigbee.util.teststep.TestStepForZigBeeImpl;
import org.osgi.impl.service.zigbee.util.teststep.ZigBeeEventSourceImpl;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.test.support.step.TestStep;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 * 
 * @author $Id$
 */
public class ZigBeeBaseDriver {

	private static final String		TAG					= ZigBeeBaseDriver.class.getName();

	BundleContext					bc					= null;

	private List					importedEpsList		= new ArrayList();
	private List					exportedEpsList		= new ArrayList();

	private ServiceTracker			exportedEpsTracker	= null;

	private ConfigurationFileReader	conf;
	private ServiceRegistration		sReg				= null;
	private ServiceRegistration		sRegTestStep		= null;

	private static final String		EXPORTED_EPS_FILTER	= "(&(objectclass=" + ZigBeeEndpoint.class.getName() + ")(" + ZigBeeEndpoint.ZIGBEE_EXPORT + "=))";

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
	 * This method starts the base driver. And registers with controller for
	 * getting notifications.
	 */
	public void start() {
		Logger.d(TAG, "Start the base driver.");

		TestStepForZigBeeImpl testStep = new TestStepForZigBeeImpl(this);
		sRegTestStep = bc.registerService(TestStep.class.getName(), testStep, null);

		try {
			exportedEpsTracker = new ServiceTracker(bc, bc.createFilter(EXPORTED_EPS_FILTER), new ServiceTrackerCustomizer() {
				public Object addingService(ServiceReference ref) {
					ZigBeeEndpoint endpoint = (ZigBeeEndpoint) bc.getService(ref);
					boolean hasBeenExported = tryToExportEndpoint(endpoint);
					if (hasBeenExported) {
						addExportedEndpoint(endpoint);
						ZigBeeNode node = getZigBeeNode(endpoint.getNodeAddress());
						if (node == null) {
							Logger.e(TAG, "Fatal error: this must never happen");
						} else {
							node.getEndpoints();
						}
					}
					return endpoint;
				}

				public void modifiedService(ServiceReference reference, Object service) {
					/*
					 * In our CT we don't test this scenario, because it is
					 * really unlikely that an exported service changes its
					 * properties.
					 */
				}

				public void removedService(ServiceReference reference, Object service) {
					removeExportedEndpoint((ZigBeeEndpoint) service);
				}
			});

			/*
			 * Starts to track exported ZigBeeEndpoint services.
			 */
			exportedEpsTracker.open();
		} catch (InvalidSyntaxException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * This method stops the base driver. And unregisters with controller for
	 * getting notifications.
	 */
	public void stop() {
		exportedEpsTracker.close();

		if (this.sReg != null) {
			sReg.unregister();
		}

		if (this.sRegTestStep != null) {
			sRegTestStep.unregister();
		}

		/**
		 * TODO: unregister: ZigBeeNodes, ZigBeeEndpoints services.
		 */

		Logger.d(TAG, "Stop the base driver.");
	}

	/**
	 * Checks if the passed endpoint has the same ID and the same IEEE address
	 * of an already registered exported ZigBeeEndpoint service. If this
	 * happens, the notExported method is called on the new endpoint to notify
	 * it that cannot be exported.
	 * 
	 * @param newEndpoint A {@link ZigBeeEndpoint} that has been just
	 *        registered.
	 * 
	 * @return {@code true} if the endpoint can be exported successfully. It
	 *         means that notExported() method has not been called on the new
	 *         endpoint.
	 */

	protected boolean tryToExportEndpoint(ZigBeeEndpoint newEndpoint) {
		for (Iterator it = exportedEpsList.iterator(); it.hasNext();) {
			ZigBeeEndpoint endpoint = (ZigBeeEndpoint) it.next();
			if (endpoint.getId() == newEndpoint.getId() && endpoint.getNodeAddress().equals(newEndpoint.getNodeAddress())) {
				try {
					newEndpoint.notExported(new ZigBeeException(ZigBeeException.OSGI_EXISTING_ID, "this Id already exists"));
				} catch (Throwable e) {
					/*
					 * Isolates the RI from any exception occurring calling the
					 * notExported() method.
					 */
					Logger.e(TAG, "got from the endpoint exception '" + e.getMessage() + "' while calling notExported()");
				}
				return true;
			}
		}
		return false;
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
	 */

	public void loadConfigurationFile(InputStream is) {
		conf = ConfigurationFileReader.getInstance(is);

		/*
		 * the configuration file contains the minimum ZCL Header size, that is
		 * a number that can be found inside the ZCL cluster library
		 * specification document.
		 */

		ZCLFrameImpl.minHeaderSize = conf.getHeaderMinSize();

		/*
		 * register a ZigBeeHost service with the information found in the
		 * configuration file.
		 */

		ZigBeeHost host = conf.getZigBeeHost();
		Dictionary hostProperties = new Properties();
		hostProperties.put(ZigBeeNode.IEEE_ADDRESS, host.getIEEEAddress());

		sReg = bc.registerService(ZigBeeHost.class.getName(), host, hostProperties);

		/*
		 * Register the ZigBeeNode services and their ZigBeeEndpoint services
		 * according to the information present in the configuration file. The
		 * ZigBeeEndpoint belonging to a ZigBeeNode are registered before their
		 * ZigBeeNode.
		 */

		ZigBeeNodeImpl[] nodes = conf.getZigBeeNodes();

		for (int i = 0; i < nodes.length; i++) {
			ZigBeeNodeImpl node = nodes[i];
			ZigBeeEndpoint[] endpoints = conf.getEnpoints(node);
			for (int j = 0; j < endpoints.length; j++) {
				ZigBeeEndpoint endpoint = endpoints[j];
				Dictionary props = new Hashtable();

				props.put(ZigBeeNode.IEEE_ADDRESS, node.getIEEEAddress());
				props.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(endpoint.getId()));

				bc.registerService(ZigBeeEndpoint.class.getName(), endpoint, props);

				importedEpsList.add(endpoint);
			}

			Dictionary nodeProperties = new Hashtable();
			nodeProperties.put(ZigBeeNode.IEEE_ADDRESS, node.getIEEEAddress());

			/*
			 * Register the ZigBeeNode the ZigBeeNodes registered above belong
			 * to.
			 */
			bc.registerService(ZigBeeNode.class.getName(), node, nodeProperties);
		}
	}

	/**
	 * Adds the passed endpoint to the list of ZigBeeEndpoint services that have
	 * been actually exported by the base driver.
	 * 
	 * @param endpoint The endpoint that has been exported.
	 */
	protected void addExportedEndpoint(ZigBeeEndpoint endpoint) {
		this.exportedEpsList.add(endpoint);
	}

	/**
	 * Removes the passed endpoint from the list of ZigBeeEndpoint services that
	 * have been actually exported.
	 * 
	 * @param endpoint The endpoint that need to be removed.
	 */

	protected void removeExportedEndpoint(ZigBeeEndpoint endpoint) {
		this.exportedEpsList.remove(endpoint);
	}

	/**
	 * Called to simulate a reportable attribute.
	 */
	public void startReportableEventing() {

		NetworkAttributeIds attrId = conf.getFirstReportableAttribute();
		Integer value = new Integer(12);
		ZigBeeEvent event = this.createEvent(attrId, value);

		// create, and launch a test event source.
		ZigBeeEventSourceImpl aZigBeeEventSourceImpl = new ZigBeeEventSourceImpl(bc, event);
		aZigBeeEventSourceImpl.start();
	}

	protected ZigBeeEvent createEvent(NetworkAttributeIds attrId, Object value) {
		ZigBeeEvent event = new ZigBeeEventImpl(attrId.getIeeeAddresss(),
				attrId.getEndpointId(),
				attrId.getClusterId(),
				attrId.getAttributeId(),
				value);

		return event;
	}

	/**
	 * We are in the base driver so we know which are the Nodes we registered
	 * and we don't have to ask the Service registry for them!
	 * 
	 * @param nodeIeeeAddress
	 * @return The {@link ZigBeeNode} that matches the passed nodeIeeeAddress.
	 */

	protected ZigBeeNode getZigBeeNode(BigInteger nodeIeeeAddress) {
		ZigBeeNodeImpl[] nodes = conf.getZigBeeNodes();

		for (int i = 0; i < nodes.length; i++) {
			ZigBeeNode node = nodes[i];
			if (nodeIeeeAddress.equals(node.getIEEEAddress())) {
				return node;
			}
		}
		return null;
	}
}
