/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.module;

import java.util.*;

/*
 * A MappedList maps values into keyed list arrays.  All values with the same key are stored
 * into the same array.  Extending classes may override the sort method to sort the individual
 * arrays in the MappedList.  By default the MappedList appends new values to the end of the array.
 */
public class MappedList {
	// the mapping with key -> Object[] mapping
	protected HashMap internal = new HashMap();

	public void put(Object key, Object value) {
		Object[] existing = (Object[]) internal.get(key);
		if (existing == null) {
			existing = new Object[1]; // be optimistic; start small
			existing[0] = value;
			internal.put(key, existing);
		} else {
			// append the new value to the end.
			Object[] newValues = new Object[existing.length + 1];
			System.arraycopy(existing, 0, newValues, 0, existing.length);
			newValues[existing.length] = value;
			sort(newValues); // sort the new values array
			internal.put(key, newValues); // overwrite the old values in tha map
		}
	}

	protected void sort(Object[] values) {
		// a MappedList is not sorted by default
		// extending classes may override this method to sort lists
	}

	// removes all values with the specified key
	public Object[] remove(Object key) {
		return get(key, true);
	}

	// gets all values with the specified key
	public Object[] get(Object key) {
		return get(key, false);
	}

	// gets all values with the specified and optionally removes them
	private Object[] get(Object key, boolean remove) {
		Object[] result = (Object[]) (remove ? internal.remove(key) : internal.get(key));
		return result == null ? new Object[0] : result;
	}

	// returns the number of keyed lists
	public int getSize() {
		return internal.size();
	}

	// returns all values of all keys
	public Object[] getAllValues() {
		if (getSize() == 0)
			return new Object[0];
		ArrayList results = new ArrayList(getSize());
		Iterator iter = internal.values().iterator();
		while (iter.hasNext()) {
			Object[] values = (Object[]) iter.next();
			for (int i = 0; i < values.length; i++)
				results.add(values[i]);
		}
		return results.toArray();
	}

	// removes all keys from the map
	public void clear() {
		internal.clear();
	}

}
