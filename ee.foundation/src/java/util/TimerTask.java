/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util;
public abstract class TimerTask implements java.lang.Runnable {
    protected TimerTask() { }
    public boolean cancel() { return false; }
    public long scheduledExecutionTime() { return 0l; }
    public abstract void run();
}

