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

package org.xml.sax.ext;
public interface DeclHandler {
	void attributeDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) throws org.xml.sax.SAXException;
	void elementDecl(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException;
	void externalEntityDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws org.xml.sax.SAXException;
	void internalEntityDecl(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException;
}

