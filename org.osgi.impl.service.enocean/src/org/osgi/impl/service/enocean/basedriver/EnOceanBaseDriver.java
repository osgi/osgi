
package org.osgi.impl.service.enocean.basedriver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
// import javax.comm.CommPortIdentifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanDeviceImpl;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanHostImpl;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanHostTestImpl;
import org.osgi.impl.service.enocean.basedriver.radio.Message;
import org.osgi.impl.service.enocean.basedriver.radio.Message4BS;
import org.osgi.impl.service.enocean.basedriver.radio.MessageRPS;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * EnOcean base driver's main class.
 */
public class EnOceanBaseDriver implements EnOceanPacketListener, ServiceTrackerCustomizer, EventHandler {

	private BundleContext		bc;
	private Hashtable			eoDevices;
	private Hashtable			eoHostRefs;

	private ServiceTracker		eoDevicesTracker;
	private EventAdmin			eventAdmin;
	private ServiceRegistration	eventHandlerRegistration;
	private EnOceanHostImpl		host;

	/**
	 * EnOcean base driver's tag/prefix for logger.
	 */
	public static final String	TAG							= "EnOceanBaseDriver";

	/**
	 * EnOcean base driver's key for the config exported PID table.
	 */
	public static final String	CONFIG_EXPORTED_PID_TABLE	= "org.enocean.ExportedDeviceTable";

	/**
	 * The {@link EnOceanBaseDriver} constructor initiates the connection
	 * towards an {@link EnOceanHostImpl} device. Then it registers itself as a
	 * service listener for any {@link EnOceanDevice},
	 * {@link EnOceanMessageDescriptionSet},
	 * {@link EnOceanChannelDescriptionSet} that would be registered in the
	 * framework.
	 * 
	 * @param bundleContext
	 */
	public EnOceanBaseDriver(BundleContext bundleContext) {
		/* Init driver internal state */
		this.bc = bundleContext;
		eoDevices = new Hashtable(10);
		eoHostRefs = new Hashtable(10);

		/* Register initial EnOceanHost */
		String hostPath = System.getProperty("org.osgi.service.enocean.host.path", "/dev/ttyUSB0");
		System.out.println("initial host path : " + hostPath);
		if (hostPath != null && hostPath != "") {
			if (hostPath.equals(":testcase:")) {
				System.out.println("Create, and register EnOceanHostTestImpl.");
				host = new EnOceanHostTestImpl(hostPath, this.bc);
			}
			registerHost(hostPath, host);
			host.addPacketListener(this);
		}

		/* Initialize EventAdmin */
		ServiceReference ref = this.bc.getServiceReference(EventAdmin.class.getName());
		if (ref != null) {
			eventAdmin = (EventAdmin) this.bc.getService(ref);
		}

		/* Initializes self as EventHandler */
		Hashtable ht = new Hashtable();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, new String[] {
				EnOceanEvent.TOPIC_MSG_RECEIVED,
		});
		eventHandlerRegistration = this.bc.registerService(EventHandler.class.getName(), this, ht);
		Logger.d(TAG, "EnOcean base driver's eventHandler (as a ServiceRegistration), eventHandlerRegistration: " + eventHandlerRegistration);

