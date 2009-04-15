package org.osgi.test.cases.upnp.tbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.osgi.test.cases.upnp.tbc.parser.XMLParser;
import org.osgi.test.cases.upnp.tbc.parser.XMLTag;

/**
 * 
 * 
 * @version 1.1
 */
public class UPnPTester extends Assert implements UPnPEventListener {
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
	private String						SCPD_URL		= null;
	private String						CONTROL_URL		= null;
	public static final int				DISCOVERY		= 1;
	public static final int				DESCRIPTION		= 2;
	public static final int				CONTROL			= 3;
	public static final int				EVENTING		= 4;
	public static final int				EXPORT			= 5;

	public UPnPTester(int state,
			UPnPDevice dev, BundleContext bc) {
		this.dev = dev;
		this.bc = bc;
		waiter = new Object();
		vector = new Vector();
		assertNotNull("UPnPDevice is NULL", dev);
		udn = (String) dev.getDescriptions(null).get(UPnPDevice.UDN);
		switch (state) {
			case DISCOVERY : {
				createDiscoveryTest();
				break;
			}
			case DESCRIPTION : {
				fail("no DESCRIPTION test");
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
				fail("NO SUCH TEST STEP IN UPnP");
			}
		}
	}

	private void createDiscoveryTest() {
		try {
			Dictionary dict = dev.getDescriptions(null);
			String test = (String) dict.get(UPnPDevice.UPC);
			log("UPnPDevice UPC: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " UPC not matched", UPnPConstants.V_UPC, test);
			test = (String) dict.get(UPnPDevice.FRIENDLY_NAME);
			log("UPnPDevice FRIENDLY_NAME: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Friendly name not matched", UPnPConstants.V_FN, test);
			test = (String) dict.get(UPnPDevice.MANUFACTURER);
			log("UPnPDevice MANUFACTURER: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Manufacturer not matched", UPnPConstants.V_MANU, test);
			test = (String) dict.get(UPnPDevice.MANUFACTURER_URL);
			log("UPnPDevice MANUFACTURER_URL: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Manufacturer URL not matched",
					UPnPConstants.V_MANU_URL, test);
			test = (String) dict.get(UPnPDevice.MODEL_DESCRIPTION);
			log("UPnPDevice MODEL_DESCRIPTION: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Model Description not matched",
					UPnPConstants.V_MOD_DESC, test);
			test = (String) dict.get(UPnPDevice.MODEL_NAME);
			log("UPnPDevice MODEL_NAME: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Model Name not matched", UPnPConstants.V_MOD_NAME, test);
			test = (String) dict.get(UPnPDevice.MODEL_NUMBER);
			log("UPnPDevice MODEL_NUMBER: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Model Number not matched", UPnPConstants.V_MOD_NUMB,
					test);
			test = (String) dict.get(UPnPDevice.MODEL_URL);
			log("UPnPDevice MODEL_URL: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Model URL not matched", UPnPConstants.V_MOD_URL, test);
			test = (String) dict.get(UPnPDevice.SERIAL_NUMBER);
			log("UPnPDevice SERIAL_NUMBER: " + test);
			assertEquals("SERVER: " + udn + UPnPConstants.DD
					+ " Serial Number not matched", UPnPConstants.V_SN, test);
			UPnPIcon[] icos = dev.getIcons("En");
			if (icos.length == 2) {
				for (int i = 0; i < icos.length; i++) {
					InputStream fis = null;
					if (icos[i].getHeight() == 16) {
						if (icos[i].getWidth() != 16) {
							fail("SERVER: " + udn + UPnPConstants.DD
									+ " Not property width for icon: " + i);
						}
						fis = UPnPTester.class
								.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/images/small.gif");
					}
					else
						if (icos[i].getHeight() == 32) {
							if (icos[i].getWidth() != 32) {
								fail("SERVER: " + udn + UPnPConstants.DD
										+ " Not property width for icon: " + i);
							}
							fis = UPnPTester.class
									.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/images/big.gif");
						}
						else {
							fail("SERVER: " + udn + UPnPConstants.DD
									+ " Not property height for icon: " + i);
						}
					if (icos[i].getDepth() != 8) {
						fail("SERVER: " + udn + UPnPConstants.DD
								+ " Not property width for icon: " + i);
					}
					if (!icos[i].getMimeType().equals(UPnPConstants.V_MT)) {
						fail("SERVER: " + udn + UPnPConstants.DD
								+ " Not property mime type for icon: " + i);
					}
					if (fis != null) {
						try {
							InputStream ricon = icos[i].getInputStream();
							byte[] bfis = readFully(fis);
							byte[] bricon = readFully(ricon);
							if (!checkArrays(bfis, bricon)) {
								fail("SERVER: " + udn + UPnPConstants.DD
										+ " Icons are not the same: " + i);
							}
							else {
								log("Compared icons are the same.Icon test is OK");
							}
							ricon.close();
							fis.close();
						}
						catch (Exception exc) {
							fail("SERVER: " + udn + UPnPConstants.DD
									+ " Exception :" + exc + " while icon: "
									+ i, exc);
						}
					}
					else {
						fail("SERVER: " + udn + UPnPConstants.DD
								+ " Unable to get file for icon: " + i);
					}
				}
			}
			else {
				fail("SERVER: " + udn + UPnPConstants.DD
						+ " Wrong Number of Icons");
			}
		}
		catch (Exception er) {
			fail("unexpected exception", er);
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
		log("Invoking an action Post Fail");
		try {
			act.invoke(hash);
			fail("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_PF);
		}
		catch (Exception exc) {
			log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_MPF);
		log("Invoking an action MPost Fail");
		try {
			act.invoke(hash);
			fail("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_MPF);
		}
		catch (Exception exc) {
			//exc.printStackTrace();
			log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_PB);
		log("Invoking an action Post Block.Please wait");
		try {
			act.invoke(hash);
			fail("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_PB);
		}
		catch (Exception exc) {
			log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_MPB);
		log("Invoking an action MPost Block.Please wait");
		try {
			act.invoke(hash);
			fail("SERVER: Exception not thrown while invoking: "
					+ UPnPConstants.ACT_MPB);
		}
		catch (Exception exc) {
			log("Exception is thrown OK");
		}
		act = cs.getAction(UPnPConstants.ACT_POS);
		log("Invoking an action Post Out Success");
		try {
			Dictionary dict = act.invoke(hash);
			checkRet(dict);
		}
		catch (Exception exc) {
			fail("Unexpected Exception", exc);
		}
		act = cs.getAction(UPnPConstants.ACT_MPOS);
		log("Invoking an action MPost Out Success");
		try {
			Dictionary dict = act.invoke(hash);
			checkRet(dict);
		}
		catch (Exception exc) {
			fail("Unexpected Exception", exc);
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
		log("Invoking an action Post In Success with invalid arguments");
		try {
			act.invoke(hash);
			fail("SERVER: "
							+ UPnPConstants.ACT_PIS
							+ " Invoke was successful: The UPnP Driver doesn't check action arguments for validity");
		}
		catch (Exception exc) {
			log("Exception is thrown OK");
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
		log("Invoking an action Post In Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (dict != null) {
				String res = (String) dict.get(UPnPConstants.N_OUT_OUT);
				assertEquals("SERVER: " + UPnPConstants.ACT_PIS
						+ " Invoke Failed: " + res, UPnPConstants.V_OUT_OUT_OK,
						res);
			}
		}
		catch (Exception exc) {
			fail("SERVER: " + UPnPConstants.ACT_PIS
					+ " Invoke Failed: Threre was an exception", exc);
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
		log("Invoking an action MPost In Success");
		try {
			Dictionary dict = act.invoke(hash);
			if (dict != null) {
				String res = (String) dict.get(UPnPConstants.N_OUT_OUT);
				assertEquals("SERVER: " + UPnPConstants.ACT_MPIS
						+ " Invoking Failed: " + res,
						UPnPConstants.V_OUT_OUT_OK, res);
			}
		}
		catch (Exception exc) {
			fail("Unexpected Exception", exc);
		}
	}

	/*
	 * 
	 *  
	 */
	private void checkRet(Dictionary dict) {
		assertNotNull(dict);
		String st = new String((byte[]) dict.get(UPnPConstants.N_OUT_HEX));
		assertEquals("SERVER: " + UPnPConstants.N_OUT_HEX
				+ " value is not ok: " + st, UPnPConstants.V_OUT_HEX, st);
		Integer inint = (Integer) dict.get(UPnPConstants.N_OUT_INT);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_INT
				+ " value is not ok: " + inint, new Integer(
				UPnPConstants.V_OUT_INT), inint);
		Long lo = (Long) dict.get(UPnPConstants.N_OUT_UI4);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_UI4
				+ " value is not ok: " + lo, new Long(UPnPConstants.V_OUT_UI4),
				lo);
		Float fl = (Float) dict.get(UPnPConstants.N_OUT_FLOAT);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_FLOAT
				+ " value is not ok: " + fl, new Float(
				UPnPConstants.V_OUT_FLOAT), fl);
		Double dl = (Double) dict.get(UPnPConstants.N_OUT_NUMBER);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_NUMBER
				+ " value is not ok: " + dl, new Double(
				UPnPConstants.V_OUT_NUMBER), dl);
		Character ch = (Character) dict.get(UPnPConstants.N_OUT_CHAR);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_CHAR
				+ " value is not ok: " + ch, new Character(
				UPnPConstants.V_OUT_CHAR.charAt(0)), ch);
		String str = (String) dict.get(UPnPConstants.N_OUT_STRING);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_STRING
				+ " value is not ok: " + str, UPnPConstants.V_OUT_STRING, str);
		Boolean bool = (Boolean) dict.get(UPnPConstants.N_OUT_BOOLEAN);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_BOOLEAN
				+ " value is not ok: " + bool, new Boolean(
				UPnPConstants.V_OUT_BOOLEAN), bool);
	}

