/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.lang.ref;
public abstract class Reference {
    public void clear() { }
    public boolean enqueue() { return false; }
    public java.lang.Object get() { return null; }
    public boolean isEnqueued() { return false; }
    Reference() { }
}

