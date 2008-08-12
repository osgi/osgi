/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.net.URL;

//  ******************** AliasMatch ********************
/**
 * * A very simple container class that groups an AliasRegistration * together
 * with an InputStream matching a resource. * Used to reduce the number of calls
 * to getResource(). * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$ *
 * @see AliasRegistration
 */
public final class AliasMatch {
	AliasRegistration	aliasReg;
	URL					url;

	public AliasMatch(AliasRegistration aliasReg, URL url) {
		this.aliasReg = aliasReg;
		this.url = url;
	}
}
