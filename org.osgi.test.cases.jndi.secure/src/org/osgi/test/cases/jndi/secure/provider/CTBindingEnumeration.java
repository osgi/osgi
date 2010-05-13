/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.jndi.secure.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @version $Id$
 */
public class CTBindingEnumeration implements NamingEnumeration {
	
	private ArrayList bindings = new ArrayList();

	public CTBindingEnumeration(Map bindingMap) {
		Set bindingKeys = bindingMap.keySet();
		Iterator iter = bindingKeys.iterator();
		
		while (iter.hasNext()) {
			Binding binding = new Binding(iter.next().toString(), bindingMap.get(iter.next().toString()));
			bindings.add(binding);
		}
		
	}

	public void close() throws NamingException {
		bindings.clear();
	}

	public boolean hasMore() throws NamingException {
		if(!(bindings.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	public Object next() throws NamingException {
		Object next = bindings.get(0);
		bindings.remove(0);
		return next;
	}

	public boolean hasMoreElements() {
		if(!(bindings.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	public Object nextElement() {
		Object next = bindings.get(0);
		bindings.remove(0);
		return next;
	}
}
