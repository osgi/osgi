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
package org.osgi.test.cases.jakartaws.junit.binding;

import java.net.URL;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;

@WebServiceClient(name = "WSEchoService", targetNamespace = "http://webservice.jakartaws.cases.test.osgi.org/")
public class WSEchoService extends Service {

	private final static QName WSECHOSERVICE_QNAME = new QName(
			"http://webservice.jakartaws.cases.test.osgi.org/",
			"WSEchoService");

	public WSEchoService(URL wsdlLocation) {
		super(wsdlLocation, WSECHOSERVICE_QNAME);
	}

	@WebEndpoint(name = "EchoPort")
	public Echo getEchoPort() {
		return super.getPort(
				new QName("http://webservice.jakartaws.cases.test.osgi.org/",
						"EchoPort"),
				Echo.class);
	}

}
