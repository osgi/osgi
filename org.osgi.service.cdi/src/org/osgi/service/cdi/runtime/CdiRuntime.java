/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.service.cdi.runtime;

import java.util.Collection;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.cdi.dto.ContainerDTO;
import org.osgi.service.cdi.dto.template.ContainerTemplateDTO;

/**
 * The {@code CdiRuntime} service represents the CDI Runtime that manages the
 * CDI containers and their life cycle. The {@code CdiRuntime} service allows
 * introspection of the CDI containers managed by CDI Runtime.
 * <p>
 * This service must be registered with a {@link Constants#SERVICE_CHANGECOUNT}
 * service property that must be updated each time any of the DTOs available
 * from this service change.
 * <p>
 * Access to this service requires the
 * {@code ServicePermission[CdiRuntime, GET]} permission. It is intended that
 * only administrative bundles should be granted this permission to limit access
 * to the potentially intrusive methods provided by this service.
 *
 * @ThreadSafe
 * @author $Id: 7eb44b532af575c65929deec4b97dcbefc7181f4 $
 */
@ProviderType
public interface CdiRuntime {
	/**
	 * Returns the container snapshots declared by the specified active bundles.
	 * <p>
	 * Only container snapshots from active bundles are returned. If the specified
	 * bundles have no declared container or are not active, an empty collection is
	 * returned.
	 *
	 * @param bundles The bundles who's container snapshots are to be returned.
	 *        Specifying no bundles, or the equivalent of an empty {@code Bundle}
	 *        array, will return the declared container templates from all active
	 *        bundles.
	 * @return The declared container templates of the specified active
	 *         {@code bundles}. An empty collection is returned if there are no
	 *         container templates for the specified active bundles.
	 */
	Collection<ContainerDTO> getContainerDTOs(Bundle... bundles);

	/**
	 * Returns the container snapshot for the specified bundle.
	 *
	 * @param bundle The container bundle. Must not be {@code null}.
	 * @return A snapshot of the current container for the specified container
	 *         template. {@code null} is returned if the provided bundle does not
	 *         have an associated CDI container.
	 */
	ContainerDTO getContainerDTO(Bundle bundle);

	/**
	 * Returns the change count of the container hosted in the specified bundle
	 * 
	 * @param bundle
	 * @return A positive number indicating the last time this container changed
	 *         it's {@link ContainerDTO}. If the supplied bundle does not have an
	 *         associated CDI container returns {@code -1}.
	 */
	long getContainerChangeCount(Bundle bundle);

	/**
	 * Returns the {@link ContainerTemplateDTO} declared by the specified bundle.
	 *
	 * <p>
	 * Only container models from active bundles are returned. {@code null} if no
	 * container is declared by the given {@code bundle} or the bundle is not
	 * active.
	 *
	 * @param bundle The bundle declaring the container. Must not be {@code null}.
	 * @return The declared container model or {@code null} if the specified bundle
	 *         is not active or does not declare a container.
	 */
	ContainerTemplateDTO getContainerTemplateDTO(Bundle bundle);
}
