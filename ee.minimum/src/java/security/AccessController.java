/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public final class AccessController {
    private AccessController() { }
    public static void checkPermission(java.security.Permission var0) throws java.security.AccessControlException { }
    public static java.security.AccessControlContext getContext() { return null; }
    public static java.lang.Object doPrivileged(java.security.PrivilegedAction var0) { return null; }
    public static java.lang.Object doPrivileged(java.security.PrivilegedAction var0, java.security.AccessControlContext var1) { return null; }
    public static java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction var0) throws java.security.PrivilegedActionException { return null; }
    public static java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction var0, java.security.AccessControlContext var1) throws java.security.PrivilegedActionException { return null; }
}

