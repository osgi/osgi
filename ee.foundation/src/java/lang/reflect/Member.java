/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.lang.reflect;
public abstract interface Member {
	public abstract java.lang.Class getDeclaringClass();
	public abstract int getModifiers();
	public abstract java.lang.String getName();
	public final static int PUBLIC = 0;
	public final static int DECLARED = 1;
}

