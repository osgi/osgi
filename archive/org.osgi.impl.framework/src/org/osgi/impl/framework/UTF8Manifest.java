package org.osgi.impl.framework;

import java.io.*;
import java.util.Vector;
import java.util.jar.JarFile;

public class UTF8Manifest {
	HeaderDictionary	hd			= new HeaderDictionary();
	int					_section;
	Vector				_duplicates	= new Vector();

	public UTF8Manifest(JarFile jf) throws IOException {
		parse(new InputStreamReader(jf.getInputStream(jf
				.getEntry(JarFile.MANIFEST_NAME)), "UTF-8"));
	}

	public void put(String header, String value) {
		if (hd.get(header) != null) {
			if (!header.equalsIgnoreCase("comment")) {
				_duplicates.addElement(header + ":" + value);
			}
		}
		hd.put(header, value);
	}

	void parse(Reader in) throws IOException {
		BufferedReader rdr = new BufferedReader(in);
		String current = " ";
		String buffer = rdr.readLine();
		_section = 0;
		if (buffer != null && !buffer.startsWith("Manifest-Version")) {
			System.err
					.println("The first line of a manifest file must be the Manifest-Version attribute");
			throw new IOException(
					"The first line of a manifest file must be the Manifest-Version attribute");
		}
		while (buffer != null && current != null && _section == 0) {
			if (current.startsWith(" ")) {
				buffer += current.substring(1);
			}
			else {
				_section += entry(buffer);
				buffer = current;
			}
			current = rdr.readLine();
		}
		entry(buffer);
	}

	int entry(String line) throws IOException {
		if (line.length() < 2)
			return 1;
		int colon = line.indexOf(':');
		if (colon < 1)
			error("Invalid header '" + line + "'");
		String header = line.substring(0, colon).toLowerCase();
		String alphanum = "abcdefghijklmnopqrstuvwxyz0123456789";
		String set = alphanum;
		if (alphanum.indexOf(header.charAt(0)) < 0)
			error("Header does not start with alphanum: " + header);
		for (int i = 0; i < header.length(); i++) {
			if (set.indexOf(header.charAt(i)) < 0)
				error("Header contains non alphanum, - _: " + header);
			set = "_-" + alphanum;
		}
		String value = "";
		if (colon + 2 < line.length())
			value = line.substring(colon + 2);
		else
			error("No value for manifest header " + header);
		if (_section == 0) {
			put(header, value);
		}
		return 0;
	}

	void error(String msg) throws IOException {
		throw new IOException("Reading manifest: " + msg);
	}

	public String get(String key) {
		return (String) hd.get(key);
	}

	public HeaderDictionary getHeaderDictionary() {
		return new HeaderDictionary(hd);
	}
}
