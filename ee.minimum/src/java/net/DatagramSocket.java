/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

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
}

