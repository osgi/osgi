/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.lang.reflect;
public class Proxy implements java.io.Serializable {
    private Proxy() { }
    protected Proxy(java.lang.reflect.InvocationHandler var0) { }
    public static java.lang.Class getProxyClass(java.lang.ClassLoader var0, java.lang.Class[] var1) throws java.lang.IllegalArgumentException { return null; }
    public static java.lang.Object newProxyInstance(java.lang.ClassLoader var0, java.lang.Class[] var1, java.lang.reflect.InvocationHandler var2) throws java.lang.IllegalArgumentException { return null; }
    public static boolean isProxyClass(java.lang.Class var0) { return false; }
    public static java.lang.reflect.InvocationHandler getInvocationHandler(java.lang.Object var0) throws java.lang.IllegalArgumentException { return null; }
    protected java.lang.reflect.InvocationHandler h;
}

