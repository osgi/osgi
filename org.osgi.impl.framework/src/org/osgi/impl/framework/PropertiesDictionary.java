/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
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

import java.util.*;

import org.osgi.framework.*;

/**
 * Creates a copy of the properties associated with a service registration.
 * Checks that all the keys are strings and adds the class names.
 * Note! Creation of PropertiesDictionary must be synchronized.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class PropertiesDictionary extends Dictionary
{
    private TreeMap props = new TreeMap(new Comparator()
	{
	    public int compare(Object a, Object b) {
		return ((String)a).compareToIgnoreCase((String)b);
	    }
	});
    
    private static long nextServiceID = 1;
    
    PropertiesDictionary(Dictionary in, String[] classes, Long sid)
    {
	props.put(Constants.OBJECTCLASS, classes);
	props.put(Constants.SERVICE_ID, sid != null ? sid : new Long(nextServiceID));
	if (in != null) {
    for (Enumeration e = in.keys(); e.hasMoreElements();) {
      try {
        String key = (String)e.nextElement();
        if (props.containsKey(key)) {
          if (key.equalsIgnoreCase(Constants.OBJECTCLASS) ||
              key.equalsIgnoreCase(Constants.SERVICE_ID)) {
              continue;
          }
          throw new IllegalArgumentException(
          "Properties contains same key (" + key + ") with different cases");
        }
        props.put(key, in.get(key));
      } catch (ClassCastException ignore) {
        continue;
      }
    }
	}
	if (sid == null) {
	    nextServiceID++;
	}
    }
    
    public Object get(Object key)
    {
	return props.get(key);
    }
    
    
    public int size()
    {
	return props.size();
    }
    
    
    public String [] keyArray()
    {
	String[] res = new String[props.size()];
	props.keySet().toArray(res);
	return res;
    }
    
    // These aren't used but we implement to fulfill Dictionary class
    
    public Enumeration elements()
    {
	throw new RuntimeException("Not implemented");
    }
    
    public boolean isEmpty()
    {
	throw new RuntimeException("Not implemented");
    }
    
    public Enumeration keys()
    {
	throw new RuntimeException("Not implemented");
    }
    
    public Object put(Object k, Object v)
    {
	throw new RuntimeException("Not implemented");
    }
    
    public Object remove(Object k)
    {
	throw new RuntimeException("Not implemented");
    }
    
}
