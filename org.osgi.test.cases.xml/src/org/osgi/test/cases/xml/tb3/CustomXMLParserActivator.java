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

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Constants;
import org.osgi.util.xml.XMLParserActivator;

import junit.framework.TestCase;

public class CustomXMLParserActivator extends XMLParserActivator {
	public void setSAXProperties(SAXParserFactory factory,
			Hashtable<String,Object> props) {
		if ((factory instanceof SimpleSAXParserFactory) == false) {
			TestCase.fail(
					"factory is not a SimpleSAXParserFactory");
		}
		super.setSAXProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple SAX parser");
		props.put("marsupial", "kangaroo");
	}

	public void setDOMProperties(DocumentBuilderFactory factory,
			Hashtable<String,Object> props) {
		if ((factory instanceof SimpleDocumentBuilderFactory) == false) {
			TestCase.fail(
					"factory is not a SimpleDocumentBuilderFactory");
		}
		super.setDOMProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple DOM parser");
		props.put("marsupial", "koala");
	}
}
