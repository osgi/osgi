/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract interface ObjectInputValidation {
	public abstract void validateObject() throws java.io.InvalidObjectException;
}

