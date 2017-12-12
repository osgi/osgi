/*
 * Copyright (c) OSGi Alliance (2012, 2017). All Rights Reserved.
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

package org.osgi.service.jaxrs.whiteboard;

import java.nio.file.DirectoryStream.Filter;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.osgi.service.jaxrs.runtime.JaxRSServiceRuntimeConstants;
import org.osgi.service.jaxrs.runtime.dto.DTOConstants;

/**
 * Defines standard constants for the JAX-RS Whiteboard services.
 * 
 * @author $Id$
 */
public final class JaxRSWhiteboardConstants {
	private JaxRSWhiteboardConstants() {
		// non-instantiable
	}

	/**
	 * Service property specifying the name of a JAX-RS whiteboard service.
	 * <p>
	 * This name is provided as a property on the registered Endpoint service so
	 * that the URI for a particular JAX-RS service can be identified. If this
	 * service property is not specified, then no Endpoint information will be
	 * registered for this resource.
	 * <p>
	 * Resource names must be unique among all services associated with a single
	 * Whiteboard implementation. If a clashing name is registered then the
	 * lower ranked service will be failed with a cause of
	 * {@link DTOConstants#FAILURE_REASON_DUPLICATE_NAME}
	 * <p>
	 * The value of this service property must be of type {@code String}.
	 */
	public static final String	JAX_RS_NAME		= "osgi.jaxrs.name";

	/**
	 * Service property specifying that a JAX-RS resource should be processed by
	 * the whiteboard.
	 * <p>
	 * The value of this service property must be of type {@code String} or
	 * {@link Boolean} and set to &quot;true&quot; or <code>true</code>.
	 */
	public static final String	JAX_RS_RESOURCE				= "osgi.jaxrs.resource";

	/**
	 * Service property specifying the base URI mapping for a JAX-RS application
	 * service.
	 * <p>
	 * The specified uri is used to determine whether a request should be mapped
	 * to the resource. Services without this service property are ignored.
	 * <p>
	 * The value of this service property must be of type {@code String}, and
	 * will have a "/" prepended if no "/" exists.
	 * <p>
	 * If two applications are registered with the same base uri then the lower
	 * ranked service is failed with a cause of
	 * {@link DTOConstants#FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE}
	 */
	public static final String	JAX_RS_APPLICATION_BASE		= "osgi.jaxrs.application.base";

	/**
	 * Service property specifying the target application for a JAX-RS resource
	 * or extension service.
	 * <p>
	 * The specified filter is used to determine whether a resource should be
	 * included in a particular application. Services without this service
	 * property are bound to the default Application.
	 * <p>
	 * If a filter property is registered and no application running in the
	 * whiteboard matches the filter then the service will be failed with a
	 * cause of
	 * {@link DTOConstants#FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE}
	 * <p>
	 * The value of this service property must be of type {@code String}, and be
	 * a valid OSGi filter.
	 */
	public static final String	JAX_RS_APPLICATION_SELECT	= "osgi.jaxrs.application.select";

	/**
	 * The property key which can be used to find the application service
	 * properties inside an injected {@link Configuration}
	 */
	public static final String	JAX_RS_APPLICATION_SERVICE_PROPERTIES	= "osgi.jaxrs.application.serviceProperties";

	/**
	 * The name of the default JAX-RS application in every Whiteboard instance.
	 */
	public static final String	JAX_RS_DEFAULT_APPLICATION	= ".default";

	/**
	 * Service property specifying that a JAX-RS resource should be processed by
	 * the whiteboard.
	 * <p>
	 * The value of this service property must be of type {@code String} or
	 * {@link Boolean} and set to &quot;true&quot; or <code>true</code>.
	 * <p>
	 * A service providing this property must be registered as one or more of
	 * the following types:
	 * <ul>
	 * <li>{@link MessageBodyReader}</li>
	 * <li>{@link MessageBodyWriter}</li>
	 * <li>{@link ContainerRequestFilter}</li>
	 * <li>{@link ContainerResponseFilter}</li>
	 * <li>{@link ReaderInterceptor}</li>
	 * <li>{@link WriterInterceptor}</li>
	 * <li>{@link ContextResolver}</li>
	 * <li>{@link ExceptionMapper}</li>
	 * <li>{@link ParamConverterProvider}</li>
	 * <li>{@link Feature}</li>
	 * <li>{@link DynamicFeature}</li>
	 * </ul>
	 * <p>
	 * If a service with this property does not match any of the defined types
	 * then it is registered as a failure DTO with the error code
	 * {@link DTOConstants#FAILURE_REASON_NOT_AN_EXTENSION_TYPE},
	 */
	public static final String	JAX_RS_EXTENSION			= "osgi.jaxrs.extension";

	/**
	 * A Service property specifying one or more target filters used to select
	 * the set of JAX-RS extension services required to support this whiteboard
	 * service.
	 * <p>
	 * A JAX-RS Whiteboard service may require one or more extensions to be
	 * available so that it can function. For example a resource which declares
	 * that it <code>@Produces("text/json")</code> requires a
	 * {@link MessageBodyWriter} which supports JSON to be available.
	 * <p>
	 * This service property provides a String+ set of LDAP filters which will
	 * be applied to the service properties of all extensions available in the
	 * JAX-RS container. If all of the filters are satisfied then this service
	 * is eligible to be hosted by the JAX-RS container.
	 * <p>
	 * This service property may be declared by any JAX-RS whiteboard service,
	 * whether it is a resource, or an extension.
	 * <p>
	 * If this service property is not specified, then no extensions are
	 * required.
	 * <p>
	 * If one or more filter properties are registered and no suitable
	 * extension(s) are available then the service will be failed with a cause
	 * of {@link DTOConstants#FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE}
	 * <p>
	 * The value of this service property must be of type {@code String} and be
	 * a valid {@link Filter filter string}.
	 */
	public static final String	JAX_RS_EXTENSION_SELECT		= "osgi.jaxrs.extension.select";

	/**
	 * Service property specifying the target filter to select the JAX-RS
	 * Whiteboard implementation to process the service.
	 * <p>
	 * A JAX-RS Whiteboard implementation can define any number of service
	 * properties which can be referenced by the target filter. The service
	 * properties should always include the
	 * {@link JaxRSServiceRuntimeConstants#JAX_RS_SERVICE_ENDPOINT
	 * osgi.http.endpoint} service property if the endpoint information is
	 * known.
	 * <p>
	 * If this service property is not specified, then all JAX-RS Whiteboard
	 * implementations can process the service.
	 * <p>
	 * The value of this service property must be of type {@code String} and be
	 * a valid {@link Filter filter string}.
	 */
	public static final String	JAX_RS_WHITEBOARD_TARGET	= "osgi.jaxrs.whiteboard.target";

	/**
	 * The name of the implementation capability for the JAX-RS Whiteboard
	 * specification
	 */
	public static final String	JAX_RS_WHITEBOARD_IMPLEMENTATION		= "osgi.jaxrs";

	/**
	 * The version of the implementation capability for the JAX-RS Whiteboard
	 * specification
	 */
	public static final String	JAX_RS_WHITEBOARD_SPECIFICATION_VERSION	= "1.0.0";

}
