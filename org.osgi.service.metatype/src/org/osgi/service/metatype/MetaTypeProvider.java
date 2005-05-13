/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.metatype;

/**
 * Provides access to metatypes.
 * 
 * @version $Revision$
 */
public interface MetaTypeProvider {
	/**
	 * Returns an object class definition for the specified id localized to the
	 * specified locale.
	 * 
	 * <p>
	 * The locale parameter must be a name that consists of <code>language</code>[
	 * "_" <code>country</code>[ "_" <code>variation</code>] ] as is customary in
	 * the <code>Locale</code> class. This <code>Locale</code> class is not used
	 * because certain profiles do not contain it.
	 * 
	 * @param id The ID of the requested object class. This can be a pid or
	 *        factory pid returned by getPids or getFactoryPids.
	 * @param locale The locale of the definition or <code>null</code> for default
	 *        locale.
	 * @return A <code>ObjectClassDefinition</code> object.
	 * @throws IllegalArgumentException If the id or locale arguments are not
	 *         valid
	 */
	ObjectClassDefinition getObjectClassDefinition(String id, String locale);

	/**
	 * Return a list of available locales.
	 * 
	 * The results must be names that consists of language [ _ country [ _
	 * variation ]] as is customary in the <code>Locale</code> class.
	 * 
	 * @return An array of locale strings or <code>null</code> if there is no
	 *         locale specific localization can be found.
	 *  
	 */
	String[] getLocales();
}
