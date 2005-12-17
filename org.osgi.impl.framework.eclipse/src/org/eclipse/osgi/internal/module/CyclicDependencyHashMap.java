/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.module;

import java.util.ArrayList;
import java.util.HashMap;

public class CyclicDependencyHashMap {
	private HashMap internal = new HashMap();

	// Cyclic dependencies
	Object put(ResolverBundle dependentOn, ResolverBundle module) {
		ArrayList existing = (ArrayList) internal.get(dependentOn);
		if (existing == null) {
			ArrayList v = new ArrayList();
			v.add(module);
			internal.put(dependentOn, v);
		} else {
			if (!existing.contains(module)) {
				existing.add(module);
			}
		}
		return null;
	}

	ArrayList remove(ResolverBundle dependentOn) {
		return (ArrayList) internal.remove((Object) dependentOn);
	}
}
