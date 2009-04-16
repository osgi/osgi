/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
