package org.osgi.tools.ipaviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class IPAViewer {

	public static void main(String args[]) throws FileNotFoundException, IOException {
		for (int i = 0; i < args.length; i++) {
			view(args[i]);
		}
	}

	static void view(String file) throws FileNotFoundException, IOException {
		File f = new File(file);
		if (!f.exists())
			System.err.println("Does not exist: " + f.getAbsoluteFile());

		JarInputStream jin = new JarInputStream(new FileInputStream(f));
		while (true) {
			JarEntry jentry = jin.getNextJarEntry();
			String extra = "";
			if ( jentry == null )
				break;
			
			byte [] ex = jentry.getExtra();
			if ( ex != null ) {
				extra = new String(ex);
				extra += " ";
				extra += Arrays.toString(ex);
			}
			System.out.printf("%40s %10s\n", new Object[] {jentry.getName(),
					extra});
		}
	}
}
