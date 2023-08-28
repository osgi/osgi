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

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.Action;

@WebService(name = "Echo", targetNamespace = "http://webservice.jakartaws.cases.test.osgi.org/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Echo {

	@WebMethod(action = "echo")
	@WebResult(partName = "return")
	@Action(input = "echo", output = "http://webservice.jakartaws.cases.test.osgi.org/Echo/echoResponse")
	public String echo(@WebParam(name = "textIn", partName = "textIn")
	String textIn);

}
