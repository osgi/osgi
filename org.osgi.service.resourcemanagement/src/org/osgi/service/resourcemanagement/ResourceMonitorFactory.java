/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.resourcemanagement;

/**
 * A ResourceMonitorFactory are OSGI services used to create ResourceMonitor
 * instance. These factories should only be used by ResourceManager singleton or
 * authorities.
 * 
 * @author see RFC 200 authors: Andre Bottaro, Gregory Bonnardel, Svetozar
 *         Dimov, Evgeni Grigorov, Arnaud Rinquin, Antonin Chazalet.
 */
public interface ResourceMonitorFactory {

	/** RESOURCE_TYPE_PROPERTY */
	public static final String	RESOURCE_TYPE_PROPERTY	= "org.osgi.resourcemanagement.ResourceType";

	/**
	 * Return the type of ResourceMonitor instance this factory is able to
	 * create.
	 * 
	 * @return factory type
	 */
	public String getType();

	/**
	 * Create a new ResourceMonitor instance. This instance is associated with
	 * the ResourceContext instance provided as argument (
	 * {@link ResourceContext#addResourceMonitor(ResourceMonitor)} is called by
	 * the factory). The newly ResourceMonitor instance is disabled. It can be
	 * enabled by calling {@link ResourceMonitor#enable()}.
	 * 
	 * @param resourceContext ResourceContext instance associated with the newly
	 *        created ResourceMonitor instance
	 * @return a ResourceMonitor instance
	 * @throws ResourceMonitorException
	 */
	public ResourceMonitor createResourceMonitor(ResourceContext resourceContext) throws ResourceMonitorException;

}
