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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.service.webservice.runtime.dto.EndpointDTO;
import org.osgi.service.webservice.runtime.dto.HandlerDTO;
import org.osgi.service.webservice.runtime.dto.RuntimeDTO;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.test.cases.webservice.webservices.CaseChangingHandler;
import org.osgi.test.cases.webservice.webservices.Reflect;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.support.map.Maps;

import jakarta.xml.ws.handler.Handler;

public class HandlerRegistrationTests {
	
	private static final String OSGI_WEBSERVICE_TCK_ID = "osgi.webservice.tck.id";
	private static final String REFLECT_URI = "http://127.0.0.1:21980/reflect";
	private static final String REFLECT_URI_2 = "http://127.0.0.1:21980/reflect2";

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterFirst(
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {
		
		List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
		
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
		
		regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
		
		props = new Hashtable<>();
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
		
		regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
		
		// TODO improve this when it is specced
		for(ServiceRegistration<?> reg : regs) {
			boolean found = false;
			loop: for(int i = 0; i < 30; i++) {
				RuntimeDTO dto = runtime.getRuntimeDTO();
				for(EndpointDTO e : dto.endpoints) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
						found = true;
						break loop;
					}
				}
				for(HandlerDTO e : dto.handlers) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.serviceReference.id)) {
						found = true;
						break loop;
					}
				}
				Thread.sleep(200);
			}
			
			assertTrue(found);
		}
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse));
	}

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlersRegisterSecond(
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {
		
		List<ServiceRegistration<?>> regs = new ArrayList<ServiceRegistration<?>>();
		
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, Boolean.TRUE);
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS, REFLECT_URI);
		
		regs.add(ctx.registerService(Reflect.class, new Reflect(), props));
		
		props = new Hashtable<>();
		props.put(WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_EXTENSION, Boolean.TRUE);
		
		regs.add(ctx.registerService(Handler.class, new CaseChangingHandler(), props));
		
		// TODO improve this when it is specced
		for(ServiceRegistration<?> reg : regs) {
			boolean found = false;
			loop: for(int i = 0; i < 30; i++) {
				RuntimeDTO dto = runtime.getRuntimeDTO();
				for(EndpointDTO e : dto.endpoints) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
						found = true;
						break loop;
					}
				}
				for(HandlerDTO e : dto.handlers) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.serviceReference.id)) {
						found = true;
						break loop;
					}
				}
				Thread.sleep(200);
			}
			
			assertTrue(found);
		}
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse));
	}

	/**
	 * A basic test that registers a handler as described in 161.1.4
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlerMultipleEndpoints(
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {
		
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
		
		// TODO improve this when it is specced
		for(ServiceRegistration<?> reg : regs) {
			boolean found = false;
			loop: for(int i = 0; i < 30; i++) {
				RuntimeDTO dto = runtime.getRuntimeDTO();
				for(EndpointDTO e : dto.endpoints) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
						found = true;
						break loop;
					}
				}
				for(HandlerDTO e : dto.handlers) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.serviceReference.id)) {
						found = true;
						break loop;
					}
				}
				Thread.sleep(200);
			}
			
			assertTrue(found);
		}
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		String soapRequest2 = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BONG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		String soapResponse2 = WebServiceHelper.getSoapResponse(REFLECT_URI_2, soapRequest2);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse));
		assertEquals("gNoB", WebServiceHelper.extractResponse("reflectResponse", soapResponse2));
	}

	/**
	 * A basic test that registers a handler with filtering as described in 161.1.5
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandlerMultipleEndpointsFiltered(
			@InjectBundleContext BundleContext ctx,
			@InjectService WebserviceServiceRuntime runtime) throws Exception {
		
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
		
		// TODO improve this when it is specced
		for(ServiceRegistration<?> reg : regs) {
			boolean found = false;
			loop: for(int i = 0; i < 30; i++) {
				RuntimeDTO dto = runtime.getRuntimeDTO();
				for(EndpointDTO e : dto.endpoints) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.implementor.id)) {
						found = true;
						break loop;
					}
				}
				for(HandlerDTO e : dto.handlers) {
					if(reg.getReference().getProperty(SERVICE_ID).equals(e.serviceReference.id)) {
						found = true;
						break loop;
					}
				}
				Thread.sleep(200);
			}
			
			assertTrue(found);
		}
		
		String soapRequest = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BANG"));
		String soapRequest2 = WebServiceHelper.createSOAPMessage(Reflect.REFLECT_NS, "reflect", Maps.mapOf("message", "BONG"));
		
		String soapResponse = WebServiceHelper.getSoapResponse(REFLECT_URI, soapRequest);
		String soapResponse2 = WebServiceHelper.getSoapResponse(REFLECT_URI_2, soapRequest2);
		
		assertEquals("gNaB", WebServiceHelper.extractResponse("reflectResponse", soapResponse));
		assertEquals("GNOB", WebServiceHelper.extractResponse("reflectResponse", soapResponse2));
	}

	
}
