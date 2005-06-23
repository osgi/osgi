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

package java.security.interfaces;
public abstract interface DSAPrivateKey extends java.security.interfaces.DSAKey, java.security.PrivateKey {
	public abstract java.math.BigInteger getX();
	public final static long serialVersionUID = 7776497482533790279l;
}

