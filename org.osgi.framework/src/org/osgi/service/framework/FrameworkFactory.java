/*
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
package org.osgi.service.framework;

import java.util.Map;
import org.osgi.framework.launch.SystemBundle;

/**
 * A framework factory ...
 *
 */
// TODO javadoc needs much work!!
public interface FrameworkFactory {
	/**
	 * Creates a new SystemBundle that is a child of framework which registered this
	 * framework factory.
	 * @param configuration the framework configuration
	 * @return an unintialized SystemBundle
	 * @see SystemBundle
	 */
	SystemBundle createChildBundle(Map configuration);

	/**
	 * Creates a link between two rframeworks.
	 * @param source the source framework
	 * @param target the target framework
	 * @param description
	 * @return a framework link
	 */
	FrameworkLink createFrameworkLink(SystemBundle source, SystemBundle target, LinkDescription description);
}
