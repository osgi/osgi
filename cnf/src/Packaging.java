import java.io.*;
import java.util.*;
import java.util.jar.*;

import aQute.bnd.build.*;
import aQute.bnd.service.*;
import aQute.lib.osgi.*;
import aQute.libg.header.*;
import aQute.libg.version.*;

public class Packaging implements AnalyzerPlugin {

	final static String	PACK	= "-pack";
	final static String	ROOT	= "";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		String pack = analyzer.getProperty(PACK);
		if (pack == null)
			return false;

		ProjectBuilder pb = (ProjectBuilder) analyzer;

		Workspace workspace = pb.getProject().getWorkspace();
		Jar jar = analyzer.getJar();
		Map<String, Map<String, String>> ct = pb.parseHeader(pack);
		for (Map.Entry<String, Map<String, String>> entry : ct.entrySet()) {
			try {
				Project project = workspace.getProject(entry.getKey());
				if (!project.isValid())
					analyzer.error("Invalid project to pack: %s", project);
				else
					pack(analyzer, jar, project);
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
	private void pack(Analyzer analyzer, Jar jar, Project project)
			throws Exception {
		Collection<Container> runpath = project.getRunpath();
		Collection<Container> runbundles = project.getRunbundles();
		String runproperties = project.getProperty(Constants.RUNPROPERTIES);
		StringBuilder sb = new StringBuilder();

		sb.append("# bnd pack for project " + project + "\n");
		sb.append("# " + new Date() + "\n");
		sb.append("build=.\n");
		sb.append("\n");
		sb.append("-target = ");
		flatten(analyzer, sb, jar, project, Collections.EMPTY_MAP);
		sb.deleteCharAt(sb.length() - 1);

		sb.append("\n");
		sb.append("\n");
		sb.append("-runpath = ");
		flatten(analyzer, sb, jar, runpath);

		sb.append("\n\n");
		sb.append("-runbundles = ");
		flatten(analyzer, sb, jar, runbundles);

		if (runproperties != null) {
			Map<String, String> properties = OSGiHeader
					.parseProperties(runproperties);

			String del = "\n\n" + Constants.RUNPROPERTIES + " = \\\n";
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				sb.append(del);

				sb.append(entry.getKey());
				sb.append("=");
				if (entry.getKey().equals(
						"org.osgi.framework.trust.repositories")) {
					sb.append("keystore");

					// Copy the key store
					File keystore = analyzer.getFile(entry.getValue());
					if (keystore.exists() && keystore.isFile()) {
						jar.putResource("keystore", new FileResource(keystore));
					}
					else {
						analyzer.error(
								"The referred keystore %s is not a file", entry
										.getValue());
					}
				}
				else
					sb.append(entry.getValue());
				del = ", \\\n";
			}
		}
		sb.append("\n\n\n\n");

		Resource r = new EmbeddedResource(sb.toString().getBytes("UTF-8"),
				project.lastModified());
		jar.putResource(project.getName() + ".bnd", r);

	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Collection<Container> path) throws Exception {
		for (Container container : path) {
			flatten(analyzer, sb, jar, container);
		}
		if (sb != null)
			sb.deleteCharAt(sb.length() - 2);
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Container container) throws Exception {
		switch (container.getType()) {
			case LIBRARY :
				flatten(analyzer, sb, jar, container.getMembers());
				return;

			case PROJECT :
				flatten(analyzer, sb, jar, container.getProject(), container
						.getAttributes());
				break;

			case EXTERNAL :
				flatten(analyzer, sb, jar, container.getFile(), container
						.getAttributes());
				break;

			case REPO :
				flatten(analyzer, sb, jar, container.getFile(), container
						.getAttributes());
				break;
		}
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Project project, Map<String, String> map) throws Exception {
		File[] subs = project.getBuildFiles();
		analyzer.getInfo(project);
		if (subs == null) {
			analyzer.error("Project cannot build %s ", project);
		}
		else
			for (File sub : subs)
				flatten(analyzer, sb, jar, sub, map);
	}

	private void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			File sub, Map<String, String> map) throws Exception {
		Jar s = new Jar(sub);
		try {
			Manifest m = s.getManifest();
			String bsn = m.getMainAttributes().getValue(
					Constants.BUNDLE_SYMBOLICNAME);
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
