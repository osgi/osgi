/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

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

