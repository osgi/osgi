/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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

/**
 * Provides access to metatypes.
 * 
 * @version $Revision$
 */
public interface MetaTypeProvider {
	/**
	 * Return the definition of this object class for a locale.
	 * 
	 * <p>
	 * The locale parameter must be a name that consists of <tt>language</tt>[
	 * "_" <tt>country</tt>[ "_" <tt>variation</tt>] ] as is customary in
	 * the <tt>Locale</tt> class. This <tt>Locale</tt> class is not used
	 * because certain profiles do not contain it.
	 * 
	 * <p>
	 * The implementation should use the locale parameter to match an
	 * <tt>ObjectClassDefinition</tt> object. It should follow the customary
	 * locale search path by removing the latter parts of the name.
	 * 
	 * @param pid The PID for which the type is needed or null if there is only
	 *        1
	 * @param locale The locale of the definition or null for default locale
	 * @return the <tt>ObjectClassDefinition</tt> object
	 */
	ObjectClassDefinition getObjectClassDefinition(String pid, String locale);

	/**
	 * Return a list of locales available or null if only 1
	 * 
	 * The return parameter must be a name that consists of language [ _ country [ _
	 * variation ]] as is customary in the <tt>Locale</tt> class. This Locale
	 * class is not used because certain profiles do not contain it.
	 */
	String[] getLocales();
}

