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

import java.util.Dictionary;

import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 * 
 * @author left
 * @author $Id$
 */
public class MetaTypeProviderImpl implements ManagedService, MetaTypeProvider {

	/**
	 * Creates a new intance of MetaTypeProviderImpl
	 */
	public MetaTypeProviderImpl() {
		// empty
	}

	/**
	 * This service does not use any provided configuration
	 * 
	 * @param properties The configuration for this service
	 * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
	 */
	public void updated(Dictionary<String, ? > properties) {
		// empty
	}

	/**
	 * Return the only supported locale (pt_BR)
	 * 
	 * @return The supported locale
	 * @see org.osgi.service.metatype.MetaTypeProvider#getLocales()
	 */
	public String[] getLocales() {
		return new String[] {"pt_BR"};
	}

	/**
	 * Return the an object class definition created by the bundle. The
	 * supported parameters are the id "br.org.cesar.ocd1" and the locale null
	 * or "pt_BR"
	 * 
	 * @param id The ID of the requested object class. The only supported id is
	 *        "br.org.cesar.ocd1".
	 * @param locale The locale of the definition (only "pt_BR" is supported) or
	 *        <code>null</code> for default locale.
	 * @return A <code>ObjectClassDefinition</code> object.
	 * @see org.osgi.service.metatype.MetaTypeProvider#getObjectClassDefinition(java.lang.String,
	 *      java.lang.String)
	 */
	public ObjectClassDefinition getObjectClassDefinition(String id,
			String locale) {
		ObjectClassDefinition result;

		if (id.equals("org.osgi.test.cases.metatype.ocd1")
        && (locale == null || locale.equals("pt_BR")) ) {
			result = new ObjectClassDefinitionImpl();
		}
		else {
			result = null;
		}

		return result;
	}
}
