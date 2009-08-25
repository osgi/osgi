package org.osgi.test.cases.upnp.tbc.device.description;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.upnp.tbc.UPnPConstants;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 */
public class DescriptionInvoker {
	private final Hashtable	hash;

	public DescriptionInvoker() {
		hash = new Hashtable();
	}

	public void response(String serviceType, String actionName,
			Dictionary args, boolean mposted, HttpServletResponse res)
			throws IOException {
		hash.clear();
		DefaultTestBundleControl.log("Incoming invoke request for Action: "
				+ actionName);
		if (actionName.equals(UPnPConstants.ACT_POS)) {
			postOutSucc(serviceType, actionName, args, res);
		}
		else
			if (actionName.equals(UPnPConstants.ACT_MPOS)) {
				if (!mposted) {
					sendError(res, 405, UPnPConstants.ERR_MNA);
					return;
				}
				postOutSucc(serviceType, actionName, args, res);
			}
			else
				if (actionName.equals(UPnPConstants.ACT_PIS)) {
					postInSucc(serviceType, actionName, args, res);
				}
				else
					if (actionName.equals(UPnPConstants.ACT_MPIS)) {
						if (!mposted) {
							sendError(res, 405, UPnPConstants.ERR_MNA);
							return;
						}
						postInSucc(serviceType, actionName, args, res);
					}
					else
						if (actionName.equals(UPnPConstants.ACT_PF)) {
							postFail(serviceType, actionName, args, res);
						}
						else
							if (actionName.equals(UPnPConstants.ACT_MPF)) {
								if (!mposted) {
									sendError(res, 405, UPnPConstants.ERR_MNA);
									return;
								}
								postFail(serviceType, actionName, args, res);
							}
							else
								if (actionName.equals(UPnPConstants.ACT_PB)) {
									postBlock(serviceType, actionName, args,
											res);
								}
								else
									if (actionName
											.equals(UPnPConstants.ACT_MPB)) {
										if (!mposted) {
											sendError(res, 405,
													UPnPConstants.ERR_MNA);
											return;
										}
										postBlock(serviceType, actionName,
												args, res);
									}
									else
										if (actionName
												.equals(UPnPConstants.ACT_QSV)) {
											postQuery(serviceType, actionName,
													args, res);
										}
	}

	private void postOutSucc(String serv, String act, Dictionary params,
			HttpServletResponse res) throws IOException {
		hash.put(UPnPConstants.N_OUT_INT, UPnPConstants.V_OUT_INT);
		hash.put(UPnPConstants.N_OUT_UI4, UPnPConstants.V_OUT_UI4);
		hash.put(UPnPConstants.N_OUT_NUMBER, UPnPConstants.V_OUT_NUMBER);
		hash.put(UPnPConstants.N_OUT_FLOAT, UPnPConstants.V_OUT_FLOAT);
		hash.put(UPnPConstants.N_OUT_CHAR, UPnPConstants.V_OUT_CHAR);
		hash.put(UPnPConstants.N_OUT_STRING, UPnPConstants.V_OUT_STRING);
		hash.put(UPnPConstants.N_OUT_BOOLEAN, UPnPConstants.V_OUT_BOOLEAN);
		try {
			hash.put(UPnPConstants.N_OUT_HEX, Hex
					.encode(UPnPConstants.V_OUT_HEX.getBytes()));
		}
		catch (Exception exc) {
			hash.put(UPnPConstants.N_OUT_HEX, UPnPConstants.V_OUT_HEX);
		}
		sendOK(serv, act, hash, res);
	}

