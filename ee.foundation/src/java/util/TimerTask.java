/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util;
public abstract class TimerTask implements java.lang.Runnable {
	protected TimerTask() { }
	public boolean cancel() { return false; }
	public long scheduledExecutionTime() { return 0l; }
	public abstract void run();
}

