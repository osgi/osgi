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
public class PKIXParameters implements java.security.cert.CertPathParameters {
	public PKIXParameters(java.security.KeyStore var0) throws java.security.InvalidAlgorithmParameterException, java.security.KeyStoreException { }
	public PKIXParameters(java.util.Set var0) throws java.security.InvalidAlgorithmParameterException { }
	public void addCertPathChecker(java.security.cert.PKIXCertPathChecker var0) { }
	public void addCertStore(java.security.cert.CertStore var0) { }
	public java.lang.Object clone() { return null; }
	public java.util.List getCertPathCheckers() { return null; }
	public java.util.List getCertStores() { return null; }
	public java.util.Date getDate() { return null; }
	public java.util.Set getInitialPolicies() { return null; }
	public boolean getPolicyQualifiersRejected() { return false; }
	public java.lang.String getSigProvider() { return null; }
	public java.security.cert.CertSelector getTargetCertConstraints() { return null; }
	public java.util.Set getTrustAnchors() { return null; }
	public boolean isAnyPolicyInhibited() { return false; }
	public boolean isExplicitPolicyRequired() { return false; }
	public boolean isPolicyMappingInhibited() { return false; }
	public boolean isRevocationEnabled() { return false; }
	public void setAnyPolicyInhibited(boolean var0) { }
	public void setCertPathCheckers(java.util.List var0) { }
	public void setCertStores(java.util.List var0) { }
	public void setDate(java.util.Date var0) { }
	public void setExplicitPolicyRequired(boolean var0) { }
	public void setInitialPolicies(java.util.Set var0) { }
	public void setPolicyMappingInhibited(boolean var0) { }
	public void setPolicyQualifiersRejected(boolean var0) { }
	public void setRevocationEnabled(boolean var0) { }
	public void setSigProvider(java.lang.String var0) { }
	public void setTargetCertConstraints(java.security.cert.CertSelector var0) { }
	public void setTrustAnchors(java.util.Set var0) throws java.security.InvalidAlgorithmParameterException { }
}

