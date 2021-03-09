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

package org.osgi.test.cases.metatype.tb2;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 *
 * @author left 
 * @author $Id$
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
