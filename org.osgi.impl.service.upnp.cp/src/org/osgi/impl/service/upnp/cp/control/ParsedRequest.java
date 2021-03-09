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
package org.osgi.impl.service.upnp.cp.control;

import java.util.Dictionary;
import java.util.Hashtable;

public class ParsedRequest {
	private String		serviceType;
	private String		actionName;
	private Hashtable<String,Object>	args	= new Hashtable<>();
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
	public Dictionary<String,Object> getArguments() {
		return args;
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
