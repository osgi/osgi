/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.service.resourcemonitoring;

/**
 * A Resource Monitor Factory is a service that provides Resource Monitor
 * instances of a specific resource type (e.g., CPUMonitor, MemoryMonitor...)
 * for every Resource Context.
 * 
 * Every Resource Monitor Factory service is registered with the
 * {@link #RESOURCE_TYPE_PROPERTY} mandatory property. This property indicates
 * which type of Resource Monitor a Resource Monitor Factory is able to create.
 * The type can also be retrieved through a call to {@link #getType()}. The type
 * MUST be unique (two Resource Monitor Factory instances MUST not have the same
 * type).
 * 
 * @version 1.0
 * @author $Id$
 */
public interface ResourceMonitorFactory {

	/**
	 * Resource type property. The value is of type {@link String}, e.g.,
	 * {@link ResourceMonitoringService#RES_TYPE_CPU}
	 */
	public static final String	RESOURCE_TYPE_PROPERTY	= "org.osgi.resourcemonitoring.ResourceType";

	/**
	 * Returns the type of ResourceMonitor instance this factory is able to
	 * create.
	 * 
	 * @return factory type
	 */
	public String getType();

	/**
	 * Creates a new ResourceMonitor instance. This instance is associated with
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
