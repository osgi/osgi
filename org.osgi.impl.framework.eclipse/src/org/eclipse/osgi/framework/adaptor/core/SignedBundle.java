/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

public abstract class SignedBundle extends BundleFile {
	/**
	 * Sets the BundleFile for this singed bundle. It will extract
	 * signatures and digests from the bundle file and validate input streams
	 * before using them from the bundle file.
	 * 
	 * @param bundleFile the BundleFile to extract elements from.
	 * @throws IOException
	 */
	public abstract void setBundleFile(BundleFile bundleFile) throws IOException;

	/**
	 * Retrieves the certificate chains that signed this repository. Only
	 * validated certificate chains are returned. Each element of the returned
	 * array will contain a chain of distinguished names (DNs) separated by
	 * semicolons. The first DN is the signer and the last is the root
	 * Certificate Authority.
	 * 
	 * @return the certificate chains that signed this repository.
	 */
	public abstract String[] getSigningCertificateChains();
}
