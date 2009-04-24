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

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.wrapped.framework.TServiceRegistration;

public class ServiceRegistrationImpl implements ServiceRegistration {

	final TServiceRegistration	registration;

	ServiceRegistrationImpl(TServiceRegistration reference) {
		this.registration = reference;
	}
	
	public ServiceReference getReference() {
		return new ServiceReferenceImpl(registration.getReference());
	}

	public void setProperties(Map<String, Object> properties) {
		registration.setProperties(new Hashtable<String, Object>(properties));
	}

	public void unregister() {
		registration.unregister();
	}

	@Override
	public boolean equals(Object o) {
		return registration.equals(T.getWrapped((ServiceRegistration) o));
	}

	@Override
	public int hashCode() {
		return registration.hashCode();
	}

	@Override
	public String toString() {
		return registration.toString();
	}


}
