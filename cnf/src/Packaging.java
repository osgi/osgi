import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.lib.osgi.Analyzer;
import aQute.lib.osgi.Constants;
import aQute.lib.osgi.EmbeddedResource;
import aQute.lib.osgi.FileResource;
import aQute.lib.osgi.Jar;
import aQute.lib.osgi.Resource;
import aQute.libg.header.OSGiHeader;
import aQute.libg.version.Version;

/**
 * This script runs after the bnd file stuff has been done, before analyzing any
 * classes. It will check if the bnd file contains -pack (the bnd file must
 * contain it, not a parent). It will then pack all projects listed as its
 * valued. For each project, a bnd file is created that has no longer references
 * to the build. All dependent JAR files are stored in the jar directory for
 * this purpose. Additionally, a runtests script is added and the bnd jar is
 * included to make the tess self contained.
 */

public class Packaging implements AnalyzerPlugin {

	final static String	PACK	= "-pack";
	final static String	ROOT	= "";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		// Make sure -pack is set in the actual file or one of its includes
		if (!analyzer.getProperties().containsKey(PACK))
			return false;

		String pack = analyzer.getProperty(PACK);
		ProjectBuilder pb = (ProjectBuilder) analyzer;
		Workspace workspace = pb.getProject().getWorkspace();
		Jar jar = analyzer.getJar();

		// For each project listed ...
		Map<String, Map<String, String>> ct = pb.parseHeader(pack);
		if (ct.isEmpty()) {
			analyzer.warning("No projects to pack");
			return false;
		}

		// Do the shared stuff, we use our project as a template
		Project us = pb.getProject();
		Collection<Container> runpath = us.getRunpath();

		StringBuilder sb = new StringBuilder();
		sb.append("# Workspace information\n");
		sb.append("-runpath = ");
		flatten(analyzer, sb, jar, runpath, false);
		sb.append('\n');
		jar.putResource("shared.inc", new EmbeddedResource(sb.toString()
				.getBytes("UTF-8"), 0));

		for (Map.Entry<String, Map<String, String>> entry : ct.entrySet()) {
			try {
				Project project = workspace.getProject(entry.getKey());
				if (!project.isValid())
					analyzer.error("Invalid project to pack: %s", project);
				else
					pack(analyzer, jar, project, runpath);
			}
			catch (Exception t) {
				analyzer.error("While packaging %s got %s", entry.getKey(), t);
				throw t;
			}
		}

		// Include bnd so it is fully self contained, except for the
		// java runtime.
		Container c = pb.getProject().getBundle("biz.aQute.bnd", "latest",
				Constants.STRATEGY_HIGHEST, null);

		File f = c.getFile();
		if (f != null)
			jar.putResource("jar/bnd.jar", new FileResource(f));
		else
			analyzer.error("Cannot find bnd's jar file in a repository ");

		List<Container> extra = pb.getProject().getBundles(
				Constants.STRATEGY_HIGHEST, "com.springsource.junit");
		flatten(analyzer, null, jar, extra, true);

		StringBuilder script = new StringBuilder();
		script.append("java -jar jar/bnd.jar runtests -title ");
		script.append(pb.getProject());
		script.append("\n");
		jar.putResource("runtests", new EmbeddedResource(script.toString()
				.getBytes("UTF-8"), 0));

