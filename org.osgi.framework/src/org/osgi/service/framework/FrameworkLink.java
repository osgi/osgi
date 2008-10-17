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
import org.osgi.framework.BundleException;

/**
 * A link between two frameworks. A framework link shares packages and services
 * from a source framework to a target framework.
 * <p>
 * When a link is established between two frameworks then:
 * <ul>
 * <li>This <code>FrameworkLink</code> is registered as a service by the system
 * bundles in both the source and target frameworks.</li>
 * <li>A source bundle is installed into the source framework which imports the
 * packages from the source framework as specified by the link description. The
 * source bundle is also used to acquire services from the source framework as
 * specified by the link description.</li>
 * <li>A target bundle is installed which is used to export packages which the
 * source bundle gets resolved to. The target bundle is also used to register
 * services acquired by the source framework as specified by the link
 * description.</li>
 * </ul>
 */
//TODO javadoc needs review
public interface FrameworkLink {
	/**
	 * Service registration property (named
	 * <code>org.osgi.service.framework.linkID</code>) specifying the link id
	 * for a registered framework link. <code>FrameworkLink</code> services must
	 * be registered with this property. The value of this property is a unique
	 * <code>String</code> identifying the link.
	 */
	public static String ID = "org.osgi.service.framework.linkID";

	/**
	 * Returns the link description that specifies the packages and services
	 * which are shared over the framework link.
	 * 
	 * @return the link description
	 */
	public LinkDescription getLinkDescription();

	/**
	 * Updates this framework link to share the packages and services specified
	 * by the link description
	 * 
	 * @param description
	 *            the link description
	 * @throws BundleException
	 *             if an error occurred while updating the link
	 */
	public void update(LinkDescription description) throws BundleException;

	/**
	 * Returns the bundle representing the packages and services imported from
	 * the source framework.
	 * <p>
	 * The following lifecycle operations on the source bundle effect this
	 * framework link:
	 * <ul>
	 * <li>{@link Bundle#start() start}</li> - starts the source and target
	 * bundles and starts sharing available services in the source framework
	 * that match the link description with the target framework.
	 * <li>{@link Bundle#stop() stop} - stops the source and target bundles and
	 * stops sharing services with the target framework.</li>
	 * <li>{@link Bundle#uninstall() uninstall} - uninstalls the source and
	 * target bundles and breaks this framework link.</li>
	 * <li>{@link Bundle#update() update} - updates the source bundle and
	 * refreshes the target bundle.</li>
	 * <li>{@link Bundle#update(java.io.InputStream) update(InputStream)} -
	 * closes and ignores the input stream and does an {@link Bundle#update()
	 * update} operation
	 * </ul>
	 * The following source bundle states effect this framework link:
	 * <ul>
	 * <li>{@link Bundle#INSTALLED INSTALLED} - the target bundle must also be
	 * enter the {@link Bundle#INSTALLED INSTALLED} state. No packages or
	 * services are shared with the target framework.</li>
	 * <li>{@link Bundle#RESOLVED RESOLVED} - the target bundle must also enter
	 * the {@link Bundle#RESOLVED RESOLVED} state. Packages from the source
	 * framework are now shared with the target framework</li>
	 * <li>{@link Bundle#UNINSTALLED UNINSTALLED} - the target bundle must also
	 * enter the {@link Bundle#UNINSTALLED UNINSTALLED} state and breaks this
	 * framework link.</li>
	 * </ul>
	 * 
	 * @return the source bundle.
	 */
	public Bundle getSource();

	/**
	 * Returns the bundle representing packages and services exported to the
	 * target framework.
	 * <p>
	 * The following lifecycle operations on the target bundle effect this
	 * framework link:
	 * <ul>
	 * <li>{@link Bundle#start() start}</li> - starts the target bundle. If the
	 * source bundle is active then available services in the source framework
	 * that match the link description are shared with the target framework.
	 * <li>{@link Bundle#stop() stop} - stops the target bundle and stops
	 * sharing services with the target framework.</li>
	 * <li>{@link Bundle#uninstall() uninstall} - uninstalls the source and
	 * target bundle and breaks this framework link.</li>
	 * <li>{@link Bundle#update() update} - updates the target bundle.</li>
	 * <li>{@link Bundle#update(java.io.InputStream) update(InputStream)} -
	 * closes and ignores the input stream and does an {@link Bundle#update()
	 * update} operation
	 * </ul>
	 * The following target bundle states effect this framework link:
	 * <ul>
	 * <li>{@link Bundle#INSTALLED INSTALLED} - the target bundle should only be
	 * enter and stay in the {@link Bundle#INSTALLED INSTALLED} state if the
	 * source bundle is in the {@link Bundle#INSTALLED INSTALLED} state. No
	 * packages or services are shared with the target framework.</li>
	 * <li>{@link Bundle#RESOLVED RESOLVED} - the target bundle must only enter
	 * the {@link Bundle#RESOLVED RESOLVED} state if the source bundle is
	 * {@link Bundle#RESOLVED RESOLVED}, {@link Bundle#STARTING STARTING},
	 * {@link Bundle#ACTIVE ACTIVE}, or {@link Bundle#STOPPING STOPPING} states.
	 * Packages from the source framework are now shared with the target
	 * framework.</li>
	 * <li>{@link Bundle#UNINSTALLED UNINSTALLED} - the source bundle must also
	 * enter the {@link Bundle#UNINSTALLED UNINSTALLED} state and breaks this
	 * framework link.</li>
	 * </ul>
	 * 
	 * @return the target bundle.
	 */
	public Bundle getTarget();

	/**
	 * Returns a unique link id which is given to the link when it is created.
	 * 
	 * @return the unique link id.
	 */
	public String getLinkID();
}
