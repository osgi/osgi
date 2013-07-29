/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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

package org.osgi.service.resource;

/**
 * OSGi Resource Listener Service.
 * 
 * <p>
 * Bundles registering this service will be called by the Resource Manager when 
 * a {@link ResourceEvent} is generated.
 * <p>
 * The services can filter the events they are interested in using the
 * {@link #RESOURCE_CONTEXT} and {@link #RESOURCE_TYPE} registration properties.
 */
public interface ResourceListener {
  
  /**
   * ResourceListener services can use this registration property to filter
   * resource events based on the associated {@link ResourceContext}.
   * <p>
   * The value of the property can be a String or a String[] - to indicate 
   * interest in the events from a single context, or a list of contexts.
   * <p>
   * If the property is not specified, the listener will receive all events.
   */
  public final String RESOURCE_CONTEXT = "resource.context";
  
  /**
   * ResourceListener services can use this registration property to filter
   * resource events based on the associated resource type.
   * <p>
   * The value of the property can be a String or a String[] - to indicate 
   * interest in the events for a single context, or a list of resource types.
   * <p>
   * If the property is not specified, the listener will receive all events.
   */
  public final String RESOURCE_TYPE = "resource.type";
  
  /**
   * Receives a resource management notification
   * @param event The {@link ResourceEvent} oject
   */
  public void resourceEvent(ResourceEvent event);
}
