/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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
public abstract class SSLContextSpi {
	public SSLContextSpi() { } 
	protected abstract javax.net.ssl.SSLEngine engineCreateSSLEngine();
	protected abstract javax.net.ssl.SSLEngine engineCreateSSLEngine(java.lang.String var0, int var1);
	protected abstract javax.net.ssl.SSLSessionContext engineGetClientSessionContext();
	protected javax.net.ssl.SSLParameters engineGetDefaultSSLParameters() { return null; }
	protected abstract javax.net.ssl.SSLSessionContext engineGetServerSessionContext();
	protected abstract javax.net.ssl.SSLServerSocketFactory engineGetServerSocketFactory();
	protected abstract javax.net.ssl.SSLSocketFactory engineGetSocketFactory();
	protected javax.net.ssl.SSLParameters engineGetSupportedSSLParameters() { return null; }
	protected abstract void engineInit(javax.net.ssl.KeyManager[] var0, javax.net.ssl.TrustManager[] var1, java.security.SecureRandom var2) throws java.security.KeyManagementException;
}
