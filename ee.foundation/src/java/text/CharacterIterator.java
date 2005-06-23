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

package java.text;
public abstract interface CharacterIterator extends java.lang.Cloneable {
	public abstract java.lang.Object clone();
	public abstract char current();
	public abstract char first();
	public abstract int getBeginIndex();
	public abstract int getEndIndex();
	public abstract int getIndex();
	public abstract char last();
	public abstract char next();
	public abstract char previous();
	public abstract char setIndex(int var0);
	public final static char DONE = 65535;
}

