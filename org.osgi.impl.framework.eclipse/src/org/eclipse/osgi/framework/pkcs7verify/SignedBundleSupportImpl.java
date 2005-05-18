/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.pkcs7verify;

import org.eclipse.osgi.framework.adaptor.core.SignedBundle;
import org.eclipse.osgi.framework.adaptor.core.SignedBundleSupport;

/**
 * Implements SignedBundleSupport for a FrameworkAdapotr 
 */
public class SignedBundleSupportImpl implements SignedBundleSupport {

	public SignedBundle createSignedBundle() {
		return new SignedBundleImpl();
	}

	public boolean matchDNChain(String pattern, String dnChain[]) {
		boolean satisfied = false;
		if (dnChain != null) {
			for (int i = 0; i < dnChain.length; i++)
				if (DNChainMatching.match(dnChain[i], pattern)) {
					satisfied = true;
					break;
				}
		}

		return satisfied;
	}

}
