/*
 * Copyright (c) OSGi Alliance (2019, 2020). All Rights Reserved.
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

package org.osgi.service.onem2m;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.osgi.util.promise.Promise;

/**
 * Primary Interface for an oneM2M application entity to send request and get
 * response to/from other oneM2M entity.
 * <p>
 * It contains low level API and high level API. The only low level method is
 * request() and other methods are categorized as high level API.
 */
@ProviderType
public interface ServiceLayer {
	/**
	 * send a request and receive response.
	 * <p>
	 * This method allows very raw data type access and it enables all possible
	 * message exchanges among oneM2M entities. This is called the low level
	 * API. This method allows all possible operation of oneM2M. For the return
	 * type, OSGi Promise is used for allowing synchronous and asynchronous
	 * calling manner.
	 *
	 * @param request request primitive
	 * @return promise of ResponseDTO.
	 */
	Promise<ResponsePrimitiveDTO> request(RequestPrimitiveDTO request);

	/**
	 * create resource
	 * <p>
	 * The create() method is a method to create new resource under specified
	 * uri. The second argument resource is expression of resource to be
	 * generated. The resourceType field of the resourceDTO must be assigned.
	 * For other fields depends on resource type. Section 7.4 of TS-00004
	 * specifies the optionalities of the fields.
	 * 
	 * @param uri URI for parent resource of the resource being created.
	 * @param resource resource data
	 * @return Promise of created resource
	 */
	public Promise<ResourceDTO> create(String uri, ResourceDTO resource);

	/**
	 * retrieve resource
	 * <p>
	 * retrieve resource on URI specified by uri argument. This method retrieve
	 * all attributes of the resource.
	 *
	 * @param uri URI for retrieving resource
	 * @return retrieved resource data
	 */
	public Promise<ResourceDTO> retrieve(String uri);

	/**
	 * retrieve resource with selected attributes.
	 * <p>
	 * retrieve resource on URI specified by uri argument. This method retrieve
	 * selected attributes by targetAttributes argument. The retrieve() methods
	 * are methods to retrieve resource on URI specified by uri argument.
	 *
	 * @param uri URI for retrieving resource
	 * @param targetAttributes names of the target attribute
	 * @return retrieved resource data
	 */
	public Promise<ResourceDTO> retrieve(String uri,
			List<String> targetAttributes);

	/**
	 * update resource
	 * <p>
	 * The update() method is a method to update resource on the URI specified
	 * by uri argument. The resource argument holds attributes to be updated.
	 * Attributes not to be updated shall not included in the argument.
	 *
	 * @param uri URI for updating resource
	 * @param resource data resource
	 * @return updated resource
	 */
	public Promise<ResourceDTO> update(String uri, ResourceDTO resource);

	/**
	 * delete resource
	 * <p>
	 * delete resource on the URI specified by uri argument.
	 *
	 * @param uri target URI for deleting resource
	 * @return promise of execution status
	 */
	public Promise<Boolean> delete(String uri);

	/**
	 * find resources with filter condition specified in fc argument.
	 * <p>
	 * Discovery Result Type is kept as blank and default value of target CSE is
	 * used for the parameter.
	 *
	 * @param uri URI for resource tree to start the search
	 * @param fc filter criteria selecting resources
	 * @return list of URIs matching the condition specified in fc
	 */
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc);

	/**
	 * find resources with filter condition specified in fc argument.
	 * <p>
	 * With this method application can specify desired identifier in result
	 * 
	 * @param uri URI for resource tree to start the search
	 * @param fc filter criteria
	 * @param drt Discovery Result Type (structured/unstructured)
	 * @return list of URIs matching the condition specified in fc
	 */
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc,
			RequestPrimitiveDTO.DesiredIdentifierResultType drt);

	/**
	 * send notification
	 * <p>
	 * 
	 * @param uri uri of destination
	 * @param notification content of notification
	 * @return Promise of notification execution status
	 */
	public Promise<Boolean> notify(String uri, NotificationDTO notification);

}
