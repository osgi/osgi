package org.osgi.tools.btool;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class FileResource extends Resource {
	File	file;
	boolean	preprocess;

	FileResource(BTool btool, String path, File file, boolean preprocess) {
		super(btool, null, path);
		this.file = file;
		this.preprocess = preprocess;
	}

	protected int getLineLength() {
		return 120;
	}

	InputStream getInputStream() throws IOException {
		if (!preprocess)
			return super.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF8"));
		parse(br, pw);
		pw.print("\r\n");
		pw.close();
		out.close();
		return new ByteArrayInputStream(out.toByteArray());
	}

	void parse(BufferedReader br, PrintWriter pw) throws IOException {
		parse(br, pw, getLineLength(), "\\\r\n");
	}

	void parse(BufferedReader rdr, PrintWriter pw, int linewidth,
			String continuation) throws IOException {
		String line = rdr.readLine();
		while (line != null) {
			if (line.length() == 0)
				pw.print("\r\n");
			else {
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
								break;

							case '$' :
								if (i < line.length() - 2) {
									int c2 = hex(line.charAt(i + 1), line
											.charAt(i + 2));
									if (c2 >= 0) {
										c = (char) c2;
										i += 2;
									}
								}
						}
						if (linewidth != 0 && n > linewidth) {
							sb.append(continuation);
							n = 1;
						}
						sb.append(c);
						n++;
					}
					String s = sb.toString();
					pw.print(s);
					pw.print("\r\n");
					if (btool.showmanifest)
						System.out.println(s);
				}
			}
			line = rdr.readLine();
		}
		// Force and empty line to mark the end
		String lastLine = process("");
		if (lastLine != null)
			pw.print(lastLine + "\r\n");
	}

	/**
	 * @param c
	 * @param d
	 * @return
	 */
	static int hex(char c, char d) {
		int cc = nibble(c);
		int dd = nibble(d);
		if (cc < 0 || dd < 0)
			return -1;
		System.out.println("Hex " + c + " " + d + " " + (cc * 16 + dd));
		return cc * 16 + dd;
	}

	static int nibble(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		c = Character.toUpperCase(c);
		if (c >= 'A' && c <= 'F')
			return c - 'A' + 10;

		return -1;
	}

	String process(String line) throws IOException {
		StringBuffer sb = new StringBuffer();
		process(line, 0, '\0', sb);
		return sb.toString();
	}

	int process(String line, int index, char type, StringBuffer result)
			throws IOException {
		StringBuffer variable = new StringBuffer();
		outer: while (index < line.length()) {
			char c1 = line.charAt(index++);
			if (((type == '(' && c1 == ')') || (type == '{' && c1 == '}'))) {
				result.append(replace(variable.toString()));
				return index;
			}

			if (c1 == '$' && index < line.length() - 2) {
				char c2 = line.charAt(index);
				if (c2 == '(' || c2 == '{') {
					index = process(line, index + 1, c2, variable);
					continue outer;
				}
			}
			variable.append(c1);
		}
		result.append(variable);
		return index;
	}

	protected String replace(String key) throws IOException {
		String value = (String) btool.properties.get(key);
		if (value != null)
			return value;

		value = doCommands(key);
		if (value != null)
			return value;

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

	/**
	 * Parse the key as a command. A command consist of parameters separated by
	 * ':'.
	 * 
	 * @param key
	 * @return
	 */
	static Pattern	commands	= Pattern.compile(":");

	private String doCommands(String key) throws IOException {
		String[] args = commands.split(key);
		if (args == null || args.length == 0)
			return null;

		btool.trace("doCommands " + Arrays.asList(args));
		String cname = "_" + args[0].replaceAll("-", "_");

		try {
			Method m = getClass()
					.getMethod(cname, new Class[] {String[].class});
			return (String) m.invoke(this, new Object[] {args});
		}
		catch (Exception e) {
			// Ignore
		}
		return null;
	}

	/**
	 * 
	 * @param args
	 * @return
	 */
	public String _replace(String args[]) {
		if (args.length != 4) {
			btool.warning("Invalid nr of arguments to replace "
					+ Arrays.asList(args));
			return null;
		}

		String list[] = args[1].trim().split("\\s*,\\s*");
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < list.length; i++) {
			String element = list[i].trim();
			if (!element.equals("")) {
				sb.append(del);
				sb.append(element.replaceAll(args[2], args[3]));
				del = ", ";
			}
		}
		return sb.toString();
	}

	public String _filter(String args[]) {
		if (args.length != 3) {
			btool.warning("Invalid nr of arguments to filter "
					+ Arrays.asList(args));
			return null;
		}

		String list[] = args[1].split("\\s*,\\s*");
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < list.length; i++) {
			if (list[i].matches(args[2])) {
				sb.append(del);
				sb.append(list[i]);
				del = ", ";
			}
		}
		return sb.toString();
	}

	public String _filterout(String args[]) {
		if (args.length != 3) {
			btool.warning("Invalid nr of arguments to filterout "
					+ Arrays.asList(args));
			return null;
		}

		String list[] = args[1].split("\\s*,\\s*");
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < list.length; i++) {
			if (!list[i].matches(args[2])) {
				sb.append(del);
				sb.append(list[i]);
				del = ", ";
			}
		}
		return sb.toString();
	}

	public String _sort(String args[]) {
		if (args.length != 2) {
			btool.warning("Invalid nr of arguments to join "
					+ Arrays.asList(args));
			return null;
		}

		String list[] = args[1].split("\\s*,\\s*");
		StringBuffer sb = new StringBuffer();
		String del = "";
		Arrays.sort(list);
		for (int i = 0; i < list.length; i++) {
			sb.append(del);
			sb.append(list[i]);
			del = ", ";
		}
		return sb.toString();
	}

	public String _join(String args[]) {
		if (args.length == 1) {
			btool.warning("Invalid nr of arguments to join "
					+ Arrays.asList(args));
			return null;
		}

		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 1; i < args.length; i++) {
			String list[] = args[i].split("\\s*,\\s*");
			for (int j = 0; j < list.length; j++) {
				sb.append(del);
				sb.append(list[j]);
				del = ", ";
			}
		}
		return sb.toString();
	}

	public String _if(String args[]) {
		if (args.length < 3) {
			btool.warning("Invalid nr of arguments to if "
					+ Arrays.asList(args));
			return null;
		}

		if (args[1].trim().length() != 0)
			return args[2];
		if (args.length > 3)
			return args[3];
		else
			return "";
	}

	public String _DATE(String args[]) {
		return new Date().toString();
	}

	public String _BTOOL(String args[]) {
		return "OSGi Bundle tool utiltity v" + btool.version;
	}

	public String _DELIVER(String args[]) {
		return "OSGi Bundle tool utiltity v" + btool.version;
	}

	public String _FILE_SECTION(String args[]) {
		return "Comment: File section should go here";
	}

	public String _ADMIN(String args[]) {
		return "(org.osgi.framework.AdminPermission)";
	}

	public String _strip(String args[]) {
		if (args.length == 2) {
			int l = Math.min(args[1].length(), args[2].length());
			if (args[2].startsWith(args[1]))
				return args[2].substring(l);
			else
				return args[2];
		}
		else
			throw new IllegalArgumentException("Need 2 arguments to _strip");
	}
}