/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.interfaces;
public abstract interface DSAPublicKey extends java.security.interfaces.DSAKey, java.security.PublicKey {
	public abstract java.math.BigInteger getY();
	public final static long serialVersionUID = 1234526332779022332l;
}

