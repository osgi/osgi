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

import java.util.Dictionary;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultBundleData;
import org.osgi.framework.BundleException;

public class BundleData extends DefaultBundleData {

	public BundleData(Adapter adapter, long id) {
		super(adapter, id);
	}

	public Dictionary getManifest() throws BundleException {
		if (manifest != null)
			return manifest;
		manifest = ((Adapter)adaptor).getManifest(getLocation());
		if (manifest != null)
			return manifest;
		return super.getManifest();
	}
}
