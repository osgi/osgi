package org.osgi.test.cases.upnp.tbc;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;
import org.osgi.test.cases.upnp.tbc.parser.*;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 * @version 1.1
 */
public class UPnPTester implements UPnPEventListener {
	private DefaultTestBundleControl	control;
	private XMLParser					parser;
	private ControlServer				cps;
	private UPnPDevice					dev;
	private String						udn;
	private String						currentServ;
	private Object						waiter;
	private Vector						vector;
	private Vector						argsNames;
	private Filter						filter;
	private int							ender;
	private BundleContext				bc;
	private ControlPoint				cp;
	//from discovery XML
	private String						URL_BASE		= null;
	private String						SERVICE_TYPE	= null;
	private String						SERVICE_ID		= null;
	private String						SCPD_URL		= null;
	private String						CONTROL_URL		= null;
	private String						EVENT_SUB_URL	= null;
	public static final int				DISCOVERY		= 1;
	public static final int				DESCRIPTION		= 2;
	public static final int				CONTROL			= 3;
	public static final int				EVENTING		= 4;
	public static final int				EXPORT			= 5;

	public UPnPTester(DefaultTestBundleControl control, int state,
			UPnPDevice dev, BundleContext bc) {
		this.control = control;
		this.dev = dev;
		this.bc = bc;
		waiter = new Object();
		vector = new Vector();
		if (dev == null) {
			control.log("UPnPDevice is NULL");
			return;
		}
		udn = (String) dev.getDescriptions(null).get(UPnPDevice.UDN);
		switch (state) {
			case DISCOVERY : {
				createDiscoveryTest();
				break;
			}
			case DESCRIPTION : {
			}
			case CONTROL : {
				createControlTest();
				break;
			}
			case EVENTING : {
				createEventTest();
				break;
			}
			case EXPORT : {
				createExportTest();
				break;
			}
			default : {
				control.log("NO SUCH TEST STEP IN UPnP");
			}
		}
	}

