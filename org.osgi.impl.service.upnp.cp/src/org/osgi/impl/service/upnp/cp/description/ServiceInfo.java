package org.osgi.impl.service.upnp.cp.description;

public class ServiceInfo {
	private int				major;
	private int				minor;
	private Action			action[];
	private StateVariable	sv[];
	private String			serviceAttribute;
	private String			serviceType;
	private String			serviceId;
	private String			scpdurl;
	private String			controlURL;
	private String			eventSubURL;

	// This method returns the service type of the service.
	public String getServiceType() {
		return serviceType;
	}

	// This method returns the service id of the service.
	public String getServiceID() {
		return serviceId;
	}

	// This method returns the SCPDURL of the service.
	public String getSCPDURL() {
		return scpdurl;
	}

	// This method returns the ServiceInfo object based on the SCPD URL.
	public ServiceInfo getServiceInfo() {
		ServiceInfo si = null;
		Document docu = null;
		try {
			Description mdoc = new Description(Description.bcd);
			docu = mdoc.getDocument(scpdurl);
		}
		catch (Exception eo1) {
			System.out.println(eo1.getMessage());
		}
		si = docu.getServiceInfo();
		if (si == null) {
			return null;
		}
		return si;
	}

	// This method returns the control URL of the service.
	public String getControlURL() {
		return controlURL;
	}

	// This method returns the event sub URL of the service.
	public String getEventSubURL() {
		return eventSubURL;
	}

	// This method sets the service type of the service.
	public void setServiceType(String type) {
		serviceType = type;
	}

	// This method sets the service id of the service.
	public void setServiceID(String id) {
		serviceId = id;
	}

	// This method sets the SCPDURL of the service.
	public void setSCPDURL(String url) {
		scpdurl = url;
	}

	// This method sets the control URL of the service.
	public void setControlURL(String url) {
		controlURL = url;
	}

	// This method sets the event sub URL of the service.
	public void setEventSubURL(String url) {
		eventSubURL = url;
	}

	// This method returns the major version of the service.
	public int getMajor() {
		return major;
	}

	// This method returns the minor version of the service.
	public int getMinor() {
		return minor;
	}

	// This method returns all the actions of the service.
	public Action[] getActions() {
		return action;
	}

	// This method returns the state variables of the service.
	public StateVariable[] getStateVariables() {
		return sv;
	}

	// This method returns the state variables based on the name of the state
	// variable..
	public StateVariable getStateVariable(String name) {
		StateVariable stateVars[] = getStateVariables();
		for (int ctr = 0; ctr < stateVars.length; ctr++) {
			if (stateVars[ctr].getName().equalsIgnoreCase(name)) {
				return stateVars[ctr];
			}
		}
		System.out.println("Unable to find matching name.");
		return null;
	}

	//
	public String getServiceAttribute() {
		return serviceAttribute;
	}

	// This method sets the root service attribute of the service.
	public void setServiceAttribute(String attKey, String attValue) {
		serviceAttribute = attValue;
	}

	// This method sets the major version of the service.
	public void setMajor(int maj) {
		major = maj;
	}

	// This method sets the minor version of the service.
	public void setMinor(int min) {
		minor = min;
	}

	// This method sets the actions for the service.
	public void setActions(Action arr[]) {
		action = arr;
	}

	// This method sets the StateVariables of the service.
	public void setStateVariable(StateVariable sva[]) {
		sv = sva;
	}
}
