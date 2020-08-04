/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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
package org.osgi.test.cases.networkadapter.junit;

import java.net.InetAddress;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkAddressTestCase extends DefaultTestBundleControl {
    private NetworkTestProxy testProxy;

    @Override
	protected void setUp() throws Exception {
        this.testProxy = new NetworkTestProxy(new TestStepProxy(getContext()));
    }

    @Override
	protected void tearDown() throws Exception {
        this.testProxy.close();
    }

    /**
     * Tests NetworkAddress of the interface that starts.
     */
    public void testNetworkAddress01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADD01] Add an up network adapter.";
            String[] parameters = new String[]{"up"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            ServiceReference< ? > ref = addressListener.get(0);
            Object type = ref.getProperty(NetworkAddress.NETWORKADAPTER_TYPE);
            Object version = ref.getProperty(NetworkAddress.IPADDRESS_VERSION);
            Object scope = ref.getProperty(NetworkAddress.IPADDRESS_SCOPE);
            Object ipAddress = ref.getProperty(NetworkAddress.IPADDRESS);
            Object subnetmask = ref.getProperty(NetworkAddress.SUBNETMASK_LENGTH);
            Object pid = ref.getProperty(NetworkAddress.NETWORKADAPTER_PID);

            assertTrue("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, version instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, scope instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAddress.IPADDRESS, ipAddress instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAddress.SUBNETMASK_LENGTH, subnetmask instanceof Integer);
            assertTrue("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_PID, pid instanceof String);

            NetworkAddress networkAddress = (NetworkAddress) getContext().getService(addressListener.get(0));
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.NETWORKADAPTER_TYPE, type, networkAddress.getNetworkAdapterType());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS_VERSION, version, networkAddress.getIpAddressVersion());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS_SCOPE, scope, networkAddress.getIpAddressScope());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS, ipAddress, networkAddress.getIpAddress());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS, InetAddress.getByName((String)ipAddress), networkAddress.getInetAddress());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS, ((Integer)subnetmask).intValue(), networkAddress.getSubnetMaskLength());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }
}
