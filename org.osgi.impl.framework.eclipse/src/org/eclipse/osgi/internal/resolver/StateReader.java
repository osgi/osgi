/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Version;

class StateReader {

	// objectTable will be a hashmap of objects. The objects will be things
	// like BundleDescription, ExportPackageDescription, Version etc.. The integer
	// index value will be used in the cache to allow cross-references in the
	// cached state.
	protected Map objectTable = new HashMap();
	private File cacheFile;
	private boolean lazyLoad = true;
	private int lazyDataOffset;
	private int numBundles;

	public static final byte STATE_CACHE_VERSION = 10;
	public static final byte NULL = 0;
	public static final byte OBJECT = 1;
	public static final byte INDEX = 2;

	public StateReader() {
		this(null, false);
	}

	public StateReader(File cacheFile, boolean lazyLoad) {
		this.cacheFile = cacheFile;
		this.lazyLoad = lazyLoad;
	}

	private int addToObjectTable(Object object, int index) {
		objectTable.put(new Integer(index),object);
		// return the index of the object just added (i.e. size - 1)
		return index;
	}

	private Object getFromObjectTable(int index) {
		return objectTable.get(new Integer(index));
	}

	private boolean readState(StateImpl state, DataInputStream in, long expectedTimestamp) throws IOException {
		if (in.readByte() != STATE_CACHE_VERSION)
			return false;
		byte tag = readTag(in);
		if (tag != OBJECT)
			return false;
		int index = in.readInt();
		long timestampRead = in.readLong();
		if (expectedTimestamp >= 0 && timestampRead != expectedTimestamp)
			return false;
		addToObjectTable(state, index);
		numBundles = in.readInt();
		if (numBundles == 0)
			return true;
		for (int i = 0; i < numBundles; i++) {
			BundleDescriptionImpl bundle = readBundleDescription(in);
			state.basicAddBundle(bundle);
			if (bundle.isResolved())
				state.addResolvedBundle(bundle);
		}
		state.setTimeStamp(timestampRead);
		state.setResolved(in.readBoolean());
		lazyDataOffset = in.readInt();
		if (lazyLoad)
			return true;
		for (int i = 0; i < numBundles; i++)
			readBundleDescriptionLazyData(in, null);
		return true;
	}

	private BundleDescriptionImpl readBundleDescription(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		if (tag == INDEX)
			return (BundleDescriptionImpl) getFromObjectTable(in.readInt());
		// first read in non-lazy loaded data
		BundleDescriptionImpl result = new BundleDescriptionImpl();
		addToObjectTable(result, in.readInt());

		result.setBundleId(in.readLong());
		readBaseDescription(result, in);
		result.setResolved(in.readBoolean());
		result.setSingleton(in.readBoolean());
		result.setHost(readHostSpec(in));

		// set the bundle dependencies from imports and requires.
		int numDeps = in.readInt();
		if (numDeps > 0) {
			BundleDescription[] deps = new BundleDescription[numDeps];
			for (int i = 0; i < numDeps; i++)
				deps[i] = readBundleDescription(in);
			result.addDependencies(deps);
		}
		// set the dependencies between fragment and hosts.
		HostSpecificationImpl hostSpec = (HostSpecificationImpl) result.getHost();
		if (hostSpec != null) {
			BundleDescription[] hosts = hostSpec.getHosts();
			if (hosts != null) {
				for (int i = 0; i < hosts.length; i++)
					((BundleDescriptionImpl)hosts[i]).addDependency(result);
				result.addDependencies(hosts);
			}
		}
		// the rest is lazy loaded data
		result.setFullyLoaded(false);
		return result;
	}

