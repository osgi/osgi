/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.interfaces;
public abstract interface DSAParams {
	public abstract java.math.BigInteger getP();
	public abstract java.math.BigInteger getQ();
	public abstract java.math.BigInteger getG();
}

