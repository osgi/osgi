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

package org.eclipse.osgi.module;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Dictionary;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.util.Headers;
import org.osgi.framework.BundleException;

public class BundleManifest {

	private Dictionary manifestDictionary;

	public BundleManifest(String manifest) throws BundleException {
		
		InputStream manifestInputStream = new ByteArrayInputStream(manifest.getBytes());
		manifestDictionary = Headers.parseManifest(manifestInputStream);
	}

	public Dictionary getManifest() throws BundleException {
		return manifestDictionary;
	}
	
	public String getBundleSymbolicName() {
		return (String)manifestDictionary.get(Constants.BUNDLE_SYMBOLICNAME);
	}
}
