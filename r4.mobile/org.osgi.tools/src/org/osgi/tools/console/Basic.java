package org.osgi.tools.console;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.log.*;
import org.osgi.service.packageadmin.*;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.tools.command.*;

/**
 * Provide a basic set of commands.
 */
public class Basic implements CommandProvider {
	Console			_console;
	BundleContext	_context;
	String			_password	= null;
	boolean			_stacktraces;
	String			_user;
	String			_username	= null;
	Hashtable		_variables;

	Basic(Console console, BundleContext context) {
		_console = console;
		_context = context;
		try {
			Properties props = new Properties();
			InputStream is = getClass().getResourceAsStream(
					"console.properties");
			if (is != null) {
				props.load(is);
				is.close();
			}
			_username = props.getProperty("console.user");
			_password = props.getProperty("console.password");
		}
		catch (Exception _e) {
			// Ignored on purpose
		}
	}

	//
	// Sets the console to ascii (normal) mode. Lines are terminated with \r\n.
	//
	public Object _ascii(CommandInterpreter intp) throws Exception {
		intp.setVariable("eol", "\r\n");
		return null;
	}

	//
	// Sets the console to binary mode. Lines are terminated with CTRL-A instead
	// of \r\n.
	//  
	public Object _bin(CommandInterpreter intp) throws Exception {
		intp.setVariable("eol", "\001");
		return null;
	}

	public Object _cat(CommandInterpreter intp) throws IOException {
		return _v(intp);
	}

	public Object _cd(CommandInterpreter intp) throws IOException {
		String name = intp.nextArgument();
		File pwd = getPWD(intp);
		if (name == null)
			pwd = new File("");
		else
			pwd = new File(pwd, name);
		pwd = new File(pwd.getCanonicalPath());
		if (!pwd.exists())
			return intp.error("No Such direcory " + name);
		intp.setVariable("PWD", pwd);
		return pwd;
	}

	public Object _decompile(CommandInterpreter intp)
			throws InvalidSyntaxException {
		ServiceReference[] refs = _context.getServiceReferences(
				null,
				"(service.id=" + intp.nextArgument() + ")");
		for (int i = 0; refs != null && i < refs.length; i++) {
			Object o = _context.getService(refs[i]);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			decompile(pw, o);
			pw.flush();
			return new String(out.toByteArray());
		}
		return null;
	}

	public Object _defaultpermissions(CommandInterpreter intp) throws Exception {
		PermissionAdmin admin = getPermissionAdmin();
		return admin.getDefaultPermissions();
	}

	public Object _echo(CommandInterpreter intp) {
		String v = intp.nextArgument();
		StringBuffer sb = new StringBuffer();
		String del = "";
		while (v != null) {
			if (v.equals("^"))
				del = "";
			else {
				sb.append(del);
				sb.append(v);
				del = " ";
			}
			v = intp.nextArgument();
		}
		return sb.toString();
	}

	public Object _exports(CommandInterpreter intp) {
		boolean s = false;
		String filter = null;
		String option = intp.nextArgument();
		if (option != null) {
			if (option.equals("-s"))
				s = true;
			else
				filter = option;
		}
		StringBuffer sb = new StringBuffer();
		ServiceReference ref = _context.getServiceReference(PackageAdmin.class
				.getName());
		if (ref != null) {
			PackageAdmin pa = (PackageAdmin) _context.getService(ref);
			if (pa != null) {
				ExportedPackage packs[] = pa.getExportedPackages((Bundle) null);
				for (int i = 0; packs != null && i < packs.length; i++) {
					if (filter == null
							|| packs[i].getName().indexOf(filter) >= 0) {
						sb.append(packs[i].getName());
						if (packs[i].getSpecificationVersion() != null) {
							sb.append("-");
							sb.append(packs[i].getSpecificationVersion());
						}
						sb.append(" exported by ");
						sb.append(Handler.toString(packs[i]
								.getExportingBundle()));
						sb.append("\r\n"); // akr added \r
						Bundle bs[] = packs[i].getImportingBundles();
						if (!s) {
							for (int j = 0; bs != null && j < bs.length; j++) {
								sb.append(" imported by ");
								sb.append(Handler.toString(bs[j]));
								sb.append("\r\n"); // akr added \r
							}
						}
					}
				}
				return sb.toString();
			}
			else
				sb.append("No pm");
		}
		Bundle b[] = _context.getBundles();
		for (int i = 0; i < b.length; i++) {
			Dictionary headers = b[i].getHeaders();
			String name = (String) headers.get("Bundle-Name");
			if (name == null)
				name = b[i].getLocation();
			name += " " + b[i].getBundleId();
			String exportString = (String) headers.get("Export-Package");
			if (exportString == null)
				continue;
			StringTokenizer st = new StringTokenizer(exportString, ",");
			while (st.hasMoreTokens()) {
				String pack = st.nextToken();
				int n = pack.indexOf(";");
				if (n > 0)
					pack = pack.substring(0, n);
				pack = pack.trim();
				sb.append(name + " exports " + pack);
			}
		}
		return sb.toString();
	}

