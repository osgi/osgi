/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util.zip;
public abstract interface Checksum {
    public abstract long getValue();
    public abstract void reset();
    public abstract void update(int var0);
    public abstract void update(byte[] var0, int var1, int var2);
}

