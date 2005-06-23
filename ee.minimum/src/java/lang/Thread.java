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
public class Thread implements java.lang.Runnable {
	public Thread() { }
	public Thread(java.lang.Runnable var0) { }
	public Thread(java.lang.Runnable var0, java.lang.String var1) { }
	public Thread(java.lang.String var0) { }
	public Thread(java.lang.ThreadGroup var0, java.lang.Runnable var1) { }
	public Thread(java.lang.ThreadGroup var0, java.lang.Runnable var1, java.lang.String var2) { }
	public Thread(java.lang.ThreadGroup var0, java.lang.String var1) { }
	public static int activeCount() { return 0; }
	public final void checkAccess() { }
	public static java.lang.Thread currentThread() { return null; }
	public java.lang.ClassLoader getContextClassLoader() { return null; }
	public final java.lang.String getName() { return null; }
	public final int getPriority() { return 0; }
	public final java.lang.ThreadGroup getThreadGroup() { return null; }
	public void interrupt() { }
	public static boolean interrupted() { return false; }
	public final boolean isAlive() { return false; }
	public final boolean isDaemon() { return false; }
	public boolean isInterrupted() { return false; }
	public final void join() throws java.lang.InterruptedException { }
	public final void join(long var0) throws java.lang.InterruptedException { }
	public final void join(long var0, int var1) throws java.lang.InterruptedException { }
	public void run() { }
	public void setContextClassLoader(java.lang.ClassLoader var0) { }
	public final void setDaemon(boolean var0) { }
	public final void setName(java.lang.String var0) { }
	public final void setPriority(int var0) { }
	public static void sleep(long var0) throws java.lang.InterruptedException { }
	public static void sleep(long var0, int var1) throws java.lang.InterruptedException { }
	public void start() { }
	public java.lang.String toString() { return null; }
	public static void yield() { }
	public final static int MAX_PRIORITY = 10;
	public final static int MIN_PRIORITY = 1;
	public final static int NORM_PRIORITY = 5;
}

