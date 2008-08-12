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

import java.io.File;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import com.icl.saxon.aelfred.SAXParserImpl;

/**
 * Class to read an eclipse .classpath file
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * 
 * Adapted by Peter Kriens
 * 
 * @version Eclipse 3.0
 */

public class EclipseProject {
	Hashtable			properties;
	File				eclipseProject;
	boolean				root;
	File				workspace;
	File				projectFile;
	Locator				locator;
	Vector				sourceFolders	= new Vector();
	static final String	PATHSEP			= File.pathSeparator;	// This
	List				sourcepath		= new Vector();
	List				classpath		= new Vector();
	List				bootclasspath	= new Vector();
	String				buildpath		= null;
	String				bindir;
	List				before			= new Vector();
	int					level;
	Hashtable			dependingOn		= new Hashtable();

	public EclipseProject(File eclipseProject, boolean root) {
		this.eclipseProject = eclipseProject;
		this.root = root;
	}

	public String getSourcepath() {
		return toPath(sourcepath);
	}

	public String getClasspath() {
		return toPath(classpath);
	}

	/**
	 * Make into a path and remove duplicates.
	 * 
	 * @param c
	 * @return
	 */
	String toPath(Collection c) {
		if (c == null || c.isEmpty())
			return null;

		StringBuffer sb = new StringBuffer();
		String del = "";
		HashSet set = new HashSet();
		for (Iterator i = c.iterator(); i.hasNext();) {
			Object entry = i.next();
			if (set.contains(entry))
				continue;
			set.add(entry);
			sb.append(del);
			sb.append(entry);
			del = PATHSEP;
		}
		return sb.toString();
	}

	public String getBootclasspath() {
		return toPath(bootclasspath);
	}

	public String getBindir() {
		return checkNull(bindir);
	}

	void addSourcepath(String path) {
		sourcepath.add(path);
	}

	void addClasspath(String path) {
		if (isBoot(path))
			addBootclasspath(path);
		else {
			classpath.add(path);
		}
	}

	boolean isBoot(String path) {
		if (path == null)
			return false;
		File file = new File(path);
		return file.getName().startsWith("ee.");
	}

	void addBootclasspath(String path) {
		bootclasspath.add(path);
	}

	void setBindir(String path) {
		bindir = path;
	}

	void execute(Hashtable properties) {
		try {
			this.properties = properties;
			if (!eclipseProject.isDirectory()) {
				throw new IllegalArgumentException(eclipseProject
						+ " is not a directory");
			}
			workspace = eclipseProject.getParentFile();
			projectFile = new File(eclipseProject, ".classpath");
			if (!projectFile.exists()) {
				if (classpath == null) {
					throw new RuntimeException("No classpath for "
							+ eclipseProject.getPath());
				}
				else
					return;
			}
			else {
				SAXParserImpl saxParser = new SAXParserImpl();
				saxParser.parse(projectFile, new Handler());
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e);
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
	class Handler extends DefaultHandler {
		public void startElement(String uri, String localName, String tag,
				Attributes attrs) throws SAXException {

			if (tag.equals("classpath"))
				doClasspath(attrs);
			else if (tag.equals("classpathentry"))
				doClasspathEntry(attrs);
			else
				System.err.println("Invalid tag in .classpath file, ignored "
						+ tag);
		}

		void doClasspath(Attributes attrs) {
		}

		void doClasspathEntry(Attributes attrs) {
			String kind = attrs.getValue("kind");
			String path = attrs.getValue("path");
			String sourcePath = attrs.getValue("sourcepath");
			boolean exported = false;
			String s = attrs.getValue("exported");
			if (s != null)
				exported = Boolean.valueOf(s).booleanValue();

			if (path.equals(""))
				path = ".";

			if ("src".equals(kind))
				doSrc(path, exported);
			else if ("lib".equals(kind))
				doLib(path, sourcePath, exported);
			else if ("output".equals(kind))
				doOutput(path);
			else if ("con".equals(kind))
				/* ignore */;
			else
				System.out.println("Unknown kind: " + kind);
		}
	}

	/**
	 * 
	 * &lt;classpathentry kind="src" path="/org.osgi.framework"/&gt;
	 * 
	 * @param path
	 * @param exported
	 */
	void doSrc(String path, boolean exported) {
		File file = getPath(path);
		String absolute = file.getAbsolutePath();
		sourceFolders.add(absolute);

		if (path.startsWith("/")) {
			// Absolute directory, == other project
			// Let another eclipse handle it ...
			if (root || exported) {
				EclipseProject cp = getEclipseProject(path);
				if (cp.level <= level)
					cp.level = level + 1;
				before.add(cp);
				cp.execute(properties);
				classpath.addAll(cp.classpath);
			}
			// We must ignore non-exports from foreign projects
		}
		else {
			// A local src dir in current project
			addSourcepath(absolute);
			addClasspath(absolute);
		}
	}

	/**
	 * 
	 * &lt;classpathentry sourcepath="/osgi.released/ee.foundation.jar"
	 * kind="lib" path="/osgi.released/ee.foundation.jar" /&gt;
	 * 
	 * @param path
	 * @param sourcePath
	 * @param exported
	 */
	void doLib(String path, String sourcePath, boolean exported) {
		if (root || exported) {
			File libFile = getPath(path);
			if (!libFile.exists())
				throw new RuntimeException("No such lib " + path
						+ " for project " + this);

			addClasspath(libFile.getAbsolutePath());
		}
		// Ignore non exported entries for foreign projects
	}

	/**
	 * &ltclasspathentry kind="output" path="src"/&gt;
	 * 
	 * @param path
	 */
	void doOutput(String path) {
		path = getPath(path).getAbsolutePath();
		setBindir(path);
		addClasspath(path);
	}

	EclipseProject getEclipseProject(String path) {
		File project = new File(workspace, path);
		EclipseProject cp = (EclipseProject) dependingOn.get(project);
		if (cp == null) {
			cp = new EclipseProject(project, false);
			dependingOn.put(project, cp);
		}
		return cp;
	}

	File getPath(String path) {
		if (path == null)
			return null;

		if (path.equals(""))
			path = ".";

		if (path.startsWith("/"))
			return new File(workspace, path.substring(1));
		else
			return new File(eclipseProject, path);
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
				del = PATHSEP;
				sb.append(dir.getAbsolutePath());
			}
		}
		return buildpath = sb.toString();
	}

	/**
	 * @return
	 */
	String checkNull(String s) {
		if (s == null)
			return s;
		return s.length() == 0 ? null : s;
	}

}