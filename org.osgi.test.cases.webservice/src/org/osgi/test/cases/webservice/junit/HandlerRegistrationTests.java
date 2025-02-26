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
import static org.osgi.test.cases.webservice.junit.EndpointRegistrationTests.getServiceId;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.osgi.test.cases.webservice.webservices.Reflect;
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
		assertTrue(webServiceRuntimeTracker.hasChanged(), "No Change Count detected for the WebserviceServiceRuntime");
	}
	
	@AfterEach
	void shutdown() {
		webServiceRuntimeTracker.close();
	}
	
	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterFirst() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
			regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
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
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterSecond() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
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
				 " and endpoint " + getServiceId(regs.get(2)) + " in the dto: " + dto,
				6, TimeUnit.SECONDS);
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse),
				"The Web Service and Handler did not respond correctly");
	}

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlerMultipleEndpoints() throws Exception {
		
		Callable<List<ServiceRegistration<?>>> action = () -> {
			List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
			
			Hashtable<String, Object> props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI_2);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			
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
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
			props.put(OSGI_WEBSERVICE_TCK_ID, id);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI_2);
			props.put(OSGI_WEBSERVICE_TCK_ID, id2);
			
			regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
			
			props = new Hashtable<>();
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
			props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_FILTER, filter);
			
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
					props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
					return ctx.registerService(WSEcho.class.getName(), failingFactory, props);
				},
				(reg,dto) -> {
					for(FailedHandlerDTO e : dto.failedHandlers) {
						if(getServiceId(reg).equals(e.serviceReference.id)) {
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
