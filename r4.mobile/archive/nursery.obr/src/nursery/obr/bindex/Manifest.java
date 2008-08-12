package nursery.obr.bindex;

import java.io.*;
import java.util.*;

import org.osgi.framework.Version;

public class Manifest extends Hashtable {
	List			imports;
	List			exports;
	Entry				name;
	String				activator;
	String				classpath[]	= new String[] {"."};
	int					section;
	String				location;
	Native				_native[];
	Vector				duplicates	= new Vector();
	final static String	wordparts	= "~!@#$%^&*_:/?><.-+";
	Entry				bsn;
	Version				version;
	Entry				host;
	List			require;

	public Manifest(InputStream in) throws IOException {
		parse(new InputStreamReader(in, "UTF8"));
	}

	public Manifest(Reader in) throws IOException {
		parse(in);
	}

	public Object put(Object header, Object value) {
		if (containsKey(header)) {
			if (!((String) header).equalsIgnoreCase("comment"))
				duplicates.add(header + ":" + value);
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
			if (section == 0) {
				if (header.equals("bundle-symbolicname")) {
					bsn = (Entry) getEntries(value).get(0);
				}
				if (header.equals("bundle-version"))
					version = new Version(value.trim());
				if (header.equals("fragment-host"))
					host = (Entry) getEntries(value).get(0);
				if (header.equals("require-bundle"))
					require = getEntries(value);
				if (header.equals("import-package"))
					imports = getEntries(value);
				else if (header.equals("export-package"))
					exports = getEntries(value);
				else if (header.equals("bundle-activator"))
					activator = value.trim();
				else if (header.equals("bundle-updatelocation"))
					location = value.trim();
				else if (header.equals("bundle-classpath"))
					classpath = getClasspath(value);
				else if (header.equals("bundle-nativecode"))
					_native = getNative(value);
				put(header, value);
			}
		}
		return 0;
	}

	void error(String msg) throws IOException {
		System.err.println("Reading manifest: " + msg);
	}

	void warning(String msg) throws IOException {
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

	List getEntries(String line) throws IOException {
		List v = new Vector();
		StreamTokenizer st = getStreamTokenizer(line);
		do {
			Parameter parameter = getParameter(st);
			Entry p = new Entry(parameter.key);
			while (st.ttype == ';') {
				parameter = getParameter(st);
				if (parameter.type == Parameter.ATTRIBUTE)
					p.addParameter(parameter);
				else
					p.addParameter(parameter);
			}
			v.add(p);
		} while (st.ttype == ',');
		return v;
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
					names.add(parameter.key);
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
					warning("Unknown parameter for native code : " + parameter);
			} while (st.ttype == ';');
			spec.paths = new String[names.size()];
			names.copyInto(spec.paths);
			v.add(spec);
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

	public List getImports() {
		return imports;
	}

	public List getExports() {
		return exports;
	}

	public String getActivator() {
		return activator;
	}

	public String getLocation() {
		return location;
	}

	public String[] getClasspath() {
		return classpath;
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

	public String getValue(String key) {
		return (String) super.get(key.toLowerCase());
	}

	public String getValue(String key, String deflt) {
		String s = getValue(key);
		if (s == null)
			return deflt;
		else
			return s;
	}

	public String[] getRequiredExecutionEnvironments() {
		String ees = getValue("Bundle-RequiredExecutionEnvironment");
		if (ees != null)
			return ees.trim().split("\\s*,\\s*");
		else
			return null;
	}

	public Version getVersion() {
		if ( version == null )
			return new Version("0");
		return version;
	}

	public String getSymbolicName() {
		Entry bsn = getBsn();

		if (bsn == null) {
			String name = getValue("Bundle-Name");
			if (name == null)
				name = "Untitled-" + hashCode();
			return name;
		}
		else
			return bsn.getName();
	}

	public String getManifestVersion() {
		return getValue("Bundle-ManifestVersion", "1");
	}

	public String getCopyright() {
		return getValue("Bundle-Copyright");
	}

	public String getDocumentation() {
		return getValue("Bundle-DocURL");
	}

	public String[] getCategories() {
		String cats = getValue("Bundle-Category");
		if (cats == null)
			return new String[0];
		else
			return cats.split("\\s*,\\s*");
	}

	public Native[] get_native() {
		return _native;
	}

	public void set_native(Native[] _native) {
		this._native = _native;
	}

	public Entry getBsn() {
		return bsn;
	}

	public void setBsn(Entry bsn) {
		this.bsn = bsn;
	}

	public Vector getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(Vector duplicates) {
		this.duplicates = duplicates;
	}

	public Entry getHost() {
		return host;
	}

	public void setHost(Entry host) {
		this.host = host;
	}

	public Entry getName() {
		return name;
	}

	public void setName(Entry name) {
		this.name = name;
	}

	public List getRequire() {
		return require;
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

