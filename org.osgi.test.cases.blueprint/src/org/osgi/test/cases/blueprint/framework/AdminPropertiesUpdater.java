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

public class AdminPropertiesUpdater implements TestEventListener{

    BundleContext testContext;
    String configurationManagerServiceInterface;
    String id;
    Dictionary newDic;

    public AdminPropertiesUpdater(BundleContext testContext, String configurationManagerServiceInterface, String targetId, Dictionary newDic){
        this.testContext = testContext;
        this.configurationManagerServiceInterface = configurationManagerServiceInterface;
        this.id = targetId;
        this.newDic = newDic;
    }

    public void eventReceived(TestEvent expected, TestEvent received) {
        try {
            ConfigurationManager configurationManager = (ConfigurationManager) this.retrieveService(testContext, configurationManagerServiceInterface);
            ManagedConfigurationInterface mci= configurationManager.getConfig(id);
            mci.update(configurationManager.getConfigurationAdmin(), this.newDic);
        } catch (Exception e) {
            System.out.println("Unexpected exception" + e);
            e.printStackTrace();
        }
    }

    public void eventNotReceived(TestEvent expected) throws Exception {
        // this is a NOP event
    }

    public TestEvent validateEvent(TestEvent expected, TestEvent received) {
        // this is a NOP event
        return null;
    }

    private Object retrieveService(BundleContext testContext, String serviceInterface) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(serviceInterface, null);
        return testContext.getService(refs[0]);
    }

}
