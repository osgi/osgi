import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import aQute.bnd.build.Container;
import aQute.bnd.build.Container.TYPE;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.service.Strategy;
import aQute.libg.generics.Create;

/**
 * This script runs after the bnd file stuff has been done, before analyzing any
 * classes. It will check if the bnd file contains -ctpack (the bnd file must
 * contain it, not a parent). It will then pack all projects listed as its
 * valued. For each project, a bnd file is created that has no longer references
 * to the build. All dependent JAR files are stored in the jar directory for
 * this purpose. Additionally, a runtests script is added and the bnd jar is
 * included to make the tess self contained.
 */

public class CTPackaging extends Packaging implements AnalyzerPlugin {
	private final static String	CTPACK	= "-ctpack";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		// Make sure -ctpack is set in the actual file or one of its includes
		String ctpack = analyzer.getProperty(CTPACK);
		if (ctpack == null)
			return false;

		Map<String,String> fileToPath = Create.map();

		Project us = ((ProjectBuilder) analyzer).getProject();
		Workspace workspace = us.getWorkspace();
		Jar jar = analyzer.getJar();

		// For each param listed ...
		Parameters params = analyzer.parseHeader(ctpack);
		if (params.isEmpty()) {
			analyzer.warning("No items to pack");
			return false;
		}

		// Do the shared stuff, we use our project as a template
		Collection<Container> runfw = us.getRunFw();
		Collection<Container> runpath = us.getRunpath();

		StringBuilder sb = new StringBuilder();
		addNotice(sb);
		sb.append("# Workspace information\n");
		sb.append(Constants.RUNFW);
		sb.append(" = ");
		flatten(analyzer, sb, jar, runfw, false, fileToPath);
		if (!runpath.isEmpty()) {
			sb.append("\n\n");
			sb.append(Constants.RUNPATH);
			sb.append(" = ");
			flatten(analyzer, sb, jar, runpath, false, fileToPath);
		}
		sb.append("\n\n-runtrace = true\n\n");
		String tester = analyzer.getProperty(Constants.TESTER);
		if (tester != null) {
			sb.append(Constants.TESTER);
			sb.append(" = ");
			sb.append(tester);
			sb.append("\n\n");
		}

		jar.putResource(Workspace.CNFDIR + "/" + Workspace.BUILDFILE,
				new EmbeddedResource(sb.toString().getBytes(UTF_8), 0L));

		for (String entry : params.keySet()) {
			try {
				Project project = workspace.getProject(entry);
				if (project != null) {
					pack(analyzer, jar, project, runpath, fileToPath);
				} else {
					flatten(analyzer, null, jar,
							new File(Processor.removeDuplicateMarker(entry)),
							Collections.<String, String> emptyMap(), true,
							fileToPath);
				}
			} catch (Exception t) {
				analyzer.error("While packaging %s got %s", entry, t);
				throw t;
			}
		}

		// Include biz.aQute.bnd so it is fully self contained, except for the
		// java runtime.
		pack(analyzer, jar, us, "biz.aQute.bnd", "bnd.jar");

		StringBuilder script = new StringBuilder();
		script.append("java -jar jar/bnd.jar runtests --title ");
		script.append(us);
		script.append("\n");
		jar.putResource("runtests",
				new EmbeddedResource(script.toString().getBytes(UTF_8), 0L));

