/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.lang.ref;
public abstract class Reference {
	public void clear() { }
	public boolean enqueue() { return false; }
	public java.lang.Object get() { return null; }
	public boolean isEnqueued() { return false; }
	Reference() { } /* generated constructor to prevent compiler adding default public constructor */
}

