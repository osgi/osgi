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
public abstract interface SortedSet extends java.util.Set {
	public abstract java.util.Comparator comparator();
	public abstract java.lang.Object first();
	public abstract java.util.SortedSet headSet(java.lang.Object var0);
	public abstract java.lang.Object last();
	public abstract java.util.SortedSet subSet(java.lang.Object var0, java.lang.Object var1);
	public abstract java.util.SortedSet tailSet(java.lang.Object var0);
}

