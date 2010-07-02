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

package org.osgi.framework;

/**
 * Allows services to provide customized service objects in the OSGi
 * environment.
 * 
 * <p>
 * When registering a service, a {@code ServiceFactory} object can be
 * used instead of a service object, so that the bundle developer can gain
 * control of the specific service object granted to a bundle that is using the
 * service.
 * 
 * <p>
 * When this happens, the
 * {@code BundleContext.getService(ServiceReference)} method calls the
 * {@code ServiceFactory.getService} method to create a service object
 * specifically for the requesting bundle. The service object returned by the
 * {@code ServiceFactory} is cached by the Framework until the bundle
 * releases its use of the service.
 * 
 * <p>
 * When the bundle's use count for the service equals zero (including the bundle
 * stopping or the service being unregistered), the
 * {@code ServiceFactory.ungetService} method is called.
 * 
 * <p>
 * {@code ServiceFactory} objects are only used by the Framework and are
 * not made available to other bundles in the OSGi environment. The Framework
 * may concurrently call a {@code ServiceFactory}.
 * 
 * @see BundleContext#getService
 * @ThreadSafe
 * @version $Id$
 */

public interface ServiceFactory {
	/**
	 * Creates a new service object.
	 * 
	 * <p>
	 * The Framework invokes this method the first time the specified
	 * {@code bundle} requests a service object using the
	 * {@code BundleContext.getService(ServiceReference)} method. The
	 * service factory can then return a specific service object for each
	 * bundle.
	 * 
	 * <p>
	 * The Framework caches the value returned (unless it is {@code null}),
	 * and will return the same service object on any future call to
	 * {@code BundleContext.getService} for the same bundle. This means the
	 * Framework must not allow this method to be concurrently called for the
	 * same bundle.
	 * 
	 * <p>
	 * The Framework will check if the returned service object is an instance of
	 * all the classes named when the service was registered. If not, then
	 * {@code null} is returned to the bundle.
	 * 
	 * @param bundle The bundle using the service.
	 * @param registration The {@code ServiceRegistration} object for the
	 *        service.
	 * @return A service object that <strong>must</strong> be an instance of all
	 *         the classes named when the service was registered.
	 * @see BundleContext#getService
	 */
	public Object getService(Bundle bundle, ServiceRegistration registration);

	/**
	 * Releases a service object.
	 * 
	 * <p>
	 * The Framework invokes this method when a service has been released by a
	 * bundle. The service object may then be destroyed.
	 * 
	 * @param bundle The bundle releasing the service.
	 * @param registration The {@code ServiceRegistration} object for the
	 *        service.
	 * @param service The service object returned by a previous call to the
	 *        {@code ServiceFactory.getService} method.
	 * @see BundleContext#ungetService
	 */
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service);
}
