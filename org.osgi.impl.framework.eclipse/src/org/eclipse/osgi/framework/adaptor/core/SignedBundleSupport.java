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

/**
 * Provides the implementation of signed bundle support for the framework adaptor.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1 
 */
public interface SignedBundleSupport {
	/**
	 * Create a new SignedBundle object
	 * @return a new SignedBundle object
	 */
	public SignedBundle createSignedBundle();

	/**
	 * Matches the distinguished name chain against a pattern of a distinguished name chain.
	 * 
	 * @param pattern the pattern of distinguished name (DN) chains to match
	 *        against the dnChain.
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#matchDNChain(String, String[])
	 * @return true if a dnChain matches the pattern. A value of false is returned
	 * if bundle signing is not supported.
	 * @throws IllegalArgumentException
	 */
	public boolean matchDNChain(String pattern, String dnChain[]);
}
