/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.support.xpath;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Attr;

/**
 * 
 * @author $Id$
 */
public class BaseNamespaceContext implements NamespaceContext {
	private final String	uri;
	private final String	prefix;

	/**
	 * @param uri
	 * 
	 * @param prefix
	 */
	public BaseNamespaceContext(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	/**
	 * @param namespaceAttr
	 */
	public BaseNamespaceContext(Attr namespaceAttr) {
		this.prefix = namespaceAttr.getLocalName();
		this.uri = namespaceAttr.getValue();
	}

	/**
	 * @return result
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @return result
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamespaceURI(final String namespacePrefix) {
		if (namespacePrefix == null)
			throw new IllegalArgumentException();
		if (namespacePrefix.equals(prefix))
			return uri;
		switch (namespacePrefix) {
			case XMLConstants.XML_NS_PREFIX :
				return XMLConstants.XML_NS_URI;
			case XMLConstants.XMLNS_ATTRIBUTE :
				return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
			default :
				return XMLConstants.NULL_NS_URI;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrefix(final String namespaceURI) {
		if (namespaceURI == null)
			throw new IllegalArgumentException();
		if (namespaceURI.equals(uri))
			return prefix;
		switch (namespaceURI) {
			case XMLConstants.XML_NS_URI :
				return XMLConstants.XML_NS_PREFIX;
			case XMLConstants.XMLNS_ATTRIBUTE_NS_URI :
				return XMLConstants.XMLNS_ATTRIBUTE;
			default :
				return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<String> getPrefixes(final String namespaceURI) {
		String p = getPrefix(namespaceURI);
		if (p == null) {
			return Collections.emptyIterator();
		}
		return Collections.singletonList(p).iterator();
	}
}
