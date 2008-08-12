package org.osgi.tools.btool;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.apache.tools.ant.*;

public class BTool extends Task {
	SortedMap			contents		= new TreeMap();	// Packages in
	SortedSet			planned			= new TreeSet();	// Exported
	HashMap				exports			= new HashMap();	// calculated
	Collection			mainClasspath	= new Vector();
	DirSource			workspace;
	DirSource			project;
	boolean				auto;
	ZipOutputStream		zip;
	boolean				verbose			= false;
	Properties			properties		= new Properties();
	String				manifestSource	= null;
	boolean				compress		= false;
	Vector				errors			= new Vector();
	boolean				analyse;
	String				version;
	boolean				showmanifest;
	String				permission;
	boolean				sources;
	String				sourcesPrefix	= "OSGI-OPT/src/";
	boolean				sourcesBuild;
	Vector				warnings		= new Vector();
	EclipseProject		eclipse;
	String				expands;
	Vector				referred		= new Vector();
	Hashtable			checksums;
	boolean				failok			= false;
	Set					directories		= new HashSet();
	String				zipname;
	String				classpath;
	private String		infoPrefix		= "project";
	String				contentFolders	= null;
	String				includes;
	Manifest			manifest;
	ManifestResource	manifestResource;
	Dependencies		dependencies;
	List				excludeImport	= null;
	List				includeExport	= null;
	boolean				ignoreVersions;
	String				permissions;
	IPA					ipa;
	private File		workspaceDir;
	private File		projectDir;
	String				prebuild		= "";
	long				modified;
	boolean				archiveChanged	= false;
	String				signers;
	int					manifestVersion = 1;
	List				extraRoots = new ArrayList();

