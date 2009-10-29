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

package org.xml.sax;
/** @deprecated */ public abstract interface Parser {
	public abstract void parse(java.lang.String var0) throws java.io.IOException, org.xml.sax.SAXException;
	public abstract void parse(org.xml.sax.InputSource var0) throws java.io.IOException, org.xml.sax.SAXException;
	public abstract void setDTDHandler(org.xml.sax.DTDHandler var0);
	public abstract void setDocumentHandler(org.xml.sax.DocumentHandler var0);
	public abstract void setEntityResolver(org.xml.sax.EntityResolver var0);
	public abstract void setErrorHandler(org.xml.sax.ErrorHandler var0);
	public abstract void setLocale(java.util.Locale var0) throws org.xml.sax.SAXException;
}

