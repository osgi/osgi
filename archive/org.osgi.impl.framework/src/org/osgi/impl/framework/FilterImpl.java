/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.util.Dictionary;

import org.osgi.framework.*;


/**
 * Filter implementation.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class FilterImpl implements Filter
{
    private String filter;


    FilterImpl(String filter) throws InvalidSyntaxException {
        filter = LDAPQuery.canonicalize(filter);
	LDAPQuery.check(filter);
	this.filter = filter;
    }


    public boolean match(ServiceReference reference)
    {
	try {
	    return LDAPQuery.query(filter, ((ServiceReferenceImpl)reference).getProperties());
	} catch (InvalidSyntaxException ignore) {
	    // Will not happen
	    return false;
	}
    }


    public boolean match(Dictionary dictionary)
    {
	try {
	    return LDAPQuery.query(filter, dictionary);
	} catch (InvalidSyntaxException ignore) {
	    // Will not happen
	    return false;
	}
    }


    public String toString()
    {
	return filter;
    }


    public boolean equals(Object obj)
    {
	return filter.equals(obj);
    }


    public int hashCode()
    {
	return filter.hashCode();
    }

}