	private BundleDescriptionImpl readBundleDescriptionLazyData(DataInputStream in, List toLoad) throws IOException {
		int index = in.readInt();
		BundleDescriptionImpl result = (BundleDescriptionImpl) getFromObjectTable(index);
		boolean shouldLoad = toLoad == null ? true : toLoad.remove(result); // always try to remove here
		if (result.isFullyLoaded()) {
			in.skipBytes(result.getLazyDataSize());
			in.readInt();
			return result;
		}
		if (!shouldLoad) {
			skipBundleDescriptionLazyData(result, in);
			return result;
		}

		result.setLocation(readString(in, false));

		int exportCount = in.readInt();
		if (exportCount > 0) {
			ExportPackageDescription[] exports = new ExportPackageDescription[exportCount];
			for (int i = 0; i < exports.length; i++)
				exports[i] = readExportPackageDesc(in);
			result.setExportPackages(exports);
		}

		int importCount = in.readInt();
		if (importCount > 0) {
			ImportPackageSpecification[] imports = new ImportPackageSpecification[importCount];
			for (int i = 0; i < imports.length; i++)
				imports[i] = readImportPackageSpec(in);
			result.setImportPackages(imports);
		}

		int requiredBundleCount = in.readInt();
		if (requiredBundleCount > 0) {
			BundleSpecification[] requiredBundles = new BundleSpecification[requiredBundleCount];
			for (int i = 0; i < requiredBundles.length; i++)
				requiredBundles[i] = readBundleSpec(in);
			result.setRequiredBundles(requiredBundles);
		}

		int selectedCount = in.readInt();
		if (selectedCount > 0) {
			ExportPackageDescription[] selected = new ExportPackageDescription[selectedCount];
			for (int i = 0; i < selected.length; i++)
				selected[i] = readExportPackageDesc(in);
			result.setSelectedExports(selected);
		}

		int resolvedCount = in.readInt();
		if (resolvedCount > 0) {
			ExportPackageDescription[] resolved = new ExportPackageDescription[resolvedCount];
			for (int i = 0; i < resolved.length; i++)
				resolved[i] = readExportPackageDesc(in);
			result.setResolvedImports(resolved);
		}

		int resolvedRequiredCount = in.readInt();
		if (resolvedRequiredCount > 0) {
			BundleDescription[] resolved = new BundleDescription[resolvedRequiredCount];
			for (int i = 0; i < resolved.length; i++)
				resolved[i] = readBundleDescription(in);
			result.setResolvedRequires(resolved);
		}

		// read the size of the lazy data
		result.setLazyDataSize(in.readInt());

		result.setFullyLoaded(true); // set fully loaded before setting the dependencies

		// No need to add bundle dependencies for hosts, imports or requires;
		// This is done by readBundleDescription
		return result;
	}

	private void skipBundleDescriptionLazyData(BundleDescriptionImpl result, DataInputStream in) throws IOException {
		if (result.getLazyDataSize() > 0) {
			in.skipBytes(result.getLazyDataSize());
			in.readInt();
			return;
		}

		skipString(in); // location

		int exportCount = in.readInt();
		if (exportCount > 0)
			for (int i = 0; i < exportCount; i++)
				skipExportPackageDesc(in);

		int importCount = in.readInt();
		if (importCount > 0)
			for (int i = 0; i < importCount; i++)
				skipImportPackageSpec(in);

		int requiredBundleCount = in.readInt();
		if (requiredBundleCount > 0)
			for (int i = 0; i < requiredBundleCount; i++)
				skipBundleSpec(in);

		int selectedCount = in.readInt();
		if (selectedCount > 0)
			for (int i = 0; i < selectedCount; i++)
				skipExportPackageDesc(in);

		int resolvedCount = in.readInt();
		if (resolvedCount > 0)
			for (int i = 0; i < resolvedCount; i++)
				skipExportPackageDesc(in);

		int resolvedRequiredCount = in.readInt();
		if (resolvedRequiredCount > 0)
			for (int i = 0; i < resolvedRequiredCount; i++)
				readBundleDescription(in); // no need to skip here since it is a cached object

		// read the size of the lazy data
		result.setLazyDataSize(in.readInt());
	}

	private BundleSpecificationImpl readBundleSpec(DataInputStream in) throws IOException {
		BundleSpecificationImpl result = new BundleSpecificationImpl();
		readVersionConstraint(result, in);
		result.setSupplier(readBundleDescription(in));
		result.setExported(in.readBoolean());
		result.setOptional(in.readBoolean());
		return result;
	}

	private void skipBundleSpec(DataInputStream in) throws IOException {
		skipVersionConstraint(in);
		readBundleDescription(in); // no need to skip here since it is a cached object
		in.readBoolean(); // exported
		in.readBoolean(); // optional
	}

