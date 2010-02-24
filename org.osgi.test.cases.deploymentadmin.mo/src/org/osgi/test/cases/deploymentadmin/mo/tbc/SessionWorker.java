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
 * ============  ==============================================================
 * Oct 20, 2005  Andre Assad
 * 97            Implement MEGTCK for the deployment MO Spec
 * ============  ============================================================== 
 */

package org.osgi.test.cases.deploymentadmin.mo.tbc;
import java.net.URL;

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDeploymentPackage;
import org.osgi.test.support.OSGiTestCase;

public class SessionWorker extends Thread {
        
        private TestingDeploymentPackage testDP;
        private DeploymentmoTestControl tbc;
        
        public SessionWorker(DeploymentmoTestControl tbc, TestingDeploymentPackage testDP) {
            this.testDP = testDP;
            this.tbc = tbc;
        }
        
        public void run() {
            DeploymentPackage dp = null;
            URL url = null;
            try {
                url = new URL(DeploymentmoConstants.SERVER + "www/" + testDP.getFilename());
                dp = tbc.getDeploymentAdmin().installDeploymentPackage(url.openStream());
            } catch (Exception e) {
			OSGiTestCase.fail(e.getMessage(), e);
            } finally {
                try {
                    dp.uninstall();
                } catch (DeploymentException e1) {
                    try {
						dp.uninstallForced();
					} catch (DeploymentException e) {
					OSGiTestCase.fail(e.getMessage(), e);
					}
                }
            }
        }
    }