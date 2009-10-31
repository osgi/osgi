/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.rmi.ssl;
public class SslRMIServerSocketFactory implements java.rmi.server.RMIServerSocketFactory {
	public SslRMIServerSocketFactory() { } 
	public SslRMIServerSocketFactory(java.lang.String[] var0, java.lang.String[] var1, boolean var2) { } 
	public java.net.ServerSocket createServerSocket(int var0) throws java.io.IOException { return null; }
	public final java.lang.String[] getEnabledCipherSuites() { return null; }
	public final java.lang.String[] getEnabledProtocols() { return null; }
	public final boolean getNeedClientAuth() { return false; }
}

