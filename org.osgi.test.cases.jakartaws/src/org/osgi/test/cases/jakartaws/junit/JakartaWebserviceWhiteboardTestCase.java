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
package org.osgi.test.cases.jakartaws.junit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.jakarta.xml.ws.runtime.WebserviceServiceRuntime;
import org.osgi.service.jakarta.xml.ws.runtime.dto.EndpointDTO;
import org.osgi.service.jakarta.xml.ws.runtime.dto.FailedEndpointDTO;
import org.osgi.service.jakarta.xml.ws.runtime.dto.FailedHandlerDTO;
import org.osgi.service.jakarta.xml.ws.runtime.dto.HandlerDTO;
import org.osgi.service.jakarta.xml.ws.runtime.dto.RuntimeDTO;
import org.osgi.service.jakarta.xml.ws.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.test.cases.jakartaws.junit.binding.WSEchoService;
import org.osgi.test.cases.jakartaws.junit.handler.BadHandler;
import org.osgi.test.cases.jakartaws.junit.handler.InvalidHandler;
import org.osgi.test.cases.jakartaws.junit.handler.TestLogicalHandler;
import org.osgi.test.cases.jakartaws.junit.handler.TestSoapHandler;
import org.osgi.test.cases.jakartaws.junit.implementor.BadImplementor;
import org.osgi.test.cases.jakartaws.webservice.WSEcho;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.xml.ws.handler.Handler;

