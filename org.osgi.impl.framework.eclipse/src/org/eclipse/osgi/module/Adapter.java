/********************************************************************************
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
import java.util.Hashtable;

import org.eclipse.osgi.framework.adaptor.core.AdaptorElementFactory;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;

/**
 * 
 */
public class Adapter extends DefaultAdaptor {

	protected Hashtable manifests = new Hashtable();

	Adapter(String[] args) {
		super(args);
	}
	
	public AdaptorElementFactory getElementFactory() {
		if (elementFactory == null)
			elementFactory = new ElementFactory();
		return elementFactory;
	}

	Dictionary getManifest(String location) {
		return (Dictionary) manifests.get(location);
	}

	void setManifest(String location, Dictionary manifest) {
		manifests.put(location, manifest);
	}
}
