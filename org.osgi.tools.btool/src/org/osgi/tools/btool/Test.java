package org.osgi.tools.btool;

import java.io.*;

public class Test {
	public static void main(String args[]) throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream(args[0]);
		byte buffer[] = new byte[1024];
		int size = in.read(buffer);
		while (size > 0) {
			System.out.println(new String(buffer, 0, size));
			size = in.read(buffer);
		}
	}
}
