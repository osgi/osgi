package org.osgi.impl.service.enocean.basedriver;

import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Properties;
// import javax.comm.CommPortIdentifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanDeviceImpl;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanHostImpl;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanMessageSetImpl;
import org.osgi.impl.service.enocean.basedriver.impl.EnOceanMessage_A5_02_01;
import org.osgi.impl.service.enocean.basedriver.radio.Message;
import org.osgi.impl.service.enocean.basedriver.radio.Message4BS;
import org.osgi.impl.service.enocean.utils.EnOceanDriverException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.sets.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.sets.EnOceanMessageSet;
import org.osgi.service.enocean.sets.EnOceanRPCSet;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class EnOceanBaseDriver implements EnOceanPacketListener, ServiceTrackerCustomizer {

	private Hashtable		devices;
	private Hashtable		servicerefs;
	private BundleContext	bc;

	// Close every service properly upon base driver stop.
	private ServiceTracker		deviceServiceRef;
	private ServiceTracker		messageSetServiceRef;
	private ServiceTracker		rpcSetServiceRef;
	private ServiceTracker		channelDescriptionSetServiceRef;
	private EnOceanHostImpl		initialHost;
	private EnOceanMessageSetImpl	internalMessageSet;

	public static final String		TAG	= "EnOceanBaseDriver";

	/**
	 * The {@link EnOceanBaseDriver} constructor initiates the connection
	 * towards an {@link EnOceanHostImpl} device. Then it registers itself as
	 * a service listener for any {@link EnOceanDevice},
	 * {@link EnOceanMessageSet}, {@link EnOceanRPCSet},
	 * {@link EnOceanChannelDescriptionSet} that would be registered in the
	 * framework.
	 * 
	 */
	public EnOceanBaseDriver(BundleContext bc) {
		/* Init driver internal state */
		this.bc = bc;
		devices = new Hashtable(10);
		servicerefs = new Hashtable(10);

		/* Init internal messageSet */
		internalMessageSet = new EnOceanMessageSetImpl();
		internalMessageSet.putMessageClass(0xA5, 02, 01, EnOceanMessage_A5_02_01.class);

		/* Register initial EnOceanHost */
		String hostPath = System.getProperty("org.osgi.service.enocean.host.path");
		if (hostPath != null && hostPath != "") {
			try {
				initialHost = new EnOceanHostImpl(hostPath);
				registerHost(hostPath, initialHost);
				initialHost.addPacketListener(this);
			} catch (EnOceanDriverException e) {
				Logger.e(TAG, "initial enoceanhost registration failed : " + e.getMessage());
			} catch (FileNotFoundException e) {
				Logger.e(TAG, "initial enoceanhost path was incorrect : " + e.getMessage());
			}
		}

		/* Track the EnOcean services */
		try {
			deviceServiceRef = registerDeviceListener(bc, this);
			rpcSetServiceRef = registerRpcSetListener(bc, this);
			messageSetServiceRef = registerMessageSetListener(bc, this);
			channelDescriptionSetServiceRef = registerChannelDescriptionSetListener(bc, this);
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
	public void radioPacketReceived(Message msg) {

		/* First, determine if teach-in and eventually create a device */
		switch (msg.getRORG()) {
			case Message.MESSAGE_4BS :
				Message4BS msg_4BS = new Message4BS(msg) {};
				if (msg_4BS.isTeachin()) {
					register4BSDeviceFromTeachIn(msg_4BS);
					return; // No need to do more processing on the message
				}
				break;
			default :
				// TODO: implement other message types
				break;
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
				EnOceanMessage eoMsg = radioToEnOceanMessage(msg, rorg, func, type);
				implDev.setLastMessage(eoMsg);
			}
		}

	}

	public Object addingService(ServiceReference ref) {
		Object service = this.bc.getService(ref);
		if (service == null) {
			return null;
		} else {
			if (service instanceof EnOceanDevice) {
				// TODO: Do something when a device is added
				Logger.d(TAG, "a new EnOceanDevice service has been registered");
			}
			if (service instanceof EnOceanMessageSet) {
				// TODO: Do something when a message is created
				Logger.d(TAG, "a new EnOceanMessage service has been registered");
			}
			if (service instanceof EnOceanRPCSet) {
				// TODO: Do something when a message is created
				Logger.d(TAG, "a new EnOceanRPC service has been registered");
			}
			if (service instanceof EnOceanChannelDescriptionSet) {
				// TODO: Do something when a message is created
				Logger.d(TAG, "a new EnOceanChannelDescriptionSet service has been registered");
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

	public void start() {
		initialHost.start();
	}

	public void stop() {
	}

	public ServiceRegistration registerHost(String hostPath, EnOceanHost host) throws EnOceanDriverException {
		ServiceRegistration sr = null;
		if (host == null) {
			throw new EnOceanDriverException("the specified host was null");
		}
		try {
			Properties props = new Properties();
			props.put(EnOceanHost.HOST_ID, hostPath);
			sr = bc.registerService("org.osgi.service.enocean.EnOceanHost",
					host, props);
			servicerefs.put(hostPath, sr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return sr;
	}

	/**
	 * Internal method to retrieve the service reference associated to a
	 * message's SENDER_ID.
	 * 
	 * @param msg
	 * @return the EnOceanDevice service, or null.
	 */
	private EnOceanDevice getAssociatedDevice(Message msg) {
		int uid = Utils.bytes2intLE(msg.getSenderId(), 0, 4);
		String strSenderId = String.valueOf(uid);
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

	/**
	 * Converts a radio message type (pure driver-specific implementation) into
	 * a proper specification-based EnOcean Message.
	 * 
	 * @param msg the raw input Message.
	 * @param rorg
	 * @param func
	 * @param type
	 * @return
	 */
	private EnOceanMessage radioToEnOceanMessage(Message msg, int rorg, int func, int type) {
		/* First check in the internal message class table */
		Class msgClass = internalMessageSet.getMessage(rorg, func, type, -1);
		if (msgClass != null) {
			EnOceanMessage oeMsg;
			try {
				oeMsg = (EnOceanMessage) msgClass.newInstance();
				oeMsg.deserialize(msg.getData());
				return oeMsg;
			} catch (IllegalAccessException e) {
				Logger.e(TAG, "Illegal access : " + e.getMessage());
			} catch (InstantiationException e) {
				Logger.e(TAG, "Instanciation exception : " + e.getMessage());
			} catch (EnOceanException e) {
				Logger.e(TAG, "Deserialization exception : " + e.getMessage());
			}
		}
		return null;
	}

	private void register4BSDeviceFromTeachIn(Message4BS msg) {
		int uid = Utils.bytes2intLE(msg.getSenderId(), 0, 4);
		EnOceanDeviceImpl device = new EnOceanDeviceImpl(bc, uid);
		if (msg.hasTeachInInfo()) {
			device.registerProfile(msg.getRORG(), msg.getFunc(), msg.getType(),
					msg.getManuf());
		}
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

	private ServiceTracker registerRpcSetListener(BundleContext bc, ServiceTrackerCustomizer listener) {
		return registerServiceFrom(bc, org.osgi.service.enocean.sets.EnOceanRPCSet.class, listener);
	}

	private ServiceTracker registerMessageSetListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		return registerServiceFrom(bc, org.osgi.service.enocean.sets.EnOceanMessageSet.class, listener);
	}

	private ServiceTracker registerChannelDescriptionSetListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		return registerServiceFrom(bc, org.osgi.service.enocean.sets.EnOceanChannelDescriptionSet.class, listener);
	}

}
