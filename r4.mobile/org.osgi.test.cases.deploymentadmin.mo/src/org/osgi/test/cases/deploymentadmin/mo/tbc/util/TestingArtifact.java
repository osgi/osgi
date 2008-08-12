/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Aug 25, 2005 Andre Assad
 * 147          Rework after formal inspection 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tbc.util;


/**
 * @author Andre Assad
 * 
 * Supports download artifacts through Management Objects
 *
 */
public class TestingArtifact {
    
    private TestingDlota dlota;
    private TestingDeploymentPackage dp;
    private TestingBundle bundle;
    private boolean isBundle;
    
    public TestingArtifact(TestingDlota dlota, TestingDeploymentPackage dp) {
        this.dlota = dlota;
        this.dp = dp;
        isBundle=false;
        
    }
    
    public TestingArtifact(TestingDlota dlota, TestingBundle bundle) {
        this.dlota = dlota;
        this.bundle = bundle;
        isBundle=true;
    }
    
    
    /**
     * @return Returns the bundle.
     */
    public TestingBundle getBundle() {
        return bundle;
    }
    /**
     * @return Returns the dlota.
     */
    public TestingDlota getDlota() {
        return dlota;
    }
    /**
     * @return Returns the dp.
     */
    public TestingDeploymentPackage getDeploymentPackage() {
        return dp;
    }

	public boolean isBundle() {
		return isBundle;
	}
}