		/* Track the EnOcean services */
		try {
			Logger.d(TAG, "Track the EnOcean services, eoDevicesTracker: " + eoDevicesTracker);
			eoDevicesTracker = registerDeviceListener(this.bc, this);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
		}
	}

	/**
	 * This callback gets called every time a message has been correctly parsed
	 * by one of the hosts.
	 * 
	 * @param data
	 * 
	 * @see org.osgi.impl.service.enocean.basedriver.EnOceanPacketListener#radioPacketReceived(byte[])
	 */
	public void radioPacketReceived(byte[] data) {
		Logger.d(TAG, "radioPacketReceived(data: " + data + ")");
		Logger.d(TAG, "radioPacketReceived - data[0]: " + data[0] + ")");
		Message msg;

		/* First, determine if teach-in and eventually create a device */
		switch (data[0]) {
			case Message.MESSAGE_4BS :
				msg = new Message4BS(data);
				Logger.d(TAG, "4BS msg received, payload: " + Utils.bytesToHexString(msg.getPayloadBytes()));
				if (msg.isTeachin()) {
					EnOceanDevice dev = getAssociatedDevice(msg);
					if (dev == null) {
						if (msg.hasTeachInInfo()) {
							registerDeviceAndProfile(msg.getSenderId(), msg.getRorg(), msg.teachInFunc(), msg.teachInType(), msg.teachInManuf());
						} else {
							new EnOceanDeviceImpl(bc, this, msg.getSenderId(), msg.getRorg());
						}
					} else {
						Logger.d(TAG, "message was a teach-in, but device already exists.");
					}
					return; // No need to do more processing on the message
				}
				break;
			case Message.MESSAGE_RPS :
				msg = new MessageRPS(data);
				EnOceanDevice dev = getAssociatedDevice(msg);
				if (dev == null) {
					dev = new EnOceanDeviceImpl(bc, this, msg.getSenderId(), msg.getRorg());
				}
				break;
			default :
				return;
		}

		/* Try to associate the message with a device and send to EventAdmin */
		EnOceanDevice dev = getAssociatedDevice(msg);
		if (dev != null && dev instanceof EnOceanDeviceImpl) {
			EnOceanDeviceImpl implDev = (EnOceanDeviceImpl) dev;
			int rorg = implDev.getRorg();
			int func = implDev.getFunc();
			int type = implDev.getType();

			Logger.d(TAG, "rorg: " + rorg + ", func: " + func + ", type: " + type);

			msg.setFunc(func);
			msg.setType(type);
			implDev.setLastMessage(msg);
			broadcastToEventAdmin(msg);
		}
	}

	public Object addingService(ServiceReference ref) {
		Object service = this.bc.getService(ref);
		if (service == null) {
			return null;
		} else {
			if (service instanceof EnOceanDevice) {
				String servicePid = (String) ref.getProperty(Constants.SERVICE_PID);
				String chipId = (String) ref.getProperty(EnOceanDevice.CHIP_ID);
				Object hasExport = ref.getProperty(EnOceanDevice.ENOCEAN_EXPORT);
				if (servicePid == null) {
					if (chipId == null) {
						throw new IllegalStateException();
					}
					servicePid = chipId;
				}

				try {
					if (chipId == null && hasExport != null) {
						host.allocChipID(servicePid);
					}
				} catch (Exception e) {
					System.out.println("exception: " + e.getMessage());
				}
				eoDevices.put(servicePid, bc.getService(ref));
				Logger.d(TAG, "servicetracker: EnOceanDevice service registered, PID: " + servicePid);
			}
			return service;
		}
	}

	public void modifiedService(ServiceReference arg0, Object service) {
		// TODO
	}

	public void removedService(ServiceReference arg0, Object service) {
		// TODO
	}

	public void handleEvent(Event event) {
		if (event.getTopic().equals(EnOceanEvent.TOPIC_MSG_RECEIVED)) {
			if (event.getProperty(EnOceanEvent.PROPERTY_EXPORTED) != null) {
				EnOceanMessage msg = (EnOceanMessage) event.getProperty(EnOceanEvent.PROPERTY_MESSAGE);
				if (msg != null) {
					EspPacket pkt = new EspPacket(msg);
					System.out.println("Writing packet : " + Utils.bytesToHexString(pkt.serialize()));
					host.send(pkt.serialize());
				}
			}
		}
	}

	/**
	 * Start the base driver.
	 */
	public void start() {
		try {
			host.startup();
		} catch (EnOceanHostImplException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the base driver.
	 */
	public void stop() {
		unregisterDevices();
	}

	/**
	 * Register a host.
	 * 
	 * @param hostPath
	 * @param enOceanHost
	 * @return the corresponding ServiceRegistration object.
	 */
	public ServiceRegistration registerHost(String hostPath, EnOceanHost enOceanHost) {
		ServiceRegistration sr = null;
		try {
			Properties props = new Properties();
			props.put(EnOceanHost.HOST_ID, hostPath);
			sr = bc.registerService(EnOceanHost.class.getName(), enOceanHost, props);
			eoHostRefs.put(hostPath, sr);
		} catch (Exception e) {
			Logger.e(TAG, "exception when registering host : " + e.getMessage());
		}
		return sr;
	}

	/**
	 * Send data.
	 * 
	 * @param data
	 */
	public void send(byte[] data) {
		host.send(data);
	}

	private void unregisterDevices() {
		Iterator it = eoDevices.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue() instanceof EnOceanDeviceImpl) {
				EnOceanDeviceImpl dev = (EnOceanDeviceImpl) entry.getValue();
				Logger.d(TAG, "unregistering device : " + Utils.bytesToHexString(Utils.intTo4Bytes(dev.getChipId())));
				dev.remove();
			}
			it.remove();
		}
	}

	private void broadcastToEventAdmin(EnOceanMessage eoMsg) {
		if (eventAdmin != null) {
			Map properties = new Hashtable();
			properties.put(EnOceanDevice.CHIP_ID, String.valueOf(eoMsg.getSenderId()));
			properties.put(EnOceanDevice.RORG, String.valueOf(eoMsg.getRorg()));
			properties.put(EnOceanDevice.FUNC, String.valueOf(eoMsg.getFunc()));
			properties.put(EnOceanDevice.TYPE, String.valueOf(eoMsg.getType()));
			properties.put(EnOceanEvent.PROPERTY_MESSAGE, eoMsg);

			Event event = new Event(EnOceanEvent.TOPIC_MSG_RECEIVED, properties);
			eventAdmin.sendEvent(event);
		}
	}

	/**
	 * Internal method to retrieve the service reference associated to a
	 * message's SENDER_ID.
	 * 
	 * @param msg
	 * @return the EnOceanDevice service, or null.
	 */
	private EnOceanDevice getAssociatedDevice(Message msg) {
		String strSenderId = String.valueOf(msg.getSenderId());
		String filter = "(&(objectClass=" + EnOceanDevice.class.getName() + ")(" + EnOceanDevice.CHIP_ID + "=" + strSenderId + "))";
		ServiceReference[] ref = null;
		try {
			ref = bc.getServiceReferences(null, filter);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, "Invalid syntax in device search : " + e.getMessage());
		}
		if (ref != null && ref.length == 1) {
			return (EnOceanDevice) bc.getService(ref[0]);
		}
		return null;
	}

	private void registerDeviceAndProfile(int senderId, int rorg, int func, int type, int manuf) {
		EnOceanDeviceImpl device = new EnOceanDeviceImpl(bc, this, senderId, rorg);
		device.registerProfile(func, type, manuf);
	}

	/*
	 * The functions that come below are used to register the necessary
	 * services.
	 */

	private ServiceTracker registerServiceFrom(BundleContext bundleContext, Class objectClass, ServiceTrackerCustomizer listener) {
		String filter = "(objectClass=" + (objectClass).getName() + ')';
		Filter deviceAdminFilter;
		try {
			deviceAdminFilter = bundleContext.createFilter(filter);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
			return null;
		}
		ServiceTracker serviceTracker = new ServiceTracker(bundleContext, deviceAdminFilter, listener);
		serviceTracker.open();
		return serviceTracker;
	}

	private ServiceTracker registerDeviceListener(BundleContext bundleContext, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		return registerServiceFrom(bundleContext, org.osgi.service.enocean.EnOceanDevice.class, listener);
	}

}
