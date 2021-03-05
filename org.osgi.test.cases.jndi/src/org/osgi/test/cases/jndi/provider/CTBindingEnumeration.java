/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/


package org.osgi.test.cases.jndi.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author $Id$
 */
public class CTBindingEnumeration implements NamingEnumeration<Binding> {
	
	private List<Binding> bindings = new ArrayList<>();

	public CTBindingEnumeration(Map<String,Object> bindingMap) {
		Set<String> bindingKeys = bindingMap.keySet();
		Iterator<String> iter = bindingKeys.iterator();
		
		while (iter.hasNext()) {
			String name = iter.next();
			Binding binding = new Binding(name, bindingMap.get(name));
			bindings.add(binding);
		}
		
	}

	@Override
	public void close() throws NamingException {
		bindings.clear();
	}

	@Override
	public boolean hasMore() throws NamingException {
		if(!(bindings.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Binding next() throws NamingException {
		Binding next = bindings.get(0);
		bindings.remove(0);
		return next;
	}

	@Override
	public boolean hasMoreElements() {
		if(!(bindings.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Binding nextElement() {
		Binding next = bindings.get(0);
		bindings.remove(0);
		return next;
	}
}
