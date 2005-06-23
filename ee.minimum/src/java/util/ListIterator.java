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
public abstract interface ListIterator extends java.util.Iterator {
	public abstract void add(java.lang.Object var0);
	public abstract boolean hasNext();
	public abstract boolean hasPrevious();
	public abstract java.lang.Object next();
	public abstract int nextIndex();
	public abstract java.lang.Object previous();
	public abstract int previousIndex();
	public abstract void remove();
	public abstract void set(java.lang.Object var0);
}

