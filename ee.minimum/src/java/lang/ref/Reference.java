/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
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

