package org.osgi.impl.service.upnp.cp.description;

public class ArgumentList {
	private String	name;
	private String	direction;
	private String	returnValue;
	private String	relatedStateVariable;

	// This method returns the name of the argument.
	public String getName() {
		return name;
	}

	// This method returns the direction of the argument.
	public String getDirection() {
		return direction;
	}

	//
	public String getReturnValue() {
		return returnValue;
	}

	// This method returns the related state variable of the argument.
	public String getRelatedStateVariable() {
		return relatedStateVariable;
	}

	// This method sets the name for the argument.
	public void setName(String nam) {
		name = nam;
	}

	//	This method sets the direction for the argument.
	public void setDirection(String direct) {
		direction = direct;
	}

	// This method sets the return value for the argument.
	public void setReturnValue(String val) {
		returnValue = val;
	}

	// This method sets the related state variable for the argument.
	public void setRelatedStateVariable(String val) {
		relatedStateVariable = val;
	}
}