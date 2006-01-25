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
 * Date          Author(s)
 * CR            Headline
 * ===========   ==============================================================
 * Jun 10, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.util;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;

/**
 * @author Andre Assad
 * 
 * Helper interface to test session methods and use cases
 *
 */
public interface TestingSessionResourceProcessor extends ResourceProcessor {
    
	// TYPES
    public static final int PROCESS = 0;
    public static final int DROPPED = 1;
    public static final int DROP_ALL_RESOURCES = 2;
    public static final int PREPARE = 3;
    public static final int COMMIT = 4;
    public static final int ROLL_BACK = 5;
    public static final int CANCEL = 6;
    public static final int START = 7;
    //--
    public static final int ORDER_OF_UNINSTALLING = 8;
    
    public File getDataFile(Bundle bundle);
	public DeploymentPackage getTargetDeploymentPackage();
	public DeploymentPackage getSourceDeploymentPackage();
    public String getResourceName();
    public String getResourceString();
    
    public boolean isProcessed();
    public boolean isDropped();
    public boolean isDroppedAllResources();
    public boolean isPrepared();
    public boolean isCommited();
    public boolean isRolledback();
    public boolean isCancelled();
    public boolean isStarted();
    public void reset();
    
    public void setException(int type);
    public void setTest(int type);
    
    public void waitForRelease(boolean waitAgain);
    public void setReleased(boolean release);
    public boolean isReleased();
    
    public String[] getResourcesDropped();
    
    public boolean uninstallOrdered();
}
