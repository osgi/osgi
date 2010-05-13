/*
 * $Id$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.microedition.io;
public abstract interface SocketConnection extends javax.microedition.io.StreamConnection {
	public abstract java.lang.String getAddress() throws java.io.IOException;
	public abstract java.lang.String getLocalAddress() throws java.io.IOException;
	public abstract int getLocalPort() throws java.io.IOException;
	public abstract int getPort() throws java.io.IOException;
	public abstract int getSocketOption(byte var0) throws java.io.IOException;
	public abstract void setSocketOption(byte var0, int var1) throws java.io.IOException;
	public final static byte DELAY = 0;
	public final static byte KEEPALIVE = 2;
	public final static byte LINGER = 1;
	public final static byte RCVBUF = 3;
	public final static byte SNDBUF = 4;
}

