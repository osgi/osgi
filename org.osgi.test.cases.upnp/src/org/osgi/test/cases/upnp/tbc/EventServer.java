package org.osgi.test.cases.upnp.tbc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 */
public class EventServer implements Runnable {
	private Socket						socket;
	private InetAddress					address;
	private OutputStream				os;
	private InputStream					is;
	private BundleContext				bc;
	private DefaultTestBundleControl	control;
	private ServiceReference			listRef;
	private UPnPEventListener			eventList;

	public EventServer(String host, int port, BundleContext bc,
			DefaultTestBundleControl control) throws Exception {
		address = InetAddress.getByName(host);
		socket = new Socket(address, port);
		this.bc = bc;
		this.control = control;
	}

	public void send(byte[] bytes) {
		try {
			os = socket.getOutputStream();
			os.write(bytes);
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void run() {
		try {
			is = socket.getInputStream();
			Thread.sleep(5000);
			if (is.available() < 0) {
				return;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int rd = is.read(bytes);
			while (rd != -1) {
				baos.write(bytes, 0, rd);
				rd = is.read(bytes);
			}
			String ans = new String(baos.toByteArray());
			BufferedReader br = new BufferedReader(new StringReader(ans));
			String ll = br.readLine();
			if (ll != null) {
				if (ll.equals("HTTP/1.1 200 OK")) {
					control
							.log("HTTP/1.1 200 OK msg as reply to subsribe msg is received");
					ServiceReference[] listRefs = bc
							.getServiceReferences(UPnPEventListener.class.getName(),null);
					for ( int i=0; listRefs!=null && i<listRefs.length; i++ ) {
						String s = listRefs[i].getProperty("upnp.filter") + "";
						if ( s.indexOf("Tester") >= 0 ) {
							listRef = listRefs[i];
							eventList = (UPnPEventListener) bc.getService(listRef);							
							break;
						}
					}
				}
			}
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void finish() throws Exception {
		socket.close();
		os.close();
		is.close();
	}

	public UPnPEventListener getListener() {
		return eventList;
	}
}