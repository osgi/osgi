/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
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

public class RestService extends Application {

	public static final String BUNDLE_CONTEXT_ATTR = "context";

	public static final String TRACKER_ATTR = "tracker";

	private final BundleContext context;

	private ServiceTracker<RestApiExtension, Class<? extends ServerResource>> tracker;

	RestService(final BundleContext context) {
		this.context = context;
		getTunnelService().setExtensionsTunnel(true);
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
		framework.attach("/bundles/{filter}/representations",
				BundleRepresentationsResource.class);
		framework.attach("/bundles/{filter}", BundlesResource.class);

		// a specific bundle
		framework.attach("/bundle/{bundleId}", BundleResource.class);
		framework.attach("/bundle/{bundleId}/state", BundleStateResource.class);
		framework.attach("/bundle/{bundleId}/startlevel",
				BundleStartLevelResource.class);
		framework.attach("/bundle/{bundleId}/headers",
				BundleHeaderResource.class);

		// available services
		framework.attach("/services", ServiceListResource.class);
		framework.attach("/services/representations",
				ServiceRepresentationsResource.class);
		framework.attach("/services/{filter}/representations",
				ServiceRepresentationsResource.class);
		framework.attach("/services/{filter}", ServiceListResource.class);

		// a specific service
		framework.attach("/service/{serviceId}", ServiceResource.class);

		// enable the extension mechanism
		final Router extensions = new Router(getContext());
		root.attach("/extensions", extensions);

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

		private final Router router;

		public ExtensionsTrackerCustomizer(final Router router) {
			this.router = router;
		}

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

				System.out.println("attached " + uri_path);

				return proprietary_extension_unit;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}

		public void modifiedService(
				final ServiceReference<RestApiExtension> reference,
				final Class<? extends ServerResource> service) {
			removedService(reference, service);
			addingService(reference);
		}

		public void removedService(
				final ServiceReference<RestApiExtension> reference,
				final Class<? extends ServerResource> service) {
			router.detach(service);
		}

	}

}
