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

package javax.net.ssl;
public abstract class SSLSocketFactory extends javax.net.SocketFactory {
	public SSLSocketFactory() { } 
	public abstract java.net.Socket createSocket(java.net.Socket var0, java.lang.String var1, int var2, boolean var3) throws java.io.IOException;
	public static javax.net.SocketFactory getDefault() { return null; }
	public abstract java.lang.String[] getDefaultCipherSuites();
	public abstract java.lang.String[] getSupportedCipherSuites();
}

