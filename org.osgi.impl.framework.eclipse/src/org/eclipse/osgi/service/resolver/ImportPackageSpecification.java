/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
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

	public String[] getPropagate();
	public int getResolution();
	public String getBundleSymbolicName();
	public VersionRange getBundleVersionRange();
	public Map getAttributes();
}