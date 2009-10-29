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

package java.security.cert;
public abstract class CertPath implements java.io.Serializable {
	protected CertPath(java.lang.String var0) { }
	public abstract java.util.List getCertificates();
	public abstract byte[] getEncoded() throws java.security.cert.CertificateEncodingException;
	public abstract byte[] getEncoded(java.lang.String var0) throws java.security.cert.CertificateEncodingException;
	public abstract java.util.Iterator getEncodings();
	public java.lang.String getType() { return null; }
	public int hashCode() { return 0; }
	protected java.lang.Object writeReplace() throws java.io.ObjectStreamException { return null; }
	protected static class CertPathRep implements java.io.Serializable {
		protected CertPathRep(java.lang.String var0, byte[] var1) { }
		protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
	}
}

