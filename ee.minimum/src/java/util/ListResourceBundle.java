/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util;
public abstract class ListResourceBundle extends java.util.ResourceBundle {
    public ListResourceBundle() { }
    protected abstract java.lang.Object[][] getContents();
    public java.util.Enumeration getKeys() { return null; }
    public final java.lang.Object handleGetObject(java.lang.String var0) { return null; }
}

