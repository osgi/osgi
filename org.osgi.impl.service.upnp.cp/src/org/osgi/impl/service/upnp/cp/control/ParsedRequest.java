package org.osgi.impl.service.upnp.cp.control;

import java.util.Hashtable;
import java.util.Dictionary;

public class ParsedRequest {
	private String		serviceType;
	private String		actionName;
	private Hashtable	args	= new Hashtable();
	private String		returnValue;

	// This method sets the serviceType.
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	// This method sets the action name
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	// This method sets the given argument name and value.
	public void setArgument(String argName, String argValue) {
		args.put(argName, argValue);
	}

	// This method returns the Service type.
	public String getServiceType() {
		return serviceType;
	}

	// This method returns the Action name
	public String getActionName() {
		return actionName;
	}

	// This method returns all the arguments
	public Dictionary getArguments() {
		return (Dictionary) args;
	}

	// This method returns the argument value of the given name
	public String getArgumentValue(String name) {
		if (name == null) {
			return null;
		}
		return (String) args.get(name);
	}

	// This method returns the out argument value
	public String getReturnValue() {
		return returnValue;
	}
}
