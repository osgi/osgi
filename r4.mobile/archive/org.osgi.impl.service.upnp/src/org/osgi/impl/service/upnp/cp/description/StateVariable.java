package org.osgi.impl.service.upnp.cp.description;

import java.util.Vector;

public class StateVariable {
	private String	sendEvents			= "yes";
	private String	name;
	private String	dataType;
	private String	defaultValue;
	private Vector	allowedValueList	= new Vector();
	private Number	maximum;
	private Number	minimum;
	private Number	step;

	// This method returns the value of the sendEvents.
	public String getSendEvents() {
		return sendEvents;
	}

	// This method returns the name of the service.
	public String getName() {
		return name;
	}

	// This method returns the data type of the service.
	public String getDataType() {
		return dataType;
	}

	// This method returns the default value of the service.
	public String getDefaultValue() {
		return defaultValue;
	}

	// This method returns the allowed values of the service.
	public Vector getAllowedValueList() {
		return allowedValueList;
	}

	// This method returns the maximum value of the service.
	public Number getMaximum() {
		return maximum;
	}

	// This method returns the minimum value of the service.
	public Number getMinimum() {
		return minimum;
	}

	// This method returns the step value of the service.
	public Number getStep() {
		return step;
	}

	// This method sets the step value for the variable.
	public void setStep(Number stepVal) {
		step = stepVal;
	}

	// This method sets the maximum value for the variable.
	public void setMaximum(Number maxVal) {
		maximum = maxVal;
	}

	// This method sets the minimum value for the variable.
	public void setMinimum(Number minVal) {
		minimum = minVal;
	}

	// This method sets the allowed values of the variable.
	public void setAllowedValueList(Vector allowedVals) {
		allowedValueList = allowedVals;
	}

	// This method sets the default value of the variable.
	public void setDefaultValue(String defaultVal) {
		defaultValue = defaultVal;
	}

	// This method sets the data type for the variable.
	public void setDataType(String dType) {
		dataType = dType;
	}

	// This method sets the name for the variable.
	public void setName(String stateName) {
		name = stateName;
	}

	// This method sets the value for the sendEvents attribute.
	public void sendEvents(String eventsValue) {
		eventsValue = eventsValue.trim();
		sendEvents = eventsValue;
	}
}
