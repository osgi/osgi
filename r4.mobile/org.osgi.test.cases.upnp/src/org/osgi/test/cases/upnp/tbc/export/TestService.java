package org.osgi.test.cases.upnp.tbc.export;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;

/**
 * 
 * 
 */
public abstract class TestService implements UPnPService, ServiceListener {
	private UPnPAction[]		actions;
	private UPnPStateVariable[]	variables;
	private Hashtable			acts;
	private Hashtable			vars;
	private Hashtable			serviceProps;
	private Hashtable			refLis;
	private UPnPExportedDevice	device;
	private BundleContext		bc;
	private Vector				listeners;

	public TestService(UPnPAction[] actions, UPnPStateVariable[] variables,
			BundleContext bc) {
		this.bc = bc;
		acts = new Hashtable();
		vars = new Hashtable();
		if (variables == null || variables.length < 1) {
			throw new IllegalArgumentException(
					"UPnPService must have at least one state variable");
		}
		this.actions = actions;
		this.variables = variables;
		if (actions != null) {
			for (int i = 0; i < actions.length; i++) {
				acts.put(actions[i].getName(), actions[i]);
			}
		}
		for (int j = 0; j < variables.length; j++) {
			vars.put(variables[j].getName(), variables[j]);
		}
	}

	public abstract String getId();

	public abstract String getType();

	public abstract String getVersion();

	public final UPnPAction getAction(String name) {
		if (acts != null) {
			return (UPnPAction) acts.get(name);
		}
		else {
			if (actions != null) {
				for (int i = 0; i < actions.length; i++) {
					if (actions[i].getName().equals(name)) {
						return actions[i];
					}
				}
			}
			return null;
		}
	}

	public final UPnPAction[] getActions() {
		return (UPnPAction[]) ((Object) actions);
	}

	public final UPnPStateVariable[] getStateVariables() {
		return (UPnPStateVariable[]) ((Object) variables);
	}

	public final UPnPStateVariable getStateVariable(String name) {
		if (vars != null) {
			return (UPnPStateVariable) vars.get(name);
		}
		else {
			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getName().equals(name)) {
					return variables[i];
				}
			}
			return null;
		}
	}

	public void generateEvent(Dictionary events) {
		if (device != null) {
			if (listeners != null) {
				synchronized (listeners) {
					for (int i = 0; i < listeners.size(); i++) {
						UPnPEventListener tmp = (UPnPEventListener) listeners
								.elementAt(i);
						tmp.notifyUPnPEvent(device.getUDN(), getId(), events);
					}
				}
			}
		}
	}

	final void setDevice(UPnPExportedDevice device) {
		this.device = device;
	}

	public final void registerListener() {
		if (serviceProps == null) {
			serviceProps = new Hashtable(4);
			serviceProps.put(UPnPService.ID, getId());
			serviceProps.put(UPnPDevice.ID, device.getUDN());
			int xxx = Integer.parseInt(this.getVersion());
			String[] types = new String[xxx];
			for (int i = 0; i < xxx; i++) {
				types[i] = getType() + ":" + i;
			}
			serviceProps.put(UPnPService.TYPE, types);
			serviceProps.put(UPnPDevice.TYPE, device.getType());
		}
		listeners = new Vector(1);
		refLis = new Hashtable(1);
		try {
			StringBuffer filter = new StringBuffer();
			filter.append("(ObjectClass=");
			filter.append(UPnPEventListener.class.getName());
			filter.append(")");
			bc.addServiceListener(this, filter.toString());
			ServiceReference[] refs = bc.getServiceReferences(
					UPnPEventListener.class.getName(), filter.toString());
			//System.out.println("REFS: " + refs);
			if (refs != null && refs.length > 0) {
				//        synchronized (listeners) {
				for (int i = 0; i < refs.length; i++) {
					Filter f = (Filter) refs[i].getProperty("Filter.Object");
					if (f != null && f.match(serviceProps)) {
						UPnPEventListener tmp = (UPnPEventListener) bc
								.getService(refs[i]);
						listeners.addElement(tmp);
						refLis.put(refs[i], tmp);
					}
				}
				//        }
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void unregisterListener() {
		if (bc != null) {
			bc.removeServiceListener(this);
			synchronized (listeners) {
				listeners.removeAllElements();
				Enumeration en = refLis.keys();
				while (en.hasMoreElements()) {
					ServiceReference ref = (ServiceReference) en.nextElement();
					bc.ungetService(ref);
				}
				refLis.clear();
			}
			listeners = null;
			refLis = null;
			bc = null;
		}
	}

	public void serviceChanged(ServiceEvent se) {
		if (se.getType() == ServiceEvent.REGISTERED) {
			synchronized (listeners) {
				ServiceReference ref = se.getServiceReference();
				Filter f = (Filter) ref.getProperty("Filter.Object");
				if (f != null && f.match(serviceProps)) {
					UPnPEventListener tmp = (UPnPEventListener) bc
							.getService(ref);
					listeners.addElement(tmp);
					refLis.put(ref, tmp);
				}
			}
		}
		else
			if (se.getType() == ServiceEvent.UNREGISTERING) {
				synchronized (listeners) {
					ServiceReference ref = se.getServiceReference();
					UPnPEventListener tmp = (UPnPEventListener) refLis
							.remove(ref);
					listeners.removeElement(tmp);
				}
			}
	}
}