/*
 * $Header$
 *
 * Eclipse .classpath parser
 *
 * OSGi Alliance Confidential.
 *
 * (C) Copyright IBM Corporation 2004
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.tools.btool;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Class to read an eclipse .classpath file
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version Eclipse 3.0
 */
public class EclipseProject {
	private final boolean	DEBUG			= false;
	protected Hashtable				properties;
	protected File					eclipseProject;
	protected boolean				root;
	protected File					workspace;
	protected File					file;
	protected Locator				locator;
	Vector							sourceFolders	= new Vector();
	final String				PATHSEP			= ",";
	private StringBuffer			sourcepath		= new StringBuffer(1024);
	private StringBuffer			classpath		= new StringBuffer(1024);
	private StringBuffer			bootclasspath	= new StringBuffer(1024);
	private String					buildpath		= null;
	private String					bindir;
	List							before			= new Vector();
	int								level;

	public EclipseProject(File eclipseProject, boolean root) {
		this.eclipseProject = eclipseProject;
		this.root = root;
	}

	public String getSourcepath() {
		return checkNull(sourcepath.toString());
	}

	/**
	 * @return
	 */
	private String checkNull(String s) {
		return s.length() == 0 ? null : s;
	}

	public String getClasspath() {
		return checkNull(classpath.toString());
	}

	public String getBootclasspath() {
		return checkNull(bootclasspath.toString());
	}

	public String getBindir() {
		return checkNull(bindir);
	}

	protected void addSourcepath(String path) {
		if (sourcepath.length() > 0) {
			sourcepath.append(PATHSEP);
		}
		sourcepath.append(path);
	}

	protected void addClasspath(String path) {
		if (classpath.length() > 0) {
			classpath.append(PATHSEP);
		}
		classpath.append(path);
	}

	protected void addBootclasspath(String path) {
		if (bootclasspath.length() > 0) {
			bootclasspath.append(PATHSEP);
		}
		bootclasspath.append(path);
	}

	protected void setBindir(String path) {
		bindir = path;
	}

