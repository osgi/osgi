/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract interface Key extends java.io.Serializable {
	public abstract java.lang.String getAlgorithm();
	public abstract byte[] getEncoded();
	public abstract java.lang.String getFormat();
	public final static long serialVersionUID = 6603384152749567654l;
}

