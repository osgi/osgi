/**
 * Copyright (c) 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.util.*;

/**
 * Class representing a java specification version.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class VersionNumber implements Comparable
{
    final private int x;
    final private int y;
    final private int z;

    /**
     * Construct a VersionNumber object
     *
     * @param ver string in X.Y.Z format.
     */
    public VersionNumber(String ver) throws NumberFormatException {
	if (ver != null) {
	    StringTokenizer st = new StringTokenizer(ver,".");
	    x = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : -1;
	    y = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : -1;
	    z = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : -1;
	    if (st.hasMoreTokens()) {
		throw new NumberFormatException("Too many '.' in version number");
	    }
	} else {
	    x = -1;
	    y = -1;
	    z = -1;
	}
    }


    /**
     * Check if version has been specified
     *
     * @return true if version is specified.
     */
    public boolean isSpecified() {
	return x != -1;
    }


    /**
     * Compare object to another VersionNumber.
     *
     * @param obj Version to compare to.
     * @return Return 0 if equals, negative if this object is less than obj
     *         and positive if this object is larger then obj.
     * @exception ClassCastException if object is not a VersionNumber object.
     */
    public int compareTo(Object obj) throws ClassCastException {
	VersionNumber v2 = (VersionNumber)obj;
	int res = x - v2.x;
	if (res == 0) {
	    res = y - v2.y;
	    if (res == 0) {
		res = z - v2.z;
	    }
	}
	return res;
    }


    /**
     * String with version number. If version is not specified return
     * an empty string.
     *
     * @return String.
     */
    public String toString() {
	StringBuffer res = new StringBuffer();
	if (x != -1) {
	    res.append(x);
	    if (y != -1) {
		res.append("." + y);
		if (z != -1) {
		    res.append("." + z);
		}
	    }
	    return res.toString();
	} else {
	    return "";
	}
    }


    /**
     * Check if object is equal to this object.
     *
     * @param obj Package entry to compare to.
     * @return true if equal, otherwise false.
     */
    public boolean equals(Object obj) throws ClassCastException {
	VersionNumber o = (VersionNumber)obj;
	return x == o.x && y == o.y && z == o.z;
    }


    /**
     * Hash code for this package entry.
     *
     * @return int value.
     */
    public int hashCode() {
	return x << 6 + y << 3 + z;
    }

}
