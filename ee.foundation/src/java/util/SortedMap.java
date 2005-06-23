/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package java.util;
public abstract interface SortedMap extends java.util.Map {
	public abstract java.util.Comparator comparator();
	public abstract java.lang.Object firstKey();
	public abstract java.util.SortedMap headMap(java.lang.Object var0);
	public abstract java.lang.Object lastKey();
	public abstract java.util.SortedMap subMap(java.lang.Object var0, java.lang.Object var1);
	public abstract java.util.SortedMap tailMap(java.lang.Object var0);
}

