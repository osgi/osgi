package org.osgi.impl.service.enocean.basedriver;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.osgi.impl.service.enocean.basedriver.radio.Message;
import org.osgi.impl.service.enocean.basedriver.radio.Message4BS;
import org.osgi.impl.service.enocean.basedriver.radio.MessageRPS;
import org.osgi.impl.service.enocean.utils.EnOceanDriverException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanRPCDescriptionSet;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class EnOceanBaseDriver implements EnOceanPacketListener, ServiceTrackerCustomizer, EventHandler {

	private BundleContext		bc;
	private Hashtable			eoDevices;
	private Hashtable			eoHostRefs;

	private ServiceTracker		eoDevicesTracker;
	private EnOceanHostImpl		initialHost;
	private EventAdmin			eventAdmin;
	private ServiceRegistration	eventHandlerRegistration;

	public static final String	TAG	= "EnOceanBaseDriver";

	public static final String	CONFIG_EXPORTED_PID_TABLE	= "org.enocean.ExportedDeviceTable";
	public static final String	TOPIC_REMOVE_DEVICE			= "org/osgi/impl/service/enocean/EnOceanBaseDriver/REMOVE_DEVICE";
	public static final String	TOPIC_CLEAN_DEVICES			= "org/osgi/impl/service/enocean/EnOceanBaseDriver/CLEAN_DEVICES";

	/**
	 * The {@link EnOceanBaseDriver} constructor initiates the connection
	 * towards an {@link EnOceanHostImpl} device. Then it registers itself as
	 * a service listener for any {@link EnOceanDevice},
	 * {@link EnOceanMessageSet}, {@link EnOceanRPCDescriptionSet},
	 * {@link EnOceanChannelDescriptionSet} that would be registered in the
	 * framework.
	 * 
	 */
	public EnOceanBaseDriver(BundleContext bc) {
		/* Init driver internal state */
		this.bc = bc;
		eoDevices = new Hashtable(10);
		eoHostRefs = new Hashtable(10);

		/* Register initial EnOceanHost */
		String hostPath = System.getProperty("org.osgi.service.enocean.host.path");
		int baseId = Integer.valueOf(System.getProperty("org.osgi.service.enocean.host.base_id")).intValue();
		int chipId = Integer.valueOf(System.getProperty("org.osgi.service.enocean.host.chip_id")).intValue();
		if (hostPath != null && hostPath != "") {
			try {
				initialHost = new EnOceanHostImpl(chipId, baseId, hostPath, bc);
				registerHost(hostPath, initialHost);
				initialHost.addPacketListener(this);
			} catch (EnOceanDriverException e) {
				Logger.e(TAG, "initial enoceanhost registration failed : " + e.getMessage());
			} catch (FileNotFoundException e) {
				Logger.e(TAG, "initial enoceanhost path was incorrect : " + e.getMessage());
			} catch (IOException e) {
				Logger.e(TAG, "initial enoceanhost access to persisted data failed : " + e.getMessage());
			}
		}

		/* Initialize EventAdmin */
		ServiceReference ref = bc.getServiceReference(EventAdmin.class.getName());
		if (ref != null) {
			eventAdmin = (EventAdmin) bc.getService(ref);
		}

		/* Initializes self as EventHandler */
		Hashtable ht = new Hashtable();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, new String[] {
				EnOceanEvent.TOPIC_MSG_RECEIVED,
				EnOceanBaseDriver.TOPIC_REMOVE_DEVICE,
				EnOceanBaseDriver.TOPIC_CLEAN_DEVICES,
		});
		eventHandlerRegistration = bc.registerService(EventHandler.class.getName(), this, ht);

		/* Track the EnOcean services */
		try {
			eoDevicesTracker = registerDeviceListener(bc, this);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
		}
	}

	/**
	 * This callback gets called every time a message has been correctly parsed
	 * by one of the hosts.
	 * 
	 * @param msg
	 */
	public void radioPacketReceived(byte[] data) {

		Message msg;

		/* First, determine if teach-in and eventually create a device */
		switch (data[0]) {
			case Message.MESSAGE_4BS :
				msg = new Message4BS(data);
				if (msg.isTeachin() && msg.hasTeachInInfo()) {
					registerDevice(msg.getSenderId(), msg.getRorg(), msg.teachInFunc(), msg.teachInType(), msg.teachInManuf());
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
			/* If we have full profile information */
			if (rorg != -1 && func != -1 && type != -1) {
				msg.setFunc(func);
				msg.setType(type);
				implDev.setLastMessage(msg);
				broadcastToEventAdmin(msg);
			}
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
						initialHost.generateChipID(servicePid);
					}					
				} catch (Exception e) {
					System.out.println("There has been an exception");
				}
				eoDevices.put(servicePid, bc.getService(ref));
				Logger.d(TAG, "EnOceanDevice service registered : " + servicePid);
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
				if(msg != null) {
					EspPacket pkt = new EspPacket(msg);
					initialHost.send(pkt.serialize());
				}
			}
		}
		if (event.getTopic().equals(EnOceanBaseDriver.TOPIC_REMOVE_DEVICE)) {
			// Implement
		}
		if (event.getTopic().equals(EnOceanBaseDriver.TOPIC_CLEAN_DEVICES)) {
			unregisterDevices();
		}
	}

	public void start() {
		initialHost.start();
	}

	public void stop() {
		unregisterDevices();
	}

	public ServiceRegistration registerHost(String hostPath, EnOceanHost host) throws EnOceanDriverException {
		ServiceRegistration sr = null;
		if (host == null) {
			throw new EnOceanDriverException("the specified host was null");
		}
		try {
			Properties props = new Properties();
			props.put(EnOceanHost.HOST_ID, hostPath);
			sr = bc.registerService(EnOceanHost.class.getName(), host, props);
			eoHostRefs.put(hostPath, sr);
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		return sr;
	}

	public void send(byte[] data) {
		initialHost.send(data);
	}

	private void unregisterDevices() {
		Iterator it = eoDevices.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue() instanceof EnOceanDeviceImpl) {
				EnOceanDeviceImpl dev = (EnOceanDeviceImpl) entry.getValue();
				System.out.println("Unregistering device : " + dev.getRorg());
				dev.unregister();
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

	private void registerDevice(int senderId, int rorg, int func, int type, int manuf) {
		EnOceanDeviceImpl device = new EnOceanDeviceImpl(bc, this, senderId, rorg);
		device.registerProfile(func, type, manuf);
	}

	/* The functions that come below are used to register the necessary services */
	private ServiceTracker registerServiceFrom(BundleContext bc, Class objectClass, ServiceTrackerCustomizer listener) {
		String filter = "(objectClass=" + (objectClass).getName() + ')';
		Filter deviceAdminFilter;
		try {
			deviceAdminFilter = bc.createFilter(filter);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
			return null;
		}
		ServiceTracker serviceTracker = new ServiceTracker(bc, deviceAdminFilter, listener);
		serviceTracker.open();
		return serviceTracker;
	}

	private ServiceTracker registerDeviceListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		return registerServiceFrom(bc, org.osgi.service.enocean.EnOceanDevice.class, listener);
	}

}
