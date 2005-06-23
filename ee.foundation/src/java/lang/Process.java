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

