/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.text;
public abstract interface CharacterIterator extends java.lang.Cloneable {
    public abstract java.lang.Object clone();
    public abstract char current();
    public abstract char first();
    public abstract int getBeginIndex();
    public abstract int getEndIndex();
    public abstract int getIndex();
    public abstract char last();
    public abstract char next();
    public abstract char previous();
    public abstract char setIndex(int var0);
    public final static char DONE = 65535;
}

