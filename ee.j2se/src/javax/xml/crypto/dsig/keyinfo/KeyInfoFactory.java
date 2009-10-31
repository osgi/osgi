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

package javax.xml.crypto.dsig.keyinfo;
public abstract class KeyInfoFactory {
	protected KeyInfoFactory() { } 
	public static javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getInstance() { return null; }
	public static javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getInstance(java.lang.String var0) { return null; }
	public static javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchProviderException { return null; }
	public static javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getInstance(java.lang.String var0, java.security.Provider var1) { return null; }
	public final java.lang.String getMechanismType() { return null; }
	public final java.security.Provider getProvider() { return null; }
	public abstract javax.xml.crypto.URIDereferencer getURIDereferencer();
	public abstract boolean isFeatureSupported(java.lang.String var0);
	public abstract javax.xml.crypto.dsig.keyinfo.KeyInfo newKeyInfo(java.util.List var0);
	public abstract javax.xml.crypto.dsig.keyinfo.KeyInfo newKeyInfo(java.util.List var0, java.lang.String var1);
	public abstract javax.xml.crypto.dsig.keyinfo.KeyName newKeyName(java.lang.String var0);
	public abstract javax.xml.crypto.dsig.keyinfo.KeyValue newKeyValue(java.security.PublicKey var0) throws java.security.KeyException;
	public abstract javax.xml.crypto.dsig.keyinfo.PGPData newPGPData(byte[] var0);
	public abstract javax.xml.crypto.dsig.keyinfo.PGPData newPGPData(byte[] var0, java.util.List var1);
	public abstract javax.xml.crypto.dsig.keyinfo.PGPData newPGPData(byte[] var0, byte[] var1, java.util.List var2);
	public abstract javax.xml.crypto.dsig.keyinfo.RetrievalMethod newRetrievalMethod(java.lang.String var0);
	public abstract javax.xml.crypto.dsig.keyinfo.RetrievalMethod newRetrievalMethod(java.lang.String var0, java.lang.String var1, java.util.List var2);
	public abstract javax.xml.crypto.dsig.keyinfo.X509Data newX509Data(java.util.List var0);
	public abstract javax.xml.crypto.dsig.keyinfo.X509IssuerSerial newX509IssuerSerial(java.lang.String var0, java.math.BigInteger var1);
	public abstract javax.xml.crypto.dsig.keyinfo.KeyInfo unmarshalKeyInfo(javax.xml.crypto.XMLStructure var0) throws javax.xml.crypto.MarshalException;
}

