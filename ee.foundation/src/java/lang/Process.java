/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.lang;
public abstract class Process {
	public Process() { }
	public abstract void destroy();
	public abstract int exitValue();
	public abstract java.io.InputStream getErrorStream();
	public abstract java.io.InputStream getInputStream();
	public abstract java.io.OutputStream getOutputStream();
	public abstract int waitFor() throws java.lang.InterruptedException;
}

