/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.net;
public abstract class SocketImpl implements java.net.SocketOptions {
    public SocketImpl() { }
    protected abstract void accept(java.net.SocketImpl var0) throws java.io.IOException;
    protected abstract int available() throws java.io.IOException;
    protected abstract void bind(java.net.InetAddress var0, int var1) throws java.io.IOException;
    protected abstract void close() throws java.io.IOException;
    protected abstract void connect(java.lang.String var0, int var1) throws java.io.IOException;
    protected abstract void connect(java.net.InetAddress var0, int var1) throws java.io.IOException;
    protected abstract void create(boolean var0) throws java.io.IOException;
    protected java.io.FileDescriptor getFileDescriptor() { return null; }
    protected java.net.InetAddress getInetAddress() { return null; }
    protected abstract java.io.InputStream getInputStream() throws java.io.IOException;
    protected int getLocalPort() { return 0; }
    public abstract java.lang.Object getOption(int var0) throws java.net.SocketException;
    protected abstract java.io.OutputStream getOutputStream() throws java.io.IOException;
    protected int getPort() { return 0; }
    protected abstract void listen(int var0) throws java.io.IOException;
    public abstract void setOption(int var0, java.lang.Object var1) throws java.net.SocketException;
    public java.lang.String toString() { return null; }
    protected java.net.InetAddress address;
    protected int port;
    protected java.io.FileDescriptor fd;
    protected int localport;
}

