package org.osgi.tools.btool;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.tools.ant.*;

public class BTool extends Task {
	SortedSet			contents		= new TreeSet();	// Packages in
	// output
	SortedSet			planned			= new TreeSet();	// Exported
	// packages

	// Dependency
	Set					imports			= new HashSet();	// calculated
	Set					exports			= new HashSet();	// calculated

	// maintained

	Collection			mainClasspath	= new Vector();

	DirSource			workspace;
	DirSource			project;

	ZipOutputStream		zip;
	boolean				silent			= true;
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
	List				excludeImport = null;
	List				includeExport = null;
	boolean				ignoreVersions;

	/**
	 * Try out the Deliver program. Syntax: Please note that the classpath must
	 * include Deliver AND all the paths to be searched.
	 */

	public void execute() throws BuildException {
		try {
			if (eclipse == null)
				throw new IllegalArgumentException(
						"Eclipse project file /must/ be set");
			
			eclipse.execute(getProject().getProperties()); // Read Eclipse
			
			if (zipname == null) {
				setProperties();
				return;
			}
			

			if (manifestSource == null)
				manifestSource = zipname.replaceFirst("\\.jar$", ".mf");

			File f = new File(manifestSource);
			if (!f.exists())
				throw new IllegalArgumentException("manifest: no such file "
						+ manifestSource);


			ManifestResource mf = new ManifestResource(this,manifestSource,false);
			manifest = new Manifest( mf.getInputStream() );
			
			
			
			setClasspath(); // Create search path
			setSourceFolders(); // Read the list of folders that are included

			addContentPackages(planned);
			expands(); // Expands contents of another jar
			includes(); // Add contents of another jar

			if (properties.size() > 0)
				addContents(new PropertyResource(this, "osgi.properties",
						properties));

			openZip();

			for (Iterator i = contents.iterator(); i.hasNext();) {
				Resource r = (Resource) i.next();
				InputStream in = r.getInputStream();
				if (in != null) {
					addToZip(r.getPath(), r.getInputStream(), null);
				}
			}
			closeZip();
			doManifest();
			showErrors();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	/**
	 * @param manifest
	 * @return @throws
	 *         IOException
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
				exports.add(p);
				trace("Exporting " + p.name + "-" + p.getVersion());
			}
			packages = manifest.getImports();
			for (int i = 0; packages != null && i < packages.length; i++) {
				PackageResource p = new PackageResource(this, null,
						PackageResource.nameToPath(packages[i].getName()));
				p.setType(PackageResource.IMPORT);
				p.setVersion(packages[i].getVersion());
				imports.add(p);
				trace("Importing " + p.name + "-" + p.getVersion());
			}
		}
	}

	/**
	 * @param manifest
	 */
	private void setProperties() {
		if (eclipse != null) {
			addProperty(infoPrefix + ".classpath", eclipse.getClasspath().replace(',',File.pathSeparatorChar));
			addProperty(infoPrefix + ".bootclasspath", eclipse
					.getBootclasspath().replace(',',File.pathSeparatorChar));
			addProperty(infoPrefix + ".sourcepath", eclipse.getSourcepath().replace(',',File.pathSeparatorChar));
			addProperty(infoPrefix + ".bindir", eclipse.getBindir().replace(',',File.pathSeparatorChar));
		}
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
		if ( !silent )
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
				} else
					p.setSource(source);
			}

			addContents(p);
			Collection sub = p.getResources();
			for (Iterator ir = sub.iterator(); ir.hasNext();) {
				Resource r = (Resource) ir.next();
				if (r.getPath().endsWith(".java")) {
					trace("Java file: " + r.getPath());
					if (sources) {
						Resource rr = new Resource(this, r.source,
								sourcesPrefix + r.getPath());
						rr.setSourcePath(r.getPath());
						addContents(rr);
					}
				} else
					addContents(r);
			}
		}
	}

	/**
	 * @param p
	 */
	private void addContents(Resource p) {
		if (!contents.contains(p))
			contents.add(p);
		else
			trace("Already in jar: " + p.getPath());
	}

	Source findSource(String path) {
		for (Iterator e = mainClasspath.iterator(); e.hasNext();) {
			Source source = (Source) e.next();
			trace("Checking " + source + " for " + path);
			if (source.contains(path))
				return source;
		}
		return null;
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
	 * @param s
	 *            name of the zip file. File is created, a previous file with
	 *            the same name will be overwritten without warning.
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
	 * @param name
	 *            name of the resource
	 * @param actual
	 *            input stream with actual data, will be closed at end.
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
			//if ( extra != null )
			//	checksum.update( extra );
			ZipEntry ze = new ZipEntry(name);
			ze.setSize(buffer.length);
			ze.setCrc(checksum.getValue());
			if (extra != null)
				ze.setExtra(extra);
			zip.putNextEntry(ze);
			zip.write(buffer, 0, buffer.length);
			zip.closeEntry();
		} catch (Exception e) {
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
	void doManifest() {
		try {
			ManifestResource mf = new ManifestResource(this, manifestSource,true);

			InputStream in = mf.getInputStream();
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(in, "UTF8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw;
			pw = new PrintWriter(new OutputStreamWriter(out, "UTF8"));
			if (showmanifest)
				trace("------- BEGIN ---------");
			String line = br.readLine();
			while (line != null) {
				if (showmanifest)
					trace(line);
				pw.print(line);
				pw.print("\r\n");
				line = br.readLine();
			}
			pw.print("\r\n");
			printChecksums(pw, checksums);
			pw.close();
			out.close();
			if (showmanifest)
				trace("------- END ---------");

			ByteArrayInputStream bin = new ByteArrayInputStream(out
					.toByteArray());
			byte buffer[] = readAll(bin, 0);
			bin.close();
			// Create a temporary file, with a unique file name
			Random rand1 = new Random();
			String zName = new String(rand1.nextInt(1000) + ".tmp");
			File temp = File.createTempFile("OSGi", "BTOOL");
			while (temp.exists()) {
				zName = new String(rand1.nextInt(1000) + ".tmp");
				temp = new File(zName);
			}
			// Change name of file _zipname
			File zipN = new File(zName);
			File zipFile = new File(zipname);
			zipFile.renameTo(zipN);
			// Create a new zip output stream
			FileOutputStream zout = new FileOutputStream(zipname);
			trace("Creating " + zipname);
			ZipOutputStream _zipOut = new ZipOutputStream(zout);
			ZipEntry ze = new ZipEntry("META-INF/MANIFEST.MF");
			ze.setSize(buffer.length);
			CRC32 checksum = new CRC32();
			checksum.update(buffer);
			ze.setCrc(checksum.getValue());
			_zipOut.putNextEntry(ze);
			_zipOut.write(buffer, 0, buffer.length);
			// Open the input zip file,
			FileInputStream zin = new FileInputStream(zName);
			ZipInputStream _zipIn = new ZipInputStream(zin);
			while ((ze = _zipIn.getNextEntry()) != null) {
				ZipEntry zu = new ZipEntry(ze);
				_zipOut.putNextEntry(zu);
				byte[] databuf = new byte[1024];
				for (int size = 0; (size = _zipIn.read(databuf, 0, 1024)) > 0;) {
					_zipOut.write(databuf, 0, size);
				}
				_zipOut.closeEntry();
				_zipIn.closeEntry();
			}
			_zipIn.close();
			_zipOut.close();
			// Remove temporary file
			zipN.delete();
		} catch (IOException e) {
			e.printStackTrace();
			errors.addElement("Could not handle manifest: " + e);
		}
	}

	/**
	 * Print the checksums
	 */
	void printChecksums(PrintWriter pw, Hashtable checksums) {

		if (checksums == null)
			return;
		for (Enumeration e = checksums.elements(); e.hasMoreElements();) {
			Object o = e.nextElement();
			if (showmanifest)
				System.out.print(o);
			pw.print(o);
		}
	}

	void expands() throws IOException {
		if (expands == null)
			return;

		StringTokenizer st = new StringTokenizer(expands, " ,");
		while (st.hasMoreElements()) {
			String pack = st.nextToken().trim().replace('.','/');
			trace("Expand " + pack);
			
			Source source = findSource(pack);
			if ( source != null ) {
				addRecursive(source, pack );
			}
		}
	}

	void includes() throws IOException {
		if (includes == null)
			return;

		StringTokenizer st = new StringTokenizer(includes, ",");
		while (st.hasMoreElements()) {
			String file = st.nextToken().trim();
			String outname = null;
			int n = file.indexOf('=');
			if (n > 0) {
				outname = file.substring(0, n);
				file = file.substring(n + 1).trim();
			} else
				outname = getFileName(file);
			trace("Include " + file + " as " + outname);
			Source s;
			if (file.startsWith("/")) {
				file = file.substring(1);
				s = workspace;
			} else
				s = project;

			Resource r = new Resource(this, s, outname);
			r.setSourcePath(file);
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

	void addRecursive(Source source, String path) throws IOException {
		if (path.equals("META-INF/MANIFEST.MF"))
			return;

		if (source.isDirectory(path)) {
			trace("Dir " + path);
			if (!path.equals(""))
				addContents(new PackageResource(this, source, path));

			if (path.length() > 0)
				path = path + "/";

			Collection c = source.getResources(path);
			for (Iterator i = c.iterator(); i.hasNext();) {
				String name = path + i.next();
				addRecursive(source, name);
			}
		} else {
			trace("File " + path);
			addContents(new Resource(source, path));
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
			StringTokenizer st = new StringTokenizer(eclipse.getClasspath(),
					",");
			while (st.hasMoreElements()) {
				String file = st.nextToken().trim();
				File f = new File(file);
				if (f.exists()) {
					trace("Classpath File " + f);
					if (f.isDirectory()) {
						mainClasspath.add(new DirSource(f));
					} else
						mainClasspath.add(new ZipSource(f));
				} else
					warning("No such Classpath file " + f.getAbsolutePath());
			}
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

	public void setSilent(boolean silent) {
		this.silent = silent;
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
			getProject().setInheritedProperty(n, v);
	}
	/**
	 * @param source
	 *            The source to set.
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
		System.out.println("Exclude exports " + excludeImport);
	}
	
	public void setIncludeExportProperty(String key) {
		includeExport = makeListFromProperty(key);
		System.out.println("Include exports " + includeExport);
	}

	List makeListFromProperty(String key) {
		Vector	v = new Vector();
		String list = getProject().getProperty(key);
		if ( list == null ) {
			System.out.println("No Such property " + key);
			return null;
		}
		StringTokenizer st = new StringTokenizer(list,", ");
		while ( st.hasMoreTokens() ) {
			v.add(st.nextToken());
		}
		return v;
	}
	
	/**
	 * @return @throws
	 *         IOException
	 */
	public Collection getImports() throws IOException {
		initDependencies();
		return dependencies.getReferred();
	}

	/**
	 * @throws IOException
	 */
	private void initDependencies() throws IOException {
		if (dependencies == null) {
			dependencies = new Dependencies(mainClasspath, manifest, contents, excludeImport, includeExport );
			dependencies.calculate();
		}
	}

	/**
	 * @return @throws
	 *         IOException
	 */
	public Collection getExports() throws IOException {
		initDependencies();
		return dependencies.getContained();
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
}