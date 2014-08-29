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

package org.osgi.impl.service.rest.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.RestService;
import org.osgi.impl.service.rest.pojos.BundleExceptionPojo;
import org.osgi.resource.Capability;
import org.osgi.service.rest.RestApiExtension;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ServerResource;

/**
 * Abstract OSGi resource with the functionality to translate OSGi entities to
 * representations according to requested media types (variants).
 * 
 * @author Jan S. Rellermeyer, IBM Research
 *
 * @param <T> the pojo base class which is reflected by the pojo reflector.
 */
public class AbstractOSGiResource<T> extends ServerResource {

	private static final String				number	= "([0-9]*)";

	protected final static Representation	SUCCESS	= null;

	protected final static Representation	ERROR	= null;

	private final PojoReflector<T>			reflector;

	protected AbstractOSGiResource(final PojoReflector<T> reflector) {
		this.reflector = reflector;
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

	protected Bundle getBundleFromKeys(final String bundleIdKey,
			final String bundleSymbolicNameKey, final String bundleVersionKey) {
		final String id = (String) getRequest().getAttributes()
				.get(bundleIdKey);
		if (id != null) {
			return getBundleContext().getBundle(Long.parseLong(id));
		}
		final String symbolicName = (String) getRequest().getAttributes().get(
				bundleSymbolicNameKey);
		final Version version = new Version((String) getRequest()
				.getAttributes().get(bundleVersionKey));
		for (final Bundle bundle : getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().equals(symbolicName)
					&& bundle.getVersion().equals(version)) {
				return bundle;
			}
		}
		return null;
	}

	protected Bundle[] getBundlesFromFilter(final String filterKey)
			throws InvalidSyntaxException {
		final BundleContext context = getBundleContext();

		final String filterStr = (String) getRequest().getAttributes().get(
				filterKey);
		if (filterStr == null) {
			return context.getBundles();
		}

		final Filter filter = FrameworkUtil.createFilter(filterStr);

		final Bundle[] bundles = context.getBundles();
		final ArrayList<Bundle> result = new ArrayList<Bundle>();
		for (final Bundle bundle : bundles) {
			final BundleRevision rev = bundle.adapt(BundleRevision.class);
			final List<Capability> caps = rev
					.getCapabilities("osgi.wiring.bundle");
			System.out.println(caps);
			for (final Capability cap : caps) {
				System.out.println("\t" + filter.matches(cap.getAttributes())
						+ "\t" + cap);
				System.out.println("\t\t" + cap.getAttributes());
				System.out.println("\t\t" + cap.getDirectives());
				if (filter.matches(cap.getAttributes())
						|| filter.matches(cap.getDirectives())) {
					result.add(bundle);
					break;
				}
			}
		}
		return result.toArray(new Bundle[result.size()]);
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

	protected Representation getRepresentation(final T bean,
			final Variant variant) throws IOException {
		final Representation rep;
		System.err.println("VARIANT MEDIA TYPE " + variant.getMediaType());

		if (MediaType.APPLICATION_ALL_XML.includes(variant.getMediaType())) {
			throw new UnsupportedOperationException("TODO: "
					+ variant.getMediaType().toString());
		} else if (MediaType.APPLICATION_JSON.includes(variant.getMediaType())
				|| MediaType.TEXT_ALL.includes(variant.getMediaType())) {
			if (bean instanceof Collection) {
				final JSONArray arr = new JSONArray((Collection<?>) bean);
				rep = toRepresentation(arr, variant);
			} else if (bean instanceof Map) {
				rep = toRepresentation(new JSONObject((Map<?, ?>) bean),
						variant);
			} else {
				rep = toRepresentation(new JSONObject(bean), variant);
			}
		} else {
			throw new UnsupportedOperationException(variant.getMediaType()
					.toString());
		}
		rep.setMediaType(variant.getMediaType());
		return rep;
	}

	protected T fromRepresentation(final Representation r, final Variant variant)
			throws Exception {
		if (MediaType.APPLICATION_JSON.includes(variant.getMediaType())) {
			return reflector.beanFromJSONObject(toObject(r, JSONObject.class));
		}
		throw new UnsupportedOperationException(variant.getMediaType()
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

	protected Representation ERROR(final Status status, final Throwable t,
			final Variant variant) {
		t.printStackTrace();
		if (t instanceof BundleException) {
			setStatus(status);
			try {
				return toRepresentation(new BundleExceptionPojo(
						(BundleException) t), variant);
			} catch (final IOException ioe) {
				// fallback
			}
		}

		setStatus(status, t);
		return null;
	}

	protected Representation SUCCESS(final Status status) {
		setStatus(status);

		return SUCCESS;
	}

	protected Representation ERROR(final Status status) {
		setStatus(status);

		return null;
	}

}
