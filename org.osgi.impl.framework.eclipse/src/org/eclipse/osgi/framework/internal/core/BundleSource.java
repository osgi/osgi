/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * BundleSource class to wrap in InputStream.
 *
 * <p>This class implements a URLConnection which
 * wraps an InputStream.
 */
public class BundleSource extends URLConnection {
	private InputStream in;

	protected BundleSource(InputStream in) {
		super(null);
		this.in = in;
	}

	public void connect() throws IOException {
		connected = true;
	}

	public InputStream getInputStream() throws IOException {
		return (in);
	}
}
