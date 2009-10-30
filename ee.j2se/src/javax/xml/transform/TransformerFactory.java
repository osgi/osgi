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

package javax.xml.transform;
public abstract class TransformerFactory {
	protected TransformerFactory() { } 
	public abstract javax.xml.transform.Source getAssociatedStylesheet(javax.xml.transform.Source var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) throws javax.xml.transform.TransformerConfigurationException;
	public abstract java.lang.Object getAttribute(java.lang.String var0);
	public abstract javax.xml.transform.ErrorListener getErrorListener();
	public abstract boolean getFeature(java.lang.String var0);
	public abstract javax.xml.transform.URIResolver getURIResolver();
	public static javax.xml.transform.TransformerFactory newInstance() throws javax.xml.transform.TransformerFactoryConfigurationError { return null; }
	public abstract javax.xml.transform.Templates newTemplates(javax.xml.transform.Source var0) throws javax.xml.transform.TransformerConfigurationException;
	public abstract javax.xml.transform.Transformer newTransformer() throws javax.xml.transform.TransformerConfigurationException;
	public abstract javax.xml.transform.Transformer newTransformer(javax.xml.transform.Source var0) throws javax.xml.transform.TransformerConfigurationException;
	public abstract void setAttribute(java.lang.String var0, java.lang.Object var1);
	public abstract void setErrorListener(javax.xml.transform.ErrorListener var0);
	public abstract void setFeature(java.lang.String var0, boolean var1) throws javax.xml.transform.TransformerConfigurationException;
	public abstract void setURIResolver(javax.xml.transform.URIResolver var0);
}

