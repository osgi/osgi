/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util;
public abstract interface Map {
    public abstract void clear();
    public abstract boolean containsKey(java.lang.Object var0);
    public abstract boolean containsValue(java.lang.Object var0);
    public abstract java.util.Set entrySet();
    public abstract boolean equals(java.lang.Object var0);
    public abstract java.lang.Object get(java.lang.Object var0);
    public abstract int hashCode();
    public abstract boolean isEmpty();
    public abstract java.util.Set keySet();
    public abstract java.lang.Object put(java.lang.Object var0, java.lang.Object var1);
    public abstract void putAll(java.util.Map var0);
    public abstract java.lang.Object remove(java.lang.Object var0);
    public abstract int size();
    public abstract java.util.Collection values();
    public abstract static interface Entry {
        public abstract boolean equals(java.lang.Object var0);
        public abstract java.lang.Object getKey();
        public abstract java.lang.Object getValue();
        public abstract int hashCode();
        public abstract java.lang.Object setValue(java.lang.Object var0);
    }
}

