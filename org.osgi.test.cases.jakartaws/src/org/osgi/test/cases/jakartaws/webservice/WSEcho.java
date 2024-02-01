/*
* Copyright (c) 2023 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   SmartCity Jena - initial
*   Stefan Bischof (bipolis.org) - initial
*   Christoph Läubrich - initial
*/
package org.osgi.test.cases.jakartaws.webservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
// @HttpWhiteboardEndpoint(contextpath = "/echo")
// @WhiteboardEndpoint(address = "")
// @Component(immediate = true, service = WSEcho.class, property =
// "wstype=echo")
public class WSEcho {

	public WSEcho() {
		System.out.println("I will echo everything I receive!");
	}

	@WebMethod(operationName = "echo", action = "echo")
	public String echo(@WebParam(name = "textIn") String text) {
		System.out.println("Echo '" + text + "' to caller!");
		return text;
	}

}
