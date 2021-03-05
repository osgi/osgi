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

package org.osgi.impl.service.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.rest.resources.BundleHeaderResource;
import org.osgi.impl.service.rest.resources.BundleRepresentationsResource;
import org.osgi.impl.service.rest.resources.BundleResource;
import org.osgi.impl.service.rest.resources.BundleStartLevelResource;
import org.osgi.impl.service.rest.resources.BundleStateResource;
import org.osgi.impl.service.rest.resources.BundlesResource;
import org.osgi.impl.service.rest.resources.ExtensionsResource;
import org.osgi.impl.service.rest.resources.FrameworkStartLevelResource;
import org.osgi.impl.service.rest.resources.ServiceListResource;
import org.osgi.impl.service.rest.resources.ServiceRepresentationsResource;
import org.osgi.impl.service.rest.resources.ServiceResource;
import org.osgi.service.rest.RestApiExtension;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * Rest service implementation. Manages the routing of paths to resources, etc.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class RestService extends Application {

	public static final String													BUNDLE_CONTEXT_ATTR	= "context";

	public static final String													TRACKER_ATTR		= "tracker";

	public static final String													SERVICE_ID_KEY		= "serviceId";

	public static final String													FILTER_ID_KEY		= "filter";

	public static final String													BUNDLE_ID_KEY		= "bundleId";

	private final BundleContext													context;

	private ServiceTracker<RestApiExtension, Class<? extends ServerResource>>	tracker;

	RestService(final BundleContext context) {
		this.context = context;
	}

	@Override
	public synchronized Restlet createInboundRoot() {
		final Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(BUNDLE_CONTEXT_ATTR, context);

		final Router root = new Router(getContext());
		// framework
		final Router framework = new Router(getContext());
		root.attach("/framework", framework);
		framework.attach("/startlevel", FrameworkStartLevelResource.class);

		// the registered bundles
		framework.attach("/bundles", BundlesResource.class);
		framework.attach("/bundles/representations",
				BundleRepresentationsResource.class);

		// a specific bundle
		framework.attach("/bundle/{" + BUNDLE_ID_KEY + "}", BundleResource.class);
		framework.attach("/bundle/{" + BUNDLE_ID_KEY + "}/state", BundleStateResource.class);
		framework.attach("/bundle/{" + BUNDLE_ID_KEY + "}/startlevel",
				BundleStartLevelResource.class);
		framework.attach("/bundle/{" + BUNDLE_ID_KEY + "}/header",
				BundleHeaderResource.class);

		// available services
		framework.attach("/services", ServiceListResource.class);
		framework.attach("/services/representations",
				ServiceRepresentationsResource.class);

		// a specific service
		framework.attach("/service/{" + SERVICE_ID_KEY + "}", ServiceResource.class);

		// enable the extension mechanism
		final Router extensions = new Router(getContext());
		root.attach("/extensions", extensions);

		getTunnelService().setExtensionsTunnel(true);

		tracker = new ServiceTracker<RestApiExtension, Class<? extends ServerResource>>(
				context, RestApiExtension.class,
				new ExtensionsTrackerCustomizer(extensions));
		tracker.open();

		attributes.put(TRACKER_ATTR, tracker);
		getContext().setAttributes(attributes);

		extensions.attach("", ExtensionsResource.class);

		return root;
	}

	protected static class ExtensionsTrackerCustomizer
			implements
			ServiceTrackerCustomizer<RestApiExtension, Class<? extends ServerResource>> {

		private final Router	router;

		public ExtensionsTrackerCustomizer(final Router router) {
			this.router = router;
		}

		@Override
		public Class<? extends ServerResource> addingService(
				final ServiceReference<RestApiExtension> reference) {
			final String uri_path = (String) reference
					.getProperty(RestApiExtension.URI_PATH);
			@SuppressWarnings("unchecked")
			final Class<? extends ServerResource> proprietary_extension_unit = (Class<? extends ServerResource>) reference
					.getProperty("restlet");
			// check if the uri is valid.
			try {
				final URI uri = new URI("/" + uri_path);
				router.attach(uri.toString(), proprietary_extension_unit);

				return proprietary_extension_unit;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void modifiedService(
				final ServiceReference<RestApiExtension> reference,
				final Class<? extends ServerResource> service) {
			removedService(reference, service);
			addingService(reference);
		}

		@Override
		public void removedService(
				final ServiceReference<RestApiExtension> reference,
				final Class<? extends ServerResource> service) {
			router.detach(service);
		}

	}

}
