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

package org.osgi.impl.service.rest.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.RestService;
import org.osgi.impl.service.rest.pojos.BundleExceptionPojo;
import org.osgi.impl.service.rest.pojos.ServicePojo;
import org.osgi.resource.Capability;
import org.osgi.service.rest.RestApiExtension;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;

/**
 * Abstract OSGi resource with the functionality to translate OSGi entities to
 * representations according to requested media types (variants).
 * 
 * @author Jan S. Rellermeyer, IBM Research
 *
 * @param <T> the pojo base class which is reflected by the pojo reflector.
 */
public class AbstractOSGiResource<T> extends ServerResource {

	private static final String				number		= "([0-9]*)";

	private static final String				MT_JSON		= "+json";

	private static final String				MT_XML		= "+xml";

	private static final String				MT_BE		= "application/org.osgi.bundleexception";

	private static final MediaType			MT_BE_JSON	= new MediaType(MT_BE + MT_JSON);

	private static final MediaType			MT_BE_XML	= new MediaType(MT_BE + MT_XML);

	protected final static Representation	SUCCESS		= null;

	private final static Representation		ERROR		= null;

	private final PojoReflector<T>			reflector;

	private final MediaType					xmlMediaType;

	private final MediaType					jsonMediaType;

	protected AbstractOSGiResource(final PojoReflector<T> reflector, final MediaType mediaType) {
		this.reflector = reflector;
		this.xmlMediaType = new MediaType(mediaType.toString() + MT_XML);
		this.jsonMediaType = new MediaType(mediaType.toString() + MT_JSON);

		this.setNegotiated(true);

		getVariants().add(new Variant(jsonMediaType));
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));

		getVariants().add(new Variant(xmlMediaType));
		getVariants().add(new Variant(MediaType.APPLICATION_XML));
		getVariants().add(new Variant(MediaType.TEXT_XML));

		getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}

	protected BundleContext getBundleContext() {
		return (BundleContext) getContext().getAttributes().get(
				RestService.BUNDLE_CONTEXT_ATTR);
	}

	@SuppressWarnings("unchecked")
	protected ServiceTracker<RestApiExtension, Class<? extends ServerResource>> getTracker() {
		return (ServiceTracker<RestApiExtension, Class<? extends ServerResource>>) getContext()
				.getAttributes().get(RestService.TRACKER_ATTR);
	}

	protected FrameworkStartLevel getFrameworkStartLevel() {
		return getBundleContext().getBundle(0).adapt(FrameworkStartLevel.class);
	}

	protected Bundle getBundleFromKeys(final String bundleIdKey) {
		final String id = (String) getRequest().getAttributes()
				.get(bundleIdKey);

		if (id != null) {
			return getBundleContext().getBundle(Long.parseLong(id));
		}
		return null;
	}

	protected Bundle[] getBundles()
			throws InvalidSyntaxException {
		final BundleContext context = getBundleContext();

		if (getQuery().isEmpty()) {
			return context.getBundles();
		}
		final Map<String, String> filterMap = getQuery().getValuesMap();

		final Bundle[] bundles = context.getBundles();

		final ArrayList<BundleRevision> workingSet = new ArrayList<BundleRevision>();
		for (Bundle bundle : bundles) {
			workingSet.add(bundle.adapt(BundleRevision.class));
		}

		for (final Map.Entry<String, String> filterDir : filterMap.entrySet()) {
			final String namespace;
			final Filter filter;
			if (filterDir.getValue() == null) {
				namespace = IdentityNamespace.IDENTITY_NAMESPACE;
				filter = FrameworkUtil.createFilter(filterDir.getKey());
			} else {
				namespace = filterDir.getKey();
				filter = FrameworkUtil.createFilter(filterDir.getValue());
			}

			final Iterator<BundleRevision> iter = workingSet.iterator();
			bundleLoop: while (iter.hasNext()) {
				final BundleRevision rev = iter.next();
				final List<Capability> caps = rev
						.getCapabilities(namespace);

				for (final Capability cap : caps) {
					if (filter.matches(cap.getAttributes())) {
						continue bundleLoop;
					}
				}

				// no match, remove
				iter.remove();
			}
		}

		if (workingSet.isEmpty()) {
			return new Bundle[0];
		}

		final Bundle[] result = new Bundle[workingSet.size()];
		int i = -1;
		for (final BundleRevision rev : workingSet) {
			result[++i] = rev.getBundle();
		}

		return result;
	}

	protected Bundle[] getBundleVersionsBySymbolicName(String key) {
		final String symbolicName = (String) getRequest().getAttributes().get(
				key);
		if (symbolicName == null) {
			return null;
		}
		final ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		for (final Bundle bundle : getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().equals(symbolicName)) {
				bundles.add(bundle);
			}
		}
		return bundles.toArray(new Bundle[bundles.size()]);
	}

	protected ServiceReference<?> getServiceReferenceFromKey(final String key) {
		final String id = (String) getRequest().getAttributes().get(key);
		if (id == null) {
			return null;
		}

		if (!id.matches(number)) {
			throw new IllegalArgumentException("Invalid service id " + id);
		}

		try {
			final ServiceReference<?>[] srefs = getBundleContext()
					.getServiceReferences((String) null,
							"(" + Constants.SERVICE_ID + "=" + id + ")");
			return srefs != null ? srefs[0] : null;
		} catch (final InvalidSyntaxException e) {
			// does not happen
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected Representation getRepresentation(final Object bean,
			final Variant variant) throws Exception {
		final Representation rep;

		final MediaType mt;

		if (xmlMediaType.includes(variant.getMediaType()) ||
				MediaType.APPLICATION_XML.includes(variant.getMediaType()) ||
				MediaType.TEXT_XML.includes(variant.getMediaType())) {
			mt = xmlMediaType;

			if (bean instanceof Map) {
				// special case: bundle header is a plain map and has no
				// reflector
				rep = toRepresentation(PojoReflector.mapToXml((Map<String, String>) bean), new Variant(MediaType.APPLICATION_ALL_XML));
			} else {
				rep = toRepresentation(reflector.xmlFromBean((T) bean), new Variant(MediaType.APPLICATION_ALL_XML));
			}
		} else if (jsonMediaType.includes(variant.getMediaType())
				|| MediaType.APPLICATION_JSON.includes(variant.getMediaType())
				|| MediaType.TEXT_PLAIN.includes(variant.getMediaType())) {
			mt = jsonMediaType;

			// in an ideal world we would not have to massage the data
			// in order to get it to serialize properly...
			if (bean instanceof Collection) {
				final Collection<Object> reprList = new ArrayList<Object>();
				for (final Object o : (Collection<?>) bean) {
					if (o instanceof String) {
						reprList.add(o);
					} else {
						reprList.add(jsonObject(o));
					}
				}
				final JSONArray arr = new JSONArray(reprList);
				rep = toRepresentation(arr, variant);
			} else if (bean instanceof Map) {
				rep = toRepresentation(new JSONObject((Map<?, ?>) bean),
						variant);
			} else {
				rep = toRepresentation(jsonObject(bean), variant);
			}
		} else {
			throw new UnsupportedOperationException(variant.getMediaType()
					.toString());
		}
		rep.setMediaType(mt);
		return rep;
	}

	private JSONObject jsonObject(final Object bean) {
		if (bean instanceof ServicePojo) {
			// fix for buggy JSONObject
			final JSONObject json = new JSONObject(bean);
			try {
				json.put("properties", new JSONObject(((ServicePojo) bean).getProperties()));
				json.put("usingBundles", new JSONArray(((ServicePojo) bean).getUsingBundles()));
			} catch (final JSONException e) {
				e.printStackTrace();
			}
			return json;
		} else {
			return new JSONObject(bean);
		}
	}

	protected T fromRepresentation(final Representation r, final MediaType mediaType)
			throws Exception {
		if (xmlMediaType.includes(mediaType) ||
				MediaType.APPLICATION_XML.includes(mediaType) ||
				MediaType.TEXT_XML.includes(mediaType)) {
			return reflector.beanFromXml(toObject(r, Document.class));
		} else if (jsonMediaType.includes(mediaType)
				|| MediaType.APPLICATION_JSON.includes(mediaType) || MediaType.TEXT_PLAIN.includes(mediaType)) {
			return reflector.beanFromJSONObject(toObject(r, JSONObject.class));
		}
		throw new UnsupportedOperationException(mediaType
				.toString());
	}

	protected Map<String, String> mapFromDict(
			final Dictionary<String, String> headers) {
		final HashMap<String, String> map = new HashMap<String, String>(
				headers.size());
		final Enumeration<String> keyE = headers.keys();
		while (keyE.hasMoreElements()) {
			final String key = keyE.nextElement();
			map.put(key, headers.get(key));
		}
		return map;
	}

	protected Representation ERROR(final Throwable t,
			final Variant variant) {
		t.printStackTrace();
		if (t instanceof BundleException) {
			try {
				final Representation rep;
				final MediaType mt;

				if (MediaType.APPLICATION_ALL_XML.includes(variant.getMediaType())) {
					mt = MT_BE_XML;

					rep = getRepresentation(new BundleExceptionPojo(
							(BundleException) t), new Variant(MediaType.TEXT_XML));
				} else {
					mt = MT_BE_JSON;
					rep = getRepresentation(new BundleExceptionPojo(
							(BundleException) t), new Variant(MediaType.APPLICATION_JSON));
				}

				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				rep.setMediaType(mt);

				return rep;
			} catch (final Exception ioe) {
				// fallback
			}
		} else if (t instanceof IllegalArgumentException) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ERROR;
		} else if (t instanceof InvalidSyntaxException) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ERROR;
		}

		setStatus(Status.SERVER_ERROR_INTERNAL, t);
		return ERROR;
	}

	protected Representation SUCCESS(final Status status) {
		setStatus(status);
		return SUCCESS;
	}

	protected Representation ERROR(final Status status) {
		setStatus(status);
		return ERROR;
	}

	protected Representation ERROR(final Status status, final Exception e) {
		setStatus(status, e);
		return ERROR;
	}

	protected Representation ERROR(final Status status, final String s) {
		setStatus(status, s);
		return ERROR;
	}

}
