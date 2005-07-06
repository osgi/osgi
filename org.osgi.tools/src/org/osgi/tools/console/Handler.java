package org.osgi.tools.console;

import java.io.*;
import java.net.Socket;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.*;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.tools.command.CommandInterpreter;

class Handler extends Thread {
	Socket			_socket;
	Console			_console;
	StringTokenizer	_st;
	String			_current;
	Vector			_targets;
	PrintWriter		_wrt;
	InputStream		_in;
	OutputStream	_out;
	boolean			_stacktraces;
	boolean			_continue	= true;
	Hashtable		_variables;
	String			_user;

	public void setVariable(String name, Object value) {
		_variables.put(name, value);
	}

	public Object getVariable(String name) {
		Object o = _variables.get(name);
		if (o != null)
			return o;
		o = _console.getGlobal(name);
		if (o != null)
			return o;
		return System.getProperty(name);
	}

	public String getUser() {
		return _user;
	}

	Handler(Console console, Socket socket) {
		_socket = socket;
		_console = console;
		_variables = new Hashtable();
	}

	void close() {
		_continue = false;
		try {
			_socket.close();
		}
		catch (Exception e) {
		}
		_variables = null;
	}

	public static String toString(Object o) {
		return toString(0, o);
	}

	public static String toString(int n, Object o) {
		if (o instanceof Vector)
			return toString(n, (Vector) o);
		if (o instanceof Object[])
			return toString(n, (Object[]) o);
		if (o instanceof Bundle)
			return toString(n, (Bundle) o);
		if (o instanceof LogEntry)
			return toString(n, (LogEntry) o);
		if (o instanceof ServiceReference)
			return toString(n, (ServiceReference) o);
		if (o instanceof Thread)
			return toString(n, (Thread) o);
		if (o instanceof Dictionary)
			return toString(n, (Dictionary) o);
		if (o instanceof ExportedPackage)
			return toString(n, (ExportedPackage) o);
		return "" + o;
	}

	public static String toString(int n, ExportedPackage ep) {
		StringBuffer sb = new StringBuffer();
		sb.append(ep.getName());
		sb.append(spaces(30 - ep.getName().length()));
		sb.append(ep.getSpecificationVersion());
		return sb.toString();
	}

	public static String toString(int indent, Dictionary d) {
		int max = 0;
		String l[] = new String[d.size()];
		int n = 0;
		for (Enumeration e = d.keys(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			if (name.length() > max)
				max = name.length();
			l[n++] = name;
		}
		Arrays.sort(l);
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < l.length; i++) {
			sb.append(del);
			sb.append(l[i]);
			sb.append(spaces(max + 2 - l[i].length()));
			sb.append(toString(indent + max, d.get(l[i])));
			del = "\r\n" + spaces(n);
		}
		return sb.toString();
	}

