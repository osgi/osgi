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


package org.osgi.test.cases.component.service;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestSet<E> extends AbstractSet<E> /**/ implements Set<E> {
	private final Set<E> set;
	private final StringBuffer	ops;

	public TestSet() {
		set = Collections.synchronizedSet(new LinkedHashSet<E>());
		ops = new StringBuffer();
	}

	public TestSet(Collection< ? extends E> var0) {
		this();
		addAll(var0);
	}

	public StringBuffer getOps() {
		return ops;
	}

	public boolean add(E var0) {
		ops.append("add");
		boolean result = set.add(var0);
//		System.out.printf("TestSet[%X].add(%s) = %b\n", System.identityHashCode(this), var0, result);
		return result;
	}

	public boolean remove(Object var0) {
		ops.append("remove");
		boolean result = set.remove(var0);
//		System.out.printf("TestSet[%X].remove(%s) = %b\n", System.identityHashCode(this), var0, result);
		return result;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public int size() {
		return set.size();
	}

	public boolean contains(Object var0) {
		return set.contains(var0);
	}

	public Iterator<E> iterator() {
		return set.iterator();
	}

	public boolean containsAll(Collection< ? > var0) {
		return set.containsAll(var0);
	}

	public boolean removeAll(Collection< ? > var0) {
		return set.removeAll(var0);
	}

	public boolean retainAll(Collection< ? > var0) {
		return set.retainAll(var0);
	}

	public void clear() {
		set.clear();
	}

	public Object[] toArray() {
		return set.toArray();
	}

	public <T> T[] toArray(T[] var0) {
		return set.toArray(var0);
	}

	public String toString() {
		return set.toString();
	}

	public boolean equals(Object var0) {
		return (var0 == this) || set.equals(var0);
	}

	public int hashCode() {
		return set.hashCode();
	}

}
