package org.osgi.impl.bundle.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.FrameworkConstants;
import org.osgi.framework.ServiceReference;
import org.osgi.tools.command.CommandInterpreter;

class Handler extends Thread {
	Socket			_socket;
	Console			_console;
	StringTokenizer	_st;
	String			_current;
	PrintWriter		_wrt;
	InputStream		_in;
	OutputStream	_out;
	boolean			_stacktraces;
	boolean			_continue	= true;
	Map<String, Object>	_variables;
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
		_variables = new HashMap<String, Object>();
	}

	void close() {
		_continue = false;
		try {
			_socket.close();
		}
		catch (Exception e) {
			// ignored
		}
		_variables = null;
	}

	public static String toString(Object o) {
		return toString(0, o);
	}

	@SuppressWarnings("unchecked")
	public static String toString(int n, Object o) {
		if (o instanceof Iterable)
			return toString(n, (Iterable) o);
		if (o instanceof Object[])
			return toString(n, (Object[]) o);
		if (o instanceof Bundle)
			return toString(n, (Bundle) o);
		// if (o instanceof LogEntry)
		// return toString(n, (LogEntry) o);
		if (o instanceof ServiceReference)
			return toString(n, (ServiceReference) o);
		if (o instanceof Thread)
			return toString(n, (Thread) o);
		if (o instanceof Map)
			return toString(n, (Map) o);
		 if (o instanceof ExportedPackage)
			return toString(n, (ExportedPackage) o);
		if (o instanceof Throwable)
			return toString(n, (Throwable) o);
		return "" + o;
	}

	 public static String toString(int n, ExportedPackage ep) {
		StringBuilder sb = new StringBuilder();
		sb.append(ep.getName());
		sb.append(spaces(30 - ep.getName().length()));
		sb.append(ep.getVersion());
		return sb.toString();
	}

	public static String toString(int indent, Map<String, Object> d) {
		int max = 0;
		String l[] = new String[d.size()];
		int n = 0;
		for (String name : d.keySet()) {
			if (name.length() > max)
				max = name.length();
			l[n++] = name;
		}
		Arrays.sort(l);
		StringBuilder sb = new StringBuilder();
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

	public static String toString(int n, Throwable throwable) {
		StringBuilder sb = new StringBuilder();
		String del = "";
		while (throwable != null) {
			sb.append(del);
			sb.append(spaces(n));
			sb.append(throwable);
			try {
				StackTraceElement elements[] = throwable.getStackTrace();
				sb.append(" ");
				sb.append(elements[0]);
			}
			catch (Throwable t) {
				// Only works on 1.4
			}
			throwable = throwable.getCause();
			del = "\r\n";
			n += 4;
		}
		return sb.toString();
	}

	public static String toString(int n, Object s[]) {
		StringBuilder sb = new StringBuilder();
		String del = "";
		for (int i = 0; i < s.length; i++)
			if (s[i] != null) {
				sb.append(del);
				sb.append(toString(s[i]));
				del = "\r\n" + spaces(n);
				// del = ",";
			}
		return sb.toString();
	}

	public static String toString(int n, Iterable< ? > v) {
		StringBuilder sb = new StringBuilder();
		String del = "";
		for (Object element : v) {
			if (element != null) {
				sb.append(del);
				del = "\r\n" + spaces(n);
				sb.append(toString(element));
			}
		}
		return sb.toString();
	}

	// public static String toString(int n, LogEntry l) {
	// String level = "UNKNOWN";
	// switch (l.getLevel()) {
	// case LogService.LOG_ERROR :
	// level = "ERROR  ";
	// break;
	// case LogService.LOG_WARNING :
	// level = "WARNING";
	// break;
	// case LogService.LOG_INFO :
	// level = "INFO   ";
	// break;
	// case LogService.LOG_DEBUG :
	// level = "DEBUG  ";
	// break;
	// }
	// Bundle b = l.getBundle();
	// String id = "0";
	// if (b != null)
	// id = "" + b.getBundleId();
	// StringBuilder exception = new StringBuilder();
	// Throwable e = l.getException();
	// while (e != null) {
	// exception.append(" : ");
	// exception.append(e.getMessage());
	// e = e.getCause();
	// }
	// String date = getDate(l.getTime());
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(new Date(l.getTime()));
	// return spaces(n) + date + ":" + level + ":" + id + ": "
	// + l.getMessage() + exception.toString();
	// }
	//
	/**
	 * I know, there is SimpleDateFormat ... it is just not part of the min.
	 * exec. env. ...
	 * 
	 * @param time
	 * @return
	 */
	public static String getDate(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time));
		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(fit(cal.get(Calendar.MONTH), 2));
		sb.append(fit(cal.get(Calendar.DAY_OF_MONTH), 2));
		sb.append(fit(cal.get(Calendar.HOUR_OF_DAY), 2));
		sb.append(fit(cal.get(Calendar.MINUTE), 2));
		return sb.toString();
	}

	// TODO innefficient
	public static String fit(int n, int width) {
		String number = Integer.toString(n);
		int diff = width - number.length();
		if (diff <= 0)
			return number;

		return "000000000000".substring(0, diff) + number;
	}

	static String status(Bundle b) {
		return b.getState().toString();
	}

	public static String toString(int n, Bundle b) {
		return spaces(n) + justify(b.getBundleId() + " ", 8, 'L') + " "
				+ justify(status(b), 12, 'L') + justify(getName(b), 60, 'L');
	}

	static String getName(Bundle b) {
		String name = b.getSymbolicName();
		if (name != null)
			return name;

		name = b.getHeaders().get("Bundle-Name");
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
	public static String toString(int n, ServiceReference< ? > ref) {
		StringBuilder sb = new StringBuilder();
		String[] ocs = (String[]) ref
				.getProperty(FrameworkConstants.OBJECTCLASS);
		for (int i = 0; ocs != null && i < ocs.length; i++) {
			String id = ref.getProperty(FrameworkConstants.SERVICE_ID) + "";
			long bundle = ref.getBundle().getBundleId();
			String pid = (String) ref
					.getProperty(FrameworkConstants.SERVICE_PID);
			String name = (String) ref.getProperty("service.name");
			String description = (String) ref
					.getProperty(FrameworkConstants.SERVICE_DESCRIPTION);
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

	static List<String>	_history	= new ArrayList<String>();

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
							// _out.flush();
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
						String l = _history
								.get(_history.size() - 1);
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
			_history.add(line);
			if (_history.size() > 20)
				_history.remove(0);
		}
		_wrt.print("\r\n");
		return line;
	}

	String getNextLine(boolean _echo, boolean _break) throws IOException {
		_wrt.print(">> ");
		_wrt.flush();
		return getLine(_echo, _break);
	}

	@Override
	public void run() {
		Context context = new Context(this);
		boolean loggedIn = false;
		try {
			context.setVariable("eol", new String("\r\n"));
			// _user = System.getProperty("user.name", "");
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
				PrintStream err = System.err;
				PrintStream out = System.out;
				InputStream in  = System.in;
				try {
					System.setErr(new PrintStream(_out));
					System.setOut(new PrintStream(_out));
					if ( _in.available() > 0 )
						_in.skip(_in.available());
					
					System.setIn(_in);
					Object result = context.execute(line);
					doTranslation(context, task, result);
				}
				finally {
					err.flush();
					out.flush();
					System.setErr(err);
					System.setOut(out);
					System.setIn(in);
				}
			}
		}
		catch (Exception e) {
			_wrt.println("BYE: " + e);
			return;
		}
		context.close();
	}

	private void doTranslation(Context context, Object task, Object result) {
		Object translation = null;
		if (result != null) {
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
		StringBuilder sb = new StringBuilder();
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
