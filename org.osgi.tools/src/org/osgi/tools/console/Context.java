package org.osgi.tools.console;

import java.lang.reflect.*;
import java.util.StringTokenizer;
import org.osgi.framework.BundleException;
import org.osgi.tools.command.CommandInterpreter;

class Context implements CommandInterpreter {
	Handler			_handler;
	StringTokenizer	_st;
	String			_current;

	Context(Handler handler) {
		_handler = handler;
	}

	public boolean nextLine() {
		try {
			_st = new StringTokenizer(_handler.getNextLine(true, false),
					" \t\"\'^={}[]", true);
			nextArgument();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public String getUser() {
		return _handler.getUser();
	}

	public Object getVariable(String name) {
		return _handler.getVariable(name);
	}

	public void setVariable(String name, Object value) {
		_handler.setVariable(name, value);
	}

	public String nextArgument() {
		String old = _current;
		if (old != null && old.startsWith("$")) {
			Object replace = _handler.getVariable(old.substring(1));
			if (replace != null)
				old = Handler.toString(replace);
		}
		if (old != null && old.startsWith("`")) {
			Context context = new Context(_handler);
			Object result = context.execute(old.substring(1, old.length() - 1));
			if (result != null)
				old = Handler.toString(result);
		}
		if (_st.hasMoreTokens()) {
			_current = _st.nextToken();
			while (in(" \t") && _st.hasMoreTokens())
				_current = _st.nextToken();
			if (!_st.hasMoreTokens() && _current.trim().length() == 0)
				_current = null;
			if (in("\"'`") && _st.hasMoreTokens()) {
				StringBuffer sb = new StringBuffer();
				String delimeter = _current;
				_current = _st.nextToken();
				while (_current != null && !_current.equals(delimeter)) {
					sb.append(_current);
					_current = _st.nextToken();
				}
				if (_st.hasMoreTokens())
					_st.nextToken();
				_current = sb.toString();
				if (delimeter.equals("`"))
					_current = "`" + _current + "`";
			}
		}
		else
			_current = null;
		return old;
	}

	boolean in(String delimeters) {
		return _current != null && delimeters.indexOf(_current) >= 0;
	}

	String token() {
		return _current;
	}

	public Object execute(String line) {
		Class[] parameterTypes = new Class[] {CommandInterpreter.class};
		Object[] parameters = new Object[] {this};
		_st = new StringTokenizer(line, " \t\"\'^=[]{},", true);
		nextArgument();
		String cmd = nextArgument();
		if (cmd.equals("delete") || cmd.equals("create")
				|| cmd.equals("update") || cmd.equals("list")
				|| cmd.equals("open") || cmd.equals("show")
				|| cmd.equals("close") || cmd.equals("get"))
			cmd = cmd + nextArgument();
		Object services[] = _handler._console.providers();
		for (int service = 0; services != null && service < services.length; service++)
			try {
				Object target = services[service];
				Method method = target.getClass().getMethod("_" + cmd,
						parameterTypes);
				return method.invoke(target, parameters);
			}
			catch (NoSuchMethodException ite) {
			}
			catch (IllegalAccessException ite) {
				ite.printStackTrace();
			}
			catch (RuntimeException ite) {
				ite.printStackTrace();
				return "ERROR: Unexpected exception " + ite + ":"
						+ ite.getMessage();
			}
			catch (InvocationTargetException ite) {
				ite.getTargetException().printStackTrace();
				if (ite.getTargetException() instanceof BundleException) {
					BundleException be = (BundleException) ite
							.getTargetException();
					return "ERROR: Bundle Exception " + cmd + " " + be.getMessage() + " ("
							+ be.getNestedException() + " )";
				}
				return "ERROR: Command resulted in exception "
						+ ite.getTargetException();
			}
			catch (Exception ee) {
				return "ERROR: Invalid cmd " + cmd + " " + ee;
			}
		Object var = getVariable(cmd);
		if (var != null)
			return var;
		return "ERROR: No such command " + cmd + " found, try help";
	}

	void close() {
		_current = null;
		_handler = null;
		_st = null;
	}

	public Object error(String msg) {
		return "ERROR: " + msg;
	}
}
