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
	public static final String RESOLUTION_STATIC = "static"; //$NON-NLS-1$
	public static final String RESOLUTION_OPTIONAL = "optional"; //$NON-NLS-1$
	public static final String RESOLUTION_DYNAMIC  = "dynamic"; //$NON-NLS-1$

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

	/**
	 * Returns the directives that control this import package.
	 * @return the directives that control this import package.
	 */
	public Map getDirectives();

	/**
	 * Returns the specified directive that control this import package.
	 * @return the specified directive that control this import package.
	 */
	public Object getDirective(String key);
}
