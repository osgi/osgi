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
package org.osgi.framework.resource;

/**
 * A {@link ResourceMonitor} for the 
 * {@link ResourceManager#RES_TYPE_MEMORY} resource type.
 * A MemoryMonitor instance monitors and limits the memory used
 * by a {@link ResourceContext} instance.
 *   
 */
public interface MemoryMonitor extends ResourceMonitor {
  
  /**
   * Returns the size of the java heap used by the bundles in this 
   * resource context.
   * <p>
   * The {@link #getUsage()} method returns the same value,
   * wrapped in a {@link Long}
   * @return the size of the used java heap in bytes
   */
  public long getUsedMemory();
  
  
}
