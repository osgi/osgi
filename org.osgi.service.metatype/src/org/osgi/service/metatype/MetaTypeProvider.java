/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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
	 * The locale parameter must be a name that consists of <tt>language</tt>[
	 * "_" <tt>country</tt>[ "_" <tt>variation</tt>] ] as is customary in
	 * the <tt>Locale</tt> class. This <tt>Locale</tt> class is not used
	 * because certain profiles do not contain it.
	 * 
	 * @param id The ID of the requested object class. This can be a pid or
	 *        factory pid returned by getPids or getFactoryPids.
	 * @param locale The locale of the definition or <tt>null</tt> for default
	 *        locale.
	 * @return A <tt>ObjectClassDefinition</tt> object.
	 * @throws IllegalArgumentException If the id or locale arguments are not
	 *         valid
	 */
	ObjectClassDefinition getObjectClassDefinition(String id, String locale);

	/**
	 * Return a list of available locales.
	 * 
	 * The results must be names that consists of language [ _ country [ _
	 * variation ]] as is customary in the <tt>Locale</tt> class.
	 * 
	 * @return An array of locale strings or <tt>null</tt> if there is no
	 *         locale specific localization can be found.
	 *  
	 */
	String[] getLocales();
}

