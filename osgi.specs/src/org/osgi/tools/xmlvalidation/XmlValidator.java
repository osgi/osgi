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

package org.osgi.tools.xmlvalidation;

import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author $Id$
 */
public class XmlValidator {
	File	xsd;
	File	document;
	boolean	fail	= false;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new XmlValidator(new File(args[0]), new File(args[1])).run();
	}

	/**
	 * @param xsd
	 * @param document
	 */
	public XmlValidator(File xsd, File document) {
		this.xsd = xsd;
		this.document = document;
	}

	/**
	 * @throws Exception
	 */
	public void run() throws Exception {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema(xsd);
		Validator validator = schema.newValidator();
		validator.setErrorHandler(new XmlErrorHandler());
		Source source = new StreamSource(document);
		validator.validate(source);
		if (fail) {
			throw new Exception("validation failed");
		}
	}

	/**
	 */
	public class XmlErrorHandler implements ErrorHandler {
		@SuppressWarnings("boxing")
		@Override
		public void error(SAXParseException e) throws SAXException {
			String message = e.getMessage();
			if (message.matches("^cvc\\-id\\.1[^0-9].*")) {
				// ignore cvc-id.1: There is no ID/IDREF binding for IDREF
				return;
			}
			System.err.printf("\nerror: %s:%d:%d\n%s\n", e.getSystemId(), e.getLineNumber(), e.getColumnNumber(), message);
			fail = true;
		}

		@SuppressWarnings("boxing")
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			String message = e.getMessage();
			System.err.printf("\nfatalError: %s:%d:%d\n%s\n", e.getSystemId(), e.getLineNumber(), e.getColumnNumber(), message);
			fail = true;
		}

		@SuppressWarnings("boxing")
		@Override
		public void warning(SAXParseException e) throws SAXException {
			String message = e.getMessage();
			System.err.printf("\nwarning: %s:%d:%d\n%s\n", e.getSystemId(), e.getLineNumber(), e.getColumnNumber(), message);
		}
	}
}
