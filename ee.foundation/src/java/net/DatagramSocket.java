/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.net;
public class DatagramSocket {
    public DatagramSocket() throws java.net.SocketException { }
    public DatagramSocket(int var0) throws java.net.SocketException { }
    public DatagramSocket(int var0, java.net.InetAddress var1) throws java.net.SocketException { }
    public void close() { }
    public void connect(java.net.InetAddress var0, int var1) { }
    public void disconnect() { }
    public java.net.InetAddress getInetAddress() { return null; }
    public java.net.InetAddress getLocalAddress() { return null; }
    public int getLocalPort() { return 0; }
    public int getPort() { return 0; }
    public int getReceiveBufferSize() throws java.net.SocketException { return 0; }
    public int getSendBufferSize() throws java.net.SocketException { return 0; }
    public int getSoTimeout() throws java.net.SocketException { return 0; }
    public void receive(java.net.DatagramPacket var0) throws java.io.IOException { }
    public void send(java.net.DatagramPacket var0) throws java.io.IOException { }
    public void setSendBufferSize(int var0) throws java.net.SocketException { }
    public void setReceiveBufferSize(int var0) throws java.net.SocketException { }
    public void setSoTimeout(int var0) throws java.net.SocketException { }
    public static void setDatagramSocketImplFactory(java.net.DatagramSocketImplFactory var0) throws java.io.IOException { }
}

