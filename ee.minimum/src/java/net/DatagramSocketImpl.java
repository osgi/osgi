/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract class DatagramSocketImpl implements java.net.SocketOptions {
	public DatagramSocketImpl() { }
	protected abstract void bind(int var0, java.net.InetAddress var1) throws java.net.SocketException;
	protected abstract void close();
	protected abstract void create() throws java.net.SocketException;
	protected java.io.FileDescriptor getFileDescriptor() { return null; }
	protected int getLocalPort() { return 0; }
	public abstract java.lang.Object getOption(int var0) throws java.net.SocketException;
	protected abstract int getTimeToLive() throws java.io.IOException;
	protected abstract void join(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract void leave(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract int peek(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract void receive(java.net.DatagramPacket var0) throws java.io.IOException;
	protected abstract void send(java.net.DatagramPacket var0) throws java.io.IOException;
	public abstract void setOption(int var0, java.lang.Object var1) throws java.net.SocketException;
	protected abstract void setTimeToLive(int var0) throws java.io.IOException;
	protected java.io.FileDescriptor fd;
	protected int localPort;
}

