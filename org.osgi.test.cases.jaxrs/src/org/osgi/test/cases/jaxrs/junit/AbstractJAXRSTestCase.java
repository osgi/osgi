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

import static org.osgi.service.jaxrs.runtime.JaxRSServiceRuntimeConstants.JAX_RS_SERVICE_ENDPOINT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxRSServiceRuntime;
import org.osgi.test.cases.jaxrs.util.ServiceUpdateHelper;
import org.osgi.test.support.OSGiTestCase;

/**
 * This test covers the lifecycle behaviours described in section 151.4.2
 */
public abstract class AbstractJAXRSTestCase extends OSGiTestCase {
	
	protected CloseableHttpClient					client;

	protected ServiceUpdateHelper					helper;

	protected ServiceReference<JaxRSServiceRuntime>	runtime;

	public void setUp() {
		client = HttpClients.createDefault();

		helper = new ServiceUpdateHelper(getContext());
		helper.open();

		runtime = helper.awaitRuntime(5000);
	}

	public void tearDown() throws IOException {
		helper.close();
		client.close();
	}

	protected String getBaseURI() {
		Object value = runtime.getProperty(JAX_RS_SERVICE_ENDPOINT);

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
