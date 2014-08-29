/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.impl.service.rest.client;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;
import org.osgi.service.rest.client.RestClient;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * Implementation of the (Java) REST client
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class RestClientImpl implements RestClient {

	private static final MediaType	FRAMEWORK_STARTLEVEL_JSON		= new MediaType(
																			"application/org.osgi.framework.startlevel+json");

	private static final MediaType	BUNDLE_JSON						= new MediaType(
																			"application/org.osgi.bundle+json");

	private static final MediaType	BUNDLES_JSON					= new MediaType(
																			"application/org.osgi.bundles+json");

	private static final MediaType	BUNDLES_REPRESENTATIONS_JSON	= new MediaType(
																			"application/org.osgi.bundles.representations+json");

	private static final MediaType	BUNDLE_STATE_JSON				= new MediaType(
																			"application/org.osgi.bundle.state+json");

	private static final MediaType	BUNDLE_HEADER_JSON				= new MediaType(
																			"application/org.osgi.bundle.header+json");

	private static final MediaType	BUNDLE_STARTLEVEL_JSON			= new MediaType(
																			"application/org.osgi.bundle.startlevel+json");

	private static final MediaType	SERVICE_JSON					= new MediaType(
																			"application/org.osgi.service+json");

	private static final MediaType	SERVICES_JSON					= new MediaType(
																			"application/org.osgi.services+json");

	private static final MediaType	SERVICES_REPRESENTATIONS_JSON	= new MediaType(
																			"application/org.osgi.services.representations+json");

	private final URI				baseUri;

	public RestClientImpl(final URI uri) {
		this.baseUri = uri.normalize().resolve("/");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getFrameworkStartLevel()
	 */
	public FrameworkStartLevelDTO getFrameworkStartLevel() throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve("framework/startlevel"))
				.get(FRAMEWORK_STARTLEVEL_JSON);

		return DTOReflector.getDTO(FrameworkStartLevelDTO.class,
				new JsonRepresentation(repr).getJsonObject());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setFrameworkStartLevel(org.osgi.dto.framework
	 *      .startlevel.FrameworkStartLevelDTO)
	 */
	public void setFrameworkStartLevel(final FrameworkStartLevelDTO startLevel)
			throws Exception {
		new ClientResource(Method.PUT, "framework/startlevel").put(
				DTOReflector.getJson(FrameworkStartLevelDTO.class, startLevel),
				FRAMEWORK_STARTLEVEL_JSON);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundles()
	 */
	public Collection<String> getBundles() throws Exception {
		final ClientResource res = new ClientResource(Method.GET,
				baseUri.resolve("framework/bundles"));
		final Representation repr = res.get(BUNDLES_JSON);
		return jsonArrayToStrings(new JsonRepresentation(repr).getJsonArray());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleRepresentations()
	 */
	public Collection<BundleDTO> getBundleRepresentations() throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve("framework/bundles/representations"))
				.get(BUNDLES_REPRESENTATIONS_JSON);

		return DTOReflector.getDTOs(BundleDTO.class, new JsonRepresentation(
				repr).getJsonArray());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundle(long)
	 */
	public BundleDTO getBundle(final long id) throws Exception {
		return getBundle("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundle(java.lang.String)
	 */
	public BundleDTO getBundle(final String bundlePath) throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath)).get(BUNDLE_JSON);

		return DTOReflector.getDTO(BundleDTO.class,
				new JsonRepresentation(repr).getJsonObject());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleState(long)
	 */
	public int getBundleState(final long id) throws Exception {
		return getBundleState("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleState(java.lang.String)
	 */
	public int getBundleState(final String bundlePath) throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/state")).get(BUNDLE_STATE_JSON);

		final JSONObject obj = new JsonRepresentation(repr).getJsonObject();
		return obj.getInt("state");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(long)
	 */
	public void startBundle(final long id) throws Exception {
		startBundle("framework/bundle/" + id, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(long)
	 */
	public void startBundle(final long id, final int options) throws Exception {
		startBundle("framework/bundle/" + id, options);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(java.lang.String)
	 */
	public void startBundle(final String bundlePath) throws Exception {
		startBundle(bundlePath, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(java.lang.String)
	 */
	public void startBundle(final String bundlePath, final int options)
			throws Exception {
		final JSONObject state = new JSONObject();
		state.put("state", 32);
		state.put("options", options);
		new ClientResource(Method.POST, baseUri.resolve(bundlePath + "/state"))
				.post(state, BUNDLE_STATE_JSON);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(long)
	 */
	public void stopBundle(final long id) throws Exception {
		stopBundle("framework/bundle/" + id, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(long)
	 */
	public void stopBundle(final long id, final int options) throws Exception {
		stopBundle("framework/bundle/" + id, options);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(java.lang.String)
	 */
	public void stopBundle(final String bundlePath) throws Exception {
		stopBundle(bundlePath, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(java.lang.String)
	 */
	public void stopBundle(final String bundlePath, final int options)
			throws Exception {
		final JSONObject state = new JSONObject();
		state.put("state", 4);
		state.put("options", options);
		new ClientResource(Method.POST, baseUri.resolve(bundlePath + "/state"))
				.post(state, BUNDLE_STATE_JSON);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleHeaders(long)
	 */
	public Map<String, Object> getBundleHeaders(final long id) throws Exception {
		return getBundleHeaders("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleHeaders(java.lang.String)
	 */
	public Map<String, Object> getBundleHeaders(final String bundlePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/headers"))
				.get(BUNDLE_HEADER_JSON);

		return DTOReflector.getMapfromJsonObject(new JsonRepresentation(repr)
				.getJsonObject());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleStartLevel(long)
	 */
	public BundleStartLevelDTO getBundleStartLevel(final long id)
			throws Exception {
		return getBundleStartLevel("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleStartLevel(java.lang.String)
	 */
	public BundleStartLevelDTO getBundleStartLevel(final String bundlePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/startlevel"))
				.get(BUNDLE_STARTLEVEL_JSON);

		return DTOReflector.getDTO(BundleStartLevelDTO.class,
				new JsonRepresentation(repr).getJsonObject());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setBundleStartLevel(long,
	 *      org.osgi.dto.framework.startlevel.BundleStartLevelDTO)
	 */
	public void setBundleStartLevel(final long id,
			final BundleStartLevelDTO startLevel) throws Exception {
		setBundleStartLevel("framework/bundle/" + id, startLevel);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setBundleStartLevel(java.lang.String,
	 *      org.osgi.dto.framework.startlevel.BundleStartLevelDTO)
	 */
	public void setBundleStartLevel(final String bundlePath,
			final BundleStartLevelDTO startLevel) throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve(bundlePath
				+ "/startlevel")).put(
				DTOReflector.getJson(BundleStartLevelDTO.class, startLevel),
				BUNDLE_STARTLEVEL_JSON);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#installBundle(java.net.URL)
	 */
	public String installBundle(final String url) throws Exception {
		final ClientResource res = new ClientResource(Method.POST,
				baseUri.resolve("framework/bundles"));
		final Representation repr = res.post(url);
		return repr.getText();
	}

	/**
	 * @see org.osgi.rest.client.RestClient#installBundle(java.io.InputStream)
	 */
	public String installBundle(final String location, final InputStream in)
			throws Exception {
		final ClientResource res = new ClientResource(Method.POST,
				baseUri.resolve("framework/bundles"));
		res.getRequest().getAttributes()
				.put("message.entity.locationRef", new Reference(location));

		final Representation repr = res.post(in);

		return repr.getText();
	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long)
	 */
	public void updateBundle(final long id) throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundles/"
				+ id)).put("");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long, java.net.URL)
	 */
	public void updateBundle(final long id, final String url) throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundles/"
				+ id)).put(url);

	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long,
	 *      java.io.InputStream)
	 */
	public void updateBundle(final long id, final InputStream in)
			throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundles/"
				+ id)).put(in);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#uninstallBundle(long)
	 */
	public void uninstallBundle(final long id) throws Exception {
		uninstallBundle("framework/bundles/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#uninstallBundle(java.lang.String)
	 */
	public void uninstallBundle(final String bundlePath) throws Exception {
		final ClientResource res = new ClientResource(Method.PUT,
				baseUri.resolve(bundlePath));
		res.delete();
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServices()
	 */
	public Collection<String> getServices() throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve("framework/services")).get(SERVICES_JSON);

		return jsonArrayToStrings(new JsonRepresentation(repr).getJsonArray());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServices(java.lang.String)
	 */
	public Collection<String> getServices(final String filter) throws Exception {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceRepresentations()
	 */
	public Collection<ServiceReferenceDTO> getServiceRepresentations()
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve("framework/services/representations"))
				.get(SERVICES_REPRESENTATIONS_JSON);

		return DTOReflector.getDTOs(ServiceReferenceDTO.class,
				new JsonRepresentation(repr).getJsonArray());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceRepresentations(java.lang.String
	 *      )
	 */
	public Collection<ServiceReferenceDTO> getServiceRepresentations(
			final String filter) throws Exception {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceReference(long)
	 */
	public ServiceReferenceDTO getServiceReference(final long id)
			throws Exception {
		return getServiceReference("framework/services/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceReference(java.lang.String)
	 */
	public ServiceReferenceDTO getServiceReference(final String servicePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(servicePath)).get(SERVICE_JSON);

		return DTOReflector.getDTO(ServiceReferenceDTO.class,
				new JsonRepresentation(repr).getJsonObject());
	}

	private Collection<String> jsonArrayToStrings(final JSONArray array)
			throws JSONException {
		final Collection<String> result = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			result.add(array.getString(i));
		}
		return result;
	}

}
