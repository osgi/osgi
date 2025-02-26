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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.service.webservice.runtime.dto.FailedDTO.FAILURE_REASON_SERVICE_NOT_GETTABLE;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.service.webservice.runtime.dto.EndpointDTO;
import org.osgi.service.webservice.runtime.dto.FailedEndpointDTO;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.test.cases.webservice.webservices.WSEcho;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.support.map.Maps;

public class EndpointRegistrationTests {
	
	private static final String ECHO_URI = "http://127.0.0.1:56780/echo";

	@InjectBundleContext 
	BundleContext ctx;

	private WebServiceRuntimeTracker webServiceRuntimeTracker;
	
	@BeforeEach
	void setup(@InjectService ServiceAware<WebserviceServiceRuntime> runtime)
			throws InterruptedException {
		webServiceRuntimeTracker = new WebServiceRuntimeTracker(ctx, runtime.getServiceReference());
		webServiceRuntimeTracker.waitForQuiet(1, 5, TimeUnit.SECONDS);
		assertTrue(webServiceRuntimeTracker.hasChanged(), "No Change Count detected for the WebserviceServiceRuntime");
	}
	
	@AfterEach
	void shutdown() {
		webServiceRuntimeTracker.close();
	}
	
	/**
	 * A basic test that registers an endpoint as described in 161.1.3
	 * 
	 * It should not matter what type is used to register
	 * 
	 * @throws Exception
	 */
	@ParameterizedTest
	@ValueSource(classes = {Object.class, WSEcho.class})
	public void testSimpleWebserviceEndpointRegistration(
			Class<?> registrationType) throws Exception {
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					return ctx.registerService(registrationType.getName(), new WSEcho(), props);
				},
				(reg,dto) -> {
					for(EndpointDTO e : dto.endpoints) {
						if(getServiceId(reg).equals(e.implementor.id)) {
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The endpoint with id " + getServiceId(reg) + " was not present in the DTO endpoints " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(WSEcho.ECHO_NS, "echoAction", Maps.mapOf("textIn", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(ECHO_URI, soapRequest);
		
		assertEquals("BANG", WebServiceHelper.extractResponse("echoActionResponse", soapResponse),
				"Received an incorrect response from the Web Service");
	}

	static Object getServiceId(ServiceRegistration<?> reg) {
		return reg.getReference().getProperty(SERVICE_ID);
	}

	/**
	 * A basic test that fails to register an endpoint due
	 * to an incorrect or missing property, as described in 
	 * 161.1.3
	 * 
	 * It should not matter what type is used to register
	 * 
	 * @throws Exception
	 */
	@ParameterizedTest
	@ValueSource(strings = {WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, "wrongProp"})
	public void testWrongServiceProp(String propName) throws Exception {
		
		webServiceRuntimeTracker.waitForNoChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(propName, Boolean.FALSE);
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					return ctx.registerService(WSEcho.class, new WSEcho(), props);
				},
				6, TimeUnit.SECONDS);
	}
	
	@Test
	public void testNotGettableEndpoint() throws Exception {
		
		ServiceFactory<Object> failingFactory = new ServiceFactory<Object>() {
			
			@Override
			public void ungetService(Bundle bundle, ServiceRegistration<Object> registration, Object service) {
			}
			
			@Override
			public Object getService(Bundle bundle, ServiceRegistration<Object> registration) {
				return null;
			}
		};
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					return ctx.registerService(WSEcho.class.getName(), failingFactory, props);
				},
				(reg,dto) -> {
					for(FailedEndpointDTO e : dto.failedEndpoints) {
						if(getServiceId(reg).equals(e.implementor.id)) {
							assertEquals(FAILURE_REASON_SERVICE_NOT_GETTABLE, e.failureCode, "Endpoint service should not be gettable");
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The endpoint with id " + getServiceId(reg) + " was not present in the failed DTO endpoints with code " + 
				FAILURE_REASON_SERVICE_NOT_GETTABLE + ": " + dto,
				6, TimeUnit.SECONDS);
	}
}
