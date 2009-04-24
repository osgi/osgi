/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
package org.osgi.service.distribution;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * Every Distribution Provider registers exactly one Service in the
 * ServiceRegistry implementing this interface. The service is registered with
 * extra properties identified at the beginning of this interface to denote the
 * Distribution Provider product name, version, vendor and supported intents.
 *
 * @ThreadSafe
 * @version $Revision$
 */
public interface DistributionProvider {
    /**
     * Service Registration property for the name of the Distribution Provider
     * product.
	 * <p>
	 * The value of this property is of type String.
     */
    static final String PRODUCT_NAME = 
                            "osgi.remote.distribution.product";

    /**
     * Service Registration property for the version of the Distribution
     * Provider product.
	 * <p>
	 * The value of this property is of type String.
     */
    static final String PRODUCT_VERSION =
                            "osgi.remote.distribution.product.version";

    /**
     * Service Registration property for the Distribution Provider product
     * vendor name.
	 * <p>
	 * The value of this property is of type String.
     */
    static final String VENDOR_NAME = 
                            "osgi.remote.distribution.vendor";

	/**
	 * Service Registration property that lists the intents supported by this
	 * DistributionProvider.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
    static final String SUPPORTED_INTENTS =
                            "osgi.remote.distribition.supported_intents";

    /**
     * @return ServiceReferences of services registered in the local Service
     *         Registry that are proxies to remote services. If no proxies are
     *         registered, then an empty collection is returned.
     */
    Collection /*<? extends ServiceReference>*/ getRemoteServices();

    /**
     * @return ServiceReferences of local services that are exposed remotely 
     *         using this DisitributionProvider. Note that certain services may be
     *         exposed and without being published to a discovery service. This 
     *         API returns all the exposed services. If no services are exposed an 
     *         empty collection is returned.
     */
    Collection /*<? extends ServiceReference>*/ getExposedServices();

	/**
	 * Provides access to extra properties set by the DistributionProvider on
	 * endpoints, as they will appear on client side proxies given an exposed
	 * ServiceReference. These properties are not always available on the
	 * server-side ServiceReference of the exposed service but will be on the
	 * remote client side proxy to this service. This API provides access to
	 * these extra properties from the exposing side. E.g. a service is exposed
	 * remotely, the distribution software is configured to add transactionality
	 * to the remote service. Because of this, on the client-side proxy the
	 * property serviceosgi.intents="transactionality" is set. However, these
	 * intents are <i>not</i> always set on the original ServiceRegistration on
	 * the server-side since on the server side the service object is a local
	 * pojo which doesn't provide transactionality by itself. This QoS is added
	 * by the distribution. This API provides access to these extra properties
	 * from the server-side.
	 * 
	 * @param sr A ServiceReference of an exposed service.
	 * @return The map of extra properties.
	 */
    Map /*<String, String>*/ getExposedProperties(ServiceReference sr);
}

