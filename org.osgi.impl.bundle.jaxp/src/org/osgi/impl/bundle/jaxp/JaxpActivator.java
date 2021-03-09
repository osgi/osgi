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
package org.osgi.impl.bundle.jaxp;

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.xml.XMLParserActivator;

public class JaxpActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			if (f != null) {
				f.setNamespaceAware(true);
				f.setValidating(true);
				Hashtable<String,Object> properties = new Hashtable<>();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, Boolean.valueOf(f.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING,
						Boolean.valueOf(f.isValidating()));
				context.registerService(DocumentBuilderFactory.class.getName(),
						f, properties);
			}
			else
				System.err.println("No DocumentBuilderFactory");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SAXParserFactory s = SAXParserFactory.newInstance();
			if (s != null) {
				s.setNamespaceAware(true);
				s.setValidating(true);
				Hashtable<String,Object> properties = new Hashtable<>();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, 
						Boolean.valueOf(s.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING, 
						Boolean.valueOf(s.isValidating()));
				context.registerService(SAXParserFactory.class.getName(), s,
						properties);
			}
			else
				System.err.println("No SAXParserFactory");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// nothing to do
	}
}
