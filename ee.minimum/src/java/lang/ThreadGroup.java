/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.lang;
public class ThreadGroup {
	public ThreadGroup(java.lang.String var0) { }
	public ThreadGroup(java.lang.ThreadGroup var0, java.lang.String var1) { }
	public int activeCount() { return 0; }
	public int activeGroupCount() { return 0; }
	public final void checkAccess() { }
	public final void destroy() { }
	public int enumerate(java.lang.Thread[] var0) { return 0; }
	public int enumerate(java.lang.Thread[] var0, boolean var1) { return 0; }
	public int enumerate(java.lang.ThreadGroup[] var0) { return 0; }
	public int enumerate(java.lang.ThreadGroup[] var0, boolean var1) { return 0; }
	public final int getMaxPriority() { return 0; }
	public final java.lang.String getName() { return null; }
	public final java.lang.ThreadGroup getParent() { return null; }
	public final boolean isDaemon() { return false; }
	public boolean isDestroyed() { return false; }
	public void list() { }
	public final boolean parentOf(java.lang.ThreadGroup var0) { return false; }
	public final void setDaemon(boolean var0) { }
	public final void setMaxPriority(int var0) { }
	public java.lang.String toString() { return null; }
	public void uncaughtException(java.lang.Thread var0, java.lang.Throwable var1) { }
}

