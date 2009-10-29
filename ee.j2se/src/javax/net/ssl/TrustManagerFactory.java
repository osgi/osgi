/*
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

package javax.net.ssl;
public class TrustManagerFactory {
	protected TrustManagerFactory(javax.net.ssl.TrustManagerFactorySpi var0, java.security.Provider var1, java.lang.String var2) { }
	public final java.lang.String getAlgorithm() { return null; }
	public final static java.lang.String getDefaultAlgorithm() { return null; }
	public final static javax.net.ssl.TrustManagerFactory getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public final static javax.net.ssl.TrustManagerFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final static javax.net.ssl.TrustManagerFactory getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.NoSuchAlgorithmException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final javax.net.ssl.TrustManager[] getTrustManagers() { return null; }
	public final void init(java.security.KeyStore var0) throws java.security.KeyStoreException { }
	public final void init(javax.net.ssl.ManagerFactoryParameters var0) throws java.security.InvalidAlgorithmParameterException { }
}

