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
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanPacketListener;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class EnOceanBaseDriver implements EnOceanPacketListener, ServiceTrackerCustomizer {

	private Hashtable		devices;
	private Hashtable		servicerefs;
	private BundleContext	bc;

	// Those are just kept to be properly closed when the Base Driver is
	// stopped.
	private ServiceTracker		deviceServiceRef;
	private ServiceTracker		messageServiceRef;
	private ServiceTracker		descriptionServiceRef;

	private static final String TAG = "EnOceanBaseDriver";

	public EnOceanBaseDriver(BundleContext bc) {

		try {
			registerPhysicalEnOceanHost();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// FIXME: Messages are NOT registered services, are they ? Or are they
		// sent via EventAdmin ?
		try {
			deviceServiceRef = registerDeviceListener(bc, this);
			messageServiceRef = registerMessageListener(bc, this);
			descriptionServiceRef = registerDescriptionsListener(bc, this);
		} catch (InvalidSyntaxException e) {
			Logger.e(TAG, e.getMessage());
		}

		this.bc = bc;
		devices = new Hashtable(10);
		servicerefs = new Hashtable(10);
	}

	private ServiceTracker registerDeviceListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		String filter = "(objectClass=" + (org.osgi.service.enocean.EnOceanDevice.class).getName() + ')';
		Filter deviceAdminFilter = bc.createFilter(filter);
		ServiceTracker serviceTracker = new ServiceTracker(bc, deviceAdminFilter, listener);
		serviceTracker.open();
		return serviceTracker;
	}

	private ServiceTracker registerMessageListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		String filter = "(objectClass=" + (org.osgi.service.enocean.EnOceanMessage.class).getName() + ')';
		Filter deviceAdminFilter = bc.createFilter(filter);
		ServiceTracker serviceTracker = new ServiceTracker(bc, deviceAdminFilter, listener);
		serviceTracker.open();
		return serviceTracker;
	}

	private ServiceTracker registerDescriptionsListener(BundleContext bc, ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
		// TODO: Filter upon RPCSets, ChannelDescriptionSets
		String filter = "(objectClass=" + (org.osgi.service.enocean.EnOceanRPC.class).getName() + ')';
		Filter deviceAdminFilter = bc.createFilter(filter);
		ServiceTracker serviceTracker = new ServiceTracker(bc, deviceAdminFilter, listener);
		serviceTracker.open();
		return serviceTracker;
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
			if (service instanceof EnOceanMessage) {
				// TODO: Do something when a message is created
				Logger.d(TAG, "a new EnOceanMessage service has been registered");
			}
			if (service instanceof EnOceanRPC) {
				// TODO: Do something when a message is created
				Logger.d(TAG, "a new EnOceanRPC service has been registered");
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

	private void registerPhysicalEnOceanHost() throws EnOceanDriverException {

		String hostId = System.getProperty("org.osgi.service.enocean.host.id");
		String serialPort = System.getProperty("org.osgi.service.enocean.host.port");
		int serialSpeed = Integer.parseInt(System.getProperty("org.osgi.service.enocean.host.speed"));
		if (serialPort == null) {
			throw new EnOceanDriverException("no physical host available");
		}

		Properties props = new Properties();
		EnOceanHost host = new EnOceanPhysicalHost(serialPort, serialSpeed);
		Logger.i(TAG, "registering physical host on "+serialPort);
		try {
			props.put(EnOceanHost.HOST_ID, hostId);
			props.put(EnOceanHost.HOST_TYPE, "physical");
			ServiceRegistration sr = bc.registerService("org.osgi.service.enocean.EnOceanHost", host, props);
			servicerefs.put(hostId, sr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (host != null) {
			host.addPacketListener(this);
			host.start();
			devices.put(hostId, host);
		}
	}

	public void start() {
	}

	public void stop() {
	}

	public void packetReceived(byte[] packet) {
		// TODO Here stands the logic that parses the packet, converts it into
		// messages/devices/RPCs...

	}

}
