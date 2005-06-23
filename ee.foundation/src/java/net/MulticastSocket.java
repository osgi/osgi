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
public class MulticastSocket extends java.net.DatagramSocket {
	public MulticastSocket() throws java.io.IOException { }
	public MulticastSocket(int var0) throws java.io.IOException { }
	public java.net.InetAddress getInterface() throws java.net.SocketException { return null; }
	public int getTimeToLive() throws java.io.IOException { return 0; }
	public void joinGroup(java.net.InetAddress var0) throws java.io.IOException { }
	public void leaveGroup(java.net.InetAddress var0) throws java.io.IOException { }
	public void send(java.net.DatagramPacket var0, byte var1) throws java.io.IOException { }
	public void setInterface(java.net.InetAddress var0) throws java.net.SocketException { }
	public void setTimeToLive(int var0) throws java.io.IOException { }
}

