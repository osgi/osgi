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

package javax.xml.transform.sax;
public abstract class SAXTransformerFactory extends javax.xml.transform.TransformerFactory {
	public final static java.lang.String FEATURE = "http://javax.xml.transform.sax.SAXTransformerFactory/feature";
	public final static java.lang.String FEATURE_XMLFILTER = "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter";
	protected SAXTransformerFactory() { } 
	public abstract javax.xml.transform.sax.TemplatesHandler newTemplatesHandler() throws javax.xml.transform.TransformerConfigurationException;
	public abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler() throws javax.xml.transform.TransformerConfigurationException;
	public abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler(javax.xml.transform.Source var0) throws javax.xml.transform.TransformerConfigurationException;
	public abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler(javax.xml.transform.Templates var0) throws javax.xml.transform.TransformerConfigurationException;
	public abstract org.xml.sax.XMLFilter newXMLFilter(javax.xml.transform.Source var0) throws javax.xml.transform.TransformerConfigurationException;
	public abstract org.xml.sax.XMLFilter newXMLFilter(javax.xml.transform.Templates var0) throws javax.xml.transform.TransformerConfigurationException;
}