	protected void execute(Hashtable properties) {
		try {
			this.properties = properties;
			if (!eclipseProject.isDirectory()) {
				throw new IllegalArgumentException(eclipseProject
						+ " is not a directory");
			}
			workspace = eclipseProject.getParentFile();
			file = new File(eclipseProject, ".classpath");
			if (!file.exists()) {
				if (DEBUG) {
					System.out.println("No classpath for "
							+ eclipseProject.getPath());
				}
				return;
			}
			if (DEBUG) {
				System.out.println("file=" + file.getAbsolutePath());
			}
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(file, new Handler());
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void traverse(EclipseProject project, Set l) {
		l.add(project);
		for (Iterator it = project.before.iterator(); it.hasNext();) {
			EclipseProject next = (EclipseProject) it.next();
			traverse(next, l);
		}
	}

	/**
	 * Handler for the "classpathentry" element
	 * 
	 * Examples: <classpathentry kind="src" path="src"/> <classpathentry
	 * kind="src" path="/org.osgi.framework"/> <classpathentry kind="con"
	 * path="org.eclipse.jdt.launching.JRE_CONTAINER"/> <classpathentry
	 * kind="lib" path="/jar/servlet.jar"/> <classpathentry kind="lib"
	 * path="myfolder/my.jar"/> <classpathentry kind="lib"
	 * path="C:/lib/some.jar"/> <classpathentry kind="var"
	 * path="JUNIT_HOME/junit.jar"/> <classpathentry kind="output" path="bin"/>
	 * 
	 * For kind="var", the variable is always the first path component.
	 * 
	 * We do not need to do anything with rootpath or sourcepath except
	 * tolerate.
	 * 
	 * We must handle exported attribute. All kind="src" entry for this folder
	 * are automatically exported.
	 */
	private class Handler extends DefaultHandler {
		public void startElement(String uri, String localName, String tag,
				Attributes attrs) throws SAXException {
			if (DEBUG) {
				System.out.print(tag + ":");
			}
			if (!tag.equals("classpathentry")) {
				return;
			}
			String kind = null;
			String path = null;
			String output = null;
			boolean exported = false;
			int size = attrs.getLength();
			for (int i = 0; i < size; i++) {
				String key = attrs.getQName(i);
				String value = attrs.getValue(i);
				if (key.equals("kind")) {
					if (DEBUG) {
						System.out.print(" kind=" + value);
					}
					kind = value;
				}
				else
					if (key.equals("path")) {
						if (DEBUG) {
							System.out.print(" path=" + value);
						}
						if (value.equals("")) {
							path = ".";
						}
						else {
							path = value;
						}
					}
					else
						if (key.equals("output")) {
							if (DEBUG) {
								System.out.print(" output=" + value);
							}
							output = value;
						}
						else
							if (key.equals("rootpath")) {
								// we don't need this attribute
							}
							else
								if (key.equals("sourcepath")) {
									// we don't need this attribute
								}
								else
									if (key.equals("exported")) {
										exported = Boolean.valueOf(value)
												.booleanValue();
									}
									else
										if (key.equals("excluding")) {
											// we don't need this attribute
										}
										else {
											throw new SAXParseException(
													"Unexpected attribute \""
															+ key + "\"",
													locator);
										}
			}
			if (DEBUG) {
				System.out.println();
			}
			if ("src".equals(kind)) {
				/*
				 * src is either a source folder within the current project dir
				 * or another project dir.
				 */
				sourceFolders.add(path);
				if (path.startsWith("/")) {
					if (root || exported) {
						File otherEclipseProject = new File(workspace, path);
						EclipseProject cp;
						try {
							cp = new EclipseProject(otherEclipseProject, false);
							cp.level = level + 1;
							before.add(cp);
							cp.execute(properties);
							if (path.startsWith("/ee.")) {
								addBootclasspath(cp.getClasspath());
							}
							else {
								addClasspath(cp.getClasspath());
							}
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else {
					File srcFile = new File(eclipseProject, path);
					path = srcFile.getAbsolutePath();
					addSourcepath(path);
					if (output != null) {
						File outDir = new File(srcFile, output);
						path = outDir.getAbsolutePath();
						addClasspath(path);
					}
				}
			}
			else
				if ("lib".equals(kind)) {
					/*
					 * lib is a jar/zip file or a directory.
					 */
					if (root || exported) {
						if (path.startsWith("/")) { // relative to workspace
							File libFile = new File(workspace, path.substring(1));
							if (libFile.exists()) {
								path = libFile.getAbsolutePath();
							}
							else
								System.err.println("Non existent lib in classpath: " + path );
						}
						else {
							File libFile = new File(eclipseProject, path);
							if (libFile.exists()) {
								path = libFile.getAbsolutePath();
							}
							else
								System.err.println("Non existent lib in classpath: " + path );
						}
						addClasspath(path);
					}
				}
				else
					if ("var".equals(kind)) {
						/*
						 * var is a dir or jar/zip file the first component of
						 * which is a variable name.
						 */
						if (root || exported) {
							int index = path.indexOf('/');
							String variable = (index < 0) ? path : path
									.substring(0, index);
							String value = (String) properties.get(variable);
							if (value == null) {
								value = variable;
							}
							if (value == null) {
								throw new SAXParseException(
										"Undefined property \"" + value + "\"",
										locator);
							}
							String result = (index < 0) ? value : (value + path
									.substring(index + 1));
							addClasspath(result);
						}
					}
					else
						if ("output".equals(kind)) {
							/*
							 * output is the output folder within the current
							 * eclipse dir.
							 */
							path = new File(eclipseProject, path)
									.getAbsolutePath();
							setBindir(path);
							addClasspath(path);
						}
						else
							if ("con".equals(kind)) {
								/*
								 * con is a container. Don't know how to find
								 * its value :-(
								 */
							}
							else {
								throw new SAXParseException(
										"Unexpected attribute value \"" + kind
												+ "\"", locator);
							}
		}
	}

	/**
	 * @return
	 */
	static String[]	EMPTY	= new String[0];

	public String[] getSourceFolders() {
		return (String[]) sourceFolders.toArray(EMPTY);
	}

	public String toString() {
		return "P@" + eclipseProject + "|" + level;
	}

	/**
	 * @return
	 */
	public String getBuildPath() {
		if (buildpath != null)
			return buildpath;
		//
		// Now build the dependency tree
		//
		StringBuffer sb = new StringBuffer();
		if (root) {
			Comparator c = new Comparator() {
				public int compare(Object o1, Object o2) {
					EclipseProject p1 = (EclipseProject) o1;
					EclipseProject p2 = (EclipseProject) o2;
					if (p1.level != p2.level)
						return p2.level - p1.level;
					return p2.eclipseProject.compareTo(p1.eclipseProject);
				}
			};
			TreeSet set = new TreeSet(c);
			traverse(this, set);
			String del = "";
			Set visited = new HashSet();
			for (Iterator it = set.iterator(); it.hasNext();) {
				EclipseProject ep = (EclipseProject) it.next();
				if (visited.contains(ep))
					continue;
				if (ep.root)
					continue;
				visited.add(ep);
				File dir = ep.eclipseProject;
				sb.append(del);
				del = ",";
				sb.append(dir.getAbsolutePath());
			}
		}
		return buildpath = sb.toString();
	}
}