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
package org.osgi.test.cases.xml.tb3;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SimpleDocumentBuilderFactory extends DocumentBuilderFactory {
	public DocumentBuilder newDocumentBuilder()
			throws ParserConfigurationException {
		return null;
	}

	public Object getAttribute(String s) throws IllegalArgumentException {
		throw new IllegalArgumentException(s);
	}

	public void setAttribute(String s, Object o)
			throws IllegalArgumentException {
		throw new IllegalArgumentException(s);
	}

	public void setFeature(String name, boolean value)
			throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}

	public boolean getFeature(String name) throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}
}
