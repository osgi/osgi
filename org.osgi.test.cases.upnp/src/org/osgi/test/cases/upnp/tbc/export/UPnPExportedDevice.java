package org.osgi.test.cases.upnp.tbc.export;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;
import org.osgi.test.cases.upnp.tbc.*;

/**
 * 
 * 
 */
public class UPnPExportedDevice implements UPnPDevice {
	private boolean					root;
	private boolean					active			= true;
	private boolean					enable			= true;
	static final String				TESTING_SV		= "Testing";
	static final String				TESTMSG_SV		= "TestMessage";
	static final String				SERVICE_ID		= "urn:prosyst-com:serviceId:TesterDevice";
	static final String				SERVICE_TYPE	= "urn:schemas-prosyst-com:service:Tester";
	static final String				SERVICE_VER		= "1";
	private Hashtable				props;
	private Dictionary				defaultProperties;
	private static BundleContext	bc;
	private TestService[]			service;

	public UPnPExportedDevice(TestService[] service, BundleContext bc) {
		this.service = service;
		UPnPExportedDevice.bc = bc;
		// put properties
		this.props = new Hashtable(13);
		String ip = "unknown ip";
		try {
			ip = java.net.InetAddress.getLocalHost().getHostName();
		}
		catch (Exception uhe) {
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
	}

	public static UPnPExportedDevice newUPnPTestervice() {
    // init state var Printing
    TestStateVariable testingSV = new TestStateVariable(TESTING_SV, Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, Boolean.FALSE,
                                                        null, null, null, null, false);
    // init state var PrintMessage
    TestStateVariable testMsg = new TestStateVariable(TESTMSG_SV, String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);


    TestStateVariable outString = new TestStateVariable(UPnPConstants.N_OUT_STRING, String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);

    TestStateVariable outStr = new TestStateVariable(UPnPConstants.N_OUT_STR, String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);

    TestStateVariable outBoolean = new TestStateVariable(UPnPConstants.N_OUT_BOOLEAN, Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, null,
                                                        null, null, null, null, false);


    // var number
    TestStateVariable outNumber = new TestStateVariable(UPnPConstants.N_OUT_NUMBER, Double.class,
                                                        UPnPStateVariable.TYPE_NUMBER, null,
                                                        null, null, null, null, true);

    // var int
    TestStateVariable outInt = new TestStateVariable(UPnPConstants.N_OUT_INT, Integer.class,
                                                        UPnPStateVariable.TYPE_INT, null,
                                                        null, null, null, null, true);

    // var char
    TestStateVariable outChar = new TestStateVariable(UPnPConstants.N_OUT_CHAR, Character.class,
                                                        UPnPStateVariable.TYPE_CHAR, null,
                                                        null, null, null, null, true);

    // var float
    TestStateVariable outFloat = new TestStateVariable(UPnPConstants.N_OUT_FLOAT, Float.class,
                                                        UPnPStateVariable.TYPE_FLOAT, null,
                                                        null, null, null, null, true);

    // init state var Printing
    TestStateVariable inBoolean = new TestStateVariable(UPnPConstants.N_IN_BOOLEAN, Boolean.class,
                                                        UPnPStateVariable.TYPE_BOOLEAN, Boolean.FALSE,
                                                        null, null, null, null, true);
    // init state var PrintMessage
    TestStateVariable inString = new TestStateVariable(UPnPConstants.N_IN_STRING, String.class,
                                                      UPnPStateVariable.TYPE_STRING, "",
                                                      null, null, null, null, true);

    // var number
    TestStateVariable inNumber = new TestStateVariable(UPnPConstants.N_IN_NUMBER, Double.class,
                                                        UPnPStateVariable.TYPE_NUMBER, null,
                                                        null, null, null, null, false);

    // var int
    TestStateVariable inInt = new TestStateVariable(UPnPConstants.N_IN_INT, Integer.class,
                                                        UPnPStateVariable.TYPE_INT, null,
                                                        null, null, null, null, false);

    // var char
    TestStateVariable inChar = new TestStateVariable(UPnPConstants.N_IN_CHAR, Character.class,
                                                        UPnPStateVariable.TYPE_CHAR, null,
                                                        null, null, null, null, false);

    // var float
    TestStateVariable inFloat = new TestStateVariable(UPnPConstants.N_IN_FLOAT, Float.class,
                                                        UPnPStateVariable.TYPE_FLOAT, null,
                                                        null, null, null, null, false);

    TestStateVariable levski = new TestStateVariable("blabla", String.class,
                                                        UPnPStateVariable.TYPE_STRING, "",
                                                        null, null, null, null, false);


    TestStateVariable[] vars = new TestStateVariable[] {testingSV, testMsg, outStr, outString, outNumber, outInt, outChar, outFloat, outBoolean,
                                                        inBoolean, inString, inNumber, inInt, inChar, inFloat, levski};
    // init action Print
    Hashtable nameVar = new Hashtable(6);
    nameVar.put(testMsg.getName(), testMsg);
    TestAction msgAct = new TestAction("testMsg", null, new String[]{TESTMSG_SV}, null, nameVar, null);
    nameVar.put(testingSV.getName(), testingSV);
    nameVar.put(levski.getName(), levski);
    Hashtable resp = new Hashtable();
    TestAction printAct = new TestAction("testPrint", null, new String[]{TESTMSG_SV}, new String[]{"blabla"}, nameVar, null);
    nameVar.put(outNumber.getName(), outNumber);
    TestAction numAct = new TestAction("testNum", null, new String[]{UPnPConstants.N_OUT_NUMBER}, null, nameVar, null);
    nameVar.put(outInt.getName(), outInt);
    TestAction intAct = new TestAction("testInt", null, new String[]{UPnPConstants.N_OUT_INT}, null, nameVar, null);
    nameVar.put(outChar.getName(), outChar);
    TestAction charAct = new TestAction("testChar", null, new String[]{UPnPConstants.N_OUT_CHAR}, null, nameVar, null);
    nameVar.put(outFloat.getName(), outFloat);
    TestAction floatAct = new TestAction("testFloat", null, new String[]{UPnPConstants.N_OUT_FLOAT}, null, nameVar, null);

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

    TestAction testALL = new TestAction("testALL", null, new String[]{UPnPConstants.N_IN_STRING, UPnPConstants.N_IN_BOOLEAN, UPnPConstants.N_IN_NUMBER, UPnPConstants.N_IN_INT,
                                                                      UPnPConstants.N_IN_CHAR, UPnPConstants.N_IN_FLOAT}, new String[]{UPnPConstants.N_OUT_STRING,
                                                                      UPnPConstants.N_OUT_STR, UPnPConstants.N_OUT_BOOLEAN, UPnPConstants.N_OUT_NUMBER, UPnPConstants.N_OUT_INT,UPnPConstants.N_OUT_CHAR,
                                                                      UPnPConstants.N_OUT_FLOAT}, nameVar, resp);
    TestAction[] actions = new TestAction[] {testALL, printAct, msgAct,
				numAct, intAct, charAct, floatAct};
		// init printer service
		TestService[] services = new TestService[] {new TestService(actions,
				vars, bc) {
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
		for (int i = 0; i < actions.length; i++) {
			actions[i].setParent(services[0]);
		}
		// init printer device
		UPnPExportedDevice upnpTester = new UPnPExportedDevice(services, bc);
		for (int j = 0; j < services.length; j++) {
			services[j].setDevice(upnpTester);
		}
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

	public String getUDN() {
		if (defaultProperties == null) {
			defaultProperties = getDescriptions(null);
		}
		return (String) defaultProperties.get(UPnPDevice.UDN);
	}

	public String getType() {
		if (defaultProperties == null) {
			defaultProperties = getDescriptions(null);
		}
		return (String) defaultProperties.get(UPnPDevice.TYPE);
	}
}