/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.interfaces;
public abstract interface DSAPrivateKey extends java.security.interfaces.DSAKey, java.security.PrivateKey {
	public abstract java.math.BigInteger getX();
	public final static long serialVersionUID = 7776497482533790279l;
}

