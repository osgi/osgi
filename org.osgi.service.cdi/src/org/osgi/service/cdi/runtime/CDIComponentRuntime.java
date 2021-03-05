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

package org.osgi.service.cdi.runtime;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.service.cdi.runtime.dto.template.ContainerTemplateDTO;

/**
 * The {@link CDIComponentRuntime} service represents the actor that manages the
 * CDI containers and their life cycle. The {@link CDIComponentRuntime} service
 * allows introspection of the managed CDI containers.
 * <p>
 * This service must be registered with a {@link Constants#SERVICE_CHANGECOUNT}
 * service property that must be updated each time any of the DTOs available
 * from this service change.
 * <p>
 * Access to this service requires the
 * {@code ServicePermission[CDIComponentRuntime, GET]} permission. It is
 * intended that only administrative bundles should be granted this permission
 * to limit access to the potentially intrusive methods provided by this
 * service.
 *
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface CDIComponentRuntime {
	/**
	 * Returns a collection of container description snapshots for a set of
	 * bundles.
	 *
	 * @param bundles The bundles who's container description snapshots are to
	 *            be returned. Specifying no bundles, or the equivalent of an
	 *            empty {@code Bundle} array, will return the container
	 *            descriptions of all active bundles that define a container.
	 * @return A set of descriptions of the container of the specified
	 *         {@code bundles}. Only bundles that have an associated container
	 *         are included. If a bundle is listed multiple times in
	 *         {@code bundles} only one {@link ContainerDTO} is returned.
	 *         Returns an empty collection if no CDI containers are found.
	 */
	Collection<ContainerDTO> getContainerDTOs(Bundle... bundles);

	/**
	 * Returns the {@link ContainerTemplateDTO} for the specified bundle
	 *
	 * @param bundle The bundle defining a container. Must not be {@code null} and
	 *        must be active.
	 * @return The container template for of the specified bundle or {@code null} if
	 *         it does not have an associated container.
	 */
	ContainerTemplateDTO getContainerTemplateDTO(Bundle bundle);
}
