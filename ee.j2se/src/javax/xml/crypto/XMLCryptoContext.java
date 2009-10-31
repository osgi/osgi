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

package javax.xml.crypto;
public interface XMLCryptoContext {
	java.lang.Object get(java.lang.Object var0);
	java.lang.String getBaseURI();
	java.lang.String getDefaultNamespacePrefix();
	javax.xml.crypto.KeySelector getKeySelector();
	java.lang.String getNamespacePrefix(java.lang.String var0, java.lang.String var1);
	java.lang.Object getProperty(java.lang.String var0);
	javax.xml.crypto.URIDereferencer getURIDereferencer();
	java.lang.Object put(java.lang.Object var0, java.lang.Object var1);
	java.lang.String putNamespacePrefix(java.lang.String var0, java.lang.String var1);
	void setBaseURI(java.lang.String var0);
	void setDefaultNamespacePrefix(java.lang.String var0);
	void setKeySelector(javax.xml.crypto.KeySelector var0);
	java.lang.Object setProperty(java.lang.String var0, java.lang.Object var1);
	void setURIDereferencer(javax.xml.crypto.URIDereferencer var0);
}

