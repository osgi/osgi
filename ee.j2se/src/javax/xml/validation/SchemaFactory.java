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

package javax.xml.validation;
public abstract class SchemaFactory {
	protected SchemaFactory() { } 
	public abstract org.xml.sax.ErrorHandler getErrorHandler();
	public boolean getFeature(java.lang.String var0) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException { return false; }
	public java.lang.Object getProperty(java.lang.String var0) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException { return null; }
	public abstract org.w3c.dom.ls.LSResourceResolver getResourceResolver();
	public abstract boolean isSchemaLanguageSupported(java.lang.String var0);
	public final static javax.xml.validation.SchemaFactory newInstance(java.lang.String var0) { return null; }
	public static javax.xml.validation.SchemaFactory newInstance(java.lang.String var0, java.lang.String var1, java.lang.ClassLoader var2) { return null; }
	public abstract javax.xml.validation.Schema newSchema() throws org.xml.sax.SAXException;
	public javax.xml.validation.Schema newSchema(java.io.File var0) throws org.xml.sax.SAXException { return null; }
	public javax.xml.validation.Schema newSchema(java.net.URL var0) throws org.xml.sax.SAXException { return null; }
	public javax.xml.validation.Schema newSchema(javax.xml.transform.Source var0) throws org.xml.sax.SAXException { return null; }
	public abstract javax.xml.validation.Schema newSchema(javax.xml.transform.Source[] var0) throws org.xml.sax.SAXException;
	public abstract void setErrorHandler(org.xml.sax.ErrorHandler var0);
	public void setFeature(java.lang.String var0, boolean var1) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException { }
	public void setProperty(java.lang.String var0, java.lang.Object var1) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException { }
	public abstract void setResourceResolver(org.w3c.dom.ls.LSResourceResolver var0);
}

