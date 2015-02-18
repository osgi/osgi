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
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.cases.networkadapter.util.NetworkIfTestUtil;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkIfTestCase extends DefaultTestBundleControl {
	private NetworkTestProxy testProxy;

	protected void setUp() throws Exception {
		this.testProxy = new NetworkTestProxy(new TestStepProxy(getContext()));
	}

	protected void tearDown() throws Exception {
	}

	/**
	 * Tests NetworkAdapter add operation.
     * Please set in advance the information to the system property (bnd.bnd file).
	 */
	public void testAddAction01() {
		String[] ids = null;
		try {
			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
			String command = "addNetworkAdapter";
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = null;
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = null;

			ids = testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(1, adapterListener.size());
			assertEquals(0, addressListener.size());

			// Confirmation of the service property.
			ServiceReference adapterRef = adapterListener.get(0);
			assertEquals(parameters[0], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE));
			assertEquals(parameters[1], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME));
			assertEquals(parameters[2], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME));
			assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), (byte[]) adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS)));
			assertEquals(Boolean.valueOf(parameters[5]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK));
			assertEquals(Boolean.valueOf(parameters[6]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT));
			assertEquals(Boolean.valueOf(parameters[7]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP));
			assertEquals(Boolean.valueOf(parameters[8]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL));
			assertEquals(Boolean.valueOf(parameters[9]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST));
			assertEquals(parameters[10], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_PARENT));
			assertTrue(Arrays.equals(NetworkIfTestUtil.toStringArray(parameters[11]), (String[]) adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE)));
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
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: HOST<br>
	 */
	public void testAddAction02() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_HOST;
		String address = System.getProperty(NetworkIfTestUtil.PROP_HOST_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_HOST_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: PRIVATE_USE<br>
	 */
	public void testAddAction03() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
		String address = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: SHARED<br>
	 */
	public void testAddAction04() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_SHARED;
		String address = System.getProperty(NetworkIfTestUtil.PROP_SHARED_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_SHARED_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: LOOPBACK<br>
	 */
	public void testAddAction05() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_LOOPBACK;
		String address = System.getProperty(NetworkIfTestUtil.PROP_LOOPBACK_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_LOOPBACK_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: LINKLOCAL<br>
	 */
	public void testAddAction06() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_LINKLOCAL;
		String address = System.getProperty(NetworkIfTestUtil.PROP_LINKLOCAL_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_LINKLOCAL_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV4<br>
	 * scope: GLOBAL<br>
	 */
	public void testAddAction07() {

		String version = NetworkAddress.IPADDRESS_VERSION_4;
		String scope = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
		String address = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_IPADDRESS_4);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_MASKLENGTH_4);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV6<br>
	 * scope: LOOPBACK<br>
	 */
	public void testAddAction08() {

		String version = NetworkAddress.IPADDRESS_VERSION_6;
		String scope = NetworkAddress.IPADDRESS_SCOPE_LOOPBACK;
		String address = System.getProperty(NetworkIfTestUtil.PROP_LOOPBACK_IPADDRESS_6);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_LOOPBACK_MASKLENGTH_6);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV6<br>
	 * scope: UNSPECIFIED<br>
	 */
	public void testAddAction09() {

		String version = NetworkAddress.IPADDRESS_VERSION_6;
		String scope = NetworkAddress.IPADDRESS_SCOPE_UNSPECIFIED;
		String address = System.getProperty(NetworkIfTestUtil.PROP_UNSPECIFIED_IPADDRESS_6);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_UNSPECIFIED_MASKLENGTH_6);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV6<br>
	 * scope: UNIQUE_LOCAL<br>
	 */
	public void testAddAction10() {

		String version = NetworkAddress.IPADDRESS_VERSION_6;
		String scope = NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL;
		String address = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_IPADDRESS_6);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_MASKLENGTH_6);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV6<br>
	 * scope: LINKLOCAL<br>
	 */
	public void testAddAction11() {

		String version = NetworkAddress.IPADDRESS_VERSION_6;
		String scope = NetworkAddress.IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST;
		String address = System.getProperty(NetworkIfTestUtil.PROP_LINKEDSCOPEDUNICAST_IPADDRESS_6);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_LINKEDSCOPEDUNICAST_MASKLENGTH_6);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	/**
	 * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
	 * version: IPV6<br>
	 * scope: GLOBAL<br>
	 */
	public void testAddAction12() {

		String version = NetworkAddress.IPADDRESS_VERSION_6;
		String scope = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
		String address = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_IPADDRESS_6);
		String masklength = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_MASKLENGTH_6);

		testAddNetworkAdapter(version, scope, address, masklength);
	}

	private void testAddNetworkAdapter(String version, String scope, String address, String masklength) {
		String[] ids = null;
		try {
			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
			String command = "addNetworkAdapter";
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = version;
			parameters[13] = scope;
			parameters[14] = address;
			parameters[15] = masklength;

			ids = testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(1, adapterListener.size());
			assertEquals(1, addressListener.size());

			// Confirmation of the service property.
			ServiceReference adapterRef = adapterListener.get(0);
			assertEquals(parameters[0], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE));
			assertEquals(parameters[1], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME));
			assertEquals(parameters[2], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME));
			assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), (byte[]) adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS)));
			assertEquals(Boolean.valueOf(parameters[5]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK));
			assertEquals(Boolean.valueOf(parameters[6]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT));
			assertEquals(Boolean.valueOf(parameters[7]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP));
			assertEquals(Boolean.valueOf(parameters[8]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL));
			assertEquals(Boolean.valueOf(parameters[9]), adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST));
			assertEquals(parameters[10], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_PARENT));
			assertTrue(Arrays.equals(NetworkIfTestUtil.toStringArray(parameters[11]), (String[]) adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE)));

			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[0], addressRef.getProperty(NetworkAddress.NETWORKADAPTER_TYPE));
			assertEquals(parameters[12], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[13], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[14], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[15]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
			assertEquals(adapterRef.getProperty(Constants.SERVICE_PID), addressRef.getProperty(NetworkAddress.NETWORKADAPTER_PID));
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
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			Object servicePid = adapterListener.get(0).getProperty(Constants.SERVICE_PID);
			adapterListener.clear();

			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Addition of the IP address information
			command = "addNetworkAddress";
			message = "Add the network address set in System Properties.";
			String[] parameters1 = new String[5];
			parameters1[0] = ids[0];
			parameters1[1] = NetworkAddress.IPADDRESS_VERSION_6;
			parameters1[2] = NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL;
			parameters1[3] = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_IPADDRESS_6);
			parameters1[4] = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_MASKLENGTH_6);

			testProxy.executeTestStep(command, message, parameters1);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(0, adapterListener.size());
			assertEquals(1, addressListener.size());

			// Confirmation of the service property.
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[0], addressRef.getProperty(NetworkAddress.NETWORKADAPTER_TYPE));
			assertEquals(parameters1[1], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters1[2], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters1[3], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters1[4]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
			assertEquals(servicePid, addressRef.getProperty(NetworkAddress.NETWORKADAPTER_PID));
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
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Change of the IP address information
			command = "modifyNetworkAddress";
			message = "Modify the network address set in System Properties.";
			parameters = new String[5];
			parameters[0] = ids[1];
			parameters[1] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[2] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4_1);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4_1);

			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(0, adapterListener.size());
			assertEquals(1, addressListener.size());

			// Confirmation of the service property.
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[1], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[2], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[3], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[4]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Change of the IP address information
			command = "modifyNetworkAddress";
			message = "Modify the network address set in System Properties.";
			parameters = new String[5];
			parameters[0] = ids[1];
			parameters[1] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[2] = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_IPADDRESS_4);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_GLOBAL_MASKLENGTH_4);

			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(0, adapterListener.size());
			assertEquals(1, addressListener.size());

			// Confirmation of the service property.
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[1], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[2], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[3], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[4]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Change of the IP address information
			command = "modifyNetworkAddress";
			message = "Modify the network address set in System Properties.";
			parameters = new String[5];
			parameters[0] = ids[1];
			parameters[1] = NetworkAddress.IPADDRESS_VERSION_6;
			parameters[2] = NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL;
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_IPADDRESS_6);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_UNIQUELOCAL_MASKLENGTH_6);

			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the number of the services.
			assertEquals(0, adapterListener.size());
			assertEquals(1, addressListener.size());

			// Confirmation of the service property.
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[1], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[2], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[3], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[4]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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
	 * Tests NetworkAddress remove operation. Two NetworkAddress.
	 */
	public void testRemoveAction01() {
		String[] ids = null;
		try {
			String command = "addNetworkAdapter";
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			command = "addNetworkAddress";
			message = "Add the network address set in System Properties.";
			String[] parameters1 = new String[5];
			parameters1[0] = ids[0];
			parameters1[1] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters1[2] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters1[3] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4_1);
			parameters1[4] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4_1);

			testProxy.executeTestStep(command, message, parameters1);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Deletion of the IP address information.
			command = "removeNetworkAddress";
			message = "Remove the network address.";
			String[] parameters2 = new String[1];
			parameters2[0] = ids[1];
			testProxy.executeTestStep(command, message, parameters2);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the service unregistration.
			assertEquals(0, adapterListener.size());

			assertEquals(1, addressListener.size());
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[0], addressRef.getProperty(NetworkAddress.NETWORKADAPTER_TYPE));
			assertEquals(parameters[12], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[13], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[14], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[15]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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
	 * Tests NetworkAddress remove operation. One NetworkAddress.
	 */
	public void testRemoveAction02() {
		String[] ids = null;
		try {
			String command = "addNetworkAdapter";
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Deletion of the IP address information.
			command = "removeNetworkAddress";
			message = "Remove the network address.";
			String[] parameters1 = new String[1];
			parameters1[0] = ids[1];
			testProxy.executeTestStep(command, message, parameters1);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the service unregistration.
			assertEquals(0, adapterListener.size());

			assertEquals(1, addressListener.size());
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[12], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[13], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[14], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[15]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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
	 * Tests NetworkAdapter remove operation.
	 */
	public void testRemoveAction03() {
		String[] ids = null;
		try {
			String command = "addNetworkAdapter";
			String message = "Add the network adapter set in System Properties.";
			String[] parameters = new String[16];
			parameters[0] = System.getProperty(NetworkIfTestUtil.PROP_NETWORK_ADAPTER_TYPE);
			parameters[1] = System.getProperty(NetworkIfTestUtil.PROP_DISPLAYNAME);
			parameters[2] = System.getProperty(NetworkIfTestUtil.PROP_NAME);
			parameters[3] = System.getProperty(NetworkIfTestUtil.PROP_HARDWAREADDRESS);
			parameters[4] = System.getProperty(NetworkIfTestUtil.PROP_MTU);
			parameters[5] = System.getProperty(NetworkIfTestUtil.PROP_ISLOOPBACK);
			parameters[6] = System.getProperty(NetworkIfTestUtil.PROP_ISPOINTTOPOINT);
			parameters[7] = System.getProperty(NetworkIfTestUtil.PROP_ISUP);
			parameters[8] = System.getProperty(NetworkIfTestUtil.PROP_ISVIRTUAL);
			parameters[9] = System.getProperty(NetworkIfTestUtil.PROP_SUPPORTSMULTICAST);
			parameters[10] = System.getProperty(NetworkIfTestUtil.PROP_PARENT);
			parameters[11] = System.getProperty(NetworkIfTestUtil.PROP_SUBINTERFACE);
			parameters[12] = NetworkAddress.IPADDRESS_VERSION_4;
			parameters[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
			parameters[14] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_IPADDRESS_4);
			parameters[15] = System.getProperty(NetworkIfTestUtil.PROP_PRIVATE_MASKLENGTH_4);

			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
			TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

			// Deletion of the IP address information.
			command = "removeNetworkAdapter";
			message = "Remove the network adapter.";
			String[] parameters1 = new String[1];
			parameters1[0] = ids[0];
			testProxy.executeTestStep(command, message, parameters1);

			getContext().removeServiceListener(adapterListener);
			getContext().removeServiceListener(addressListener);

			// Confirmation of the service unregistration.
			assertEquals(1, adapterListener.size());
			ServiceReference adapterRef = adapterListener.get(0);
			assertEquals(parameters[0], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE));
			assertEquals(parameters[1], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME));
			assertEquals(parameters[2], adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME));
			assertTrue(Arrays.equals(NetworkIfTestUtil.toByteArrayMac(parameters[3]), (byte[]) adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS)));

			assertEquals(1, addressListener.size());
			ServiceReference addressRef = addressListener.get(0);
			assertEquals(parameters[12], addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION));
			assertEquals(parameters[13], addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE));
			assertEquals(parameters[14], addressRef.getProperty(NetworkAddress.IPADDRESS));
			assertEquals(Integer.parseInt(parameters[15]), ((Integer) addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH)).intValue());
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