/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.cdi.secure.junit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class CloseableTracker<S, T> extends ServiceTracker<S, T> implements AutoCloseable {

	public CloseableTracker(BundleContext context, Filter filter) {
		super(context, filter, null);
	}

	public CloseableTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer<S, T> customizer) {
		super(context, filter, customizer);
	}

}