	/**
	 * Try out the Deliver program. Syntax: Please note that the classpath must
	 * include Deliver AND all the paths to be searched.
	 */
	public void execute() throws BuildException {
		File zipfile = null;
		try {
			if (eclipse == null)
				throw new IllegalArgumentException(
						"Eclipse project file /must/ be set");
			eclipse.execute(getProject().getProperties()); // Read Eclipse
			if (zipname == null) {
				setProperties();
				return;
			}
			zipfile = new File(zipname);
			if (!zipfile.exists())
				archiveChanged = true;

			if ( zipfile.lastModified() < modified ) {
				archiveChanged = true;
			}
			trace("Zip name " + zipname + " " + archiveChanged + " " + new Date(modified) + " " + new Date(zipfile.lastModified()));
			modified = zipfile.lastModified();
			getManifest(); // Read manifest
			getPermissions();
			setClasspath(); // Create search path
			setSourceFolders(); // Read the list of folders that are included
			if (auto) {
				trace("Adding full bindir to JAR " + eclipse.getBindir());
				;
				DirSource ds = new DirSource(new File(eclipse.getBindir()));
				addRecursive(ds, "", false);
			}
			addContentPackages(planned);
			expands(); // Expands contents of another jar
			includes(); // Add contents of another jar
			doIpa();
			doProperties();

			trace("Date of archive: " + new Date(modified));
			if (archiveChanged) {
				openZip();
				doMetaInf();
				int n = doContents();
				closeZip();

				if (n == 0)
					errors.add("No files in ZIP");
				else {
					if (analyse) {
						if (manifestSource != null)
							doAnalysis();
						else
							warnings.add("No manifest to analyse");
					}
				}
			}
			else {
				System.out.println("Archive not changed");
			}
			showErrors();
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		contents = null;
		planned = null;
		exports = null;
		mainClasspath = null;
		workspace = null;
		project = null;
		if (zip != null)
			try {
				zip.close();
			}
			catch (Throwable t) {
			};
		zip = null;
		properties = null;
		eclipse = null;
		referred = null;
		checksums = null;
		directories = null;
		manifest = null;
		manifestResource = null;
		dependencies = null;
		ipa = null;
		workspaceDir = null;
		projectDir = null;
		if (failok || errors.isEmpty())
			return;

		zipfile.delete();
		throw new BuildException("Errors found");
	}

	private int doContents() {
		int n = 0;
		for (Iterator i = contents.values().iterator(); i.hasNext();) {
			Resource r = (Resource) i.next();
			if (r.lastModified() > modified)
				System.out.println("New " + r + "(" + r.lastModified() + ")");
			try {
				InputStream in = r.getInputStream();
				if (in != null) {
					addToZip(r.getPath(), r.getInputStream(), r.getExtra());
					n++;
				}
			}
			catch (Exception e1) {
				System.err.println("Could not read stream " + r);
				e1.printStackTrace();
			}
		}
		return n;
	}

	private void doProperties() {
		if (properties.size() > 0)
			addContents(new PropertyResource(this, "osgi.properties",
					properties));
	}

	private void doIpa() throws Exception {
		if (ipa != null) {
			File buildProperties = new File(projectDir, "build.properties");
			if (buildProperties.lastModified() > modified)
				archiveChanged = true;
			ipa.execute();
		}
	}

	/**
	 * 
	 */
	private void getPermissions() {
		if (permissions == null)
			permissions = replaceExt(zipname, ".perm");
		if (permissions == null)
			return;
		File f = new File(permissions);
		if (!f.exists())
			f = new File("permissions.perm");
		if (f.exists()) {
			Resource perms = new PermissionResource(this, f);
			addContents(perms);
		}
	}

	private void getManifest() throws IOException {
		// In certain cases we do not want to fallback
		// to the default manifest. So we allow the properties
		// to have a value specifying that there is no
		// manifest for certain file names.
		String regex = getProject().getProperty("nomanifest");
		if (regex != null) {
			StringTokenizer st = new StringTokenizer(regex, " ");
			for (int i = 0; i < st.countTokens(); i++) {
				if (zipname.endsWith(st.nextToken())) {
					trace("Ignoring manifest for " + zipname);
					return;
				}
			}
		}
		if (manifestSource == null) {
			
			manifestSource = replaceExt(zipname, ".mf");
		}
		if (manifestSource == null)
			return;

		File f = new File(manifestSource);
		if (!f.exists())
			f = new File(projectDir, "Manifest.mf");
		if (!f.exists())
			f = new File(projectDir, "META-INF/MANIFEST.MF");
		if (!f.exists()) {
			warnings.add("No manifest used for " + zipname);
			manifestSource = null;
			return;
		}
		manifestSource = f.getAbsolutePath();
		if (f.lastModified() > modified) {
			trace("Newer manifest");
			archiveChanged = true;
		}

		boolean showmanifest = this.showmanifest;
		this.showmanifest = false;
		ManifestResource mf = new ManifestResource(this, manifestSource, false);
		manifest = new Manifest(this,mf.getInputStream());
		this.showmanifest = showmanifest;
	}

	private String replaceExt(String name, String extTo) {
		int n = name.lastIndexOf(".");
		if (n < 0) {
			trace("Cannot find extension: " + name );
			return null;
		}
		return name.substring(0, n) + extTo;
	}

	/**
	 * 
	 */
	private void doAnalysis() throws Exception {
		initDependencies();
		Analysis analysis = new Analysis(this, dependencies, zipname);
		analysis.execute();
		if (verbose)
			analysis.report();
	}

	/**
	 * @param manifest
	 * @return
	 * @throws IOException
	 */
	void learnContentFromManifest(Manifest manifest) throws IOException {
		if (manifestSource != null) {
			trace("Follow manifest " + manifestSource);
			Package packages[] = manifest.getExports();
			for (int i = 0; packages != null && i < packages.length; i++) {
				PackageResource p = new PackageResource(this, null,
						PackageResource.nameToPath(packages[i].getName()));
				p.setType(PackageResource.EXPORT);
				p.setVersion(packages[i].getVersion());
				addExport(p);
				trace("Exporting " + p.name + "-" + p.getVersion());
			}
			// packages = manifest.getImports();
			// for (int i = 0; packages != null && i < packages.length; i++) {
			// PackageResource p = new PackageResource(this, null,
			// PackageResource.nameToPath(packages[i].getName()));
			// p.setType(PackageResource.IMPORT);
			// p.setVersion(packages[i].getVersion());
			// imports.add(p);
			// trace("Importing " + p.name + "-" + p.getVersion());
			// }
		}
	}

	/**
	 * @param p
	 */
	private void addExport(PackageResource p) {
		exports.put(p.getPath(), p);
	}

	/**
	 * @param manifest
	 */
	private void setProperties() {
		if (eclipse != null) {
			String classpath = eclipse.getClasspath();
			if (classpath != null) {
				verify("classpath", classpath);
				addProperty(infoPrefix + ".classpath", classpath);
			}
			String bootclasspath = eclipse.getBootclasspath();
			if (bootclasspath != null) {
				verify("bootclasspath", bootclasspath);
				addProperty(infoPrefix + ".bootclasspath", bootclasspath);
			}
			String sourcePath = eclipse.getSourcepath();
			if (sourcePath != null) {
				addProperty(infoPrefix + ".sourcepath", sourcePath);
			}
			String bindir = eclipse.getBindir();
			if (bindir != null) {
				addProperty(infoPrefix + ".bindir", bindir);
			}
			else {
				addProperty(infoPrefix + ".bindir", sourcePath);
			}
			/**
			 * The prebuild is an attribute that is prepended to the dependency
			 * path derived from the Eclipse project
			 */
			String buildpath = eclipse.getBuildPath();
			StringBuffer sb = new StringBuffer();
			String del = "";
			StringTokenizer st = new StringTokenizer(prebuild, " ,");
			while (st.hasMoreTokens()) {
				sb.append(del);
				sb.append(new File(workspaceDir, st.nextToken())
						.getAbsoluteFile());
				del = EclipseProject.PATHSEP;
			}
			if (buildpath != null && buildpath.length() > 0) {
				sb.append(del);
				sb.append(buildpath);
			}
			if (sb.length() > 0) {
				addProperty(infoPrefix + ".buildpath", sb.toString());
			}
		}
	}

	void verify(String type, String paths) throws BuildException {
		// StringTokenizer st = new
		// StringTokenizer(paths,EclipseProject.PATHSEP);
		// while (st.hasMoreTokens()) {
		// String s = st.nextToken();
		// File f = new File( s );
		// if ( ! f.exists() )
		// throw new BuildException("Non existent file in " + infoPrefix + "." +
		// type + " -> " + s );
		// }
	}

	/**
	 * 
	 */
	private void setSourceFolders() {
		if (contentFolders != null) {
			Source srce = new DirSource(new File(eclipse.getBindir()));
			String[] sourceFolders = eclipse.getSourceFolders();
			StringTokenizer st = new StringTokenizer(contentFolders, ",");
			while (st.hasMoreTokens()) {
				String folder = st.nextToken();
				for (int i = 0; i < sourceFolders.length; i++) {
					//
					// We have to strip the source prefix so
					// we know the package names
					//
					if (folder.startsWith(sourceFolders[i])) {
						folder = folder.substring(sourceFolders[i].length());
						if (folder.endsWith("/"))
							folder = folder.substring(0, folder.length() - 1);
						if (folder.startsWith("/"))
							folder = folder.substring(1);
						PackageResource pr = new PackageResource(this, srce,
								folder);
						pr.setType(PackageResource.PRIVATE);
						addPlanned(pr);
					}
				}
			}
		}
	}

	public void trace(Object o) {
		if (verbose)
			System.out.println(o);
	}

	public void warning(Object o) {
		System.err.println(o);
	}

	/**
	 * 
	 */
	private void showErrors() {
		int n = 0;
		for (Enumeration e = errors.elements(); e.hasMoreElements();) {
			System.err.println(n++ + " *** " + e.nextElement());
		}
		for (Enumeration e = warnings.elements(); e.hasMoreElements();) {
			System.err.println(n++ + " WARNING " + e.nextElement());
		}
		if (errors.size() != 0) {
			if (errors.size() == 1)
				System.err.println("One error detected");
			else
				System.err.println(errors.size() + " Errors detected");
			if (failok)
				System.err.println("FAIL ALLOWED");
		}
	}

	/**
	 * @throws FileNotFoundException
	 */
	private void openZip() throws FileNotFoundException {
		FileOutputStream fo = new FileOutputStream(zipname);
		zip = new ZipOutputStream(fo);
		if (compress)
			zip.setMethod(ZipOutputStream.DEFLATED);
		else
			zip.setMethod(ZipOutputStream.STORED);
	}

	public void addContentPackages(Collection packages) throws IOException {
		for (Iterator i = packages.iterator(); i.hasNext();) {
			PackageResource p = (PackageResource) i.next();
			if (p.getSource() == null) {
				Source source = findSource(p.getPath());
				if (source == null) {
					trace("Did not find package " + p);
					errors.add("No source JAR/Directory found for package: "
							+ p.getName());
					continue;
				}
				else
					p.setSource(source);
			}
			addContents(p);
			Collection sub = p.getResources();
			for (Iterator ir = sub.iterator(); ir.hasNext();) {
				Resource r = (Resource) ir.next();
				r = modify(r);
				addContents(r);
			}
		}
	}

	/**
	 * @param p
	 */
	void addContents(Resource p) {
		if (p == null)
			return;
		if (p.lastModified() > modified) {
			trace("New resource " + p);
			archiveChanged = true;
		}
		if (!contents.containsKey(p.getPath()))
			contents.put(p.getPath(), p);
		else
			trace("Already in jar: " + p.getPath());
	}

	Source findSource(String path) {
		for (Iterator e = mainClasspath.iterator(); e.hasNext();) {
			Source source = (Source) e.next();
			if (source.contains(path))
				return source;
		}
		trace("Not found  " + path);
		return null;
	}

	List findSources(String path) {
		Vector result = new Vector();
		for (Iterator e = mainClasspath.iterator(); e.hasNext();) {
			Source source = (Source) e.next();
			if (source.contains(path))
				result.add(source);
		}
		return result;
	}

	/**
	 * Get the version of this application.
	 * 
	 */
	String version() throws IOException {
		InputStream in = getClass().getResourceAsStream("osgi.properties");
		if (in != null) {
			Properties p = new Properties();
			p.load(in);
			String result = p.getProperty("version");
			if (result != null)
				return result;
		}
		return "<no version info>";
	}

	/**
	 * Set the name of a zip file.
	 * 
	 * If no name is set, no zip file will be created. In that case the program
	 * will only output a list of dependencies.
	 * 
	 * @param s name of the zip file. File is created, a previous file with the
	 *        same name will be overwritten without warning.
	 */
	public void setJar(String s) throws IOException {
		zipname = s;
	}

	void createDirectories(String name) throws IOException {
		int index = name.lastIndexOf('/');
		if (index > 0) {
			String path = name.substring(0, index);
			if (directories.contains(path))
				return;
			createDirectories(path);
			ZipEntry ze = new ZipEntry(path + '/');
			ze.setSize(0);
			CRC32 checksum = new CRC32();
			checksum.update(new byte[0]);
			ze.setCrc(checksum.getValue());
			zip.putNextEntry(ze);
			zip.closeEntry();
			directories.add(path);
		}
	}

	/**
	 * Add a resource to the output zip file, if it was set.
	 * 
	 * This function can always be called, the function is ignored if not zip
	 * file has been set.
	 * 
	 * @param name name of the resource
	 * @param actual input stream with actual data, will be closed at end.
	 */
	void addToZip(String name, InputStream actual) throws IOException {
		addToZip(name, actual, null);
	}

	void addToZip(String name, InputStream actual, byte extra[])
			throws IOException {
		if (zip == null)
			return;
		createDirectories(name);
		byte buffer[];
		try {
			buffer = readAll(actual, 0);
			actual.close();
			CRC32 checksum = new CRC32();
			checksum.update(buffer);
			// if ( extra != null )
			// checksum.update( extra );
			ZipEntry ze = new ZipEntry(name);
			ze.setSize(buffer.length);
			ze.setCrc(checksum.getValue());
			if (extra != null)
				ze.setExtra(extra);
			zip.putNextEntry(ze);
			zip.write(buffer, 0, buffer.length);
			zip.closeEntry();
		}
		catch (Exception e) {
			errors.addElement("Could not read " + name + "  " + actual + " "
					+ e);
		}
	}

	/**
	 * Set manifest file name.
	 */
	public void setManifest(String file) throws IOException {
		manifestSource = file;
	}

	/*
	 * The doManifest is quite rewritten as it now prepends the manifest first
	 * in the zip file. It is now reqired to make a closeZip() call first.
	 * 
	 * This is needed as the _zipname file is renamed to a temporary file, the
	 * old _zipfile is overwritten with the manifest and then the content from
	 * the temorary file is added to the _zipname file.
	 * 
	 * The temporary file is then deleted.
	 * 
	 */
	void doMetaInf() {
		if (manifestSource == null)
			return;
		try {
			ManifestResource mf = new ManifestResource(this, manifestSource, true);
			addToZip("META-INF/MANIFEST.MF", mf.getInputStream() );

//			if ( signers != null ) {
//				StringTokenizer st = new StringTokenizer(signers,",");
//				int n = 1;
//				while ( st.hasMoreTokens() ) {
//					String 	signer = st.nextToken();
//					sign(""+n++, signer);
//				}
//			}
		}
		catch (IOException e) {
			e.printStackTrace();
			errors.addElement("Could not handle manifest: " + e);
		}
	}


	/**
	 * @param signer
	private void sign(String baseName, String signer) {
		SignerResource r = new SignerResource(this,signer) ;
		addToZip("META-INF/" + baseName + ".dsa", r.getInputStream() );
		addToZip("META-INF/" + baseName + ".SF", getSFFile());
		
	}
	 */

	void expands() throws IOException {
		if (expands == null)
			return;

		StringTokenizer st = new StringTokenizer(expands, " ,");
		outer: while (st.hasMoreElements()) {
			String pack = st.nextToken().trim().replace('.', '/');
			trace("Expand " + pack);
			boolean export = pack.startsWith("[") && pack.endsWith("]");
			if (export) {
				pack = pack.substring(1, pack.length() - 1).trim();
			}

			boolean merge = pack.startsWith("{") && pack.endsWith("}");
			if (merge) {
				pack = pack.substring(1, pack.length() - 1).trim();
				trace("Merging ... " + pack);
			}

			if (pack.endsWith("/*")) {
				pack = pack.substring(0, pack.length() - 2);
				Source source = findSource(pack);
				if (source != null) {
					addRecursive(source, pack, export);
				}
				else
					errors.add("Cannot find package " + pack);
			}
			else {
				List sources = findSources(pack);
				if (sources.isEmpty())
					errors.add("Cannot find package " + pack);
				else {
					Iterator s = sources.iterator();
					do {
						Source source = (Source) s.next();
						trace("Source for package " + source.getFile());
						PackageResource pr = new PackageResource(this, source,
								pack);
						pr.setType(export ? PackageResource.EXPORT
								: PackageResource.PRIVATE);
						if (export)
							addExport(pr);
						addContents(pr);
						for (Iterator i = pr.getResources().iterator(); i
								.hasNext();) {
							Resource r = (Resource) i.next();
							trace("Adding " + r);
							r = modify(r);
							addContents(r);
						}
					} while (merge && s.hasNext());
				}
			}
		}
	}

	Resource modify(Resource r) {
		if (r.getPath().endsWith("CVS"))
			return null;
		if (r.getPath().endsWith(".java")
				|| r.getPath().endsWith("package.html")) {
			if (sources) {
				Resource rr = new Resource(this, r.getSource(), sourcesPrefix
						+ r.getPath());
				rr.setSourcePath(r.getPath());
				return rr;
			}
			return null;
		}
		// File must also be added to sources!
		// but must also be present in binary
		if (r.getPath().endsWith("packageinfo")) {
			if (sources) {
				Resource rr = new Resource(this, r.getSource(), sourcesPrefix
						+ r.getPath());
				rr.setSourcePath(r.getPath());
				addContents(rr);
			}
		}
		return r;
	}

	void includes() throws IOException {
		if (includes == null)
			return;
		StringTokenizer st = new StringTokenizer(includes, ",");
		while (st.hasMoreElements()) {
			String file = st.nextToken().trim();
			String outname = null;
			boolean preprocess = false;
			if (file.startsWith("[") && file.endsWith("]")) {
				preprocess = true;
				file = file.substring(1, file.length() - 1).trim();
			}
			int n = file.indexOf('=');
			if (n > 0) {
				outname = file.substring(0, n).trim();
				file = file.substring(n + 1).trim();
			}
			else
				outname = getFileName(file);
			trace("Include " + file + " as " + outname);
			Source s;
			if (file.startsWith("/")) {
				file = file.substring(1);
				s = workspace;
			}
			else
				s = project;
			Resource r;
			if (preprocess)
				r = new FileResource(this, outname,
						new File(s.getFile(), file), preprocess);
			else {
				r = new Resource(this, s, outname);
				r.setSourcePath(file);
			}
			addContents(r);
		}
	}

	/**
	 * @param file
	 * @return
	 */
	String getFileName(String file) {
		int n = file.lastIndexOf('/');
		if (n > 0) {
			return file.substring(n + 1);
		}
		return file;
	}

	void addRecursive(Source source, String path, boolean export)
			throws IOException {
		if (path.equals("META-INF/MANIFEST.MF"))
			return;
		Resource r = new Resource(this, source, path);
		r = modify(r);
		if (r == null)
			return;
		if (source.isDirectory(path)) {
			trace("Dir " + path);
			if (!path.equals("")) {
				PackageResource pr = new PackageResource(this, source, path);
				if (export)
					addExport(pr);
				addContents(pr);
			}
			if (path.length() > 0)
				path = path + "/";
			Collection c = source.getResources(path);
			if (c != null) {
				for (Iterator i = c.iterator(); i.hasNext();) {
					String name = path + i.next();
					addRecursive(source, name, export);
				}
			}
			else
				trace("Not resources found for " + path);
		}
		else {
			trace("File " + path);
			addContents(r);
		}
	}

	/**
	 * Read a complete stream till EOF. This method will parse the input stream
	 * until a -1 is discovered.
	 * 
	 * The method is recursive. It keeps on calling a higher level routine until
	 * EOF. Only then is the result buffer calculated.
	 */
	byte[] readAll(InputStream in, int offset) throws IOException {
		byte temp[] = new byte[4096];
		byte result[];
		int size = in.read(temp, 0, temp.length);
		if (size <= 0)
			return new byte[offset];
		//
		// We have a positive result, copy it
		// to the right offset.
		//
		result = readAll(in, offset + size);
		System.arraycopy(temp, 0, result, offset, size);
		return result;
	}

	/**
	 * Close the zip file.
	 * 
	 * This function should be called after all files are processed. It may be
	 * called at any time even if no zip file is set.
	 */
	void closeZip() throws IOException {
		if (zip != null)
			zip.close();
	}

	/**
	 * @throws Exception
	 */
	void setClasspath() throws Exception {
		if (eclipse != null) {
			String cp = eclipse.getClasspath();
			String bp = eclipse.getBootclasspath();
			
			Collection	col = new Vector();
			add(col,cp,EclipseProject.PATHSEP);
			add(col,bp,EclipseProject.PATHSEP);
			add(col,classpath,",");
			
			
			for (Iterator i=col.iterator(); i.hasNext(); ) {
				String file = (String) i.next();
				File f = new File(file);
				if (f.exists()) {
					trace("Classpath File " + f);
					if (f.isDirectory()) {
						mainClasspath.add(new DirSource(f));
					}
					else
						mainClasspath.add(new ZipSource(f));
				}
				else
					warning("No such Classpath file " + f.getAbsolutePath());
			}
		}
	}

	void add(Collection col, String paths, String pathsep) {
		if ( paths == null )
			return;
		
		StringTokenizer st = new StringTokenizer(paths,pathsep);
		while ( st.hasMoreTokens() ) {
			col.add( st.nextToken().trim());
		}
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public void setPermission(String permissionFile) {
		this.permission = permissionFile;
	}

	public void setShowManifest(boolean show) {
		this.showmanifest = show;
	}

	public void setExport(String exportList) {
		trace("setExport " + exportList);
		StringTokenizer st = new StringTokenizer(exportList, " ,");
		while (st.hasMoreElements()) {
			String name = st.nextToken();
			PackageResource p = new PackageResource(this, null, PackageResource
					.nameToPath(name));
			p.setType(PackageResource.EXPORT);
			addPlanned(p);
		}
	}

	public void setExpand(String expandList) {
		expands = expandList;
	}

	public void setPrivate(String privateList) {
		StringTokenizer st = new StringTokenizer(privateList, " ,");
		while (st.hasMoreElements()) {
			PackageResource p = new PackageResource(this, null, st.nextToken());
			p.setType(PackageResource.PRIVATE);
			addPlanned(p);
		}
	}

	/**
	 * @param p
	 */
	private void addPlanned(PackageResource p) {
		if (!planned.contains(p))
			planned.add(p);
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	// TODO IMPORTAS
	// TODO PROPERTY
	public void setChecksums(boolean t) {
		checksums = t ? new Hashtable() : null;
	}

	public void setSources(boolean t) {
		sources = sourcesBuild = t;
	}

	public void setSourcesPrefix(String prefix) {
		sourcesPrefix = prefix;
	}

	public void setAnalyse(boolean t) {
		analyse = t;
	}

	public void setFailOk(boolean t) {
		failok = t;
	}

	public void setEclipse(String project) throws Exception {
		File f = new File(project);
		if (!f.exists())
			throw new IllegalArgumentException(
					"Eclipse project does not exist " + f);
		this.project = new DirSource(f);
		this.workspace = new DirSource(f.getParentFile());
		projectDir = f;
		workspaceDir = f.getParentFile();
		eclipse = new EclipseProject(f, true);
	}

	// TODO IPA
	// TODO entry-text
	// TODO entry-file
	/**
	 * @return Returns the analyse.
	 */
	public boolean isAnalyse() {
		return analyse;
	}

	public void setInfoPrefix(String name) {
		infoPrefix = name;
	}

	protected void addProperty(String n, String v) {
		if (v != null)
			getProject().setProperty(n, v);
	}

	/**
	 * @param source The source to set.
	 */
	public void setContentFoldersProperty(String key) {
		this.contentFolders = getProject().getProperty(key);
		trace("Content folders = " + contentFolders);
	}

	public void setExpandProperty(String key) {
		setExpand(getProject().getProperty(key));
	}

	public void setIncludeProperty(String key) {
		includes = getProject().getProperty(key);
	}

	public void setExcludeImportProperty(String key) {
		excludeImport = makeListFromProperty(key);
	}

	public void setIncludeExportProperty(String key) {
		includeExport = makeListFromProperty(key);
	}

	List makeListFromProperty(String key) {
		Vector v = new Vector();
		String list = getProject().getProperty(key);
		if (list == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(list, ", ");
		while (st.hasMoreTokens()) {
			v.add(st.nextToken());
		}
		return v;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Collection getImports() throws IOException {
		initDependencies();
		return dependencies.getReferred();
	}

	/**
	 * @throws IOException
	 */
	Dependencies initDependencies() throws IOException {
		if (dependencies == null) {
			dependencies = new Dependencies(this, mainClasspath, manifest,
					contents, excludeImport, includeExport);
			dependencies.calculate();
		}
		return dependencies;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Collection getExports() throws IOException {
		initDependencies();
		Collection contained = dependencies.getContained();
		SortedSet set = new TreeSet();
		for (Iterator i = contained.iterator(); i.hasNext();) {
			Package pr = (Package) i.next();
			if (exports.containsKey(pr.getPath()))
				set.add(pr);
		}
		return set;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Collection getPrivates() throws IOException {
		initDependencies();
		Collection contained = dependencies.getContained();
		SortedSet set = new TreeSet();
		for (Iterator i = contained.iterator(); i.hasNext();) {
			Package pr = (Package) i.next();
			if (!exports.containsKey(pr.getPath()))
				set.add(pr);
		}
		return set;
	}

	/**
	 * @return Returns the ignoreVersions.
	 */
	boolean isIgnoreVersions() {
		return ignoreVersions;
	}

	/**
	 * @param ignoreVersions The ignoreVersions to set.
	 */
	public void setIgnoreVersions(boolean ignoreVersions) {
		this.ignoreVersions = ignoreVersions;
	}

	/**
	 * @param auto The auto to set.
	 */
	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	/**
	 * @param permissions The permissions to set.
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public void setIpaProperty(String ipaProperty) {
		String ipas = getProject().getProperty(ipaProperty);
		if (ipas != null) {
			ipa = new IPA(this, ipas);
		}
	}

	/**
	 * @param prebuild The prebuild to set.
	 */
	public void setPrebuild(String prebuild) {
		this.prebuild = prebuild;
	}

	public void setManifestVersion(int manifestVersion) {
		this.manifestVersion = manifestVersion;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}
	
	
	public void setClasspath(String list) {
		if ( list != null && list.length() > 0 )
		classpath = list;
	}

	public List getExtraRoots() {
		return extraRoots;
	}

	public void setExtraRoots(String extraRoots) {
		String list = getProject().getProperty(extraRoots);
		if ( list != null ) {
			StringTokenizer st = new StringTokenizer(list,",");
			List	roots = new ArrayList();
			while (st.hasMoreTokens()) {
				roots.add(st.nextToken().trim());
			}
			setExtraRoots(roots);
		}
	}
	
	public void setExtraRoots(List extraRoots) {
		this.extraRoots = extraRoots;
	}
}