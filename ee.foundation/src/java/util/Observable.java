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

package java.util;
public class Observable {
	public Observable() { }
	public void addObserver(java.util.Observer var0) { }
	protected void clearChanged() { }
	public int countObservers() { return 0; }
	public void deleteObserver(java.util.Observer var0) { }
	public void deleteObservers() { }
	public boolean hasChanged() { return false; }
	public void notifyObservers() { }
	public void notifyObservers(java.lang.Object var0) { }
	protected void setChanged() { }
}

