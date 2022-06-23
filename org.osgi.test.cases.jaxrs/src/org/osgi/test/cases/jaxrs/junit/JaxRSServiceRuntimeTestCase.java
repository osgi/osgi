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

package org.osgi.test.cases.jaxrs.junit;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_DUPLICATE_NAME;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_NOT_AN_EXTENSION_TYPE;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_SERVICE_NOT_GETTABLE;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.FAILURE_REASON_VALIDATION_FAILED;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_EXTENSION;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_NAME;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_RESOURCE;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.ApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.ExtensionDTO;
import org.osgi.service.jaxrs.runtime.dto.FailedApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.FailedExtensionDTO;
import org.osgi.service.jaxrs.runtime.dto.FailedResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceMethodInfoDTO;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.test.cases.jaxrs.applications.SimpleApplication;
import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer.NameBound;
import org.osgi.test.cases.jaxrs.extensions.OSGiTextMimeTypeCodec;
import org.osgi.test.cases.jaxrs.extensions.StringReplacer;
import org.osgi.test.cases.jaxrs.resources.EchoResource;
import org.osgi.test.cases.jaxrs.resources.NameBoundWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;

/**
 * This test covers the lifecycle behaviours described in section 151.4
 */
public class JaxRSServiceRuntimeTestCase extends AbstractJAXRSTestCase {

	private JaxrsServiceRuntime runtimeService;

	@BeforeEach
	public void getRuntimeService() {
		runtimeService = context.getService(runtime);
	}

