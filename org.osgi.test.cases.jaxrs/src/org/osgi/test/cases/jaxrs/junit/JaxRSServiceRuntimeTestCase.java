/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.jaxrs.junit;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.service.jaxrs.runtime.dto.DTOConstants.*;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.*;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;

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

	public void setUp() {
		super.setUp();
		runtimeService = getContext().getService(runtime);
	}

	public void tearDown() throws IOException {
		getContext().ungetService(runtime);
		super.tearDown();
	}

	/**
	 * Section 151.2.1 Register a simple JAX-RS singleton resource and show that
	 * it appears in the DTOs
	 * 
	 * @throws Exception
	 */
	public void testWhiteboardResourceDTO() throws Exception {

		ResourceDTO[] resourceDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.resourceDTOs;
		int previousResourceLength = resourceDTOs == null ? 0
				: resourceDTOs.length;

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			resourceDTOs = runtimeService
					.getRuntimeDTO().defaultApplication.resourceDTOs;

			assertEquals(previousResourceLength + 1, resourceDTOs.length);

			ResourceDTO whiteboardResource = findResourceDTO(resourceDTOs,
					"test");

			assertNotNull(whiteboardResource);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					whiteboardResource.serviceId);
			assertEquals(5, whiteboardResource.resourceMethods.length);

			for (ResourceMethodInfoDTO infoDTO : whiteboardResource.resourceMethods) {
				checkWhiteboardResourceMethod(infoDTO);
			}
		} finally {
			reg.unregister();
		}
	}

	private void checkWhiteboardResourceMethod(ResourceMethodInfoDTO infoDTO) {
		switch (infoDTO.method) {
			case "DELETE" :
				assertEquals("whiteboard/resource/{name}", infoDTO.path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "PUT" :
				assertEquals("whiteboard/resource/{name}", infoDTO.path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "POST" :
				assertEquals("whiteboard/resource/{oldName}/{newName}",
						infoDTO.path);
				assertNull(infoDTO.consumingMimeType);
				assertNotNull(infoDTO.producingMimeType);
				assertNull(infoDTO.nameBindings);
				assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				break;
			case "GET" :
				if ("whiteboard/resource/{name}".equals(infoDTO.path)
						|| "whiteboard/resource".equals(infoDTO.path)) {
					assertNull(infoDTO.consumingMimeType);
					assertNotNull(infoDTO.producingMimeType);
					assertNull(infoDTO.nameBindings);
					assertEquals(TEXT_PLAIN, infoDTO.producingMimeType[0]);
				} else {
					fail("Invalid resource path " + infoDTO.path);
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
	public void testWhiteboardExtensionDTO() throws Exception {

		ExtensionDTO[] extensionDTOs = runtimeService
				.getRuntimeDTO().defaultApplication.extensionDTOs;
		int previousExtensionLength = extensionDTOs == null ? 0
				: extensionDTOs.length;

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration< ? > reg = getContext()
				.registerService(new String[] {
						MessageBodyReader.class.getName(),
						MessageBodyWriter.class.getName()
		}, new OSGiTextMimeTypeCodec(), properties);

		try {

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
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.2.1 Show that name binding is reflected in the DTOs
	 * 
	 * @throws Exception
	 */
	public void testNameBoundDTOs() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<NameBoundWhiteboardResource> resourceReg = getContext()
				.registerService(NameBoundWhiteboardResource.class,
						new NameBoundWhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JAX_RS_EXTENSION, TRUE);
			properties.put(JAX_RS_NAME, "test");
			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new BoundStringReplacer("fizz", "fizzbuzz"),
							properties);
			try {
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
				assertEquals(
						extensionReg.getReference().getProperty(SERVICE_ID),
						whiteboardExtension.serviceId);

				assertEquals(asList(NameBound.class.getName()),
						asList(whiteboardExtension.nameBindings));

				assertNotNull(whiteboardExtension.filteredByName);
				assertEquals(1, whiteboardExtension.filteredByName.length);

				checkBoundResourceDTO(resourceReg,
						whiteboardExtension.filteredByName[0]);

				checkBoundResourceDTO(resourceReg,
						findResourceDTO(
								runtimeService
										.getRuntimeDTO().defaultApplication.resourceDTOs,
								whiteboardExtension.filteredByName[0].name));
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
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
				case "bound" :
					assertEquals(asList(NameBound.class.getName()),
							asList(dto.nameBindings));
					break;
				case "unbound" :
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
	public void testWhiteboardApplicationDTO() throws Exception {

		ApplicationDTO[] applicationDTOs = runtimeService
				.getRuntimeDTO().applicationDTOs;
		int previousApplicationsLength = applicationDTOs == null ? 0
				: applicationDTOs.length;

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_APPLICATION_BASE, "/test");
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<SimpleApplication> reg = getContext()
				.registerService(SimpleApplication.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		try {

			awaitSelection.getValue();

			applicationDTOs = runtimeService.getRuntimeDTO().applicationDTOs;

			ApplicationDTO whiteboardApp = null;
			for (ApplicationDTO app : applicationDTOs) {
				if ("test".equals(app.name)) {
					whiteboardApp = app;
					break;
				}
			}

			assertEquals(previousApplicationsLength + 1,
					applicationDTOs.length);

			assertNotNull(whiteboardApp);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					whiteboardApp.serviceId);
			assertEquals(5, whiteboardApp.resourceMethods.length);

			for (ResourceMethodInfoDTO infoDTO : whiteboardApp.resourceMethods) {
				checkWhiteboardResourceMethod(infoDTO);
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.3 Clashing names must trigger a failure
	 * 
	 * @throws Exception
	 */
	public void testResourcesWithClashingNames() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = getContext().registerService(
				EchoResource.class, new EchoResource(), properties);

		try {
			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			ServiceRegistration<EchoResource> reg2 = getContext()
					.registerService(EchoResource.class, new EchoResource(),
							properties);

			try {
				awaitSelection.getValue();

				awaitSelection = helper.awaitModification(runtime, 5000);

				properties = new Hashtable<>();
				properties.put(JAX_RS_EXTENSION, Boolean.TRUE);
				properties.put(JAX_RS_NAME, "test");

				ServiceRegistration<WriterInterceptor> extensionReg = getContext()
						.registerService(WriterInterceptor.class,
								new StringReplacer("fizz", "fizzbuzz"),
								properties);

				try {
					awaitSelection.getValue();

					FailedResourceDTO[] dtos = runtimeService
							.getRuntimeDTO().failedResourceDTOs;
					assertNotNull(dtos);
					assertEquals(1, dtos.length);
					assertEquals(FAILURE_REASON_DUPLICATE_NAME,
							dtos[0].failureReason);
					assertEquals(reg2.getReference().getProperty(SERVICE_ID),
							dtos[0].serviceId);

					FailedExtensionDTO[] exDtos = runtimeService
							.getRuntimeDTO().failedExtensionDTOs;
					assertNotNull(exDtos);
					assertEquals(1, exDtos.length);
					assertEquals(FAILURE_REASON_DUPLICATE_NAME,
							exDtos[0].failureReason);
					assertEquals(
							extensionReg.getReference().getProperty(SERVICE_ID),
							exDtos[0].serviceId);
				} finally {
					extensionReg.unregister();
				}
			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.3 Missing extensions
	 * 
	 * @throws Exception
	 */
	public void testMissingExtension() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_EXTENSION_SELECT, "(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = getContext().registerService(
				EchoResource.class, new EchoResource(), properties);

		try {
			awaitSelection.getValue();

			FailedResourceDTO[] dtos = runtimeService
					.getRuntimeDTO().failedResourceDTOs;
			assertNotNull(dtos);
			assertEquals(1, dtos.length);
			assertEquals(FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE,
					dtos[0].failureReason);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					dtos[0].serviceId);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.3 Missing applications
	 * 
	 * @throws Exception
	 */
	public void testMissingApplications() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_APPLICATION_SELECT, "(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = getContext().registerService(
				EchoResource.class, new EchoResource(), properties);

		try {
			awaitSelection.getValue();

			FailedResourceDTO[] dtos = runtimeService
					.getRuntimeDTO().failedResourceDTOs;
			assertNotNull(dtos);
			assertEquals(1, dtos.length);
			assertEquals(FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE,
					dtos[0].failureReason);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					dtos[0].serviceId);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * 151.2.2.2 bad filter
	 * 
	 * @throws Exception
	 */
	public void testInvalidProperty() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JAX_RS_EXTENSION_SELECT, "...foo=bar...");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = getContext().registerService(
				EchoResource.class, new EchoResource(), properties);

		try {
			awaitSelection.getValue();

			FailedResourceDTO[] dtos = runtimeService
					.getRuntimeDTO().failedResourceDTOs;
			assertNotNull(dtos);
			assertEquals(1, dtos.length);
			assertEquals(FAILURE_REASON_VALIDATION_FAILED,
					dtos[0].failureReason);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					dtos[0].serviceId);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * 151.5 - must be advertised as an acceptable type
	 * 
	 * @throws Exception
	 */
	public void testInvalidExtensionType() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_EXTENSION, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<OSGiTextMimeTypeCodec> reg = getContext()
				.registerService(OSGiTextMimeTypeCodec.class,
						new OSGiTextMimeTypeCodec(), properties);

		try {
			awaitSelection.getValue();

			FailedExtensionDTO[] dtos = runtimeService
					.getRuntimeDTO().failedExtensionDTOs;
			assertNotNull(dtos);
			assertEquals(1, dtos.length);
			assertEquals(FAILURE_REASON_NOT_AN_EXTENSION_TYPE,
					dtos[0].failureReason);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					dtos[0].serviceId);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * 151.2.2.2 not gettable
	 * 
	 * @throws Exception
	 */
	public void testUngettableService() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> reg = getContext().registerService(
				EchoResource.class,
				getPrototypeServiceFactory(() -> null, (a, b) -> {}),
				properties);

		try {
			awaitSelection.getValue();

			FailedResourceDTO[] dtos = runtimeService
					.getRuntimeDTO().failedResourceDTOs;
			assertNotNull(dtos);
			assertEquals(1, dtos.length);
			assertEquals(FAILURE_REASON_SERVICE_NOT_GETTABLE,
					dtos[0].failureReason);
			assertEquals(reg.getReference().getProperty(SERVICE_ID),
					dtos[0].serviceId);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6.1 Clashing names must trigger a failure
	 * 
	 * @throws Exception
	 */
	public void testApplicationShadowing() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JAX_RS_APPLICATION_BASE, "/clash");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<SimpleApplication> reg = getContext()
				.registerService(SimpleApplication.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		try {
			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			ServiceRegistration<SimpleApplication> reg2 = getContext()
					.registerService(SimpleApplication.class,
							new SimpleApplication(emptySet(),
									singleton(new WhiteboardResource())),
							properties);

			try {
				awaitSelection.getValue();

				FailedApplicationDTO[] dtos = runtimeService
						.getRuntimeDTO().failedApplicationDTOs;
				assertNotNull(dtos);
				assertEquals(1, dtos.length);
				assertEquals(FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
						dtos[0].failureReason);
				assertEquals(reg2.getReference().getProperty(SERVICE_ID),
						dtos[0].serviceId);
			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}
}
