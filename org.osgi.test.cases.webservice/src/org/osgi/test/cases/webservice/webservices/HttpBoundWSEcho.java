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
package org.osgi.test.cases.webservice.webservices;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.http.HTTPBinding;

@BindingType(HTTPBinding.HTTP_BINDING)
@WebService(targetNamespace = HttpBoundWSEcho.ECHO_NS)
public class HttpBoundWSEcho {
    public static final String ECHO_NS = "http://echo.webservice.osgi.org";

	@WebMethod(operationName = "echoAction", action = "echo")
    public String echo(@WebParam(name = "textIn") String text) {
    	return text;
    }
}