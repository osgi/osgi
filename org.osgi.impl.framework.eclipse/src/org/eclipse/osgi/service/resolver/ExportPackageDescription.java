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

public interface ExportPackageDescription extends BaseDescription {
	/**
	 * Returns the list of package names which this export uses.
	 * @return the list of package names which this export uses.
	 */
	public String[] getUses();

	/**
	 * Returns the list of classes and resources that are included in the export package
	 * @return the list of classes and resources that are included in the export package
	 */
	public String getInclude();

	/**
	 * Returns the list of classes and resources that are excluded from the export package
	 * @return the list of classes and resources that are excluded fromt he export package
	 */
	public String getExclude();

	/**
	 * Returns true if the export package is a root package; false otherwise.
	 * A ExportPackageDescription is not a root package the exporting bundle
	 * is re-exporting the package using the Reexport-Package header.
	 * @return true if the export package is a root package; false otherwise
	 */
	public boolean isRoot();

	/**
	 * Returns the list of mandatory matching attributes that must be used when importing
	 * this exported package.  If there are no mandatory attributes then a <code>null</code>
	 * is returned.
	 * @return the list of mandatory matching attributes that must be used when importing
	 * this exported package or <code>null</code> if there are no mandatory attributes
	 */
	public String[] getMandatory();

	/**
	 * Returns the arbitrary attributes for this package.
	 * @return the arbitrary attributes for this package
	 */
	public Map getAttributes();

	/**
	 * Returns the exporter of this package. 
	 * @return the exporter of this package.
	 */
	public BundleDescription getExporter();
}