	private void createDiscoveryTest() {
		try {
			Dictionary dict = dev.getDescriptions(null);
			String test = (String) dict.get(UPnPDevice.UPC);
			control.log("UPnPDevice UPC: " + test);
			if (!test.equals(UPnPConstants.V_UPC)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " UPC not matched");
			}
			test = (String) dict.get(UPnPDevice.FRIENDLY_NAME);
			control.log("UPnPDevice FRIENDLY_NAME: " + test);
			if (!test.equals(UPnPConstants.V_FN)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Friendly name not matched");
			}
			test = (String) dict.get(UPnPDevice.MANUFACTURER);
			control.log("UPnPDevice MANUFACTURER: " + test);
			if (!test.equals(UPnPConstants.V_MANU)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Manufacturer not matched");
			}
			test = (String) dict.get(UPnPDevice.MANUFACTURER_URL);
			control.log("UPnPDevice MANUFACTURER_URL: " + test);
			if (!test.equals(UPnPConstants.V_MANU_URL)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Manufacturer URL not matched");
			}
			test = (String) dict.get(UPnPDevice.MODEL_DESCRIPTION);
			control.log("UPnPDevice MODEL_DESCRIPTION: " + test);
			if (!test.equals(UPnPConstants.V_MOD_DESC)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Model Description not matched");
			}
			test = (String) dict.get(UPnPDevice.MODEL_NAME);
			control.log("UPnPDevice MODEL_NAME: " + test);
			if (!test.equals(UPnPConstants.V_MOD_NAME)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Model Name not matched");
			}
			test = (String) dict.get(UPnPDevice.MODEL_NUMBER);
			control.log("UPnPDevice MODEL_NUMBER: " + test);
			if (!test.equals(UPnPConstants.V_MOD_NUMB)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Model Number not matched");
			}
			test = (String) dict.get(UPnPDevice.MODEL_URL);
			control.log("UPnPDevice MODEL_URL: " + test);
			if (!test.equals(UPnPConstants.V_MOD_URL)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Model URL not matched");
			}
			test = (String) dict.get(UPnPDevice.SERIAL_NUMBER);
			control.log("UPnPDevice SERIAL_NUMBER: " + test);
			if (!test.equals(UPnPConstants.V_SN)) {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Serial Number not matched");
			}
			UPnPIcon[] icos = (UPnPIcon[]) dev.getIcons("En");
			if (icos.length == 2) {
				for (int i = 0; i < icos.length; i++) {
					InputStream fis = null;
					if (icos[i].getHeight() == 16) {
						if (icos[i].getWidth() != 16) {
							control.log("SERVER: " + udn + UPnPConstants.DD
									+ " Not property width for icon: " + i);
						}
						fis = UPnPTester.class
								.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/images/small.gif");
					}
					else
						if (icos[i].getHeight() == 32) {
							if (icos[i].getWidth() != 32) {
								control.log("SERVER: " + udn + UPnPConstants.DD
										+ " Not property width for icon: " + i);
							}
							fis = UPnPTester.class
									.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/images/big.gif");
						}
						else {
							control.log("SERVER: " + udn + UPnPConstants.DD
									+ " Not property height for icon: " + i);
						}
					if (icos[i].getDepth() != 8) {
						control.log("SERVER: " + udn + UPnPConstants.DD
								+ " Not property width for icon: " + i);
					}
					if (!icos[i].getMimeType().equals(UPnPConstants.V_MT)) {
						control.log("SERVER: " + udn + UPnPConstants.DD
								+ " Not property mime type for icon: " + i);
					}
					if (fis != null) {
						try {
							InputStream ricon = icos[i].getInputStream();
							byte[] bfis = readFully(fis);
							byte[] bricon = readFully(ricon);
							if (!checkArrays(bfis, bricon)) {
								control.log("SERVER: " + udn + UPnPConstants.DD
										+ " Icons are not the same: " + i);
							}
							else {
								control
										.log("Compared icons are the same.Icon test is OK");
							}
							ricon.close();
							fis.close();
						}
						catch (Exception exc) {
							control.log("SERVER: " + udn + UPnPConstants.DD
									+ " Exception :" + exc + " while icon: "
									+ i);
						}
					}
					else {
						control.log("SERVER: " + udn + UPnPConstants.DD
								+ " Unable to get file for icon: " + i);
					}
				}
			}
			else {
				control.log("SERVER: " + udn + UPnPConstants.DD
						+ " Wrong Number of Icons");
			}
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	private byte[] readFully(InputStream is) throws IOException {
		byte[] buff = new byte[1024];
		int size = is.read(buff);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (size >= 0) {
			baos.write(buff, 0, size);
			size = is.read(buff);
		}
		return baos.toByteArray();
	}

	private boolean checkArrays(byte[] arr, byte[] to) {
		if (arr.length != to.length) {
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != to[i]) {
				return false;
			}
		}
		return true;
	}

