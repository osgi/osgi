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
public interface Collection {
	boolean add(java.lang.Object var0);
	boolean addAll(java.util.Collection var0);
	void clear();
	boolean contains(java.lang.Object var0);
	boolean containsAll(java.util.Collection var0);
	boolean equals(java.lang.Object var0);
	int hashCode();
	boolean isEmpty();
	java.util.Iterator iterator();
	boolean remove(java.lang.Object var0);
	boolean removeAll(java.util.Collection var0);
	boolean retainAll(java.util.Collection var0);
	int size();
	java.lang.Object[] toArray();
	java.lang.Object[] toArray(java.lang.Object[] var0);
}