		return false;
	}

	/**
	 * Store a project in a JAR so that we can later unzip this project and have
	 * all information.
	 * 
	 * @param jar
	 * @param project
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void pack(Analyzer analyzer, Jar jar, Project project,
			Collection<Container> sharedRunpath) throws Exception {
		Collection<Container> runpath = project.getRunpath();
		Collection<Container> runbundles = project.getRunbundles();
		String runproperties = project.getProperty(Constants.RUNPROPERTIES);
		StringBuilder sb = new StringBuilder();

		sb.append("# bnd pack for project " + project + "\n");
		sb.append("# " + new Date() + "\n");
		sb.append("-include= ~shared.inc\n");
		sb.append("build=.\n");
		sb.append("\n");
		sb.append("-target = ");
		flatten(analyzer, sb, jar, project, Collections.EMPTY_MAP, true);
		sb.deleteCharAt(sb.length() - 1);

		if (!equals(runpath, sharedRunpath)) {
			sb.append("\n");
			sb.append("\n");
			sb.append("-runpath = ");
			flatten(analyzer, sb, jar, runpath, false);
		}
		sb.append("\n\n");
		sb.append("-runbundles = ");
		flatten(analyzer, sb, jar, runbundles, false);

		Map<String, String> properties = OSGiHeader
				.parseProperties(runproperties);

		String del = "\n\n" + Constants.RUNPROPERTIES + " = \\\n";
		properties.put("report", "true");
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			sb.append(del);

			sb.append(entry.getKey());
			sb.append("=");
			if (entry.getKey().equals("org.osgi.framework.trust.repositories")) {
				sb.append("keystore");

				// Copy the key store
				File keystore = analyzer.getFile(entry.getValue());
				if (keystore.exists() && keystore.isFile()) {
					jar.putResource("keystore", new FileResource(keystore));
				}
				else {
					analyzer.error("The referred keystore %s is not a file",
							entry.getValue());
				}
			}
			else {
				sb.append("\"");
				sb.append(entry.getValue());
				sb.append("\"");
			}
			del = ", \\\n";
		}
		sb.append("\n\n\n\n");

		Resource r = new EmbeddedResource(sb.toString().getBytes("UTF-8"),
				project.lastModified());
		jar.putResource(project.getName() + ".bnd", r);

	}

	private <T> boolean equals(Collection<? extends T> a,
			Collection<? extends T> b) {
		if ( a.size() != b.size())
			return false;

		for ( T x : a ) {
			if ( ! b.contains(x))
				return false;
		}
		return true;
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Collection<Container> path, boolean store) throws Exception {
		for (Container container : path) {
			flatten(analyzer, sb, jar, container, store);
		}
		if (sb != null)
			sb.deleteCharAt(sb.length() - 2);
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Container container, boolean store) throws Exception {
		switch (container.getType()) {
			case LIBRARY :
				flatten(analyzer, sb, jar, container.getMembers(), store);
				return;

			case PROJECT :
				flatten(analyzer, sb, jar, container.getProject(), container
						.getAttributes(), store);
				break;

			case EXTERNAL :
				flatten(analyzer, sb, jar, container.getFile(), container
						.getAttributes(), store);
				break;

			case REPO :
				flatten(analyzer, sb, jar, container.getFile(), container
						.getAttributes(), store);
				break;
		}
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Project project, Map<String, String> map, boolean store)
			throws Exception {
		File[] subs = project.getBuildFiles();
		analyzer.getInfo(project);
		if (subs == null) {
			analyzer.error("Project cannot build %s ", project);
		}
		else
			for (File sub : subs)
				flatten(analyzer, sb, jar, sub, map, store);
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			File sub, Map<String, String> map, boolean store) throws Exception {
		Jar s = new Jar(sub);
		try {
			Manifest m = s.getManifest();
			String bsn = m.getMainAttributes().getValue(
					Constants.BUNDLE_SYMBOLICNAME);
			if ( bsn == null ) {
				analyzer.error("Invalid bundle in flattening a path (no bsn set): %s", sub.getAbsolutePath());
				return;
			}
			
			int n = bsn.indexOf(';');
			if (n > 0)
				bsn = bsn.substring(0, n);

			String version = m.getMainAttributes().getValue(
					Constants.BUNDLE_VERSION);
			if (version == null)
				version = "0";
			Version v = new Version(version);

			String path = "jar/" + bsn + "-" + v.getMajor() + "."
					+ v.getMinor() + "." + v.getMicro() + ".jar";

			if (store)
				jar.putResource(path, new FileResource(sub));

			if (sb != null) {
				sb.append("\\\n    ");
				sb.append(path);
				sb.append(";version=file");
			}
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (sb != null && !entry.getKey().equals("version")) {
					sb.append(";");
					sb.append(entry.getKey());
					sb.append("=\"");
					sb.append(entry.getValue());
					sb.append("\"");
				}
			}
			if (sb != null)
				sb.append(", ");
		}
		finally {
			s.close();
		}
	}

}