	private void createControlTest() {
		UPnPService cs = dev.getService(UPnPConstants.SCONT_ID);
		Hashtable hash = new Hashtable();
		hash.put(UPnPConstants.N_IN_OUT, UPnPConstants.V_IN_OUT);
		UPnPAction act = cs.getAction(UPnPConstants.ACT_PF);
		control.log("Invoking an action Post Fail");
		try {
			act.invoke(hash);
			control.log("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_PF);
		}
		catch (Exception exc) {
			control.log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_MPF);
		control.log("Invoking an action MPost Fail");
		try {
			act.invoke(hash);
			control.log("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_MPF);
		}
		catch (Exception exc) {
			//exc.printStackTrace();
			control.log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_PB);
		control.log("Invoking an action Post Block.Please wait");
		try {
			act.invoke(hash);
			control.log("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_PB);
		}
		catch (Exception exc) {
			control.log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_MPB);
		control.log("Invoking an action MPost Block.Please wait");
		try {
			act.invoke(hash);
			control.log("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_MPB);
		}
		catch (Exception exc) {
			control.log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_POS);
		control.log("Invoking an action Post Out Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (!checkRet(dict)) {
				control.log("SERVER: " + UPnPConstants.ACT_POS
						+ " Invoking Failed");
			}
		}
		catch (Exception exc) {
			control.log("Unexpected Exception");
			exc.printStackTrace();
		}
		act = cs.getAction(UPnPConstants.ACT_MPOS);
		control.log("Invoking an action MPost Out Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (!checkRet(dict)) {
				control.log("SERVER: " + UPnPConstants.ACT_MPOS
						+ " Invoking Failed");
			}
		}
		catch (Exception exc) {
			control.log("Unexpected Exception");
			exc.printStackTrace();
		}
		hash.clear();
		Double duble = new Double(UPnPConstants.V_IN_NUMBER);
		Float fl = new Float(UPnPConstants.V_IN_FLOAT);
		hash.put(UPnPConstants.N_IN_INT, UPnPConstants.V_IN_INT);
		hash.put(UPnPConstants.N_IN_UI4, UPnPConstants.V_IN_UI4);
		hash.put(UPnPConstants.N_IN_FLOAT, fl);
		hash.put(UPnPConstants.N_IN_NUMBER, duble);
		hash.put(UPnPConstants.N_IN_CHAR, UPnPConstants.V_IN_CHAR);
		hash.put(UPnPConstants.N_IN_STRING, UPnPConstants.V_IN_STRING);
		hash.put(UPnPConstants.N_IN_BOOLEAN, UPnPConstants.V_IN_BOOLEAN);
		hash.put(UPnPConstants.N_IN_HEX, UPnPConstants.V_IN_HEX.getBytes());
		act = cs.getAction(UPnPConstants.ACT_PIS);
		control
				.log("Invoking an action Post In Success with invalid arguments");
		try {
			Dictionary dict = act.invoke(hash);
			control
					.log("SERVER: "
							+ UPnPConstants.ACT_PIS
							+ " Invoke was successful: The UPnP Driver doesn't check action arguments for validity");
		}
		catch (Exception exc) {
			control.log("Exception is thrown OK");
		}
		hash = new Hashtable(9, 1f);
		hash.put(UPnPConstants.N_IN_INT, new Integer(UPnPConstants.V_IN_INT));
		hash.put(UPnPConstants.N_IN_UI4, new Long(UPnPConstants.V_IN_UI4));
		hash.put(UPnPConstants.N_IN_FLOAT, fl);
		hash.put(UPnPConstants.N_IN_NUMBER, duble);
		hash.put(UPnPConstants.N_IN_CHAR, new Character(UPnPConstants.V_IN_CHAR
				.charAt(0)));
		hash.put(UPnPConstants.N_IN_STRING, UPnPConstants.V_IN_STRING);
		hash.put(UPnPConstants.N_IN_BOOLEAN, new Boolean(
				UPnPConstants.V_IN_BOOLEAN));
		hash.put(UPnPConstants.N_IN_HEX, UPnPConstants.V_IN_HEX.getBytes());
		control.log("Invoking an action Post In Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (dict != null) {
				String res = (String) dict.get(UPnPConstants.N_OUT_OUT);
				if (!res.equals(UPnPConstants.V_OUT_OUT_OK)) {
					control.log("SERVER: " + UPnPConstants.ACT_PIS
							+ " Invoke Failed: " + res);
				}
			}
		}
		catch (Exception exc) {
			control.log("SERVER: " + UPnPConstants.ACT_PIS
					+ " Invoke Failed: Threre was an exception");
		}
		hash = new Hashtable(9, 1f);
		hash.put(UPnPConstants.N_IN_INT, new Integer(UPnPConstants.V_IN_INT));
		hash.put(UPnPConstants.N_IN_UI4, new Long(UPnPConstants.V_IN_UI4));
		hash.put(UPnPConstants.N_IN_FLOAT, fl);
		hash.put(UPnPConstants.N_IN_NUMBER, duble);
		hash.put(UPnPConstants.N_IN_CHAR, new Character(UPnPConstants.V_IN_CHAR
				.charAt(0)));
		hash.put(UPnPConstants.N_IN_STRING, UPnPConstants.V_IN_STRING);
		hash.put(UPnPConstants.N_IN_BOOLEAN, new Boolean(
				UPnPConstants.V_IN_BOOLEAN));
		hash.put(UPnPConstants.N_IN_HEX, UPnPConstants.V_IN_HEX.getBytes());
		act = cs.getAction(UPnPConstants.ACT_MPIS);
		control.log("Invoking an action MPost In Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (dict != null) {
				String res = (String) dict.get(UPnPConstants.N_OUT_OUT);
				if (!res.equals(UPnPConstants.V_OUT_OUT_OK)) {
					control.log("SERVER: " + UPnPConstants.ACT_MPIS
							+ " Invoking Failed: " + res);
				}
			}
		}
		catch (Exception exc) {
			control.log("Unexpected Exception");
		}
	}

