/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import org.eclipse.osgi.framework.util.SecureAction;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Version;

class StateReader {
	public static final String STATE_FILE = ".state"; //$NON-NLS-1$
	public static final String LAZY_FILE = ".lazy"; //$NON-NLS-1$
	private static final SecureAction secureAction = new SecureAction();

	// objectTable will be a hashmap of objects. The objects will be things
	// like BundleDescription, ExportPackageDescription, Version etc.. The integer
	// index value will be used in the cache to allow cross-references in the
	// cached state.
	protected Map objectTable = new HashMap();

	private File stateFile;
	private File lazyFile;

	private boolean lazyLoad = true;
	private int numBundles;

	public static final byte STATE_CACHE_VERSION = 21;
	public static final byte NULL = 0;
	public static final byte OBJECT = 1;
	public static final byte INDEX = 2;

	public StateReader() //TODO - deprecated
	{
		lazyLoad = false;
	}

	public StateReader(File stateDirectory) {
		if (!stateDirectory.exists())
			stateDirectory.mkdirs();
		this.stateFile = new File(stateDirectory, STATE_FILE);
		this.lazyFile = new File(stateDirectory, LAZY_FILE);
		this.lazyLoad = false;
	}

	public StateReader(File stateFile, File lazyFile, boolean lazyLoad) {
		this.stateFile = stateFile;
		this.lazyFile = lazyFile;
		this.lazyLoad = lazyLoad;
	}

	private void addToObjectTable(Object object, int index) {
		objectTable.put(new Integer(index), object);
	}

	private Object getFromObjectTable(int index) {
		return objectTable.get(new Integer(index));
	}