		return false;
	}

	/**
	 * Store a bundle in a JAR so that we can later unzip this project and have
	 * all information.
	 */
	protected void pack(Analyzer analyzer, Jar jar, Project project, String bsn,
			String name) throws Exception {
		Container c = project.getBundle(bsn, "latest", Strategy.HIGHEST, null);

		File f = c.getFile();
		if (f != null) {
			if (name == null) {
				name = new BundleInfo(analyzer, f).canonicalName();
			}
			jar.putResource("jar/" + name, new FileResource(f));
		} else {
			analyzer.error("Cannot find " + bsn + " in a repository");
		}
	}

	/**
	 * Store a project in a JAR so that we can later unzip this project and have
	 * all information.
	 *
	 * @param jar
	 * @param project
	 * @throws Exception
	 */
	protected void pack(Analyzer analyzer, Jar jar, Project project,
			Collection<Container> sharedRunpath, Map<String,String> fileToPath)
			throws Exception {
		Collection<Container> runpath = project.getRunpath();
		Collection<Container> runbundles = new LinkedHashSet<>(
				project.getRunbundles());
		String runproperties = project.mergeProperties(Constants.RUNPROPERTIES);
		String runsystempackages = project
				.mergeProperties(Constants.RUNSYSTEMPACKAGES);
		String runsystemcapabilities = project
				.mergeProperties(Constants.RUNSYSTEMCAPABILITIES);
		String runframework = project.getProperty(Constants.RUNFRAMEWORK);
		String runvm = project.mergeProperties(Constants.RUNVM);
		String tester = project.getProperty(Constants.TESTER);
		StringBuilder sb = new StringBuilder();
		addNotice(sb);

		/**
		 * Add all sub bundles to the -runbundles so they are installed We
		 * assume here that the project is build ahead of time.
		 */
		File[] files = project.getBuildFiles();
		if (files == null) {
			System.out.println("Project has no build files " + project);
			return;
		}
		for (File sub : files) {
			Container c = new Container(project, sub);
			runbundles.add(c);
		}

		sb.append("# bnd pack for project " + project + "\n");
		sb.append("# ").append(new Date()).append("\n");
		sb.append("\n");
		sb.append("-target = ");
		flatten(analyzer, sb, jar, project,
				Collections.<String, String> emptyMap(), true, fileToPath);
		sb.deleteCharAt(sb.length() - 1);

		if (!equals(runpath, sharedRunpath)) {
			sb.append("\n");
			sb.append("\n");
			sb.append(Constants.RUNPATH);
			sb.append(" = ");
			flatten(analyzer, sb, jar, runpath, false, fileToPath);
		}
		sb.append("\n\n");
		sb.append(Constants.RUNBUNDLES);
		sb.append(" = ");
		flatten(analyzer, sb, jar, runbundles, false, fileToPath);

		Map<String,String> properties = OSGiHeader
				.parseProperties(runproperties);

		String del = "\n\n" + Constants.RUNPROPERTIES + " = \\\n    ";
		properties.put("report", "true");

		for (Map.Entry<String,String> entry : properties.entrySet()) {
			sb.append(del);
			del = ", \\\n    ";

			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key);
			sb.append("='");

			value = replacePaths(analyzer, jar, fileToPath, value,
					key.endsWith(".bundles") == false);

			escapeSingleQuotes(sb, value);
			sb.append('\'');
		}

		if (runsystempackages != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNSYSTEMPACKAGES);
			sb.append(" = \\\n    ");
			sb.append(runsystempackages);
		}

		if (runsystemcapabilities != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNSYSTEMCAPABILITIES);
			sb.append(" = \\\n    ");
			sb.append(runsystemcapabilities);
		}

		if (runframework != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNFRAMEWORK);
			sb.append(" = ");
			sb.append(runframework);
		}

		if (runvm != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNVM);
			sb.append(" = \\\n    ");
			sb.append(runvm);
		}

		if (tester != null) {
			sb.append("\n\n");
			sb.append(Constants.TESTER);
			sb.append(" = ");
			sb.append(tester);
		}

		sb.append("\n\n\n\n");

		Resource r = new EmbeddedResource(sb.toString().getBytes(UTF_8),
				project.lastModified());
		jar.putResource(project.getName() + ".bnd", r);

	}

	@Override
	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Container container, boolean store, Map<String,String> fileToPath)
			throws Exception {
		if (container.getType() == TYPE.REPO) {
			switch (container.getBundleSymbolicName()) {
				case "biz.aQute.junit" :
				case "biz.aQute.tester" :
				case "biz.aQute.tester.junit-platform" :
				case "biz.aQute.launcher" :
					if (sb != null) {
						sb.append("\\\n    ");
						sb.append(container.getBundleSymbolicName());
						sb.append(";version=latest");
						for (Map.Entry<String,String> entry : container
								.getAttributes()
								.entrySet()) {
							if (!entry.getKey().equals("version")) {
								sb.append(";");
								sb.append(entry.getKey());
								sb.append("='");
								escapeSingleQuotes(sb, entry.getValue());
								sb.append('\'');
							}
						}
						sb.append(", ");
					}
					return;
				default :
					break;
			}
		}

		super.flatten(analyzer, sb, jar, container, store, fileToPath);
	}

	private String replacePaths(Analyzer analyzer, Jar jar,
			Map<String,String> fileToPath, String value, boolean store)
			throws Exception {
		Collection<String> paths = Processor.split(value);
		List<String> result = Create.list();
		for (String path : paths) {
			File f = analyzer.getFile(path);
			boolean storeFile = store;
			if (f.isAbsolute() && f.exists()
					&& !f.getPath().contains(analyzer.getProperty("target"))) {
				f = f.getCanonicalFile();
				path = fileToPath.get(f.getAbsolutePath());
				if (path == null) {
					if (f.getName().endsWith(".jar")) {
						BundleInfo info = new BundleInfo(analyzer, f);
						switch (info.bsn) {
							case "biz.aQute.junit" :
							case "biz.aQute.tester" :
							case "biz.aQute.tester.junit-platform" :
							case "biz.aQute.launcher" :
								path = "${repo;" + info.bsn + ";latest}";
								storeFile = false;
								break;
							default :
								path = "jar/" + info.canonicalName();
								fileToPath.put(f.getAbsolutePath(), path);
								break;
						}
					} else {
						path = "property-resources/" + f.getName();

						// Ensure names are unique
						int n = 1;
						while (jar.getResource(path) != null)
							path = "property-resources/" + f.getName() + "-"
									+ n++;

						fileToPath.put(f.getAbsolutePath(), path);
					}
				}
				if (storeFile && (jar.getResource(path) == null)) {
					if (f.isFile()) {
						jar.putResource(path, new FileResource(f));
					} else {
						try (Jar j = new Jar(f)) {
							jar.addAll(j, null, path);
						}
					}
				}
				result.add(path);
			} else
				// If one entry is not a file not match, we assume they're not
				// paths
				return value;
		}
		return Processor.join(result);
	}

	private <T> boolean equals(Collection< ? extends T> a,
			Collection< ? extends T> b) {
		if (a.size() != b.size())
			return false;

		for (T x : a) {
			if (!b.contains(x))
				return false;
		}
		return true;
	}

	private void addNotice(StringBuilder sb) {
		sb.append("# Copyright (c) OSGi Alliance (")
				.append(Calendar.getInstance().get(Calendar.YEAR))
				.append("). All Rights Reserved.\n");
		sb.append("#\n");
		sb.append(
				"# Licensed under the Apache License, Version 2.0 (the \"License\");\n");
		sb.append(
				"# you may not use this file except in compliance with the License.\n");
		sb.append("# You may obtain a copy of the License at\n");
		sb.append("#\n");
		sb.append("#      https://www.apache.org/licenses/LICENSE-2.0\n");
		sb.append("#\n");
		sb.append(
				"# Unless required by applicable law or agreed to in writing, software\n");
		sb.append(
				"# distributed under the License is distributed on an \"AS IS\" BASIS,\n");
		sb.append(
				"# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
		sb.append(
				"# See the License for the specific language governing permissions and\n");
		sb.append("# limitations under the License.\n");
		sb.append("\n");
	}

	private void escapeSingleQuotes(StringBuilder sb, String value) {
		int i = sb.length();
		sb.append(value);
		for (; i < sb.length(); i++) {
			if (sb.charAt(i) == '\'') {
				sb.insert(i, "\\\\\\");
				i += 3;
			}
		}
	}
}
