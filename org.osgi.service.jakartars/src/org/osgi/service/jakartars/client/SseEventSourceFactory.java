/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
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
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.jakartars.client;

import org.osgi.annotation.versioning.ProviderType;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;

/**
 * A factory for {@link SseEventSource} instances.
 * <p>
 * Bundles may obtain an instance of a {@link SseEventSourceFactory} using the
 * service registry. This service may then be used to construct
 * {@link SseEventSource} instances for the supplied {@link WebTarget}.
 * 
 * @author $Id$
 */
@ProviderType
public interface SseEventSourceFactory {

	/**
	 * Create a new {@link jakarta.ws.rs.sse.SseEventSource.Builder}
	 * 
	 * @param target The web target to consume events from
	 * @return a builder which can be used to further configure the event source
	 */
	public SseEventSource.Builder newBuilder(WebTarget target);

	/**
	 * Create a new {@link SseEventSource}
	 * 
	 * @param target The web target to consume events from
	 * @return a configured event source
	 */
	public SseEventSource newSource(WebTarget target);
}
