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

import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TVersion;
import org.osgi.wrapped.service.packageadmin.TRequiredBundle;

public class TRequiredBundleImpl implements TRequiredBundle {
	final RequiredBundle	rb;

	TRequiredBundleImpl(RequiredBundle rb) {
		this.rb = rb;
	}

	public TBundle getBundle() {
		return T.toTBundle(rb.getBundle());
	}

	public TBundle[] getRequiringBundles() {
		return T.toTBundles(rb.getRequiringBundles());
	}

	public String getSymbolicName() {
		return rb.getSymbolicName();
	}

	public TVersion getVersion() {
		return T.toTVersion(rb.getVersion());
	}

	public boolean isRemovalPending() {
		return rb.isRemovalPending();
	}
	
	@Override
	public boolean equals(Object o) {
		return rb.equals(T.unwrap((TRequiredBundle) o));
	}

	@Override
	public int hashCode() {
		return rb.hashCode();
	}

	@Override
	public String toString() {
		return rb.toString();
	}
}
