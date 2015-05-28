
package org.osgi.impl.service.enocean.basedriver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
import org.osgi.impl.service.enocean.basedriver.radio.MessageSYS_EX;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;
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

    private BundleContext bc;
    private Hashtable devices;
    private Hashtable hostRefs;

    private ServiceTracker deviceTracker;
    private EventAdmin eventAdmin;
    private ServiceRegistration eventHandlerRegistration;
    private EnOceanHostImpl host;

    /**
     * EnOcean base driver's tag/prefix for logger.
     */
    private static final String TAG = "EnOceanBaseDriver";

    /**
     * EnOcean base driver's key for the config exported PID table.
     */
    public static final String CONFIG_EXPORTED_PID_TABLE = "org.enocean.ExportedDeviceTable";

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
	devices = new Hashtable(10);
	hostRefs = new Hashtable(10);

	/* Register initial EnOceanHost */
	String hostPath = System.getProperty(
		"org.osgi.service.enocean.host.path", "/dev/ttyUSB0");
	Logger.d(TAG, "initial host path : " + hostPath);
	if (hostPath != null && hostPath != "") {
	    if (hostPath.equals(":testcase:")) {
		Logger.d(TAG, "Create, and register EnOceanHostTestImpl.");
		host = new EnOceanHostTestImpl(hostPath, this.bc);
	    }
	    registerHost(hostPath, host);
	    host.addPacketListener(this);
	}

	/* Initialize EventAdmin */
	ServiceReference ref = this.bc.getServiceReference(EventAdmin.class
		.getName());
	if (ref != null) {
	    eventAdmin = (EventAdmin) this.bc.getService(ref);
	}

	/* Initializes self as EventHandler */
	Hashtable ht = new Hashtable();
	ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, new String[] {
		EnOceanEvent.TOPIC_MSG_RECEIVED,
		EnOceanEvent.TOPIC_RPC_BROADCAST, });
	eventHandlerRegistration = this.bc.registerService(
		EventHandler.class.getName(), this, ht);
	Logger.d(TAG,
		"EnOcean base driver's eventHandler (as a ServiceRegistration), eventHandlerRegistration: "
			+ eventHandlerRegistration);

	/* Track the EnOcean services */
	try {
	    Logger.d(TAG, "Track the EnOcean services, deviceTracker: "
		    + deviceTracker);
	    deviceTracker = registerDeviceListener(this.bc, this);
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
	Logger.d(TAG, "radioPacketReceived data: " + data + " / data[0]: " + data[0]);
	Message msg;

	/* First, determine if teach-in and eventually create a device */
	switch (data[0]) {
	case Message.MESSAGE_4BS:
	    msg = new Message4BS(data);
	    Logger.d(TAG, "4BS msg received, payload: "
			    + Utils.bytesToHexString(msg.getPayloadBytes()));
	    if (msg.isTeachin()) {
		EnOceanDevice dev = getAssociatedDevice(msg);
		if (dev == null) {
		    if (msg.hasTeachInInfo()) {
			dev = new EnOceanDeviceImpl(bc, this,
				msg.getSenderId(), msg.getRorg());
			((EnOceanDeviceImpl) dev).registerProfile(
				msg.teachInFunc(), msg.teachInType(),
				msg.teachInManuf());
		    } else {
			dev = new EnOceanDeviceImpl(bc, this,
				msg.getSenderId(), msg.getRorg());
		    }
		    Logger.d(TAG, "new device " + dev);
		} else {
		    Logger.d(TAG, "message was a teach-in, but device already exists.");
		}
		return; // No need to do more processing on the message
	    }
	    break;
	case Message.MESSAGE_RPS:
	    msg = new MessageRPS(data);
	    EnOceanDevice dev = getAssociatedDevice(msg);
	    if (dev == null) {
		dev = new EnOceanDeviceImpl(bc, this, msg.getSenderId(), msg.getRorg());
		Logger.d(TAG, "new device " + dev);
	    }
	    break;
	case Message.MESSAGE_SYS_EX:
	    // case of a broadcast RPC (unicast RPCs are encapsulated into an ADT telegram)
	    MessageSYS_EX sysex = new MessageSYS_EX(new EspPacket(data).getData());
	    sendAsEvent(sysex.getRPC());

	default:
	    return;
	}

	/* Try to associate the message with a device and send to EventAdmin */
	EnOceanDevice dev = getAssociatedDevice(msg);
	if ((dev != null) && (dev instanceof EnOceanDeviceImpl)) {
	    EnOceanDeviceImpl implDev = (EnOceanDeviceImpl) dev;
	    int rorg = implDev.getRorg();
	    int func = implDev.getFunc();
	    int type = implDev.getType();

	    Logger.d(TAG, "rorg: " + rorg + ", func: " + func + ", type: " + type);

	    msg.setFunc(func);
	    msg.setType(type);
	    implDev.setLastMessage(msg);
	    sendAsEvent(msg);
	}
    }

    public Object addingService(ServiceReference ref) {
	Object service = this.bc.getService(ref);
	if ((service != null) && (service instanceof EnOceanDevice)) {
	    String servicePid = (String) ref.getProperty(Constants.SERVICE_PID);
	    String chipId = (String) ref.getProperty(EnOceanDevice.CHIP_ID);
	    Object hasExport = ref.getProperty(EnOceanDevice.ENOCEAN_EXPORT);
	    if (servicePid == null) {
		if (chipId == null) {
		    throw new IllegalStateException();
		}
		servicePid = chipId;
	    }
	    if ((chipId == null) && (hasExport != null)) {
		try {
		    host.allocChipID(servicePid);
		} catch (Exception e) {
		    Logger.d(TAG, "exception: " + e.getMessage());
		}
	    }
	    devices.put(servicePid, service);
	    Logger.d(TAG,
		    "servicetracker: EnOceanDevice service registered, PID: "
			    + servicePid);
	}
	return service;
    }

    public void modifiedService(ServiceReference ref, Object service) {
	Logger.d(TAG, "servicetracker: modifiedService: " + ref + " for " + service);
    }

    public void removedService(ServiceReference ref, Object service) {
	Logger.d(TAG, "servicetracker: removedService: " + ref + " for " + service);
	if (service instanceof EnOceanDevice) {
	    String servicePid = (String) ref.getProperty(Constants.SERVICE_PID);
	    if (servicePid == null) {
		servicePid = (String) ref.getProperty(EnOceanDevice.CHIP_ID);
	    }
	    Logger.d(TAG, "servicetracker: removedService: " + servicePid);
	}
    }

    public void handleEvent(Event event) {
	if (event.getTopic().equals(EnOceanEvent.TOPIC_MSG_RECEIVED)) {
	    if (event.getProperty(EnOceanEvent.PROPERTY_EXPORTED) != null) {
		try {
		    EnOceanMessage msg = (EnOceanMessage) event
			    .getProperty(EnOceanEvent.PROPERTY_MESSAGE);
		    if (msg != null) {
			EspPacket pkt = new EspPacket(msg);
			Logger.d(TAG, "Writing msg packet : "
					+ Utils.bytesToHexString(pkt.serialize()));
			send(pkt.serialize());
		    }
		} catch (Exception e) {
		    Logger.e(TAG, "could not process TOPIC_MSG_RECEIVED event", e);
		}
	    }
	} else if (event.getTopic().equals(EnOceanEvent.TOPIC_RPC_BROADCAST)) {
	    if (event.getProperty(EnOceanEvent.PROPERTY_EXPORTED) != null) {
		try {
		    EnOceanRPC rpc = (EnOceanRPC) event
			    .getProperty(EnOceanEvent.PROPERTY_RPC);
		    if (rpc != null) {
			EspPacket pkt = new EspPacket(rpc, 0xffffffff);
			Logger.d(TAG, "Writing RPC packet : "
					+ Utils.bytesToHexString(pkt.serialize()));
			send(pkt.serialize());
		    }
		} catch (Exception e) {
		    Logger.e(TAG, "could not process TOPIC_RPC_BROADCAST event", e);
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
    public ServiceRegistration registerHost(String hostPath,
	    EnOceanHost enOceanHost) {
	ServiceRegistration sr = null;
	try {
	    Properties props = new Properties();
	    props.put(EnOceanHost.HOST_ID, hostPath);
	    sr = bc.registerService(EnOceanHost.class.getName(), enOceanHost, props);
	    hostRefs.put(hostPath, sr);
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
	for (Iterator it = devices.entrySet().iterator(); it.hasNext();) {
	    Map.Entry entry = (Map.Entry) it.next();
	    if (entry.getValue() instanceof EnOceanDeviceImpl) {
		EnOceanDeviceImpl dev = (EnOceanDeviceImpl) entry.getValue();
		Logger.d(TAG, "unregistering device : " + dev);
		dev.remove();
	    }
	    it.remove();
	}
    }

    private void sendAsEvent(EnOceanMessage msg) {
	if (eventAdmin != null) {
	    Map properties = new Hashtable();
	    properties.put(EnOceanDevice.CHIP_ID, String.valueOf(msg.getSenderId()));
	    properties.put(EnOceanDevice.RORG, String.valueOf(msg.getRorg()));
	    properties.put(EnOceanDevice.FUNC, String.valueOf(msg.getFunc()));
	    properties.put(EnOceanDevice.TYPE, String.valueOf(msg.getType()));
	    properties.put(EnOceanEvent.PROPERTY_MESSAGE, msg);

	    Event event = new Event(EnOceanEvent.TOPIC_MSG_RECEIVED, properties);
	    eventAdmin.sendEvent(event);
	}
    }

    private void sendAsEvent(EnOceanRPC rpc) {
	if (eventAdmin != null) {
	    Map properties = new Hashtable();
	    properties.put(EnOceanEvent.PROPERTY_EXPORTED, "1");
	    properties.put(EnOceanEvent.PROPERTY_RPC, rpc);
	    Event evt = new Event(EnOceanEvent.TOPIC_RPC_BROADCAST, properties);
	    eventAdmin.sendEvent(evt);
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
	try {
	    ServiceReference[] ref = bc.getServiceReferences(null,
		"(&(objectClass=" + EnOceanDevice.class.getName() + ")"
		+ "(" + EnOceanDevice.CHIP_ID + "=" + msg.getSenderId() + "))");
	    if ((ref != null) && (ref.length == 1)) {
		return (EnOceanDevice) bc.getService(ref[0]);
	    }
	} catch (InvalidSyntaxException e) {
	    Logger.e(TAG, "Invalid syntax in device search : " + e.getMessage());
	}
	return null;
    }

    /*
     * The functions that come below are used to register the necessary
     * services.
     */
    private ServiceTracker registerDeviceListener(BundleContext bundleContext,
	    ServiceTrackerCustomizer listener) throws InvalidSyntaxException {
	Filter filter = bundleContext.createFilter("(objectClass="
		+ EnOceanDevice.class.getName() + ')');
	ServiceTracker serviceTracker = new ServiceTracker(bundleContext,
		filter, listener);
	serviceTracker.open();
	return serviceTracker;
    }

}
