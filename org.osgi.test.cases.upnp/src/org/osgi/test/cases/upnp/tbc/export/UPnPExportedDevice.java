package org.osgi.test.cases.upnp.tbc.export;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.osgi.test.cases.upnp.tbc.UPnPConstants;

/**
 * 
 * 
 */
public class UPnPExportedDevice implements UPnPDevice {
	static final String				TESTING_SV		= "Testing";
	static final String				TESTMSG_SV		= "TestMessage";
	static final String				SERVICE_ID		= "urn:prosyst-com:serviceId:TesterDevice";
	static final String				SERVICE_TYPE	= "urn:schemas-prosyst-com:service:Tester";
	static final String				SERVICE_VER		= "1";
	private final Hashtable		props;
	private Dictionary			defaultProperties;
	private final UPnPService[]	service;

	public UPnPExportedDevice(UPnPService[] service) {
		// put properties
		this.props = new Hashtable(13);
		String ip = "unknown ip";
		try {
			ip = java.net.InetAddress.getLocalHost().getHostName();
		}
		catch (Exception uhe) {
			// ignored
		}
		props.put(UPnPDevice.UDN, "uuid:TesterType-" + ip);
		props.put(UPnPDevice.TYPE,
				"urn:schemas-prosyst-com:device:UPnPTesterType:1");
		props.put(UPnPDevice.FRIENDLY_NAME, "UPnP Tester");
		props.put(UPnPDevice.MANUFACTURER, "ProSyst Software AG");
		props.put(UPnPDevice.MANUFACTURER_URL, "http://www.prosyst.com");
		props.put(UPnPDevice.MODEL_DESCRIPTION, "UPnPTester device");
		props.put(UPnPDevice.MODEL_NAME, "upnptester");
		props.put(UPnPDevice.MODEL_NUMBER, "1");
		props.put(UPnPDevice.MODEL_URL, "http://www.upnptest.com/test.html");
		props.put(UPnPDevice.UPC, "123456789");
		props.put(UPnPDevice.SERIAL_NUMBER, "1230456");
		props.put(UPnPDevice.UPNP_EXPORT, "Yes");
		props.put(UPnPDevice.PRESENTATION_URL, "");
		this.service = service;
	}

