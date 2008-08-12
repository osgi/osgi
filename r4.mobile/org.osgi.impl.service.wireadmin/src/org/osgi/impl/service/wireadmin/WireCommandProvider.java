package org.osgi.impl.service.wireadmin;

import org.osgi.framework.*;
import org.osgi.tools.command.*;
import org.osgi.service.wireadmin.*;
import java.util.*;

public class WireCommandProvider implements CommandProvider {
	BundleContext	bc			= null;
	WireAdminImpl	wireAdmin	= null;

	public WireCommandProvider(BundleContext bc, WireAdminImpl wireAdmin) {
		this.bc = bc;
		this.wireAdmin = wireAdmin;
	}

	public String getHelp() {
		return "WIRES\n" + "connect fromPid toPid [ key = value ] *\r\n"
				+ "delete wire pid\r\n" + "list wires [ pid ]\r\n";
	}

	public Object toString(Object o) {
		return null;
	}

	public Object _connect(CommandInterpreter ci) {
		String fromPid = ci.nextArgument();
		String toPid = ci.nextArgument();
		Hashtable ht = new Hashtable();
		String key = ci.nextArgument();
		while (key != null) {
			String val = ci.nextArgument();
			if (val.equals("=")) {
				val = ci.nextArgument();
				if (val != null)
					ht.put(key, val);
				key = ci.nextArgument();
			}
			else {
				ht.put(key, "");
				key = val;
			}
		}
		System.out.println("connect ( " + fromPid + ", " + toPid + "," + ht
				+ " )");
		return wireAdmin.createWire(fromPid, toPid, ht);
	}

	public Object _listwires(CommandInterpreter ci) {
		try {
			String f = ci.nextArgument();
			if (f == null)
				return wireAdmin.getWires("(service.Pid=*)");
			f = f.trim();
			if (f.startsWith("("))
				return wireAdmin.getWires(f);
			Wire[] wires = wireAdmin.getWires("(service.pid=" + f + ")");
			Vector v = new Vector();
			for (int i = 0; i < wires.length; i++)
				v.addElement(wires[i].getProperties());
			return v;
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public Object _deletewire(CommandInterpreter ci) {
		String pid = ci.nextArgument();
		wireAdmin.deleted(pid);
		return null;
	}
}
