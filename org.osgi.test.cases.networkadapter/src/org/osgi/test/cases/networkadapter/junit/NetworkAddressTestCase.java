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
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkAddressTestCase extends DefaultTestBundleControl {
	private NetworkTestProxy testProxy;

    protected void setUp() throws Exception {
		this.testProxy = new NetworkTestProxy(new TestStepProxy(getContext()));
    }

    protected void tearDown() throws Exception {
		this.testProxy.close();
    }

    /**
     * Tests NetworkAddress of the interface that starts.
     * Please set in advance the information to the system property (bnd.bnd file).
     */
    public void testNetworkAddress01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the up network adapter set in System Properties.";
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

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);
            NetworkAddress networkAddress = (NetworkAddress) getContext().getService(addressListener.get(0));

            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_NETWORK_ADAPTER_TYPE, parameters[0], networkAddress.getNetworkAdapterType());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_IPADDRESS_VERSION, parameters[12], networkAddress.getIpAddressVersion());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_IPADDRESS_SCOPE, parameters[13], networkAddress.getIpAddressScope());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_IPADDRESS, parameters[14], networkAddress.getIpAddress());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_IPADDRESS, InetAddress.getByName(parameters[14]), networkAddress.getInetAddress());
            assertEquals("The following NetworkAddress information does not match: "+ NetworkIfTestUtil.PROP_UP_MASKLENGTH, Integer.parseInt(parameters[15]), networkAddress.getSubnetMaskLength());
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
