/*
 * $Date$
 *
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
public class X509CertSelector implements java.security.cert.CertSelector {
	public X509CertSelector() { }
	public void addPathToName(int var0, java.lang.String var1) throws java.io.IOException { }
	public void addPathToName(int var0, byte[] var1) throws java.io.IOException { }
	public void addSubjectAlternativeName(int var0, java.lang.String var1) throws java.io.IOException { }
	public void addSubjectAlternativeName(int var0, byte[] var1) throws java.io.IOException { }
	public java.lang.Object clone() { return null; }
	public byte[] getAuthorityKeyIdentifier() { return null; }
	public int getBasicConstraints() { return 0; }
	public java.security.cert.X509Certificate getCertificate() { return null; }
	public java.util.Date getCertificateValid() { return null; }
	public java.util.Set getExtendedKeyUsage() { return null; }
	public byte[] getIssuerAsBytes() throws java.io.IOException { return null; }
	public java.lang.String getIssuerAsString() { return null; }
	public boolean[] getKeyUsage() { return null; }
	public boolean getMatchAllSubjectAltNames() { return false; }
	public byte[] getNameConstraints() { return null; }
	public java.util.Collection getPathToNames() { return null; }
	public java.util.Set getPolicy() { return null; }
	public java.util.Date getPrivateKeyValid() { return null; }
	public java.math.BigInteger getSerialNumber() { return null; }
	public java.util.Collection getSubjectAlternativeNames() { return null; }
	public byte[] getSubjectAsBytes() throws java.io.IOException { return null; }
	public java.lang.String getSubjectAsString() { return null; }
	public byte[] getSubjectKeyIdentifier() { return null; }
	public java.security.PublicKey getSubjectPublicKey() { return null; }
	public java.lang.String getSubjectPublicKeyAlgID() { return null; }
	public boolean match(java.security.cert.Certificate var0) { return false; }
	public void setAuthorityKeyIdentifier(byte[] var0) { }
	public void setBasicConstraints(int var0) { }
	public void setCertificate(java.security.cert.X509Certificate var0) { }
	public void setCertificateValid(java.util.Date var0) { }
	public void setExtendedKeyUsage(java.util.Set var0) throws java.io.IOException { }
	public void setIssuer(java.lang.String var0) throws java.io.IOException { }
	public void setIssuer(byte[] var0) throws java.io.IOException { }
	public void setKeyUsage(boolean[] var0) { }
	public void setMatchAllSubjectAltNames(boolean var0) { }
	public void setNameConstraints(byte[] var0) throws java.io.IOException { }
	public void setPathToNames(java.util.Collection var0) throws java.io.IOException { }
	public void setPolicy(java.util.Set var0) throws java.io.IOException { }
	public void setPrivateKeyValid(java.util.Date var0) { }
	public void setSerialNumber(java.math.BigInteger var0) { }
	public void setSubject(java.lang.String var0) throws java.io.IOException { }
	public void setSubject(byte[] var0) throws java.io.IOException { }
	public void setSubjectAlternativeNames(java.util.Collection var0) throws java.io.IOException { }
	public void setSubjectKeyIdentifier(byte[] var0) { }
	public void setSubjectPublicKey(java.security.PublicKey var0) { }
	public void setSubjectPublicKey(byte[] var0) throws java.io.IOException { }
	public void setSubjectPublicKeyAlgID(java.lang.String var0) throws java.io.IOException { }
}

