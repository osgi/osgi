/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.concurrent;

/**
 * An Executor which executes tasks asynchronously. The Async Executor service
 * is used by the framework instance to execute short duration tasks
 * asynchronously and is also registered as a service for all bundles to use.
 * 
 * <p>
 * The Async Executor implementation can be passed to the framework instance
 * when it is created. If none is provided, then the framework instance must
 * create a default Async Executor which will be used by the framework instance
 * and will also be registered as a service for all bundles to use.
 * 
 * @version $Revision$
 * @TheadSafe
 */
public interface AsyncExecutor {
	/**
	 * Executes the specified task asynchronously on another thread at some time
	 * in the future.
	 * 
	 * @param task A Runnable task to be asynchronously executed.
	 */
	void execute(Runnable task);
}
