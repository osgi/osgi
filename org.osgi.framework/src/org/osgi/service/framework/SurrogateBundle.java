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

import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * A surrogate bundle represents a composite bundle within a child framework.  
 * A surrogate bundle does the following as specified by the composite manifest:
 * <ul>
 * <li>Exports packages to the child framework from the parent framework. These
 * packages are imported by the composite bundle installed in the parent
 * framework.</li>
 * <li>Imports packages from the child framework. These packages are exported
 * by the composite bundle installed in the parent framework.</li>
 * <li>Registers services with the child framework from the parent framework.
 * These services are acquired by the composite bundle installed in the 
 * parent framework.</li>
 * <li>Acquires services from the child framework.  These services are 
 * registered by the composite bundle installed in the parent framework.</li>
 * </ul>
 * <p>
 * @see CompositeBundle
 */
public interface SurrogateBundle extends Bundle {
	/**
	 * Returns the bundle context of the composite bundle which this
	 * surrogate bundle represents.
	 * @return the bundle context of the composite bundle.  A value 
	 * of <code>null</code> is returned if the composite bundle does 
	 * not have a valid bundle context.
	 */
	BundleContext getCompositeBundleContext();

	/**
	 * This operation is not supported for surrogate bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	void update() throws BundleException;

	/**
	 * This operation is not supported for surrogate bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	void update(InputStream input) throws BundleException;

	/**
	 * This operation is not supported for surrogate bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	void uninstall() throws BundleException;
}
