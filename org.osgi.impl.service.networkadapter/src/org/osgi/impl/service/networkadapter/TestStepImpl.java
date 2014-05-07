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

import org.osgi.test.cases.step.TestStep;

public class TestStepImpl implements TestStep {

    public String[] execute(String command, String[] parameters) {

        try {
            if (Commands.ADD_NETWORK_ADAPTER.equals(command)) {
                return addNetworkAdapter(parameters);

            } else if (Commands.ADD_NETWORK_ADDRESS.equals(command)) {
                return addNetworkAddress(parameters);

            } else if (Commands.MODIFY_NETWORK_ADDRESS.equals(command)) {
                modifyNetworkAddress(parameters);

            } else if (Commands.REMOVE_NETWORK_ADAPTER.equals(command)) {
                removeNetworkAdapter(parameters);

            } else if (Commands.REMOVE_NETWORK_ADDRESS.equals(command)) {
                removeNetworkAddress(parameters);

            } else {
                throw new IllegalArgumentException("The user message is not supported.");
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)e;
            }

            throw new IllegalArgumentException(e.getMessage());
        }

        return null;
    }

    private String[] addNetworkAdapter(String[] parameters) {

        String networkAdapterId = NetworkIfUtil.getNetworkAdapterId(parameters[2], parameters[3]);

        NetworkIfData networkIfData = new NetworkIfData(parameters);
        String addressVersion = parameters[12];
        String addressScope = parameters[13];
        String address = parameters[14];
        int length = -1;
        if (parameters[15] != null) {
            length = Integer.parseInt(parameters[15]);
        }

        String networkAddressId = null;
        if (addressVersion != null && addressScope != null &&  address != null || length != -1) {
            networkAddressId = NetworkIfUtil.getNetworkAddressId(networkAdapterId);
        }

        NetworkIfTracker.getInstance().addNetworkAdapter(networkAdapterId, networkIfData, networkAddressId, addressVersion, addressScope, address, length);

        return new String[]{networkAdapterId, networkAddressId};
    }

    private String[] addNetworkAddress(String[] parameters) {

        String networkAdapterId = parameters[0];
        String networkAddressId = NetworkIfUtil.getNetworkAddressId(networkAdapterId);

        String addressVersion = parameters[1];
        String addressScope = parameters[2];
        String address = parameters[3];
        int length = Integer.parseInt(parameters[4]);

        NetworkIfTracker.getInstance().addNetworkAddress(networkAdapterId, networkAddressId, addressVersion, addressScope, address, length);

        return new String[]{networkAddressId};
    }

    private void modifyNetworkAddress(String[] parameters) {

        String networkAddressId = parameters[0];
        String addressVersion = parameters[1];
        String addressScope = parameters[2];
        String address = parameters[3];
        int length = Integer.parseInt(parameters[4]);

        NetworkIfTracker.getInstance().modifiedNetworkAddress(networkAddressId, addressVersion, addressScope, address, length);
    }

    private void removeNetworkAdapter(String[] parameters) {

        String networkAdapterId = parameters[0];

        NetworkIfTracker.getInstance().removedNetworkAdapter(networkAdapterId);
    }

    private void removeNetworkAddress(String[] parameters) {

        String networkAddressId = parameters[0];

        NetworkIfTracker.getInstance().removedNetworkAddress(networkAddressId);
    }
}
