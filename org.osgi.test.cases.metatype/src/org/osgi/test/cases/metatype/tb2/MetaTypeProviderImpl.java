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

import java.util.Dictionary;

import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * An implementation used to test the framework capability of use a bundle
 * specific MetaTypeProvider.
 * 
 * @author left
 * @version $Revision$
 */
public class MetaTypeProviderImpl implements ManagedService, MetaTypeProvider {

	/**
	 * Creates a new intance of MetaTypeProviderImpl
	 */
	public MetaTypeProviderImpl() {

	}

	/**
	 * This service does not use any provided configuration
	 * 
	 * @param properties The configuration for this service
	 * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
	 */
	public void updated(Dictionary properties) {

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

		if (id.equals("br.org.cesar.ocd1")
        && (locale == null || locale.equals("pt_BR")) ) {
			result = new ObjectClassDefinitionImpl();
		}
		else {
			result = null;
		}

		return result;
	}
}