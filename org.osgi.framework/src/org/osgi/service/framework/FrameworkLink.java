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

import org.osgi.framework.Bundle;

/**
 * A link between two frameworks
 */
//TODO javadoc needs much work!!
public interface FrameworkLink {
	/**
	 * Returns the link description that specifies the content which is 
	 * shared over the framework link.
	 * @return the link description
	 */
	LinkDescription getLinkDescription();

	/**
	 * Sets the link description that specifies the content which is 
	 * shared over the framework link.
	 * @param description the link description
	 */
	void setLinkDescription(LinkDescription description);

	/**
	 * Returns the bundle representing the content imported from the 
	 * source framework.
	 * @return the source bundle
	 */
	Bundle getSource();

	/**
	 * Returns the bundle representing the content exported to the 
	 * target framework.
	 * @return the target bundle
	 */
	Bundle getTarget();
}
