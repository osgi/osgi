/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * InputStream subclass which provides a reference (via URL) to the data
 * rather than allowing the input stream to be directly read.
 */
public class ReferenceInputStream extends InputStream {
	protected URL reference;

	public ReferenceInputStream(URL reference) {
		this.reference = reference;
	}

	/* This method should not be called.
	 */
	public int read() throws IOException {
		throw new IOException();
	}

	public URL getReference() {
		return reference;
	}
}
