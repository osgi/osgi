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
public interface SortedSet<E> extends java.util.Set<E> {
	java.util.Comparator<? super E> comparator();
	E first();
	java.util.SortedSet<E> headSet(E var0);
	E last();
	java.util.SortedSet<E> subSet(E var0, E var1);
	java.util.SortedSet<E> tailSet(E var0);
}

