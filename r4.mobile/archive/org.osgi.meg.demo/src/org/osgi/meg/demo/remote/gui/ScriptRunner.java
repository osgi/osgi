/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.remote.gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.osgi.meg.demo.remote.CommanderException;
import org.osgi.meg.demo.remote.Splitter;

public class ScriptRunner {
	
	private static final String	PROPERTY_FILE = "script.properties";
	
	private Vector 		scriptFiles = new Vector();
	private Commander 	commander;
	private Hashtable	vars = new Hashtable();

	public ScriptRunner(Commander commander) throws Exception {
		this.commander = commander;
		
		Properties props = new Properties();
		props.load(new FileInputStream(PROPERTY_FILE));
		String scriptsStr = props.getProperty("scripts");
		if (null != scriptsStr) {
			String[] scripts = Splitter.split(scriptsStr, ',', 0);
			for (int i = 0; i < scripts.length; i++)
				scriptFiles.add(scripts[i]);
		}
	}
	
	public Vector getscriptFiles() {
		return scriptFiles;
	}
	
	public String runScript(String file) throws CommanderException, IOException {
		BufferedReader reader = new BufferedReader(
			new FileReader(file));
		StringBuffer result = new StringBuffer();
		String commandResult = null;
		
		try {
			String line = reader.readLine();
			while (null != line) {
				if (null != line)
					result.append(line + "\n");

				if (!isComment(line) && !line.trim().equals("")) {
					if (isMacroCommand(line)) {
						execMacroCommand(line, commandResult.trim());
					} else {
						line = substitute(line);
						commandResult = commander.command(line);
						if (null != commandResult)
							result.append(commandResult);
					}
				}
				line = reader.readLine();
			}
			return result.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != reader)
				try {
					reader.close();
				}
				catch (IOException e) {
				}
		}
	}
	
	private String substitute(String line) {
		StringBuffer l = new StringBuffer(line);
		int s = l.indexOf("<$");
		int e = l.indexOf("$>");
		while (-1 != s) {
			String arrayExpr = l.substring(s + 2, e);
			int sb = arrayExpr.indexOf("[");
			int eb = arrayExpr.indexOf("]");
			String arrayName = arrayExpr.substring(0, sb);
			int arrayIndex = Integer.parseInt(arrayExpr.substring(sb + 1, eb));
			String[] arr = (String[]) vars.get(arrayName);
			l.replace(s, e + 2, arr[arrayIndex]);
			
			s = l.indexOf("<$");
			e = l.indexOf("$>");
		}
		return l.toString();
	}
	
	private boolean isMacroCommand(String line) {
		return line.startsWith("<$");
	}
	
	private boolean isComment(String line) {
		return line.startsWith("#");
	}

	private void execMacroCommand(String line, String commandResult) {
		if (line.startsWith("<$toArray$>")) {
			String[] a = line.split(" ", 0);
			String separator = a[1];
			String arrayName = a[2];
			String[] arrayValue = commandResult.split(separator, 0);
			vars.put(arrayName, arrayValue);
		}
	}
	
}
