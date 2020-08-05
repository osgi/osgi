package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.upnp.cp.description.Action;
import org.osgi.impl.service.upnp.cp.description.ServiceInfo;
import org.osgi.impl.service.upnp.cp.description.StateVariable;
import org.osgi.impl.service.upnp.cp.util.Converter;
import org.osgi.impl.service.upnp.cp.util.SamsungUPnPService;
import org.osgi.impl.service.upnp.cp.util.SamsungUPnPStateVariable;
import org.osgi.impl.service.upnp.cp.util.UPnPEvent;
import org.osgi.impl.service.upnp.cp.util.UPnPListener;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class UPnPServiceImpl implements SamsungUPnPService,
		ServiceTrackerCustomizer<UPnPEventListener,UPnPEventListener>,
		UPnPListener {
	public ServiceInfo			serviceinfo;
	private String				id;
	private String				type;
	private String				version;
	private UPnPActionImpl[]		actions;
	private UPnPStateVariableImpl[]	variables;
	private UPnPBaseDriver		basedriver;
	private ServiceTracker<UPnPEventListener,UPnPEventListener>	stk;
	private UPnPEventListener	upnplistener;
	private Vector<UPnPEventListener>							listeners;
	private String				devid;
	private String				devtype;
	private String				eventUrl;
	private BundleContext		bc;
	public String				host;
	private String				urlbase;
	private String				subscribeid;
	private Dictionary<String,Object>							initialEvents;
	private boolean				firstSub	= true;
	private boolean				firstEvent	= true;

	// This constructor creates the UPnPServiceImpl object based on the given
	// details.
	UPnPServiceImpl(ServiceInfo serviceinfo, UPnPBaseDriver basedriver,
			String devid, String devtype, BundleContext bc) {
		this.serviceinfo = serviceinfo;
		this.basedriver = basedriver;
		this.devid = devid;
		this.devtype = devtype;
		this.bc = bc;
		listeners = new Vector<>(5, 5);
		if (serviceinfo != null) {
			id = serviceinfo.getServiceID();
			type = serviceinfo.getServiceType();
			version = serviceinfo.getServiceType();
			eventUrl = serviceinfo.getEventSubURL();
			if (basedriver.deviceinfo != null) {
				urlbase = basedriver.deviceinfo.getURLBase();
				int i1 = urlbase.indexOf("//") + 2;
				String sub = urlbase.substring(i1, urlbase.length());
				int i2 = sub.indexOf("/");
				if (i2 == -1) {
					host = sub.substring(0, sub.length());
				}
				else {
					host = sub.substring(0, i2);
				}
			}
			Action[] actionss = serviceinfo.getActions();
			if (actionss != null) {
				actions = new UPnPActionImpl[actionss.length];
				for (int i = 0; i < actionss.length; i++) {
					UPnPActionImpl upnpaction = new UPnPActionImpl(actionss[i],
							basedriver, this);
					actions[i] = upnpaction;
				}
			}
			StateVariable[] vars = serviceinfo.getStateVariables();
			if (vars != null) {
				variables = new UPnPStateVariableImpl[vars.length];
				for (int j = 0; j < vars.length; j++) {
					UPnPStateVariableImpl upnpvar = new UPnPStateVariableImpl(
							vars[j]);
					variables[j] = upnpvar;
				}
			}
			stk = new ServiceTracker<>(bc, UPnPEventListener.class,
					this);
			stk.open();
		}
	}

	// This method returns the service id.
	@Override
	public String getId() {
		return id;
	}

	// This method returns the type of the service.
	@Override
	public String getType() {
		return type;
	}

	// This method returns the version of the service.
	@Override
	public String getVersion() {
		return version;
	}

	// This method returns the upnp action based on the given name.
	@Override
	public UPnPAction getAction(String s) {
		checkState();
		if (actions != null) {
			for (int i = 0; i < actions.length; i++) {
				if (actions[i].getName().equals(s)) {
					return actions[i];
				}
			}
		}
		return null;
	}

	// This method returns all the UPnP actions.
	@Override
	public UPnPAction[] getActions() {
		checkState();
		return actions;
	}

	// This method returns all the state variables.
	@Override
	public UPnPStateVariable[] getStateVariables() {
		checkState();
		return variables;
	}

	//	This method returns the state variable based on the given name.
	@Override
	public UPnPStateVariable getStateVariable(String s) {
		checkState();
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].getName().equals(s)) {
				return variables[i];
			}
		}
		return null;
	}

	// This method called by service tracker when ever it tracks the log Service
	// added.It subscribes
	// with device when ever application adds listener with framework.
	@Override
	public UPnPEventListener addingService(
			ServiceReference<UPnPEventListener> sr) {
		Filter fl = (Filter) sr.getProperty(UPnPEventListener.UPNP_FILTER);
		if (fl == null) {
			return null;
		}
		String f = fl.toString();
		//System.out.println(f);
		upnplistener = bc.getService(sr);
		boolean b1 = true;
		boolean b2 = true;
		boolean b3 = true;
		boolean b4 = true;
		if (f.indexOf(UPnPDevice.ID) != -1) {
			if (f.indexOf(devid) == -1) {
				b1 = false;
			}
		}
		if (f.indexOf(UPnPDevice.TYPE) != -1) {
			if (f.indexOf(devtype) == -1) {
				b2 = false;
			}
		}
		if (f.indexOf(UPnPService.ID) != -1) {
			if (f.indexOf(id) == -1) {
				b3 = false;
			}
		}
		if (f.indexOf(UPnPService.TYPE) != -1) {
			if (f.indexOf(type) == -1) {
				b4 = false;
			}
		}
		if (b1 && b2 && b3 && b4) {
			listeners.add(upnplistener);
			if (firstSub) {
				subscribe();
				firstSub = false;
			}
			if (initialEvents != null) {
				upnplistener.notifyUPnPEvent(devid, id, initialEvents);
			}
		}
		return upnplistener;
	}

	// This method called by service tracker when ever it tracks log Service
	// removed
	@Override
	public void removedService(ServiceReference<UPnPEventListener> sr,
			UPnPEventListener obj) {
		upnplistener = bc.getService(sr);
		listeners.remove(upnplistener);
		firstSub = true;
		if (listeners.size() == 0) {
			unsubscribe();
		}
		bc.ungetService(sr);
	}

	// This method called by service tracker when ever log Service modified
	@Override
	public void modifiedService(ServiceReference<UPnPEventListener> sr,
			UPnPEventListener obj) {
		// empty
	}

	// This method sets all state variable values of service.
	@Override
	public void setChangedValue(Dictionary<String,Object> dict) {
		for (Enumeration<String> enumeration = dict.keys(); enumeration
				.hasMoreElements();) {
			String variablename = enumeration.nextElement();
			SamsungUPnPStateVariable sst = (SamsungUPnPStateVariable) getStateVariable(variablename);
			if (sst != null) {
				sst.setChangedValue((String) dict.get(variablename));
			}
		}
		for (Enumeration<UPnPEventListener> e = listeners.elements(); e
				.hasMoreElements();) {
			UPnPEventListener listener = e.nextElement();
			listener.notifyUPnPEvent(devid, id, dict);
		}
	}

	// This method returns the state variable value.
	@Override
	public String getChangedValue(String name) {
		SamsungUPnPStateVariable sst = (SamsungUPnPStateVariable) getStateVariable(name);
		if (sst != null) {
			return sst.getChangedValue();
		}
		else {
			return null;
		}
	}

	// This method is called whenever the service state is been changed.
	@Override
	public void serviceStateChanged(UPnPEvent upnpevent) {
		Hashtable<String,Object> VariablePair = new Hashtable<>();
		Converter convert = new Converter();
		Dictionary<String,Object> events = upnpevent.getList();
		UPnPStateVariable SVariable;
		for (int i = 0; i < variables.length; i++) {
			SVariable = variables[i];
			VariablePair.put(SVariable.getName(), SVariable.getUPnPDataType());
		}
		try {
			events = convert.upnp2java(events, VariablePair);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (firstEvent) {
			initialEvents = events;
		}
		if (events != null) {
			subscribeid = upnpevent.getSubscriptionId();
		}
		for (Enumeration<UPnPEventListener> e = listeners.elements(); e
				.hasMoreElements();) {
			UPnPEventListener listener = e.nextElement();
			listener.notifyUPnPEvent(devid, id, events);
		}
	}

	// This method is called when the eventing is unsubscribed.
	public void unsubscribe() {
		try {
			if (subscribeid != null) {
				basedriver.eventservice.unsubscribe(subscribeid);
				subscribeid = null;
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method calles hte basedrivers subscribe method.
	public void subscribe() {
		if (basedriver.eventservice != null) {
			try {
				basedriver.eventservice.subscribe(eventUrl, urlbase, this,
						"infinite");
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void closeTracker() {
		stk.close();
	}
	
	/* package-private */void release() {
		this.bc = null;
		this.basedriver = null;
		releaseActions();
	}

	private void checkState() {
		if (null == this.basedriver) {
			throw new IllegalStateException(
					"UPnP service device has been removed from the network. Device id: "
							+ devid + "; service id: " + id);
		}
	}

	private void releaseActions() {
		if (null == this.actions) {
			return;
		}
		for (int i = 0; i < this.actions.length; i++) {
			this.actions[i].release();
		}
	}
	
}
