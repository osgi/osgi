/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract interface SocketImplFactory {
	public abstract java.net.SocketImpl createSocketImpl();
}

