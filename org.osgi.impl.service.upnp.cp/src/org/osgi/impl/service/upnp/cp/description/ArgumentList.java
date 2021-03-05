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