	/**
	 * Section 151.2.1 Register a simple JAX-RS singleton resource and show that
	 * it appears in the DTOs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWhiteboardResourceDTO() throws Exception {

		ResourceDTO[] resourceDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.resourceDTOs;
		int previousResourceLength = resourceDTOs == null ? 0
				: resourceDTOs.length;

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> reg = context
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		resourceDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.resourceDTOs;

		assertEquals(previousResourceLength + 1, resourceDTOs.length);

		ResourceDTO whiteboardResource = findResourceDTO(resourceDTOs, "test");

		assertNotNull(whiteboardResource);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				whiteboardResource.serviceId);
		assertEquals(5, whiteboardResource.resourceMethods.length);

		for (ResourceMethodInfoDTO infoDTO : whiteboardResource.resourceMethods) {
			checkWhiteboardResourceMethod(infoDTO);
		}
	}

	private void checkWhiteboardResourceMethod(ResourceMethodInfoDTO infoDTO) {
		// The spec is a little ambiguous about whether the @Path annotation's
		// value should be reproduced verbatim or as it would be handled by the
		// container which adds a leading "/" if it's not present. We therefore
		// remove the leading / if there is one to be lenient
		String path = infoDTO.path.startsWith("/") ? infoDTO.path.substring(1)
				: infoDTO.path;

		switch (infoDTO.method) {
			case "DELETE" :
				assertEquals("whiteboard/resource/{name}", path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "PUT" :
				assertEquals("whiteboard/resource/{name}", path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "POST" :
				assertEquals("whiteboard/resource/{oldName}/{newName}",
						path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "GET" :
				if ("whiteboard/resource/{name}".equals(path)
						|| "whiteboard/resource".equals(path)) {
					assertNull(infoDTO.consumingMimeType);
					assertNotNull(infoDTO.producingMimeType);
					assertNull(infoDTO.nameBindings);
					assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				} else {
					fail("Invalid resource path " + path);
				}
				break;
			default :
				fail("Unexpected Method " + infoDTO);
		}
	}

	private ResourceDTO findResourceDTO(ResourceDTO[] resourceDTOs,
			String name) {
		ResourceDTO whiteboardResource = null;
		for (ResourceDTO resource : resourceDTOs) {
			if (name.equals(resource.name)) {
				whiteboardResource = resource;
				break;
			}
		}
		return whiteboardResource;
	}

	/**
	 * Section 151.2.1 Register a simple JAX-RS extension and show that it
	 * appears in the DTOs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWhiteboardExtensionDTO() throws Exception {

		ExtensionDTO[] extensionDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.extensionDTOs;
		int previousExtensionLength = extensionDTOs == null ? 0
				: extensionDTOs.length;

		// Register a whiteboard extension

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration< ? > reg = context
				.registerService(new String[] {
						MessageBodyReader.class.getName(),
						MessageBodyWriter.class.getName()
		}, new OSGiTextMimeTypeCodec(), properties);

		awaitSelection.getValue();

		extensionDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.extensionDTOs;

		assertEquals(previousExtensionLength + 1, extensionDTOs.length);

		ExtensionDTO whiteboardExtension = null;
		for (ExtensionDTO extension : extensionDTOs) {
			if ("test".equals(extension.name)) {
				whiteboardExtension = extension;
				break;
			}
		}

		assertNotNull(whiteboardExtension);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				whiteboardExtension.serviceId);

		assertEquals(
				new HashSet<>(asList(MessageBodyReader.class.getName(),
						MessageBodyWriter.class.getName())),
				new HashSet<>(asList(whiteboardExtension.extensionTypes)));

		assertNotNull(whiteboardExtension.consumes);
		assertEquals(1, whiteboardExtension.consumes.length);
		assertEquals("osgi/text", whiteboardExtension.consumes[0]);
		assertNotNull(whiteboardExtension.produces);
		assertEquals(1, whiteboardExtension.produces.length);
		assertEquals("osgi/text", whiteboardExtension.produces[0]);

		assertNull(whiteboardExtension.nameBindings);
		assertNull(whiteboardExtension.filteredByName);
	}

	/**
	 * Section 151.2.1 Show that name binding is reflected in the DTOs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNameBoundDTOs() throws Exception {

		// Register a whiteboard resource
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<NameBoundWhiteboardResource> resourceReg = context
				.registerService(NameBoundWhiteboardResource.class,
						new NameBoundWhiteboardResource(), properties);

		awaitSelection.getValue();

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, TRUE);
		properties.put(JAX_RS_NAME, "test");
		ServiceRegistration<WriterInterceptor> extensionReg = context
				.registerService(WriterInterceptor.class,
						new BoundStringReplacer("fizz", "fizzbuzz"),
						properties);
		awaitSelection.getValue();

		ExtensionDTO[] extensionDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.extensionDTOs;

		ExtensionDTO whiteboardExtension = null;
		for (ExtensionDTO extension : extensionDTOs) {
			if ("test".equals(extension.name)) {
				whiteboardExtension = extension;
				break;
				}
		}

		assertNotNull(whiteboardExtension);
		assertEquals(extensionReg.getReference().getProperty(SERVICE_ID),
				whiteboardExtension.serviceId);

		assertEquals(asList(NameBound.class.getName()),
				asList(whiteboardExtension.nameBindings));

		assertNotNull(whiteboardExtension.filteredByName);
		assertEquals(1, whiteboardExtension.filteredByName.length);

		checkBoundResourceDTO(resourceReg,
				whiteboardExtension.filteredByName[0]);

		checkBoundResourceDTO(resourceReg, findResourceDTO(
				runtimeService.getRuntimeDTO().defaultApplication.resourceDTOs,
				whiteboardExtension.filteredByName[0].name));

	}

	private void checkBoundResourceDTO(
			ServiceRegistration<NameBoundWhiteboardResource> resourceReg,
			ResourceDTO resourceDTO) {
		assertEquals(resourceReg.getReference().getProperty(SERVICE_ID),
				resourceDTO.serviceId);

		assertNotNull(resourceDTO.resourceMethods);
		assertEquals(2, resourceDTO.resourceMethods.length);

		for (ResourceMethodInfoDTO dto : resourceDTO.resourceMethods) {
			switch (dto.path) {
				case "/whiteboard/name/bound" :
				case "whiteboard/name/bound" :
					assertEquals(asList(NameBound.class.getName()),
							asList(dto.nameBindings));
					break;
				case "/whiteboard/name/unbound" :
				case "whiteboard/name/unbound" :
					assertNull(dto.nameBindings);
					break;
				default :
					fail("Unexpected method " + dto);
			}
		}
	}

	/**
	 * Section 151.2.1 Register a JAX-RS application and show that it appears in
	 * the DTOs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWhiteboardApplicationDTO() throws Exception {

		ApplicationDTO[] applicationDTOs = runtimeService
				.getRuntimeDTO().applicationDTOs;
		int previousApplicationsLength = applicationDTOs == null ? 0
				: applicationDTOs.length;

		// Register a whiteboard application

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_APPLICATION_BASE, "/test");
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		awaitSelection.getValue();

		applicationDTOs = runtimeService.getRuntimeDTO().applicationDTOs;

		ApplicationDTO whiteboardApp = null;
		for (ApplicationDTO app : applicationDTOs) {
			if ("test".equals(app.name)) {
				whiteboardApp = app;
				break;
			}
		}

		assertEquals(previousApplicationsLength + 1, applicationDTOs.length);

		assertNotNull(whiteboardApp);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				whiteboardApp.serviceId);
		assertEquals(5, whiteboardApp.resourceMethods.length);

		for (ResourceMethodInfoDTO infoDTO : whiteboardApp.resourceMethods) {
			checkWhiteboardResourceMethod(infoDTO);
		}
	}

	/**
	 * Section 151.3 Clashing names must trigger a failure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResourcesWithClashingNames() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(EchoResource.class, new EchoResource(),
				properties);

		awaitSelection.getValue();

		// Register a second clashing whiteboard resource
		awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg2 = context.registerService(
				EchoResource.class, new EchoResource(), properties);

		awaitSelection.getValue();

		// Register a whiteboard extension, also with a clashing name

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		ServiceRegistration<WriterInterceptor> extensionReg = context
				.registerService(WriterInterceptor.class,
						new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		FailedResourceDTO[] dtos = runtimeService
				.getRuntimeDTO().failedResourceDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_DUPLICATE_NAME, dtos[0].failureReason);
		assertEquals(reg2.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);

		FailedExtensionDTO[] exDtos = runtimeService
				.getRuntimeDTO().failedExtensionDTOs;
		assertNotNull(exDtos);
		assertEquals(1, exDtos.length);
		assertEquals(FAILURE_REASON_DUPLICATE_NAME, exDtos[0].failureReason);
		assertEquals(extensionReg.getReference().getProperty(SERVICE_ID),
				exDtos[0].serviceId);

	}

	/**
	 * Section 151.3 Missing extensions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMissingExtension() throws Exception {
		// Register a whiteboard resource with an extension requirement
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_EXTENSION_SELECT, "(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = context.registerService(
				EchoResource.class, new EchoResource(), properties);

		awaitSelection.getValue();

		FailedResourceDTO[] dtos = runtimeService
				.getRuntimeDTO().failedResourceDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE,
				dtos[0].failureReason);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);

	}

	/**
	 * Section 151.3 Missing applications
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMissingApplications() throws Exception {
		// Register a whiteboard resource with an application selection
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_APPLICATION_SELECT, "(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = context.registerService(
				EchoResource.class, new EchoResource(), properties);

		awaitSelection.getValue();

		FailedResourceDTO[] dtos = runtimeService
				.getRuntimeDTO().failedResourceDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE,
				dtos[0].failureReason);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);

	}

	/**
	 * 151.2.2.2 bad filter
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidProperty() throws Exception {

		// Register a whiteboard resource with an invalid filter

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_EXTENSION_SELECT, "...foo=bar...");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = context.registerService(
				EchoResource.class, new EchoResource(), properties);

		awaitSelection.getValue();

		FailedResourceDTO[] dtos = runtimeService
				.getRuntimeDTO().failedResourceDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_VALIDATION_FAILED, dtos[0].failureReason);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);

	}

	/**
	 * 151.5 - must be advertised as an acceptable type
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidExtensionType() throws Exception {
		// Register a whiteboard extension as an invalid type
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<OSGiTextMimeTypeCodec> reg = context
				.registerService(OSGiTextMimeTypeCodec.class,
						new OSGiTextMimeTypeCodec(), properties);

		awaitSelection.getValue();

		FailedExtensionDTO[] dtos = runtimeService
				.getRuntimeDTO().failedExtensionDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_NOT_AN_EXTENSION_TYPE,
				dtos[0].failureReason);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);
	}

	/**
	 * 151.2.2.2 not gettable
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUngettableService() throws Exception {

		// Register a whiteboard resource that fails to get

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = context.registerService(
				EchoResource.class,
				getPrototypeServiceFactory(() -> null, (a, b) -> {}),
				properties);

		awaitSelection.getValue();

		FailedResourceDTO[] dtos = runtimeService
				.getRuntimeDTO().failedResourceDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_SERVICE_NOT_GETTABLE,
				dtos[0].failureReason);
		assertEquals(reg.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);

	}

	/**
	 * Section 151.6.1 Clashing names must trigger a failure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationShadowing() throws Exception {

		// Register a whiteboard application at "/clash"

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_APPLICATION_BASE, "/clash");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		awaitSelection.getValue();

		// Register a second whiteboard application at "/clash"

		awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg2 = context
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		awaitSelection.getValue();

		FailedApplicationDTO[] dtos = runtimeService
				.getRuntimeDTO().failedApplicationDTOs;
		assertNotNull(dtos);
		assertEquals(1, dtos.length);
		assertEquals(FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				dtos[0].failureReason);
		assertEquals(reg2.getReference().getProperty(SERVICE_ID),
				dtos[0].serviceId);
	}
}
