/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract interface SocketOptions {
	public abstract java.lang.Object getOption(int var0) throws java.net.SocketException;
	public abstract void setOption(int var0, java.lang.Object var1) throws java.net.SocketException;
	public final static int SO_LINGER = 128;
	public final static int SO_TIMEOUT = 4102;
	public final static int TCP_NODELAY = 1;
	public final static int IP_MULTICAST_IF = 16;
	public final static int SO_BINDADDR = 15;
	public final static int SO_REUSEADDR = 4;
	public final static int SO_SNDBUF = 4097;
	public final static int SO_RCVBUF = 4098;
	public final static int SO_KEEPALIVE = 8;
}

