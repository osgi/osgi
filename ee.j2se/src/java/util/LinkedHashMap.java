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
public class LinkedHashMap<K,V> extends java.util.HashMap<K,V> implements java.util.Map<K,V> {
	public LinkedHashMap() { } 
	public LinkedHashMap(int var0) { } 
	public LinkedHashMap(int var0, float var1) { } 
	public LinkedHashMap(int var0, float var1, boolean var2) { } 
	public LinkedHashMap(java.util.Map<? extends K,? extends V> var0) { } 
	protected boolean removeEldestEntry(java.util.Map.Entry<K,V> var0) { return false; }
}

