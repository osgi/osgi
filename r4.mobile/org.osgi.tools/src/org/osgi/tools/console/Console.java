package org.osgi.tools.console;

import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.tools.command.CommandProvider;
import org.osgi.util.tracker.ServiceTracker;

public class Console implements BundleActivator, Runnable {
	int				_port		= 2011;
	ServerSocket	_listen;
	boolean			_continue	= true;
	Vector			_handlers;
	BundleContext	_context;
	Hashtable		_globals;
	ServiceTracker	_tracker;

	public void start(BundleContext context) throws Exception {
		_context = context;
		_handlers = new Vector();
		_globals = new Hashtable();
		_context.registerService(CommandProvider.class.getName(), new Basic(
				this, context), null);
		Thread thread = new Thread(this);
		_tracker = new ServiceTracker(context, CommandProvider.class.getName(),
				null);
		_tracker.open();
		try {
			String port = System.getProperty("org.osgi.nursery.console.port");
			_port = Integer.parseInt(port);
		}
		catch (Exception e) {
		}
		thread.start();
	}

	public void stop(BundleContext context) throws Exception {
		_tracker.close();
		_continue = false;
		_listen.close();
		for (Enumeration e = _handlers.elements(); e.hasMoreElements();) {
			Handler h = (Handler) e.nextElement();
			h.close();
		}
		_globals = null;
		_handlers = null;
	}

	public void run() {
		int n = 5;
		while (_continue)
			try {
				_listen = new ServerSocket(_port);
				System.out.println("To use console, do: telnet localhost "
						+ _port);
				while (_continue) {
					Socket s = _listen.accept();
					n = 1;
					//s.setKeepAlive(true); // Not in minimum
					s.setTcpNoDelay(true);
					Handler h = new Handler(this, s);
					_handlers.addElement(h);
					h.start();
				}
			}
			catch (BindException e) {
				_port++;
			}
			catch (Exception e) {
				if (_continue) {
					e.printStackTrace();
					try {
						Thread.sleep(5000 * n++);
						_listen.close();
					}
					catch (Exception ee) {
					}
				}
			}
	}

	void remove(Handler h) {
		_handlers.removeElement(h);
	}

	Object[] providers() {
		return _tracker.getServices();
	}

	public void setGlobal(String name, Object value) {
		_globals.put(name, value);
	}

	public Object getGlobal(String name) {
		Object o = _globals.get(name);
		if (o != null)
			return o;
		return System.getProperty(name);
	}
}
