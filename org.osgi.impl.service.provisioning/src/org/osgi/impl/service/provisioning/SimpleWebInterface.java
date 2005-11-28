package org.osgi.impl.service.provisioning;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.ZipInputStream;
import org.osgi.service.provisioning.ProvisioningService;

/**
 * A very simple web interface to use with InitialProvisioning.
 * <p>
 * Since InitialProvisioning cannot really depend on the HttpService being
 * present, we basically implement our own here. Note, this is only for a
 * reference implementation. The two big things are missing. First, user
 * authentication or possibly just a check that the requests are coming from a
 * local interface needs to be done. And second, we need to turn off after
 * provisioning is "through" and turn back on when needed.
 * 
 * @author breed@almaden.ibm.com
 */
public class SimpleWebInterface extends Thread {
	/**
	 * The ProvisioningService. Since there should only be one
	 * ProvisioningService we can use a static variable.
	 */
	static ProvisioningService	svc;
	/**
	 * The Log of events in the ProvisiongService. Since there should only be
	 * one ProvisioningService we can use a static variable
	 */
	static ProvisioningLog		log;
	/**
	 * The ServerSocket requests come in on. Since there should only be one
	 * ProvisioningService we can use a static variable
	 */
	static ServerSocket			ss;

	/**
	 * Constructs a Simple Provisioning Web interface for the
	 * ProvisioningService.
	 * 
	 * @param port The port to listen on.
	 * @param dict The ProvisioningDictionary.
	 * @param log The object to log events to.
	 */
	public SimpleWebInterface(int port, ProvisioningService svc,
			ProvisioningLog log) throws IOException {
		ss = new ServerSocket(port);
		SimpleWebInterface.svc = svc;
		SimpleWebInterface.log = log;
		start();
	}

	/**
	 * To avoid creating another class. We use this constructor to launch
	 * another thread to accept the next connection.
	 */
	private SimpleWebInterface() {
		start();
	}

	/** Read the header into a String and return it. */
	static private String eatHeader(InputStream is) throws IOException {
		int c;
		boolean lastNl = false;
		// Did we just see an \n? (\r's don't count...)
		byte head[] = new byte[1024];
		int offset = 0;
		outer: while ((c = is.read()) != -1) {
			if (offset < head.length)
				head[offset++] = (byte) c;
			switch (c) {
				case '\n' :
					if (lastNl)
						break outer;
					lastNl = true;
					break;
				case '\r' :
					break;
				default :
					lastNl = false;
			}
		}
		return new String(head, 0, offset);
	}

	/**
	 * Prints out a form to submit an initial provisioning URL. It also lists
	 * the Provisioning Event log.
	 */
	static private void doForm(OutputStream os) throws IOException {
		PrintStream ps = new PrintStream(os);
		ps.println("HTTP/1.0 200 OK\r\nContent-Type: text/html\r\n\r");
		ps.println("<form method=\"POST\" action=\"/\">");
		ps.println("<b>Provisioning URL </b><input name=\"url\">");
		ps.println("<input type=\"submit\" value=\"Retrieve\">");
		ps.println("</form>");
		ps
				.println("<form method=\"POST\" enctype=\"multipart/form-data\" action=\"/\">");
		ps
				.println("<b>Provisioning Upload </b><input type=\"file\" name=\"file\">");
		ps.println("<input type=\"submit\" value=\"Upload\">");
		ps.println("</form>");
		ps.println("<hr><pre>");
		Enumeration en = log.getLog();
		for (int i = 0; i < 100 && en.hasMoreElements(); i++) {
			ps.println(":" + en.nextElement());
		}
		ps.println("</pre>");
	}

	/* Convert a nibble to a char. */
	static private int getnum(int b) {
		if (b >= '0' && b <= '9')
			return b - '0';
		if (b >= 'a' && b <= 'f')
			return b - 'a' + 0xa;
		if (b >= 'A' && b <= 'F')
			return b - 'A' + 0xa;
		return 0xf;
	}

	static byte	redirectBytes[]	= "HTTP/1.0 302 Redirect to form\r\nContent-Type: text/html\r\nLocation: /\r\n\r\nGo to <a href=\"/\">here</a>\r\n"
										.getBytes();

	static class SimpleMIME extends InputStream {
		InputStream	is;
		byte		boundary[]	= null;
		int			matched		= 0;
		int			pushedBack	= -1;
		int			nextMatched	= 0;
		boolean		atEof		= false;

		SimpleMIME(InputStream is) {
			this.is = is;
		}

