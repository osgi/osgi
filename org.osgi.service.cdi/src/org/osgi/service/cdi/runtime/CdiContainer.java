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

import javax.enterprise.inject.spi.BeanManager;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cdi.CdiConstants;
import org.osgi.service.cdi.dto.ContainerDTO;
import org.osgi.service.cdi.dto.model.ContainerModelDTO;

/**
 * A {@code CdiContainer} service is registered by the CDI extender for each
 * managed CDI container, one per CDI Bundle.
 * <p>
 * This service must be registered with a {@link CdiConstants#CDI_CONTAINER_ID}
 * service property which holds the CDI container id.
 * <p>
 * This service must be registered with a
 * {@link CdiConstants#CDI_CONTAINER_STATE} service property that must be
 * updated each time the container's state changes.
 *
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface CdiContainer {

	/**
	 * When the {@code CdiContainer}'s state is {@link CdiContainerState#CREATED
	 * CREATED} return the container's {@link BeanManager BeanManager}, otherwise
	 * {@code null}.
	 *
	 * @return the container's {@link BeanManager BeanManager} when the
	 *         {@code CdiContainer}'s state is {@link CdiContainerState#CREATED
	 *         CREATED}, otherwise {@code null}
	 */
	public BeanManager getBeanManager();

	/**
	 * Obtain the CDI container snapshot.
	 *
	 * @return the CDI container snapshot
	 */
	public ContainerDTO getContainerDTO();

	/**
	 * Obtain the CDI container model.
	 *
	 * @return the CDI container model
	 */
	public ContainerModelDTO getContainerModelDTO();

}
