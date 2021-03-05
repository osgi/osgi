/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.networkadapter.junit;

import java.util.Arrays;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class NetworkAdapterTestCase extends DefaultTestBundleControl {
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
     * Tests NetworkAdapter of the loopback interface.
     */
    public void testNetworkAdapter01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA01] Add a loopback network adapter.";
            String[] parameters = new String[]{"loopback"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, networkAdapter.isLoopback());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());
            assertFalse("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, networkAdapter.isPointToPoint());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());
            assertFalse("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, networkAdapter.isVirtual());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA02] Add a point-to-point network adapter.";
            String[] parameters = new String[]{"point-to-point"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertFalse("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, networkAdapter.isLoopback());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, networkAdapter.isPointToPoint());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA03] Add an up network adapter.";
            String[] parameters = new String[]{"up"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, networkAdapter.isUp());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA04] Add a down network adapter.";
            String[] parameters = new String[]{"down"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());
            assertFalse("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, networkAdapter.isUp());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA05] Add a virtual network adapter.";
            String[] parameters = new String[]{"virtual"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, networkAdapter.isVirtual());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA06] Add a supported multicast network adapter.";
            String[] parameters = new String[]{"multicast"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);
			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, networkAdapter.supportsMulticast());

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
            String message = "[TEST-ADA07] Add an unsupported multicast network adapter.";
            String[] parameters = new String[]{"no-multicast"};
            ids = testProxy.executeTestStep(command, message, parameters);

            getContext().removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE, type instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME, name instanceof String);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, hardwareAddress instanceof byte[]);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, isLoopback instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, isPointToPoint instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP, isUp instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, isVirtual instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, supportsMulticast instanceof Boolean);
            assertTrue("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, subInterface instanceof String[]);

            NetworkAdapter networkAdapter = (NetworkAdapter) getContext().getService(adapterListener.get(0));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE, type, networkAdapter.getNetworkAdapterType());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, displayName, networkAdapter.getDisplayName());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME, name, networkAdapter.getName());
            assertTrue("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress()));
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, ((Boolean)isLoopback).booleanValue(), networkAdapter.isLoopback());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, ((Boolean)isPointToPoint).booleanValue(), networkAdapter.isPointToPoint());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP, ((Boolean)isUp).booleanValue(), networkAdapter.isUp());
            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, ((Boolean)isVirtual).booleanValue(), networkAdapter.isVirtual());

            assertEquals("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, ((Boolean)supportsMulticast).booleanValue(), networkAdapter.supportsMulticast());
            assertFalse("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, networkAdapter.supportsMulticast());

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