	private boolean readState(StateImpl state, long expectedTimestamp) throws IOException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(secureAction.getFileInputStream(stateFile), 65536));
		DataInputStream lazyIn = null;
		try {
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
			int numSets = in.readInt();
			Dictionary[] platformProps = new Dictionary[numSets];
			for (int i = 0; i < numSets; i++) {
				Hashtable props = new Hashtable(StateImpl.PROPS.length);
				int numProps = in.readInt();
				for (int j = 0; j < numProps; j++) {
					Object value = readPlatformProp(in);
					if (value != null && j < StateImpl.PROPS.length)
						props.put(StateImpl.PROPS[j], value);
				}
				platformProps[i] = props;
			}
			state.setPlatformProperties(platformProps);
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
			if (lazyLoad)
				return true;
			//read in from lazy data file
			lazyIn = new DataInputStream(new BufferedInputStream(secureAction.getFileInputStream(lazyFile), 65536));
			for (int i = 0; i < numBundles; i++)
				readBundleDescriptionLazyData(lazyIn, 0);
		} finally {
			in.close();
			if (lazyIn != null)
				try {
					lazyIn.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return true;
	}

	private boolean readStateDeprecated(StateImpl state, DataInputStream in, long expectedTimestamp) throws IOException {
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
		int numSets = in.readInt();
		Dictionary[] platformProps = new Dictionary[numSets];
		for (int i = 0; i < numSets; i++) {
			Hashtable props = new Hashtable(StateImpl.PROPS.length);
			int numProps = in.readInt();
			for (int j = 0; j < numProps; j++) {
				Object value = readPlatformProp(in);
				if (value != null && j < StateImpl.PROPS.length)
					props.put(StateImpl.PROPS[j], value);
			}
			platformProps[i] = props;
		}
		state.setPlatformProperties(platformProps);
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
		in.readInt(); // skip past the old offset
		if (lazyLoad)
			return true;
		for (int i = 0; i < numBundles; i++)
			readBundleDescriptionLazyData(in, 0);
		return true;
	}

	private Object readPlatformProp(DataInputStream in) throws IOException {
		byte type = in.readByte();
		if (type == NULL)
			return null;
		int num = in.readInt();
		if (num == 1)
			return readString(in, false);
		String[] result = new String[num];
		for (int i = 0; i < result.length; i++)
			result[i] = readString(in, false);
		return result;
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
		result.setLazyDataOffset(in.readInt());
		result.setLazyDataSize(in.readInt());
		result.setStateBit(BundleDescriptionImpl.RESOLVED, in.readBoolean());
		result.setStateBit(BundleDescriptionImpl.SINGLETON, in.readBoolean());
		result.setStateBit(BundleDescriptionImpl.HAS_DYNAMICIMPORT, in.readBoolean());
		result.setStateBit(BundleDescriptionImpl.ATTACH_FRAGMENTS, in.readBoolean());
		result.setStateBit(BundleDescriptionImpl.DYNAMIC_FRAGMENTS, in.readBoolean());
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
					((BundleDescriptionImpl) hosts[i]).addDependency(result);
				result.addDependencies(hosts);
			}
		}
		// the rest is lazy loaded data
		result.setFullyLoaded(false);
		return result;
	}

	private BundleDescriptionImpl readBundleDescriptionLazyData(DataInputStream in, int skip) throws IOException {
		if (skip > 0)
			in.skipBytes(skip);
		int index = in.readInt();
		BundleDescriptionImpl result = (BundleDescriptionImpl) getFromObjectTable(index);
		if (result.isFullyLoaded()) {
			in.skipBytes(result.getLazyDataSize() - 4); // skip to the end subtract 4 for the int read already
			return result;
		}

		result.setLocation(readString(in, false));
		result.setPlatformFilter(readString(in, false));

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
		result.setFullyLoaded(true); // set fully loaded before setting the dependencies
		// No need to add bundle dependencies for hosts, imports or requires;
		// This is done by readBundleDescription
		return result;
	}

	private BundleSpecificationImpl readBundleSpec(DataInputStream in) throws IOException {
		BundleSpecificationImpl result = new BundleSpecificationImpl();
		readVersionConstraint(result, in);
		result.setSupplier(readBundleDescription(in));
		result.setExported(in.readBoolean());
		result.setOptional(in.readBoolean());
		return result;
	}

	private ExportPackageDescriptionImpl readExportPackageDesc(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		if (tag == INDEX)
			return (ExportPackageDescriptionImpl) getFromObjectTable(in.readInt());
		ExportPackageDescriptionImpl exportPackageDesc = new ExportPackageDescriptionImpl();
		int tableIndex = in.readInt();
		addToObjectTable(exportPackageDesc, tableIndex);
		exportPackageDesc.setTableIndex(tableIndex);
		readBaseDescription(exportPackageDesc, in);
		exportPackageDesc.setRoot(in.readBoolean());
		exportPackageDesc.setAttributes(readMap(in));
		exportPackageDesc.setDirectives(readMap(in));
		return exportPackageDesc;
	}

	private Map readMap(DataInputStream in) throws IOException {
		int count = in.readInt();
		if (count == 0)
			return null;
		HashMap result = new HashMap(count);
		for (int i = 0; i < count; i++) {
			String key = readString(in, false);
			Object value = null;
			byte type = in.readByte();
			if (type == 0)
				 value = readString(in, false);
			else if (type == 1)
				value = readList(in);
			else if (type == 2)
				value = in.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
			result.put(key, value);
		}
		return result;
	}

	private String[] readList(DataInputStream in) throws IOException {
		int count = in.readInt();
		if (count == 0)
			return null;
		String[] result = new String[count];
		for (int i = 0; i < count; i++)
			result[i] = readString(in, false);
		return result;
	}

	private void readBaseDescription(BaseDescriptionImpl root, DataInputStream in) throws IOException {
		root.setName(readString(in, false));
		root.setVersion(readVersion(in));
	}

	private ImportPackageSpecificationImpl readImportPackageSpec(DataInputStream in) throws IOException {
		ImportPackageSpecificationImpl result = new ImportPackageSpecificationImpl();
		readVersionConstraint(result, in);
		result.setSupplier(readExportPackageDesc(in));
		result.setBundleSymbolicName(readString(in, false));
		result.setBundleVersionRange(readVersionRange(in));
		result.setAttributes(readMap(in));
		result.setDirectives(readMap(in));
		return result;
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

	private VersionRange readVersionRange(DataInputStream in) throws IOException {
		byte tag = readTag(in);
		if (tag == NULL)
			return null;
		return new VersionRange(readVersion(in), in.readBoolean(), readVersion(in), in.readBoolean());
	}

	/**
	 * expectedTimestamp is the expected value for the timestamp. or -1, if
	 * 	no checking should be performed 
	 */
	public final boolean loadStateDeprecated(StateImpl state, DataInputStream input, long expectedTimestamp) throws IOException {
		try {
			return readStateDeprecated(state, input, expectedTimestamp);
		} finally {
			input.close();
		}
	}

	/**
	 * expectedTimestamp is the expected value for the timestamp. or -1, if
	 * 	no checking should be performed 
	 */
	public final boolean loadState(StateImpl state, long expectedTimestamp) throws IOException {
		return readState(state, expectedTimestamp);
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
		} else
			stringCache.put(result, new WeakReference(result));
		return result;
	}

	private byte readTag(DataInputStream in) throws IOException {
		return in.readByte();
	}

	private DataInputStream openLazyFile() throws IOException {
		if (lazyFile == null)
			throw new IOException(); // TODO error message here!
		return new DataInputStream(new BufferedInputStream(secureAction.getFileInputStream(lazyFile), 65536));
	}

	boolean isLazyLoaded() {
		return lazyLoad;
	}

	synchronized void fullyLoad() {
		DataInputStream in = null;
		try {
			in = openLazyFile();
			for (int i = 0; i < numBundles; i++)
				readBundleDescriptionLazyData(in, 0);
		} catch (IOException ioe) {
			throw new RuntimeException(); // TODO need error message here
		} finally {
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
			in = openLazyFile();
			// get the set of bundles that must be loaded according to dependencies
			ArrayList toLoad = new ArrayList();
			addDependencies(target, toLoad);
			int skipBytes[] = getSkipBytes(toLoad);
			// look for the lazy data of the toLoad list
			for (int i = 0; i < skipBytes.length; i++)
				readBundleDescriptionLazyData(in, skipBytes[i]);
		} finally {
			if (in != null)
				in.close();
		}
	}

	private void addDependencies(BundleDescriptionImpl target, List toLoad) {
		if (toLoad.contains(target) || target.isFullyLoaded())
			return;
		Iterator load = toLoad.iterator();
		int i = 0;
		while (load.hasNext()) {
			// insert the target into the list sorted by lazy data offsets
			BundleDescriptionImpl bundle = (BundleDescriptionImpl) load.next();
			if (target.getLazyDataOffset() < bundle.getLazyDataOffset())
				break;
			i++;
		}
		if (i >= toLoad.size())
			toLoad.add(target);
		else
			toLoad.add(i, target);
		List deps = target.getBundleDependencies();
		for (Iterator iter = deps.iterator(); iter.hasNext();)
			addDependencies((BundleDescriptionImpl) iter.next(), toLoad);
	}

	private int[] getSkipBytes(ArrayList toLoad) {
		int[] skipBytes = new int[toLoad.size()];
		for (int i = 0; i < skipBytes.length; i++) {
			BundleDescriptionImpl current = (BundleDescriptionImpl) toLoad.get(i);
			if (i == 0) {
				skipBytes[i] = current.getLazyDataOffset();
				continue;
			}
			BundleDescriptionImpl previous = (BundleDescriptionImpl) toLoad.get(i - 1);
			skipBytes[i] = current.getLazyDataOffset() - previous.getLazyDataOffset() - previous.getLazyDataSize();
		}
		return skipBytes;
	}
}
