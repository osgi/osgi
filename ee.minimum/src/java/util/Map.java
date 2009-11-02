/*
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package java.util;
public interface Map<K,V> {
	public interface Entry<K,V> {
		boolean equals(java.lang.Object var0);
		K getKey();
		V getValue();
		int hashCode();
		V setValue(V var0);
	}
	void clear();
	boolean containsKey(java.lang.Object var0);
	boolean containsValue(java.lang.Object var0);
	java.util.Set<java.util.Map.Entry<K,V>> entrySet();
	boolean equals(java.lang.Object var0);
	V get(java.lang.Object var0);
	int hashCode();
	boolean isEmpty();
	java.util.Set<K> keySet();
	V put(K var0, V var1);
	void putAll(java.util.Map<? extends K,? extends V> var0);
	V remove(java.lang.Object var0);
	int size();
	java.util.Collection<V> values();
}

