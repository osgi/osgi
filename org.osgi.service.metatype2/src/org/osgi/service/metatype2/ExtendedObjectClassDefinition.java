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

import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * Defines an object class that may contain attributes and actions.
 *
 * @version $Revision$
 */
public interface ExtendedObjectClassDefinition extends ObjectClassDefinition{

    /**
	 * Return the definition of the attribute with the specified ID.
	 *
	 * @param id The ID of the requested attribute
	 * @return The definition of the attribute or <code>null</code> if no such attribute exists
	 */
    AttributeDefinition getAttributeDefinition(String id);

    /**
	 * Return the definitions of all actions defined in this class.
	 *
	 * @return An array of action definitions or <code>null</code> if no actions are found
	 */
    ActionDefinition[] getActionDefinitions();

    /**
	 * Return the definition of the action with the specified ID.
	 *
	 * @param id The ID of the requested action
	 * @return The definition of the action or <code>null</code> if no such action exists
	 */
    ActionDefinition getActionDefinition(String id);
}

