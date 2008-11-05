/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.discovery;

import java.util.Collection;

/**
 * Interface for callback objects, which can be provided with an asynchronous
 * find service operation and which will be called when the operation actually
 * finished.
 * 
 * @version $Revision$
 */
public interface FindServiceCallback {
	/**
	 * Callback indicating that a previously started asynchronous find service
	 * operation finished.
	 * 
	 * @param serviceEndpointDescriptions
	 *            ServiceDescription objects satisfying the provided find
	 *            criteria. The collection is never null but may be empty if
	 *            none was found. The collection represents a snapshot and as
	 *            such is not going to be updated in case other matching
	 *            services become available at a later point of time.
	 */
	void servicesFound(
			Collection /* <? extends ServiceEndpointDescription> */serviceEndpointDescriptions);
}
