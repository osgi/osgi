/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * This dictionary holds the properties of a configuration. A separate class was
 * necessary to provide simultaneously case insensitivity of lookup and
 * preservation of case of keys.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class CMDictionary extends Hashtable {
	private Hashtable	lowerToRealCase;

	/**
	 * Constructs an empty Dictionary with 15 capacity.
	 */
	public CMDictionary() {
		super(15);
		lowerToRealCase = new Hashtable(15);
	}

	/**
	 * Constructs a Dictionary with user-defined initial capacity.
	 * 
	 * @param initialCapacity initial size of the hashtable.
	 */
	public CMDictionary(int initialCapacity) {
		super(initialCapacity);
		lowerToRealCase = new Hashtable(initialCapacity);
	}

	/**
	 * Processes case insensitive look up for a key.
	 * 
	 * @param key key to look for
	 * @return the value corresponding to the key.
	 */
	public Object get(Object key) {
		Object value = super.get(key);
		if (value != null)
			return value;
		Object realCase = lowerToRealCase.get(((String) key).toLowerCase());
		if (realCase != null) {
			return super.get(realCase);
		}
		else {
			return null;
		}
	}

	/**
	 * Puts the key-value pair in the hashtable. If the hashtable already
	 * contains this key or one that is equal-ignore-case to the new key, the
	 * old key-value pair is removed from hashtable.
	 * 
	 * @param key the hashtable key
	 * @param value the value
	 * @return the previous value corresponding to this key or to any key which
	 *         is equal-ignore-case to the new key.
	 */
	public Object put(Object key, Object value) {
		Object oldValue = super.put(key, value);
		//at deserialization lowerToRealCase is null
		if (oldValue == null && lowerToRealCase != null) {
			Object oldKey = lowerToRealCase.put(((String) key).toLowerCase(),
					key);
			if (oldKey != null && !oldKey.equals(key)) {
				return super.remove(oldKey);
			}
			else {
				return null;
			}
		}
		return oldValue;
	}

	/**
	 * If the hashtable contains a key, which is equal-ignore-case to the one
	 * passed, the key-value pair is removed from the hashtable and the value is
	 * returned.
	 * 
	 * @param key to be removed
	 * @return the value for this key or null
	 */
	public Object remove(Object key) {
		String lowerCase = ((String) key).toLowerCase();
		Object realCase = lowerToRealCase.remove(lowerCase);
		if (realCase != null) {
			return super.remove(realCase);
		}
		return null;
	}

	/**
	 * Copies the properties into the hashtable. If new properties' keys match
	 * those in the hashtable, the new key-value pairs overwrite old ones.
	 * 
	 * @param props properties to copy into the hashtable
	 */
	public void copyFrom(Dictionary props) {
		Enumeration keys = props.keys();
		Enumeration values = props.elements();
		while (keys.hasMoreElements()) {
			put(keys.nextElement(), values.nextElement());
		}
	}
}