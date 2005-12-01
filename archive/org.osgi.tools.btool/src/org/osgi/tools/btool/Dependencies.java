package org.osgi.tools.btool;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class Dependencies {
	BTool		btool;
	Set			referred	= new HashSet();
	Set			contained	= new HashSet();
	Map			dot			= new HashMap();
	Map			uses		= new HashMap();
	List		includeExport;
	List		excludeImport;
	Manifest	manifest;
	Collection	classpath;
	static byte	SkipTable[]	= {0, // 0 non existent
							-1, // 1 CONSTANT_utf8 UTF 8, handled in
							// method
							-1, // 2
							4, // 3 CONSTANT_Integer
							4, // 4 CONSTANT_Float
							8, // 5 CONSTANT_Long (index +=2!)
							8, // 6 CONSTANT_Double (index +=2!)
							-1, // 7 CONSTANT_Class
							2, // 8 CONSTANT_String
							4, // 9 CONSTANT_FieldRef
							4, // 10 CONSTANT_MethodRef
							4, // 11 CONSTANT_InterfaceMethodRef
							4 // 12 CONSTANT_NameAndType
							};

	public Dependencies(BTool btool, Collection classpath, Manifest manifest,
			Map resources, List excludeImport, List includeExport) {
		this.manifest = manifest;
		this.classpath = classpath;
		this.includeExport = includeExport;
		this.excludeImport = excludeImport;
		this.btool = btool;
		for (Iterator i = resources.values().iterator(); i.hasNext();) {
			Resource r = (Resource) i.next();
			dot.put(r.getPath(), r);
		}
	}

	public void calculate() throws IOException {
		btool.trace("calculate");
		String bundleClasspath = manifest.getValue("bundle-classpath");
		for ( Iterator i = btool.getExtraRoots().iterator(); i.hasNext(); ) {
			referred.add(i.next());
		}
		if (bundleClasspath == null)
			bundleClasspath = ".";
		StringTokenizer st = new StringTokenizer(bundleClasspath, ",");
		while (st.hasMoreTokens()) {
			String entry = st.nextToken().trim();
			if (".".equals(entry)) {
				for (Iterator i = dot.values().iterator(); i.hasNext();) {
					Resource r = (Resource) i.next();
					if (!(r instanceof PackageResource)) {
						addContained(base(r.getPath()));
					}
					if (r.getPath().endsWith(".class")) {
						InputStream in = r.getInputStream();
						if (in != null)
							doStream(r.getPath(), in);
						else
							System.err.println("Cannot parse " + r.getPath());
					}
				}
			}
			else {
				Resource r = (Resource) dot.get(entry);
				if (r != null) {
					InputStream in = r.getInputStream();
					if (in == null)
						btool.errors
								.add("BundleClasspath contains non existent entry "
										+ entry);
					else {
						JarInputStream jarStream = new JarInputStream(in);
						JarEntry jarEntry = jarStream.getNextJarEntry();
						while (jarEntry != null) {
							addContained(base(jarEntry.getName()));
							if (jarEntry.getName().endsWith(".class")) {
								doStream(jarEntry.getName(), jarStream);
							}
							jarEntry = jarStream.getNextJarEntry();
						}
					}
				}
				else
					btool.errors.addElement("No such bundle-classpath entry "
							+ entry);
			}
		}
		//
		// Special case for Bundle Activator, it is also
		// a reference that might point outside our bundle
		String act = manifest.getActivator();
		if (act != null) {
			String path = act.replace('.', '/') + ".class";
			String packageName = base(path);
			referred.add(packageName);
			addUses(base(path), packageName);
		}
		referred.removeAll(contained);
	}

	/**
	 * @param inputStream
	 * @throws IOException
	 */
	private void doStream(String sourceName, InputStream inputStream)
			throws IOException {
		DataInputStream din = new DataInputStream(inputStream);
		Collection coll = parseClassFile(din);
		String sourcePackage = base(sourceName);
		for (Iterator i = coll.iterator(); i.hasNext();) {
			String path = (String) i.next();
			String packageName = base(path);
			if (!packageName.startsWith("java/")) {
				referred.add(packageName);
				addUses(sourcePackage, packageName);
			}
		}
	}

	void addUses(String sourcePackage, String packageName) {
		if (sourcePackage.equals(packageName))
			return;

		Set set = (Set) uses.get(sourcePackage);
		if (set == null) {
			set = new TreeSet();
			uses.put(sourcePackage, set);
		}
		if (!set.contains(packageName)) {
			btool.trace("Uses " + sourcePackage + " <= " + packageName);
			set.add(packageName);
		}
	}

	/**
	 * @param resource
	 */
	void addContained(String packagePath) {
		if (!contained.contains(packagePath))
			contained.add(packagePath);
	}

	/**
	 * Parse a class file and find the other classes that are referenced.
	 * 
	 * This function is recursively called for each class and all the classes it
	 * refers to. The format of the class file is described in the "Java Virtual
	 * Machine Specification".
	 * 
	 * Notice that CONSTANT_Long and CONSTANT_Double use TWO (2) positions in
	 * the constant pool. This took me a couple of hours to figure out.
	 */
	Collection parseClassFile(DataInputStream in) throws IOException {
		Set classes = new HashSet();
		Set descriptors = new HashSet();
		Hashtable pool = new Hashtable();
		try {
			int magic = in.readInt();
			if (magic != 0xCAFEBABE)
				throw new IOException(
						"Not a valid class file (no CAFEBABE header)");
			in.readShort(); // minor version
			in.readShort(); // major version
			int count = in.readUnsignedShort();
			process: for (int i = 1; i < count; i++) {
				byte tag = in.readByte();
				switch (tag) {
					case 0 :
						break process;
					case 1 :
						// CONSTANT_Utf8
						String name = in.readUTF();
						pool.put(new Integer(i), name);
						break;
					// A Class constant is just a short reference in
					// the constant pool
					case 7 :
						// CONSTANT_Class
						Integer index = new Integer(in.readShort());
						classes.add(index);
						break;
					// For some insane optimization reason are
					// the long and the double two entries in the
					// constant pool. See 4.4.5
					case 5 :
					// CONSTANT_Long
					case 6 :
						// CONSTANT_Double
						in.skipBytes(8);
						i++;
						break;
						
					// Interface Method Ref
					case 12 :
						int nameIndex = in.readShort();
						int descriptorIndex = in.readShort();
						descriptors.add( new Integer(descriptorIndex));
						break;
						
					// We get the skip count for each record type
					// from the SkipTable. This will also automatically
					// abort when
					default :
						if (tag == 2)
							throw new IOException("Invalid tag " + tag);
						in.skipBytes(SkipTable[tag]);
						break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//
		// Now iterate over all classes we found and
		// parse those as well. We skip duplicates
		//
		Collection set = new HashSet();
		
		for (Iterator e = classes.iterator(); e.hasNext();) {
			Integer n = (Integer) e.next();
			String next = (String) pool.get(n);
			if (next != null) {
				String normalized = normalize(next);
				if (normalized != null) {
					set.add(normalized);
				}
			}
			else
				throw new IllegalArgumentException("Invalid class, parent=");
		}
		for (Iterator e = descriptors.iterator(); e.hasNext();) {
			Integer n = (Integer) e.next();
			String next = (String) pool.get(n);
			if (next != null) 
				parseDescriptor(set, next);
		}
		return set;
	}

	void parseDescriptor(Collection set, String next) {
		StringTokenizer st = new StringTokenizer(next,"(;)", true );
		while ( st.hasMoreTokens() ) {
			if ( st.nextToken().equals("(")) {
				String token = st.nextToken();
				while ( ! token.equals(")")) {
					addReference(set, token);
					token = st.nextToken();
				}
				token = st.nextToken();
				addReference(set,token);
			}
		}
	}

	private void addReference(Collection set, String token) {
		if ( token.startsWith("L")) {
			String clazz = normalize(token.substring(1));
			set.add(clazz);
		}
	}

	/**
	 * Decode a class name from a class reference.
	 * 
	 * Java encodes array information in the name of the class. This means that
	 * we should get rid of this. The following rules apply:
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *     
	 *      
	 *       
	 *        
	 *         
	 *          
	 *           
	 *            
	 *             
	 *                    	 [L*               =&gt; remove [L, try again
	 *                    	 [* &amp;&amp; length=2     =&gt; ignore (int,byte,char etc class)
	 *                    	 [*                =&gt; remove [, try again
	 *                    ;                =&gt; remove ;, try again (do not know why)
	 *                    	 A i in skip | &lt;i&gt;* =&gt; ignore
	 *              
	 *             
	 *            
	 *           
	 *          
	 *         
	 *        
	 *       
	 *      
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * Notice that we ALWAYS suffix the name with class. E.g. normalize cannot
	 * be used for normal resource names.
	 */
	String normalize(String s) {
		if (s.startsWith("[L"))
			return normalize(s.substring(2));
		if (s.startsWith("["))
			if (s.length() == 2)
				return null;
			else
				return normalize(s.substring(1));
		if (s.endsWith(";"))
			return normalize(s.substring(0, s.length() - 1));
		return s + ".class";
	}

	/*
	 * Answer the base part of the resource name, e.g. parent.
	 * 
	 */
	static String base(String name) {
		int index = name.lastIndexOf('/');
		if (index > 0)
			return name.substring(0, index);
		else
			return name;
	}

	/**
	 * @return Returns the contained.
	 * @throws IOException
	 */
	public Set getContained() throws IOException {
		return convertToPackage(contained, includeExport, null);
	}

	/**
	 * @param contained2
	 * @return
	 * @throws IOException
	 */
	private Set convertToPackage(Collection names, List includes, List excludes)
			throws IOException {
		Set result = new TreeSet();
		for (Iterator i = names.iterator(); i.hasNext();) {
			String name = (String) i.next();
			if (excludes != null && in(excludes, name)) {
				continue;
			}
			if (includes == null || in(includes, name)) {
				Package p = makePackage(name);
				if (p != null) {
					p.uses = (Set) uses.get(p.getPath());
					result.add(p);
				}
			}
		}
		return result;
	}

	/**
	 * @param excludes
	 * @param name
	 * @return
	 */
	private boolean in(List list, String name) {
		name = name.replace('/', '.');
		for (Iterator i = list.iterator(); i.hasNext();) {
			String prefix = (String) i.next();
			if (prefix.endsWith("*")) {
				if (name.startsWith(prefix.substring(0, prefix.length() - 1)))
					return true;
			}
			if (name.equals(prefix))
				return true;
		}
		return false;
	}

	/**
	 * @param name
	 * @return
	 */
	Package makePackage(String name) throws IOException {
		if ("META-INF".equals(name))
			return null;
		try {
			String version = null;
			String packname = name.replace('/', '.');
			String info = name + "/packageinfo";
			Source source = findResource(info);
			if (source != null) {
				InputStream in = source.getEntry(info);
				byte[] buffer = readAll(in, 0);
				String s = new String(buffer);
				Map map = getMap(s);
				if (map == null)
					btool.warnings.add("Invalid package def: " + name);
				else if (map.containsKey("version")) {
					version = (String) map.get("version");
				}
			}
			// if ( version == null ){
			// version = findVersionInManifest(name, version, source);
			// }
			return new Package(packname, version);
		}
		catch (Exception e) {
			btool.warning("Invalid packagedefinition " + name);
			e.printStackTrace();
		}
		return null;
	}

	Map getMap(String definition) {
		Map map = new Hashtable();
		StringTokenizer st = new StringTokenizer(definition, " \n\r\t");
		do {
			String t1 = st.nextToken();
			if (!st.hasMoreTokens())
				return null;
			String t2 = st.nextToken();
			map.put(t1, t2);
		} while (st.hasMoreTokens());
		return map;
	}

	/**
	 * @param name
	 * @param version
	 * @param source
	 * @return
	 */
	String findVersionInManifest(String name, String version,
			Source source) {
		try {
			// Try using he manifest for a good version
			// of the package. Ugly, but hey, its a mess.
			InputStream min = source.getEntry("META-INF/MANIFEST.MF");
			if (min != null) {
				Manifest manifest = new Manifest(btool, min);
				Package p = find(name.replace('/', '.'), manifest.getExports());
				if (p != null) {
					version = p.version;
				}
				else {
					InputStream jin = source.getEntry("META-INF/MANIFEST.MF");
					java.util.jar.Manifest jmanifest = new java.util.jar.Manifest(
							jin);
					Attributes att = jmanifest.getAttributes(name + "/");
					if (att != null) {
						String specversion = att.getValue(new Attributes.Name(
								"Specification-Version"));
						if (specversion != null) {
							specversion = specversion.trim();
							if (specversion.startsWith("\"")) {
								specversion = specversion.substring(1,
										specversion.length() - 1);
							}
							version = specversion;
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * @param exports
	 * @return
	 */
	private Package find(String packageName, Package[] exports) {
		for (int i = 0; i < exports.length; i++) {
			if (exports[i].name.equals(packageName))
				return exports[i];
		}
		return null;
	}

	/**
	 * @param string
	 * @return
	 * @throws IOException
	 */
	private Source findResource(String string) throws IOException {
		for (Iterator i = classpath.iterator(); i.hasNext();) {
			Source s = (Source) i.next();
			if (s.contains(string)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * @return Returns the referred.
	 * @throws IOException
	 */
	public Set getReferred() throws IOException {
		return convertToPackage(referred, null, excludeImport);
	}

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
	 * @return
	 */
	public Set getImported() {
		Set result = new HashSet(referred);
		result.removeAll(contained);
		return result;
	}
}