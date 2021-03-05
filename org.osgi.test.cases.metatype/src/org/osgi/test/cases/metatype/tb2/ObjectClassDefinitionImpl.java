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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/** 
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 *
 * @author left 
 * @author $Id$
 */
public class ObjectClassDefinitionImpl implements ObjectClassDefinition {

	/**
	 * Creates a new instance of ObjectClassDefinitionImpl
	 */
	public ObjectClassDefinitionImpl() {
		// empty
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
		return "org.osgi.test.cases.metatype.ocd";
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
