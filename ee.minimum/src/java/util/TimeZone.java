/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.util;
public abstract class TimeZone implements java.io.Serializable, java.lang.Cloneable {
    public TimeZone() { }
    public java.lang.Object clone() { return null; }
    public static java.lang.String[] getAvailableIDs() { return null; }
    public static java.lang.String[] getAvailableIDs(int var0) { return null; }
    public static java.util.TimeZone getDefault() { return null; }
    public java.lang.String getID() { return null; }
    public abstract int getOffset(int var0, int var1, int var2, int var3, int var4, int var5);
    public abstract int getRawOffset();
    public static java.util.TimeZone getTimeZone(java.lang.String var0) { return null; }
    public abstract boolean inDaylightTime(java.util.Date var0);
    public static void setDefault(java.util.TimeZone var0) { }
    public void setID(java.lang.String var0) { }
    public abstract void setRawOffset(int var0);
    public abstract boolean useDaylightTime();
}