	private void postInSucc(String serv, String act, Dictionary params,
			HttpServletResponse res) throws IOException {
		hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_OK);
		String cur = (String) params.get(UPnPConstants.N_IN_INT);
		if (!cur.equals(UPnPConstants.V_IN_INT)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_INT);
		}
		cur = (String) params.get(UPnPConstants.N_IN_UI4);
		if (!cur.equals(UPnPConstants.V_IN_UI4)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_UI4);
		}
		cur = (String) params.get(UPnPConstants.N_IN_NUMBER);
		if (!cur.equals(UPnPConstants.V_IN_NUMBER)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_NUMBER);
		}
		cur = (String) params.get(UPnPConstants.N_IN_FLOAT);
		if (!cur.equals(UPnPConstants.V_IN_FLOAT)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_FLOAT);
		}
		cur = (String) params.get(UPnPConstants.N_IN_CHAR);
		if (!cur.equals(UPnPConstants.V_IN_CHAR)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_CHAR);
		}
		cur = (String) params.get(UPnPConstants.N_IN_STRING);
		if (!cur.equals(UPnPConstants.V_IN_STRING)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_STRING);
		}
		cur = (String) params.get(UPnPConstants.N_IN_BOOLEAN);
		if (!cur.equals(UPnPConstants.V_IN_BOOLEAN)) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_BOOLEAN);
		}
		cur = (String) params.get(UPnPConstants.N_IN_HEX);
		try {
			String str = new String(Hex.decode(cur));
			if (!str.equals(UPnPConstants.V_IN_HEX)) {
				hash.put(UPnPConstants.N_OUT_OUT,
						UPnPConstants.V_OUT_OUT_FAILED + " "
								+ UPnPConstants.N_IN_HEX);
			}
		}
		catch (Exception exc) {
			hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_FAILED
					+ " " + UPnPConstants.N_IN_HEX);
		}
		sendOK(serv, act, hash, res);
	}

	private void postBlock(String serv, String act, Dictionary params,
			HttpServletResponse res) throws IOException {
		try {
			Thread.sleep(40000L);
		}
		catch (InterruptedException exc) {
			exc.printStackTrace();
		}
		hash.put(UPnPConstants.N_OUT_OUT, UPnPConstants.V_OUT_OUT_OK);
		sendOK(serv, act, hash, res);
	}

	private void postQuery(String serv, String act, Dictionary params,
			HttpServletResponse res) {
		// empty
	}

	private void postFail(String serv, String act, Dictionary params,
			HttpServletResponse res) throws IOException {
		sendError(res, 500, UPnPConstants.ERR_ISE);
	}

	public void sendOK(String serv, String act, Dictionary params,
			HttpServletResponse res) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(UPnPConstants.ENV_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_XMLNS);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_S);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.BODY_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ULB);
		sb.append(act);
		sb.append(UPnPConstants.RESP_ST);
		sb.append(serv);
		sb.append("\"");
		sb.append(UPnPConstants.RB);
		sb.append(UPnPConstants.CRLF);
		Enumeration keys = params.keys();
		while (keys.hasMoreElements()) {
			Object obj = keys.nextElement();
			Object val = params.get(obj);
			sb.append(UPnPConstants.LB);
			sb.append(obj);
			sb.append(UPnPConstants.RB);
			sb.append(val);
			sb.append(UPnPConstants.LCB);
			sb.append(obj);
			sb.append(UPnPConstants.RB);
			sb.append(UPnPConstants.CRLF);
		}
		sb.append(UPnPConstants.UCLB);
		sb.append(act);
		sb.append(UPnPConstants.RESP_END);
		sb.append(UPnPConstants.RB);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.BODY_END);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_END);
		String send = sb.toString();
		res.setHeader(UPnPConstants.HOK, "");
		res.setHeader(UPnPConstants.N_CL, String
				.valueOf(send.getBytes().length));
		res.setHeader(UPnPConstants.N_CT, "text/xml; charset=\"utf-8\"");
		res.setHeader(UPnPConstants.N_EXT, "");
		res.setHeader(UPnPConstants.N_SERVER, UPnPConstants.V_SERVER);
		res.getOutputStream().write(send.getBytes());
		//    res.flushBuffer();
	}

	public void sendError(HttpServletResponse res, int errCode, String errDesc)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(UPnPConstants.ENV_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_XMLNS);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_S);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.BODY_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.FAULT_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.FAULT_CODE);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.FAULT_STRING);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.DETAIL_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.UPNPERROR_ST);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.LB);
		sb.append(UPnPConstants.ERR_CODE);
		sb.append(UPnPConstants.RB);
		sb.append(String.valueOf(errCode));
		sb.append(UPnPConstants.LCB);
		sb.append(UPnPConstants.ERR_CODE);
		sb.append(UPnPConstants.RB);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.LB);
		sb.append(UPnPConstants.ERR_DESC);
		sb.append(UPnPConstants.RB);
		sb.append(errDesc);
		sb.append(UPnPConstants.LCB);
		sb.append(UPnPConstants.ERR_DESC);
		sb.append(UPnPConstants.RB);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.UPNPERROR_END);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.DETAIL_END);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.FAULT_END);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.BODY_END);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.ENV_END);
		String cont = sb.toString();
		//    res.setHeader(UPnPConstants.ERROR, "");
		res.setStatus(errCode);
		res.setHeader(UPnPConstants.N_CL, String
				.valueOf(cont.getBytes().length));
		res.setHeader(UPnPConstants.N_CT, "text/xml; charset=\"utf-8\"");
		res.setHeader(UPnPConstants.N_EXT, "");
		res.setHeader(UPnPConstants.N_SERVER, UPnPConstants.V_SERVER);
		//    res.sendError(errCode, null);
		res.getOutputStream().write(cont.getBytes());
		//    res.flushBuffer();
	}
}