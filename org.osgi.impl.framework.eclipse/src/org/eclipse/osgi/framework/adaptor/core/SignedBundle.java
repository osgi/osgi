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

/**
 * The SignedBundle class is used to support bundle signing.  Every SignedBundle wraps a
 * BundleFile object.  A SignedBundle uses the wrapped BundleFile to extract signitures
 * and digents from and validates input streams for the wrapped BundleFile.
 * <p>
 * Clients may extend this class.
 * </p>
 * @since 3.1 
 */
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
	 * Matches the distinguished name chains of a bundle's signers against a 
	 * pattern of a distinguished name chain.
	 * 
	 * @param pattern the pattern of distinguished name (DN) chains to match
	 *        against the dnChain. Wildcards "*" can be used in three cases:
	 *        <ol>
	 *        <li>As a DN. In this case, the DN will consist of just the "*".
	 *        It will match zero or more DNs. For example, "cn=me,c=US;*;cn=you"
	 *        will match "cn=me,c=US";cn=you" and
	 *        "cn=me,c=US;cn=her,c=CA;cn=you".
	 *        <li>As a DN prefix. In this case, the DN must start with "*,".
	 *        The wild card will match zero or more RDNs at the start of a DN.
	 *        For example, "*,cn=me,c=US;cn=you" will match "cn=me,c=US";cn=you"
	 *        and "ou=my org unit,o=my org,cn=me,c=US;cn=you"</li>
	 *        <li>As a value. In this case the value of a name value pair in an
	 *        RDN will be a "*". The wildcard will match any value for the given
	 *        name. For example, "cn=*,c=US;cn=you" will match
	 *        "cn=me,c=US";cn=you" and "cn=her,c=US;cn=you", but it will not
	 *        match "ou=my org unit,c=US;cn=you". If the wildcard does not occur
	 *        by itself in the value, it will not be used as a wildcard. In
	 *        other words, "cn=m*,c=US;cn=you" represents the common name of
	 *        "m*" not any common name starting with "m".</li>
	 *        </ol>
	 * @return true if a dnChain matches the pattern. A value of false is returned
	 * if bundle signing is not supported.
	 * @throws IllegalArgumentException
	 */
	public abstract boolean matchDNChain(String pattern);
}
