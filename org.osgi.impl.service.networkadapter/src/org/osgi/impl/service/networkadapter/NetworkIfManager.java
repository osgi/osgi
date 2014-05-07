/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.impl.service.networkadapter;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.osgi.framework.ServiceRegistration;

class NetworkIfManager {

    private static NetworkIfManager instance = new NetworkIfManager();

    private Map networkAdapterPropMap = new HashMap();

    private Map networkAdapterRegMap = new HashMap();

    private Map networkAddressPropMap = new HashMap();

    private Map networkAddressRegMap = new HashMap();

    private NetworkIfManager() {
    }

    public static NetworkIfManager getInstance() {
        return instance;
    }

    synchronized void close() {

        for (Iterator iterator = networkAddressRegMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ServiceRegistration reg = (ServiceRegistration)entry.getValue();
            reg.unregister();
        }
        networkAddressRegMap.clear();
        networkAddressPropMap.clear();

        for (Iterator iterator = networkAdapterRegMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ServiceRegistration reg = (ServiceRegistration)entry.getValue();
            reg.unregister();
        }
        networkAdapterRegMap.clear();
        networkAdapterPropMap.clear();
    }

    synchronized void putNetworkAdapterProp(String id, Dictionary prop) {
        networkAdapterPropMap.put(id, prop);
    }

    synchronized Dictionary getNetworkAdapterProp(String id) {
        return (Dictionary)networkAdapterPropMap.get(id);
    }

    synchronized Dictionary removeNetworkAdapterProp(String id) {
        return (Dictionary)networkAdapterPropMap.remove(id);
    }


    synchronized void putNetworkAddressProp(String id, Dictionary prop) {
        networkAddressPropMap.put(id, prop);
    }

    synchronized Dictionary getNetworkAddressProp(String id) {
        return (Dictionary)networkAddressPropMap.get(id);
    }

    synchronized Dictionary removeNetworkAddressProp(String id) {
        return (Dictionary)networkAddressPropMap.remove(id);
    }


    synchronized void putNetworkAdapterReg(String id, ServiceRegistration reg) {
        networkAdapterRegMap.put(id, reg);
    }

    synchronized ServiceRegistration getNetworkAdapterReg(String id) {
        return (ServiceRegistration)networkAdapterRegMap.get(id);
    }

    synchronized ServiceRegistration removeNetworkAdapterReg(String id) {
        return (ServiceRegistration)networkAdapterRegMap.remove(id);
    }


    synchronized void putNetworkAddressReg(String id, ServiceRegistration reg) {
        networkAddressRegMap.put(id, reg);
    }

    synchronized ServiceRegistration getNetworkAddressReg(String id) {
        return (ServiceRegistration)networkAddressRegMap.get(id);
    }

    synchronized ServiceRegistration removeNetworkAddressReg(String id) {
        return (ServiceRegistration)networkAddressRegMap.remove(id);
    }
}
