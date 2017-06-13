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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.zigbee.basedriver.configuration.ParserUtils;
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.impl.service.zigbee.event.ZigBeeEventImpl;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.impl.service.zigbee.util.teststep.ZigBeeEventSourceImpl;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZigBeeHostImpl extends ZigBeeNodeImpl implements ZigBeeHost {

	private static final String	TAG						= ZigBeeHostImpl.class.getName();

	private long				communicationTimeout	= 60 * 1000;
	private String				hostPid;
	private int					currentChannel;
	private int					securityLevel;
	private int					panId;
	private BigInteger			extendedPanId;
	private int					channelMask;
	private short				broadcastRadius;

	private String				preconfiguredLinkKey	= "KEY";

	private List				nodes					= new ArrayList();

	private final static int	STOPPED					= 0;
	private final static int	STARTING				= 1;
	private final static int	STARTED					= 2;
	private final static int	STOPPING				= 3;

	private int					state					= STOPPED;

	private ServiceRegistration	hostServiceReg			= null;

	private BundleContext		bc;

	private List				importedEpsList			= new ArrayList();
	private List				exportedEpsList			= new ArrayList();

	private static final String	EXPORTED_EPS_FILTER		= "(&(objectClass=" + ZigBeeEndpoint.class.getName() + ")(" + ZigBeeEndpoint.ZIGBEE_EXPORT + "=*))";

	private ServiceTracker		exportedEpsTracker		= null;

	/**
	 * ServiceTracker for ZCLEventListener objects.
	 */
	private ServiceTracker		eventListenersTracker	= null;

	protected Map				eventListenersMap		= new HashMap();

	/**
	 * Creates a ZigBeeHost object.
	 * 
	 * @param hostPid
	 * @param panId
	 * @param channel
	 * @param securityLevel
	 * @param IEEEAddress
	 * @param endpoints
	 * @param nodeDescriptor
	 * @param powerDescriptor
	 * @param userdescription
	 */
	public ZigBeeHostImpl(String hostPid, int panId, int channel, int nwkAddress, int securityLevel, BigInteger IEEEAddress, ZigBeeNodeDescriptor nodeDescriptor,
			ZigBeePowerDescriptor powerDescriptor, String userdescription) {
		super(IEEEAddress, nwkAddress, new ZigBeeEndpointImpl[0], nodeDescriptor, powerDescriptor, userdescription);

		this.currentChannel = channel;
		this.securityLevel = securityLevel;
		this.extendedPanId = BigInteger.ZERO;
		this.panId = panId;
		this.hostPid = hostPid;
	}

	public void start() throws Exception {
		synchronized (this) {
			if (state != STOPPED) {
				throw new IllegalStateException("ZigBeeHost is not stopped.");
			}

			this.internalStart();
		}
	}

	public void stop() throws Exception {
		synchronized (this) {
			if (state != STARTED) {
				throw new IllegalStateException("ZigBeeHost is not started.");
			}
			this.internalStop();
		}
	}

	protected void internalStart() throws Exception {

		this.nodeDescriptor.getLogicalType();

		if (this.nodeDescriptor.getLogicalType() == ZigBeeHost.COORDINATOR) {
			/*
			 * Register the ZigBeeNode services and their ZigBeeEndpoint
			 * services according to the information present in the
			 * configuration file. The ZigBeeEndpoint belonging to a ZigBeeNode
			 * are registered before their ZigBeeNode.
			 */

			for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
				ZigBeeNodeImpl node = (ZigBeeNodeImpl) iterator.next();
				node.activate(bc);
			}

			this.registerTrackers();

			this.state = STARTED;
		}
	}

	protected void internalStop() throws Exception {

		this.unregisterTrackers();

		if (this.nodeDescriptor.getLogicalType() == ZigBeeHost.COORDINATOR) {
			for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
				ZigBeeNodeImpl node = (ZigBeeNodeImpl) iterator.next();
				node.deactivate(bc);
			}
		}

		this.state = STOPPED;
	}

	public boolean isStarted() {
		synchronized (this) {
			return (state == STARTED);
		}
	}

	public void setPanId(int panId) throws IllegalStateException {
		synchronized (this) {
			if (state != STOPPED) {
				throw new IllegalStateException("PAN ID can be modified only if the ZigBeeHost is stopped.");
			}

			if (panId < 0 || panId > 0xffff) {
				throw new IllegalArgumentException("Invalid value for the panId argument. It must be in the range [0, 0xffff]");
			}

			this.panId = panId;
		}
	}

	public int getPanId() {
		synchronized (this) {
			return panId;
		}
	}

	public String getHostPid() {
		synchronized (this) {
			return hostPid;
		}
	}

	public BigInteger getExtendedPanId() {
		synchronized (this) {
			return extendedPanId;
		}
	}

	public void setExtendedPanId(BigInteger extendedPanId) {
		synchronized (this) {
			if (state != STOPPED) {
				throw new IllegalStateException("pan ID cannot be set if the ZigBeeHost is already started.");
			}
			this.extendedPanId = extendedPanId;
		}
	}

	public String getPreconfiguredLinkKey() throws Exception {
		synchronized (this) {
			return preconfiguredLinkKey;
		}
	}

	public Promise refreshNetwork() throws Exception {
		synchronized (this) {
			if (state != STARTED) {
				Promises.failed(new IllegalStateException("host is not started"));
			}

			return Promises.resolved(Boolean.TRUE);
		}
	}

	public void permitJoin(short duration) throws Exception {
		synchronized (this) {
			if (duration < 0 || duration > 0xff) {
				throw new IllegalArgumentException("duration cannot be a negative number");
			}

			if (state != STARTED) {
				Promises.failed(new IllegalStateException("host is not started"));
			}
			// empty implementation
		}
	}

	public int getChannelMask() throws Exception {
		synchronized (this) {
			return channelMask;
		}
	}

	public int getChannel() {
		synchronized (this) {
			return currentChannel;
		}
	}

	public int getSecurityLevel() {
		synchronized (this) {
			return securityLevel;
		}
	}

	public void setLogicalType(short logicalNodeType) throws Exception {
		synchronized (this) {
			if (state != STOPPED) {
				Promises.failed(new IllegalStateException("host is not started"));
			}
			if (logicalNodeType != ZigBeeNode.COORDINATOR) {
				throw new Exception("this RI can be configured only as a ZigBee Coordinator Device");
			}
		}
	}

	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		synchronized (this) {
			this.channelMask = mask;
		}
	}

	public void createGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("This method is not implemented because cannot be tested by the CT.");
	}

	public void removeGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("This method is not implemented because cannot be tested by the CT.");
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {

		ZCLCommandResponseStreamImpl stream = new ZCLCommandResponseStreamImpl();

		/*
		 * Stub out the response by immediately filling it with an Unsupported
		 * Operation Exception and ending it
		 */

		stream.handleResponse(new ZCLCommandResponseImpl(Promises.failed(new UnsupportedOperationException("This method is not implemented because cannot be tested by the CT."))));
		stream.handleResponse(new EndResponse());
		return stream;
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame,
			String exportedServicePID) {
		ZCLCommandResponseStreamImpl stream = new ZCLCommandResponseStreamImpl();

		/*
		 * Stub out the response by immediately filling it with an Unsupported
		 * Operation Exception and ending it
		 */

		stream.handleResponse(new ZCLCommandResponseImpl(Promises.failed(new UnsupportedOperationException("Not yet implemented"))));
		stream.handleResponse(new EndResponse());
		return stream;
	}

	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException {
		synchronized (this) {
			if (state != STARTED) {
				throw new IllegalStateException("ZigBeeHost is not started.");
			}
			this.currentChannel = channel & 0xff;
		}
	}

	public short getBroadcastRadius() {
		synchronized (this) {
			return broadcastRadius;
		}
	}

	public void setBroadcastRadius(short broadcastRadius) throws IllegalArgumentException, IllegalStateException {
		synchronized (this) {

			if (state != STOPPED) {
				throw new IllegalStateException("BroadcastRadius can be modified only if the ZigBeeHost is stopped");
			}

			if (broadcastRadius < 0 || broadcastRadius > 0xff) {
				throw new IllegalArgumentException("Invalid value for the broadcastRadius argument. It must be in the range [0, 0xff]");
			}
			this.broadcastRadius = broadcastRadius;
		}
	}

	public void setCommunicationTimeout(long timeout) {
		synchronized (this) {

			if (timeout <= 0) {
				throw new IllegalArgumentException("timeout must be a positive number.");
			}

			this.communicationTimeout = timeout;
		}
	}

	public long getCommunicationTimeout() {
		synchronized (this) {
			return communicationTimeout;
		}
	}

	protected void setContext(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Add a node to this host object.
	 * 
	 * @param node The node to add.
	 */
	public void add(ZigBeeNodeImpl node) {
		nodes.add(node);
	}

	/**
	 * This method must be called to kick the ZigBeeHost service.
	 * 
	 * @param wasStarted Pass true if we come from a situation where the
	 *        ZigBeeHost was in the STARTED state and the base driver has been
	 *        restarted.
	 * @throws Exception
	 */
	public void activate(boolean wasStarted) throws Exception {

		synchronized (this) {

			this.state = STARTING;

			/*
			 * register a ZigBeeHost service with the information found in the
			 * configuration file.
			 */

			Dictionary hostProperties = new Hashtable();
			if (wasStarted) {
				hostProperties.put(ZigBeeNode.PAN_ID, new Integer(this.getPanId()));
				hostProperties.put(ZigBeeNode.EXTENDED_PAN_ID, this.getExtendedPanId());
			}

			hostProperties.put(ZigBeeNode.IEEE_ADDRESS, this.getIEEEAddress());
			hostProperties.put(Constants.SERVICE_PID, this.getHostPid());
			hostProperties.put(ZigBeeNode.LOGICAL_TYPE, new Short(nodeDescriptor.getLogicalType()));
			hostProperties.put(ZigBeeNode.MANUFACTURER_CODE, new Integer(nodeDescriptor.getManufacturerCode()));
			hostProperties.put(ZigBeeNode.RECEIVER_ON_WHEN_IDLE, new Boolean(nodeDescriptor.getMacCapabilityFlags().isReceiverOnWhenIdle()));
			hostProperties.put(ZigBeeNode.POWER_SOURCE, new Boolean(nodeDescriptor.getMacCapabilityFlags().isMainsPower()));

			hostProperties.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, ZigBeeEndpoint.DEVICE_CATEGORY);
			if (nodeDescriptor.isComplexDescriptorAvailable() && complexDescriptor != null) {
				hostProperties.put(org.osgi.service.device.Constants.DEVICE_DESCRIPTION, complexDescriptor.getModelName());
				hostProperties.put(org.osgi.service.device.Constants.DEVICE_SERIAL, complexDescriptor.getSerialNumber());
			}

			hostServiceReg = bc.registerService(ZigBeeHost.class.getName(), this, hostProperties);

			if (wasStarted) {
				this.internalStart();
			}
		}
	}

	public void deactivate() throws Exception {
		synchronized (this) {
			if (this.state == STARTED) {
				this.internalStop();
			}

			hostServiceReg.unregister();
		}
	}

	protected void registerTrackers() throws InvalidSyntaxException {
		exportedEpsTracker = new ServiceTracker(bc, bc.createFilter(EXPORTED_EPS_FILTER), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference ref) {
				ZigBeeEndpoint endpoint = (ZigBeeEndpoint) bc.getService(ref);
				boolean hasBeenExported = tryToExportEndpoint(endpoint);
				if (hasBeenExported) {
					addExportedEndpoint(endpoint);
				}
				return endpoint;
			}

			public void modifiedService(ServiceReference reference, Object service) {
				/*
				 * It should check if the service, since the service properties
				 * are changed can now be exported. In our CT we do not issue
				 * this check (modify the service properties) so we do not have
				 * to implement this method.
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

		String IEEE_ADDRESS_FILTER = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + this.getIEEEAddress() + ")";
		String ZCL_EVENT_LISTENER_CLASS_FILTER = "(objectClass=" + ZCLEventListener.class.getName() + ")";

		String eventsFilter = "(&" + ZCL_EVENT_LISTENER_CLASS_FILTER + IEEE_ADDRESS_FILTER + ")";

		eventListenersTracker = new ServiceTracker(bc, bc.createFilter(ZCL_EVENT_LISTENER_CLASS_FILTER), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference ref) {
				ZCLEventListener eventListener = (ZCLEventListener) bc.getService(ref);

				RegistratonInfo registrationInfo = new RegistratonInfo();
				registrationInfo.properties = getProperties(ref);
				registrationInfo.eventListener = eventListener;

				ZigBeeEvent event;
				try {
					event = createEvent(registrationInfo, new Boolean(true));
					registrationInfo.eventSource = new ZigBeeEventSourceImpl(registrationInfo, event);
					registrationInfo.eventSource.start();
					eventListenersMap.put(eventListener, registrationInfo);
				} catch (Throwable e) {
					Logger.e(TAG, "Exception creating event: " + e.getMessage());
				}

				return eventListener;
			}

			public void modifiedService(ServiceReference reference, Object eventListener) {

				Logger.d(TAG, "the ZCLEventListener has been updated");

				RegistratonInfo registrationInfo = (RegistratonInfo) eventListenersMap.get(eventListener);
				registrationInfo.properties = getProperties(reference);

				try {
					registrationInfo.eventSource.update();
				} catch (Throwable e) {
					Logger.e(TAG, "Exception creating event: " + e.getMessage());
				}
			}

			public void removedService(ServiceReference reference, Object eventListener) {
				// stop the eventListener

				RegistratonInfo registrationInfo = (RegistratonInfo) eventListenersMap.get(eventListener);
				try {
					registrationInfo.eventSource.stop();
				} catch (InterruptedException e) {

				}

				eventListenersMap.remove(eventListener);
			}

			private Map getProperties(ServiceReference ref) {
				Map map = new HashMap();
				String[] keys = ref.getPropertyKeys();
				for (int i = 0; i < keys.length; i++) {
					Object value = ref.getProperty(keys[i]);
					map.put(keys[i], value);
				}
				return map;
			}
		});

		/*
		 * Starts to track exported ZigBeeEndpoint services.
		 */
		eventListenersTracker.open();
	}

	protected void unregisterTrackers() {
		exportedEpsTracker.close();
		eventListenersTracker.close();
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
		if ((newEndpoint.getId() > 0xfe) || (newEndpoint.getId() < 0)) {
			newEndpoint.notExported(new ZDPException(ZDPException.INVALID_EP, "endpoint IDs must be in the range [0, 254]"));
			return false;
		}

		for (Iterator it = exportedEpsList.iterator(); it.hasNext();) {
			ZigBeeEndpoint endpoint = (ZigBeeEndpoint) it.next();
			if (endpoint.getId() == newEndpoint.getId() && endpoint.getNodeAddress().equals(newEndpoint.getNodeAddress())) {
				/*
				 * Once the notExported() method has been called once we can
				 * immediately return without iterating over the remaining
				 * already exported endpoints.
				 */
				try {
					newEndpoint.notExported(new ZigBeeException(ZigBeeException.OSGI_EXISTING_ID, "this Id already exists"));
					return false;
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
		return true;
	}

	/**
	 * Create a ZigBeeEvent object with the specified registrationInfo.
	 * 
	 * @param registrationInfo A RegistrationInfo instance.
	 * @param value The value to be notified.
	 * @return The created and filled ZigBeeEvent instance.
	 */
	protected ZigBeeEvent createEvent(RegistratonInfo registrationInfo, Object value) {
		Map map = registrationInfo.properties;

		BigInteger ieeeAddress = ParserUtils.getParameter(map, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.ZERO);
		short endpointId = ParserUtils.getParameter(map, ZigBeeEndpoint.ENDPOINT_ID, ParserUtils.MANDATORY, (short) -1);
		int clusterId = ParserUtils.getParameter(map, ZCLCluster.ID, ParserUtils.MANDATORY, -1);
		int attributeId = ParserUtils.getParameter(map, ZCLAttribute.ID, ParserUtils.MANDATORY, -1);
		short dataType = ParserUtils.getParameter(map, ZCLEventListener.ATTRIBUTE_DATA_TYPE, ParserUtils.MANDATORY, (short) -1);

		ZigBeeEvent event = new ZigBeeEventImpl(ieeeAddress, endpointId, clusterId, attributeId, dataType, value);
		return event;
	}
}
