/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
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
import java.util.jar.*;

/**
 * Dictonary for Bundle Manifest headers.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class HeaderDictionary extends Dictionary
{
    Hashtable headers;

    
    /**
     * Create an empty dictionary for manifest attributes.
     */
    public HeaderDictionary()
    {
	headers = new Hashtable();
    }


    /**
     * Create a dictionary from manifest attributes.
     */
    public HeaderDictionary(Attributes in) throws IOException
    {
	headers = new Hashtable();
	for (Iterator i = in.entrySet().iterator(); i.hasNext();) {
	    Map.Entry e = (Map.Entry)i.next();
	    headers.put(e.getKey(), e.getValue());
	}
    }

    
    /**
     * Create a clone of an existing HeaderDictionary
     */
    public HeaderDictionary(HeaderDictionary in)
    {
	headers = (Hashtable)in.headers.clone();
    }
    
    
    /**
     * Returns an enumeration of the values in this dictionary.
     */
    public Enumeration elements()
    {
	return headers.elements();
    }
    
    
    /**
     * Returns the value to which the key is mapped in this dictionary.
     */
    public Object get(Object key)
    {
	return headers.get(new Attributes.Name((String)key));
    }
    
    
    /**
     * Tests if this dictionary maps no keys to value.
     */
    public boolean isEmpty()
    {
	return headers.isEmpty();
    }
    
    
    /**
     *  Returns an enumeration of the keys in this dictionary.
     */
    public Enumeration keys()
    {
	final Enumeration keys = headers.keys();
	return new Enumeration() {
		public boolean hasMoreElements() {
		    return keys.hasMoreElements();
		}
		public Object nextElement() {
		    return keys.nextElement().toString();
		}
	    };
    }
    
    
    /**
     * Maps the specified key to the specified value in this dictionary.
     */
    public Object put(Object key, Object value)
    {
	return headers.put(new Attributes.Name((String)key), value);
    }
    
    
    /**
     * Removes the key (and its corresponding value) from this dictionary.
     */
    public Object remove(Object key)
    {
	return headers.remove(new Attributes.Name((String)key));
    }
    
    
    /** 
     * Returns the number of entries (dinstint keys) in this dictionary.
     */
    public int size()
    {
	return headers.size();
    }
    
}
