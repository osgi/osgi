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


package org.osgi.test.cases.jakartars.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.osgi.service.jakartars.runtime.JakartarsServiceRuntimeConstants.JAKARTA_RS_SERVICE_ENDPOINT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;
import org.osgi.test.cases.jakartars.util.ServiceUpdateHelper;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.util.promise.Promises;

/**
 * This test covers the lifecycle behaviours described in section 151.4.2
 */
@ExtendWith(BundleContextExtension.class)
public abstract class AbstractJakartarsTestCase {
	
	@InjectBundleContext
	protected BundleContext							context;

	protected CloseableHttpClient					client;

	protected ServiceUpdateHelper					helper;

	protected ServiceReference<JakartarsServiceRuntime>	runtime;

	@BeforeEach
	public void setUp() {
		client = HttpClients.createDefault();

		helper = new ServiceUpdateHelper(context);
		helper.open();

		runtime = helper.awaitRuntime(5000);
		try {
			while (helper.awaitModification(runtime, 200)
					.map(x -> Boolean.TRUE)
					.fallbackTo(Promises.resolved(Boolean.FALSE))
					.getValue())
				;
		} catch (Exception e) {
			throw new IllegalStateException(
					"Failed while waiting for the whiteboard to settle before the test");
		}
	}

	@AfterEach
	public void tearDown() throws IOException {
		helper.close();
		client.close();
	}

	protected String getBaseURI() {
		Object value = runtime.getProperty(JAKARTA_RS_SERVICE_ENDPOINT);

		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof String[]) {
			return ((String[]) value)[0];
		} else if (value instanceof Collection) {
			return String.valueOf(((Collection< ? >) value).iterator().next());
		}

		throw new IllegalArgumentException(
				"The JAXRS Service Runtime did not declare an endpoint property");
	}

	protected String assertResponse(CloseableHttpResponse response,
			int responseCode, String mediaType) throws IOException {

		assertEquals(responseCode, response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();

		if (mediaType != null) {
			assertEquals(mediaType, entity.getContentType().getValue());
		}

		if (entity != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			return baos.toString("UTF-8");
		}
		return null;
	}

	protected <T> PrototypeServiceFactory<T> getPrototypeServiceFactory(
			Supplier<T> supplier,
			BiConsumer<ServiceRegistration<T>,T> destroyer) {
		return new PrototypeServiceFactory<T>() {

			@Override
			public T getService(Bundle bundle,
					ServiceRegistration<T> registration) {
				return supplier.get();
			}

			@Override
			public void ungetService(Bundle bundle,
					ServiceRegistration<T> registration, T service) {
				destroyer.accept(registration, service);
			}

		};
	}

	protected <T> ServiceFactory<T> getServiceFactory(Supplier<T> supplier,
			BiConsumer<ServiceRegistration<T>,T> destroyer) {
		return new ServiceFactory<T>() {

			@Override
			public T getService(Bundle bundle,
					ServiceRegistration<T> registration) {
				return supplier.get();
			}

			@Override
			public void ungetService(Bundle bundle,
					ServiceRegistration<T> registration, T service) {
				destroyer.accept(registration, service);
			}

		};
	}
}
