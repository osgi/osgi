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

package org.eclipse.osgi.framework.internal.core;

import java.io.File;
import java.security.PermissionCollection;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;

/**
 *
 * This class tracks the signature information for a bundle in addition to
 * managing bundle permissions.
 */
public class BundleProtectionDomainImpl extends BundleProtectionDomain {
	AbstractBundle bundle;
	String signers[];

	/**
	 * @param bundle the bundle to which this ProtectionDomain corresponds.
	 * @param permCollection the PermissionCollection used by this ProtectionDomain.
	 */
	public BundleProtectionDomainImpl(AbstractBundle bundle, PermissionCollection permCollection) {
		super(permCollection);
		this.bundle = bundle;
	}

	/**
	 * This method sets the files that are used by the classloader for the bundle to
	 * which this ProtectionDomain corresponds. It must be called before any signature
	 * or digest information is used since that information is pulled from the passed
	 * file.
	 * 
	 * @param signedFiles the list of signed files to load. The first member
	 * of the list is the signed file that contains the Manifest of the
	 * bundle. Any additional files represent embedded Jar files.
	 * @see org.eclipse.osgi.framework.adaptor.BundleProtectionDomain#loadFiles(java.io.File[])
	 */
	public void loadFiles(File[] signedFiles) {
		// TODO Add in code from Roy for getting signatures and digests
	}

	/**
	 * Gets the signers of the Manifest that were collected from loadFiles().
	 * 
	 * @return array of signers associated with this ProtectionDomain. Each signer
	 * is encoded as a semi-colon separated list of the distinguished names that
	 * make up the certificate chain of the signer. Each distinguished is encoded
	 * according to IETF RFC 2253. The last distinguished name in each semi-colon
	 * separated list is the root of the certificate chain.An empty array is returned if
	 * the files are not signed or none of the signatures could be validated.
	 * 
	 * @see org.eclipse.osgi.framework.adaptor.BundleProtectionDomain#getSigners()
	 */
	public String[] getSigners() {
		return signers;
	}

	/**
	 * @param signedFile
	 * @param name
	 * @return
	 * @see org.eclipse.osgi.framework.adaptor.BundleProtectionDomain#checkDigest(java.io.File, java.lang.String)
	 */
	public boolean checkDigest(File signedFile, String name) {
		// TODO Add in code from Roy for checking digests
		return true;
	}
}
