/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
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
	private final Dictionary<String,Object>	argSSV;
	// the response when action is invoked
	private final Dictionary<String,Object>	response;

	public TestAction(String name, String raName, String[] iaNames,
			String[] oaNames, Dictionary<String,Object> argSSV,
			Dictionary<String,Object> response) {
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
		return iaNames.clone();
	}

	public String[] getOutputArgumentNames() {
		if (oaNames == null) {
			return null;
		}
		return oaNames.clone();
	}

	public UPnPStateVariable getStateVariable(String argumentName) {
		return (UPnPStateVariable) argSSV.get(argumentName);
	}

	public Dictionary<String,Object> invoke(Dictionary<String,Object> d)
			throws Exception {
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
