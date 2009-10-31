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
public abstract class Certificate implements java.io.Serializable {
	protected static class CertificateRep implements java.io.Serializable {
		protected CertificateRep(java.lang.String var0, byte[] var1) { } 
		protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
	}
	protected Certificate(java.lang.String var0) { } 
	public abstract byte[] getEncoded() throws java.security.cert.CertificateEncodingException;
	public abstract java.security.PublicKey getPublicKey();
	public final java.lang.String getType() { return null; }
	public abstract java.lang.String toString();
	public abstract void verify(java.security.PublicKey var0) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException, java.security.cert.CertificateException;
	public abstract void verify(java.security.PublicKey var0, java.lang.String var1) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException, java.security.cert.CertificateException;
	protected java.lang.Object writeReplace() throws java.io.ObjectStreamException { return null; }
}