	private void createEventTest() {
		Hashtable hash = new Hashtable();
		UPnPService es;
		ServiceRegistration sr;
		log("Device gets service urn:prosyst-com:serviceId:EventService");
		es = dev.getService(UPnPConstants.SEV_ID);
		currentServ = es.getId();
		try {
			filter = bc.createFilter("(&(" + UPnPDevice.ID + "=" + udn + ")("
					+ UPnPService.ID + "=" + UPnPConstants.SEV_ID + "))");
		}
		catch (InvalidSyntaxException exc) {
			fail("unexpected exception", exc);
		}
		hash.put(UPnPEventListener.UPNP_FILTER, filter);
		hash.put("upnptest", "true");
		ender = 8;
		log("Register UPnPEventListener to listen for events");
		sr = bc.registerService(UPnPEventListener.class.getName(), this, hash);
		waitFor();
		//    sr.unregister();
		checkEvents();
		sr.unregister();
		vector.removeAllElements();
	}

	private void checkEvents() {
		assertEquals("SERVER: Wrong number of events", 9, vector.size());
		int evSize = vector.size();
		for (int i = 0; i < evSize; i++) {
			Dictionary dict = (Dictionary) vector.elementAt(i);
			testEvent(dict, dict.size());
		}
	}

	private void testEvent(Dictionary dict, int si) {
		assertNotNull(dict);
		switch (si) {
			case 9 : {
				String str1 = (String) dict.get(UPnPConstants.N_OUT);
				assertEquals("SERVER: " + UPnPConstants.N_OUT + " " + si
						+ " value is not ok: " + str1, UPnPConstants.V_OUT,
						str1);
			}
			case 8 : {
				String st = new String((byte[]) dict.get(UPnPConstants.N_HEX));
				assertEquals("SERVER: " + UPnPConstants.N_HEX + " " + si
						+ " value is not ok: " + st, UPnPConstants.V_HEX, st);
			}
			case 7 : {
				Boolean bool = (Boolean) dict.get(UPnPConstants.N_BOOLEAN);
				assertEquals("SERVER: " + UPnPConstants.N_BOOLEAN + " " + si
						+ " value is not ok: " + bool, new Boolean(
						UPnPConstants.V_BOOLEAN), bool);
			}
			case 6 : {
				String str = (String) dict.get(UPnPConstants.N_STRING);
				assertEquals("SERVER: " + UPnPConstants.N_STRING + " " + si
						+ " value is not ok: " + str, UPnPConstants.V_STRING,
						str);
			}
			case 5 : {
				Character ch = (Character) dict.get(UPnPConstants.N_CHAR);
				assertEquals("SERVER: " + UPnPConstants.N_CHAR + " " + si
						+ " value is not ok: " + ch, new Character(
						UPnPConstants.V_CHAR.charAt(0)), ch);
			}
			case 4 : {
				Float fl = (Float) dict.get(UPnPConstants.N_FLOAT);
				assertEquals("SERVER: " + UPnPConstants.N_FLOAT + " " + si
						+ " value is not ok: " + fl, new Float(
						UPnPConstants.V_FLOAT), fl);
			}
			case 3 : {
				Double dl = (Double) dict.get(UPnPConstants.N_NUMBER);
				assertEquals("SERVER: " + UPnPConstants.N_NUMBER + " " + si
						+ " value is not ok: " + dl, new Double(
						UPnPConstants.V_NUMBER), dl);
			}
			case 2 : {
				Long lo = (Long) dict.get(UPnPConstants.N_UI4);
				assertEquals("SERVER: " + UPnPConstants.N_UI4 + " " + si
						+ " value is not ok: " + lo, new Long(
						UPnPConstants.V_UI4), lo);
			}
			case 1 : {
				Integer in = (Integer) dict.get(UPnPConstants.N_INT);
				assertEquals("SERVER: " + UPnPConstants.N_INT + " " + si
						+ " value is not ok: " + in, new Integer(
						UPnPConstants.V_INT), in);
			}
		}
	}

