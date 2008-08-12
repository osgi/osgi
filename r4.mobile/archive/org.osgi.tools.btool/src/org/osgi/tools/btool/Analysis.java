/*
 * Created on Jul 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.osgi.tools.btool;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * @author Peter Kriens
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Analysis {
	BTool			btool;
	String			zipfile;
	Manifest		manifest;

	Analysis(BTool btool, Dependencies dependencies, String zipfile) {
		this.btool = btool;
		this.zipfile = zipfile;
	}

	/**
	 *  
	 */
	public void execute() throws Exception {
		ZipFile zip = new ZipFile(new File(zipfile));
		try {
			ZipEntry entry = zip.getEntry("META-INF/MANIFEST.MF");
			manifest = new Manifest(btool, zip.getInputStream(entry));
			checkActivator();
			checkSymbolicName();
		}
		finally {
			zip.close();
		}
	}


	private void checkSymbolicName() {
		String name = manifest.getValue("Bundle-SymbolicName");
		if ( name == null ) 
			btool.warnings.add("Bundle-SymbolicName not set");
		else if ( name!=null && !name.matches("[a-zA-Z_0-9]+(\\.[a-zA-Z_0-9]+)+") )
			btool.warnings.add("Bundle-SymbolicName not a revere domain name: " + name );
		
		String version = manifest.getValue("Bundle-Version");
		if ( version == null )
			btool.warnings.add("No version set");
		else if ( ! version.matches("[0-9]+(\\.[0-9]+(\\.[0-9]+([-\\.][-0-9_a-zA-Z]+)?)?)?"))
			btool.warnings.add("Version not in right format: " + version );			
	}

	private void checkActivator() throws IOException {
		String activator = manifest.getValue("Bundle-Activator");
		if (activator == null)
			return;
		activator = activator.replace('.', '/') + ".class";
		Resource activatorResource = (Resource) btool.contents.get(activator);
		if (activatorResource != null)
			return;
		// The activator might come from the import
		String pack = Dependencies.base(activator);
		Dependencies deps = btool.initDependencies();
		if (deps.getImported().contains(pack)) {
			System.out.println("Check act " + pack + " " + deps.referred);
			btool.warnings.add("Activator from import " + activator);
			return;
		}
		btool.errors.add("Activator not found " + activator);
	}

	void report() throws IOException {
		System.out
				.println("-------------------------------------------------------");
		prt("Imports         : ", btool.getImports());
		prt("Exports         : ", btool.getExports());
		prt("Private         : ", btool.getPrivates());
		prt("Activator       : ", manifest.getValue("Bundle-Activator"));
		System.out
				.println("-------------------------------------------------------");
	}

	private void prt(String string, String value) {
		System.out.print(string);
		System.out.println(value);
	}

	private void prt(String string, Collection imports) {
		for (Iterator i = imports.iterator(); i.hasNext();) {
			System.out.print(string);
			System.out.println(i.next());
			string = "                  ";
		}
	}
}