/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
package org.osgi.service.blueprint.context;

/**
 * If a component implements this interface then the setModuleContext operation
 * will be invoked after the component instance has been instantiated and before
 * the init-method (if specified) has been invoked.
 *
 */
public interface ModuleContextAware {
	
	/**
	 * Set the module context of the module in which the implementor is 
	 * executing.
	 * 
	 * @param context the module context in which the implementor of 
	 * this interface is executing.
	 */
	void setModuleContext(ModuleContext context);
}