	/*
	 * 
	 *  
	 */
	private boolean checkRet(Dictionary dict) throws Exception {
		boolean state = false;
		if (dict != null) {
			state = true;
			Object tmp = dict.get(UPnPConstants.N_OUT_HEX);
			//System.out.println(tmp.getClass());
			//System.out.println(tmp);
			String st = new String((byte[]) dict.get(UPnPConstants.N_OUT_HEX));
			if (st == null || !st.equals(UPnPConstants.V_OUT_HEX)) {
				control.log("SERVER: " + UPnPConstants.N_OUT_HEX
						+ " value is not ok: " + st);
				state = false;
			}
			Integer inint = (Integer) dict.get(UPnPConstants.N_OUT_INT);
			if (inint == null
					|| (inint.intValue() != Integer
							.parseInt(UPnPConstants.V_OUT_INT))) {
				control.log("SERVER: " + UPnPConstants.N_OUT_INT
						+ " value is not ok: " + inint);
				state = false;
			}
			Long lo = (Long) dict.get(UPnPConstants.N_OUT_UI4);
			if (lo == null
					|| (lo.longValue() != Long
							.parseLong(UPnPConstants.V_OUT_UI4))) {
				control.log("SERVER: " + UPnPConstants.N_OUT_UI4
						+ " value is not ok: " + lo);
				state = false;
			}
			Float fl = (Float) dict.get(UPnPConstants.N_OUT_FLOAT);
			if (fl == null
					|| (fl.floatValue() != Float
							.parseFloat(UPnPConstants.V_OUT_FLOAT))) {
				control.log("SERVER: " + UPnPConstants.N_OUT_FLOAT
						+ " value is not ok: " + fl);
				state = false;
			}
			Double dl = (Double) dict.get(UPnPConstants.N_OUT_NUMBER);
			if (dl == null
					|| (dl.doubleValue() != Double
							.parseDouble(UPnPConstants.V_OUT_NUMBER))) {
				control.log("SERVER: " + UPnPConstants.N_OUT_NUMBER
						+ " value is not ok: " + dl);
				state = false;
			}
			Character ch = (Character) dict.get(UPnPConstants.N_OUT_CHAR);
			if (ch == null
					|| (ch.charValue() != UPnPConstants.V_OUT_CHAR.charAt(0))) {
				control.log("SERVER: " + UPnPConstants.N_OUT_CHAR
						+ " value is not ok: " + ch);
				state = false;
			}
			String str = (String) dict.get(UPnPConstants.N_OUT_STRING);
			if (str == null || !str.equals(UPnPConstants.V_OUT_STRING)) {
				control.log("SERVER: " + UPnPConstants.N_OUT_STRING
						+ " value is not ok: " + str);
				state = false;
			}
			Boolean bool = (Boolean) dict.get(UPnPConstants.N_OUT_BOOLEAN);
			if (bool == null
					|| (bool.booleanValue() != (new Boolean(
							UPnPConstants.V_OUT_BOOLEAN)).booleanValue())) {
				control.log("SERVER: " + UPnPConstants.N_OUT_BOOLEAN
						+ " value is not ok: " + bool);
				state = false;
			}
		}
		return state;
	}

	private void createEventTest() {
		Hashtable hash = new Hashtable();
		UPnPService es;
		ServiceRegistration sr;
		control
				.log("Device gets service urn:prosyst-com:serviceId:EventService");
		es = dev.getService(UPnPConstants.SEV_ID);
		currentServ = es.getId();
		try {
			filter = bc.createFilter("(&(" + UPnPDevice.ID + "=" + udn + ")("
					+ UPnPService.ID + "=" + UPnPConstants.SEV_ID + "))");
		}
		catch (InvalidSyntaxException exc) {
			exc.printStackTrace();
		}
		hash.put(UPnPEventListener.UPNP_FILTER, filter);
		hash.put("upnptest", "true");
		ender = 8;
		control.log("Register UPnPEventListener to listen for events");
		sr = bc.registerService(UPnPEventListener.class.getName(), this, hash);
		waitFor();
		//    sr.unregister();
		if (!checkEvents()) {
			control.log("SERVER: " + udn + UPnPConstants.DD
					+ " Main events are not ok");
		}
		else {
			control.log("Main events are OK");
		}
		sr.unregister();
		vector.removeAllElements();
	}

