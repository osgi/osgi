/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public class OptionalDataException extends java.io.ObjectStreamException {
	public boolean eof;
	public int length;
	private OptionalDataException() { } /* generated constructor to prevent compiler adding default public constructor */
}

