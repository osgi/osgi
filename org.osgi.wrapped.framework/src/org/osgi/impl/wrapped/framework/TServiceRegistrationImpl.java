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

import java.util.Dictionary;

import org.osgi.framework.ServiceRegistration;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TServiceRegistration;

public class TServiceRegistrationImpl implements TServiceRegistration {
	
	final ServiceRegistration	registration;

	TServiceRegistrationImpl(ServiceRegistration reference) {
		this.registration = reference;
	}

	public TServiceReference getReference() {
		return new TServiceReferenceImpl(registration.getReference());
	}

	@SuppressWarnings("unchecked")
	public void setProperties(Dictionary properties) {
		registration.setProperties(properties);
	}

	public void unregister() {
		registration.unregister();
	}
	
	@Override
	public boolean equals(Object arg0) {
		return registration.equals(arg0);
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
