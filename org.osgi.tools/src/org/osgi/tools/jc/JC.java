package org.osgi.tools.jc;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class JC {
	static void print(InputStream left, InputStream right, PrintStream out,
			String level) throws Exception {
		ZipInputStream lZip = new ZipInputStream(left);
		ZipInputStream rZip = new ZipInputStream(right);
		Set lSet = new TreeSet();
		Set rSet = new TreeSet();
		Map map = new HashMap();
		ZipEntry entry = lZip.getNextEntry();
		while (entry != null) {
			lSet.add(entry.getName());
			if (entry.getName().endsWith(".jar")) {
				byte[] data = read(lZip, 0);
				map.put(entry.getName(), data);
			}
			entry = lZip.getNextEntry();
		}
		entry = rZip.getNextEntry();
		while (entry != null) {
			rSet.add(entry.getName());
			if (entry.getName().endsWith(".jar")) {
				byte[] rData = read(rZip, 0);
				byte[] lData = (byte[]) map.get(entry.getName());
				if (lData != null) {
					System.out.print(level + "Embedded JAR " + entry.getName()
							+ " ");
					print(new ByteArrayInputStream(lData),
							new ByteArrayInputStream(rData), out, level + "  ");
				}
			}
			entry = rZip.getNextEntry();
		}
		Set orglSet = new TreeSet(lSet);
		Set orgrSet = new TreeSet(rSet);
		lSet.removeAll(orgrSet);
		rSet.removeAll(orglSet);
		if (lSet.isEmpty() && rSet.isEmpty())
			out.println("==");
		else {
			print(out, level + "[*-] ", lSet);
			print(out, level + "[-*] ", rSet);
		}
	}

	static byte[] read(InputStream in, int prevSize) throws Exception {
		byte[] buffer = new byte[1024];
		int size = in.read(buffer);
		if (size <= 0) {
			byte[] data = new byte[prevSize];
			return data;
		}
		byte data[] = read(in, prevSize + size);
		System.arraycopy(buffer, 0, data, prevSize, size);
		return data;
	}

	static void print(PrintStream out, String prefix, Set set) {
		for (Iterator i = set.iterator(); i.hasNext();) {
			String path = (String) i.next();
			out.print(prefix);
			out.println(path);
		}
	}

	public static void main(String args[]) throws Exception {
		InputStream left = new FileInputStream(args[0]);
		String rname = args[1];
		if (!rname.endsWith(".jar")) {
			String lname = args[0];
			int n = lname.lastIndexOf("/");
			if (n < 0)
				n = lname.lastIndexOf("\\");
			if (n < 0)
				n = 0;
			File dir = new File(rname);
			File f = new File(dir, lname.substring(n));
			rname = f.getAbsolutePath();
		}
		InputStream right = new FileInputStream(rname);
		print(left, right, System.out, "");
	}
}