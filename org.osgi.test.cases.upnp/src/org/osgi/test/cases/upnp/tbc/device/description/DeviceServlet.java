package org.osgi.test.cases.upnp.tbc.device.description;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.upnp.tbc.UPnPConstants;
import org.osgi.test.cases.upnp.tbc.device.event.EventSender;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 */
public class DeviceServlet extends HttpServlet {
	private static final long			serialVersionUID	= 1L;
	private final DescriptionInvoker	invoker;
	private final Hashtable				evs;
	private final StringBuffer			root;
	private static long					sids	= 0;

	public DeviceServlet() throws Exception {
		invoker = new DescriptionInvoker();
		evs = new Hashtable();
		root = new StringBuffer();
		addContent(root, "/org/osgi/test/cases/upnp/tbc/resources/dd1.xml");
		root.append("  <URLBase>http://");
		root.append(UPnPConstants.LOCAL_HOST);
		root.append(UPnPConstants.DD);
		root.append(UPnPConstants.HTTP_PORT);
		root.append("</URLBase>\r\n");
		addContent(root, "/org/osgi/test/cases/upnp/tbc/resources/dd2.xml");
		root.append("    <UDN>");
		root.append(UPnPConstants.UDN_ROOT);
		root.append("</UDN>\r\n");
		addContent(root, "/org/osgi/test/cases/upnp/tbc/resources/dd3.xml");
		root.append("        <UDN>");
		root.append(UPnPConstants.UDN_EMB1);
		root.append("</UDN>");
		addContent(root, "/org/osgi/test/cases/upnp/tbc/resources/dd4.xml");
		root.append("        <UDN>");
		root.append(UPnPConstants.UDN_EMB2);
		root.append("</UDN>");
		addContent(root, "/org/osgi/test/cases/upnp/tbc/resources/dd5.xml");
		addHash(evs, "c");
		addHash(evs, "e");
		addHash(evs, "s");
		addHash(evs, "r");
		addHash(evs, "u");
	}

	private void addHash(Hashtable to, String el) {
		for (int i = 1; i < 4; i++) {
			Hashtable hash = new Hashtable();
			to.put((i + el).intern(), hash);
		}
	}

