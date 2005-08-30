package org.osgi.tools.btool;

import java.io.*;
import java.util.*;

public class PermissionResource extends FileResource {
	PermissionResource(BTool btool, File file) {
		super(btool, "OSGI-INF/permissions.perm", file, true);
	}
	
	protected int getLineLength() { return 0; }

	public String replace(String key) throws IOException {
		if (key.equals("PACKAGEPERMISSIONS")) {
			Collection imports = btool.getImports();
			StringBuffer sb = new StringBuffer();
			sb.append("# Imports\r\n");
			for (Iterator e = imports.iterator(); e.hasNext();) {
				Package rr = (Package) e.next();
				sb.append("(org.osgi.framework.PackagePermission \"");
				sb.append(rr.getName());
				sb.append("\" \"import\")\r\n");
			}
			Collection exports = btool.getExports();
			sb.append("# Exports\r\n");
			for (Iterator e = exports.iterator(); e.hasNext();) {
				Package s = (Package) e.next();
				sb.append("(org.osgi.framework.PackagePermission \"");
				sb.append(s.getName());
				sb.append("\" \"import,export\")\r\n");
			}
			return sb.toString();
		}
		else
			if (key.equals("AUTOSERVICEPERMISSIONS")) {
				Collection imports = btool.getImports();
				StringBuffer sb = new StringBuffer();
				sb.append("# Derived from imports\r\n");
				for (Iterator e = imports.iterator(); e.hasNext();) {
					Package s = (Package) e.next();
					if (s.getName().indexOf(".service.") > 0)
						sb.append("(org.osgi.framework.ServicePermission \""
								+ s.getName() + ".*\" \"get\")\r\n");
				}
				Collection exports = btool.getExports();
				sb.append("# Derived from exports\r\n");
				for (Iterator e = exports.iterator(); e.hasNext();) {
					PackageResource s = (PackageResource) e.next();
					if (s.getName().indexOf(".service.") > 0)
						sb.append("(org.osgi.framework.ServicePermission \""
								+ s + ".*\" \"get,register\")\r\n");
				}
				return sb.toString();
			}
			else
				if (key.equals("ADMIN")) {
					return "(org.osgi.framework.AdminPermission)";
				}
				else
					return null;
	}
}