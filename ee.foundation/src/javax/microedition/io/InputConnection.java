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

package javax.microedition.io;
public abstract interface InputConnection extends javax.microedition.io.Connection {
	public abstract java.io.DataInputStream openDataInputStream() throws java.io.IOException;
	public abstract java.io.InputStream openInputStream() throws java.io.IOException;
}

