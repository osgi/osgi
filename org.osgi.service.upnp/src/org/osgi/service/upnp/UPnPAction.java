/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002, 2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.upnp;

import java.util.Dictionary;

/**
 * A UPnP action.
 * 
 * Each UPnP service contains zero or more actions. Each action may have zero or
 * more UPnP state variables as arguments.
 *  
 */
public interface UPnPAction {
	/**
	 * Returns the action name.
	 * 
	 * The action name corresponds to the <code>name</code> field in the
	 * <code>actionList</code> of the service description.
	 * <ul>
	 * <li>For standard actions defined by a UPnP Forum working committee,
	 * action names must not begin with <code>X_ </code> nor <code> A_</code>.</li>
	 * <li>For non-standard actions specified by a UPnP vendor and added to a
	 * standard service, action names must begin with <code>X_</code>.</li>
	 * </ul>
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
	 * @return The name of the designated return argument or <code>null</code> if
	 *         none is marked.
	 */
	String getReturnArgumentName();

	/**
	 * Lists all input arguments for this action.
	 * <p>
	 * Each action may have zero or more input arguments.
	 * 
	 * @return Array of input argument names or <code>null</code> if no input
	 *         arguments.
	 * 
	 * @see UPnPStateVariable
	 */
	String[] getInputArgumentNames();

	/**
	 * List all output arguments for this action.
	 * 
	 * @return Array of output argument names or <code>null</code> if there are no
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
	 * @return State variable associated with the named argument or
	 *         <code>null</code> if there is no such argument.
	 * 
	 * @see UPnPStateVariable
	 */
	UPnPStateVariable getStateVariable(String argumentName);

	/**
	 * Invokes the action.
	 * 
	 * The input and output arguments are both passed as <code>Dictionary</code>
	 * objects. Each entry in the <code>Dictionary</code> object has a
	 * <code>String</code> object as key representing the argument name and the
	 * value is the argument itself. The class of an argument value must be
	 * assignable from the class of the associated UPnP state variable.
	 * 
	 * The input argument <code>Dictionary</code> object must contain exactly
	 * those arguments listed by <code>getInputArguments</code> method. The output
	 * argument <code>Dictionary</code> object will contain exactly those
	 * arguments listed by <code>getOutputArguments</code> method.
	 *
	 * @param args A <code>Dictionary</code> of arguments. Must contain the correct set and
	 * type of arguments for this action. May be <code>null</code> if no
	 * input arguments exist.
	 *
	 * @return A <code>Dictionary</code> with the output arguments.
	 *         <code>null</code> if the action has no output arguments.
	 *
	 * @throws Exception The execution fails for some reason.
	 * @throws UPnPException  A UPnP error has occured.
	 *
	 * @see UPnPStateVariable
	 */
	Dictionary invoke(Dictionary args) throws Exception, UPnPException;
}
