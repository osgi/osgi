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

package java.security.cert;
public class CertStore {
	protected CertStore(java.security.cert.CertStoreSpi var0, java.security.Provider var1, java.lang.String var2, java.security.cert.CertStoreParameters var3) { } 
	public final java.util.Collection<? extends java.security.cert.CRL> getCRLs(java.security.cert.CRLSelector var0) throws java.security.cert.CertStoreException { return null; }
	public final java.security.cert.CertStoreParameters getCertStoreParameters() { return null; }
	public final java.util.Collection<? extends java.security.cert.Certificate> getCertificates(java.security.cert.CertSelector var0) throws java.security.cert.CertStoreException { return null; }
	public final static java.lang.String getDefaultType() { return null; }
	public static java.security.cert.CertStore getInstance(java.lang.String var0, java.security.cert.CertStoreParameters var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException { return null; }
	public static java.security.cert.CertStore getInstance(java.lang.String var0, java.security.cert.CertStoreParameters var1, java.lang.String var2) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public static java.security.cert.CertStore getInstance(java.lang.String var0, java.security.cert.CertStoreParameters var1, java.security.Provider var2) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final java.lang.String getType() { return null; }
}

