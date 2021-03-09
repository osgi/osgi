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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SimpleSAXParserFactory extends SAXParserFactory {
	public SAXParser newSAXParser() throws ParserConfigurationException,
			SAXException {
		return null;
	}

	public boolean getFeature(String s) throws ParserConfigurationException,
			SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotRecognizedException(s);
	}

	public void setFeature(String s, boolean b)
			throws ParserConfigurationException, SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new SAXNotRecognizedException(s);
	}
}
