/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.metatype.tb2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/** 
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 *
 * @author left 
 * @version $Revision$
 */
public class ObjectClassDefinitionImpl implements ObjectClassDefinition {

	/**
	 * Creates a new instance of ObjectClassDefinitionImpl
	 */
	public ObjectClassDefinitionImpl() {
		
	}
	
	/**
	 * An stub method that return an array of AttributeDefinition stubs
	 * 
	 * @param filter This filter to use. This implementation does not use this parameter.
	 * @return An array of AttributeDefinition stubs
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getAttributeDefinitions(int)
	 */
	public AttributeDefinition[] getAttributeDefinitions(int filter) {
		return new AttributeDefinition[] { new AttributeDefinitionImpl() };
	}

	/**
	 * An stub method that return the description "Object class description"
	 * 
	 * @return The description "Object class description"
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getDescription()
	 */
	public String getDescription() {
		return "Object class description";
	}

	/**
	 * A stub method that return a ByteArrayInputStream with an empty byte array
	 * 
	 * @param size The size of the icon
	 * @return A ByteArrayInputStream with an empty byte array
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getIcon(int)
	 */
	public InputStream getIcon(int size) {
		return new ByteArrayInputStream(new byte[] { });
	}

	/**
	 * A stub method that return the id "br.org.cesar.ocd"  
	 * 
	 * @return The id "br.org.cesar.ocd"
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getID()
	 */
	public String getID() {
		return "br.org.cesar.ocd";
	}

	/**
	 * A stub method that return a name "Object class name"
	 * 
	 * @return The name "Object class name"
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getName()
	 */
	public String getName() {
		return "Object class name";
	}
}