	private ExportPackageDescriptionImpl readExportPackageDesc(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		if (tag == INDEX)
			return (ExportPackageDescriptionImpl) getFromObjectTable(in.readInt());
		ExportPackageDescriptionImpl exportPackageDesc = new ExportPackageDescriptionImpl();
		addToObjectTable(exportPackageDesc, in.readInt());
		readBaseDescription(exportPackageDesc, in);
		exportPackageDesc.setGrouping(readString(in, false));
		exportPackageDesc.setInclude(readString(in, false));
		exportPackageDesc.setExclude(readString(in, false));
		exportPackageDesc.setRoot(in.readBoolean());

		int attrCount = in.readInt();
		if (attrCount > 0) {
			HashMap attributes = new HashMap(attrCount);
			for (int i = 0; i < attrCount; i++)
				attributes.put(readString(in, false), readString(in, false));
			exportPackageDesc.setAttributes(attributes);
		}

		int mandatoryCount = in.readInt();
		if (mandatoryCount > 0) {
			String[] mandatory = new String[mandatoryCount];
			for (int i = 0; i < mandatoryCount; i++)
				mandatory[i] = readString(in, false);
			exportPackageDesc.setMandatory(mandatory);
		}

		return exportPackageDesc;
	}

	private void skipExportPackageDesc(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return;
		if (tag == INDEX) {
			in.readInt(); // skip the index
			return;
		}
		in.readInt(); // skip the index
		skipBaseDescription(in);
		skipString(in); // grouping
		skipString(in); // includes
		skipString(in); // excludes
		in.readBoolean(); // root

		int attrCount = in.readInt();
		if (attrCount > 0)
			for (int i = 0; i < attrCount; i++) {
				skipString(in); // key
				skipString(in); // value
			}
		int mandatoryCount = in.readInt();
		if (mandatoryCount > 0)
			for (int i = 0; i < mandatoryCount; i++)
				skipString(in);
	}

	private void readBaseDescription(BaseDescriptionImpl root, DataInputStream in) throws IOException {
		root.setName(readString(in, false));
		root.setVersion(readVersion(in));
	}

	private void skipBaseDescription(DataInputStream in) throws IOException {
		skipString(in);
		skipVersion(in);
	}

	private ImportPackageSpecificationImpl readImportPackageSpec(DataInputStream in) throws IOException {
		ImportPackageSpecificationImpl result = new ImportPackageSpecificationImpl();
		readVersionConstraint(result, in);
		result.setSupplier(readExportPackageDesc(in));
		result.setBundleSymbolicName(readString(in, false));
		result.setBundleVersionRange(readVersionRange(in));
		result.setResolution(in.readInt());

		int propagateCount = in.readInt();
		if (propagateCount > 0) {
			String[] propagate = new String[propagateCount];
			for (int i = 0; i < propagateCount; i++)
				propagate[i] = readString(in, false);
			result.setPropagate(propagate);
		}

		int attrCount = in.readInt();
		if (attrCount > 0) {
			HashMap attributes = new HashMap(attrCount);
			for (int i = 0; i < attrCount; i++)
				attributes.put(readString(in, false), readString(in, false));
			result.setAttributes(attributes);
		}
		return result;
	}

	private void skipImportPackageSpec(DataInputStream in) throws IOException {
		skipVersionConstraint(in);
		skipExportPackageDesc(in);
		skipString(in); // BSN
		skipVersionRange(in); // bundle-version
		in.readInt(); // resolution
		int propagateCount = in.readInt();
		if (propagateCount > 0)
			for (int i = 0; i < propagateCount; i++)
				skipString(in);
		int attrCount = in.readInt();
		if (attrCount > 0)
			for (int i = 0; i < attrCount; i++) {
				skipString(in); // key
				skipString(in); // value
			}
	}

	private HostSpecificationImpl readHostSpec(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		HostSpecificationImpl result = new HostSpecificationImpl();
		readVersionConstraint(result, in);
		int hostCount = in.readInt();
		if (hostCount > 0) {
			BundleDescription[] hosts = new BundleDescription[hostCount];
			for (int i = 0; i < hosts.length; i++)
				hosts[i] = readBundleDescription(in);
			result.setHosts(hosts);
		}
		return result;
	}

	// called by readers for VersionConstraintImpl subclasses
	private void readVersionConstraint(VersionConstraintImpl version, DataInputStream in) throws IOException {
		version.setName(readString(in, false));
		version.setVersionRange(readVersionRange(in));
	}

