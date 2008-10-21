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
 * <li>A source bundle is installed into the source framework. This bundle does
 * the following:
 * 
 * <ul>
 * <li>Imports the packages from the source framework as specified by the link
 * description.</li>
 * <li>Acquire services from the source framework as specified by the link
 * description.</li>
 * </ul>
 * <li>A target bundle is installed into the target framework. This bundle does
 * the following:
 * <ul>
 * <li>If the source bundle is resolved then the target bundle exports the
 * packages that source bundle is resolved to.</li>
 * <li>If the source bundle is active then the target bundle registers the
 * services acquired by the source bundle as specified by the link description.</li>
 * </ul>
 * <li>The framework should attempt to resolve and start the source bundle.</li>
 * </ul>
 * 
 * <h3>Breaking a framework link</h3>
 * <p>
 * A framework link will break if any of the following operations occur:
 * <ul>
 * <li>The source framework is stopped.</li>
 * <li>The source bundle is uninstalled.</li>
 * <li>The source bundle is updated using the
 * {@link Bundle#update(java.io.InputStream)} method.</li>
 * <li>The target framework is stopped.</li>
 * <li>The target bundle is uninstalled.</li>
 * <li>The target bundle is updated using the
 * {@link Bundle#update(java.io.InputStream)} method.</li>
 * </ul>
 * <p>
 * If a framework link is broken then the following operations must occur:
 * <ul>
 * <li>This framework link is unregistered as a service from the source and
 * target frameworks.</li>
 * <li>The source bundle must be stopped.</li>
 * <li>The target bundle must be stopped.</li>
 * <li>If the source bundle was not updated with the
 * {@link Bundle#update(java.io.InputStream)} method, it must be uninstalled.</li>
 * <li>If the target bundle was not updated with the
 * {@link Bundle#update(java.io.InputStream)} method, it must be uninstalled.</li>
 * <li>The target bundle must be refreshed to ensure other bundles in the target 
 * framework are not importing packages exported by the target bundle.</li>
 * </ul>
 * 
 * <h3>The Source Bundle</h3>
 * <p>
 * Bundle lifecycle operations performed on the source bundle will effect
 * this framework link in the following ways:
 * <ul>
 * <li>{@link Bundle#start() start} - starts the source bundle. The
 * following operations must occur:
 *   <ul>
 *   <li>The source bundle must acquire all available services that are
 * registered in the source framework and that match the link description.
 * See {@link LinkDescription#getServiceFilters()}</li>
 *   <li>The target bundle must start.</li>
 *   </ul>
 * </li>
 * <li>{@link Bundle#stop() stop} - stops the source bundle. The following
 * operations must occur:
 *   <ul>
 *   <li>The target bundle must stop.</li>
 *   <li>The source bundle must release all acquired services.</li>
 *   </ul>
 * </li>
 * <li>{@link Bundle#uninstall() uninstall} - uninstalls the source. This
 * breaks the framework link.</li>
 * <li>{@link Bundle#update() update} - updates the source bundle. The
 * following operations must occur:
 *   <ul>
 *   <li>The content of the source bundle must remain unchanged if the update
 * operation is done outside of a call to the
 * {@link #update(LinkDescription)} method.
 *   <li>The source bundle is stopped, unresolved, and re-activated if it was
 * active at the beginning of the update operation.</li>
 *   </ul>
 * </li>
 * <li>{@link Bundle#update(java.io.InputStream) update(InputStream)} -
 * updates the source bundle with the content specified. This breaks the
 * framework link.</li>
 * </ul> 
 * <p>
 * The following source bundle states effect this framework link:
 * <ul>
 * <li>{@link Bundle#INSTALLED INSTALLED} - the target bundle must also
 * enter the {@link Bundle#INSTALLED INSTALLED} state. No packages or
 * services are shared with the target framework.</li>
 * <li>{@link Bundle#RESOLVED RESOLVED} - the target bundle must also enter
 * the {@link Bundle#RESOLVED RESOLVED} state. Packages from the source
 * framework are now shared with the target framework</li>
 * <li>{@link Bundle#UNINSTALLED UNINSTALLED} - The framework link must be
 * broken</li>
 * </ul>
 *
 * <h3>The Target Bundle</h3>
 * <p>
 * Bundle lifecycle operations performed on the target bundle will effect
 * this framework link in the following ways:
 * <ul>
 * <li>{@link Bundle#start() start}</li> - starts the target bundle. If the
 * source bundle is active then the target bundle must register services in
 * the target framework that are acquired by the source bundle and that
 * match the link description. See
 * {@link LinkDescription#getServiceFilters()}</li>
 * <li>{@link Bundle#stop() stop} - stops the target bundle. The target
 * bundle must unregister all shared services.</li>
 * <li>{@link Bundle#uninstall() uninstall} - uninstalls the target. This
 * breaks the framework link.</li>
 * <li>{@link Bundle#update() update} - updates the target bundle. The
 * following operations must occur:
 *   <ul>
 *   <li>The content of the target bundle must remain unchanged if the update
 * operation is done outside of a call to the
 * {@link #update(LinkDescription)} method.
 *   <li>The target bundle is stopped, unresolved, and re-activated if it was
 * active at the beginning of the update operation.</li>
 *   </ul>
 * </li>
 * <li>{@link Bundle#update(java.io.InputStream) update(InputStream)} -
 * updates the target bundle with the content specified. This breaks the
 * framework link.</li>
 * </ul>
 * <p>
 * The following target bundle states effect this framework link:
 * <ul>
 * <li>{@link Bundle#INSTALLED INSTALLED} - the target bundle should only
 * enter and stay in the {@link Bundle#INSTALLED INSTALLED} state if the
 * source bundle is in the {@link Bundle#INSTALLED INSTALLED} state. No
 * packages or services are shared with the target framework.</li>
 * <li>{@link Bundle#RESOLVED RESOLVED} - the target bundle must only enter
 * the {@link Bundle#RESOLVED RESOLVED} state if the source bundle is in the 
 * {@link Bundle#RESOLVED RESOLVED}, {@link Bundle#STARTING STARTING},
 * {@link Bundle#ACTIVE ACTIVE}, or {@link Bundle#STOPPING STOPPING} state.
 * Packages from the source framework are now shared with the target
 * framework.</li>
 * <li>{@link Bundle#UNINSTALLED UNINSTALLED} - The framework link must be
 * broken.</li>
 * </ul>
 * 
 * @ThreadSafe
 * @version $Revision$
 */
// TODO javadoc needs review
public interface FrameworkLink {
	/**
	 * Service registration property (named
	 * <code>org.osgi.service.framework.linkID</code>) specifying the link id
	 * for a framework link service. <code>FrameworkLink</code> services must be
	 * registered with this property. The value of this property is a unique
	 * <code>String</code> identifying the link.
	 */
	public static String ID = "org.osgi.service.framework.linkID";

	/**
	 * Returns the link description for this framework link. The link
	 * description specifies the packages and services which are shared over
	 * this framework link.
	 * 
	 * @return the link description.
	 */
	public LinkDescription getLinkDescription();

	/**
	 * Updates this framework link with the specified link description. This
	 * framework link's source and target bundles are updated to share the
	 * packages and services specified by the new link description.
	 * <p>
	 * When a framework link is updated the following steps are required to
	 * update this link:
	 * <ol>
	 * <li>The source bundle must be stopped if it is active.</li>
	 * <li>The source bundle must be updated with the correct Import-Package
	 * metadata as specified by the link description. After the update the
	 * source bundle is in the {@link Bundle#UNINSTALLED UNRESOLVED} state and
	 * no packages can be shared over this link. This implies that the target
	 * bundle should be unresolved and must not be exporting any packages.</li>
	 * <li>The framework should attempt to resolve the source bundle. If the
	 * source bundle cannot resolve then the update is complete. Additional
	 * bundles may need to be installed to allow the source bundle to resolve.</li>
	 * <li>When the source bundle is resolved, the target bundle is allowed to
	 * resolve and should export the packages that the source bundle is wired
	 * to. The Export-Package metadata for the target bundle must use the
	 * Export-Package metadata from the packages that the source bundle is
	 * wired to.
	 * <li>If the source bundle is resolved and was active in step 1 then the
	 * source bundle is started.</li>
	 * </ol>
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
	 * 
	 * @return the source bundle.
	 */
	public Bundle getSource();

	/**
	 * Returns the bundle representing the packages and services exported to the
	 * target framework.
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
