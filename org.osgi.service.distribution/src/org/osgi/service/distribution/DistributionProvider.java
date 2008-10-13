package org.osgi.service.distribution;

import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * Every Distribution Provider registers exactly one Service in the
 * ServiceRegistry implementing this interface. The service is registered with
 * extra properties identified at the beginning of this interface to denote the
 * Distribution Provider product name, version, vendor and supported intents.
 */
public interface DistributionProvider {
    /**
     * Service Registration property for the name of the Distribution Provider
     * product.
     */
    static final String PROP_KEY_PRODUCT_NAME = 
                            "osgi.remote.distribution.product";

    /**
     * Service Registration property for the version of the Distribution
     * Provider product.
     */
    static final String PROP_KEY_PRODUCT_VERSION =
                            "osgi.remote.distribution.product.version";

    /**
     * Service Registration property for the Distribution Provider product
     * vendor name.
     */
    static final String PROP_KEY_VENDOR_NAME = 
                            "osgi.remote.distribution.vendor";

    /**
     * Service Registration property that lists the intents supported by this
     * DistributionProvider.
     */
    static final String PROP_KEY_SUPPORTED_INTENTS =
                            "osgi.remote.distribition.supported_intents";

    /**
     * @return ServiceReferences of services registered in the local Service
     *         Registry that are proxies to remote services. If no proxies are
     *         registered, then an empty array is returned.
     */
    ServiceReference[] getRemoteServices();

    /**
     * @return ServiceReferences of local services that are exposed remotely 
     *         using this DisitributionProvider. Note that certain services may be
     *         exposed and without being published to a discovery service. This 
     *         API returns all the exposed services. If no services are exposed an 
     *         empty array is returned.
     */
    ServiceReference[] getExposedServices();

    /**
     * @return Local ServiceReferences of exposed services that are published 
     *         remotely to a discovery mechanism using this DisitributionProvider. 
     *         Note that certain services might be exposed without being
     *         published. 
     *         This API returns all the published service. If no services are 
     *         published an empty array is returned.
     */
    ServiceReference[] getPublishedServices();

    /**
     * Provides access to extra properties set by the DistributionProvider on
     * endpoints, as they will appear on client side proxies given an exposed
     * ServiceReference. 
     * These properties are not always available on the server-side
     * ServiceReference of the published
     * service but will be on the remote client side proxy to this service.
     * This API provides access to these extra properties from the publishing
     * side.
     * E.g. a service is exposed remotely, the distribution software is configured
     * to add transactionality to the remote service. Because of this, on the 
     * client-side proxy the property osgi.intents=”transactionality” is set. 
     * However, these intents are *not* always set on the original
     * ServiceRegistration on the server-side since on the server side the service
     * object is a local pojo which doesn’t provide transactionality by itself.
     * This QoS is added by the distribution.
     * This API provides access to these extra properties from the server-side.
     * 
     * @param sr A ServiceReference of a published service.
     * @return The map of extra properties.
     */
    Map getPublicationProperties(ServiceReference sr);
}
