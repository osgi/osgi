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

import org.eclipse.osgi.service.resolver.BaseDescription;
import org.osgi.framework.Version;

public class BaseDescriptionImpl implements BaseDescription {
	private String name;
	private Version version;

	public String getName() {
		return name;
	}

	public Version getVersion() {
		if (version == null)
			return Version.emptyVersion;
		return version;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setVersion(Version version) {
		this.version = version;
	}

}
