/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.defaultadaptor;

import java.io.*;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.framework.adaptor.core.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

/**
 * A concrete implementation of BundleClassLoader.  This implementation 
 * consolidates all Bundle-ClassPath entries into a single ClassLoader.
 */
public class DefaultClassLoader extends AbstractClassLoader {
	/**
	 * The BundleData object for this BundleClassLoader
	 */
	protected AbstractBundleData hostdata;

	/**
	 * The ClasspathEntries for this BundleClassLoader.  Each ClasspathEntry object
	 * represents on Bundle-ClassPath entry.
	 */
	protected ClasspathEntry[] classpathEntries;

	protected Vector fragClasspaths; //TODO This should be an array or an arraylist if the synchronization is not required

	/**
	 * The buffer size to use when loading classes.  This value is used 
	 * only if we cannot determine the size of the class we are loading.
	 */
	protected int buffersize = 8 * 1024; //TODO Could not that be a constant?

	/**
	 * BundleClassLoader constructor.
	 * @param delegate The ClassLoaderDelegate for this ClassLoader.
	 * @param domain The ProtectionDomain for this ClassLoader.
	 * @param classpath An array of Bundle-ClassPath entries to
	 * use for loading classes and resources.  This is specified by the 
	 * Bundle-ClassPath manifest entry.
	 * @param parent The parent ClassLoader.
	 * @param bundledata The BundleData for this ClassLoader
	 */
	public DefaultClassLoader(ClassLoaderDelegate delegate, ProtectionDomain domain, String[] classpath, ClassLoader parent, AbstractBundleData bundledata) {
		super(delegate, domain, classpath, parent);
		this.hostdata = bundledata;

		try {
			hostdata.open(); /* make sure the BundleData is open */
		} catch (IOException e) {
			hostdata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, hostdata.getBundle(), e);
		}
	}

	public void initialize() {
		classpathEntries = buildClasspath(hostclasspath, hostdata, hostdomain);
	}

	/**
	 * Attaches the BundleData for a fragment to this BundleClassLoader.
	 * The Fragment BundleData resources must be appended to the end of
	 * this BundleClassLoader's classpath.  Fragment BundleData resources 
	 * must be searched ordered by Bundle ID's.  
	 * @param bundledata The BundleData of the fragment.
	 * @param domain The ProtectionDomain of the resources of the fragment.
	 * Any classes loaded from the fragment's BundleData must belong to this
	 * ProtectionDomain.
	 * @param classpath An array of Bundle-ClassPath entries to
	 * use for loading classes and resources.  This is specified by the 
	 * Bundle-ClassPath manifest entry of the fragment.
	 */
	public void attachFragment(org.eclipse.osgi.framework.adaptor.BundleData bundledata, ProtectionDomain domain, String[] classpath) {
		AbstractBundleData abstractbundledata = (AbstractBundleData) bundledata;
		try {
			bundledata.open(); /* make sure the BundleData is open */
		} catch (IOException e) {

			abstractbundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, abstractbundledata.getBundle(), e);
		}
		ClasspathEntry[] fragEntries = buildClasspath(classpath, abstractbundledata, domain);
		FragmentClasspath fragClasspath = new FragmentClasspath(fragEntries, abstractbundledata, domain);
		insertFragment(fragClasspath);
	}

	/**
	 * Inserts a fragment classpath to into the list of fragments for this host.
	 * Fragments are inserted into the list according to the fragment's 
	 * Bundle ID.
	 * @param fragClasspath The FragmentClasspath to insert.
	 */
	protected synchronized void insertFragment(FragmentClasspath fragClasspath) {
		if (fragClasspaths == null) {
			// First fragment to attach.  Simply create the list and add the fragment.
			fragClasspaths = new Vector(10);
			fragClasspaths.addElement(fragClasspath);
			return;
		}

		// Find a place in the fragment list to insert this fragment.
		int size = fragClasspaths.size();
		long fragID = fragClasspath.bundledata.getBundleID();
		for (int i = 0; i < size; i++) {
			long otherID = ((FragmentClasspath) fragClasspaths.elementAt(i)).bundledata.getBundleID();
			if (fragID < otherID) {
				fragClasspaths.insertElementAt(fragClasspath, i);
				return;
			}
		}
		// This fragment has the highest ID; put it at the end of the list.
		fragClasspaths.addElement(fragClasspath);
	}

	protected String getBundleSymbolicName() {
		return hostdata.getSymbolicName() + "_" + hostdata.getVersion();
	}

	/**
	 * Gets a ClasspathEntry object for the specified ClassPath entry.
	 * @param cp The ClassPath entry to get the ClasspathEntry for.
	 * @param bundledata The BundleData that the ClassPath entry is for.
	 * @param domain The ProtectionDomain for the ClassPath entry.
	 * @return The ClasspathEntry object for the ClassPath entry.
	 */
	protected ClasspathEntry getClasspath(String cp, AbstractBundleData bundledata, ProtectionDomain domain) {
		BundleFile bundlefile = null;
		File file = bundledata.getBaseBundleFile().getFile(cp);
		if (file != null && file.exists()) {
			try {
				bundlefile = hostdata.getAdaptor().createBundleFile(file, bundledata);
			} catch (IOException e) {
				bundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, bundledata.getBundle(), e);
			}
		} else if (bundledata.getBaseBundleFile().containsDir(cp)) {
			// the classpath entry is a directory in the bundle jar file.
			bundlefile = new BundleFile.NestedDirBundleFile(bundledata.getBaseBundleFile(), cp);
		}

		// if in dev mode, try using the cp as an absolute path
		if (bundlefile == null && DevClassPathHelper.inDevelopmentMode()) {
			file = new File(cp);
			if (file.exists() && file.isAbsolute())
				// if the file exists and is absolute then create BundleFile for it.
				try {
					bundlefile = hostdata.getAdaptor().createBundleFile(file, bundledata);
				} catch (IOException e) {
					bundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, bundledata.getBundle(), e);
				}
		}

		if (bundlefile != null)
			return createClassPathEntry(bundlefile, domain);
		else
			return null;
	}

	protected synchronized Class findClass(String name) throws ClassNotFoundException {
		Class result = findLoadedClass(name);
		if (result != null)
			return result;
		for (int i = 0; i < classpathEntries.length; i++) {
			if (classpathEntries[i] != null) {
				result = findClassImpl(name, classpathEntries[i]);
				if (result != null) {
					return result;
				}
			}
		}
		// look in fragments.
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				for (int j = 0; j < fragCP.classpathEntries.length; j++) {
					result = findClassImpl(name, fragCP.classpathEntries[j]);
					if (result != null) {
						return result;
					}
				}
			}
		}
		// Finally check the parent classloader for system classes.
		ClassLoader parent = getParentPrivileged();
		if (parent != null)
			return parent.loadClass(name);
		throw new ClassNotFoundException(name);
	}

	/**
	 * Finds a class in the BundleFile.  If a class is found then the class
	 * is defined using the ProtectionDomain bundledomain.
	 * @param name The name of the class to find.
	 * @param classpathEntry The ClasspathEntry to find the class in.
	 * @return The loaded class object or null if the class is not found.
	 */
	protected Class findClassImpl(String name, ClasspathEntry classpathEntry) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("BundleClassLoader[" + hostdata + "].findClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		}

		String filename = name.replace('.', '/').concat(".class"); //$NON-NLS-1$

		BundleEntry entry = classpathEntry.getBundleFile().getEntry(filename);

		if (entry == null) {
			return null;
		}

		InputStream in;
		try {
			in = entry.getInputStream();
		} catch (IOException e) {
			return null;
		}

		int length = (int) entry.getSize();
		byte[] classbytes;
		int bytesread = 0;
		int readcount;

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("  about to read " + length + " bytes from " + filename); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			try {
				if (length > 0) {
					classbytes = new byte[length];

					readloop: for (; bytesread < length; bytesread += readcount) {
						readcount = in.read(classbytes, bytesread, length - bytesread);

						if (readcount <= 0) /* if we didn't read anything */{
							break readloop; /* leave the loop */
						}
					}
				} else /* BundleEntry does not know its own length! */{
					length = buffersize;
					classbytes = new byte[length];

					readloop: while (true) {
						for (; bytesread < length; bytesread += readcount) {
							readcount = in.read(classbytes, bytesread, length - bytesread);

							if (readcount <= 0) /* if we didn't read anything */{
								break readloop; /* leave the loop */
							}
						}

						byte[] oldbytes = classbytes;
						length += buffersize;
						classbytes = new byte[length];
						System.arraycopy(oldbytes, 0, classbytes, 0, bytesread);
					}
				}
			} catch (IOException e) {
				if (Debug.DEBUG && Debug.DEBUG_LOADER) {
					Debug.println("  IOException reading " + filename + " from " + hostdata); //$NON-NLS-1$ //$NON-NLS-2$
				}

				return null;
			}
		} finally {
			try {
				in.close();
			} catch (IOException ee) {
			}
		}

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("  read " + bytesread + " bytes from " + filename); //$NON-NLS-1$ //$NON-NLS-2$
			Debug.println("  defining class " + name); //$NON-NLS-1$
		}

		try {
			return (defineClass(name, classbytes, 0, bytesread, classpathEntry));
		} catch (Error e) {
			if (Debug.DEBUG && Debug.DEBUG_LOADER) {
				Debug.println("  error defining class " + name); //$NON-NLS-1$
			}

			throw e;
		}
	}

	protected Class defineClass(String name, byte[] classbytes, int off, int len, ClasspathEntry classpathEntry) throws ClassFormatError {
		if (name != null && name.startsWith("java.")) { //$NON-NLS-1$
			// To work around the security issue that prevents any
			// other classloader except for the bootstrap classloader
			// from loading packages that start with java.
			name = null;
		}
		return defineClass(name, classbytes, off, len, classpathEntry.getProtectionDomain());
	}

	protected URL findResource(String name) {
		URL result = null;
		for (int i = 0; i < classpathEntries.length; i++) {
			if (classpathEntries[i] != null) {
				result = findResourceImpl(name, classpathEntries[i].getBundleFile());
				if (result != null) {
					return result;
				}
			}
		}
		// look in fragments
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				for (int j = 0; j < fragCP.classpathEntries.length; j++) {
					result = findResourceImpl(name, fragCP.classpathEntries[j].getBundleFile());
					if (result != null) {
						return result;
					}
				}
			}
		}
		// Finally check the parent classloader for system resources.
		ClassLoader parent = getParentPrivileged();
		if (parent != null)
			return parent.getResource(name);
		return null;
	}

	/**
	 * Looks in the specified BundleFile for the resource.
	 * @param name The name of the resource to find.
	 * @param bundlefile The BundleFile to look in.
	 * @return A URL to the resource or null if the resource does not exist.
	 */
	protected URL findResourceImpl(String name, BundleFile bundlefile) {
		return findResourceImpl(name, bundlefile, 0);
	}

	/**
	 * Looks in the specified BundleFile for the resource.
	 * @param name The name of the resource to find.
	 * @param bundlefile The BundleFile to look in.
	 * @param index the index of the resource.
	 * @return A URL to the resource or null if the resource does not exist.
	 */
	protected URL findResourceImpl(String name, BundleFile bundlefile, int index) {
		return bundlefile.getResourceURL(name, hostdata.getBundleID(), index);
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.BundleClassLoader#findLocalResources(String)
	 */
	public Enumeration findLocalResources(String resource) {
		Vector resources = new Vector(6); // use a Vector instead of ArrayList because we need an enumeration
		for (int i = 0; i < classpathEntries.length; i++) {
			if (classpathEntries[i] != null) {
				URL url = findResourceImpl(resource, classpathEntries[i].getBundleFile(), resources.size());
				if (url != null)
					resources.addElement(url);
			}
		}
		// look in fragments
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				for (int j = 0; j < fragCP.classpathEntries.length; j++) {
					URL url = findResourceImpl(resource, fragCP.classpathEntries[j].getBundleFile(), resources.size());
					if (url != null)
						resources.addElement(url);
				}
			}
		}
		if (resources.size() > 0)
			return resources.elements();
		return null;
	}

	public Object findLocalObject(String object) {
		BundleEntry result = null;
		for (int i = 0; i < classpathEntries.length; i++) {
			if (classpathEntries[i] != null) {
				result = findObjectImpl(object, classpathEntries[i].getBundleFile());
				if (result != null) {
					return result;
				}
			}
		}
		// look in fragments
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				for (int j = 0; j < fragCP.classpathEntries.length; j++) {
					result = findObjectImpl(object, fragCP.classpathEntries[j].getBundleFile());
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	public Enumeration findLocalObjects(String object) {
		Vector objects = new Vector(6); // use a Vector instead of ArrayList because we need an enumeration
		for (int i = 0; i < classpathEntries.length; i++) {
			if (classpathEntries[i] != null) {
				Object result = findObjectImpl(object, classpathEntries[i].getBundleFile());
				if (result != null)
					objects.addElement(result);
			}
		}
		// look in fragments
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				for (int j = 0; j < fragCP.classpathEntries.length; j++) {
					Object result = findObjectImpl(object, fragCP.classpathEntries[j].getBundleFile());
					if (result != null)
						objects.addElement(result);
				}
			}
		}
		if (objects.size() > 0)
			return objects.elements();
		return null;
	}

	protected BundleEntry findObjectImpl(String object, BundleFile bundleFile) {
		return bundleFile.getEntry(object);
	}

	/**
	 * Closes all the BundleFile objects for this BundleClassLoader.
	 */
	public void close() {
		// do not close if we are shutting down
		if (closed || hostdata.getAdaptor().isStopping())
			return;

		super.close();
		if (classpathEntries != null) {
			for (int i = 0; i < classpathEntries.length; i++) {
				if (classpathEntries[i] != null) {
					try {
						if (classpathEntries[i].getBundleFile() != hostdata.getBaseBundleFile()) {
							classpathEntries[i].getBundleFile().close();
						}
					} catch (IOException e) {
						hostdata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, hostdata.getBundle(), e);
					}
				}
			}
		}
		if (fragClasspaths != null) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				fragCP.close();
			}
		}
	}

	protected ClasspathEntry[] buildClasspath(String[] classpath, AbstractBundleData bundledata, ProtectionDomain domain) {
		ArrayList result = new ArrayList(classpath.length);

		// If not in dev mode then just add the regular classpath entries and return
		if (!DevClassPathHelper.inDevelopmentMode()) {
			for (int i = 0; i < classpath.length; i++)
				findClassPathEntry(result, classpath[i], bundledata, domain);
			return (ClasspathEntry[]) result.toArray(new ClasspathEntry[result.size()]);
		}

		// Otherwise, add the legacy entries for backwards compatibility and
		// then for each classpath entry add the dev entries as spec'd in the 
		// corresponding properties file.  If none are spec'd, add the 
		// classpath entry itself
		addDefaultDevEntries(result, bundledata, domain);
		for (int i = 0; i < classpath.length; i++) {
			String[] devEntries = getDevEntries(classpath[i], bundledata);
			if (devEntries != null && devEntries.length > 0) {
				for (int j = 0; j < devEntries.length; j++)
					findClassPathEntry(result, devEntries[j], bundledata, domain);
			} else
				findClassPathEntry(result, classpath[i], bundledata, domain);
		}
		return (ClasspathEntry[]) result.toArray(new ClasspathEntry[result.size()]);
	}

	protected void addDefaultDevEntries(ArrayList result, AbstractBundleData bundledata, ProtectionDomain domain) {
		String[] devClassPath = DevClassPathHelper.getDevClassPath(bundledata.getSymbolicName());
		if (devClassPath == null)
			return;
		for (int i = 0; i < devClassPath.length; i++)
			findClassPathEntry(result, devClassPath[i], bundledata, domain);
	}

	protected void findClassPathEntry(ArrayList result, String entry, AbstractBundleData bundledata, ProtectionDomain domain) {
		if (!addClassPathEntry(result, entry, bundledata, domain)) {
			//			if (devCP == null) {
			//				BundleException be = new BundleException(Msg.formatter.getString("BUNDLE_CLASSPATH_ENTRY_NOT_FOUND_EXCEPTION", entry, hostdata.getLocation()));
			//				bundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, bundledata.getBundle(), be);
			//			}
		}
	}

	protected boolean addClassPathEntry(ArrayList result, String entry, AbstractBundleData bundledata, ProtectionDomain domain) {
		if (entry.equals(".")) { //$NON-NLS-1$
			result.add(createClassPathEntry(bundledata.getBaseBundleFile(), domain));
			return true;
		}
		Object element = getClasspath(entry, bundledata, domain);
		if (element != null) {
			result.add(element);
			return true;
		}
		// need to check in fragments for the classpath entry.
		// only check for fragments if the bundledata is the hostdata.
		if (fragClasspaths != null && hostdata == bundledata) {
			int size = fragClasspaths.size();
			for (int i = 0; i < size; i++) {
				FragmentClasspath fragCP = (FragmentClasspath) fragClasspaths.elementAt(i);
				element = getClasspath(entry, fragCP.bundledata, fragCP.domain);
				if (element != null) {
					result.add(element);
					return true;
				}
			}
		}
		return false;
	}

	protected String[] getDevEntries(String classpathEntry, AbstractBundleData bundledata) {
		File propLocation = bundledata.getBaseBundleFile().getFile(classpathEntry + ".properties"); //$NON-NLS-1$
		if (propLocation == null)
			return null;
		try {
			InputStream in = new FileInputStream(propLocation);
			try {
				Properties devProps = new Properties();
				devProps.load(in);
				return DevClassPathHelper.getArrayFromList(devProps.getProperty("bin")); //$NON-NLS-1$
			} finally {
				in.close();
			}
		} catch (IOException e) {
			BundleException be = new BundleException(NLS.bind(AdaptorMsg.BUNDLE_CLASSPATH_PROPERTIES_ERROR, propLocation), e); //$NON-NLS-1$
			bundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, bundledata.getBundle(), be);
		}
		return null;
	}

	/**
	 * Creates a ClasspathEntry from a BundleFile and ProtectionDomain.
	 * @param bundlefile the BundleFile.
	 * @param domain the ProtectionDomain
	 * @return the ClasspathEntry
	 */
	protected ClasspathEntry createClassPathEntry(BundleFile bundlefile, ProtectionDomain domain) {
		return new ClasspathEntry(bundlefile, domain);
	}

	/**
	 * A data structure to hold information about a fragment classpath.
	 */
	protected class FragmentClasspath {
		/** The ClasspathEntries of the fragments Bundle-Classpath */
		protected ClasspathEntry[] classpathEntries;
		/** The BundleData of the fragment */
		protected AbstractBundleData bundledata;
		/** The ProtectionDomain of the fragment */
		protected ProtectionDomain domain;

		protected FragmentClasspath(ClasspathEntry[] classpathEntries, AbstractBundleData bundledata, ProtectionDomain domain) {
			this.classpathEntries = classpathEntries;
			this.bundledata = bundledata;
			this.domain = domain;
		}

		protected void close() {
			for (int i = 0; i < classpathEntries.length; i++) {
				try {
					if (classpathEntries[i].getBundleFile() != bundledata.getBaseBundleFile()) {
						classpathEntries[i].getBundleFile().close();
					}
				} catch (IOException e) {
					bundledata.getAdaptor().getEventPublisher().publishFrameworkEvent(FrameworkEvent.ERROR, bundledata.getBundle(), e);
				}
			}
		}
	}

	/**
	 * A data structure to hold information about a classpath entry.
	 */
	protected class ClasspathEntry {
		protected BundleFile bundlefile;
		protected ProtectionDomain domain;

		protected ClasspathEntry(BundleFile bundlefile, ProtectionDomain domain) {
			this.bundlefile = bundlefile;
			this.domain = domain;
		}

		public BundleFile getBundleFile() {
			return bundlefile;
		}

		public ProtectionDomain getProtectionDomain() {
			return domain;
		}
	}

}
