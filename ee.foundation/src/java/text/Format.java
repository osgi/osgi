/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.text;
public abstract class Format implements java.io.Serializable, java.lang.Cloneable {
    public Format() { }
    public java.lang.Object clone() { return null; }
    public final java.lang.String format(java.lang.Object var0) { return null; }
    public abstract java.lang.StringBuffer format(java.lang.Object var0, java.lang.StringBuffer var1, java.text.FieldPosition var2);
    public java.lang.Object parseObject(java.lang.String var0) throws java.text.ParseException { return null; }
    public abstract java.lang.Object parseObject(java.lang.String var0, java.text.ParsePosition var1);
}

