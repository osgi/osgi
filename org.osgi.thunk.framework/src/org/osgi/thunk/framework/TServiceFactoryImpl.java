/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.thunk.framework;

import org.osgi.framework.bundle.ServiceFactory;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TServiceFactory;
import org.osgi.wrapped.framework.TServiceRegistration;

public class TServiceFactoryImpl implements TServiceFactory {
	final ServiceFactory	factory;

	public TServiceFactoryImpl(ServiceFactory factory) {
		this.factory = factory;
	}

	public Object getService(TBundle bundle, TServiceRegistration registration) {
		return factory.getService(new BundleImpl(bundle),
				new ServiceRegistrationImpl(registration));
	}

	public void ungetService(TBundle bundle, TServiceRegistration registration,
			Object service) {
		factory.ungetService(new BundleImpl(bundle),
				new ServiceRegistrationImpl(registration), service);
	}

}
