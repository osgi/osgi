/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.net;
public class ServerSocket {
    public ServerSocket(int var0) throws java.io.IOException { }
    public ServerSocket(int var0, int var1) throws java.io.IOException { }
    public ServerSocket(int var0, int var1, java.net.InetAddress var2) throws java.io.IOException { }
    public java.net.Socket accept() throws java.io.IOException { return null; }
    public void close() throws java.io.IOException { }
    public java.net.InetAddress getInetAddress() { return null; }
    public int getLocalPort() { return 0; }
    public int getSoTimeout() throws java.io.IOException { return 0; }
    protected final void implAccept(java.net.Socket var0) throws java.io.IOException { }
    public static void setSocketFactory(java.net.SocketImplFactory var0) throws java.io.IOException { }
    public void setSoTimeout(int var0) throws java.net.SocketException { }
    public java.lang.String toString() { return null; }
}