	public static UPnPExportedDevice newUPnPTestervice() {
    // init state var Printing
		UPnPStateVariable testingSV = new TestStateVariable(TESTING_SV,
				Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, Boolean.FALSE,
                                                        null, null, null, null, false);
    // init state var PrintMessage
		UPnPStateVariable testMsg = new TestStateVariable(TESTMSG_SV,
				String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);


		UPnPStateVariable outString = new TestStateVariable(
				UPnPConstants.N_OUT_STRING, String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);

		UPnPStateVariable outStr = new TestStateVariable(
				UPnPConstants.N_OUT_STR, String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);

		UPnPStateVariable outBoolean = new TestStateVariable(
				UPnPConstants.N_OUT_BOOLEAN, Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, null,
                                                        null, null, null, null, false);


    // var number
		UPnPStateVariable outNumber = new TestStateVariable(
				UPnPConstants.N_OUT_NUMBER, Double.class,
                                                        UPnPStateVariable.TYPE_NUMBER, null,
                                                        null, null, null, null, true);

    // var int
		UPnPStateVariable outInt = new TestStateVariable(
				UPnPConstants.N_OUT_INT, Integer.class,
                                                        UPnPStateVariable.TYPE_INT, null,
                                                        null, null, null, null, true);

    // var char
		UPnPStateVariable outChar = new TestStateVariable(
				UPnPConstants.N_OUT_CHAR, Character.class,
                                                        UPnPStateVariable.TYPE_CHAR, null,
                                                        null, null, null, null, true);

    // var float
		UPnPStateVariable outFloat = new TestStateVariable(
				UPnPConstants.N_OUT_FLOAT, Float.class,
                                                        UPnPStateVariable.TYPE_FLOAT, null,
                                                        null, null, null, null, true);

    // init state var Printing
		UPnPStateVariable inBoolean = new TestStateVariable(
				UPnPConstants.N_IN_BOOLEAN, Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, Boolean.FALSE,
                                                        null, null, null, null, true);
    // init state var PrintMessage
		UPnPStateVariable inString = new TestStateVariable(
				UPnPConstants.N_IN_STRING, String.class,
                                                      UPnPStateVariable.TYPE_STRING, "",
                                                      null, null, null, null, true);

    // var number
		UPnPStateVariable inNumber = new TestStateVariable(
				UPnPConstants.N_IN_NUMBER, Double.class,
                                                        UPnPStateVariable.TYPE_NUMBER, null,
                                                        null, null, null, null, false);

    // var int
		UPnPStateVariable inInt = new TestStateVariable(UPnPConstants.N_IN_INT,
				Integer.class,
                                                        UPnPStateVariable.TYPE_INT, null,
                                                        null, null, null, null, false);

    // var char
		UPnPStateVariable inChar = new TestStateVariable(
				UPnPConstants.N_IN_CHAR, Character.class,
                                                        UPnPStateVariable.TYPE_CHAR, null,
                                                        null, null, null, null, false);

    // var float
		UPnPStateVariable inFloat = new TestStateVariable(
				UPnPConstants.N_IN_FLOAT, Float.class,
                                                        UPnPStateVariable.TYPE_FLOAT, null,
                                                        null, null, null, null, false);

		UPnPStateVariable levski = new TestStateVariable("blabla",
				String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);


		UPnPStateVariable[] vars = new UPnPStateVariable[] {testingSV, testMsg,
				outStr, outString, outNumber, outInt, outChar, outFloat,
				outBoolean,
                                                        inBoolean, inString, inNumber, inInt, inChar, inFloat, levski};
    // init action Print
    Hashtable nameVar = new Hashtable(6);
    nameVar.put(testMsg.getName(), testMsg);
		UPnPAction msgAct = new TestAction("testMsg", null,
				new String[] {TESTMSG_SV}, null, nameVar, null);
    nameVar.put(testingSV.getName(), testingSV);
    nameVar.put(levski.getName(), levski);
    Hashtable resp = new Hashtable();
		UPnPAction printAct = new TestAction("testPrint", null,
				new String[] {TESTMSG_SV}, new String[] {"blabla"}, nameVar,
				null);
    nameVar.put(outNumber.getName(), outNumber);
		UPnPAction numAct = new TestAction("testNum", null,
				new String[] {UPnPConstants.N_OUT_NUMBER}, null, nameVar, null);
    nameVar.put(outInt.getName(), outInt);
		UPnPAction intAct = new TestAction("testInt", null,
				new String[] {UPnPConstants.N_OUT_INT}, null, nameVar, null);
    nameVar.put(outChar.getName(), outChar);
		UPnPAction charAct = new TestAction("testChar", null,
				new String[] {UPnPConstants.N_OUT_CHAR}, null, nameVar, null);
    nameVar.put(outFloat.getName(), outFloat);
		UPnPAction floatAct = new TestAction("testFloat", null,
				new String[] {UPnPConstants.N_OUT_FLOAT}, null, nameVar, null);

    nameVar.put(inBoolean.getName(), inBoolean);
    nameVar.put(inString.getName(), inString);
    nameVar.put(inNumber.getName(), inNumber);
    nameVar.put(inInt.getName(), inInt);
    nameVar.put(inChar.getName(), inChar);
    nameVar.put(inFloat.getName(), inFloat);
    nameVar.put(outString.getName(), outString);
    nameVar.put(outStr.getName(), outStr);
    nameVar.put(outBoolean.getName(), outBoolean);

    resp = new Hashtable();

    resp.put(UPnPConstants.N_OUT_STRING, UPnPConstants.V_OUT_STRING);
    resp.put(UPnPConstants.N_OUT_STR, UPnPConstants.V_OUT_STR);
    resp.put(UPnPConstants.N_OUT_BOOLEAN, Boolean.TRUE);
    resp.put(UPnPConstants.N_OUT_NUMBER , new Double(42.0d));
    resp.put(UPnPConstants.N_OUT_INT, new Integer(42));
    resp.put(UPnPConstants.N_OUT_CHAR, new Character('\42'));
    resp.put(UPnPConstants.N_OUT_FLOAT, new Float(42.0f));

		UPnPAction testALL = new TestAction("testALL", null, new String[] {
				UPnPConstants.N_IN_STRING, UPnPConstants.N_IN_BOOLEAN,
				UPnPConstants.N_IN_NUMBER, UPnPConstants.N_IN_INT,
                                                                      UPnPConstants.N_IN_CHAR, UPnPConstants.N_IN_FLOAT}, new String[]{UPnPConstants.N_OUT_STRING,
                                                                      UPnPConstants.N_OUT_STR, UPnPConstants.N_OUT_BOOLEAN, UPnPConstants.N_OUT_NUMBER, UPnPConstants.N_OUT_INT,UPnPConstants.N_OUT_CHAR,
                                                                      UPnPConstants.N_OUT_FLOAT}, nameVar, resp);
		UPnPAction[] actions = new UPnPAction[] {testALL, printAct, msgAct,
				numAct, intAct, charAct, floatAct};
		// init printer service
		UPnPService[] services = new UPnPService[] {new TestService(actions,
				vars) {
			public String getId() {
				return SERVICE_ID;
			}

			public String getType() {
				return SERVICE_TYPE;
			}

			public String getVersion() {
				return SERVICE_VER;
			}
		}};
		// init printer device
		UPnPExportedDevice upnpTester = new UPnPExportedDevice(services);
		return upnpTester;
	}

	public Dictionary getDescriptions(String locale) {
		return props;
	}

	//  // methods from UPnPHTMLResource
	//  public String getPresentationalHTMLResourceName() {
	//    return "presentation.html";
	//  }
	//  public InputStream getResourceInputStream(String s, String locale) {
	//     return this.getClass().getResourceAsStream(s);
	//  }
	//  public String getResourceMIMEType(String s) {
	//     return "text/html";
	//  }
	public org.osgi.service.upnp.UPnPIcon[] getIcons(String str) {
		return null;
	}

	public org.osgi.service.upnp.UPnPService[] getServices() {
		TestService[] tmp = new TestService[service.length];
		System.arraycopy(service, 0, tmp, 0, service.length);
		return tmp;
	}

	public org.osgi.service.upnp.UPnPService getService(String str) {
		//System.out.println("STRING: " + str);
		for (int i = 0; i < service.length; i++) {
			if (service[i].getId().equals(str)) {
				return service[i];
			}
		}
		return null;
	}

	public synchronized String getUDN() {
		if (defaultProperties == null) {
			defaultProperties = getDescriptions(null);
		}
		return (String) defaultProperties.get(UPnPDevice.UDN);
	}

	public synchronized String getType() {
		if (defaultProperties == null) {
			defaultProperties = getDescriptions(null);
		}
		return (String) defaultProperties.get(UPnPDevice.TYPE);
	}
}