	private void addContent(StringBuffer sb, String file) throws IOException {
		InputStream fis = DeviceServlet.class.getResourceAsStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] arr = new byte[1024];
		int size = fis.read(arr);
		while (size >= 0) {
			baos.write(arr, 0, size);
			size = fis.read(arr);
		}
		sb.append(baos.toString());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String xml = req.getParameter("xml");
		String icon = req.getParameter("icon");
		String uri = req.getRequestURI();
		if ((uri.equals(UPnPConstants.SR_DESC)) && (xml != null)) {
			if (xml.equals("root")) {
				res.setContentType("text/xml");
				OutputStream out = res.getOutputStream();
				out.write(root.toString().getBytes());
				out.close();
			}
			else {
				InputStream in = DeviceServlet.class
						.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/"
								+ xml);
				byte[] bytes = new byte[1024];
				res.setContentType("text/xml");
				OutputStream out = res.getOutputStream();
				int size = in.read(bytes);
				while (size >= 0) {
					out.write(bytes, 0, size);
					size = in.read(bytes);
				}
				out.close();
				in.close();
			}
		}
		else
			if ((uri.equals(UPnPConstants.SR_IM)) && (icon != null)) {
				InputStream in = DeviceServlet.class
						.getResourceAsStream("/org/osgi/test/cases/upnp/tbc/resources/images/"
								+ icon);
				byte[] bytes = new byte[1024];
				res.setContentType("image/gif");
				OutputStream out = res.getOutputStream();
				int size = in.read(bytes);
				while (size >= 0) {
					out.write(bytes, 0, size);
					size = in.read(bytes);
				}
				out.close();
				in.close();
			}
			else {
				super.doGet(req, res);
			}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		DefaultTestBundleControl.log("{DeviceServlet} - Incoming POST request");
		String uri = req.getRequestURI();
		if (uri.equals(UPnPConstants.SR_CON)) {
			String dsi = req.getParameter(UPnPConstants.DSI);
			String soap = req.getHeader(UPnPConstants.N_SOAPACTION);
			String body = getBody(req);
			replyControl(dsi, soap, body, res, false);
		}
	}

	public void doMPost(HttpServletRequest req, HttpServletResponse res) {
		DefaultTestBundleControl
				.log("{DeviceServlet} - Incoming M-POST request");
		//// try {
		//// Object xx = null;
		//// Enumeration en = req.getHeaderNames();
		//// for (xx = en.nextElement(); en.hasMoreElements(); xx =
		// en.nextElement()) {
		//// //#warning DUMP
		//// System.out.println(xx + "\t" + req.getHeader((String)xx));
		//// }
		//// }
		//// catch (Exception e) {
		//// e.printStackTrace();
		//// }
		String uri = req.getRequestURI();
		//// //#warning DUMP
		//// System.out.println(uri);
		try {
			if (uri.equals(UPnPConstants.SR_CON)) {
				String dsi = req.getParameter(UPnPConstants.DSI);
				String man = req.getHeader(UPnPConstants.N_MAN);
				if (man == null || !man.startsWith(UPnPConstants.V_MAN)) {
					genError(res, 500, UPnPConstants.ERR_NM);
				}
				String ns = man.substring(man.indexOf(UPnPConstants.V_NS) + 3)
						.trim();
				String soap = req.getHeader(ns + "-"
						+ UPnPConstants.N_SOAPACTION);
				String body = getBody(req);
				replyControl(dsi, soap, body, res, true);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private String getBody(HttpServletRequest req) throws IOException {
		ServletInputStream sis = req.getInputStream();
		byte[] arr = new byte[req.getContentLength()];
		int si = sis.read(arr);
		return new String(arr, 0, si);
	}

	private void genError(HttpServletResponse httpservletresponse, int i,
			String s) throws IOException {
		invoker.sendError(httpservletresponse, i, s);
	}

	private Dictionary getActionArguments(String xml, String actionName) {
		String env_string = extractStringT(xml, ":Envelope", ">");
		if (env_string.length() == 0) {
			DefaultTestBundleControl
					.log("Unable to find <Envelope> tag in action XML");
			return null;
		}
		String env_ns = extractStringT(env_string, "xmlns:", "=");
		if (env_ns.length() == 0) {
			DefaultTestBundleControl
					.log("Unable to find xmlns definition for <Envelope> tag in action XML");
			return null;
		}
		String body = extractStringT(xml, "<" + env_ns + ":Body>", "</"
				+ env_ns + ":Body>");
		if (body.length() == 0) {
			DefaultTestBundleControl
					.log("Unable to find <Body> tag in action XML");
			return null;
		}
		String act_ns = extractStringT(body, "<", ":" + actionName);
		if (act_ns.length() == 0) {
			DefaultTestBundleControl.log("Unable to find the XML NS for <"
					+ actionName
					+ "> or the tag it self in action XML");
			return null;
		}
		String act_xmlns = extractStringT(body, actionName, ">");
		String arguments = extractStringT(body, ("<" + act_ns + ":"
				+ actionName + act_xmlns + ">"), ("</" + act_ns + ":"
				+ actionName + ">"));
		if (arguments.length() == 0) {
			DefaultTestBundleControl
					.log("There are no argumets in the invoke XML, although that is a possible scenario the testcase has no actions with no arguments");
			return null;
		}
		Dictionary a = parseArgs(arguments);
		if (a == null) {
			DefaultTestBundleControl.log("Unable to parse the " + actionName
					+ " arguments");
		}
		return a;
	}

	private Hashtable parseArgs(String xml) {
		Hashtable args = new Hashtable(5);
		StringTokenizer st = new StringTokenizer(xml, "\r\n");
		while (st.hasMoreTokens()) {
			String row = st.nextToken();
			String name = extractStringT(row, "<", ">");
			String value = extractStringT(xml, "<" + name + ">", "</" + name
					+ ">");
			if (name.length() > 0 && value.length() > 0) {
				args.put(name, value);
			}
		}
		if (args.size() > 0) {
			return args;
		}
		return null;
	}

	public static String extractStringT(String whole, String beg, String end) {
		int start = whole.indexOf(beg);
		int stop = whole.indexOf(end, start + beg.length());
		String substring;
		if ((start > -1) && (stop > start)) {
			substring = whole.substring(start + beg.length(), stop);
		}
		else
			return "";
		return substring;
	}

	//// private String removeSoapDefined (String body) throws IOException {
	//// System.out.println("removeSoapDefined\tbody:\r\n" + body);
	//// BufferedReader br = new BufferedReader( new StringReader( body ) );
	//// StringBuffer sb = new StringBuffer();
	////
	//// String ll = null;
	//// while((ll = br.readLine()) != null) {
	//// if(!ll.trim().startsWith("<?")) {
	//// break;
	//// }
	//// }
	//// if (ll == null || !ll.trim().substring(0, ll.indexOf("
	// ")).equals(UPnPConstants.ENV_ST)) {
	//// return null;
	//// }
	////
	//// ll = br.readLine();
	//// if (ll == null || !ll.trim().equals(UPnPConstants.BODY_ST)) {
	//// return null;
	//// }
	////
	//// boolean flag = true;
	//// while ( (ll = br.readLine() ) != null) {
	//// if ( ll.trim().equals(UPnPConstants.BODY_END) ) {
	//// flag = false;
	//// break;
	//// }
	//// sb.append( ll + UPnPConstants.CRLF );
	//// }
	////
	////
	//// if( flag ) {
	//// return null;
	//// }
	////
	//// ll = br.readLine();
	//// if ( ll == null || !ll.trim().equals(UPnPConstants.ENV_END) ) {
	//// return null;
	//// } else {
	//// return sb.toString();
	//// }
	//// }
	////
	//// private Dictionary getActionArguments (String body, String serv,
	// String act) throws IOException {
	//// Hashtable hash = new Hashtable();
	//// BufferedReader br = new BufferedReader (new StringReader( body ));
	//// String ll = br.readLine();
	//// if(ll == null || !ll.trim().equals("<u:" + act + " xmlns:u=\"" + serv
	// + "\">")) {
	//// return null;
	//// }
	////
	//// boolean flag = true;
	//// while( (ll = br.readLine()) != null ) {
	//// ll = ll.trim();
	////
	//// if( ll.equals("</u:" + act + UPnPConstants.RB) ) {
	//// flag = false;
	//// break;
	//// }
	////
	//// String var = ll.substring(ll.indexOf(UPnPConstants.LB) + 1,
	// ll.indexOf(UPnPConstants.RB));
	//// if(!ll.startsWith(UPnPConstants.LB + var + UPnPConstants.RB) ||
	// !ll.endsWith(UPnPConstants.LCB + var + UPnPConstants.RB)) {
	//// break;
	//// }
	//// String val = ll.substring(("<" + var + ">").length(), ll.indexOf("</"
	// + var + ">"));
	//// hash.put(var, val);
	//// }
	////
	//// if( flag ) {
	//// return null;
	//// } else {
	//// return hash;
	//// }
	//// }
	private void replyControl(String dsi, String soap, String body,
			HttpServletResponse res, boolean mposted) throws IOException {
		int in = 0;
		String serviceType = soap.substring(1, in = soap.indexOf("#"));
		String actionName = soap.substring(in + 1, soap.length() - 1);
		Dictionary args = getActionArguments(body, actionName);
		if (args == null) {
			DefaultTestBundleControl
					.log("Error while parsing invoke XML for action "
					.concat(actionName));
			genError(res, 500, UPnPConstants.ERR_WB);
			return;
		}
		invoker.response(serviceType, actionName, args, mposted, res);
	}

	public synchronized void doSubscribe(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		DefaultTestBundleControl
				.log("{DeviceServlet} - Incoming SUBSCRIBE request");
		String uri = req.getRequestURI();
		if (uri == null || !uri.equals(UPnPConstants.SR_EV)) {
			res.sendError(400, "Must access event url");
			return;
		}
		String sid = req.getHeader(UPnPConstants.N_SID);
		String nt = req.getHeader(UPnPConstants.N_NT);
		String callback = req.getHeader(UPnPConstants.N_CALLBACK);
		try {
			if (sid != null && (nt != null || callback != null)) {
				res.sendError(400, UPnPConstants.ERR_INCHEAD);
				return;
			}
			String dsi = req.getParameter(UPnPConstants.DSI);
			long time = 1800;
			try {
				time = Long.parseLong(req.getHeader(UPnPConstants.N_TIMEOUT)
						.substring(UPnPConstants.V_SEC.length()));
			}
			catch (NumberFormatException nfe) {
				//ignore alredy has a default value
			}
			boolean eventing = false;
			URL url = null;
			if (sid != null) {
				if (sid.length() == 0) {
					res.sendError(412, UPnPConstants.ERR_MSID);
					return;
				}
				Hashtable rdev = (Hashtable) evs.get(dsi);
				Object[] arr = (Object[]) rdev.get(sid);
				if (arr == null) {
					res.sendError(412, UPnPConstants.ERR_ISID);
					return;
				}
				long ended = ((Long) arr[1]).longValue();
				if (ended < System.currentTimeMillis()) {
					rdev.remove(sid);
					res.sendError(412, UPnPConstants.ERR_ISID);
					return;
				}
				if (dsi.indexOf("r") > 0) {
					res.sendError(500, UPnPConstants.ERR_UAR);
					return;
				}
				arr[1] = new Long(System.currentTimeMillis() + (time * 1000));
				rdev.put(sid, arr);
			}
			else {
				if (nt == null || nt != null && !nt.equals(UPnPConstants.V_NT)) {
					res.sendError(412, UPnPConstants.ERR_INT);
					return;
				}
				if (callback == null) {
					res.sendError(412, UPnPConstants.ERR_MICB);
					return;
				}
				try {
					url = new URL(callback.substring(1, callback.length() - 1));
				}
				catch (MalformedURLException mue) {
					DefaultTestBundleControl.log("Call Back URL: " + callback
							+ " is invalid");
					res.sendError(412, UPnPConstants.ERR_MICB);
					return;
				}
				synchronized (DeviceServlet.class) {
					sid = "uuid:" + dsi + sids++;
				}
				if (dsi.indexOf("s") > 0) {
					res.sendError(500, UPnPConstants.ERR_UAS);
					Thread th = new Thread(new EventSender(sid, url),
							"EVENT SENDER #0");
					th.start();
					return;
				}
				else
					if (dsi.indexOf("r") > 0) {
						time = 2;
					}
				Object[] obj = new Object[2];
				obj[0] = url;
				obj[1] = new Long(System.currentTimeMillis() + (time * 1000));
				Hashtable rdev = (Hashtable) evs.get(dsi);
				rdev.put(sid, obj);
				eventing = true;
			}
			res.setStatus(200);
			res.setHeader(UPnPConstants.N_DATE, (new Date()).toString());
			res.setHeader(UPnPConstants.N_SERVER, UPnPConstants.V_SERVER);
			res.setHeader(UPnPConstants.N_SID, sid);
			res.setHeader(UPnPConstants.N_TIMEOUT, UPnPConstants.V_SEC + time);
			if (eventing) {
				Thread th = new Thread(new EventSender(sid, url),
						"EVENT SENDER");
				th.start();
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
			res.sendError(500, exc.toString());
		}
	}

	public synchronized void doUnsubscribe(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		//    logger.log("{DeviceServlet} - Incoming UNSUBSCRIBE request");
		String uri = req.getRequestURI();
		if (uri == null || !uri.equals(UPnPConstants.SR_EV)) {
			res.sendError(400, "Must access event url");
			return;
		}
		String sid = req.getHeader(UPnPConstants.N_SID);
		String nt = req.getHeader(UPnPConstants.N_NT);
		String callback = req.getHeader(UPnPConstants.N_CALLBACK);
		try {
			if (sid != null && (nt != null || callback != null)) {
				res.sendError(400, UPnPConstants.ERR_INCHEAD);
				return;
			}
			String dsi = req.getParameter(UPnPConstants.DSI);
			if (sid == null || sid.length() == 0) {
				res.sendError(412, UPnPConstants.ERR_MSID);
				return;
			}
			Hashtable rdev = (Hashtable) evs.get(dsi);
			Object[] arr = (Object[]) rdev.get(sid);
			if (arr == null) {
				res.sendError(412, UPnPConstants.ERR_ISID);
				return;
			}
			if (dsi.indexOf("u") >= 0) {
				res.sendError(500, UPnPConstants.ERR_UAU);
				return;
			}
			rdev.remove(sid);
			res.setStatus(200);
		}
		catch (Exception exc) {
			exc.printStackTrace();
			res.sendError(500, exc.toString());
		}
	}

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String method = req.getMethod();
		if (method.equals(UPnPConstants.SUBSCRIBE)) {
			doSubscribe(req, res);
		}
		else
			if (method.equals(UPnPConstants.UNSUBSCRIBE)) {
				doUnsubscribe(req, res);
			}
			else
				if (method.equals(UPnPConstants.MPOST)) {
					doMPost(req, res);
				}
				else {
					super.service(req, res);
				}
	}
}