	public Object _framework(CommandInterpreter intp) {
		StringBuffer sb = new StringBuffer();
		sb.append("Vendor                 "
				+ _context.getProperty("org.osgi.framework.vendor") + "\r\n");
		sb.append("Version                "
				+ _context.getProperty("org.osgi.framework.version") + "\r\n");
		sb.append("OS name                "
				+ _context.getProperty("org.osgi.framework.os.name") + "\r\n");
		sb.append("OS version             "
				+ _context.getProperty("org.osgi.framework.os.version")
				+ "\r\n");
		sb
				.append("Processor              "
						+ _context.getProperty("org.osgi.framework.processor")
						+ "\r\n");
		return sb.toString();
	}

	public Object _free(CommandInterpreter intp) {
		return new Long(Runtime.getRuntime().freeMemory());
	}

	public Object _gc(CommandInterpreter intp) {
		Runtime.getRuntime().gc();
		return _free(intp);
	}

	public Object _get(CommandInterpreter intp) {
		String name = intp.nextArgument();
		if (name == null)
			return intp.error("Name of variable non null");
		return intp.getVariable(name);
	}

	public Object _global(CommandInterpreter intp) {
		String name = intp.nextArgument();
		String value = intp.nextArgument();
		if (value != null && value.equals("="))
			value = intp.nextArgument();
		if (name == null || value == null)
			return intp.error("Name and value should be non null, name=" + name
					+ " and value=" + value);
		_console.setGlobal(name, value);
		return null;
	}

	public Object _headers(CommandInterpreter intp) throws Exception {
		StringBuffer sb = new StringBuffer();
		Bundle b = getBundle(intp);
		Dictionary d = b.getHeaders();
		for (Enumeration e = d.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			sb.append(" " + key + "=" + Handler.toString(d.get(key)) + "\r\n");
		}
		return sb.toString();
	}

	public Object _help(CommandInterpreter intp) {
		StringBuffer sb = new StringBuffer();
		String type = intp.nextArgument();
		Object services[] = _console.providers();
		for (int i = 0; services != null && i < services.length; i++) {
			CommandProvider p = (CommandProvider) services[i];
			String help = p.getHelp();
			if (type == null || help.startsWith(type))
				sb.append(help);
		}
		return sb.toString();
	}

