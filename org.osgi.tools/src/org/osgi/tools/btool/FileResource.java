package org.osgi.tools.btool;

import java.io.*;
import java.util.Date;

public class FileResource extends Resource {
	File	file;
	boolean	preprocess;

	FileResource(BTool btool, String path, File file, boolean preprocess) {
		super(btool, null, path);
		this.file = file;
		this.preprocess = preprocess;
	}

	InputStream getInputStream() throws IOException {
		if (!preprocess)
			return super.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF8"));
		parse(br, pw, 500);
		pw.print("\r\n");
		pw.close();
		out.close();
		return new ByteArrayInputStream(out.toByteArray());
	}

	void parse(BufferedReader rdr, PrintWriter pw, int linewidth)
			throws IOException {
		String line = rdr.readLine();
		while (line != null) {
			line = process(line);
			StringBuffer sb = new StringBuffer();
			if (line != null && line.length() > 0) {
				int n = 0;
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					switch (c) {
						case '\n' :
						case '\r' :
							n = 0;
						default :
							if (n > linewidth) {
								sb.append("\r\n ");
								n = 1;
							}
							sb.append(c);
							n++;
					}
				}
				String s = sb.toString();
				pw.print(s + "\r\n");
				if (btool.showmanifest)
					System.out.println(s);
			}
			line = rdr.readLine();
		}
	}

	String process(String line) throws IOException {
		int start = 0;
		while (true) {
			start = line.indexOf("$(", start);
			if (start < 0) {
				line = line.replace('\\', ' ');
				return line;
			}
			if (start == 0 || line.charAt(start - 1) != '\\') {
				int end = line.indexOf(")", start + 2);
				if (end > start) {
					String key = line.substring(start + 2, end);
					String value = replace(key);
					if (value == null) {
						value = "Not-found: " + key;
					}
					line = line.substring(0, start) + value
							+ line.substring(end + 1);
				}
			}
			else
				start++;
		}
	}

	protected String replace(String key) throws IOException {
		String value = (String) btool.properties.get(key);
		if (value != null)
			return value;
		if (key.equals("DATE"))
			return new Date() + "";
		else
			if (key.equals("BTOOL"))
				return "OSGi Bundle tool utiltity v" + btool.version;
			else
				if (key.equals("FILE-SECTION"))
					return "Comment: File section should go here";
				else
					if (key.equals("ADMIN")) {
						return "(org.osgi.framework.AdminPermission)";
					}
		value = System.getProperty(key);
		if (value != null)
			return value;
		value = btool.getProject().getProperty(key);
		if (value != null)
			return value;
		File file = new File(key);
		if (file.exists()) {
			StringBuffer sb = new StringBuffer();
			FileReader rdr = new FileReader(file);
			int c = rdr.read();
			int n = 0;
			while (c > 0) {
				if (n > 72) {
					sb.append("\r\n ");
					n = 0;
				}
				switch (c) {
					case '\n' :
					case '\r' :
						c = ' ';
					default :
						sb.append((char) c);
						n++;
				}
				c = rdr.read();
			}
			return replace(sb.toString());
		}
		return "Comment: NOT FOUND " + key;
	}
}