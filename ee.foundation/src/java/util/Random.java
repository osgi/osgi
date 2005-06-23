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
public class Random implements java.io.Serializable {
	public Random() { }
	public Random(long var0) { }
	protected int next(int var0) { return 0; }
	public boolean nextBoolean() { return false; }
	public void nextBytes(byte[] var0) { }
	public double nextDouble() { return 0.0d; }
	public float nextFloat() { return 0.0f; }
	public double nextGaussian() { return 0.0d; }
	public int nextInt() { return 0; }
	public int nextInt(int var0) { return 0; }
	public long nextLong() { return 0l; }
	public void setSeed(long var0) { }
}