	private boolean checkEvents() {
		boolean state = true;
		if (vector.size() != 9) {
			control.log("SERVER: Wrong number of events");
			return false;
		}
		try {
			int evSize = vector.size();
			for (int i = 0; i < evSize; i++) {
				Dictionary dict = (Dictionary) vector.elementAt(i);
				if (!testEvent(dict, dict.size())) {
					control.log("SERVER: Wrong " + i + " events");
					state = false;
				}
			}
		}
		catch (Exception exc) {
			control.log("SERVER: " + udn + UPnPConstants.DD
					+ " Exception while checking events: " + exc);
			return false;
		}
		return state;
	}

	private boolean testEvent(Dictionary dict, int si) {
		boolean state = true;
		switch (si) {
			case 9 : {
				String str1 = (String) dict.get(UPnPConstants.N_OUT);
				if (str1 == null || !str1.equals(UPnPConstants.V_OUT)) {
					control.log("SERVER: " + UPnPConstants.N_OUT + " " + si
							+ " value is not ok: " + str1);
					state = false;
				}
			}
			case 8 : {
				String st = new String((byte[]) dict.get(UPnPConstants.N_HEX));
				if (st == null || !st.equals(UPnPConstants.V_HEX)) {
					control.log("SERVER: " + UPnPConstants.N_HEX + " " + si
							+ " value is not ok: " + st);
					state = false;
				}
			}
			case 7 : {
				Boolean bool = (Boolean) dict.get(UPnPConstants.N_BOOLEAN);
				if (bool == null
						|| (bool.booleanValue() != (new Boolean(
								UPnPConstants.V_BOOLEAN)).booleanValue())) {
					control.log("SERVER: " + UPnPConstants.N_BOOLEAN + " " + si
							+ " value is not ok: " + bool);
					state = false;
				}
			}
			case 6 : {
				String str = (String) dict.get(UPnPConstants.N_STRING);
				if (str == null || !str.equals(UPnPConstants.V_STRING)) {
					control.log("SERVER: " + UPnPConstants.N_STRING + " " + si
							+ " value is not ok: " + str);
					state = false;
				}
			}
			case 5 : {
				Character ch = (Character) dict.get(UPnPConstants.N_CHAR);
				if (ch == null
						|| (ch.charValue() != UPnPConstants.V_CHAR.charAt(0))) {
					control.log("SERVER: " + UPnPConstants.N_CHAR + " " + si
							+ " value is not ok: " + ch);
					state = false;
				}
			}
			case 4 : {
				Float fl = (Float) dict.get(UPnPConstants.N_FLOAT);
				if (fl == null
						|| (fl.floatValue() != Float
								.parseFloat(UPnPConstants.V_FLOAT))) {
					control.log("SERVER: " + UPnPConstants.N_FLOAT + " " + si
							+ " value is not ok: " + fl);
					state = false;
				}
			}
			case 3 : {
				Double dl = (Double) dict.get(UPnPConstants.N_NUMBER);
				if (dl == null
						|| (dl.doubleValue() != Double
								.parseDouble(UPnPConstants.V_NUMBER))) {
					control.log("SERVER: " + UPnPConstants.N_NUMBER + " " + si
							+ " value is not ok: " + dl);
					state = false;
				}
			}
			case 2 : {
				Long lo = (Long) dict.get(UPnPConstants.N_UI4);
				if (lo == null
						|| (lo.longValue() != Long
								.parseLong(UPnPConstants.V_UI4))) {
					control.log("SERVER: " + UPnPConstants.N_UI4 + " " + si
							+ " value is not ok: " + lo);
					state = false;
				}
			}
			case 1 : {
				Integer in = (Integer) dict.get(UPnPConstants.N_INT);
				if (in == null
						|| (in.intValue() != Integer
								.parseInt(UPnPConstants.V_INT))) {
					control.log("SERVER: " + UPnPConstants.N_INT + " " + si
							+ " value is not ok: " + in);
					state = false;
				}
			}
		}
		return state;
	}

