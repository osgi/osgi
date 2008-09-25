/*
 * $Revision$
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

package org.w3c.dom;
public abstract interface DOMImplementation {
	public abstract org.w3c.dom.Document createDocument(java.lang.String var0, java.lang.String var1, org.w3c.dom.DocumentType var2);
	public abstract org.w3c.dom.DocumentType createDocumentType(java.lang.String var0, java.lang.String var1, java.lang.String var2);
	public abstract boolean hasFeature(java.lang.String var0, java.lang.String var1);
}

