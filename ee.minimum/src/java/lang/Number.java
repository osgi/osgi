/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.lang;
public abstract class Number implements java.io.Serializable {
    public Number() { }
    public byte byteValue() { return 0; }
    public abstract double doubleValue();
    public abstract float floatValue();
    public abstract int intValue();
    public abstract long longValue();
    public short shortValue() { return 0; }
}

