package org.osgi.impl.bundle.console;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.tools.command.CommandInterpreter;
import org.osgi.tools.command.CommandProvider;

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
		for (ServiceReference< ? > ref : _context
				.getServiceReferences("(service.id=" + intp.nextArgument()
						+ ")")) {
			Object o = _context.getService(ref);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			decompile(pw, o);
			pw.flush();
			return new String(out.toByteArray());
		}
		return null;
	}

	// public Object _defaultpermissions(CommandInterpreter intp) throws
	// Exception {
	// PermissionAdmin admin = getPermissionAdmin();
	// return admin.getDefaultPermissions();
	// }

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
		StringBuilder sb = new StringBuilder();
		Collection<ExportedPackage> packs = _context.getFramework()
				.getExportedPackages(
				null);
		for (ExportedPackage pack : packs) {
			if (filter == null || pack.getName().indexOf(filter) >= 0) {
				sb.append(pack.getName());
				if (pack.getVersion() != null) {
					sb.append("-");
					sb.append(pack.getVersion());
				}
				sb.append(" exported by ");
				sb.append(Handler.toString(pack.getExportingBundle()));
				sb.append("\r\n"); // akr added \r
				if (!s) {
					Collection<Bundle> bs = pack.getImportingBundles();
					for (Bundle b : bs) {
						sb.append(" imported by ");
						sb.append(Handler.toString(b));
						sb.append("\r\n"); // akr added \r
					}
				}
			}
		}
		sb.append("\r\n"); // akr added \r

		// for (Bundle b : _context.getFramework().getBundles()) {
		// Map<String, String> headers = b.getHeaders();
		// String name = b.getSymbolicName();
		// if (name == null)
		// name = b.getLocation();
		// name += " " + b.getBundleId();
		// String exportString = headers.get("Export-Package");
		// if (exportString == null)
		// continue;
		// StringTokenizer st = new StringTokenizer(exportString, ",");
		// while (st.hasMoreTokens()) {
		// String pack = st.nextToken();
		// int n = pack.indexOf(";");
		// if (n > 0)
		// pack = pack.substring(0, n);
		// pack = pack.trim();
		// sb.append(name + " exports " + pack);
		// }
		// sb.append("\r\n"); // akr added \r
		//
		// }
		return sb.toString();
	}

	public Object _framework(CommandInterpreter intp) {
		StringBuilder sb = new StringBuilder();
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
		for (Map.Entry<String, String> entry : b.getHeaders().entrySet()) {
			sb.append(" " + entry.getKey() + "="
					+ Handler.toString(entry.getValue()) + "\r\n");
		}
		return sb.toString();
	}

	public Object _help(CommandInterpreter intp) {
		StringBuffer sb = new StringBuffer();
		String type = intp.nextArgument();
		for (CommandProvider p : _console.providers()) {
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
		Class< ? > clazz = b.loadClass(c);
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

	public Object _getentrypaths(CommandInterpreter intp) {
		Bundle b = getBundle(intp);
		String name = intp.nextArgument();
		return b.getEntryPaths(name);
	}
	
	public Object _inspect(CommandInterpreter intp) throws Exception {
		StringBuilder sb = new StringBuilder();
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
		return _context.getFramework().installBundle(s, null);
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
		return b.getEntryPaths(prefix);
	}

	public Object _load(CommandInterpreter intp) {
		Runtime.getRuntime().load(intp.nextArgument());
		return null;
	}

	// public List<Throwable> _logcause(CommandInterpreter intp) throws
	// Exception {
	// @SuppressWarnings("unchecked")
	// Collection<LogEntry> list = (Collection<LogEntry>) _log(intp);
	// List<Throwable> result = new ArrayList<Throwable>();
	// for (LogEntry entry : list) {
	// Throwable throwable = entry.getException();
	// if (throwable != null)
	// result.add(throwable);
	// }
	// return result;
	// }

	// public Object _log(CommandInterpreter intp) throws Exception {
	// String n = intp.nextArgument();
	// String datePrefix = null;
	//
	// int mask = 0;
	// Bundle bundle = null;
	// while (n != null) {
	// if (n.equalsIgnoreCase("debug"))
	// mask |= 1;
	// else if (n.equalsIgnoreCase("info"))
	// mask |= 2;
	// else if (n.equalsIgnoreCase("warning"))
	// mask |= 4;
	// else if (n.equalsIgnoreCase("error"))
	// mask |= 8;
	// else if (n.equalsIgnoreCase("all"))
	// mask |= 0xF;
	// else if (n.equalsIgnoreCase("bundle"))
	// bundle = getBundle(intp);
	// else if (n.equalsIgnoreCase("date"))
	// datePrefix = intp.nextArgument();
	// n = intp.nextArgument();
	// }
	// if (mask == 0)
	// mask = 0xE;
	// LogReaderService rdr = _context.getService(LogReaderService.class);
	// if (rdr == null)
	// return intp.error("Could not get log reader service from context");
	// List<LogEntry> result = new ArrayList<LogEntry>();
	// @SuppressWarnings("unchecked")
	// Enumeration<LogEntry> entries = rdr.getLog();
	// for (LogEntry entry : Collections.list(entries)) {
	// boolean selected = false;
	//
	// if (bundle != null && bundle != entry.getBundle())
	// continue;
	//
	// switch (entry.getLevel()) {
	// case LogService.LOG_DEBUG :
	// if ((mask & 1) != 0)
	// selected = true;
	// break;
	// case LogService.LOG_INFO :
	// if ((mask & 2) != 0)
	// selected = true;
	// break;
	// case LogService.LOG_WARNING :
	// if ((mask & 4) != 0)
	// selected = true;
	// break;
	// case LogService.LOG_ERROR :
	// if ((mask & 8) != 0)
	// selected = true;
	// break;
	// default :
	// selected = true;
	// break;
	// }
	// String date = Handler.getDate(entry.getTime());
	// if (datePrefix != null) {
	// if (selected) {
	// selected = date.startsWith(datePrefix);
	// }
	// }
	//
	// if (selected)
	// result.add(entry);
	// }
	// Collections.reverse(result);
	// return result;
	// }
	//
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
		Collection<Bundle> bundles = _context.getFramework().getBundles();
		return bundles;
	}

	public Object _lss(CommandInterpreter intp) throws Exception {
		String f = intp.nextArgument();
		Collection<ServiceReference< ? >> refs = _context
				.getServiceReferences(f);
		return refs;
	}

	// public Object _permissions(CommandInterpreter intp) throws Exception {
	// PermissionAdmin admin = getPermissionAdmin();
	// Bundle b = getBundle(intp);
	// if (b != null) {
	// return admin.getPermissions(b.getLocation());
	// }
	// else {
	// List<PermissionInfo[]> v = new ArrayList<PermissionInfo[]>();
	// String[] locations = admin.getLocations();
	// for (String location : locations) {
	// v.add(admin.getPermissions(location));
	// }
	// return v;
	// }
	// }

	public Object _properties(CommandInterpreter intp) {
		return System.getProperties();
	}

	 public Object _refresh(CommandInterpreter intp) throws Exception {
		_context.getFramework().refreshPackages();
		return null;
	}

	public Object _registered(CommandInterpreter intp) {
		Bundle b = getBundle(intp);
		return b.getRegisteredServices();
	}

	public Object _restart(CommandInterpreter intp) throws BundleException {
		Bundle system = _context.getFramework().getBundle(0);
		system.update(null);
		return null;
	}

	public Object _service(CommandInterpreter intp) throws Exception {
		List<List<Object>> all = new ArrayList<List<Object>>();
		StringBuilder sb = new StringBuilder();
		Collection<ServiceReference< ? >> refs;
		String arg = intp.nextArgument();
		while (arg != null) {
			sb.append(arg);
			arg = intp.nextArgument();
		}
		String filter = sb.toString().trim();
		if (!filter.startsWith("("))
			filter = "(service.id=" + filter + ")";
		refs = _context.getServiceReferences(filter);
		for (ServiceReference< ? > ref : refs) {
			List<Object> part = new ArrayList<Object>();
			all.add(part);
			Map<String, Object> ht = new HashMap<String, Object>();
			Set<String> properties = ref.getPropertyKeys();
			for (String property : properties) {
				ht.put(property, ref.getProperty(property));
			}
			part.add(ht);
			part.add(ref.getBundle());
			part.add("");
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
		URL url = b.getEntry(name);
		if (url == null)
			throw new RuntimeException("No such resource: " + name);
		InputStream in = url.openStream();
		byte[] buffer = new byte[1024];
		int size = in.read(buffer);
		StringBuilder sb = new StringBuilder();
		while (size > 0) {
			for (int i = 0; i < size; i++) {
				char c = (char) (0xFF & buffer[i]);
				if (c == 0x0D || c == 0x0A)
					sb.append(c);
				else
					if (c >= ' ' && c < 0x7F && c != '%')
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

	public Object _shutdown(CommandInterpreter intp) throws BundleException {
		Bundle system = _context.getFramework().getBundle(0);
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
			// ignored
		}
		return new Date();
	}

	public Object _ss(CommandInterpreter intp) throws Exception {
		Collection<ServiceReference< ? >> refs = _context
				.getServiceReferences(
				"(service.id=" + intp.nextArgument() + ")");
		StringBuilder sb = new StringBuilder();
		for (ServiceReference< ? > ref : refs) {
			Set<String> keys = ref.getPropertyKeys();
			sb.append("service.id=" + ref.getProperty("service.id"));
			sb.append("\r\n");
			for (String key : keys) {
				// sb.append( " " );
				sb.append(key + "=");
				sb.append(Handler.toString(4, ref.getProperty(key)));
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
		StringBuilder sb = new StringBuilder();
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
		StringBuilder sb = new StringBuilder();
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
			b.update(null);
		else
			b.update((new URL(s)).openStream());
		return b;
	}

	public Object _v(CommandInterpreter intp) throws IOException {
		StringBuilder sb = new StringBuilder();
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
		StringBuilder sb = new StringBuilder();
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

	void decompile(PrintWriter pw, Class< ? > c) {
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
		Bundle b = _context.getFramework().getBundle(c);
		if (b != null)
			pw.println("loaded from " + Handler.getName(b));
		pw.flush();
	}

	void decompile(PrintWriter pw, Field f) {
		Class< ? > c = f.getType();
		pw.print(simpleName(c));
		pw.print(" ");
		pw.print(f.getName());
	}

	void decompile(PrintWriter pw, Method m) {
		Class< ? > c = m.getReturnType();
		pw.print(simpleName(c));
		pw.print(" ");
		pw.print(m.getName());
		pw.print("(");
		Class< ? > parameters[] = m.getParameterTypes();
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
		Class< ? > clazzes[] = o.getClass().getInterfaces();
		for (int i = 0; i < clazzes.length; i++) {
			decompile(pw, clazzes[i]);
		}
	}

	public Object _call(CommandInterpreter intp) throws Exception {
		Collection<ServiceReference< ? >> refs = _context
				.getServiceReferences(
				"(service.id=" + intp.nextArgument() + ")");

		String call = intp.nextArgument();
		for (ServiceReference< ? > ref : refs) {
			Object o = _context.getService(ref);

			List<String> v = new ArrayList<String>();
			String arg;
			while ((arg = intp.nextArgument()) != null) {
				v.add(arg);
			}
			Object[] args = v.toArray();
			Method methods[] = o.getClass().getMethods();
			outer: for (Method method : methods) {
				if (method.getName().equals(call)) {
					Class< ? > types[] = method.getParameterTypes();
					Object parms[] = new Object[types.length];
					if (types.length != args.length)
						continue;

					try {
						for (int k = 0; k < types.length; k++) {
							Constructor< ? > constructor = types[k]
									.getConstructor(new Class[] {String.class});
							if (constructor == null)
								continue outer;

							parms[k] = constructor
									.newInstance(new Object[] {args[k]});
						}

						return method.invoke(o, parms);
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
			Bundle Bundle = _context.getFramework().getBundle(id);
			if (Bundle == null)
				throw new RuntimeException("No such bundle " + id);
			return Bundle;
		}
		catch (NumberFormatException e) {
			// ignored
		}
		catch (NullPointerException e) {
			// ignored
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

	// PackageAdmin getPackageAdmin() {
	// PackageAdmin pa = _context.getService(PackageAdmin.class);
	// if (pa == null)
	// throw new RuntimeException("No Package Admin available");
	// return pa;
	// }
	//
	// PermissionAdmin getPermissionAdmin() {
	// PermissionAdmin pa = _context.getService(PermissionAdmin.class);
	// if (pa == null)
	// throw new RuntimeException("No Permission Admin available");
	// return pa;
	// }

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

	String simpleName(Class< ? > c) {
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
		return b.getState().toString();
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
