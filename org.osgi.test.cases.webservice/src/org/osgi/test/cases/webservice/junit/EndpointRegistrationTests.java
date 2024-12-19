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
package org.osgi.test.cases.webservice.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.framework.Constants.SERVICE_ID;

import java.util.Hashtable;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.service.webservice.runtime.dto.EndpointDTO;
import org.osgi.service.webservice.runtime.dto.RuntimeDTO;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.test.cases.webservice.webservices.WSEcho;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.support.map.Maps;

public class EndpointRegistrationTests {
	
	private static final String ECHO_URI = "http://127.0.0.1:56780/echo";

	/**
	 * A basic test that registers an endpoint as described in 161.1.3
	 * 
	 * It should not matter what type is used to register
	 * 
	 * @throws Exception
	 */
	@ParameterizedTest
	@ValueSource(classes = {Object.class, WSEcho.class})
	public void testSimpleWebserviceEndpointRegistration(Class<?> registrationType,
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {

		Hashtable<String, Object> props = new Hashtable<>();
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
		
		ServiceRegistration<?> reg = ctx.registerService(registrationType.getName(),
				new WSEcho(), props);
		
		// TODO improve this when it is specced
		boolean found = false;
		for(int i = 0; i < 30; i++) {
			RuntimeDTO dto = runtime.getRuntimeDTO();
			for(EndpointDTO e : dto.endpoints) {
				if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
					found = true;
					break;
				}
			}
			Thread.sleep(200);
		}
		
		assertTrue(found);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(WSEcho.ECHO_NS, "echoAction", Maps.mapOf("textIn", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(ECHO_URI, soapRequest);
		
		assertEquals("BANG", WebServiceHelper.extractResponse("echoActionResponse", soapResponse));
	}

	/**
	 * A basic test that fails to register an endpoint due
	 * to a missing property, as described in 161.1.3
	 * 
	 * It should not matter what type is used to register
	 * 
	 * @throws Exception
	 */
	@ParameterizedTest
	@ValueSource(strings = {WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, "wrongProp"})
	public void testWrongServiceProp(String propName,
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {
		
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(propName, Boolean.FALSE);
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
		
		ServiceRegistration<?> reg = ctx.registerService(WSEcho.class,
				new WSEcho(), props);
		
		// TODO improve this when it is specced
		boolean found = false;
		for(int i = 0; i < 30; i++) {
			RuntimeDTO dto = runtime.getRuntimeDTO();
			for(EndpointDTO e : dto.endpoints) {
				if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
					found = true;
					break;
				}
			}
			Thread.sleep(200);
		}
		
		assertFalse(found);
	}
}
