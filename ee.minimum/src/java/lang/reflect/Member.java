/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.lang.reflect;
public abstract interface Member {
    public abstract java.lang.Class getDeclaringClass();
    public abstract int getModifiers();
    public abstract java.lang.String getName();
    public final static int PUBLIC = 0;
    public final static int DECLARED = 1;
}

