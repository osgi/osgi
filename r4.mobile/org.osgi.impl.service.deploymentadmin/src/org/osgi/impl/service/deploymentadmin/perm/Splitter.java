/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin.perm;

import java.util.Vector;

public class Splitter {
	public static String[] split(String input, char sep, int limit) {
		Vector v = new Vector();
		boolean limited = (limit > 0);
		int applied = 0;
		int index = 0;
		StringBuffer part = new StringBuffer();
		while (index < input.length()) {
			char ch = input.charAt(index);
			if (ch != sep)
				part.append(ch);
			else {
				++applied;
				v.add(part.toString());
				part = new StringBuffer();
			}
			++index;
			if (limited && applied == limit - 1)
				break;
		}
		while (index < input.length()) {
			char ch = input.charAt(index);
			part.append(ch);
			++index;
		}
		v.add(part.toString());
		int last = v.size();
		if (0 == limit) {
			for (int j = v.size() - 1; j >= 0; --j) {
				String s = (String) v.elementAt(j);
				if ("".equals(s))
					--last;
				else
					break;
			}
		}
		String[] ret = new String[last];
		for (int i = 0; i < last; ++i)
			ret[i] = (String) v.elementAt(i);
		return ret;
	}
}
