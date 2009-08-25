package org.osgi.test.cases.upnp.tbc.export;

import java.util.Dictionary;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;

/**
 * 
 * 
 */
public class TestAction implements UPnPAction {
	// Action name.
	private final String		name;
	// Action return argument name.
	private final String		raName;
	// Output argument names.
	private final String[]		oaNames;
	// Input argument names.
	private final String[]		iaNames;
	// Contains service state variables.
	private final Dictionary	argSSV;
	// the response when action is invoked
	private final Dictionary	response;

	public TestAction(String name, String raName, String[] iaNames,
			String[] oaNames, Dictionary argSSV, Dictionary response) {
		this.name = name;
		this.raName = raName;
		this.oaNames = ((oaNames == null) ? null : (String[]) oaNames.clone());
		this.iaNames = ((iaNames == null) ? null : (String[]) iaNames.clone());
		this.argSSV = argSSV;
		this.response = response;
	}

	public String getName() {
		return name;
	}

	public String getReturnArgumentName() {
		return raName;
	}

	public String[] getInputArgumentNames() {
		if (iaNames == null) {
			return null;
		}
		return (String[]) iaNames.clone();
	}

	public String[] getOutputArgumentNames() {
		if (oaNames == null) {
			return null;
		}
		return (String[]) oaNames.clone();
	}

	public UPnPStateVariable getStateVariable(String argumentName) {
		return (UPnPStateVariable) argSSV.get(argumentName);
	}

	public Dictionary invoke(Dictionary d) throws Exception {
		/*
		 * String st = (String) d.get(UPnPConstants.N_IN_STRING); Double dl =
		 * (Double) d.get(UPnPConstants.N_IN_NUMBER); Integer inint = (Integer)
		 * d.get(UPnPConstants.N_IN_INT); Float fl = (Float)
		 * d.get(UPnPConstants.N_IN_FLOAT); Boolean bool = (Boolean)
		 * d.get(UPnPConstants.N_IN_BOOLEAN); Character ch = (Character)
		 * d.get(UPnPConstants.N_IN_CHAR);
		 */
		return response;
	}
}