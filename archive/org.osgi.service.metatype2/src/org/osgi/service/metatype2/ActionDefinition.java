/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.metatype2;

/**
 * An interface to describe an action.
 *
 * <p>An <tt>ActionDefinition</tt> object defines a description of an action.
 * An action is very similar to an attribute, it only adds the definition of the
 * input arguments if any.
 *
 * @version $Revision$
 */
public interface ActionDefinition extends ExtendedAttributeDefinition {
	
	/**
	 * The <code>VOID(13)</code> type. 
	 * 
	 * The action may have VOID type meaning that it does not 
	 * have output results.
	 */
	public static final int VOID = 13;
	
  /**
	 * Returns the definitions of the input arguments of this action
	 * 
	 * @return The definitions of the input arguments or null if there is no input arguments.
	 */
    ExtendedAttributeDefinition[] getInputArgumentDefinitions();
}


