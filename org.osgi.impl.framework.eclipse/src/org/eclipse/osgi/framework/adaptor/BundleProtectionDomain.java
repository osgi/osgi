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

package org.eclipse.osgi.framework.adaptor;

import java.io.File;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;

/**
 * 
 * This is a specialized ProtectionDomain that also has information about the
 * signers and the hash of the bundle.
 */
public abstract class BundleProtectionDomain extends ProtectionDomain {

	/**
	 * Constructs a special ProtectionDomain that allows access to signature and
	 * digest information.
	 * 
	 * @param permCollection
	 *            the PermissionCollection for the Bundle
	 */
	public BundleProtectionDomain(PermissionCollection permCollection) {
		super(null, permCollection);
	}

	/**
	 * Loads a list of signed files into this <tt>BundleProtectionDomain</tt>.
	 * 
	 * @param signedFiles
	 *            the list of signed files to load. The first member of the list
	 *            is the signed file that contains the Manifest of the bundle.
	 *            Any additional files represent embedded Jar files.
	 */
	public abstract void loadFiles(File[] signedFiles);

	/**
	 * Gets the signers of associated with this ProtectionDomain.
	 * 
	 * @return array of signers associated with this ProtectionDomain. Each
	 *         signer is encoded as a semi-colon separated list of the
	 *         distinguished names that make up the certificate chain of the
	 *         signer. Each distinguished is encoded according to IETF RFC 2253.
	 *         The last distinguished name in each semi-colon separated list is
	 *         the root of the certificate chain. This method returns null if
	 *         loadFiles has not yet been called. An empty array is returned if
	 *         the files are not signed or none of the signatures could be
	 *         validated.
	 */
	public abstract String[] getSigners();

	/**
	 * Checks the digest of a named class or resource that is loaded from a
	 * signed file.
	 * 
	 * @param signedFile
	 *            the file from which the class or resource is loaded.
	 * @param name
	 *            the name of the class or resource which is loaded.
	 * @return true if the digest checks out, false if the check fails.
	 */
	public abstract boolean checkDigest(File signedFile, String name);
}
