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

package java.security;
public class KeyStore {
	public static abstract class Builder {
		protected Builder() { } 
		public abstract java.security.KeyStore getKeyStore() throws java.security.KeyStoreException;
		public abstract java.security.KeyStore.ProtectionParameter getProtectionParameter(java.lang.String var0) throws java.security.KeyStoreException;
		public static java.security.KeyStore.Builder newInstance(java.lang.String var0, java.security.Provider var1, java.io.File var2, java.security.KeyStore.ProtectionParameter var3) { return null; }
		public static java.security.KeyStore.Builder newInstance(java.lang.String var0, java.security.Provider var1, java.security.KeyStore.ProtectionParameter var2) { return null; }
		public static java.security.KeyStore.Builder newInstance(java.security.KeyStore var0, java.security.KeyStore.ProtectionParameter var1) { return null; }
	}
	public static class CallbackHandlerProtection implements java.security.KeyStore.ProtectionParameter {
		public CallbackHandlerProtection(javax.security.auth.callback.CallbackHandler var0) { } 
		public javax.security.auth.callback.CallbackHandler getCallbackHandler() { return null; }
	}
	public interface Entry {
	}
	public interface LoadStoreParameter {
		java.security.KeyStore.ProtectionParameter getProtectionParameter();
	}
	public static class PasswordProtection implements java.security.KeyStore.ProtectionParameter, javax.security.auth.Destroyable {
		public PasswordProtection(char[] var0) { } 
		public void destroy() throws javax.security.auth.DestroyFailedException { }
		public char[] getPassword() { return null; }
		public boolean isDestroyed() { return false; }
	}
	public static final class PrivateKeyEntry implements java.security.KeyStore.Entry {
		public PrivateKeyEntry(java.security.PrivateKey var0, java.security.cert.Certificate[] var1) { } 
		public java.security.cert.Certificate getCertificate() { return null; }
		public java.security.cert.Certificate[] getCertificateChain() { return null; }
		public java.security.PrivateKey getPrivateKey() { return null; }
	}
	public interface ProtectionParameter {
	}
	public static final class SecretKeyEntry implements java.security.KeyStore.Entry {
		public SecretKeyEntry(javax.crypto.SecretKey var0) { } 
		public javax.crypto.SecretKey getSecretKey() { return null; }
	}
	public static final class TrustedCertificateEntry implements java.security.KeyStore.Entry {
		public TrustedCertificateEntry(java.security.cert.Certificate var0) { } 
		public java.security.cert.Certificate getTrustedCertificate() { return null; }
	}
	protected KeyStore(java.security.KeyStoreSpi var0, java.security.Provider var1, java.lang.String var2) { } 
	public final java.util.Enumeration<java.lang.String> aliases() throws java.security.KeyStoreException { return null; }
	public final boolean containsAlias(java.lang.String var0) throws java.security.KeyStoreException { return false; }
	public final void deleteEntry(java.lang.String var0) throws java.security.KeyStoreException { }
	public final boolean entryInstanceOf(java.lang.String var0, java.lang.Class<? extends java.security.KeyStore.Entry> var1) throws java.security.KeyStoreException { return false; }
	public final java.security.cert.Certificate getCertificate(java.lang.String var0) throws java.security.KeyStoreException { return null; }
	public final java.lang.String getCertificateAlias(java.security.cert.Certificate var0) throws java.security.KeyStoreException { return null; }
	public final java.security.cert.Certificate[] getCertificateChain(java.lang.String var0) throws java.security.KeyStoreException { return null; }
	public final java.util.Date getCreationDate(java.lang.String var0) throws java.security.KeyStoreException { return null; }
	public final static java.lang.String getDefaultType() { return null; }
	public final java.security.KeyStore.Entry getEntry(java.lang.String var0, java.security.KeyStore.ProtectionParameter var1) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableEntryException { return null; }
	public static java.security.KeyStore getInstance(java.lang.String var0) throws java.security.KeyStoreException { return null; }
	public static java.security.KeyStore getInstance(java.lang.String var0, java.lang.String var1) throws java.security.KeyStoreException, java.security.NoSuchProviderException { return null; }
	public static java.security.KeyStore getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.KeyStoreException { return null; }
	public final java.security.Key getKey(java.lang.String var0, char[] var1) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final java.lang.String getType() { return null; }
	public final boolean isCertificateEntry(java.lang.String var0) throws java.security.KeyStoreException { return false; }
	public final boolean isKeyEntry(java.lang.String var0) throws java.security.KeyStoreException { return false; }
	public final void load(java.io.InputStream var0, char[] var1) throws java.io.IOException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException { }
	public final void load(java.security.KeyStore.LoadStoreParameter var0) throws java.io.IOException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException { }
	public final void setCertificateEntry(java.lang.String var0, java.security.cert.Certificate var1) throws java.security.KeyStoreException { }
	public final void setEntry(java.lang.String var0, java.security.KeyStore.Entry var1, java.security.KeyStore.ProtectionParameter var2) throws java.security.KeyStoreException { }
	public final void setKeyEntry(java.lang.String var0, java.security.Key var1, char[] var2, java.security.cert.Certificate[] var3) throws java.security.KeyStoreException { }
	public final void setKeyEntry(java.lang.String var0, byte[] var1, java.security.cert.Certificate[] var2) throws java.security.KeyStoreException { }
	public final int size() throws java.security.KeyStoreException { return 0; }
	public final void store(java.io.OutputStream var0, char[] var1) throws java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException { }
	public final void store(java.security.KeyStore.LoadStoreParameter var0) throws java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException { }
}

