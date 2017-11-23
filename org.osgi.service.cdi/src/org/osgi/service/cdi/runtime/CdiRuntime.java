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
 * @author $Id: ccfde01de427bf319588042619e24fd5501530fa $
 */
@ProviderType
public interface CdiRuntime {
	/**
	 * Returns the container description snapshot for a set of bundles
	 *
	 * @param bundles The bundles who's container description snapshots are to be
	 *        returned. Specifying no bundles, or the equivalent of an empty
	 *        {@code Bundle} array, will return the container descriptions of all
	 *        active bundles that define a container.
	 * @return A set of descriptions of the container of the specified
	 *         {@code bundles}. Only bundles that have an associated container are
	 *         included. If a bundle is listed multiple times in {@code bundles}
	 *         only one {@link ContainerDTO} is returned. Returns an empty
	 *         collection rather than {@code null}.
	 */
	Collection<ContainerDTO> getContainerDTOs(Bundle... bundles);

	/**
	 * Returns the container description snapshot for the specified bundle
	 *
	 * @param bundle The container bundle. Must not be {@code null}.
	 * @return A snapshot of the current container for the specified active bundle.
	 *         {@code null} is returned if the provided bundle does not have an
	 *         associated container.
	 */
	ContainerDTO getContainerDTO(Bundle bundle);

	/**
	 * Returns the change count of the container of the specified bundle.
	 * <p>
	 * The returned change count is equal to the value of
	 * {@link Constants#SERVICE_CHANGECOUNT} of this {@link CdiRuntime} service at
	 * the time of the last change of the container of the specified bundle.
	 * <p>
	 * The returned number is equal or greater to the
	 * {@link ContainerDTO#changeCount} of any previously created
	 * {@link ContainerDTO} for the specified bundle.
	 * <p>
	 * It is permissible for multiple bundles to have the same change count.
	 * 
	 * @param bundle
	 * @return A positive number indicating the last time the {@link ContainerDTO}
	 *         of the specified bundle changed value. If the supplied bundle does
	 *         not have an associated container returns {@code -1}.
	 */
	long getContainerChangeCount(Bundle bundle);

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
