package org.osgi.tools.btool;

import java.io.*;
import java.util.*;

public class Manifest extends Hashtable {
	Package				_imports[];
	Package				_exports[];
	String				_activator;
	String				_classpath[]	= new String[] {"."};
	int					_section;
	String				_location;
	Native				_native[];
	Vector				_duplicates		= new Vector();
	final static String	wordparts		= "~!@#$%^&*_:/?><.-+";
	BTool				model;

	public Manifest(BTool model, InputStream in) throws IOException {
		this.model = model;
		try {
			parse(new InputStreamReader(in, "UTF8"));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot read manifest with UTF8: " + e);
			System.err.println("Will try without decoding (bad)");
			parse(new InputStreamReader(in));
		}
	}

	public Manifest(Reader in) throws IOException {
		parse(in);
	}

	public Object put(Object header, Object value) {
		if (containsKey(header)) {
			if (!((String) header).equalsIgnoreCase("comment"))
				_duplicates.addElement(header + ":" + value);
		}
		return super.put(header, value);
	}

	void parse(Reader in) throws IOException {
		BufferedReader rdr = new BufferedReader(in);
		String current = " ";
		String buffer = rdr.readLine();
		int section = 0;
		if (buffer != null && !buffer.startsWith("Manifest-Version")) {
			System.err
					.println("The first line of a manifest file must be the Manifest-Version attribute");
			throw new IOException(
					"The first line of a manifest file must be the Manifest-Version attribute");
		}
		while (buffer != null && current != null && section == 0) {
			if (current.startsWith(" ")) {
				buffer += current.substring(1);
			}
			else {
				section += entry(buffer);
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
		if (colon < 1) {
			error("Invalid header '" + line + "'");
		}
		else {
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
				if (header.equals("import-package"))
					_imports = getPackages(value, PackageResource.IMPORT);
				else if (header.equals("export-package"))
					_exports = getPackages(value, PackageResource.EXPORT);
				else if (header.equals("bundle-activator"))
					_activator = value.trim();
				else if (header.equals("bundle-updatelocation"))
					_location = value.trim();
				else if (header.equals("bundle-classpath"))
					_classpath = getClasspath(value);
				else if (header.equals("bundle-nativecode"))
					_native = getNative(value);
				put(header, value);
			}
		}
		return 0;
	}

	void error(String msg) throws IOException {
		if (model != null)
			model.warnings.add("Reading manifest: " + msg);
		else
			System.err.println("Reading manifest: " + msg);
	}

	StreamTokenizer getStreamTokenizer(String line) {
		StreamTokenizer st = new StreamTokenizer(new StringReader(line));
		st.resetSyntax();
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars('0', '9');
		st.whitespaceChars(0, ' ');
		st.quoteChar('"');
		for (int i = 0; i < wordparts.length(); i++)
			st.wordChars(wordparts.charAt(i), wordparts.charAt(i));
		return st;
	}

	String word(StreamTokenizer st) throws IOException {
		switch (st.nextToken()) {
			case '"' :
			case StreamTokenizer.TT_WORD :
				String result = st.sval;
				st.nextToken();
				return result;
		}
		return null;
	}

	Parameter getParameter(StreamTokenizer st) throws IOException {

		Parameter parameter = new Parameter();
		parameter.key = word(st);
		if (st.ttype == ':') {
			st.nextToken();
			parameter.type = Parameter.DIRECTIVE;
		}
		else {
			parameter.type = Parameter.ATTRIBUTE;
		}

		if (st.ttype == '=') {
			parameter.value = word(st);
			while (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"') {
				parameter.value += " " + st.sval;
				st.nextToken();
			}
		}

		return parameter;
	}

	Package[] getPackages(String line, int type) throws IOException {
		Vector v = new Vector();
		StreamTokenizer st = getStreamTokenizer(line);
		do {
			Parameter parameter = getParameter(st);
			Package p = new Package(parameter.key);
			while (st.ttype == ';') {
				parameter = getParameter(st);
				if (parameter.is("specification-version", Parameter.ATTRIBUTE))
					p.version = parameter.value;
				else if (parameter.is("version", Parameter.ATTRIBUTE))
					p.version = parameter.value;
				else if (parameter.type == Parameter.ATTRIBUTE)
					p.addParameter(parameter);
				else
					p.addParameter(parameter);
			}
			v.addElement(p);
		} while (st.ttype == ',');
		Package[] result = new Package[v.size()];
		v.copyInto(result);
		return result;
	}

	Native[] getNative(String line) throws IOException {
		Vector v = new Vector();
		StreamTokenizer st = getStreamTokenizer(line);
		do {
			Native spec = new Native();
			Vector names = new Vector();
			do {
				Parameter parameter = getParameter(st);
				if (parameter.value == null)
					names.addElement(parameter.key);
				else if (parameter.is("processor", Parameter.ATTRIBUTE))
					spec.processor = parameter.value;
				else if (parameter.is("osname", Parameter.ATTRIBUTE))
					spec.osname = parameter.value;
				else if (parameter.is("osversion", Parameter.ATTRIBUTE))
					spec.osversion = parameter.value;
				else if (parameter.is("language", Parameter.ATTRIBUTE))
					spec.language = parameter.value;
				else if (parameter.is("selection-filter", Parameter.DIRECTIVE))
					spec.filter = parameter.value;
				else
					model.warning("Unknown parameter for native code : "
							+ parameter);
			} while (st.ttype == ';');
			spec.paths = new String[names.size()];
			names.copyInto(spec.paths);
			v.addElement(spec);
		} while (st.ttype == ',');
		Native[] result = new Native[v.size()];
		v.copyInto(result);
		return result;
	}

	String[] getClasspath(String line) throws IOException {
		StringTokenizer st = new StringTokenizer(line, " \t,");
		String result[] = new String[st.countTokens()];
		for (int i = 0; i < result.length; i++)
			result[i] = st.nextToken();
		return result;
	}

	public Package[] getImports() {
		return _imports;
	}

	public Package[] getExports() {
		return _exports;
	}

	public String getActivator() {
		return _activator;
	}

	public String getLocation() {
		return _location;
	}

	public String[] getClasspath() {
		return _classpath;
	}

	public Native[] getNative() {
		return _native;
	}

	public Object get(Object key) {
		if (key instanceof String)
			return super.get(((String) key).toLowerCase());
		else
			return null;
	}

	String getValue(String key) {
		return (String) super.get(key.toLowerCase());
	}
}

class Native {
	String	filter;
	int		index	= -1;
	String	paths[];
	String	osname;
	String	osversion;
	String	language;
	String	processor;

}

