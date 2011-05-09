/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.service.obr;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.wiring.Resource;

/**
 * TODO
 *
 */
public interface RepositoryDelta {
	final RepositoryDelta EMPTY = new RepositoryDelta() {
		
		public List<Resource> getRemovedResources() {
			return Collections.emptyList();
		}
		
		public List<Resource> getChangedResources() {
			return Collections.emptyList();
		}
		
		public List<Resource> getAddedResources() {
			return Collections.emptyList();
		}
	};
  /**
   * TODO 
   * @return
   */
  public List<Resource> getAddedResources();

  /**
   * TODO 
   * @return
   */
  public List<Resource> getChangedResources();

  /**
   * TODO 
   * @return
   */
  public List<Resource> getRemovedResources();
}
