package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.*;
import org.osgi.impl.service.upnp.cp.description.StateVariable;
import org.osgi.impl.service.upnp.cp.util.SamsungUPnPStateVariable;

public class UPnPStateVariableImpl implements SamsungUPnPStateVariable {
	private StateVariable	var;
	private String			name;
	private String			type;
	private String[]		vals;
	private String			newValue;

	UPnPStateVariableImpl(StateVariable var) {
		this.var = var;
		name = var.getName();
		type = var.getDataType();
		newValue = var.getDefaultValue();
		Vector values = var.getAllowedValueList();
		String[] vals = new String[values.size()];
		int i = 0;
		for (Enumeration e = values.elements(); e.hasMoreElements(); i++) {
			vals[i] = (String) e.nextElement();
		}
	}

	public String getName() {
		return name;
	}

	// This method returns the java data type of the state variable.
	public Class getJavaDataType() {
		if ((type.equals("ui1")) || (type.equals("ui2")) || (type.equals("i1"))
				|| (type.equals("i2")) || (type.equals("i4"))
				|| (type.equals("int"))) {
			Integer in = new Integer("1");
			return in.getClass();
		}
		else
			if (type.equals("ui2")) {
				Long ll = new Long("1");
				return ll.getClass();
			}
			else
				if ((type.equals("r4")) || (type.equals("float"))) {
					Float ff = new Float("1");
					return ff.getClass();
				}
				else
					if ((type.equals("r8")) || (type.equals("number"))
							|| (type.equals("fixed.14.4"))) {
						Double dd = new Double("1");
						return dd.getClass();
					}
					else
						if ((type.equals("string")) || (type.equals("uri"))
								|| (type.equals("uuid"))) {
							String ss = new String("aa");
							return ss.getClass();
						}
						else
							if (type.equals("char")) {
								Character cc = new Character('a');
								return cc.getClass();
							}
							else
								if (type.equals("boolean")) {
									Boolean bb = new Boolean(true);
									return bb.getClass();
								}
		return null;
	}

	// This method returns the UPnPData type of the state variable.
	public String getUPnPDataType() {
		return type;
	}

	// This method returns the default value of the state variable.
	public Object getDefaultValue() {
		return var.getDefaultValue();
	}

	// This method returns the allowed values of the state variable.
	public String[] getAllowedValues() {
		return vals;
	}

	// This method returns the minimum value of the state variable.
	public Number getMinimum() {
		return var.getMinimum();
	}

	//	
	public Number getMaximum() {
		return var.getMaximum();
	}

	// This method returns the step value of the state variable.
	public Number getStep() {
		return var.getStep();
	}

	// This method returns the value of the state variable's sendEvent
	// attribute.
	public boolean sendsEvents() {
		if (var.getSendEvents().equals("yes")) {
			return true;
		}
		return false;
	}

	// This method sets the state variable value.
	public void setChangedValue(String value) {
		newValue = value;
	}

	// This method returns the state variable value.
	public String getChangedValue() {
		return newValue;
	}
}