	private void skipVersionConstraint(DataInputStream in) throws IOException {
		skipString(in);
		skipVersionRange(in);
	}

	private Version readVersion(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return Version.emptyVersion;
		int majorComponent = in.readInt();
		int minorComponent = in.readInt();
		int serviceComponent = in.readInt();
		String qualifierComponent = readString(in, false);
		Version result = new Version(majorComponent, minorComponent, serviceComponent, qualifierComponent);
		return result;
	}

	private void skipVersion(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return;
		in.readInt();
		in.readInt();
		in.readInt();
		skipString(in);
	}

	private VersionRange readVersionRange(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		return new VersionRange(readVersion(in), in.readBoolean(), readVersion(in), in.readBoolean());
	}

	private void skipVersionRange(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return;
		skipVersion(in);
		in.readBoolean();
		skipVersion(in);
		in.readBoolean();
	}

	/**
	 * expectedTimestamp is the expected value for the timestamp. or -1, if
	 * 	no checking should be performed 
	 */
	public final boolean loadState(StateImpl state, DataInputStream input, long expectedTimestamp) throws IOException {
		try {
			return readState(state, input, expectedTimestamp);
		} finally {
			input.close();
		}
	}
	/**
	 * expectedTimestamp is the expected value for the timestamp. or -1, if
	 * 	no checking should be performed 
	 */
	public final boolean loadState(StateImpl state, long expectedTimestamp) throws IOException {
		return loadState(state, openCacheFile(), expectedTimestamp);
	}

	private WeakHashMap stringCache = new WeakHashMap();
	private String readString(DataInputStream in, boolean intern) throws IOException {
		byte type = in.readByte();
		if (type == NULL)
			return null;
		String result;
		if (intern)
			result = in.readUTF().intern();
		else
			result = in.readUTF();
		WeakReference ref = (WeakReference) stringCache.get(result);
		if (ref != null) {
			String refString = (String) ref.get();
			if (refString != null)
				result = refString;
		}
		else
			stringCache.put(result, new WeakReference(result));
		return result;
	}

	private void skipString(DataInputStream in) throws IOException {
		byte type = in.readByte();
		if (type == NULL)
			return;
		int utfLength = in.readUnsignedShort();
		byte bytearr[] = new byte[utfLength];
		in.readFully(bytearr, 0, utfLength);
	}

	private byte readTag(DataInputStream in) throws IOException {
		return in.readByte();
	}

	private DataInputStream openCacheFile() throws IOException {
		if (cacheFile == null)
			throw new IOException(); // TODO error message here!
		return new DataInputStream(new BufferedInputStream(new FileInputStream(cacheFile), 65536));
	}

	boolean isLazyLoaded() {
		return lazyLoad;
	}

	synchronized void fullyLoad() {
		DataInputStream in = null;
		try {
			in = openCacheFile();
			in.skipBytes(lazyDataOffset);
			in.readInt(); // skip the offset itself
			for (int i = 0; i < numBundles; i++)
				readBundleDescriptionLazyData(in, null);
		}
		catch (IOException ioe) {
			throw new RuntimeException(); // TODO need error message here
		}
		finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// nothing we can do now
				}
		}
	}

	synchronized void fullyLoad(BundleDescriptionImpl target) throws IOException {
		DataInputStream in = null;
		try {
			in = openCacheFile();
			// skip to the lazy data
			in.skipBytes(lazyDataOffset);
			in.readInt(); // skip the offset itself
			// get the set of bundles that must be loaded according to dependencies
			ArrayList toLoad = new ArrayList();
			addDependencies(target, toLoad);
			// look for the lazy data of the toLoad list
			for (int i = 0; i < numBundles; i++) {
				readBundleDescriptionLazyData(in, toLoad);
				// when the toLoad list is empty we are done
				if (toLoad.size() == 0)
					break;			
			}
		}
		finally {
			if (in != null)
				in.close();
		}
	}

	private void addDependencies(BundleDescriptionImpl target,  List toLoad) {
		if (toLoad.contains(target))
			return;
		toLoad.add(target);
		List deps = target.getBundleDependencies();
		for (Iterator iter = deps.iterator(); iter.hasNext();)
			addDependencies((BundleDescriptionImpl) iter.next(), toLoad);
	}
}