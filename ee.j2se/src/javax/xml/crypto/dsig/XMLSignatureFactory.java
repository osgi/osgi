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

package javax.xml.crypto.dsig;
public abstract class XMLSignatureFactory {
	protected XMLSignatureFactory() { } 
	public static javax.xml.crypto.dsig.XMLSignatureFactory getInstance() { return null; }
	public static javax.xml.crypto.dsig.XMLSignatureFactory getInstance(java.lang.String var0) { return null; }
	public static javax.xml.crypto.dsig.XMLSignatureFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchProviderException { return null; }
	public static javax.xml.crypto.dsig.XMLSignatureFactory getInstance(java.lang.String var0, java.security.Provider var1) { return null; }
	public final javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getKeyInfoFactory() { return null; }
	public final java.lang.String getMechanismType() { return null; }
	public final java.security.Provider getProvider() { return null; }
	public abstract javax.xml.crypto.URIDereferencer getURIDereferencer();
	public abstract boolean isFeatureSupported(java.lang.String var0);
	public abstract javax.xml.crypto.dsig.CanonicalizationMethod newCanonicalizationMethod(java.lang.String var0, javax.xml.crypto.XMLStructure var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.CanonicalizationMethod newCanonicalizationMethod(java.lang.String var0, javax.xml.crypto.dsig.spec.C14NMethodParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.DigestMethod newDigestMethod(java.lang.String var0, javax.xml.crypto.dsig.spec.DigestMethodParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.Manifest newManifest(java.util.List var0);
	public abstract javax.xml.crypto.dsig.Manifest newManifest(java.util.List var0, java.lang.String var1);
	public abstract javax.xml.crypto.dsig.Reference newReference(java.lang.String var0, javax.xml.crypto.dsig.DigestMethod var1);
	public abstract javax.xml.crypto.dsig.Reference newReference(java.lang.String var0, javax.xml.crypto.dsig.DigestMethod var1, java.util.List var2, java.lang.String var3, java.lang.String var4);
	public abstract javax.xml.crypto.dsig.Reference newReference(java.lang.String var0, javax.xml.crypto.dsig.DigestMethod var1, java.util.List var2, java.lang.String var3, java.lang.String var4, byte[] var5);
	public abstract javax.xml.crypto.dsig.Reference newReference(java.lang.String var0, javax.xml.crypto.dsig.DigestMethod var1, java.util.List var2, javax.xml.crypto.Data var3, java.util.List var4, java.lang.String var5, java.lang.String var6);
	public abstract javax.xml.crypto.dsig.SignatureMethod newSignatureMethod(java.lang.String var0, javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.SignatureProperties newSignatureProperties(java.util.List var0, java.lang.String var1);
	public abstract javax.xml.crypto.dsig.SignatureProperty newSignatureProperty(java.util.List var0, java.lang.String var1, java.lang.String var2);
	public abstract javax.xml.crypto.dsig.SignedInfo newSignedInfo(javax.xml.crypto.dsig.CanonicalizationMethod var0, javax.xml.crypto.dsig.SignatureMethod var1, java.util.List var2);
	public abstract javax.xml.crypto.dsig.SignedInfo newSignedInfo(javax.xml.crypto.dsig.CanonicalizationMethod var0, javax.xml.crypto.dsig.SignatureMethod var1, java.util.List var2, java.lang.String var3);
	public abstract javax.xml.crypto.dsig.Transform newTransform(java.lang.String var0, javax.xml.crypto.XMLStructure var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.Transform newTransform(java.lang.String var0, javax.xml.crypto.dsig.spec.TransformParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException;
	public abstract javax.xml.crypto.dsig.XMLObject newXMLObject(java.util.List var0, java.lang.String var1, java.lang.String var2, java.lang.String var3);
	public abstract javax.xml.crypto.dsig.XMLSignature newXMLSignature(javax.xml.crypto.dsig.SignedInfo var0, javax.xml.crypto.dsig.keyinfo.KeyInfo var1);
	public abstract javax.xml.crypto.dsig.XMLSignature newXMLSignature(javax.xml.crypto.dsig.SignedInfo var0, javax.xml.crypto.dsig.keyinfo.KeyInfo var1, java.util.List var2, java.lang.String var3, java.lang.String var4);
	public abstract javax.xml.crypto.dsig.XMLSignature unmarshalXMLSignature(javax.xml.crypto.XMLStructure var0) throws javax.xml.crypto.MarshalException;
	public abstract javax.xml.crypto.dsig.XMLSignature unmarshalXMLSignature(javax.xml.crypto.dsig.XMLValidateContext var0) throws javax.xml.crypto.MarshalException;
}