	public Object _loadclass(CommandInterpreter intp)
			throws ClassNotFoundException {
		Bundle b = getBundle(intp);
		String c = intp.nextArgument();
		Class clazz = b.loadClass(c);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out)); 
		decompile(pw, clazz);
		return clazz;
	}

	public Object _getresource(CommandInterpreter intp) throws IOException {
		boolean view = false;
		Bundle b = getBundle(intp);
		String name = intp.nextArgument();
		while (name.startsWith("-")) {
			if (name.equals("-view")) {
				view = true;
			}
			name = intp.nextArgument();
		}
		URL url = b.getResource(name);
		return view(url, view);
	}

	Object view(URL url, boolean view) throws IOException {
		if (view && url != null) {
			InputStream in = url.openStream();
			byte buffer[] = new byte[1024];
			int size = in.read(buffer);
			while (size > 0) {
				System.out.print(buffer);
				size = in.read(buffer);
			}
			in.close();
		}
		return url;
	}

	public Object _getentry(CommandInterpreter intp) throws IOException {
		boolean view = false;
		Bundle b = getBundle(intp);
		String name = intp.nextArgument();
		while (name.startsWith("-")) {
			if (name.equals("-view")) {
				view = true;
			}
			name = intp.nextArgument();
		}
		URL url = b.getEntry(name);
		return view(url, view);
	}

	public Object _getentrypaths(CommandInterpreter intp) throws IOException {
		boolean view = false;
		Bundle b = getBundle(intp);
		String name = intp.nextArgument();
		return b.getEntryPaths(name);
	}
	
	public Object _inspect(CommandInterpreter intp) throws Exception {
		StringBuffer sb = new StringBuffer();
		Bundle b = getBundle(intp);
		sb.append("Id            : " + b.getBundleId() + "\r\n"
				+ "Location:     : " + b.getLocation() + "\r\n"
				+ "Status:       : " + status(b) + "\r\n");

		sb.append("Last Modified : ");
		sb.append(new Date(b.getLastModified()));
		sb.append("\r\n");

		return sb;
	}

	public Object _install(CommandInterpreter intp) throws Exception {
		String s = intp.nextArgument();
		if (s.indexOf(":") <= 0) {
			String prefix = (String) intp.getVariable("PREFIX");
			if (prefix != null)
				s = prefix + "/" + s;
		}
		return _context.installBundle(s);
	}

	public Object _interrupt(CommandInterpreter intp) throws Exception {
		String name = intp.nextArgument();
		Thread t = getThread(name);
		if (t != null) {
			t.interrupt();
			return t;
		}
		else
			return intp.error("No such thread " + name);
	}

	public Object _inuse(CommandInterpreter intp) {
		Bundle b = getBundle(intp);
		return b.getServicesInUse();
	}

	public Object _library(CommandInterpreter intp) {
		Runtime.getRuntime().loadLibrary(intp.nextArgument());
		return null;
	}

	//
	// List the resources in the jar file
	//
	public Object _listresources(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		if (b == null)
			return "No bundle specified";

		String prefix = intp.nextArgument();
		if (prefix == null)
			prefix = "";
		try {
			Method m = b.getClass().getMethod(
					"getEntryPaths",
					new Class[] {String.class});
			Vector v = new Vector();
			for (Enumeration e = (Enumeration) m.invoke(
					b,
					new Object[] {prefix}); e.hasMoreElements();) {
				String path = (String) e.nextElement();
				v.add(path);
			}
			return v;
		}
		catch (Error e) {
			// This might be a framework < 1.2
			throw new RuntimeException(
					"This framework does not support enumerating resources");
		}
	}

	public Object _load(CommandInterpreter intp) {
		Runtime.getRuntime().load(intp.nextArgument());
		return null;
	}

	public Object _logcause(CommandInterpreter intp) throws Exception {
		Collection list = (Collection) _log(intp);
		List result = new ArrayList();
		for (Iterator i = list.iterator(); i.hasNext();) {
			LogEntry entry = (LogEntry) i.next();
			Throwable throwable = entry.getException();
			if (throwable != null)
				result.add(throwable);
		}
		return result;
	}

	public Object _log(CommandInterpreter intp) throws Exception {
		String n = intp.nextArgument();
		String datePrefix = null;

		int mask = 0;
		Bundle bundle = null;
		while (n != null) {
			if (n.equalsIgnoreCase("debug"))
				mask |= 1;
			else if (n.equalsIgnoreCase("info"))
				mask |= 2;
			else if (n.equalsIgnoreCase("warning"))
				mask |= 4;
			else if (n.equalsIgnoreCase("error"))
				mask |= 8;
			else if (n.equalsIgnoreCase("all"))
				mask |= 0xF;
			else if (n.equalsIgnoreCase("bundle"))
				bundle = getBundle(intp);
			else if (n.equalsIgnoreCase("date"))
				datePrefix = intp.nextArgument();
			n = intp.nextArgument();
		}
		if (mask == 0)
			mask = 0xE;
		ServiceReference ref = _context
				.getServiceReference(LogReaderService.class.getName());
		if (ref == null)
			return intp.error("No log reader service in registry");
		LogReaderService rdr = (LogReaderService) _context.getService(ref);
		if (rdr == null)
			return intp.error("Could not get log reader service from context");
		List result = new ArrayList();
		for (Enumeration e = rdr.getLog(); e.hasMoreElements();) {
			LogEntry entry = (LogEntry) e.nextElement();
			boolean selected = false;

			if (bundle != null && bundle != entry.getBundle())
				continue;

			switch (entry.getLevel()) {
				case LogService.LOG_DEBUG :
					if ((mask & 1) != 0)
						selected = true;
					break;
				case LogService.LOG_INFO :
					if ((mask & 2) != 0)
						selected = true;
					break;
				case LogService.LOG_WARNING :
					if ((mask & 4) != 0)
						selected = true;
					break;
				case LogService.LOG_ERROR :
					if ((mask & 8) != 0)
						selected = true;
					break;
				default :
					selected = true;
					break;
			}
			String date = Handler.getDate(entry.getTime());
			if (datePrefix != null) {
				if (selected) {
					selected = date.startsWith(datePrefix);
				}
			}

			if (selected)
				result.add(entry);
		}
		Collections.reverse(result);
		return result;
	}

	//
	// Login user
	//
	public Object _login(CommandInterpreter intp) throws Exception {
		String user = intp.nextArgument();
		String pass = intp.nextArgument();
		if (user == null || pass == null || _username == null
				|| _password == null) {
			return new Boolean(false);
		}
		return new Boolean(user.equals(_username) && pass.equals(_password));
	}

	public Object _ls(CommandInterpreter intp) throws IOException {
		return _v(intp);
	}

	public Object _lsb(CommandInterpreter intp) throws Exception {
		Bundle[] bundles = _context.getBundles();
		return bundles;
	}

	public Object _lss(CommandInterpreter intp) throws Exception {
		String f = intp.nextArgument();
		ServiceReference[] refs = _context.getServiceReferences(null, f);
		return refs;
	}

	public Object _permissions(CommandInterpreter intp) throws Exception {
		PermissionAdmin admin = getPermissionAdmin();
		Bundle b = getBundle(intp);
		if (b != null) {
			return admin.getPermissions(b.getLocation());
		}
		else {
			Vector v = new Vector();
			String[] locations = admin.getLocations();
			for (int i = 0; locations != null && i < locations.length; i++) {
				v.add(admin.getPermissions(locations[i]));
			}
			return v;
		}
	}

	public Object _properties(CommandInterpreter intp) {
		return System.getProperties();
	}

	public Object _refresh(CommandInterpreter intp) throws Exception {
		PackageAdmin admin = getPackageAdmin();
		admin.refreshPackages(null);
		return null;
	}

	public Object _registered(CommandInterpreter intp) {
		Bundle b = getBundle(intp);
		return b.getRegisteredServices();
	}

	public Object _restart(CommandInterpreter intp) throws BundleException {
		Bundle system = _context.getBundle(0);
		system.update();
		return null;
	}

	public Object _service(CommandInterpreter intp) throws Exception {
		Vector all = new Vector();
		StringBuffer sb = new StringBuffer();
		ServiceReference refs[];
		String arg = intp.nextArgument();
		while (arg != null) {
			sb.append(arg);
			arg = intp.nextArgument();
		}
		String filter = sb.toString().trim();
		if (!filter.startsWith("("))
			filter = "(service.id=" + filter + ")";
		refs = _context.getServiceReferences(null, filter);
		for (int i = 0; refs != null && i < refs.length; i++) {
			Vector part = new Vector();
			all.addElement(part);
			Hashtable ht = new Hashtable();
			String properties[] = refs[i].getPropertyKeys();
			for (int j = 0; j < properties.length; j++) {
				ht.put(properties[j], refs[i].getProperty(properties[j]));
			}
			part.addElement(ht);
			part.addElement(refs[i].getBundle());
			part.addElement("");
		}
		return all;
	}

	public Object _set(CommandInterpreter intp) {
		String name = intp.nextArgument();
		String value = intp.nextArgument();
		if (value != null && value.equals("="))
			value = intp.nextArgument();
		if (name == null)
			return intp.error("Name should be non null, name=" + name);
		if (value != null)
			intp.setVariable(name, value);
		else
			intp.setVariable(name, "");
		return null;
	}

	public Object _showresource(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		String name = intp.nextArgument();
		if (name == null)
			throw new RuntimeException("You must specify a resource");
		try {
			Method m = b.getClass().getMethod(
					"getEntry",
					new Class[] {String.class});
			URL url = (URL) m.invoke(b, new Object[] {name});
			if (url == null)
				throw new RuntimeException("No such resource: " + name);
			InputStream in = url.openStream();
			byte[] buffer = new byte[1024];
			int size = in.read(buffer);
			StringBuffer sb = new StringBuffer();
			while (size > 0) {
				for (int i = 0; i < size; i++) {
					char c = (char) (0xFF & buffer[i]);
					if (c == 0x0D || c == 0x0A)
						sb.append(c);
					else if (c >= ' ' && c < 0x7F && c != '%')
						sb.append(c);
					else {
						sb.append("%");
						sb.append(nibble(c >> 4));
						sb.append(nibble(c));
					}
				}
				size = in.read(buffer);
			}
			in.close();
			return sb.toString();
		}
		catch (Error e) {
			// This might be a framework < 1.2
			throw new RuntimeException("Error" + e);
		}
	}

	public Object _shutdown(CommandInterpreter intp) throws BundleException {
		Bundle system = _context.getBundle(0);
		system.stop();
		System.exit(0);
		return null;
	}

	public Object _sleep(CommandInterpreter intp) {
		String time = intp.nextArgument();
		if (time == null)
			time = "1";
		long t = Long.parseLong(time) * 1000;
		try {
			Thread.sleep(t);
		}
		catch (Exception e) {
		}
		return new Date();
	}

	public Object _ss(CommandInterpreter intp) throws Exception {
		ServiceReference[] refs = _context.getServiceReferences(
				null,
				"(service.id=" + intp.nextArgument() + ")");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; refs != null && i < refs.length; i++) {
			String[] keys = refs[i].getPropertyKeys();
			sb.append("service.id=" + refs[i].getProperty("service.id"));
			sb.append("\r\n");
			for (int j = 0; keys != null && j < keys.length; j++) {
				// sb.append( " " );
				sb.append(keys[j] + "=");
				sb.append(Handler.toString(4, refs[i].getProperty(keys[j])));
				sb.append("\r\n");
			}
		}
		return sb.toString();
	}

	public Object _stacktraces(CommandInterpreter intp) {
		String arg = intp.nextArgument();
		if (arg == null || arg.equals("on"))
			_stacktraces = true;
		else
			_stacktraces = false;
		return null;
	}

	public Object _start(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		b.start();
		return b;
	}

	public Object _stop(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		b.stop();
		return b;
	}

	public Object _system(CommandInterpreter intp) throws Exception {
		StringBuffer sb = new StringBuffer();
		String s = intp.nextArgument();
		while (s != null) {
			sb.append(s + " ");
			s = intp.nextArgument();
		}
		Runtime.getRuntime().exec(sb.toString());
		return null;
	}

	public Object _threads(CommandInterpreter intp) throws Exception {
		return getThreads();
	}

	public Object _total(CommandInterpreter intp) {
		return new Long(Runtime.getRuntime().totalMemory());
	}

	public Object _type(CommandInterpreter intp) throws IOException {
		StringBuffer sb = new StringBuffer();
		File file = new File(intp.nextArgument());
		if (file.isDirectory()) {
			String list[] = file.list();
			sb.append(Handler.toString(list));
		}
		else if (!file.exists()) {
			return intp.error("No Such File: " + file);
		}
		else {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null) {
				sb.append(line + "\r\n");
				line = reader.readLine();
			}
			reader.close();
			in.close();
		}
		return sb.toString();
	}

	public Object _uninstall(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		b.uninstall();
		return b;
	}

	public Object _updatebundle(CommandInterpreter intp) throws Exception {
		Bundle b = getBundle(intp);
		String s = intp.nextArgument();
		if (s == null)
			b.update();
		else
			b.update((new URL(s)).openStream());
		return b;
	}

	public Object _v(CommandInterpreter intp) throws IOException {
		StringBuffer sb = new StringBuffer();
		File file = getFile(intp);
		if (!file.exists())
			return intp.error("No such file " + file.getPath());
		if (file.isDirectory())
			return file.list();
		FileInputStream fin = new FileInputStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(fin));
		String line = in.readLine();
		while (line != null) {
			sb.append(line);
			sb.append("\r\n");
			line = in.readLine();
		}
		return sb;
	}

	/*
	 * public Object _quit( CommandInterpreter intp ) throws Exception { String
	 * error = intp.nextArgument(); int n = 0; if ( error != null ) n =
	 * Integer.parseInt( error );
	 * 
	 * System.exit( n ); return null; }
	 */
	public Object copy(CommandInterpreter intp) throws Exception {
		StringBuffer sb = new StringBuffer();
		URL src = new URL(intp.nextArgument());
		String dst = intp.nextArgument();
		FileOutputStream out = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(src
				.openStream()));
		if (dst != null)
			out = new FileOutputStream(dst);
		String line = in.readLine();
		while (line != null) {
			if (out == null)
				sb.append(line + "\r\n");
			else
				out.write(line.getBytes());
			line = in.readLine();
		}
		in.close();
		out.close();
		return sb.toString();
	}

	void decompile(PrintWriter pw, Class c) {
		pw.print("interface ");
		pw.print(c.getName());
		pw.println("{");
		Field f[] = c.getFields();
		for (int i = 0; i < f.length; i++) {
			pw.print("   ");
			decompile(pw, f[i]);
			pw.println(";");
		}
		Method m[] = c.getMethods();
		for (int i = 0; i < m.length; i++) {
			pw.print("   ");
			decompile(pw, m[i]);
			pw.println(";");
		}
		pw.println("}");
		PackageAdmin admin = getPackageAdmin();
		if (admin != null) {
			Bundle b = admin.getBundle(c);
			if (b != null)
				pw.println("loaded from " + Handler.getName(b));
		}
		pw.flush();
	}

	void decompile(PrintWriter pw, Field f) {
		Class c = f.getType();
		pw.print(simpleName(c));
		pw.print(" ");
		pw.print(f.getName());
	}

	void decompile(PrintWriter pw, Method m) {
		Class c = m.getReturnType();
		pw.print(simpleName(c));
		pw.print(" ");
		pw.print(m.getName());
		pw.print("(");
		Class parameters[] = m.getParameterTypes();
		String del = "";
		for (int i = 0; i < parameters.length; i++) {
			pw.print(del);
			pw.print(simpleName(parameters[i]));
			del = ",";
		}
		pw.print(")");
	}

	void decompile(PrintWriter pw, Object o) {
		pw.println(o.getClass().getName());
		Class clazzes[] = o.getClass().getInterfaces();
		for (int i = 0; i < clazzes.length; i++) {
			decompile(pw, clazzes[i]);
		}
	}

	public Object _call(CommandInterpreter intp) throws Exception {
		ServiceReference[] refs = _context.getServiceReferences(
				null,
				"(service.id=" + intp.nextArgument() + ")");

		String call = intp.nextArgument();
		for (int i = 0; refs != null && i < refs.length; i++) {
			Object o = _context.getService(refs[i]);

			Vector v = new Vector();
			String arg;
			while ((arg = intp.nextArgument()) != null) {
				v.addElement(arg);
			}
			Object[] args = v.toArray();
			Method methods[] = o.getClass().getMethods();
			outer: for (int j = 0; j < methods.length; j++) {
				if (methods[j].getName().equals(call)) {
					Class types[] = methods[i].getParameterTypes();
					Object parms[] = new Object[types.length];
					if (types.length != args.length)
						continue;

					try {
						for (int k = 0; k < types.length; k++) {
							Constructor constructor = types[k]
									.getConstructor(new Class[] {String.class});
							if (constructor == null)
								continue outer;

							parms[k] = constructor
									.newInstance(new Object[] {args[k]});
						}

						return methods[j].invoke(o, parms);
					}
					catch (Exception e) {
						continue outer;
					}
				}
			}
		}
		return null;

	}

	public Bundle getBundle(CommandInterpreter intp) {
		String bundle = intp.nextArgument();
		if (bundle == null)
			return null;

		Object var = intp.getVariable(bundle);
		if (var != null && var instanceof String)
			bundle = (String) var;
		try {
			int id = Integer.parseInt(bundle);
			Bundle Bundle = _context.getBundle(id);
			if (Bundle == null)
				throw new RuntimeException("No such bundle " + id);
			return Bundle;
		}
		catch (NumberFormatException e) {
		}
		catch (NullPointerException e) {
		}
		return null;
	}

	File getFile(CommandInterpreter intp) throws IOException {
		String name = intp.nextArgument();
		File pwd = getPWD(intp);
		if (name == null)
			name = "";
		return new File(pwd, name);
	}

	public String getHelp() {
		return ""
				+ "BASIC\r\n"
				+ "cd <dir>                        Change directory (for v cmd -> sets PWD)\r\n"
				+ "capture                         Capture the console until a <cr> is hit\r\n"
				+ "exports [-s]                    List all exports\r\n"
				+ "decompile <service_id>          Decompile the interfaces of the given interface\r\n"
				+ "free                            free memory\r\n"
				+ "framework                       Show framework info\r\n"
				+ "gc                              garbage collect\r\n"
				+ "headers <id>                    Show the manifest headers\r\n"
				+ "inspect <id>                    inspect bundle <id>\r\n"
				+ "install <url>                   install a bundle\r\n"
				+ "interrupt <thread>              interrupt thread\r\n"
				+ "inuse <id>                      Services in use by <id>\r\n"
				+ "list resources <bundle> [ prfx] List resources in a bundle\r\n"
				+ "load <name>                     load library with full name\r\n"
				+ "loadlibrary <name>              load library with library name\r\n"
				+ "log [all|warning|info|error|debug]* [bundle <id>] Show log\r\n"
				+ "login <user> <password>         Login user\r\n"
				+ "lsb                             list installed bundles\r\n"
				+ "lss [ \"<filter>\" ]              list services, filter is LDAP syntax, no spaces\r\n"
				+ "registered <id>                 Services registered by <id>\r\n"
				+ "restart                         Restart the framework\r\n"
				+ "shutdown                        Stops the system bundle\r\n"
				+ "ss <sid>                        Show service\r\n"
				+ "start <id>                      Start bundle\r\n"
				+ "stop <id>                       Stop bundle\r\n"
				+ "service <id>                    Show service info\r\n"
				+ "threads                         Show threads\r\n"
				+ "total                           Total memory\r\n"
				+ "uninstall <id>                  uninstal bundle\r\n"
				+ "update bundle <id>              update bundle\r\n"
				+ "v <file>                        View a file or directory\r\n";
	}

	public String getName() {
		return "basic";
	}

	PackageAdmin getPackageAdmin() {
		ServiceReference ref = _context.getServiceReference(PackageAdmin.class
				.getName());
		if (ref == null)
			throw new RuntimeException("No Package Admin available");
		PackageAdmin pa = (PackageAdmin) _context.getService(ref);
		if (pa == null)
			throw new RuntimeException("No Package Admin available");
		return pa;
	}

	PermissionAdmin getPermissionAdmin() {
		ServiceReference ref = _context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null)
			throw new RuntimeException("No Permission Admin available");
		PermissionAdmin pa = (PermissionAdmin) _context.getService(ref);
		if (pa == null)
			throw new RuntimeException("No Permission Admin available");
		return pa;
	}

	File getPWD(CommandInterpreter intp) throws IOException {
		File file = (File) intp.getVariable("PWD");
		if (file == null)
			return new File("");
		else
			return new File(file.getCanonicalPath());
	}

	Thread getThread(String name) {
		Thread[] list = getThreads();
		for (int i = 0; i < list.length; i++)
			if (list[i] != null && list[i].getName().equals(name))
				return list[i];
		return null;
	}

	Thread[] getThreads() {
		ThreadGroup g = Thread.currentThread().getThreadGroup();
		while (g.getParent() != null)
			g = g.getParent();
		Thread list[] = new Thread[g.activeCount() + 20];
		g.enumerate(list);
		return list;
	}

	public String getUser() {
		return _user;
	}

	char nibble(int c) {
		return (char) (c < 10 ? c + '0' : c - 10 + 'A');
	}

	String simpleName(Class c) {
		int arrays = 0;
		int last = 0;
		String s = c.getName();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '[' :
					arrays++;
					break;
				case '.' :
					last = i + 1;
			}
		}
		return s.substring(last, s.length());
	}

	String status(Bundle b) {
		switch (b.getState()) {
			case Bundle.ACTIVE :
				return "ACTIVE";
			case Bundle.STARTING :
				return "STARTING";
			case Bundle.STOPPING :
				return "STOPPING";
			case Bundle.INSTALLED :
				return "INSTALLED";
			case Bundle.UNINSTALLED :
				return "UNINSTALLED";
			case Bundle.RESOLVED :
				return "RESOLVED";
			default :
				return "UNKNOWN STATE";
		}
	}

	public Object toString(Object o) {
		return Handler.toString(o);
	}

	public Object _capture(CommandInterpreter intp) throws IOException {
		BufferedReader rdr = new BufferedReader(
				new InputStreamReader(System.in));
		return rdr.readLine();
	}
}
