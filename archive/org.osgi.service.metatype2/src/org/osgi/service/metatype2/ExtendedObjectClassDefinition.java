/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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

