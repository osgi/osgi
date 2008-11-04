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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

/**
 * Framework service that is used to create child frameworks and framework
 * links.
 * <p>
 * If present, there will only be a single instance of this service registered
 * with the Framework.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
// TODO javadoc needs review
public interface FrameworkFactory {
	/**
	 * Creates a new <code>Framework</code> that is a child of the Framework
	 * which owns this factory.
	 * <p>
	 * The new <code>Framework</code> returned will be in the
	 * {@link Bundle#INSTALLED INSTALLED} state. This child
	 * <code>Framework</code> can then be used to manage and control the child
	 * framework instance.
	 * <p>
	 * The child framework lifecycle is tied to its parent framework. When the
	 * parent <code>Framework</code> enters the {@link Bundle#STOPPING STOPPING}
	 * state then all child frameworks of that parent are shutdown using the to
	 * the {@link Framework#stop()} method. The parent <code>Framework</code>
	 * must not enter the {@link Bundle#RESOLVED} state until all of the child
	 * frameworks have completed their shutdown process. After the parent
	 * framework has completed the shutdown process then all child framework
	 * instances become invalid and must not be allowed to re-initialize or
	 * re-start.
	 * <p>
	 * Child framework instances are not automatically recreated when their
	 * parent framework is restarted.
	 * 
	 * @param configuration the framework configuration.
	 * @return an unintialized Framework in the {@link Bundle#INSTALLED
	 *         INSTALLED} state
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @see Framework
	 */
	Framework newChildFramework(Map configuration);

	/**
	 * Creates a link to share packages and services from a source framework to
	 * a target framework.
	 * <p>
	 * The returned framework link is also registered as a service in both the
	 * source and target frameworks using the <code>BundleContext</code> of the
	 * <code>Framework</code> from each framework.
	 * <p>
	 * A Framework link can only be established between two framework system
	 * bundle instances that are in the {@link Bundle#ACTIVE ACTIVE} state.
	 * Framework links are not automatically recreated when either the source or
	 * target frameworks are restarted.
	 * 
	 * @param source the source framework to share packages and services from.
	 * @param target the target framework to share packages and services to.
	 * @param description describes the packages and services to share from the
	 *        source framework to the target framework.
	 * @return a framework link.
	 * @throws BundleException If the source or target Frameworks are not active
	 *         or if any other error occurred in establishing the link.
	 */
	FrameworkLink newFrameworkLink(Framework source, Framework target, LinkDescription description) throws BundleException;
}
