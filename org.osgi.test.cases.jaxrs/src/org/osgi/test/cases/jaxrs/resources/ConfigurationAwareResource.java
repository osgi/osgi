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
package org.osgi.test.cases.jaxrs.resources;

import static org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SERVICE_PROPERTIES;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("whiteboard/config")
public class ConfigurationAwareResource {

	@Context
	private Configuration configuration;

	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getValues(@PathParam("name") String name) {
		@SuppressWarnings("unchecked")
		Map<String,Object> serviceProps = (Map<String,Object>) configuration
				.getProperty(JAX_RS_APPLICATION_SERVICE_PROPERTIES);
		return String.valueOf(serviceProps.get(name));
	}
}