	private void createExportTest() {
		control.log("Start export test");
		Hashtable tagHash = new Hashtable();
		Hashtable servsHash = new Hashtable();
		try {
			control.log("Prepare to start my Control Point");
			cp = new ControlPoint();
			control.log("Create MSearch package and send it");
			DatagramPacket search = cp.createMSearch();
			cp.send(search);
			int count = 0;
			while (cp.received == null) {
				count++;
				Thread.sleep(50);
				if (count == 1000) {
					control.log("No answer is received to an MSearch message");
					break;
				}
			}
			control.log("Parsing received Discovery Response xml");
			String toParse = cp.received;
			parser = new XMLParser(toParse);
			XMLTag rootTag = parser.getRootXMLTag();
			if (!rootTag.hasOnlyTags()) {
				throw new Exception("Root tag has something else except tags");
			}
			Vector content = rootTag.getContent();
			int len = content.size();
			for (int i = 0; i < len; i++) {
				XMLTag tag = (XMLTag) content.elementAt(i);
				if (tag.getName().equalsIgnoreCase("device")) {
					Vector elem = tag.getContent();
					for (int j = 0; j < elem.size(); j++) {
						XMLTag tagDev = (XMLTag) elem.elementAt(j);
						Vector vv = tagDev.getContent();
						if (tagDev.getName().equalsIgnoreCase("serviceList")) {
							Vector servs = tagDev.getContent();
							for (int k = 0; k < servs.size(); k++) {
								XMLTag tagServ = (XMLTag) servs.elementAt(k);
								if (tagServ.getName().equalsIgnoreCase(
										"service")) {
									Vector sss = tagServ.getContent();
									for (int p = 0; p < sss.size(); p++) {
										XMLTag tagSSS = (XMLTag) sss
												.elementAt(p);
										Vector sps = tagSSS.getContent();
										if (tagSSS.hasOnlyText()
												&& sps.size() > 0) {
											servsHash.put(tagSSS.getName(), sps
													.elementAt(0));
										}
									}
								}
							}
						}
						if (tagDev.hasOnlyText() && vv.size() > 0) {
							tagHash.put(tagDev.getName(), vv.elementAt(0));
						}
					}
				}
				if (tag.hasOnlyText()) {
					URL_BASE = tag.getContent().elementAt(0).toString();
				}
			}
			if (testProps(tagHash)) {
				control.log("Properties of the exported device are OK");
			}
			takeServs(servsHash);
			String url = URL_BASE + SCPD_URL;
			control.log("Get SCPD xml from discovery message");
			String xml = cp.getXML(url);
			String action = null;
			Hashtable names = parseSCPD_XML(xml); // action names
			if (names.containsKey("testALL")) {
				action = "testALL";
			}
			String controlURL = null;
			if (CONTROL_URL.toLowerCase().startsWith("http")) {
				controlURL = CONTROL_URL;
			}
			else {
				controlURL = URL_BASE + CONTROL_URL;
			}
			URL contrlURL = new URL(controlURL);
			String host = contrlURL.getHost();
			int port = contrlURL.getPort();
			String[] args = changeType((Vector) names.get(action));
			control.log("Control Server is created and started");
			cps = new ControlServer(host, port, control);
			String[] values = new String[] {UPnPConstants.V_OUT_STRING,
					UPnPConstants.V_OUT_BOOLEAN, UPnPConstants.V_OUT_NUMBER,
					UPnPConstants.V_OUT_INT, UPnPConstants.V_OUT_CHAR,
					UPnPConstants.V_OUT_FLOAT};
			control.log("Creating Post message and send it");
			byte[] post = cp.createPOST(CONTROL_URL, host, port, SERVICE_TYPE,
					action, args, values);
			Thread th = new Thread(cps, "CPS");
			th.start();
			cps.send(post);
			while (!cps.isFinished) {
				Thread.sleep(100);
				if (cps.isError) {
					break;
				}
			}
			eventExportTest();
			cp.interrupt();
			cps.finish();
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	
	
	
	
	private void eventExportTest() {
//		try {
//			control.log("Starting event test of Export");
//			String event = null;
//			if (EVENT_SUB_URL.toLowerCase().startsWith("http")) {
//				event = EVENT_SUB_URL;
//			}
//			else {
//				event = URL_BASE + EVENT_SUB_URL;
//			}
//			URL eventURL = new URL(event);
//			String evHost = eventURL.getHost();
//			int evPort = eventURL.getPort();
//			
//			control.log("Create and start Eventing Server");			
//			EventServer evs = new EventServer(evHost, evPort, bc, control);
//			int pr = evPort + 1;
//			String callback = "http://" + evHost + ":" + pr + "/";
//			control.log("Create subscribe message and send it");
//			
//			// pkr; added to have the same path URL
//			String match = (udn + SERVICE_ID).replace(':', '-');
//			
//			byte[] subscr = cp.createSUBSCRIBE(/*EVENT_SUB_URL*/ match, evHost, evPort,
//					callback, 60);
//			Thread th = new Thread(evs, "EVS");
//			th.start();
//			evs.send(subscr);
//			
//			control.log("Create table with events");
//			Hashtable events = new Hashtable(6);
//			events.put(UPnPConstants.N_IN_STRING, UPnPConstants.E_STRING);
//			events.put(UPnPConstants.N_IN_BOOLEAN, UPnPConstants.E_BOOLEAN);
//			events.put(UPnPConstants.N_OUT_NUMBER, UPnPConstants.E_NUMBER);
//			events.put(UPnPConstants.N_OUT_INT, UPnPConstants.E_INT);
//			events.put(UPnPConstants.N_OUT_CHAR, UPnPConstants.E_CHAR);
//			events.put(UPnPConstants.N_OUT_FLOAT, UPnPConstants.E_FLOAT);
//			int count = 0;
//			while (evs.getListener() == null) {
//				count++;
//				Thread.sleep(1000);
//				if (count == 1000) {
//					control.log("No UPnPEventListener is found");
//					break;
//				}
//			}			
//			control.log("Starting ServerSocket to listen for Notify msg");
//			SSNotify not = new SSNotify(pr, control);
//			not.start();
//			control.log("Notify UPnPEventListener service");
//			evs.getListener().notifyUPnPEvent(udn, SERVICE_ID, events);
//			evs.finish();				
//			while (!not.isFinished) {
//				Thread.sleep(100);
//				if (cps.isError) {
//					break;
//				}
//			}
//			not.finish();
//		}
//		catch (Exception er) {
//			er.printStackTrace();
//		}
	}

	private String[] changeType(Vector args) {
		String[] names = new String[6];
		for (int i = 0; i < args.size(); i++) {
			names[i] = args.elementAt(i).toString();
		}
		return names;
	}

	private Hashtable parseSCPD_XML(String str) throws Exception {
		Hashtable hash = new Hashtable();
		//    System.out.println("SCPD: " + str);
		XMLParser parser = new XMLParser(str);
		XMLTag rootTag = parser.getRootXMLTag();
		if (!rootTag.hasOnlyTags()) {
			throw new Exception(
					"SCPD XML:Root tag has something else except tags");
		}
		Vector content = rootTag.getContent();
		int len = content.size();
		for (int i = 0; i < len; i++) {
			XMLTag tag = (XMLTag) content.elementAt(i);
			if (tag.getName().equalsIgnoreCase("actionList")) {
				Vector acts = tag.getContent();
				for (int j = 0; j < acts.size(); j++) {
					XMLTag act = (XMLTag) acts.elementAt(j);
					if (act.getName().equalsIgnoreCase("action")) {
						Vector names = act.getContent();
						for (int k = 0; k < names.size(); k++) {
							XMLTag name = (XMLTag) names.elementAt(k);
							if (name.getName().equalsIgnoreCase("name")) {
								argsNames = new Vector();
								hash.put(name.getContent().elementAt(0)
										.toString(), argsNames);
							}
							else
								if (name.getName().equalsIgnoreCase(
										"argumentList")) {
									Vector args = name.getContent();
									for (int p = 0; p < args.size(); p++) {
										XMLTag arg = (XMLTag) args.elementAt(p);
										if (arg.getName().equalsIgnoreCase(
												"argument")) {
											Vector argsName = arg.getContent();
											String argname = null;
											for (int y = 0; y < argsName.size(); y++) {
												XMLTag xxx = (XMLTag) argsName
														.elementAt(y);
												if (xxx.hasOnlyText()
														&& xxx
																.getName()
																.equalsIgnoreCase(
																		"name")) {
													argname = xxx.getContent()
															.elementAt(0)
															.toString();
												}
												else
													if (xxx.hasOnlyText()
															&& xxx
																	.getName()
																	.equalsIgnoreCase(
																			"direction")) {
														String dir = xxx
																.getContent()
																.elementAt(0)
																.toString();
														if (dir.equals("in")) {
															argsNames
																	.addElement(argname);
															argname = null;
														}
													}
											}
										}
									}
								}
						}
					}
				}
			}
		}
		return hash;
	}

	private void takeServs(Dictionary dic) {
		String test = dic.get("serviceType").toString();
		SERVICE_TYPE = test;
		test = dic.get("serviceId").toString();
		SERVICE_ID = test;
		test = dic.get("SCPDURL").toString();
		SCPD_URL = test;
		test = dic.get("controlURL").toString();
		CONTROL_URL = test;
		test = dic.get("eventSubURL").toString();
		EVENT_SUB_URL = test;
	}

	private boolean testProps(Dictionary dic) {
		boolean state = true;
		String test = dic.get("friendlyName").toString();
		control.log("Exported device FRIENDLY_NAME: " + test);
		if (!test.equals(UPnPConstants.E_FN)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " FRINDLY_NAME not matched");
			state = false;
		}
		test = dic.get("manufacturer").toString();
		control.log("Exported device MANUFACTURER: " + test);
		if (!test.equals(UPnPConstants.E_MANU)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " MANUFACTURER not matched");
			state = false;
		}
		test = dic.get("manufacturerURL").toString();
		control.log("Exported device MANUFACTURER_URL: " + test);
		if (!test.equals(UPnPConstants.E_MANU_URL)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " MANUFACTURER_URL not matched");
			state = false;
		}
		test = dic.get("modelName").toString();
		control.log("Exported device MODEL_NAME: " + test);
		if (!test.equals(UPnPConstants.E_MOD_NAME)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " MODEL_NAME not matched");
			state = false;
		}
		test = dic.get("modelNumber").toString();
		control.log("Exported device MODEL_NUMBER: " + test);
		if (!test.equals(UPnPConstants.E_MOD_NUMB)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " MODEL_NUMBER not matched");
			state = false;
		}
		test = dic.get("modelURL").toString();
		control.log("Exported device MODEL_URL: " + test);
		if (!test.equals(UPnPConstants.E_MOD_URL)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " MODEL_URL not matched");
			state = false;
		}
		test = dic.get("serialNumber").toString();
		control.log("Exported device SERIAL_NUMBER: " + test);
		if (!test.equals(UPnPConstants.E_SN)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " SERIAL_NUMBER not matched");
			state = false;
		}
		test = dic.get("UPC").toString();
		control.log("Exported device UPC: " + test);
		if (!test.equals(UPnPConstants.E_UPC)) {
			control.log("EXPORTED: " + udn + UPnPConstants.DD
					+ " UPC not matched");
			state = false;
		}
		return state;
	}

	private void waitFor() {
		try {
			synchronized (waiter) {
				waiter.wait();
			}
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void notifyUPnPEvent(String did, String sid, Dictionary vals) {
		if (!did.equals(udn) || !sid.equals(currentServ)) {
			control.log("SERVER: Received Event for UDN: " + did + " & SID: "
					+ sid + ".\r\nExpected UDN: " + udn + " SID: "
					+ currentServ);
			return;
		}
		vector.addElement(vals);
		if (ender-- == 0) {
			synchronized (waiter) {
				waiter.notify();
			}
		}
	}
}