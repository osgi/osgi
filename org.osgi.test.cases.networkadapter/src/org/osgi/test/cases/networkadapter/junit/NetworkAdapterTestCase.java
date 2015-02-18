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

import java.util.Arrays;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.test.cases.networkadapter.util.NetworkIfTestUtil;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkAdapterTestCase extends DefaultTestBundleControl {
	private NetworkTestProxy testProxy;

    protected void setUp() throws Exception {
		this.testProxy = new NetworkTestProxy(new TestStepProxy(getContext()));
    }

    protected void tearDown() throws Exception {
    }
    
    /**
     * Tests NetworkAdapter of the loopback interface.
     * Please set in advance the information to the system property (bnd.bnd file).
     */
    public void testNetworkAdapter01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the loopback network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_LB_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_LB_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_LB_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_LB_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_LB_MTU);
            parameters[5] = "true";
            parameters[6] = "false";
            parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_LB_ISUP);
            parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_LB_ISVIRTUAL);
            parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_LB_SUPPORTSMULTICAST);
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_LB_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_LB_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_LB_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_LB_MASKLENGTH);

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());

            assertTrue(networkAdapter.isLoopback());
            assertFalse(networkAdapter.isPointToPoint());

            assertEquals(Boolean.valueOf(parameters[7]).booleanValue(), networkAdapter.isUp());
            assertEquals(Boolean.valueOf(parameters[8]).booleanValue(), networkAdapter.isVirtual());
            assertEquals(Boolean.valueOf(parameters[9]).booleanValue(), networkAdapter.supportsMulticast());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the point-to-point interface.
     */
    public void testNetworkAdapter02() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the point-to-point network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_PTP_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_PTP_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_PTP_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_PTP_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_PTP_MTU);
            parameters[5] = "false";
            parameters[6] = "true";
            parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_PTP_ISUP);
            parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_PTP_ISVIRTUAL);
            parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_PTP_SUPPORTSMULTICAST);
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_PTP_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_PTP_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PTP_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PTP_MASKLENGTH);

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());

            assertFalse(networkAdapter.isLoopback());
            assertTrue(networkAdapter.isPointToPoint());

            assertEquals(Boolean.valueOf(parameters[7]).booleanValue(), networkAdapter.isUp());
            assertEquals(Boolean.valueOf(parameters[8]).booleanValue(), networkAdapter.isVirtual());
            assertEquals(Boolean.valueOf(parameters[9]).booleanValue(), networkAdapter.supportsMulticast());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that starts.
     */
    public void testNetworkAdapter03() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
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
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());
            assertEquals(Boolean.valueOf(parameters[5]).booleanValue(), networkAdapter.isLoopback());
            assertEquals(Boolean.valueOf(parameters[6]).booleanValue(), networkAdapter.isPointToPoint());

            assertTrue(networkAdapter.isUp());
            assertFalse(networkAdapter.isVirtual());

            assertEquals(Boolean.valueOf(parameters[9]).booleanValue(), networkAdapter.supportsMulticast());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that stops.
     */
    public void testNetworkAdapter04() {

        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the down network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_UP_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_UP_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_UP_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_UP_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_UP_MTU);
            parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_UP_ISLOOPBACK);
            parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_UP_ISPOINTTOPOINT);
            parameters[7] = "false";
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
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());
            assertEquals(Boolean.valueOf(parameters[5]).booleanValue(), networkAdapter.isLoopback());
            assertEquals(Boolean.valueOf(parameters[6]).booleanValue(), networkAdapter.isPointToPoint());

            assertFalse(networkAdapter.isUp());
            assertFalse(networkAdapter.isVirtual());

            assertEquals(Boolean.valueOf(parameters[9]).booleanValue(), networkAdapter.supportsMulticast());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the virtual interface.
     */
    public void testNetworkAdapter05() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the virtual network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_VIR_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_VIR_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_VIR_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_VIR_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_VIR_MTU);
            parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_VIR_ISLOOPBACK);
            parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_VIR_ISPOINTTOPOINT);
            parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_VIR_ISUP);
            parameters[8] = "true";
            parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_VIR_SUPPORTSMULTICAST);
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_VIR_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_VIR_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_VIR_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_VIR_MASKLENGTH);

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());
            assertEquals(Boolean.valueOf(parameters[5]).booleanValue(), networkAdapter.isLoopback());
            assertEquals(Boolean.valueOf(parameters[6]).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals(Boolean.valueOf(parameters[7]).booleanValue(), networkAdapter.isUp());

            assertTrue(networkAdapter.isVirtual());

            assertEquals(Boolean.valueOf(parameters[9]).booleanValue(), networkAdapter.supportsMulticast());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that supports multicast.
     */
    public void testNetworkAdapter06() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the multicast support network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_MUL_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_MUL_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_MUL_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_MUL_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MUL_MTU);
            parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISLOOPBACK);
            parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISPOINTTOPOINT);
            parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISUP);
            parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISVIRTUAL);
            parameters[9] = "true";
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_MUL_MASKLENGTH);

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());
            assertEquals(Boolean.valueOf(parameters[5]).booleanValue(), networkAdapter.isLoopback());
            assertEquals(Boolean.valueOf(parameters[6]).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals(Boolean.valueOf(parameters[7]).booleanValue(), networkAdapter.isUp());
            assertEquals(Boolean.valueOf(parameters[8]).booleanValue(), networkAdapter.isVirtual());

            assertTrue(networkAdapter.supportsMulticast());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
            	testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that dose not supports multicast.
     */
    public void testNetworkAdapter07() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
			String message = "Add the multicast no-support network adapter set in System Properties.";
            String[] parameters = new String[16];
            parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_MUL_NETWORK_ADAPTER_TYPE);
            parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_MUL_DISPLAYNAME);
            parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_MUL_NAME);
            parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_MUL_HARDWAREADDRESS);
            parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MUL_MTU);
            parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISLOOPBACK);
            parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISPOINTTOPOINT);
            parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISUP);
            parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_MUL_ISVIRTUAL);
            parameters[9] = "false";
            parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
            parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
            parameters[12] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS_VERSION);
            parameters[13] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS_SCOPE);
            parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_MUL_IPADDRESS);
            parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_MUL_MASKLENGTH);

            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));

            assertEquals(parameters[0], networkAdapter.getNetworkAdapterType());
            assertEquals(parameters[1], networkAdapter.getDisplayName());
            assertEquals(parameters[2], networkAdapter.getName());
            assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), networkAdapter.getHardwareAddress()));
            assertEquals(Integer.parseInt(parameters[4]), networkAdapter.getMTU());
            assertEquals(Boolean.valueOf(parameters[5]).booleanValue(), networkAdapter.isLoopback());
            assertEquals(Boolean.valueOf(parameters[6]).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals(Boolean.valueOf(parameters[7]).booleanValue(), networkAdapter.isUp());
            assertEquals(Boolean.valueOf(parameters[8]).booleanValue(), networkAdapter.isVirtual());

            assertFalse(networkAdapter.supportsMulticast());
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