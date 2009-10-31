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

package javax.xml.xpath;
public abstract class XPathFactory {
	public final static java.lang.String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
	public final static java.lang.String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
	protected XPathFactory() { } 
	public abstract boolean getFeature(java.lang.String var0) throws javax.xml.xpath.XPathFactoryConfigurationException;
	public abstract boolean isObjectModelSupported(java.lang.String var0);
	public final static javax.xml.xpath.XPathFactory newInstance() { return null; }
	public final static javax.xml.xpath.XPathFactory newInstance(java.lang.String var0) throws javax.xml.xpath.XPathFactoryConfigurationException { return null; }
	public static javax.xml.xpath.XPathFactory newInstance(java.lang.String var0, java.lang.String var1, java.lang.ClassLoader var2) throws javax.xml.xpath.XPathFactoryConfigurationException { return null; }
	public abstract javax.xml.xpath.XPath newXPath();
	public abstract void setFeature(java.lang.String var0, boolean var1) throws javax.xml.xpath.XPathFactoryConfigurationException;
	public abstract void setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver var0);
	public abstract void setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver var0);
}

