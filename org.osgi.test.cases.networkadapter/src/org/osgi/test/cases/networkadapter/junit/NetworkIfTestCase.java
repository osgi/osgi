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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkIfTestCase extends DefaultTestBundleControl {
    private NetworkTestProxy	testProxy;

    @Override
	protected void setUp() throws Exception {
        this.testProxy = new NetworkTestProxy(new TestStepProxy(getContext()));
    }

    @Override
	protected void tearDown() throws Exception {
        this.testProxy.close();
    }

    /**
     * Tests NetworkAdapter add operation.
     */
    public void testAddAction01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-AA01] Add a no-address network adapter.";
            String[] parameters = new String[]{"no-address"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only one NetworkAdapter service registerd event is expected.", 1, adapterListener.size());
            assertEquals("Zero NetworkAddress service registered event is expected.", 0, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > adapterRef = adapterListener.get(0);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS) instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE) instanceof String[]);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: HOST<br>
     */
    public void testAddAction02() {

        String message = "[TEST-AA02] Add an ipv4-host network adapter.";
        String type = "ipv4-host";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_HOST);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     */
    public void testAddAction03() {

        String message = "[TEST-AA03] Add an ipv4-private-use network adapter.";
        String type = "ipv4-private-use";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: SHARED<br>
     */
    public void testAddAction04() {

        String message = "[TEST-AA04] Add an ipv4-shared network adapter.";
        String type = "ipv4-shared";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_SHARED);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: LOOPBACK<br>
     */
    public void testAddAction05() {

        String message = "[TEST-AA05] Add an ipv4-loopback network adapter.";
        String type = "ipv4-loopback";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_LOOPBACK);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: LINKLOCAL<br>
     */
    public void testAddAction06() {

        String message = "[TEST-AA06] Add an ipv4-linklocal network adapter.";
        String type = "ipv4-linklocal";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_LINKLOCAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: GLOBAL<br>
     */
    public void testAddAction07() {

        String message = "[TEST-AA07] Add an ipv4-global network adapter.";
        String type = "ipv4-global";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_GLOBAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: LOOPBACK<br>
     */
    public void testAddAction08() {

        String message = "[TEST-AA08] Add an ipv6-loopback network adapter.";
        String type = "ipv6-loopback";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_LOOPBACK);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: UNSPECIFIED<br>
     */
    public void testAddAction09() {

        String message = "[TEST-AA09] Add an ipv6-unspecified network adapter.";
        String type = "ipv6-unspecified";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_UNSPECIFIED);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    public void testAddAction10() {

        String message = "[TEST-AA10] Add an ipv6-uniquelocal network adapter.";
        String type = "ipv6-uniquelocal";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: LINKED_SCOPED_UNICAST<br>
     */
    public void testAddAction11() {

        String message = "[TEST-AA11] Add an ipv6-linkedscopedunicast network adapter.";
        String type = "ipv6-linkedscopedunicast";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: GLOBAL<br>
     */
    public void testAddAction12() {

        String message = "[TEST-AA12] Add an ipv6-global network adapter.";
        String type = "ipv6-global";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_GLOBAL);
    }

    private void testAddNetworkAdapter(String message,String type, String version, String scope) {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String[] parameters = new String[]{type};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only one NetworkAdapter service registered event is expected.", 1, adapterListener.size());
            assertEquals("Only one NetworkAddress service registered event is expected.", 1, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > adapterRef = adapterListener.get(0);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS) instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST) instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE) instanceof String[]);

            ServiceReference< ? > addressRef = addressListener.get(0);
            assertTrue("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_TYPE, addressRef.getProperty(NetworkAddress.NETWORKADAPTER_TYPE) instanceof String);
            assertEquals("The following service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, version, addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
            assertEquals("The following service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, scope, addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
            assertTrue("The following service property is not correct: " + NetworkAddress.IPADDRESS, addressRef.getProperty(NetworkAddress.IPADDRESS) instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAddress.SUBNETMASK_LENGTH, addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH) instanceof Integer);
            assertTrue("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_PID, addressRef.getProperty(NetworkAddress.NETWORKADAPTER_PID) instanceof String);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * IP address 1<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address 2<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    public void testAddAction13() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-AA13] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            adapterListener.clear();

            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Addition of the IP address information
            command = "addNetworkAddress";
            message = "Add an ipv6-uniquelocal network address.";
            String[] parameters1 = new String[]{"ipv6-uniquelocal", ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only zero NetworkAdapter service registered event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service registered event is expected.", 1, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, NetworkAddress.IPADDRESS_VERSION_6, addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL, addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     */
    public void testModifyAction01() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA01] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to other ipv4-private-use network address.";
            parameters = new String[]{"ipv4-private-use_2", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only zero NetworkAdapter service modified event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service modified event is expected.", 1, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, NetworkAddress.IPADDRESS_VERSION_4, addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE, addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV4<br>
     * scope: GLOBAL<br>
     */
    public void testModifyAction02() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA02] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to ipv4-global network address.";
            parameters = new String[]{"ipv4-global", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only zero NetworkAdapter service modified event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service modified event is expected.", 1, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, NetworkAddress.IPADDRESS_VERSION_4, addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, NetworkAddress.IPADDRESS_SCOPE_GLOBAL, addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    public void testModifyAction03() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA03] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to ipv6-uniquelocal network address.";
            parameters = new String[]{"ipv6-uniquelocal", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertEquals("Only zero NetworkAdapter service modified event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service modified event is expected.", 1, addressListener.size());

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION, NetworkAddress.IPADDRESS_VERSION_6, addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
            assertEquals("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE, NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL, addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress remove operation. Two NetworkAddress.
     */
    public void testRemoveAction01() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA01] Add the first network adapter/address.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            command = "addNetworkAddress";
            message = "Add the second network address.";
            String[] parameters1 = new String[]{"ipv4-private-use_2", ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAddress";
            message = "Remove the first network address.";
            String[] parameters2 = new String[]{ids[1]};
            testProxy.executeTestStep(command, message, parameters2);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertEquals("Only zero NetworkAdapter service unregistering event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service unregistering event is expected.", 1, addressListener.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress remove operation. One NetworkAddress.
     */
    public void testRemoveAction02() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA02] Add a network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAddress";
            message = "Remove the network address.";
            String[] parameters1 = new String[]{ids[1]};
            testProxy.executeTestStep(command, message, parameters1);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertEquals("Only zero NetworkAdapter service unregistering event is expected.", 0, adapterListener.size());
            assertEquals("Only one NetworkAddress service unregistering event is expected.", 1, addressListener.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter remove operation.
     */
    public void testRemoveAction03() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA03] Add a network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAdapter";
            message = "Remove the network adapter.";
            String[] parameters1 = new String[]{ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            getContext().removeServiceListener(adapterListener);
            getContext().removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertEquals("Only one NetworkAdapter service unregistering event is expected.", 1, adapterListener.size());
            assertEquals("Only one NetworkAddress service unregistering event is expected.", 1, addressListener.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }
}
