package org.osgi.test.cases.upnp.tbc.export;

import java.util.*;
import org.osgi.service.upnp.*;
import org.osgi.test.cases.upnp.tbc.*;

/**
 * 
 * 
 */
public class TestAction implements UPnPAction {
	// Action name.
	protected String		name;
	// Action return argument name.
	protected String		raName;
	// Output argument names.
	protected String[]		oaNames;
	// Input argument names.
	protected String[]		iaNames;
	// Contains service state variables.
	protected Dictionary	argSSV;
	private TestService		parent;
  //the response when action is invoked
  private Dictionary response;

  public TestAction(String name, String raName, String[] iaNames,
                    String[] oaNames, Dictionary argSSV, Dictionary response) {
    this.name = name;
    this.raName = raName;
    this.oaNames = oaNames;
    this.iaNames = iaNames;
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
		return iaNames;
	}

	public String[] getOutputArgumentNames() {
		return oaNames;
	}

	public UPnPStateVariable getStateVariable(String argumentName) {
		return (UPnPStateVariable) argSSV.get(argumentName);
	}

	public void setParent(TestService parent) {
		this.parent = parent;
	}

	public Dictionary invoke(Dictionary d) throws Exception {
/*
		String st = (String) d.get(UPnPConstants.N_IN_STRING);
		Double dl = (Double) d.get(UPnPConstants.N_IN_NUMBER);
		Integer inint = (Integer) d.get(UPnPConstants.N_IN_INT);
		Float fl = (Float) d.get(UPnPConstants.N_IN_FLOAT);
		Boolean bool = (Boolean) d.get(UPnPConstants.N_IN_BOOLEAN);
		Character ch = (Character) d.get(UPnPConstants.N_IN_CHAR);

*/		return response;
	}
}