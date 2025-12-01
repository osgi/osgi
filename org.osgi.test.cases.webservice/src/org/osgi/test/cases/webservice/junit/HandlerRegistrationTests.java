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
import static org.osgi.framework.Constants.SERVICE_RANKING;
import static org.osgi.service.webservice.runtime.dto.FailedDTO.FAILURE_REASON_SERVICE_NOT_GETTABLE;
import static org.osgi.service.webservice.runtime.dto.FailedHandlerDTO.FAILURE_REASON_INVALID_FILTER;
import static org.osgi.service.webservice.runtime.dto.FailedHandlerDTO.FAILURE_REASON_NO_MATCHING_ENDPOINT;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_FILTER;
import static org.osgi.test.cases.webservice.junit.EndpointRegistrationTests.ECHO_URI;
import static org.osgi.test.cases.webservice.junit.EndpointRegistrationTests.getServiceId;
import static org.osgi.test.cases.webservice.webservices.WSEcho.ECHO_NS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
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
import org.osgi.service.webservice.runtime.dto.FailedHandlerDTO;
import org.osgi.service.webservice.runtime.dto.HandlerDTO;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.test.cases.webservice.webservices.CaseChangingHandler;
import org.osgi.test.cases.webservice.webservices.HttpBoundWSEcho;
import org.osgi.test.cases.webservice.webservices.Reflect;
import org.osgi.test.cases.webservice.webservices.ReversingHandler;
import org.osgi.test.cases.webservice.webservices.WSEcho;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.support.map.Maps;

import jakarta.xml.ws.handler.Handler;

public class HandlerRegistrationTests {
	
	private static final String OSGI_WEBSERVICE_TCK_ID = "osgi.webservice.tck.id";
	private static final String REFLECT_URI = "http://127.0.0.1:21980/reflect";
	private static final String REFLECT_URI_2 = "http://127.0.0.1:21980/reflect2";

	@InjectBundleContext 
	BundleContext ctx;

	private WebServiceRuntimeTracker webServiceRuntimeTracker;
	
	@BeforeEach
	void setup(@InjectService ServiceAware<WebserviceServiceRuntime> runtime)
			throws InterruptedException {
		webServiceRuntimeTracker = new WebServiceRuntimeTracker(ctx, runtime.getServiceReference());
		webServiceRuntimeTracker.waitForQuiet(1, 5, TimeUnit.SECONDS);
		assertTrue(webServiceRuntimeTracker.hasChangeCount(), "No Change Count detected for the WebserviceServiceRuntime");
	}
	
