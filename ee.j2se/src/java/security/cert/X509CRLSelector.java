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
public class X509CRLSelector implements java.security.cert.CRLSelector {
	public X509CRLSelector() { }
	public void addIssuerName(java.lang.String var0) throws java.io.IOException { }
	public void addIssuerName(byte[] var0) throws java.io.IOException { }
	public java.lang.Object clone() { return null; }
	public java.security.cert.X509Certificate getCertificateChecking() { return null; }
	public java.util.Date getDateAndTime() { return null; }
	public java.util.Collection getIssuerNames() { return null; }
	public java.math.BigInteger getMaxCRL() { return null; }
	public java.math.BigInteger getMinCRL() { return null; }
	public boolean match(java.security.cert.CRL var0) { return false; }
	public void setCertificateChecking(java.security.cert.X509Certificate var0) { }
	public void setDateAndTime(java.util.Date var0) { }
	public void setIssuerNames(java.util.Collection var0) throws java.io.IOException { }
	public void setMaxCRLNumber(java.math.BigInteger var0) { }
	public void setMinCRLNumber(java.math.BigInteger var0) { }
}

