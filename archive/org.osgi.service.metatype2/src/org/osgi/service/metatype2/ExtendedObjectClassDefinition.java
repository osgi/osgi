/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