	@AfterEach
	void shutdown() {
		webServiceRuntimeTracker.close();
	}
	
	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * This handler is registered <em>before</em> the endpoint that it is applied to
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterFirst() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			return regs;
		};
		
		
		webServiceRuntimeTracker.waitForChange(
				action, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(HandlerDTO e : dto.handlers) {
						if(id.equals(e.serviceReference.id)) {
							found = true;
							break;
						}
					}
					boolean found2 = false;
					id = regs.get(1).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found2 = true;
							break;
						}
					}
					return found && found2;
				},
				(regs,dto) -> "Unable to find both handler " + getServiceId(regs.get(0)) +
				 " and endpoint " + getServiceId(regs.get(1)) + " in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
	}

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * This handler is registered <em>after</em> the endpoint that it is applied to
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterSecond() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			return regs;
		};
		
		webServiceRuntimeTracker.waitForChange(
				action, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found = true;
							break;
						}
					}
					boolean found2 = false;
					id = regs.get(1).getReference().getProperty(SERVICE_ID);
					for(HandlerDTO e : dto.handlers) {
						if(id.equals(e.serviceReference.id)) {
							found2 = true;
							break;
						}
					}
					return found && found2;
				},
				(regs,dto) -> "Unable to find both handler " + getServiceId(regs.get(1)) +
				 " and endpoint " + getServiceId(regs.get(0)) + " in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
	}

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * This handler applies to multiple endpoints and should be called for all of them
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlerMultipleEndpoints() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI_2);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			return regs;
		};
		
		webServiceRuntimeTracker.waitForChange(
				action, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found = true;
							break;
						}
					}
					boolean found2 = false;
					id = regs.get(1).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found2 = true;
							break;
						}
					}
					boolean found3 = false;
					id = regs.get(2).getReference().getProperty(SERVICE_ID);
					for(HandlerDTO e : dto.handlers) {
						if(id.equals(e.serviceReference.id)) {
							found3 = true;
							break;
						}
					}
					return found && found2 && found3;
				},
				(regs,dto) -> "Unable to find both handler " + getServiceId(regs.get(2)) +
				 " and endpoints " + getServiceId(regs.get(0)) + " and " + 
						getServiceId(regs.get(1)) + " in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		String soapRequest2 = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BONG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		String soapResponse2 = WebServiceHelper.getSoapResponse(REFLECT_URI_2, soapRequest2);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
		assertEquals("gNoB", WebServiceHelper.extractResponse("reflectResponse", soapResponse2),
				"The Web Service and Handler did not respond correctly");
	}

	/**
	 * A basic test that registers a handler with filtering as described in 161.1.5
	 * 
	 * Only the targeted endpoint services should be called
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlerMultipleEndpointsFiltered() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			String id = "handlerTest";
			String id2 = "handlerTest2";
			String filter = "(" + OSGI_WEBSERVICE_TCK_ID + "=" + id + ")";
			
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			props.put(OSGI_WEBSERVICE_TCK_ID, id);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI_2);
			props.put(OSGI_WEBSERVICE_TCK_ID, id2);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			props.put(WEBSERVICE_HANDLER_FILTER, filter);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			return regs;
		};
		
		webServiceRuntimeTracker.waitForChange(
				action, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found = true;
							break;
						}
					}
					boolean found2 = false;
					id = regs.get(1).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							found2 = true;
							break;
						}
					}
					boolean found3 = false;
					id = regs.get(2).getReference().getProperty(SERVICE_ID);
					for(HandlerDTO e : dto.handlers) {
						if(id.equals(e.serviceReference.id)) {
							found3 = true;
							break;
						}
					}
					return found && found2 && found3;
				},
				(regs,dto) -> "Unable to find both handler " + getServiceId(regs.get(2)) +
				 " and endpoints " + getServiceId(regs.get(0)) + " and " + 
						getServiceId(regs.get(1)) + " in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		String soapRequest2 = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BONG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		String soapResponse2 = WebServiceHelper.getSoapResponse(REFLECT_URI_2, soapRequest2);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
		assertEquals("GNOB", WebServiceHelper.extractResponse("reflectResponse", soapResponse2),
				"The plain Web Service did not respond correctly");
	}

	/**
	 * A basic test that fails to register a handler due
	 * to an incorrect or missing property, as described in 
	 * 161.1.4
	 * 
	 * @throws Exception
	 */
	@ParameterizedTest
	@ValueSource(strings = {WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, "wrongProp"})
	public void testWrongServiceProp(String propName) throws Exception {
		
		webServiceRuntimeTracker.waitForNoChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(propName, Boolean.FALSE);
					return ctx.registerService(Handler.class, new CaseChangingHandler(), props);
				},
				6, TimeUnit.SECONDS);
	}
	
	/**
	 * A test that registers multiple handlers and uses service ranking to order them as described in 161.1.4
	 * 
	 * This handler is registered <em>after</em> the endpoint that it is applied to
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersOrdering() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
			
			regs.add(ctx.registerService(WSEcho.class, new WSEcho(), props));
			
			props = new Hashtable<>();
			props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			regs.add(ctx.registerService(Handler.class, new ReversingHandler(), props));
			return regs;
		};
		
		List<ServiceRegistration<?>> registered = webServiceRuntimeTracker.waitForChange(
				action, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							if(e.handlers.length == 2) {
								assertEquals(getServiceId(regs.get(1)), e.handlers[0].serviceReference.id, "Wrong handler order in DTO");
								assertEquals(getServiceId(regs.get(2)), e.handlers[1].serviceReference.id, "Wrong handler order in DTO");
								found = true;
							}
							break;
						}
					}
					return found;
				},
				(regs,dto) -> "Unable to find both handlers set for the endpoint in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(ECHO_NS, "echoAction", Maps.mapOf("textIn", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(ECHO_URI, soapRequest);

		assertEquals("gNaB", WebServiceHelper.extractResponse("echoActionResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					props.put(SERVICE_RANKING, 5);
					registered.get(2).setProperties(props);

					return registered;
				}, (regs,dto) -> {
					boolean found = false;
					Object id = regs.get(0).getReference().getProperty(SERVICE_ID);
					for(EndpointDTO e : dto.endpoints) {
						if(id.equals(e.implementor.id)) {
							if(e.handlers.length == 2) {
								assertEquals(getServiceId(regs.get(1)), e.handlers[1].serviceReference.id, "Wrong handler order in DTO after ranking change");
								assertEquals(getServiceId(regs.get(2)), e.handlers[0].serviceReference.id, "Wrong handler order in DTO after ranking change");
								found = true;
							}
							break;
						}
					}
					return found;
				},
				(regs,dto) -> "Unable to find both handlers set for the endpoint in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		soapResponse = WebServiceHelper.getSoapResponse(ECHO_URI, soapRequest);

		assertEquals("gnAB", WebServiceHelper.extractResponse("echoActionResponse", soapResponse),
				"The Web Service and Handlers did not respond correctly");
	}
	
	
	/**
	 * A test which checks the error handling for ungettable services
	 * as described in 160.2.3.1
	 * @throws Exception
	 */
	@Test
	public void testNotGettableHandler() throws Exception {
		
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
					props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
					props.put(WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					ServiceRegistration<?> reg = ctx.registerService(Object.class, new HttpBoundWSEcho(), props);
					
					props = new Hashtable<>();
					props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					ServiceRegistration<?> reg2 = ctx.registerService(Handler.class.getName(), failingFactory, props);
					return Arrays.asList(reg, reg2);
				},
				(reg,dto) -> {
					for(FailedHandlerDTO e : dto.failedHandlers) {
						if(getServiceId(reg.get(1)).equals(e.serviceReference.id)) {
							assertEquals(FAILURE_REASON_SERVICE_NOT_GETTABLE, e.failureCode, "Handler service should not be gettable");
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The endpoint with id " + getServiceId(reg.get(1)) + " was not present in the failed DTO endpoints with code " + 
				FAILURE_REASON_SERVICE_NOT_GETTABLE + ": " + dto,
				6, TimeUnit.SECONDS);
	}
	
	/**
	 * A test which checks the error handling for handlers with no available endpoint services
	 * as described in 160.2.3.2
	 * @throws Exception
	 */
	@Test
	public void testNoEndpointHandler() throws Exception {
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					return ctx.registerService(Handler.class, new CaseChangingHandler(), props);
				},
				(reg,dto) -> {
					for(FailedHandlerDTO e : dto.failedHandlers) {
						if(getServiceId(reg).equals(e.serviceReference.id)) {
							assertEquals(FAILURE_REASON_NO_MATCHING_ENDPOINT, e.failureCode, "Handler service has no endpoints to target");
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The handler with id " + getServiceId(reg) + " was not present in the failed DTO endpoints with code " + 
						FAILURE_REASON_NO_MATCHING_ENDPOINT + ": " + dto,
						6, TimeUnit.SECONDS);
	}

	/**
	 * A test which checks the error handling for handlers with no matching endpoint services
	 * as described in 160.2.3.2
	 * @throws Exception
	 */
	@Test
	public void testNoMatchingEndpointHandler() throws Exception {
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
					props.put(WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					ServiceRegistration<?> reg = ctx.registerService(Object.class, new HttpBoundWSEcho(), props);
					
					props = new Hashtable<>();
					props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					props.put(WEBSERVICE_HANDLER_FILTER, "(foo=bar)");
					ServiceRegistration<?> reg2 = ctx.registerService(Handler.class.getName(), new CaseChangingHandler(), props);
					return Arrays.asList(reg, reg2);				},
				(reg,dto) -> {
					for(FailedHandlerDTO e : dto.failedHandlers) {
						if(getServiceId(reg.get(1)).equals(e.serviceReference.id)) {
							assertEquals(FAILURE_REASON_NO_MATCHING_ENDPOINT, e.failureCode, "Handler service has no matching targets");
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The handler with id " + getServiceId(reg.get(1)) + " was not present in the failed DTO endpoints with code " + 
						FAILURE_REASON_NO_MATCHING_ENDPOINT + ": " + dto,
						6, TimeUnit.SECONDS);
	}
	
	/**
	 * A test which checks the error handling for handlers with invalid filters
	 * as described in 160.2.3.2
	 * @throws Exception
	 */
	@Test
	public void testInvalidFilterHandler() throws Exception {
		
		webServiceRuntimeTracker.waitForChange(
				() -> {
					Hashtable<String, Object> props = new Hashtable<>();
					props.put(WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
					props.put(WEBSERVICE_ENDPOINT_ADDRESS, ECHO_URI);
					ServiceRegistration<?> reg = ctx.registerService(Object.class, new HttpBoundWSEcho(), props);
					
					props = new Hashtable<>();
					props.put(WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					props.put(WEBSERVICE_HANDLER_FILTER, "(foo)&(bar)");
					ServiceRegistration<?> reg2 = ctx.registerService(Handler.class.getName(), new CaseChangingHandler(), props);
					return Arrays.asList(reg, reg2);				},
				(reg,dto) -> {
					for(FailedHandlerDTO e : dto.failedHandlers) {
						if(getServiceId(reg.get(1)).equals(e.serviceReference.id)) {
							assertEquals(FAILURE_REASON_INVALID_FILTER, e.failureCode, "Handler service should have an invalid filter");
							return true;
						}
					}
					return false;
				},
				(reg,dto) -> "The handler with id " + getServiceId(reg.get(1)) + " was not present in the failed DTO endpoints with code " + 
						FAILURE_REASON_INVALID_FILTER + ": " + dto,
						6, TimeUnit.SECONDS);
	}
}
