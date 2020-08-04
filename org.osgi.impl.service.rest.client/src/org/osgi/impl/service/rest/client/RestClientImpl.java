/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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
import java.util.Collection;
import java.util.Map;
import org.json.JSONObject;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;
import org.osgi.service.rest.client.RestClient;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

/**
 * Implementation of the (Java) REST client
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class RestClientImpl implements RestClient {

	private static final String	MT_FRAMEWORK_STARTLEVEL		= "application/org.osgi.framework.startlevel";

	private static final String	MT_BUNDLE					= "application/org.osgi.bundle";

	private static final String	MT_BUNDLES					= "application/org.osgi.bundles";

	private static final String	MT_BUNDLES_REPRESENTATIONS	= "application/org.osgi.bundles.representations";

	private static final String	MT_BUNDLE_STATE				= "application/org.osgi.bundle.state";

	private static final String	MT_BUNDLE_HEADER			= "application/org.osgi.bundle.header";

	private static final String	MT_BUNDLE_STARTLEVEL		= "application/org.osgi.bundle.startlevel";

	private static final String	MT_SERVICE					= "application/org.osgi.service";

	private static final String	MT_SERVICES					= "application/org.osgi.services";

	private static final String	MT_SERVICES_REPRESENTATIONS	= "application/org.osgi.services.representations";

	private static final String	MT_JSON_EXT					= "+json";

	private static final String	MT_XML_EXT					= "+xml";

	private final MediaType		FRAMEWORK_STARTLEVEL;

	private final MediaType		BUNDLE;

	private final MediaType		BUNDLES;

	private final MediaType		BUNDLES_REPRESENTATIONS;

	private final MediaType		BUNDLE_STATE;

	private final MediaType		BUNDLE_HEADER;

	private final MediaType		BUNDLE_STARTLEVEL;

	private final MediaType		SERVICE;

	private final MediaType		SERVICES;

	private final MediaType		SERVICES_REPRESENTATIONS;

	private final URI			baseUri;

	protected RestClientImpl(final URI uri, final boolean useXml) {
		this.baseUri = uri.normalize().resolve("/");
		final String ext = useXml ? MT_XML_EXT : MT_JSON_EXT;
		FRAMEWORK_STARTLEVEL = new MediaType(MT_FRAMEWORK_STARTLEVEL + ext);
		BUNDLE = new MediaType(MT_BUNDLE + ext);
		BUNDLES = new MediaType(MT_BUNDLES + ext);
		BUNDLES_REPRESENTATIONS = new MediaType(MT_BUNDLES_REPRESENTATIONS + ext);
		BUNDLE_STATE = new MediaType(MT_BUNDLE_STATE + ext);
		BUNDLE_HEADER = new MediaType(MT_BUNDLE_HEADER + ext);
		BUNDLE_STARTLEVEL = new MediaType(MT_BUNDLE_STARTLEVEL + ext);
		SERVICE = new MediaType(MT_SERVICE + ext);
		SERVICES = new MediaType(MT_SERVICES + ext);
		SERVICES_REPRESENTATIONS = new MediaType(MT_SERVICES_REPRESENTATIONS + ext);

	}

	/**
	 * @see org.osgi.rest.client.RestClient#getFrameworkStartLevel()
	 */
	@Override
	public FrameworkStartLevelDTO getFrameworkStartLevel() throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve("framework/startlevel"))
				.get(FRAMEWORK_STARTLEVEL);

		return DTOReflector.getDTO(FrameworkStartLevelDTO.class, repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setFrameworkStartLevel(org.osgi.dto.framework
	 *      .startlevel.FrameworkStartLevelDTO)
	 */
	@Override
	public void setFrameworkStartLevel(final FrameworkStartLevelDTO startLevel)
			throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/startlevel")).put(
				DTOReflector.getJson(FrameworkStartLevelDTO.class, startLevel),
				FRAMEWORK_STARTLEVEL);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundles()
	 */
	@Override
	public Collection<String> getBundlePaths() throws Exception {
		final ClientResource res = new ClientResource(Method.GET,
				baseUri.resolve("framework/bundles"));
		final Representation repr = res.get(BUNDLES);

		System.err.println("HAVING MEDIA TYPE " + repr.getMediaType());

		return DTOReflector.getStrings(repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleRepresentations()
	 */
	@Override
	public Collection<BundleDTO> getBundles() throws Exception {
		try {
			final Representation repr = new ClientResource(Method.GET,
					baseUri.resolve("framework/bundles/representations"))
					.get(BUNDLES_REPRESENTATIONS);

			return DTOReflector.getDTOs(BundleDTO.class, repr);
		} catch (final ResourceException e) {
			if (Status.CLIENT_ERROR_NOT_FOUND.equals(e.getStatus())) {
				return null;
			}
			throw e;
		}
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundle(long)
	 */
	@Override
	public BundleDTO getBundle(final long id) throws Exception {
		return getBundle("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundle(java.lang.String)
	 */
	@Override
	public BundleDTO getBundle(final String bundlePath) throws Exception {
		try {
			final Representation repr = new ClientResource(Method.GET,
					baseUri.resolve(bundlePath)).get(BUNDLE);
			return DTOReflector.getDTO(BundleDTO.class, repr);
		} catch (final ResourceException e) {
			if (Status.CLIENT_ERROR_NOT_FOUND.equals(e.getStatus())) {
				return null;
			}
			throw e;
		}
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleState(long)
	 */
	@Override
	public int getBundleState(final long id) throws Exception {
		return getBundleState("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleState(java.lang.String)
	 */
	@Override
	public int getBundleState(final String bundlePath) throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/state")).get(BUNDLE_STATE);

		// FIXME: hardcoded to JSON
		final JSONObject obj = new JsonRepresentation(repr).getJsonObject();
		return obj.getInt("state");
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(long)
	 */
	@Override
	public void startBundle(final long id) throws Exception {
		startBundle("framework/bundle/" + id, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(long)
	 */
	@Override
	public void startBundle(final long id, final int options) throws Exception {
		startBundle("framework/bundle/" + id, options);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(java.lang.String)
	 */
	@Override
	public void startBundle(final String bundlePath) throws Exception {
		startBundle(bundlePath, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#startBundle(java.lang.String)
	 */
	@Override
	public void startBundle(final String bundlePath, final int options)
			throws Exception {
		// FIXME: hardcoded to JSON
		final JSONObject state = new JSONObject();
		state.put("state", 32);
		state.put("options", options);
		new ClientResource(Method.PUT, baseUri.resolve(bundlePath + "/state"))
				.put(state, BUNDLE_STATE);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(long)
	 */
	@Override
	public void stopBundle(final long id) throws Exception {
		stopBundle("framework/bundle/" + id, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(long)
	 */
	@Override
	public void stopBundle(final long id, final int options) throws Exception {
		stopBundle("framework/bundle/" + id, options);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(java.lang.String)
	 */
	@Override
	public void stopBundle(final String bundlePath) throws Exception {
		stopBundle(bundlePath, 0);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#stopBundle(java.lang.String)
	 */
	@Override
	public void stopBundle(final String bundlePath, final int options)
			throws Exception {
		final JSONObject state = new JSONObject();
		state.put("state", 4);
		state.put("options", options);
		new ClientResource(Method.PUT, baseUri.resolve(bundlePath + "/state"))
				.put(state, BUNDLE_STATE);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleHeaders(long)
	 */
	@Override
	public Map<String, String> getBundleHeaders(final long id) throws Exception {
		return getBundleHeaders("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleHeaders(java.lang.String)
	 */
	@Override
	public Map<String, String> getBundleHeaders(final String bundlePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/header"))
				.get(BUNDLE_HEADER);

		return DTOReflector.getMap(repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleStartLevel(long)
	 */
	@Override
	public BundleStartLevelDTO getBundleStartLevel(final long id)
			throws Exception {
		return getBundleStartLevel("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getBundleStartLevel(java.lang.String)
	 */
	@Override
	public BundleStartLevelDTO getBundleStartLevel(final String bundlePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(bundlePath + "/startlevel"))
				.get(BUNDLE_STARTLEVEL);

		return DTOReflector.getDTO(BundleStartLevelDTO.class, repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setBundleStartLevel(long,
	 *      org.osgi.dto.framework.startlevel.BundleStartLevelDTO)
	 */
	@Override
	public void setBundleStartLevel(final long id,
			final int startLevel) throws Exception {
		setBundleStartLevel("framework/bundle/" + id, startLevel);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#setBundleStartLevel(java.lang.String,
	 *      org.osgi.dto.framework.startlevel.BundleStartLevelDTO)
	 */
	@Override
	public void setBundleStartLevel(final String bundlePath,
			final int startLevel) throws Exception {
		BundleStartLevelDTO bsl = new BundleStartLevelDTO();
		bsl.startLevel = startLevel;
		new ClientResource(Method.PUT, baseUri.resolve(bundlePath
				+ "/startlevel")).put(
				DTOReflector.getJson(BundleStartLevelDTO.class, bsl),
				BUNDLE_STARTLEVEL);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#installBundle(java.net.URL)
	 */
	@Override
	public BundleDTO installBundle(final String url) throws Exception {
		final ClientResource res = new ClientResource(Method.POST,
				baseUri.resolve("framework/bundles"));
		final Representation repr = res.post(url, MediaType.TEXT_PLAIN);

		return getBundle(repr.getText());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#installBundle(java.io.InputStream)
	 */
	@Override
	public BundleDTO installBundle(final String location, final InputStream in)
			throws Exception {
		final ClientResource res = new ClientResource(Method.POST,
				baseUri.resolve("framework/bundles"));
		@SuppressWarnings("unchecked")
		Series<Header> headers = (Series<Header>) res.getRequestAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Series<Header>(Header.class);
			res.getRequestAttributes().put("org.restlet.http.headers", headers);
		}
		headers.add("Content-Location", location);

		/*
		 * does not work in the current RESTLET version:
		 * res.getRequest().getAttributes() .put("message.entity.locationRef",
		 * new Reference(location));
		 */
		final Representation repr = res.post(in);

		return getBundle(repr.getText());
	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long)
	 */
	@Override
	public BundleDTO updateBundle(final long id) throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundle/"
				+ id)).put("", MediaType.TEXT_PLAIN);
		return null; // TODO return a BundleDTO
	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long, java.net.URL)
	 */
	@Override
	public BundleDTO updateBundle(final long id, final String url) throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundle/"
				+ id)).put(url, MediaType.TEXT_PLAIN);
		return null; // TODO return a BundleDTO
	}

	/**
	 * @see org.osgi.rest.client.RestClient#updateBundle(long,
	 *      java.io.InputStream)
	 */
	@Override
	public BundleDTO updateBundle(final long id, final InputStream in)
			throws Exception {
		new ClientResource(Method.PUT, baseUri.resolve("framework/bundle/"
				+ id)).put(in);
		return null; // TODO return a BundleDTO
	}

	/**
	 * @see org.osgi.rest.client.RestClient#uninstallBundle(long)
	 */
	@Override
	public BundleDTO uninstallBundle(final long id) throws Exception {
		return uninstallBundle("framework/bundle/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#uninstallBundle(java.lang.String)
	 */
	@Override
	public BundleDTO uninstallBundle(final String bundlePath) throws Exception {
		final ClientResource res = new ClientResource(Method.DELETE,
				baseUri.resolve(bundlePath));
		res.delete();
		return null; // TODO return a BundleDTO
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServices()
	 */
	@Override
	public Collection<String> getServicePaths() throws Exception {
		return getServicePaths(null);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServices(java.lang.String)
	 */
	@Override
	public Collection<String> getServicePaths(final String filter) throws Exception {
		final ClientResource res = new ClientResource(Method.GET,
				baseUri.resolve("framework/services"));

		if (filter != null) {
			res.addQueryParameter("filter", filter);
		}

		final Representation repr = res.get(SERVICES);

		return DTOReflector.getStrings(repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceRepresentations()
	 */
	@Override
	public Collection<ServiceReferenceDTO> getServiceReferences()
			throws Exception {
		return getServiceReferences(null);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceRepresentations(java.lang.String
	 *      )
	 */
	@Override
	public Collection<ServiceReferenceDTO> getServiceReferences(
			final String filter) throws Exception {
		final ClientResource res = new ClientResource(Method.GET,
				baseUri.resolve("framework/services/representations"));
		if (filter != null) {
			res.addQueryParameter("filter", filter);
		}
		final Representation repr = res.get(SERVICES_REPRESENTATIONS);

		return DTOReflector.getDTOs(ServiceReferenceDTO.class, repr);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceReference(long)
	 */
	@Override
	public ServiceReferenceDTO getServiceReference(final long id)
			throws Exception {
		return getServiceReference("framework/service/" + id);
	}

	/**
	 * @see org.osgi.rest.client.RestClient#getServiceReference(java.lang.String)
	 */
	@Override
	public ServiceReferenceDTO getServiceReference(final String servicePath)
			throws Exception {
		final Representation repr = new ClientResource(Method.GET,
				baseUri.resolve(servicePath)).get(SERVICE);

		return DTOReflector.getDTO(ServiceReferenceDTO.class,
				repr);
	}


}
