/**
 * 
 */
package org.osgi.impl.service.discovery.slp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Thomas Kiesslich
 * 
 */
public class ServicePublicationTracker implements ServiceTrackerCustomizer {

	private BundleContext context = null;

	private SLPHandlerImpl discovery = null;

	private Map/* <ServiceReference, ServiceEndpointDescription> */publicationAndSED = Collections
			.synchronizedMap(new HashMap());

	public ServicePublicationTracker(BundleContext ctx, SLPHandlerImpl disco) {
		context = ctx;
		discovery = disco;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference arg0) {
		ServicePublication sp = publishServicePublication(arg0);
		return sp;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void modifiedService(ServiceReference arg0, Object arg1) {
		unpublishServicePublication(arg0);
		publishServicePublication(arg0);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void removedService(ServiceReference arg0, Object arg1) {
		unpublishServicePublication(arg0);
	}

	/**
	 * 
	 * @param arg0
	 * @return
	 */
	private ServicePublication publishServicePublication(ServiceReference arg0) {
		ServicePublication sp = (ServicePublication) context.getService(arg0);
		ServiceEndpointDescription sed = discovery
				.publishService(
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME),
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION),
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME),
						(Map) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_PROPERTIES),
						SLPHandlerImpl.PROP_VAL_PUBLISH_STRATEGY_PUSH,
						(String) arg0
								.getProperty(ServicePublication.PROP_KEY_ENDPOINT_ID));
		publicationAndSED.put(arg0, sed);
		return sp;
	}

	/**
	 * 
	 * @param srvReference
	 *            the given reference to the service to unpublish
	 */
	private void unpublishServicePublication(ServiceReference srvReference) {
		discovery
				.unpublishService((ServiceEndpointDescription) publicationAndSED
						.get(srvReference));
		publicationAndSED.remove(srvReference);
	}
}
