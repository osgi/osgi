/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
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
package org.osgi.test.cases.blueprint.framework;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.ConfigurationManager;
import org.osgi.test.cases.blueprint.services.ManagedConfigurationInterface;

public class AdminPropertiesAdder implements TestInitializer, TestEventListener {

    BundleContext testContext;
    String configurationManagerServiceInterface;
    Dictionary newProps;
    String factoryPid;

    public AdminPropertiesAdder(BundleContext testContext, String configurationManagerServiceInterface, String factoryPid, Dictionary newProps) {
        this.testContext = testContext;
        this.configurationManagerServiceInterface = configurationManagerServiceInterface;
        this.factoryPid = factoryPid;
        this.newProps = newProps;
    }

    private void add() throws Exception {
        try{
            ConfigurationManager configurationManager = (ConfigurationManager) this.retrieveService(testContext, configurationManagerServiceInterface);
            ManagedConfigurationInterface mci = configurationManager.getConfig(factoryPid);
            mci.add(configurationManager.getConfigurationAdmin(), newProps);
        } catch (Exception e) {
            System.out.println("Unexpected exception" + e);
            e.printStackTrace();
        }
    }

    public void start(BundleContext testContext) throws Exception {
        this.add();
    }

    public void eventNotReceived(TestEvent expected) throws Exception {
        this.add();
    }

    public void eventReceived(TestEvent expected, TestEvent received) {
        // NOP
    }

    public TestEvent validateEvent(TestEvent expected, TestEvent received) {
        // NOP
        return null;
    }

    private Object retrieveService(BundleContext testContext, String serviceInterface) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(serviceInterface, null);
        return testContext.getService(refs[0]);
    }

}
