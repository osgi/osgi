package org.osgi.test.cases.upnp.tbc.export;

import org.osgi.service.upnp.*;

/**
 * 
 * 
 */
public class TestStateVariable implements UPnPStateVariable {
	/**
	 * The state variable name.
	 */
	private String		name;
	/**
	 * The java type of the variable.
	 */
	private Class		javaDT;
	/**
	 * The upnp data type of the variable.
	 */
	private String		upnpDT;
	/**
	 * The current value of the variable.
	 */
	private Object		value;
	/**
	 * The default value of the variable.
	 */
	private Object		dv;
	/**
	 * A list of all accepted values.
	 */
	private String[]	values;
	/**
	 * The minimum value of the variable.
	 */
	private Number		min;
	/**
	 * The maximum value of the variable.
	 */
	private Number		max;
	/**
	 * The inc/dec step of this variable.
	 */
	private Number		step;
	/**
	 * Indicates if events should be send.
	 */
	private boolean		events;

	/**
	 * 
	 * @param name the variable's name.
	 * @param javaDT the variable's java type.
	 * @param upnpDT the variable's upnp type.
	 * @param dv the variable's default value.
	 * @param values the variable's list of accepted values.
	 * @param min the variable's minimum value.
	 * @param max the variable's maximum value.
	 * @param step the variable's inc/dec step.
	 * @param events show if events should be send.
	 */
	public TestStateVariable(String name, Class javaDataType,
			String upnpDataType, Object defaultValue, String[] allowedValues,
			Number minimum, Number maximum, Number step, boolean sendEvents) {
		this.name = name;
		this.javaDT = javaDataType;
		this.upnpDT = upnpDataType;
		this.dv = defaultValue;
		this.values = allowedValues;
		this.min = minimum;
		this.max = maximum;
		this.step = step;
		this.events = sendEvents;
		this.value = defaultValue;
	}

	/**
	 * Gets the name of this variable.
	 * 
	 * @return the name of this variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the java class of this variable.
	 * 
	 * @return the java class of this variable.
	 */
	public Class getJavaDataType() {
		return javaDT;
	}

	/**
	 * Gets the upnp type of this variable.
	 * 
	 * @return the upnp type of this variable.
	 */
	public String getUPnPDataType() {
		return upnpDT;
	}

	/**
	 * Gets the default value of this state variable.
	 * 
	 * @return the default value of this state variable.
	 */
	public Object getDefaultValue() {
		return dv;
	}

	/**
	 * Returns a list of all allowed values.
	 * 
	 * @return a list of all allowed values.
	 */
	public String[] getAllowedValues() {
		return values;
	}

	/**
	 * Returns the minimum allowed value.
	 * 
	 * @return the minimum allowed value.
	 */
	public Number getMaximum() {
		return max;
	}

	/**
	 * Returns the maximum allowed value.
	 * 
	 * @return the maximum allowed value.
	 */
	public Number getMinimum() {
		return min;
	}

	/**
	 * Returns the inc/dec step.
	 * 
	 * @return the inc/dec step.
	 */
	public Number getStep() {
		return step;
	}

	/**
	 * Returns the events status.
	 * 
	 * @return true if notifying is performed, false otherwise.
	 */
	public boolean sendsEvents() {
		return events;
	}
}