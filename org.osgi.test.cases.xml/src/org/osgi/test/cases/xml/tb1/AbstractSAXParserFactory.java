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
package org.osgi.test.cases.xml.tb1;

import javax.xml.parsers.*;
import org.xml.sax.SAXNotRecognizedException;

abstract class AbstractSAXParserFactory extends SAXParserFactory {
	private final boolean	m_validating;
	private final boolean	m_namespaceAware;

	protected AbstractSAXParserFactory(boolean validating,
			boolean namespaceAware) {
		m_validating = validating;
		m_namespaceAware = namespaceAware;
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(name);
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(name);
	}

	public SAXParser newSAXParser() throws ParserConfigurationException {
		if (isValidating() && (m_validating == false)) {
			throw new ParserConfigurationException();
		}
		if (isNamespaceAware() && (m_namespaceAware == false)) {
			throw new ParserConfigurationException();
		}
		return null;
	}
}
