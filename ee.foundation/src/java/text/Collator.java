/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.text;
public abstract class Collator implements java.util.Comparator, java.lang.Cloneable {
    protected Collator() { }
    public java.lang.Object clone() { return null; }
    public int compare(java.lang.Object var0, java.lang.Object var1) { return 0; }
    public abstract int compare(java.lang.String var0, java.lang.String var1);
    public boolean equals(java.lang.Object var0) { return false; }
    public boolean equals(java.lang.String var0, java.lang.String var1) { return false; }
    public static java.util.Locale[] getAvailableLocales() { return null; }
    public abstract java.text.CollationKey getCollationKey(java.lang.String var0);
    public int getDecomposition() { return 0; }
    public static java.text.Collator getInstance() { return null; }
    public static java.text.Collator getInstance(java.util.Locale var0) { return null; }
    public int getStrength() { return 0; }
    public abstract int hashCode();
    public void setDecomposition(int var0) { }
    public void setStrength(int var0) { }
    public final static int NO_DECOMPOSITION = 0;
    public final static int CANONICAL_DECOMPOSITION = 1;
    public final static int FULL_DECOMPOSITION = 2;
    public final static int PRIMARY = 0;
    public final static int SECONDARY = 1;
    public final static int TERTIARY = 2;
    public final static int IDENTICAL = 3;
}

