package org.osgi.test.cases.usb.junit;

import java.util.Arrays;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.usb.USBDevice;
import org.osgi.test.cases.step.TestStep;
import org.osgi.test.cases.usb.util.TestServiceListener;
import org.osgi.test.cases.usb.util.USBTestUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class USBDeviceTestCase extends DefaultTestBundleControl {

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testRegistDevice01() {

        String[] ids = null;
        TestStep testStep = null;

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE);
            parameters[7] = System.getProperty(USBTestUtil.PROP_IMANUFACTURER);
            parameters[8] = System.getProperty(USBTestUtil.PROP_IPRODUCT);
            parameters[9] = System.getProperty(USBTestUtil.PROP_ISERIALNUMBER);
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS);
            parameters[13] = null;
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(1, listener.size());

            ServiceReference ref = listener.get(0);
            String[] category = (String[])ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue(categoryFlag);
            assertEquals(Integer.decode(parameters[0]), ref.getProperty(USBDevice.USB_RELEASE_NUMBER));
            assertEquals(Integer.decode(parameters[1]), ref.getProperty(USBDevice.DEVICE_CLASS));
            assertEquals(Integer.decode(parameters[2]), ref.getProperty(USBDevice.DEVICE_SUBCLASS));
            assertEquals(Integer.decode(parameters[3]), ref.getProperty(USBDevice.DEVICE_PROTOCOL));
            assertEquals(Integer.decode(parameters[4]), ref.getProperty(USBDevice.VID));
            assertEquals(Integer.decode(parameters[5]), ref.getProperty(USBDevice.PID));
            assertEquals(Integer.decode(parameters[6]), ref.getProperty(USBDevice.RELEASE_NUMBER));
            String iManufacturer = (String) ref.getProperty(USBDevice.MANUFACTURER);
            if (iManufacturer != null) {
                assertEquals(parameters[7], iManufacturer);
            }
            String iProduct = (String) ref.getProperty(USBDevice.PRODUCT);
            if (iProduct != null) {
                assertEquals(parameters[8], iProduct);
            }
            String iSerialNumber = (String) ref.getProperty(USBDevice.SERIALNUMBER);
            if (iSerialNumber != null) {
                assertEquals(parameters[9], iSerialNumber);
            }
            assertTrue(Arrays.equals(USBTestUtil.toIntArray(parameters[10]), (int[])ref.getProperty(USBDevice.USB_CLASS)));
            assertEquals(Integer.decode(parameters[11]), ref.getProperty(USBDevice.USB_BUS));
            assertEquals(Integer.decode(parameters[12]), ref.getProperty(USBDevice.USB_ADDR));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testStep.execute("unregisterDevice", new String[]{ids[0]});
            }
        }
    }

    public void testRegistDevice02() {

        String[] ids = null;
        String[] ids2 = null;
        TestStep testStep = null;

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE);
            parameters[7] = System.getProperty(USBTestUtil.PROP_IMANUFACTURER);
            parameters[8] = System.getProperty(USBTestUtil.PROP_IPRODUCT);
            parameters[9] = System.getProperty(USBTestUtil.PROP_ISERIALNUMBER);
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS);
            parameters[13] = null;
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            String[] parameters2 = new String[15];
            parameters2[0] = System.getProperty(USBTestUtil.PROP_BCDUSB_2);
            parameters2[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS_2);
            parameters2[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS_2);
            parameters2[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL_2);
            parameters2[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR_2);
            parameters2[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT_2);
            parameters2[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE_2);
            parameters2[7] = System.getProperty(USBTestUtil.PROP_IMANUFACTURER_2);
            parameters2[8] = System.getProperty(USBTestUtil.PROP_IPRODUCT_2);
            parameters2[9] = System.getProperty(USBTestUtil.PROP_ISERIALNUMBER_2);
            parameters2[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES_2);
            parameters2[11] = System.getProperty(USBTestUtil.PROP_BUS_2);
            parameters2[12] = System.getProperty(USBTestUtil.PROP_ADDRESS_2);
            parameters2[13] = null;
            parameters2[14] = null;
            ids2 = testStep.execute(command, parameters2);

            getContext().removeServiceListener(listener);

            assertEquals(2, listener.size());

            ServiceReference ref = listener.get(0);
            String[] category = (String[])ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue(categoryFlag);
            assertEquals(Integer.decode(parameters[0]), ref.getProperty(USBDevice.USB_RELEASE_NUMBER));
            assertEquals(Integer.decode(parameters[1]), ref.getProperty(USBDevice.DEVICE_CLASS));
            assertEquals(Integer.decode(parameters[2]), ref.getProperty(USBDevice.DEVICE_SUBCLASS));
            assertEquals(Integer.decode(parameters[3]), ref.getProperty(USBDevice.DEVICE_PROTOCOL));
            assertEquals(Integer.decode(parameters[4]), ref.getProperty(USBDevice.VID));
            assertEquals(Integer.decode(parameters[5]), ref.getProperty(USBDevice.PID));
            assertEquals(Integer.decode(parameters[6]), ref.getProperty(USBDevice.RELEASE_NUMBER));
            String iManufacturer = (String) ref.getProperty(USBDevice.MANUFACTURER);
            if (iManufacturer != null) {
                assertEquals(parameters[7], iManufacturer);
            }
            String iProduct = (String) ref.getProperty(USBDevice.PRODUCT);
            if (iProduct != null) {
                assertEquals(parameters[8], iProduct);
            }
            String iSerialNumber = (String) ref.getProperty(USBDevice.SERIALNUMBER);
            if (iSerialNumber != null) {
                assertEquals(parameters[9], iSerialNumber);
            }
            assertTrue(Arrays.equals(USBTestUtil.toIntArray(parameters[10]), (int[])ref.getProperty(USBDevice.USB_CLASS)));
            assertEquals(Integer.decode(parameters[11]), ref.getProperty(USBDevice.USB_BUS));
            assertEquals(Integer.decode(parameters[12]), ref.getProperty(USBDevice.USB_ADDR));

            ref = listener.get(1);
            category = (String[])ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue(categoryFlag);
            assertEquals(Integer.decode(parameters2[0]), ref.getProperty(USBDevice.USB_RELEASE_NUMBER));
            assertEquals(Integer.decode(parameters2[1]), ref.getProperty(USBDevice.DEVICE_CLASS));
            assertEquals(Integer.decode(parameters2[2]), ref.getProperty(USBDevice.DEVICE_SUBCLASS));
            assertEquals(Integer.decode(parameters2[3]), ref.getProperty(USBDevice.DEVICE_PROTOCOL));
            assertEquals(Integer.decode(parameters2[4]), ref.getProperty(USBDevice.VID));
            assertEquals(Integer.decode(parameters2[5]), ref.getProperty(USBDevice.PID));
            assertEquals(Integer.decode(parameters2[6]), ref.getProperty(USBDevice.RELEASE_NUMBER));
            iManufacturer = (String) ref.getProperty(USBDevice.MANUFACTURER);
            if (iManufacturer != null) {
                assertEquals(parameters2[7], iManufacturer);
            }
            iProduct = (String) ref.getProperty(USBDevice.PRODUCT);
            if (iProduct != null) {
                assertEquals(parameters2[8], iProduct);
            }
            iSerialNumber = (String) ref.getProperty(USBDevice.SERIALNUMBER);
            if (iSerialNumber != null) {
                assertEquals(parameters2[9], iSerialNumber);
            }
            assertTrue(Arrays.equals(USBTestUtil.toIntArray(parameters2[10]), (int[])ref.getProperty(USBDevice.USB_CLASS)));
            assertEquals(Integer.decode(parameters2[11]), ref.getProperty(USBDevice.USB_BUS));
            assertEquals(Integer.decode(parameters2[12]), ref.getProperty(USBDevice.USB_ADDR));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testStep.execute("unregisterDevice", new String[]{ids[0]});
            }
            if (ids2 != null) {
                testStep.execute("unregisterDevice", new String[]{ids2[0]});
            }
        }
    }

    public void testSerialServiceProperty01() {

        String[] ids = null;
        TestStep testStep = null;

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_SERIAL_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_SERIAL_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_SERIAL_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_SERIAL_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_SERIAL_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_SERIAL_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_SERIAL_BCDDEVICE);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_SERIAL_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_SERIAL_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_SERIAL_ADDRESS);
            parameters[13] = System.getProperty(USBTestUtil.PROP_SERIAL_COMPORT);
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(1, listener.size());

            ServiceReference ref = listener.get(0);
            String[] category = (String[])ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBDevice.DEVICE_CATEGORY_SERIAL)) {
                    categoryFlag = true;
                    break;
                }
            }
            if (categoryFlag) {
                assertEquals(parameters[13], ref.getProperty(USBDevice.COM_PORT));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testStep.execute("unregisterDevice", new String[]{ids[0]});
            }
        }
    }

    public void testStorageServiceProperty01() {

        String[] ids = null;
        TestStep testStep = null;

        try {
            // ServiceListnere登録
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            // TestStepサービス取得
            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            // USBデバイスの登録
            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_STORAGE_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_STORAGE_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_STORAGE_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_STORAGE_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_STORAGE_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_STORAGE_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_STORAGE_BCDDEVICE);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_STORAGE_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_STORAGE_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_STORAGE_ADDRESS);
            parameters[13] = null;
            parameters[14] = System.getProperty(USBTestUtil.PROP_STORAGE_MOUNTPOINTS);
            ids = testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(1, listener.size());

            ServiceReference ref = listener.get(0);
            String[] category = (String[])ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBDevice.DEVICE_CATEGORY_MASSSTORAGE)) {
                    categoryFlag = true;
                    break;
                }
            }
            if (categoryFlag) {
                assertTrue(Arrays.equals(USBTestUtil.toStringArray(parameters[14]), (String[])ref.getProperty(USBDevice.MOUNTPOINTS)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testStep.execute("unregisterDevice", new String[]{ids[0]});
            }
        }
    }

    public void testUuregistDevice01() {

        String[] ids = null;
        TestStep testStep = null;

        try {
            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS);
            parameters[13] = null;
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            command = "unregisterDevice";
            parameters = new String[]{ids[0]};
            testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(1, listener.size());

            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals(registerId, unregisterId);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
        }
    }

    public void testUuregistDevice02() {

        String[] ids = null;
        String[] ids2 = null;
        TestStep testStep = null;

        try {
            // TestStepサービス取得
            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            // USBデバイスの登録
            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS);
            parameters[13] = null;
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB_2);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS_2);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS_2);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL_2);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR_2);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT_2);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE_2);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES_2);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS_2);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS_2);
            parameters[13] = null;
            parameters[14] = null;
            ids2 = testStep.execute(command, parameters);

            TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            command = "unregisterDevice";
            parameters = new String[]{ids[0]};
            testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(1, listener.size());

            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals(registerId, unregisterId);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids2 != null) {
                testStep.execute("unregisterDevice", new String[]{ids2[0]});
            }
        }
    }

    public void testUuregistDevice03() {

        String[] ids = null;
        String[] ids2 = null;
        TestStep testStep = null;

        try {
            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.impl.service.usb)");

            String command = "registerDevice";
            String[] parameters = new String[15];
            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS);
            parameters[13] = null;
            parameters[14] = null;
            ids = testStep.execute(command, parameters);

            parameters[0] = System.getProperty(USBTestUtil.PROP_BCDUSB_2);
            parameters[1] = System.getProperty(USBTestUtil.PROP_BDEVICECLASS_2);
            parameters[2] = System.getProperty(USBTestUtil.PROP_BDEVICESUBCLASS_2);
            parameters[3] = System.getProperty(USBTestUtil.PROP_BDEVICEPROTOCOL_2);
            parameters[4] = System.getProperty(USBTestUtil.PROP_IDVENDOR_2);
            parameters[5] = System.getProperty(USBTestUtil.PROP_IDPRODUCT_2);
            parameters[6] = System.getProperty(USBTestUtil.PROP_BCDDEVICE_2);
            parameters[7] = null;
            parameters[8] = null;
            parameters[9] = null;
            parameters[10] = System.getProperty(USBTestUtil.PROP_INTERFACECLASSES_2);
            parameters[11] = System.getProperty(USBTestUtil.PROP_BUS_2);
            parameters[12] = System.getProperty(USBTestUtil.PROP_ADDRESS_2);
            parameters[13] = null;
            parameters[14] = null;
            ids2 = testStep.execute(command, parameters);

            TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usb.USBDevice)");

            command = "unregisterDevice";
            parameters = new String[]{ids[0]};
            testStep.execute(command, parameters);

            parameters = new String[]{ids2[0]};
            testStep.execute(command, parameters);

            getContext().removeServiceListener(listener);

            assertEquals(2, listener.size());

            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals(registerId, unregisterId);

            registerId = Long.valueOf(ids2[0]);
            unregisterId = (Long) listener.get(1).getProperty(Constants.SERVICE_ID);
            assertEquals(registerId, unregisterId);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
        }
    }
}
