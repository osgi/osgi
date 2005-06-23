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
public abstract interface Collection {
	public abstract boolean add(java.lang.Object var0);
	public abstract boolean addAll(java.util.Collection var0);
	public abstract void clear();
	public abstract boolean contains(java.lang.Object var0);
	public abstract boolean containsAll(java.util.Collection var0);
	public abstract boolean equals(java.lang.Object var0);
	public abstract int hashCode();
	public abstract boolean isEmpty();
	public abstract java.util.Iterator iterator();
	public abstract boolean remove(java.lang.Object var0);
	public abstract boolean removeAll(java.util.Collection var0);
	public abstract boolean retainAll(java.util.Collection var0);
	public abstract int size();
	public abstract java.lang.Object[] toArray();
	public abstract java.lang.Object[] toArray(java.lang.Object[] var0);
}

