package org.osgi.service.onem2m;
import java.util.List;

import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.osgi.util.promise.Promise;

/**
 * Primary Interface for an oneM2M entity to send request and get response to/from other
 * oneM2M entity.
 *
 */
public interface ServiceLayer {
	/**
	 * send a request.
	 *
	 * @param request request primitive
	 * @return promise of ResponseDTO.
	 */
	Promise<ResponsePrimitiveDTO> request(RequestPrimitiveDTO request);


	/**
	 * create resource
	 *
	 * @param uri URI for parent resource
	 * @param resource resource data
	 * @return Promise of created resource
	 */
	public Promise<ResourceDTO> create(String uri, ResourceDTO resource);

	/**
	 * retrieve resource
	 *
	 * @param uri URI for retrieving resource
	 * @return retrieved resource data
	 */
	public Promise<ResourceDTO> retrieve(String uri);

	/**
	 * retrieve subset of attributes.
	 *
	 * @param uri URI for retrieving resource
	 * @param targetAttributes names of the target attribute
	 * @return retrieved resource data
	 */
	public Promise<ResourceDTO> retrieve(String uri, List<String> targetAttributes);

	/**
	 * update resource
	 *
	 * @param uri URI for updating resource
	 * @param resource data resource
	 * @return updated resource
	 */
	public Promise<ResourceDTO> update(String uri, ResourceDTO resource);

	/**
	 * delete resource
	 *
	 * @param uri target URI for deleting resource
	 */
	public Promise<Boolean> delete(String uri);

	/**
	 * find resources.  Discovery Result Type is kept as blank and
	 * default value of target CSE is used for the parameter.
	 *
	 * @param uri URI for top of search
	 * @param fc filter criteria
	 * @return list of URIs matching the condition specified in fc
	 */
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc);

	/**
	 * find resources
	 *
	 * @param uri URI for top of search
	 * @param fc filter criteria
	 * @param drt Discovery Result Type (structured/unstructured)
	 * @return list of URIs matching the condition specified in fc
	 */
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc, RequestPrimitiveDTO.DiscoveryResultType drt);

	/**
	 * send notification
	 *
	 * @param notification
	 */
	public Promise<Boolean> notify(String uri, NotificationDTO notification );

}