	public static String toString(int n, Object s[]) {
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < s.length; i++)
			if (s[i] != null) {
				sb.append(del);
				sb.append(toString(s[i]));
				del = "\r\n" + spaces(n);
				//del = ",";
			}
		return sb.toString();
	}

	public static String toString(int n, Vector v) {
		StringBuffer sb = new StringBuffer();
		String del = "";
		for (int i = 0; i < v.size(); i++)
			if (v.elementAt(i) != null) {
				sb.append(del);
				del = "\r\n" + spaces(n);
				sb.append(toString(v.elementAt(i)));
			}
		return sb.toString();
	}

	public static String toString(int n, LogEntry l) {
		String level = "UNKNOWN";
		switch (l.getLevel()) {
			case LogService.LOG_ERROR :
				level = "ERROR  ";
				break;
			case LogService.LOG_WARNING :
				level = "WARNING";
				break;
			case LogService.LOG_INFO :
				level = "INFO   ";
				break;
			case LogService.LOG_DEBUG :
				level = "DEBUG  ";
				break;
		}
		Bundle b = l.getBundle();
		String id = "0";
		if (b != null)
			id = "" + b.getBundleId();
		String exception = "";
		if (l.getException() != null)
			exception = " : " + l.getException();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(l.getTime()));
		return spaces(n)
				+ (cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/"
						+ cal.get(Calendar.DAY_OF_MONTH) + " "
						+ cal.get(Calendar.HOUR_OF_DAY) + ":" + cal
						.get(Calendar.MINUTE)) + " : " + level + " : " + id
				+ " : " + l.getMessage() + exception;
	}

	static String status(Bundle b) {
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

	public static String toString(int n, Bundle b) {
		return spaces(n) + justify(b.getBundleId() + " ", 4, 'L') + " "
				+ justify(status(b), 12, 'L') + justify(getName(b), 60, 'L');
	}

	static String getName(Bundle b) {
		String name = (String) b.getHeaders().get("Bundle-Name");
		if (name != null)
			return name;
		return b.getLocation();
	}

	public static String toString(int n, Thread t) {
		if (t != null)
			return spaces(n) + (t.isAlive() ? "alive " : "dead  ")
					+ t.getName();
		return null;
	}

	/**
	 * -11 ACTIVE file:C:\framework\.\load\org.osgi.impl.service.automation.ja
	 * objectClass org.osgi.service.automation.WireAdmin service.id 9
	 *  
	 */
	public static String toString(int n, ServiceReference ref) {
		StringBuffer sb = new StringBuffer();
		String[] ocs = (String[]) ref.getProperty("objectclass");
		for (int i = 0; ocs != null && i < ocs.length; i++) {
			String id = ref.getProperty("service.id") + "";
			long bundle = ref.getBundle().getBundleId();
			String pid = (String) ref.getProperty("service.pid");
			String name = (String) ref.getProperty("service.name");
			String description = (String) ref
					.getProperty("service.description");
			String cname = ocs[i];
			int index = cname.lastIndexOf('.');
			if (index >= 0)
				cname = cname.substring(index + 1);
			sb.append(justify(id, 5, 'R'));
			sb.append(justify(bundle + "", 5, 'R'));
			sb.append(" ");
			sb.append(cname);
			if (pid != null)
				sb.append(" [" + pid + "]");
			if (name != null)
				sb.append(" " + name);
			if (description != null)
				sb.append(" (" + description + ")");
		}
		return sb.toString();
	}

	static Vector	_history	= new Vector();

	String getLine(boolean _echo, boolean _break) throws IOException {
		_echo = false;
		byte buffer[] = new byte[8192];
		int index = 0;
		int c = _in.read();
		outer: while (c != -1) {
			switch (c) {
				case '\r' :
				case '\n' :
					if (index > 0) {
						break outer;
					}
					break;
				case 0x08 :
					if (index > 0) {
						index--;
						if (_echo) {
							_out.write(new byte[] {0x08, 0x20, 0x08});
							//_out.flush();
						}
					}
					break;
				case 0x03 :
					if (_break) {
						index = -1;
						break outer;
					}
					index = 0;
					_wrt.print("\r\n> ");
					_wrt.flush();
					break;
				case 0x01 :
					if (_history.size() > 0) {
						String l = (String) _history
								.elementAt(_history.size() - 1);
						_wrt.print("\r\n>" + l + "\r\n");
						return l;
					}
					break;
				default :
					buffer[index++] = (byte) c;
					if (_echo) {
						_out.write((byte) c);
					}
					break;
			}
			_out.flush();
			c = _in.read();
		}
		String line = null;
		if (index > -1) {
			line = new String(buffer, 0, index);
			_history.addElement(line);
			if (_history.size() > 20)
				_history.removeElementAt(0);
		}
		_wrt.print("\r\n");
		return line;
	}

	String getNextLine(boolean _echo, boolean _break) throws IOException {
		_wrt.print(">> ");
		_wrt.flush();
		return getLine(_echo, _break);
	}

	public void run() {
		Context context = new Context(this);
		boolean loggedIn = false;
		try {
			context.setVariable("eol", new String("\r\n"));
			//_user = System.getProperty("user.name", "");
			_user = "not logged in";
			_in = _socket.getInputStream();
			_out = _socket.getOutputStream();
			_wrt = new PrintWriter(new OutputStreamWriter(_out));
			_wrt.print("OSGi Programmers Console\r\n");
			while (_continue) {
				boolean mustLogin = System
						.getProperty("org.osgi.tools.console.mustlogin") != null;
				if (mustLogin)
					_wrt.print("$" + getUser() + "$\r\n");
				_wrt.print("> ");
				_wrt.flush();
				String line = getLine(true, false);
				if (line == null) {
					break;
				}
				// quit
				if (line.trim().equalsIgnoreCase("quit")) {
					_continue = false;
					close();
					continue;
				}
				// login
				if (line.trim().startsWith("login")) {
					if (loggedIn == true) {
						_wrt.print("Already logged in\r\n");
						continue;
					}
					if (line.trim().length() == 5) {
						_wrt.print("Login: ");
						_wrt.flush();
						String u = getLine(true, true);
						if (u == null) {
							continue;
						}
						_wrt.print("Password: ");
						_wrt.flush();
						String p = getLine(false, true);
						if (p == null) {
							continue;
						}
						line = line + " " + u + " " + p;
					}
					Object o = context.execute(line);
					if (o instanceof Boolean) {
						loggedIn = ((Boolean) o).booleanValue();
						_user = "logged in";
					}
					continue;
				}
				if (loggedIn == false && mustLogin) {
					_wrt.print("Not logged in\r\n");
					_wrt.flush();
					continue;
				}
				// user is logged in
				Object task = setTimer();
				Object result = context.execute(line);
				Object translation = null;
				if (result != null) {
					// Hangs for some reason, looks like deadlock???
					//for ( Enumeration e=_targets.elements();
					// translation==null && e.hasMoreElements(); )
					//{
					//  CommandProvider p = (CommandProvider) e.nextElement();
					//  System.out.println( "Will translate " + p );
					//  translation = p.toString( result );
					//  System.out.println( "Hase done translate " + p + " " +
					// translation );
					//}
					if (translation == null)
						translation = handleEol(toString(result), context);
					_wrt.print(translation);
					_wrt.print("\r\n");
					cancel(task);
				}
				else {
					_wrt.print("\r\n");
					_wrt.flush();
				}
			}
		}
		catch (Exception e) {
			_wrt.println("BYE: " + e);
			return;
		}
		context.close();
	}

	static String justify(String s, int w, int type) {
		int diff = w - s.length();
		if (diff > 0) {
			switch (type) {
				case 'L' :
					return s + spaces(diff);
				case 'C' :
					return spaces(diff / 2) + s
							+ spaces(w - s.length() - diff / 2);
				case 'R' :
					return spaces(diff) + s;
			}
		}
		if (w > 0)
			return s.substring(0, w);
		else
			return s;
	}

	static String	spaces	= "                                                     ";

	static String spaces(int n) {
		if (n < spaces.length())
			return spaces.substring(0, n);
		else
			return spaces + spaces(n - spaces.length());
	}

	static Object	timer;

	Object setTimer() {
		/*
		 * try { if ( timer == null ) timer = new Timer();
		 * 
		 * final Thread currentThread = Thread.currentThread();
		 * 
		 * TimerTask task = new TimerTask() { int cnt; public void run() { if (
		 * cnt++ == 0 ) currentThread.interrupt(); else if ( cnt == 1 )
		 * currentThread.stop(new IllegalStateException("Command takes too
		 * long")); else if ( cnt == 3 ) currentThread.stop(); else if ( cnt >=
		 * 4 ) this.cancel(); } }; ((Timer)timer).scheduleAtFixedRate( task,
		 * 30000, 10000 ); return task; } catch( Error e ) {}
		 */
		return null;
	}

	void cancel(Object o) {
		/*
		 * if ( o != null ) ((TimerTask)o).cancel();
		 */
	}

	private String handleEol(String str, CommandInterpreter intp) {
		String eol = (String) intp.getVariable("eol");
		if (eol.equals("\r\n")) {
			return str;
		}
		return replaceAll(str, eol);
	}

	String replaceAll(String s, String eol) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\r' :
					break;
				case '\n' :
					sb.append(eol);
					break;
				default :
					sb.append(c);
			}
		}
		return sb.toString();
	}
}