		public int read() throws IOException {
			if (atEof)
				return -1;
			int c;
			if (boundary == null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// Find the boundary
				do {
					c = is.read();
				} while (c != -1 && c != '-');
				if (c == -1) {
					atEof = true;
					return -1;
				}
				// Read the boundary
				do {
					baos.write(c);
					c = is.read();
				} while (c != -1 && c != '\n');
				if (c == -1) {
					atEof = true;
					return -1;
				}
				// Skip the header
				boolean lastNL = false;
				out: do {
					c = is.read();
					switch (c) {
						case '\r' :
							break;
						case '\n' :
							if (lastNL)
								break out;
							lastNL = true;
							break;
						default :
							lastNL = false;
					}
				} while (c != -1);
				if (c == -1) {
					atEof = true;
					return -1;
				}
				// All ready to read
				boundary = baos.toByteArray();
			}
			if (matched != 0) {
				c = boundary[nextMatched];
				matched++;
				if (matched == nextMatched) {
					matched = 0;
					nextMatched = 0;
				}
				return c;
			}
			if (pushedBack != -1) {
				c = pushedBack;
				pushedBack = -1;
				return c;
			}
			c = is.read();
			if (c == -1) {
				atEof = true;
				return -1;
			}
			if (c == '\n') {
				int d;
				do {
					d = is.read();
					if (d == boundary[matched]) {
						matched++;
					}
					else
						break;
				} while (d != -1 && matched != boundary.length);
				if (matched == boundary.length || d == -1) {
					atEof = true;
					return -1;
				}
				pushedBack = d;
			}
			return c;
		}
	}

	/** Process the form submissing, then put a new form up. */
	static private void processForm(InputStream is, OutputStream os)
			throws IOException {
		byte buffer[] = new byte[1024];
		int rc, offset = 0;
		int state = 0;
		String header = eatHeader(is);
		final String lengthTag = "\ncontent-length:";
		final String contentTypeTag = "\ncontent-type: multipart/form-data";
		/* Get the length of the body */
		try {
			header = header.toLowerCase();
			int i = header.indexOf(lengthTag);
			if (header.indexOf(contentTypeTag) == -1) {
				if (i == -1)
					return;
				int count = 0;
				try {
					String number = header.substring(lengthTag.length() + i);
					i = number.indexOf('\n');
					if (i != -1)
						number = number.substring(0, i);
					number = number.trim();
					count = Integer.parseInt(number);
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				}
				/* Read in the body unescaping things */
				while (count-- > 0 && (rc = is.read()) != -1) {
					switch (state) {
						case 0 :
							if (rc != '%')
								buffer[offset++] = (byte) rc;
							else
								state = 1;
							break;
						case 1 : /* 1st char of an escaped byte */
							buffer[offset] = (byte) (getnum(rc) << 4);
							state = 2;
							break;
						case 2 :
							buffer[offset++] += getnum(rc);
							state = 0;
					}
				}
				/*
				 * Now that everything is parsed out, kick off the provisioning
				 * cycle.
				 */
				Hashtable newinfo = new Hashtable();
				newinfo.put(ProvisioningService.PROVISIONING_REFERENCE,
						new String(buffer, 4, offset - 4));
				/* Note: we start at 4 to skip the url= */
				svc.addInformation(newinfo);
			}
			else {
				try {
					log.log("Processing upload");
					svc.addInformation(new ZipInputStream(new SimpleMIME(is)));
					log.log("Finished upload");
				}
				catch (Exception e) {
					log.log("Error processing upload");
				}
			}
		}
		finally {
			os.write(redirectBytes);
		}
	}

	/* Accept a request and process the request in the same thread. */
	public void run() {
		try {
			Socket s = ss.accept();
			/* Kick off a new thread to get the next request */
			new SimpleWebInterface();
			InputStream is = s.getInputStream();
			int c = is.read();
			switch (c) {
				case 'G' :
					eatHeader(is);
					doForm(s.getOutputStream());
					break;
				case 'P' :
					processForm(is, s.getOutputStream());
					break;
			}
			s.close();
		}
		catch (Exception e) {
			if ( ss != null )
				e.printStackTrace();
		}
	}

	void shutdown() {
		
		try {
			ServerSocket sss = ss;
			ss = null;
			sss.close();
			/*
			 * It seems we can't return too quickly because the socket may not
			 * be completely closed..
			 */
			try {
				Thread.sleep(5000);
			}
			catch (Exception e) {
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
