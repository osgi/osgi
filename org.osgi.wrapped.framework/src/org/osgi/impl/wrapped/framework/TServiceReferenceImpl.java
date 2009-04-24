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
import org.osgi.framework.ServiceReference;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TServiceReference;

public class TServiceReferenceImpl implements TServiceReference {
	final ServiceReference	reference;

	TServiceReferenceImpl(ServiceReference reference) {
		this.reference = reference;
	}

	public int compareTo(Object o) {
		return reference.compareTo(T.getWrapped((TServiceReference) o));
	}

	public TBundle getBundle() {
		return new TBundleImpl(reference.getBundle());
	}

	public Object getProperty(String key) {
		return reference.getProperty(key);
	}

	public String[] getPropertyKeys() {
		return reference.getPropertyKeys();
	}

	public TBundle[] getUsingBundles() {
		Bundle[] bundles = reference.getUsingBundles();
		return T.getBundles(bundles);
	}

	public boolean isAssignableTo(TBundle bundle, String className) {
		return reference.isAssignableTo(T.getWrapped(bundle),
				className);
	}

	@Override
	public boolean equals(Object o) {
		return reference.equals(T.getWrapped((TServiceReference) o));
	}

	@Override
	public int hashCode() {
		return reference.hashCode();
	}

	@Override
	public String toString() {
		return reference.toString();
	}

}
