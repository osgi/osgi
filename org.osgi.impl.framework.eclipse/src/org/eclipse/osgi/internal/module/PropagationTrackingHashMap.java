
/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class PropagationTrackingHashMap {
	private HashMap internal = new HashMap();

	// Propagation roots
	Object put(ResolverExport re) {
		ArrayList existing = (ArrayList)internal.get(re.getName());
		if(existing == null) {
			ArrayList v = new ArrayList();
			v.add(re);
			internal.put(re.getName(), v);
		} else {
			if(!existing.contains(re)) {
				existing.add(re);
			}
		}
		return null;
	}
	
	ResolverExport get(String packageName) {
		ArrayList existing = (ArrayList)internal.get(packageName);
		if(existing == null)
			return null;
		else
			return (ResolverExport)existing.get(0);
	}
	
	ArrayList getList(String packageName) {
		return (ArrayList)internal.get(packageName);
	}

	void remove(ResolverExport re) {
		ArrayList list = (ArrayList)internal.get(re.getName());
		list.remove(re);
		if(list.size() == 0)
			internal.remove(re.getName());
	}
	
	void clear() {
		internal.clear();
	}
	
	Iterator iterator() {
		return internal.values().iterator();
	}
}
