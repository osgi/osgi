package org.osgi.impl.service.upnp.cd.event;

import java.io.*;
import java.util.*;

// This class is used for parsing the HttpRequest. This class parses the request Line, http 
// headers,and http body. This class accepts a buffered input stream and parses the request.
// This class is not a generic http parser, but is designed for UPnP http parsing . This class
// handles requests like post,mpost, msearch,subscribe,unsubscribe .
public class RequestProcessor {
	private BufferedInputStream	in;
	private String				line;
	public String				reqMethod;
	public Hashtable			headers;
	public String				charSet;
	public String				contentBody	= null;
	public String				nameSpace;
	public String				errorMessage;
	public String				reqUriPath;

	// Constructor which accepts the input stream from the client and
	// initializes all the
	// variables.
	public RequestProcessor(BufferedInputStream in) {
		this.in = in;
		headers = new Hashtable();
	}

	// function used for separating the given string into multiple tokens and
	// returns
	// a string array containing all the tokens.
	private String[] splitStr(String str) {
		StringTokenizer st = new StringTokenizer(str);
		int n = st.countTokens();
		String[] strs = new String[n];
		for (int i = 0; i < n; ++i) {
			strs[i] = st.nextToken();
		}
		return strs;
	}

	// This method parses the given input stream and retrieves all the values
	// from the input
	// stream and stores in appropriate variables. Stores all the header values
	// in headers
	// table, stores all the cookies in the reqCookies table.
	public int parseRequest() throws Exception {
		byte[] lineBytes = new byte[4096];
		int len;
		try {
			line = getLine();
			if (line == null || line.length() == 0) {
				setErrorMessage("Bad Request");
				return 400;
			}
			String[] tokens = splitStr(line);
			if (tokens[0].equals(GenaConstants.GENA_SERVER_VERSION)) {
				if (!tokens[1].equals("200")) {
					return Integer.parseInt(tokens[1].trim());
				}
			}
			extractHeaders();
			reqMethod = tokens[0];
			reqUriPath = tokens[1];
			if (reqMethod.equals("POST") || reqMethod.equals("M-POST")) {
				String content_type = (String) headers.get("content-type");
				int result = checkContentHeaders(content_type);
				if (result != 200) {
					return result;
				}
				result = extractContentBody();
				if (result != 200) {
					return result;
				}
			}
			setCharset();
			if (reqMethod.equals("M-POST")) {
				setNamespace();
			}
			return 200;
		}
		catch (IOException e) {
			setErrorMessage("Bad Request");
			return 400;
		}
	}

	// This method extracts the content body which is been send with the
	// request.
	private int extractContentBody() {
		int contentLength = Integer.parseInt((String) headers
				.get("content-length"));
		if (contentLength <= 0) {
			setErrorMessage("Length Required");
			return 411;
		}
		byte abyte0[] = new byte[contentLength];
		int i2 = 0;
		for (int l1 = 0; l1 < contentLength; l1 += i2) {
			try {
				if ((in.available() < contentLength)
						|| (in.available() > contentLength)) {
					setErrorMessage("Length Required");
					return 411;
				}
				else {
					i2 = in.read(abyte0, l1, contentLength - l1);
				}
			}
			catch (Exception e) {
			}
			if (i2 == -1) {
				setErrorMessage("Length Required");
				return 411;
			}
		}
		String s = new String(abyte0);
		contentBody = s;
		return 200;
	}

	// This method checks whether request has valid content type header and
	// content-length header
	private int checkContentHeaders(String content_type) {
		if (content_type == null) {
			setErrorMessage("PreCondition Failed");
			return 412;
		}
		if (!content_type.equals("text/xml; charset=\"utf-8\"")) {
			setErrorMessage("Unsupported Media Type");
			return 415;
		}
		if (headers.get("content-length") == null) {
			setErrorMessage("PreCondition Failed");
			return 412;
		}
		return 200;
	}

	// This method extracts all the headers from the request and stores it in a
	// table (headers).
	private void extractHeaders() throws Exception {
		while (true) {
			line = getLine();
			if (line == null || line.length() == 0) {
				break;
			}
			int colonBlank = line.indexOf(": ");
			if (colonBlank != -1) {
				String name = line.substring(0, colonBlank);
				String value = line.substring(colonBlank + 2);
				headers.put(name.toLowerCase(), value);
			}
		}
	}

	// This function extracts the namespace header from man header.
	public void setNamespace() {
		String man = (String) headers.get("man");
		int result;
		if (man != null) {
			result = man.indexOf("ns");
			if (result != -1) {
				nameSpace = man.substring((result + "ns".length() + 1)).trim();
			}
		}
	}

	// This function extracts the charactor set from the conent-type header and
	// stores in a local variable
	public void setCharset() {
		String content_type = (String) headers.get("content-type");
		int result;
		if (content_type != null) {
			result = content_type.indexOf("charset");
			if (result != -1) {
				charSet = content_type.substring(
						(result + "charset".length() + 1)).trim();
			}
		}
	}

	// Returns a line from the input stream as a string. When end of the stream
	// is reached,
	// returns null.
	private String getLine() throws IOException {
		int val = in.read();
		if (val == -1) {
			return null;
		}
		StringBuffer stringbuffer = new StringBuffer();
		for (; val != -1 && val != 13 && val != 10; val = in.read()) {
			stringbuffer.append((char) val);
		}
		if (val == 13) {
			in.read();
		}
		return stringbuffer.toString();
	}

	// This method sets the error message if the request is not a valid one.
	void setErrorMessage(String message) {
		errorMessage = message;
	}
}
