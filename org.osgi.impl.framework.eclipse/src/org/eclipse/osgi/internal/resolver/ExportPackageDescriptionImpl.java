/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.internal.resolver;

import java.util.*;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;

public class ExportPackageDescriptionImpl extends BaseDescriptionImpl implements ExportPackageDescription {
	private String[] uses;
	private Map attributes;
	private BundleDescription exporter;
	private String exclude;
	private String include;
	private String[] friends;
	private String[] mandatory;
	private boolean root;
	private int tableIndex;

	public Map getDirectives() {
		Map result =  new HashMap (5);
		if (uses != null)
			result.put(Constants.USES_DIRECTIVE, uses);
		if (exclude != null)
			result.put(Constants.EXCLUDE_DIRECTIVE, exclude);
		if (include != null)
			result.put(Constants.INCLUDE_DIRECTIVE, include);
		if (mandatory != null)
			result.put(Constants.MANDATORY_DIRECTIVE, mandatory);
		if (friends != null)
			result.put(Constants.FRIENDS_DIRECTIVE, friends);
		return result;
	}
	
	public Object getDirective(String key) {
		if (key.equals(Constants.USES_DIRECTIVE))
			return uses;
		if (key.equals(Constants.EXCLUDE_DIRECTIVE))
			return exclude;
		if (key.equals(Constants.INCLUDE_DIRECTIVE))
			return include;
		if (key.equals(Constants.MANDATORY_DIRECTIVE))
			return mandatory;
		if (key.equals(Constants.FRIENDS_DIRECTIVE))
			return friends;
		if (key.equals(Constants.INTERNAL_DIRECTIVE))
			return Boolean.FALSE;
		return null;
	}

	public Object setDirective(String key, Object value) {
		if (key.equals(Constants.USES_DIRECTIVE))
			return uses = (String[])value;
		if (key.equals(Constants.EXCLUDE_DIRECTIVE))
			return exclude = (String) value;
		if (key.equals(Constants.INCLUDE_DIRECTIVE))
			return include = (String) value;
		if (key.equals(Constants.MANDATORY_DIRECTIVE))
			return mandatory = (String[]) value;
		if (key.equals(Constants.FRIENDS_DIRECTIVE))
			return friends = (String[]) value;
		if (key.equals(Constants.INTERNAL_DIRECTIVE))
			return Boolean.FALSE;
		return null;
	}

	public void setDirectives(Map directives) {
		if (directives == null)
			return;
		uses = (String[])directives.get(Constants.USES_DIRECTIVE);
		exclude = (String)directives.get(Constants.EXCLUDE_DIRECTIVE);
		include = (String)directives.get(Constants.INCLUDE_DIRECTIVE);
		mandatory = (String[])directives.get(Constants.MANDATORY_DIRECTIVE);
		friends = (String[])directives.get(Constants.FRIENDS_DIRECTIVE);
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

	protected void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	protected void setExporter(BundleDescription exporter) {
		this.exporter = exporter;
	}

	protected void setRoot(boolean root) {
		this.root = root;
	}

	public String toString() {
		return "Export-Package: " + getName() + "; version=\"" + getVersion() + "\"";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	int getTableIndex() {
		return tableIndex;
	}

	void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}
}
