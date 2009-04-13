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

import org.osgi.service.metatype.AttributeDefinition;

/**
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 *
 * @author left 
 * @version $Revision$
 */
public class AttributeDefinitionImpl implements AttributeDefinition {

	/**
	 * Creates a new instance of AttributeDefinitionImpl
	 */
	public AttributeDefinitionImpl() {
		// empty
	}
	
	/**
	 * An stub method that return the name "Attribute name"
	 * 
	 * @return The name "Attribute name"
	 * @see org.osgi.service.metatype.AttributeDefinition#getName()
	 */
	public String getName() {
		return "Attribute name";
	}

	/**
	 * An stub method that return the id "ad1"
	 * 
	 * @return The id "ad1"
	 * @see org.osgi.service.metatype.AttributeDefinition#getID()
	 */
	public String getID() {
		return "ad1";
	}

	/**
	 * An stub method that return the description "Attribute description"
	 * 
	 * @return The description "Attribute description"
	 * @see org.osgi.service.metatype.AttributeDefinition#getDescription()
	 */
	public String getDescription() {
		return "Attribute description";
	}

	/**
	 * An stub method that return the cardinality 0
	 * 
	 * @return The cardinality 0
	 * @see org.osgi.service.metatype.AttributeDefinition#getCardinality()
	 */
	public int getCardinality() {
		return 0;
	}

	/**
	 * A stub method that return the type AttributeDefinition.STRING 
	 * 
	 * @return The type AttributeDefinition.STRING
	 * @see org.osgi.service.metatype.AttributeDefinition#getType()
	 */
	public int getType() {
		return AttributeDefinition.STRING;
	}

	/**
	 * A stub method that return the option values { "" }
	 * 
	 * @return The option values { "" }
	 * @see org.osgi.service.metatype.AttributeDefinition#getOptionValues()
	 */
	public String[] getOptionValues() {
		return new String[] { "" };
	}

	/**
	 * A stub method that return the option labels { "" }
	 * 
	 * @return The option labels { "" }
	 * @see org.osgi.service.metatype.AttributeDefinition#getOptionLabels()
	 */
	public String[] getOptionLabels() {
		return new String[] { "" };
	}

	/**
	 * A stub method that accept any value
	 * 
	 * @param value The value fo validate
	 * @return null for any value that
	 * @see org.osgi.service.metatype.AttributeDefinition#validate(java.lang.String)
	 */
	public String validate(String value) {
		return null;
	}

	/**
	 * A stub method that return the default values { "" }
	 * 
	 * @return The default values { "" }
	 * @see org.osgi.service.metatype.AttributeDefinition#getDefaultValue()
	 */
	public String[] getDefaultValue() {
		return new String[] { "" };
	}

}
