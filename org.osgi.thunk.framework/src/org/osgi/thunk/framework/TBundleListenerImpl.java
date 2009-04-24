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

import org.osgi.framework.BundleEvent;
import org.osgi.framework.bundle.BundleListener;
import org.osgi.wrapped.framework.TBundleEvent;
import org.osgi.wrapped.framework.TBundleListener;

public class TBundleListenerImpl implements TBundleListener {
	final BundleListener	listener;

	TBundleListenerImpl(BundleListener listener) {
		this.listener = listener;
	}

	public void bundleChanged(TBundleEvent event) {
		listener.bundleChanged(new BundleEvent(event.getType(), new BundleImpl(
				event.getBundle())));
	}

	@Override
	public boolean equals(Object o) {
		return listener.equals(T.getWrapped((TBundleListener) o));
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
