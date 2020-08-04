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


package org.osgi.test.cases.jndi.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author $Id$
 */
public class CTNameClassPairEnumeration
		implements NamingEnumeration<NameClassPair> {

	
	private List<NameClassPair> nameClassPair = new ArrayList<>();

	public CTNameClassPairEnumeration(Map<String,Object> bindingMap) {
		Set<String> bindingKeys = bindingMap.keySet();
		Iterator<String> iter = bindingKeys.iterator();
		
		while (iter.hasNext()) {
			String name = iter.next();
			String className = bindingMap.get(name).getClass().getName();
			nameClassPair.add(new NameClassPair(name, className));
		}
		
	}

	@Override
	public void close() throws NamingException {
		nameClassPair.clear();
	}

	@Override
	public boolean hasMore() throws NamingException {
		if(!(nameClassPair.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public NameClassPair next() throws NamingException {
		NameClassPair next = nameClassPair.get(0);
		nameClassPair.remove(0);
		return next;
	}

	@Override
	public boolean hasMoreElements() {
		if(!(nameClassPair.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public NameClassPair nextElement() {
		NameClassPair next = nameClassPair.get(0);
		nameClassPair.remove(0);
		return next;
	}

}
