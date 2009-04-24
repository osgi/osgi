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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TServiceReference;

public class ServiceReferenceImpl implements ServiceReference {
	final TServiceReference	reference;

	ServiceReferenceImpl(TServiceReference reference) {
		this.reference = reference;
	}

	public int compareTo(ServiceReference r) {
		return reference.compareTo(r);
	}

	public Bundle getBundle() {
		return new BundleImpl(reference.getBundle());
	}

	public Object getProperty(String key) {
		return reference.getProperty(key);
	}

	public Set<String> getPropertyKeys() {
		return new HashSet<String>(Arrays.asList(reference.getPropertyKeys()));
	}

	public Collection< ? extends Bundle> getUsingBundles() {
		TBundle[] bundles = reference.getUsingBundles();
		return T.getBundles(bundles);
	}

	public boolean isAssignableTo(Bundle bundle, String className) {
		return reference.isAssignableTo(T.getWrapped(bundle), className);
	}

}
