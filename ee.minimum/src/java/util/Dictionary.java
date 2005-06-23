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
public abstract class Dictionary {
	public Dictionary() { }
	public abstract java.util.Enumeration elements();
	public abstract java.lang.Object get(java.lang.Object var0);
	public abstract boolean isEmpty();
	public abstract java.util.Enumeration keys();
	public abstract java.lang.Object put(java.lang.Object var0, java.lang.Object var1);
	public abstract java.lang.Object remove(java.lang.Object var0);
	public abstract int size();
}

