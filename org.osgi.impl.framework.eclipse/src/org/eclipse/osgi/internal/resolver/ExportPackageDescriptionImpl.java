/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.internal.resolver;

import java.util.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;

public class ExportPackageDescriptionImpl extends BaseDescriptionImpl implements ExportPackageDescription {
	private String grouping;
	private Map attributes;
	private BundleDescription exporter;
	private String exclude;
	private String include;
	private String[] mandatory;
	private boolean root;

	public String getGrouping() {
		if (grouping == null)
			return getName(); // default is to have a unique grouping using the package name
		return grouping;
	}

	protected String getBasicGrouping() {
		return grouping;
	}

	public String getInclude() {
		return include;
	}

	public String getExclude() {
		return exclude;
	}

	public Map getAttributes() {
		return attributes;
	}

	public BundleDescription getExporter() {
		return exporter;
	}

	public boolean isRoot() {
		return root;
	}

	public String[] getMandatory() {
		return mandatory;
	}

	protected void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	protected void setInclude(String include) {
		this.include = include;
	}

	protected void setExclude(String exclude) {
		this.exclude = exclude;
	}

	protected void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	protected void setExporter(BundleDescription exporter) {
		this.exporter = exporter;
	}

	protected void setRoot(boolean root) {
		this.root = root;
	}

	protected void setMandatory(String[] mandatory) {
		this.mandatory = mandatory;
	}

	public String toString() {
		return "Export-Package: " + getName() + "; version=\"" + getVersion() + "\"";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}
}