	private void createExportTest() {
		log("Start export test");
		Hashtable tagHash = new Hashtable();
		Hashtable servsHash = new Hashtable();
		try {
			log("Prepare to start my Control Point");
			cp = new ControlPoint();
			cp.start();
			log("Create MSearch package and send it");
			DatagramPacket search = cp.createMSearch();
			cp.send(search);
			int count = 0;
			while (cp.received == null) {
				count++;
				Thread.sleep(50);
				if (count == 1000) {
					fail("No answer is received to an MSearch message");
				}
			}
			log("Parsing received Discovery Response xml");
			String toParse = cp.received;
			XMLParser parser = new XMLParser(toParse);
			XMLTag rootTag = parser.getRootXMLTag();
			if (!rootTag.hasOnlyTags()) {
				fail("Root tag has something else except tags");
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
			testProps(tagHash);
			log("Properties of the exported device are OK");
			takeServs(servsHash);
			String url = URL_BASE + SCPD_URL;
			log("Get SCPD xml from discovery message");
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
			log("Control Server is created and started");
			cps = new ControlServer(host, port);
			String[] values = new String[] {UPnPConstants.V_OUT_STRING,
					UPnPConstants.V_OUT_BOOLEAN, UPnPConstants.V_OUT_NUMBER,
					UPnPConstants.V_OUT_INT, UPnPConstants.V_OUT_CHAR,
					UPnPConstants.V_OUT_FLOAT};
			log("Creating Post message and send it");
			byte[] post = cp.createPOST(CONTROL_URL, host, port, SERVICE_TYPE,
					action, args, values);
			Thread th = new Thread(cps, "CPS");
			th.start();
			cps.send(post);
			while (!cps.isFinished) {
				Thread.sleep(100);
			}
			cps.checkAns();
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
		// log("Starting event test of Export");
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
		// log("Create and start Eventing Server");
//			EventServer evs = new EventServer(evHost, evPort, bc, control);
//			int pr = evPort + 1;
//			String callback = "http://" + evHost + ":" + pr + "/";
		// log("Create subscribe message and send it");
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
		// log("Create table with events");
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
		// log("No UPnPEventListener is found");
//					break;
//				}
//			}			
		// log("Starting ServerSocket to listen for Notify msg");
//			SSNotify not = new SSNotify(pr, control);
//			not.start();
		// log("Notify UPnPEventListener service");
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
			fail("SCPD XML:Root tag has something else except tags");
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
		test = dic.get("SCPDURL").toString();
		SCPD_URL = test;
		test = dic.get("controlURL").toString();
		CONTROL_URL = test;
		test = dic.get("eventSubURL").toString();
	}

	private void testProps(Dictionary dic) {
		assertNotNull(dic);
		String test = dic.get("friendlyName").toString();
		log("Exported device FRIENDLY_NAME: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " FRINDLY_NAME not matched", UPnPConstants.E_FN, test);
		test = dic.get("manufacturer").toString();
		log("Exported device MANUFACTURER: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " MANUFACTURER not matched", UPnPConstants.E_MANU, test);
		test = dic.get("manufacturerURL").toString();
		log("Exported device MANUFACTURER_URL: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " MANUFACTURER_URL not matched", UPnPConstants.E_MANU_URL,
				test);
		test = dic.get("modelName").toString();
		log("Exported device MODEL_NAME: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " MODEL_NAME not matched", UPnPConstants.E_MOD_NAME, test);
		test = dic.get("modelNumber").toString();
		log("Exported device MODEL_NUMBER: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " MODEL_NUMBER not matched", UPnPConstants.E_MOD_NUMB, test);
		test = dic.get("modelURL").toString();
		log("Exported device MODEL_URL: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " MODEL_URL not matched", UPnPConstants.E_MOD_URL, test);
		test = dic.get("serialNumber").toString();
		log("Exported device SERIAL_NUMBER: " + test);
		assertEquals("EXPORTED: " + udn + UPnPConstants.DD
				+ " SERIAL_NUMBER not matched", UPnPConstants.E_SN, test);
		test = dic.get("UPC").toString();
		log("Exported device UPC: " + test);
		assertEquals(
				"EXPORTED: " + udn + UPnPConstants.DD + " UPC not matched",
				UPnPConstants.E_UPC, test);
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
			log("SERVER: Received Event for UDN: " + did + " & SID: "
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
	
	/**
	 * Fail with cause t.
	 * 
	 * @param message Failure message.
	 * @param t Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}
	
	private static void log(String message) {
		UPnPControl.log(message);
	}
}