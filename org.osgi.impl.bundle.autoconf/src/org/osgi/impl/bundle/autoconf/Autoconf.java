/*
 * $Header$
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

package org.osgi.impl.bundle.autoconf;

import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class Autoconf implements ResourceProcessor {
	/**
	 * @param rp
	 * @param operation
	 * @see org.osgi.service.deploymentadmin.ResourceProcessor#begin(org.osgi.service.deploymentadmin.DeploymentPackage, int)
	 */
	public void begin(DeploymentPackage rp, int operation) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param commit
	 * @see org.osgi.service.deploymentadmin.ResourceProcessor#complete(boolean)
	 */
	public void complete(boolean commit) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param name
	 * @throws java.lang.Exception
	 * @see org.osgi.service.deploymentadmin.ResourceProcessor#process(java.lang.String)
	 */
	public void process(String name) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @param name
	 * @throws java.lang.Exception
	 * @see org.osgi.service.deploymentadmin.ResourceProcessor#dropped(java.lang.String)
	 */
	public void dropped(String name) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @see org.osgi.service.deploymentadmin.ResourceProcessor#dropped()
	 */
	public void dropped() {
		// TODO Auto-generated method stub
	}
}
