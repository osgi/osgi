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
 * This class represents a specific version of an exported package in the system.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface ExportPackageDescription extends BaseDescription {

	/**
	 * Returns true if the export package is a root package; false otherwise.
	 * A ExportPackageDescription is not a root package the exporting bundle
	 * is re-exporting the package using the Reexport-Package header.
	 * @return true if the export package is a root package; false otherwise
	 */
	public boolean isRoot();

	/**
	 * Returns the arbitrary attributes for this package.
	 * @return the arbitrary attributes for this package
	 */
	public Map getAttributes();

	/**
	 * Returns the directives for this package.
	 * @return the directives for this package
	 */
	public Map getDirectives();

	/**
	 * Returns the specified directive for this package.
	 * @param key the directive to fetch
	 * @return the specified directive for this package
	 */
	public Object getDirective(String key);

	/**
	 * Returns the exporter of this package. 
	 * @return the exporter of this package.
	 */
	public BundleDescription getExporter();
}
