/*
 * Copyright (c) OSGi Alliance (2012, 2015). All Rights Reserved.
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


package org.osgi.test.cases.metatype.annotations.junit;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Attr;

/**
 * 
 * 
 * @author $Id$
 */
public class NamespaceContextImpl implements NamespaceContext {
	private final String	uri;
	private final String	prefix;

	/**
	 * @param uri
	 * @param prefix
	 */
	public NamespaceContextImpl(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	/**
	 * @param namespaceAttr
	 */
	public NamespaceContextImpl(Attr namespaceAttr) {
		this.prefix = namespaceAttr.getLocalName();
		this.uri = namespaceAttr.getValue();
	}

	/**
	 * @return result
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return result
	 */
	public String getPrefix() {
		return prefix;
	}

	public String getNamespaceURI(String namespacePrefix) {
		if (namespacePrefix == null)
			throw new IllegalArgumentException();
		if (prefix.equals(namespacePrefix))
			return uri;
		if ("xml".equals(namespacePrefix))
			return "http://www.w3.org/XML/1998/namespace";
		if ("xmlns".equals(namespacePrefix))
			return "http://www.w3.org/2000/xmlns/";
		return "";
	}

	public String getPrefix(String namespaceURI) {
		if (uri.equals(namespaceURI))
			return prefix;
		if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI))
			return "xml";
		if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI))
			return "xmlns";
		return null;
	}

	public Iterator<String> getPrefixes(String namespaceURI) {
		if (namespaceURI == null)
			throw new IllegalArgumentException();
		if (uri.equals(namespaceURI))
			return Collections.singletonList(prefix).iterator();
		if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI))
			return Collections.singletonList("xml").iterator();
		if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI))
			return Collections.singletonList("xmlns").iterator();
		return Collections.<String> emptyList().iterator();
	}
}
