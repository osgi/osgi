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
public interface Map {
	public interface Entry {
		boolean equals(java.lang.Object var0);
		java.lang.Object getKey();
		java.lang.Object getValue();
		int hashCode();
		java.lang.Object setValue(java.lang.Object var0);
	}
	void clear();
	boolean containsKey(java.lang.Object var0);
	boolean containsValue(java.lang.Object var0);
	java.util.Set entrySet();
	boolean equals(java.lang.Object var0);
	java.lang.Object get(java.lang.Object var0);
	int hashCode();
	boolean isEmpty();
	java.util.Set keySet();
	java.lang.Object put(java.lang.Object var0, java.lang.Object var1);
	void putAll(java.util.Map var0);
	java.lang.Object remove(java.lang.Object var0);
	int size();
	java.util.Collection values();
}

