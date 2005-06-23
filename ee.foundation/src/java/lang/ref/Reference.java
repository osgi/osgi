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

package java.lang.ref;
public abstract class Reference {
	public void clear() { }
	public boolean enqueue() { return false; }
	public java.lang.Object get() { return null; }
	public boolean isEnqueued() { return false; }
	Reference() { } /* generated constructor to prevent compiler adding default public constructor */
}

