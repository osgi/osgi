/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public abstract interface Checksum {
    public abstract long getValue();
    public abstract void reset();
    public abstract void update(int var0);
    public abstract void update(byte[] var0, int var1, int var2);
}

