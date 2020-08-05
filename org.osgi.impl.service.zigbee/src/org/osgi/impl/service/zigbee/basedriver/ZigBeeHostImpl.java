/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

	static final String											TAG						= ZigBeeHostImpl.class
			.getName();

	private long				communicationTimeout	= 60 * 1000;
	private String				hostPid;
	private int					currentChannel;
	private int					securityLevel;
	private int					panId;
	private BigInteger			extendedPanId;
	private int					channelMask;
	private short				broadcastRadius;

	private String				preconfiguredLinkKey	= "KEY";

	private List<ZigBeeNodeImpl>	nodes					= new ArrayList<>();

	private final static int	STOPPED					= 0;
	private final static int	STARTING				= 1;
	private final static int	STARTED					= 2;
	@SuppressWarnings("unused")
	private final static int	STOPPING				= 3;

	private int					state					= STOPPED;

	private ServiceRegistration<ZigBeeHost>	hostServiceReg			= null;

	BundleContext											bc;

	private List<ZigBeeEndpoint>			exportedEpsList			= new ArrayList<>();

	private static final String	EXPORTED_EPS_FILTER		= "(&(objectClass=" + ZigBeeEndpoint.class.getName() + ")(" + ZigBeeEndpoint.ZIGBEE_EXPORT + "=*))";

	private ServiceTracker<ZigBeeEndpoint,ZigBeeEndpoint>	exportedEpsTracker		= null;

	/**
	 * ServiceTracker for ZCLEventListener objects.
	 */
	private ServiceTracker<ZCLEventListener,ZCLEventListener>	eventListenersTracker	= null;

	protected Map<ZCLEventListener,RegistratonInfo>				eventListenersMap		= new HashMap<>();

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

	@Override
	public void start() throws Exception {
		synchronized (this) {
			if (state != STOPPED) {
				throw new IllegalStateException("ZigBeeHost is not stopped.");
			}

			this.internalStart();
		}
	}

	@Override
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

		if (this.nodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
			/*
			 * Register the ZigBeeNode services and their ZigBeeEndpoint
			 * services according to the information present in the
			 * configuration file. The ZigBeeEndpoint belonging to a ZigBeeNode
			 * are registered before their ZigBeeNode.
			 */

			for (Iterator<ZigBeeNodeImpl> iterator = nodes.iterator(); iterator
					.hasNext();) {
				ZigBeeNodeImpl node = iterator.next();
				node.activate(bc);
			}

			this.registerTrackers();

			this.state = STARTED;
		}
	}

	protected void internalStop() throws Exception {

		this.unregisterTrackers();

		if (this.nodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
			for (Iterator<ZigBeeNodeImpl> iterator = nodes.iterator(); iterator
					.hasNext();) {
				ZigBeeNodeImpl node = iterator.next();
				node.deactivate(bc);
			}
		}

		this.state = STOPPED;
	}

	@Override
	public boolean isStarted() {
		synchronized (this) {
			return (state == STARTED);
		}
	}

	@Override
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

	@Override
	public int getPanId() {
		synchronized (this) {
			return panId;
		}
	}

	@Override
	public String getHostPid() {
		synchronized (this) {
			return hostPid;
		}
	}

	@Override
	public BigInteger getExtendedPanId() {
		synchronized (this) {
			return extendedPanId;
		}
	}

	@Override
	public void setExtendedPanId(BigInteger extendedPanId) {
		synchronized (this) {
			if (state != STOPPED) {
				throw new IllegalStateException("pan ID cannot be set if the ZigBeeHost is already started.");
			}
			this.extendedPanId = extendedPanId;
		}
	}

	@Override
	public String getPreconfiguredLinkKey() throws Exception {
		synchronized (this) {
			return preconfiguredLinkKey;
		}
	}

	@Override
	public Promise<Boolean> refreshNetwork() throws Exception {
		synchronized (this) {
			if (state != STARTED) {
				Promises.failed(new IllegalStateException("host is not started"));
			}

			return Promises.resolved(Boolean.TRUE);
		}
	}

	@Override
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

	@Override
	public int getChannelMask() throws Exception {
		synchronized (this) {
			return channelMask;
		}
	}

	@Override
	public int getChannel() {
		synchronized (this) {
			return currentChannel;
		}
	}

	@Override
	public int getSecurityLevel() {
		synchronized (this) {
			return securityLevel;
		}
	}

	@Override
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

	@Override
	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		synchronized (this) {
			this.channelMask = mask;
		}
	}

	@Override
	public void createGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("This method is not implemented because cannot be tested by the CT.");
	}

	public void removeGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("This method is not implemented because cannot be tested by the CT.");
	}

	@Override
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

	@Override
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

	@Override
	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException {
		synchronized (this) {
			if (state != STARTED) {
				throw new IllegalStateException("ZigBeeHost is not started.");
			}
			this.currentChannel = channel & 0xff;
		}
	}

	@Override
	public short getBroadcastRadius() {
		synchronized (this) {
			return broadcastRadius;
		}
	}

	@Override
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

	@Override
	public void setCommunicationTimeout(long timeout) {
		synchronized (this) {

			if (timeout <= 0) {
				throw new IllegalArgumentException("timeout must be a positive number.");
			}

			this.communicationTimeout = timeout;
		}
	}

	@Override
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

			Dictionary<String,Object> hostProperties = new Hashtable<>();
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

			hostServiceReg = bc.registerService(ZigBeeHost.class, this,
					hostProperties);

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
		exportedEpsTracker = new ServiceTracker<>(
				bc, bc.createFilter(EXPORTED_EPS_FILTER),
				new ServiceTrackerCustomizer<ZigBeeEndpoint,ZigBeeEndpoint>() {
					@Override
					public ZigBeeEndpoint addingService(
							ServiceReference<ZigBeeEndpoint> ref) {
						ZigBeeEndpoint endpoint = bc.getService(ref);
						boolean hasBeenExported = tryToExportEndpoint(endpoint);
						if (hasBeenExported) {
							addExportedEndpoint(endpoint);
						}
						return endpoint;
					}

					@Override
					public void modifiedService(
							ServiceReference<ZigBeeEndpoint> reference,
							ZigBeeEndpoint service) {
						/*
						 * It should check if the service, since the service
						 * properties are changed can now be exported. In our CT
						 * we do not issue this check (modify the service
						 * properties) so we do not have to implement this
						 * method.
						 */
					}

					@Override
					public void removedService(
							ServiceReference<ZigBeeEndpoint> reference,
							ZigBeeEndpoint service) {
						removeExportedEndpoint(service);
					}
				});

		/*
		 * Starts to track exported ZigBeeEndpoint services.
		 */
		exportedEpsTracker.open();

		String IEEE_ADDRESS_FILTER = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + this.getIEEEAddress() + ")";
		String ZCL_EVENT_LISTENER_CLASS_FILTER = "(objectClass=" + ZCLEventListener.class.getName() + ")";

		@SuppressWarnings("unused")
		String eventsFilter = "(&" + ZCL_EVENT_LISTENER_CLASS_FILTER + IEEE_ADDRESS_FILTER + ")";

		eventListenersTracker = new ServiceTracker<>(bc,
				bc.createFilter(ZCL_EVENT_LISTENER_CLASS_FILTER),
				new ServiceTrackerCustomizer<ZCLEventListener,ZCLEventListener>() {
					@Override
					public ZCLEventListener addingService(
							ServiceReference<ZCLEventListener> ref) {
						ZCLEventListener eventListener = bc.getService(ref);

						RegistratonInfo registrationInfo = new RegistratonInfo();
						registrationInfo.properties = getProperties(ref);
						registrationInfo.eventListener = eventListener;

						ZigBeeEvent event;
						try {
							event = createEvent(registrationInfo,
									new Boolean(true));
							registrationInfo.eventSource = new ZigBeeEventSourceImpl(
									registrationInfo, event);
							registrationInfo.eventSource.start();
							eventListenersMap.put(eventListener,
									registrationInfo);
						} catch (Throwable e) {
							Logger.e(TAG, "Exception creating event: "
									+ e.getMessage());
						}

						return eventListener;
					}

					@Override
					public void modifiedService(
							ServiceReference<ZCLEventListener> reference,
							ZCLEventListener eventListener) {

						Logger.d(TAG, "the ZCLEventListener has been updated");

						RegistratonInfo registrationInfo = eventListenersMap
								.get(eventListener);
						registrationInfo.properties = getProperties(reference);

						try {
							registrationInfo.eventSource.update();
						} catch (Throwable e) {
							Logger.e(TAG, "Exception creating event: "
									+ e.getMessage());
						}
					}

					@Override
					public void removedService(
							ServiceReference<ZCLEventListener> reference,
							ZCLEventListener eventListener) {
						// stop the eventListener

						RegistratonInfo registrationInfo = eventListenersMap
								.get(eventListener);
						try {
							registrationInfo.eventSource.stop();
						} catch (InterruptedException e) {
							// ignore
						}

						eventListenersMap.remove(eventListener);
					}

					private Map<String,Object> getProperties(
							ServiceReference<ZCLEventListener> ref) {
						Map<String,Object> map = new HashMap<>();
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

		for (Iterator<ZigBeeEndpoint> it = exportedEpsList.iterator(); it
				.hasNext();) {
			ZigBeeEndpoint endpoint = it.next();
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
		Map<String, ? > map = registrationInfo.properties;

		BigInteger ieeeAddress = ParserUtils.getParameter(map, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.ZERO);
		short endpointId = ParserUtils.getParameter(map, ZigBeeEndpoint.ENDPOINT_ID, ParserUtils.MANDATORY, (short) -1);
		int clusterId = ParserUtils.getParameter(map, ZCLCluster.ID, ParserUtils.MANDATORY, -1);
		int attributeId = ParserUtils.getParameter(map, ZCLAttribute.ID, ParserUtils.MANDATORY, -1);
		short dataType = ParserUtils.getParameter(map, ZCLEventListener.ATTRIBUTE_DATA_TYPE, ParserUtils.MANDATORY, (short) -1);

		ZigBeeEvent event = new ZigBeeEventImpl(ieeeAddress, endpointId, clusterId, attributeId, dataType, value);
		return event;
	}
}