@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class JakartaWebserviceWhiteboardTestCase {

	private static final String	HANDLER_BAD				= "bad";
	private static final String	HANDLER_SOAP			= "soap";
	private static final String	HANDLER_LOGICAL			= "logical";
	private static final String	HANDLER_INVALID			= "invalid";
	private static final String	HANDLER_TYPE			= "type";
	private static final String	DEFAULT_PUBLISH_ADDRESS	= System.getProperty(
			"org.osgi.test.cases.jakartaws.defaultaddress",
			"http://localhost:8579");
	private static final String	KEY_UUUID				= "UUUID";
	@InjectService(timeout = 10000)
	WebserviceServiceRuntime	runtime;

	@Test
	public void testEchoService(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		String id = UUID.randomUUID().toString();
		String filter = String.format("(%s=%s)", KEY_UUUID, id);
		TestLogicalHandler logicalHandler = registerLogicalHandler(
				bundleContext, filter);
		TestSoapHandler soapHandler = registerSoapHandler(bundleContext,
				filter);
		String publishAddress = DEFAULT_PUBLISH_ADDRESS + "/wsecho";
		EndpointDTO endpoint = registerEchoEndpoint(bundleContext, id,
				publishAddress);
		assertThat(endpoint.address).as("Endpoint Address")
				.isEqualTo(publishAddress);
		assertThat(endpoint.handlers)
				.areAtLeastOne(new Condition<HandlerDTO>(dto -> {
					return HANDLER_LOGICAL.equals(
							dto.serviceReference.properties.get(HANDLER_TYPE));
				}, "LogicalHandler is bound"))
				.areAtLeastOne(new Condition<HandlerDTO>(dto -> {
					return HANDLER_SOAP.equals(
							dto.serviceReference.properties.get(HANDLER_TYPE));
				}, "SoapHandler is bound"));
		WSEchoService service = new WSEchoService(
				new URL(endpoint.address + "?wsdl"));
		String textIn = "Hello World";
		String echo = service.getEchoPort().echo(textIn);
		assertThat(echo).as("Returned Text").isEqualTo(textIn);
		assertThat(logicalHandler.handledMessages.get()).isEqualTo(2);
		assertThat(soapHandler.handledMessages.get()).isEqualTo(2);
	}

	@Test
	public void testFailedHandler(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		String publishAddress = DEFAULT_PUBLISH_ADDRESS + "/dontcare";
		String id = UUID.randomUUID().toString();
		String filter = String.format("(%s=%s)", KEY_UUUID, id);
		// This handler *does* match the webservice but returns a null service
		// on each call
		bundleContext.registerService(Handler.class, new BadHandler(),
				FrameworkUtil.asDictionary(
						Map.of(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_SELECT,
								filter, HANDLER_TYPE, HANDLER_BAD)));
		// this handler simply do not match at all
		registerLogicalHandler(bundleContext, "(a=b)");
		// this handler has an invalid filter
		registerSoapHandler(bundleContext, "XSYOJHF6&/8+#=");
		EndpointDTO endpoint = registerEchoEndpoint(bundleContext, id,
				publishAddress);
		assertThat(endpoint.handlers).areNot(new Condition<HandlerDTO>(dto -> {
			return HANDLER_BAD
					.equals(dto.serviceReference.properties.get(HANDLER_TYPE));
		}, "Bad Handler is bound"));
		assertFailedHandler(HANDLER_BAD,
				FailedHandlerDTO.FAILURE_REASON_SERVICE_NOT_GETTABLE);
		assertFailedHandler(HANDLER_LOGICAL,
				FailedHandlerDTO.FAILURE_REASON_NO_MATCHING_ENDPOINT);
		assertFailedHandler(HANDLER_SOAP,
				FailedHandlerDTO.FAILURE_REASON_INVALID_FILTER);
	}

	@Test
	public void testFailedEndpointFailedPublish(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		// The invalid implementor has no annotations
		String invalidPublishAddress = DEFAULT_PUBLISH_ADDRESS + "/invalid";
		bundleContext.registerService(Object.class, new Object(),
				getImplementorProperties(invalidPublishAddress));
		assertFailedEndpoint(invalidPublishAddress,
				FailedEndpointDTO.FAILURE_REASON_PUBLISH_FAILED);
	}

	@Test
	public void testFailedEndpointLockupFailed(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		// The bad implementor return null for its service!
		String badPublishAddress = DEFAULT_PUBLISH_ADDRESS + "/bad";
		bundleContext.registerService(String.class, new BadImplementor(),
				getImplementorProperties(badPublishAddress));
		assertFailedEndpoint(badPublishAddress,
				FailedEndpointDTO.FAILURE_REASON_SERVICE_NOT_GETTABLE);
	}

	@Test
	public void testFailedEndpointInvalidHandler(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		// Here we have a valid endpoint but invalid handler
		String wrongPublishAddress = DEFAULT_PUBLISH_ADDRESS + "/wrong";
		String id = UUID.randomUUID().toString();
		String filter = String.format("(%s=%s)", KEY_UUUID, id);
		bundleContext.registerService(Handler.class, new InvalidHandler(),
				FrameworkUtil.asDictionary(
						Map.of(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_SELECT,
								filter, HANDLER_TYPE, HANDLER_INVALID)));
		Hashtable<String,Object> properties = getImplementorProperties(
				wrongPublishAddress);
		properties.put(KEY_UUUID, id);
		bundleContext.registerService(WSEcho.class, new WSEcho(), properties);
		assertFailedEndpoint(wrongPublishAddress,
				FailedEndpointDTO.FAILURE_REASON_SET_HANDLER_FAILED);
	}

	private void assertFailedEndpoint(String publishAddress, int code) {
		FailedEndpointDTO failedEndpoint = waitForDTO(5, SECONDS, dto -> {
			assertThat(dto.failedEndpoints).as("Failed Endpoint DTO")
					.isNotNull();
			for (FailedEndpointDTO failed : dto.failedEndpoints) {
				if (publishAddress.equals(failed.implementor.properties
						.get(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS))) {
					return failed;
				}
			}
			return null;
		}, "Endpoint " + publishAddress + " not marked as failed");
		assertThat(failedEndpoint.failureCode)
				.as("Failure Code of endpoint " + publishAddress + " (returned "
						+ failedEndpoint + ")")
				.isEqualTo(code);
	}

	private Hashtable<String,Object> getImplementorProperties(
			String publishAddress) {
		Hashtable<String,Object> properties = new Hashtable<>();
		properties.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_IMPLEMENTOR, true);
		properties.put(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_ADDRESS,
				publishAddress);
		return properties;
	}

	private void assertFailedHandler(String type, int code) {
		FailedHandlerDTO failedBad = waitForDTO(5, SECONDS, dto -> {
			assertThat(dto.failedHandlers).as("Failed Handler DTO").isNotNull();
			for (FailedHandlerDTO failed : dto.failedHandlers) {
				if (type.equals(
						failed.serviceReference.properties.get(HANDLER_TYPE))) {
					return failed;
				}
			}
			return null;
		}, "Handler " + type + " not marked as failed");
		assertThat(failedBad.failureCode).as("Failure Code of handler " + type)
				.isEqualTo(code);
	}

	private EndpointDTO registerEchoEndpoint(BundleContext bundleContext,
			String id, String publishAddress) {
		Hashtable<String,Object> properties = getImplementorProperties(
				publishAddress);
		properties.put(KEY_UUUID, id);
		bundleContext.registerService(WSEcho.class, new WSEcho(), properties);
		EndpointDTO endpoint = waitForDTO(10, SECONDS, dto -> {
			assertThat(dto.endpoints).as("Endpoints DTO").isNotNull();
			for (EndpointDTO ep : dto.endpoints) {
				assertThat(ep.implementor).as("Endpoint Implementor DTO")
						.isNotNull();
				if (ep.implementor.bundle == bundleContext.getBundle()
						.getBundleId()) {
					System.out.println("Waiting for " + id + " eq "
							+ ep.implementor.properties.get(KEY_UUUID));
					if (id.equals(ep.implementor.properties.get(KEY_UUUID))) {
						assertThat(ep.address).as("Publish Address")
								.isNotNull();
						return ep;
					}
				}
			}
			return null;
		});
		return endpoint;
	}

	private TestSoapHandler registerSoapHandler(BundleContext bundleContext,
			String filter) {
		TestSoapHandler soapHandler = new TestSoapHandler();
		bundleContext.registerService(Handler.class, soapHandler,
				FrameworkUtil.asDictionary(
						Map.of(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_SELECT,
								filter, HANDLER_TYPE, HANDLER_SOAP)));
		return soapHandler;
	}

	private TestLogicalHandler registerLogicalHandler(
			BundleContext bundleContext, String filter) {
		TestLogicalHandler logicalHandler = new TestLogicalHandler();
		bundleContext.registerService(Handler.class, logicalHandler,
				FrameworkUtil.asDictionary(
						Map.of(WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_SELECT,
								filter, HANDLER_TYPE, HANDLER_LOGICAL)));
		return logicalHandler;
	}

	private <T> T waitForDTO(long time, TimeUnit unit,
			Function<RuntimeDTO,T> tester) {
		return waitForDTO(time, unit, tester, "Timeout waiting for valid DTO");
	}

	private <T> T waitForDTO(long time, TimeUnit unit,
			Function<RuntimeDTO,T> tester, String msg) {
		long deadline = System.currentTimeMillis() + unit.toMillis(time);
		while (System.currentTimeMillis() < deadline) {
			RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
			assertThat(runtimeDTO).as("RuntimeDTO").isNotNull();
			T result = tester.apply(runtimeDTO);
			if (result != null) {
				return result;
			}
			Thread.yield();
		}
		Assertions.fail(msg + " // current DTO: " + runtime.getRuntimeDTO());
		return null;
	}
}
