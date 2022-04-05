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

package org.osgi.service.jakartars.whiteboard;

import java.nio.file.DirectoryStream.Filter;

import org.osgi.service.jakartars.runtime.JakartarsServiceRuntimeConstants;
import org.osgi.service.jakartars.runtime.dto.DTOConstants;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * Defines standard constants for the Jakarta RESTful Web Services Whiteboard
 * services.
 * 
 * @author $Id$
 */
public final class JakartarsWhiteboardConstants {
	private JakartarsWhiteboardConstants() {
		// non-instantiable
	}

	/**
	 * Service property specifying the name of a Jakarta RESTful Web Services
	 * whiteboard service.
	 * <p>
	 * This name is provided as a property on the registered Endpoint service so
	 * that the URI for a particular Jakarta RESTful Web Services service can be
	 * identified. If this service property is not specified, then no Endpoint
	 * information will be registered for this resource.
	 * <p>
	 * Resource names must be unique among all services associated with a single
	 * Whiteboard implementation. If a clashing name is registered then the
	 * lower ranked service will be failed with a cause of
	 * {@link DTOConstants#FAILURE_REASON_DUPLICATE_NAME}
	 * <p>
	 * The value of this service property must be of type {@code String}.
	 */
	public static final String	JAKARTA_RS_NAME								= "osgi.jakartars.name";

	/**
	 * Service property specifying that a Jakarta RESTful Web Services resource
	 * should be processed by the whiteboard.
	 * <p>
	 * The value of this service property must be of type {@code String} or
	 * {@link Boolean} and set to &quot;true&quot; or <code>true</code>.
	 */
	public static final String	JAKARTA_RS_RESOURCE							= "osgi.jakartars.resource";

	/**
	 * Service property specifying the base URI mapping for a Jakarta RESTful
	 * Web Services application service.
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
	public static final String	JAKARTA_RS_APPLICATION_BASE					= "osgi.jakartars.application.base";

	/**
	 * Service property specifying the target application for a Jakarta RESTful
	 * Web Services resource or extension service.
	 * <p>
	 * The specified filter(s) is/are used to determine whether a resource
	 * should be included in a particular application. Services without this
	 * service property are bound to the default Application.
	 * <p>
	 * If a filter property is registered and no application running in the
	 * whiteboard matches the filter then the service will be failed with a
	 * cause of
	 * {@link DTOConstants#FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE}
	 * <p>
	 * The value of this service property must be of type {@code String+}, and
	 * each entry must be a valid OSGi filter.
	 */
	public static final String	JAKARTA_RS_APPLICATION_SELECT				= "osgi.jakartars.application.select";

	/**
	 * The property key which can be used to find the application service
	 * properties inside an injected {@link Configuration}
	 */
	public static final String	JAKARTA_RS_APPLICATION_SERVICE_PROPERTIES	= "osgi.jakartars.application.serviceProperties";

	/**
	 * The name of the default Jakarta RESTful Web Services application in every
	 * Whiteboard instance.
	 */
	public static final String	JAKARTA_RS_DEFAULT_APPLICATION				= ".default";

	/**
	 * Service property specifying that a Jakarta RESTful Web Services resource
	 * service should be processed by the whiteboard.
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
	public static final String	JAKARTA_RS_EXTENSION						= "osgi.jakartars.extension";

	/**
	 * A Service property specifying one or more target filters used to select
	 * the set of Jakarta RESTful Web Services extension services required to
	 * support this whiteboard service.
	 * <p>
	 * A Jakarta RESTful Web Services Whiteboard service may require one or more
	 * extensions to be available so that it can function. For example a
	 * resource which declares that it <code>@Produces("text/json")</code>
	 * requires a {@link MessageBodyWriter} which supports JSON to be available.
	 * <p>
	 * This service property provides a String+ set of LDAP filters which will
	 * be applied to the service properties of all extensions available in the
	 * Jakarta RESTful Web Services container. If all of the filters are
	 * satisfied then this service is eligible to be hosted by the Jakarta
	 * RESTful Web Services container.
	 * <p>
	 * This service property may be declared by any Jakarta RESTful Web Services
	 * whiteboard service, whether it is a resource, or an extension.
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
	public static final String	JAKARTA_RS_EXTENSION_SELECT					= "osgi.jakartars.extension.select";

	/**
	 * A service property specifying that a Jakarta RESTful Web Services
	 * extension service, application service, or whiteboard implementation
	 * provides support for reading from and writing to a specific media type.
	 * <p>
	 * The value of this property will be one or more media type identifiers,
	 * and where possible IANA registered names, such as
	 * <code>application/json</code> should be used. The value must not be a
	 * wildcard type. Support for multiple media types that use the same suffix
	 * should be supported by registering the media type associated with the
	 * suffix.
	 */
	public static final String	JAKARTA_RS_MEDIA_TYPE						= "osgi.jakartars.media.type";

	/**
	 * Service property specifying the target filter to select the Jakarta
	 * RESTful Web Services Whiteboard implementation to process the service.
	 * <p>
	 * A Jakarta RESTful Web Services Whiteboard implementation can define any
	 * number of service properties which can be referenced by the target
	 * filter. The service properties should always include the
	 * {@link JakartarsServiceRuntimeConstants#JAKARTA_RS_SERVICE_ENDPOINT
	 * osgi.jakartars.endpoint} service property if the endpoint information is
	 * known.
	 * <p>
	 * If this service property is not specified, then all Jakarta RESTful Web
	 * Services Whiteboard implementations can process the service.
	 * <p>
	 * The value of this service property must be of type {@code String} and be
	 * a valid {@link Filter filter string}.
	 */
	public static final String	JAKARTA_RS_WHITEBOARD_TARGET				= "osgi.jakartars.whiteboard.target";

	/**
	 * The name of the implementation capability for the Jakarta RESTful Web
	 * Services Whiteboard specification
	 */
	public static final String	JAKARTA_RS_WHITEBOARD_IMPLEMENTATION		= "osgi.jakartars";

	/**
	 * The version of the implementation capability for the Jakarta RESTful Web
	 * Services Whiteboard specification
	 */
	public static final String	JAKARTA_RS_WHITEBOARD_SPECIFICATION_VERSION	= "2.0";

}
