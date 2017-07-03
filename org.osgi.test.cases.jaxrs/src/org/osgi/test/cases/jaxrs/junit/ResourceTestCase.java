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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxRSServiceRuntime;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.test.cases.jaxrs.util.ServiceUpdateHelper;
import org.osgi.test.support.OSGiTestCase;

public class ResourceTestCase extends OSGiTestCase {
	
	private CloseableHttpClient						client;

	private ServiceUpdateHelper						helper;
	// TODO remove when this is actually used
	@SuppressWarnings("unused")
	private ServiceReference<JaxRSServiceRuntime>	runtime;

	public void setUp() {
		client = HttpClients.createDefault();

		helper = new ServiceUpdateHelper(getContext());
		helper.open();

		// TODO get hold of this when the RI supports it

		// runtime = helper.awaitRuntime(5000);
	}

	public void tearDown() throws IOException {
		helper.close();
		client.close();
	}

	public void testSimpleSingletonResource() throws Exception {
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		
		// TODO use this promise to avoid racing the container

		// Promise<Void> awaitSelection = helper.awaitModification(runtime,
		// 5000);

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class, new WhiteboardResource(), properties);
		
		try {

			// TODO actually wait for the race to finish, and get hold of the
			// URI

			// awaitSelection.getValue();
			
			// String baseURI = (String) runtime
			// .getProperty(JAX_RS_SERVICE_ENDPOINT);

			// TODO stop using this, as it may be wrong if the container doesn't
			// use the root context or it uses a different port

			String baseURI = new URI("http", null,
					InetAddress.getLocalHost().getHostAddress(), 8080, null,
					null, null).toString() + "/";

			CloseableHttpResponse response = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			assertEquals(200, response.getStatusLine().getStatusCode());

			HttpEntity entity = response.getEntity();

			assertEquals(MediaType.TEXT_PLAIN,
					entity.getContentType().getValue());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);

			assertEquals("[buzz, fizz, fizzbuzz]", baos.toString("UTF-8"));
		
		} finally {
			reg.unregister();
		}
	}


}
