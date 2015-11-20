/*
 * Copyright (c) OSGi Alliance (2002, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.upnp;

import java.util.Dictionary;

/**
 * A UPnP action.
 * 
 * Each UPnP service contains zero or more actions. Each action may have zero or
 * more UPnP state variables as arguments.
 * 
 * @author $Id$
 */
public interface UPnPAction {
	/**
	 * Returns the action name.
	 * 
	 * The action name corresponds to the {@code name} field in the
	 * {@code actionList} of the service description.
	 * <ul>
	 * <li>For standard actions defined by a UPnP Forum working committee,
	 * action names must not begin with {@code X_ } nor {@code  A_}.</li>
	 * <li>For non-standard actions specified by a UPnP vendor and added to a
	 * standard service, action names must begin with {@code X_}.</li>
	 * </ul>
	 * 
	 * <p>
	 * This method must continue to return the action name after the UPnP action
	 * has been removed from the network.
	 * 
	 * @return Name of action, must not contain a hyphen character or a hash
	 *         character
	 */
	String getName();

	/**
	 * Returns the name of the designated return argument.
	 * <p>
	 * One of the output arguments can be flagged as a designated return
	 * argument.
	 * 
	 * <p>
	 * This method must continue to return the action return argument name after
	 * the UPnP action has been removed from the network.
	 * 
	 * @return The name of the designated return argument or {@code null} if
	 *         none is marked.
	 */
	String getReturnArgumentName();

	/**
	 * Lists all input arguments for this action.
	 * <p>
	 * Each action may have zero or more input arguments.
	 * 
	 * <p>
	 * This method must continue to return the action input argument names after
	 * the UPnP action has been removed from the network.
	 * 
	 * @return Array of input argument names or {@code null} if no input
	 *         arguments.
	 * 
	 * @see UPnPStateVariable
	 */
	String[] getInputArgumentNames();

	/**
	 * List all output arguments for this action.
	 * 
	 * <p>
	 * This method must continue to return the action output argument names
	 * after the UPnP action has been removed from the network.
	 * 
	 * @return Array of output argument names or {@code null} if there are no
	 *         output arguments.
	 * 
	 * @see UPnPStateVariable
	 */
	String[] getOutputArgumentNames();

	/**
	 * Finds the state variable associated with an argument name.
	 * 
	 * Helps to resolve the association of state variables with argument names
	 * in UPnP actions.
	 * 
	 * @param argumentName The name of the UPnP action argument.
	 * @return State variable associated with the named argument or {@code null}
	 *         if there is no such argument.
	 * 
	 * @throws IllegalStateException if the UPnP action has been removed from
	 *         the network.
	 * 
	 * @see UPnPStateVariable
	 */
	UPnPStateVariable getStateVariable(String argumentName);

	/**
	 * Invokes the action.
	 * 
	 * The input and output arguments are both passed as {@code Dictionary}
	 * objects. Each entry in the {@code Dictionary} object has a {@code String}
	 * object as key representing the argument name and the value is the
	 * argument itself. The class of an argument value must be assignable from
	 * the class of the associated UPnP state variable.
	 * 
	 * The input argument {@code Dictionary} object must contain exactly those
	 * arguments listed by {@code getInputArguments} method. The output argument
	 * {@code Dictionary} object will contain exactly those arguments listed by
	 * {@code getOutputArguments} method.
	 * 
	 * @param args A {@code Dictionary} of arguments. Must contain the correct
	 *        set and type of arguments for this action. May be {@code null} if
	 *        no input arguments exist.
	 * 
	 * @return A {@code Dictionary} with the output arguments. {@code null} if
	 *         the action has no output arguments.
	 * 
	 * @throws UPnPException A UPnP error has occurred.
	 * @throws IllegalStateException if the UPnP action has been removed from
	 *         the network.
	 * @throws Exception The execution fails for some reason.
	 * 
	 * @see UPnPStateVariable
	 */
	Dictionary<String, Object> invoke(Dictionary<String, ?> args) throws Exception;
}
