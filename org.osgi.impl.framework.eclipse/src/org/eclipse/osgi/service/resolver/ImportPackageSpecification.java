/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.service.resolver;

import java.util.Map;

/**
 * A representation of one package import constraint as seen in a 
 * bundle manifest and managed by a state and resolver.
 */
public interface ImportPackageSpecification extends VersionConstraint {
	public static final int RESOLUTION_STATIC   = 0x01;
	public static final int RESOLUTION_OPTIONAL = 0x02;
	public static final int RESOLUTION_DYNAMIC  = 0x04;

	/**
	 * @return
	 * @deprecated no replacement
	 */
	// TODO remove this
	public String[] getPropagate();

	/**
	 * Returns the resolution type for this import package.  Valid values are
	 * {@link ImportPackageSpecification#RESOLUTION_STATIC}, 
	 * {@link ImportPackageSpecification#RESOLUTION_OPTIONAL}, and
	 * {@link ImportPackageSpecification#RESOLUTION_DYNAMIC}.
	 * @return the resolution type for this import package.
	 */
	public int getResolution();

	/**
	 * Returns the symbolic name of the bundle this import package must be resolved to.
	 * @return the symbolic name of the bundle this import pacakge must be resolved to.
	 * A value of <code>null</code> indicates any symbolic name.
	 */
	public String getBundleSymbolicName();

	/**
	 * Returns the version range which this import package may be resolved to. 
	 * @return the version range which this import package may be resolved to.
	 */
	public VersionRange getBundleVersionRange();

	/**
	 * Returns the arbitrary attributes which this import package may be resolved to.
	 * @return the arbitrary attributes which this import package may be resolved to.
	 */
	public Map getAttributes();
}
