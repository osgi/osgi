/*
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
public interface NavigableMap<K,V> extends java.util.SortedMap<K,V> {
	java.util.Map.Entry<K,V> ceilingEntry(K var0);
	K ceilingKey(K var0);
	java.util.NavigableSet<K> descendingKeySet();
	java.util.NavigableMap<K,V> descendingMap();
	java.util.Map.Entry<K,V> firstEntry();
	java.util.Map.Entry<K,V> floorEntry(K var0);
	K floorKey(K var0);
	java.util.NavigableMap<K,V> headMap(K var0, boolean var1);
	java.util.Map.Entry<K,V> higherEntry(K var0);
	K higherKey(K var0);
	java.util.Map.Entry<K,V> lastEntry();
	java.util.Map.Entry<K,V> lowerEntry(K var0);
	K lowerKey(K var0);
	java.util.NavigableSet<K> navigableKeySet();
	java.util.Map.Entry<K,V> pollFirstEntry();
	java.util.Map.Entry<K,V> pollLastEntry();
	java.util.NavigableMap<K,V> subMap(K var0, boolean var1, K var2, boolean var3);
	java.util.NavigableMap<K,V> tailMap(K var0, boolean var1);
}

