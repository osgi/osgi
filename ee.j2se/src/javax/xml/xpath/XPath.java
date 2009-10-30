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
public interface XPath {
	javax.xml.xpath.XPathExpression compile(java.lang.String var0) throws javax.xml.xpath.XPathExpressionException;
	java.lang.String evaluate(java.lang.String var0, java.lang.Object var1) throws javax.xml.xpath.XPathExpressionException;
	java.lang.Object evaluate(java.lang.String var0, java.lang.Object var1, javax.xml.namespace.QName var2) throws javax.xml.xpath.XPathExpressionException;
	java.lang.String evaluate(java.lang.String var0, org.xml.sax.InputSource var1) throws javax.xml.xpath.XPathExpressionException;
	java.lang.Object evaluate(java.lang.String var0, org.xml.sax.InputSource var1, javax.xml.namespace.QName var2) throws javax.xml.xpath.XPathExpressionException;
	javax.xml.namespace.NamespaceContext getNamespaceContext();
	javax.xml.xpath.XPathFunctionResolver getXPathFunctionResolver();
	javax.xml.xpath.XPathVariableResolver getXPathVariableResolver();
	void reset();
	void setNamespaceContext(javax.xml.namespace.NamespaceContext var0);
	void setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver var0);
	void setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver var0);
}

