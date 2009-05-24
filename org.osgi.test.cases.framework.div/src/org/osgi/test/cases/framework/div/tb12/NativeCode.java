/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.framework.div.tb12;

import org.osgi.framework.*;

/**
 * Bundle for the NativeCode optional clause test. This bundle has an optional
 * clause present to make sure it will be loaded even if no other native code
 * clause matches. The clauses were built to intentionally NOT match in order to
 * check if the bundle is loaded.
 * 
 * @author Jorge Mascena
 */
public class NativeCode implements BundleActivator {
	/**
	 * Starts the bundle. Excercises the native code. The
	 * <CODE>org.osgi.test.cases.div.tb2.NativeCode.test()</CODE> call
	 * should throw a BundleException since no native code clause should match.
	 *  
	 * @param bc the context where the bundle is executed.
	 */
	public void start(BundleContext bc) throws BundleException {
		boolean clauseMatches;
		try {
			org.osgi.test.cases.div.tb2.NativeCode.test();
			// if started ok, then there was a match
			clauseMatches = true;
		}
		catch (BundleException e) {
			clauseMatches = false;
		}
		if(clauseMatches) {
			// there should be no match
			throw new BundleException("No native code clause should match");
		}
	}

	/**
	 * Stops the bundle.
	 * 
	 * @param bc the context where the bundle is executed.
	 */
	public void stop(BundleContext bc) {
	}
}
