package org.osgi.impl.service.enocean.basedriver;

import java.util.Hashtable;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.utils.EnOceanDriverException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanHost;
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

	private static final String TAG = "EnOceanBaseDriver";

	/**
	 * The {@link EnOceanBaseDriver} constructor initiates the connection
	 * towards an {@link EnOceanSerialHost} device. Then it registers itself as
	 * a service listener for any {@link EnOceanDevice},
	 * {@link EnOceanMessageSet}, {@link EnOceanRPCSet},
	 * {@link EnOceanChannelDescriptionSet} that would be registered in the
	 * framework.
	 * 
	 */
	public EnOceanBaseDriver(BundleContext bc) {

		try {
			registerEnOceanHost();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			deviceServiceRef = registerDeviceListener(bc, this);
			rpcSetServiceRef = registerRpcSetListener(bc, this);
			messageSetServiceRef = registerMessageSetListener(bc, this);
			channelDescriptionSetServiceRef = registerChannelDescriptionSetListener(bc, this);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
		}

		this.bc = bc;
		devices = new Hashtable(10);
		servicerefs = new Hashtable(10);
	}

	public void packetReceived(byte[] packet) {
		Logger.d(TAG, "received : " + Logger.toHexString(packet));
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
	}

	public void stop() {
	}

	private void registerEnOceanHost() throws EnOceanDriverException {
		Integer hostId = new Integer(System.getProperty("org.osgi.service.enocean.host.id"));
		String serialPort = System.getProperty("org.osgi.service.enocean.host.port");
		int serialSpeed = Integer.parseInt(System.getProperty("org.osgi.service.enocean.host.speed"));
		if (serialPort == null) {
			throw new EnOceanDriverException("no physical host available");
		}

		EnOceanSerialHost host = new EnOceanSerialHost(serialPort, serialSpeed);
		Logger.i(TAG, "registering physical host on " + serialPort);
		try {
			Properties props = new Properties();
			props.put(EnOceanHost.HOST_ID, hostId);
			ServiceRegistration sr = bc.registerService("org.osgi.service.enocean.EnOceanHost", host, props);
			servicerefs.put(hostId, sr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if (host != null) {
			host.addPacketListener(this);
			host.start();
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
