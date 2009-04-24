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

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.wrapped.framework.TBundleEvent;
import org.osgi.wrapped.framework.TBundleListener;

public class BundleListenerImpl implements BundleListener {
	final TBundleListener	listener;

	BundleListenerImpl(TBundleListener listener) {
		this.listener = listener;
	}

	public void bundleChanged(BundleEvent event) {
		listener.bundleChanged(new TBundleEvent(event.getType(),
				new TBundleImpl(event.getBundle())));
	}
	@Override
	public boolean equals(Object o) {
		return listener.equals(T.getWrapped((BundleListener) o));
	}

	@Override
	public int hashCode() {
		return listener.hashCode();
	}

	@Override
	public String toString() {
		return listener.toString();
	}

}
