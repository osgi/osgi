package org.osgi.tools.build.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.osgi.tools.btool.parser.*;

public class ExpandProperties extends Task implements Domain {
	File			propertyFile;
	private boolean	override;
	
	public void execute() throws BuildException {
		try {
			if ( propertyFile.exists() ) {
				Parser	p = new Parser(this,propertyFile.getAbsolutePath(), Parser.SHOW);
				InputStream in = p.getInputStream();
				Properties properties = new Properties();
				properties.load(in);
				in.close();
				Project project = getProject();
	
				for ( Iterator i=properties.keySet().iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					String value = properties.getProperty(key);
					if ( override || project.getProperty(key)==null)
						project.setProperty(key,value);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}
	
	public void setOverride(boolean t) {
		override = t;
	}

	public void setPropertyFile( String file ) {
		this.propertyFile = new File(file);
	}

	public String getValue(String key) {
		String value = System.getProperty(key);
		if (value != null)
			return value;

		return getProject().getProperty(key);
	}

	public void warning(String s) {
		System.err.println("* warning * " + s);
	}

	public void error(String s) {
		System.err.println("* error * " + s);
	}
}
