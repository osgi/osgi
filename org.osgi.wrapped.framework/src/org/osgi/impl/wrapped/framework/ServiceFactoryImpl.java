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

package org.osgi.impl.wrapped.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.wrapped.framework.TServiceFactory;

public class ServiceFactoryImpl implements ServiceFactory {
	final TServiceFactory	factory;

	public ServiceFactoryImpl(TServiceFactory factory) {
		this.factory = factory;
	}

	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return factory.getService(new TBundleImpl(bundle),
				new TServiceRegistrationImpl(registration));
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
		factory.ungetService(new TBundleImpl(bundle),
				new TServiceRegistrationImpl(registration), service);
	}
	
	@Override
	public boolean equals(Object o) {
		return factory.equals(T.unwrap((ServiceFactory) o));
	}

	@Override
	public int hashCode() {
		return factory.hashCode();
	}

	@Override
	public String toString() {
		return factory.toString();
	}

}
