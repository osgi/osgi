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
package org.osgi.test.cases.networkadapter.junit;

import java.net.InetAddress;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.cases.networkadapter.util.NetworkIfTestUtil;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.cases.step.TestStep;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class NetworkAddressTestCase extends DefaultTestBundleControl {

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Tests NetworkAddress of the interface that starts.
     */
    public void testNetworkAddress01() {

        String[] ids = null;
        TestStep testStep = null;

        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.networkadapter)");

            String command = "addNetworkAdapter";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_UP_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_UP_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_UP_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_UP_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_UP_MTU);
            parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_UP_ISLOOPBACK);
            parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_UP_ISPOINTTOPOINT);
            parameters[7] = "true";
            parameters[8] = "false";
            parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_UP_SUPPORTSMULTICAST);
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_UP_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_UP_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_UP_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_UP_MASKLENGTH);

            ids = testStep.execute(command, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);
            NetworkAddress networkAddress = (NetworkAddress) getContext().getService(addressListener.get(0));

            assertEquals(parameters[0], networkAddress.getNetworkAdapterType());
            assertEquals(parameters[12], networkAddress.getIpAddressVersion());
            assertEquals(parameters[13], networkAddress.getIpAddressScope());
            assertEquals(parameters[14], networkAddress.getIpAddress());
            assertEquals(InetAddress.getByName(parameters[14]), networkAddress.getInetAddress());
            assertEquals(Integer.parseInt(parameters[15]), networkAddress.getSubnetMaskLength());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testStep.execute("removeNetworkAdapter", new String[]{ids[0]});
            }
        }
    }
}
