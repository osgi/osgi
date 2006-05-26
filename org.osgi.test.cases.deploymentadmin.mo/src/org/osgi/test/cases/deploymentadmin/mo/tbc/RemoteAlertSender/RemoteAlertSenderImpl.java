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
 * Jul 4, 2005  Leonardo Barros
 * 97           Implement MEGTCK for the deployment MO Spec
 * ===========  ==============================================================
 * Jul 6, 2005  Andre assad
 * 97           Implement MEGTCK for the deployment MO Spec
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tbc.RemoteAlertSender;

import info.dmtree.notification.AlertItem;
import info.dmtree.notification.spi.RemoteAlertSender;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;


public class RemoteAlertSenderImpl implements RemoteAlertSender, BundleActivator {
    private ServiceRegistration servReg;
    
    private DeploymentmoTestControl tbc;

    public void sendAlert(String serverId, int code, String correlator,
            AlertItem[] items) throws Exception {
        try {
            tbc.setReceivedAlert(true);
            tbc.setCode(code);
            tbc.assertEquals("Asserts the length of alert item array",1,items.length);
            tbc.setAlert(items[0]);
            synchronized (tbc) {
                tbc.notifyAll();
            }
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }
    
    public RemoteAlertSenderImpl(DeploymentmoTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void start(BundleContext bc) throws Exception {
        Hashtable ht = new Hashtable();
        ht.put("principals", new String[] { DeploymentmoConstants.PRINCIPAL });       

        String[] ifs = new String[] { RemoteAlertSender.class.getName() };
        servReg = bc.registerService(ifs, this, ht);
        System.out.println("Install and Activate RemoteAlertSender activated.");
    }

    public void stop(BundleContext arg0) throws Exception {
        servReg.unregister();
    }

}
