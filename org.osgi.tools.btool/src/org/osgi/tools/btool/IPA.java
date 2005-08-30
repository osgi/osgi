/*
 * Created on Jul 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.osgi.tools.btool;

import java.io.*;
import java.util.StringTokenizer;

/**
 * @author Peter Kriens
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class IPA {
	BTool	btool;
	String	ipa;

	IPA(BTool btool, String ipa) {
		this.btool = btool;
		this.ipa = ipa;
	}

	static String[]		MIME_TYPES		= {"text/plain;charset=utf-8",
			"text/x-osgi-bundle-url;charset=utf-8", "application/octet-stream",
			"application/x-osgi-bundle",};
	public final int	IPA_TEXT		= 0;
	public final int	IPA_BUNDLE_URL	= 1;
	public final int	IPA_BINARY		= 2;
	public final int	IPA_BUNDLE		= 3;

	void ipa(String type, String name, String parameter) throws IOException {
		int intType = -1;
		if (type.equals("bundle"))
			intType = IPA_BUNDLE;
		else
			if (type.equals("bundle_url"))
				intType = IPA_BUNDLE_URL;
			else
				if (type.equals("bundle-url"))
					intType = IPA_BUNDLE_URL;
				else
					if (type.equals("text"))
						intType = IPA_TEXT;
					else
						if (type.equals("binary"))
							intType = IPA_BINARY;
						else
							throw new IllegalArgumentException(
									"-ipa requires bundle, bundle_url, text, or binary as type: "
											+ type);
		switch (intType) {
			case IPA_TEXT :
			case IPA_BUNDLE_URL :
				entryText(name, parameter, MIME_TYPES[intType]);
				break;
			case IPA_BINARY :
			case IPA_BUNDLE :
				entryFile(name, parameter, MIME_TYPES[intType]);
		}
	}

	void entryText(String name, String content, String extra)
			throws IOException {
		Resource r = new BytesResource(btool, name, content.getBytes("UTF-8"));
		r.setExtra(extra.getBytes("UTF-8"));
		btool.addContents(r);
	}

	void entryFile(String name, String content, String extra)
			throws IOException {
		Resource r = new Resource(btool, btool.project, name);
		r.setSourcePath(content);
		r.setExtra(extra.getBytes("UTF-8"));
		btool.addContents(r);
	}

	static String toUnicode(String fromInput) {
		int index = fromInput.indexOf("\\u");
		StringBuffer sb = new StringBuffer(fromInput.length());
		int last = 0;
		while (index >= 0 && (index + 6) < fromInput.length()) {
			sb.append(fromInput.substring(last, index));
			String nr = fromInput.substring(index + 2, index + 6);
			sb.append((char) Integer.parseInt(nr, 16));
			last = index + 6;
			index = fromInput.indexOf("\\u", last);
		}
		if (last == 0)
			return fromInput;
		sb.append(fromInput.substring(last));
		return sb.toString();
	}

	/**
	 *  
	 */
	public void execute() throws Exception {
		StringTokenizer st = new StringTokenizer(ipa, ",");
		while (st.hasMoreTokens()) {
			String entry = st.nextToken().trim();
			btool.trace("IPA Entry '" + entry + "'");
			if (entry.length() > 0) {
				if (entry.startsWith("[") && entry.endsWith("]")) {
					entry = entry.substring(1, entry.length() - 1);
					StringTokenizer st2 = new StringTokenizer(entry, " \t");
					String name = st2.nextToken();
					String content = st2.nextToken();
					String extra = st2.nextToken();
					entryText(name, content, extra);
				}
				else {
					StringTokenizer st2 = new StringTokenizer(entry, " \t");
					String type = st2.nextToken();
					String name = st2.nextToken();
					String parm = st2.nextToken();
					ipa(type, name, parm);
				}
			}
			else
				System.err.println("Illegal format in ipa " + ipa + " ("
						+ entry + ")");
		}
	}
}