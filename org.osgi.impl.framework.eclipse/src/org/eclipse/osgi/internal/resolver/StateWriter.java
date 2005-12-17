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
import java.util.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Version;

class StateWriter {

	// objectTable will be a hashmap of objects. The objects will be things
	// like BundleDescription, ExportPackageDescription, Version etc.. The integer
	// index value will be used in the cache to allow cross-references in the
	// cached state.
	private Map objectTable = new HashMap();
	private ArrayList forcedWrite = new ArrayList();

	private int addToObjectTable(Object object) {
		Integer cur = (Integer) objectTable.get(object);
		if (cur != null)
			return cur.intValue();
		objectTable.put(object, new Integer(objectTable.size()));
		// return the index of the object just added (i.e. size - 1)
		return (objectTable.size() - 1);
	}

	private int getFromObjectTable(Object object) {
		if (objectTable != null) {
			Object objectResult = objectTable.get(object);
			if (objectResult != null) {
				return ((Integer) objectResult).intValue();
			}
		}
		return -1;
	}

	private boolean writePrefix(Object object, DataOutputStream out) throws IOException {
		if (writeIndex(object, out))
			return true;
		// add this object to the object table first
		int index = addToObjectTable(object);
		out.writeByte(StateReader.OBJECT);
		out.writeInt(index);
		return false;
	}

	private void writeStateDeprecated(StateImpl state, DataOutputStream out) throws IOException {
		// first clear the System exports because we don't want to persist them in the system
		// bundles bundle description data
		state.setSystemExports(null);
		out.write(StateReader.STATE_CACHE_VERSION);
		if (writePrefix(state, out))
			return;
		out.writeLong(state.getTimeStamp());
		Dictionary[] propSet = state.getPlatformProperties();
		out.writeInt(propSet.length);
		for (int i = 0; i < propSet.length; i++){
			Dictionary props = propSet[i];
			out.writeInt(StateImpl.PROPS.length);
			for (int j = 0; j < StateImpl.PROPS.length; j++)
				writePlatformProp(props.get(StateImpl.PROPS[j]), out);
		}
		BundleDescription[] bundles = state.getBundles();
		StateHelperImpl.getInstance().sortBundles(bundles);
		out.writeInt(bundles.length);
		if (bundles.length == 0)
			return;
		for (int i = 0; i < bundles.length; i++)
			writeBundleDescription(bundles[i], out, false);
		out.writeBoolean(state.isResolved());
		// save the lazy data offset
		out.writeInt(out.size());
		for (int i = 0; i < bundles.length; i++)
			writeBundleDescriptionLazyData(bundles[i], out);
	}

	public void saveState(StateImpl state, File stateFile, File lazyFile) throws IOException {
		DataOutputStream outLazy = null;
		DataOutputStream outState = null;
		FileOutputStream fosLazy = null;
		FileOutputStream fosState = null;
		try {
			// first clear the System exports because we don't want to persist them in the system
			// bundles bundle description data
			state.setSystemExports(null);
			BundleDescription[] bundles = state.getBundles();
			StateHelperImpl.getInstance().sortBundles(bundles);
			// need to prime the object table with all bundles
			// this allows us to write only indexes to bundles in the lazy data
			for (int i = 0; i < bundles.length; i++)
				addToObjectTable(bundles[i]);
			// first write the lazy data to get the offsets and sizes to the lazy data
			fosLazy = new FileOutputStream(lazyFile);
			outLazy = new DataOutputStream(fosLazy);
			for (int i = 0; i < bundles.length; i++)
				writeBundleDescriptionLazyData(bundles[i], outLazy);
			// now write the state data
			fosState = new FileOutputStream(stateFile);
			outState = new DataOutputStream(fosState);
			outState.write(StateReader.STATE_CACHE_VERSION);
			if (writePrefix(state, outState))
				return;
			outState.writeLong(state.getTimeStamp());
			Dictionary[] propSet = state.getPlatformProperties();
			outState.writeInt(propSet.length);
			for (int i = 0; i < propSet.length; i++){
				Dictionary props = propSet[i];
				outState.writeInt(StateImpl.PROPS.length);
				for (int j = 0; j < StateImpl.PROPS.length; j++)
					writePlatformProp(props.get(StateImpl.PROPS[j]), outState);
			}
			outState.writeInt(bundles.length);
			if (bundles.length == 0)
				return;
			for (int i = 0; i < bundles.length; i++)
				// write out each bundle with the force flag set to make sure
				// the data is written at least once in the non-lazy state data
				writeBundleDescription(bundles[i], outState, true);
			outState.writeBoolean(state.isResolved());
			state.setDynamicCacheChanged(false);
		} finally {
			if (outLazy != null)
				try {
					outLazy.flush();
					fosLazy.getFD().sync();
				} catch (IOException e) {
					// do nothing, we tried
				}
				try {
					outLazy.close();
				} catch (IOException e) {
					// do nothing
				}
			if (outState != null)
				try {
					outState.flush();
					fosState.getFD().sync();
				} catch (IOException e) {
					// do nothing, we tried
				}
				try {
					outState.close();
				} catch (IOException e) {
					// do nothing
				}
		}
	}

	private void writePlatformProp(Object obj, DataOutputStream out) throws IOException {
		if (obj == null)
			out.writeByte(StateReader.NULL);
		else {
			out.writeByte(StateReader.OBJECT);
			if (obj instanceof String) {
				out.writeInt(1);
				writeStringOrNull((String) obj, out);
			} else {
				String[] props = (String[]) obj;
				out.writeInt(props.length);
				for (int i = 0; i < props.length; i++)
					writeStringOrNull(props[i], out);
			}
		}
	}

	/*
	 * The force flag is used when writing the non-lazy state data.  This forces the data to be
	 * written once even if the object exists in the object table.
	 * This is needed because we want to write the lazy data first but we only want
	 * to include indexes to the actual bundles in the lazy data.  To do this we
	 * prime the object table with all the bundles first.  Then we write the
	 * lazy data.  Finally we write the non-lazy data and force a write of the
	 * bundles data once even if the bundle is in the object table.
	 */
	private void writeBundleDescription(BundleDescription bundle, DataOutputStream out, boolean force) throws IOException {
		if (force && !forcedWrite.contains(bundle)) {
			int index = addToObjectTable(bundle);
			out.writeByte(StateReader.OBJECT);
			out.writeInt(index);
			forcedWrite.add(bundle);
		} else if (writePrefix(bundle, out))
			return;
		// first write out non-lazy loaded data
		out.writeLong(bundle.getBundleId()); // ID must be the first thing
		writeBaseDescription(bundle, out);
		out.writeInt(((BundleDescriptionImpl) bundle).getLazyDataOffset());
		out.writeInt(((BundleDescriptionImpl) bundle).getLazyDataSize());
		out.writeBoolean(bundle.isResolved());
		out.writeBoolean(bundle.isSingleton());
		out.writeBoolean(bundle.hasDynamicImports());
		out.writeBoolean(bundle.attachFragments());
		out.writeBoolean(bundle.dynamicFragments());
		writeHostSpec((HostSpecificationImpl) bundle.getHost(), out, force);

		List dependencies = ((BundleDescriptionImpl) bundle).getBundleDependencies();
		out.writeInt(dependencies.size());
		for (Iterator iter = dependencies.iterator(); iter.hasNext();)
			writeBundleDescription((BundleDescription) iter.next(), out, force);
		// the rest is lazy loaded data
	}

	private void writeBundleDescriptionLazyData(BundleDescription bundle, DataOutputStream out) throws IOException {
		int dataStart = out.size(); // save the offset of lazy data start
		int index = getFromObjectTable(bundle);
		((BundleDescriptionImpl) bundle).setLazyDataOffset(out.size());
		out.writeInt(index);

		writeStringOrNull(bundle.getLocation(), out);
		writeStringOrNull(bundle.getPlatformFilter(), out);

		ExportPackageDescription[] exports = bundle.getExportPackages();
		out.writeInt(exports.length);
		for (int i = 0; i < exports.length; i++)
			writeExportPackageDesc((ExportPackageDescriptionImpl) exports[i], out);

		ImportPackageSpecification[] imports = bundle.getImportPackages();
		out.writeInt(imports.length);
		for (int i = 0; i < imports.length; i++)
			writeImportPackageSpec(imports[i], out);

		BundleSpecification[] requiredBundles = bundle.getRequiredBundles();
		out.writeInt(requiredBundles.length);
		for (int i = 0; i < requiredBundles.length; i++)
			writeBundleSpec((BundleSpecificationImpl) requiredBundles[i], out);

		ExportPackageDescription[] selectedExports = bundle.getSelectedExports();
		if (selectedExports == null) {
			out.writeInt(0);
		} else {
			out.writeInt(selectedExports.length);
			for (int i = 0; i < selectedExports.length; i++)
				writeExportPackageDesc((ExportPackageDescriptionImpl) selectedExports[i], out);
		}

		ExportPackageDescription[] resolvedImports = bundle.getResolvedImports();
		if (resolvedImports == null) {
			out.writeInt(0);
		} else {
			out.writeInt(resolvedImports.length);
			for (int i = 0; i < resolvedImports.length; i++)
				writeExportPackageDesc((ExportPackageDescriptionImpl) resolvedImports[i], out);
		}

		BundleDescription[] resolvedRequires = bundle.getResolvedRequires();
		if (resolvedRequires == null) {
			out.writeInt(0);
		} else {
			out.writeInt(resolvedRequires.length);
			for (int i = 0; i < resolvedRequires.length; i++)
				writeBundleDescription(resolvedRequires[i], out, false);
		}

		HashMap dynamicStamps = ((BundleDescriptionImpl)bundle).getDynamicStamps();
		if (dynamicStamps == null)
			out.writeInt(0);
		else {
			out.writeInt(dynamicStamps.size());
			for (Iterator pkgs = dynamicStamps.keySet().iterator(); pkgs.hasNext();) {
				String pkg = (String) pkgs.next();
				writeStringOrNull(pkg, out);
				out.writeLong(((Long)dynamicStamps.get(pkg)).longValue());
			}
		}


		// save the size of the lazy data
		((BundleDescriptionImpl) bundle).setLazyDataSize(out.size() - dataStart);
	}

	private void writeBundleSpec(BundleSpecificationImpl bundle, DataOutputStream out) throws IOException {
		writeVersionConstraint(bundle, out);
		writeBundleDescription((BundleDescription) bundle.getSupplier(), out, false);
		out.writeBoolean(bundle.isExported());
		out.writeBoolean(bundle.isOptional());
	}

	private void writeExportPackageDesc(ExportPackageDescriptionImpl exportPackageDesc, DataOutputStream out) throws IOException {
		if (writePrefix(exportPackageDesc, out))
			return;
		writeBaseDescription(exportPackageDesc, out);
		out.writeBoolean(exportPackageDesc.isRoot());
		writeMap(out, exportPackageDesc.getAttributes());
		writeMap(out, exportPackageDesc.getDirectives());
	}

	private void writeMap(DataOutputStream out, Map source) throws IOException {
		if (source == null) {
			out.writeInt(0);
		} else {
			out.writeInt(source.size());
			Iterator iter = source.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				Object value = source.get(key);
				writeStringOrNull(key, out);
				if (value instanceof String) {
					out.writeByte(0);
					writeStringOrNull((String) value, out);
				} else if (value instanceof String[]) {
					out.writeByte(1);
					writeList(out, (String[]) value);
				} else if (value instanceof Boolean) {
					out.writeByte(2);
					out.writeBoolean(((Boolean) value).booleanValue());
				}
			}
		}
	}

	private void writeList(DataOutputStream out, String[] list) throws IOException {
		if (list == null) {
			out.writeInt(0);
		} else {
			out.writeInt(list.length);
			for (int i = 0; i < list.length; i++)
				writeStringOrNull(list[i], out);
		}
	}

	private void writeBaseDescription(BaseDescription rootDesc, DataOutputStream out) throws IOException {
		writeStringOrNull(rootDesc.getName(), out);
		writeVersion(rootDesc.getVersion(), out);
	}

	private void writeImportPackageSpec(ImportPackageSpecification importPackageSpec, DataOutputStream out) throws IOException {
		writeVersionConstraint(importPackageSpec, out);
		// TODO this is a hack until the state dynamic loading is cleaned up
		// we should only write the supplier if we are resolved
		if (importPackageSpec.getBundle().isResolved())
			writeExportPackageDesc((ExportPackageDescriptionImpl) importPackageSpec.getSupplier(), out);
		else
			out.writeByte(StateReader.NULL);

		writeStringOrNull(importPackageSpec.getBundleSymbolicName(), out);
		writeVersionRange(importPackageSpec.getBundleVersionRange(), out);
		writeMap(out, importPackageSpec.getAttributes());
		writeMap(out, importPackageSpec.getDirectives());
	}

	private void writeHostSpec(HostSpecificationImpl host, DataOutputStream out, boolean force) throws IOException {
		if (host == null) {
			out.writeByte(StateReader.NULL);
			return;
		}
		out.writeByte(StateReader.OBJECT);
		writeVersionConstraint(host, out);
		BundleDescription[] hosts = host.getHosts();
		if (hosts == null) {
			out.writeInt(0);
			return;
		}
		out.writeInt(hosts.length);
		for (int i = 0; i < hosts.length; i++)
			writeBundleDescription(hosts[i], out, force);
	}

	// called by writers for VersionConstraintImpl subclasses
	private void writeVersionConstraint(VersionConstraint constraint, DataOutputStream out) throws IOException {
		writeStringOrNull(constraint.getName(), out);
		writeVersionRange(constraint.getVersionRange(), out);
	}

	private void writeVersion(Version version, DataOutputStream out) throws IOException {
		if (version == null || version.equals(Version.emptyVersion)) {
			out.writeByte(StateReader.NULL);
			return;
		}
		out.writeByte(StateReader.OBJECT);
		out.writeInt(version.getMajor());
		out.writeInt(version.getMinor());
		out.writeInt(version.getMicro());
		writeQualifier(version.getQualifier(), out);
	}

	private void writeVersionRange(VersionRange versionRange, DataOutputStream out) throws IOException {
		if (versionRange == null || versionRange.equals(VersionRange.emptyRange)) {
			out.writeByte(StateReader.NULL);
			return;
		}
		out.writeByte(StateReader.OBJECT);
		writeVersion(versionRange.getMinimum(), out);
		out.writeBoolean(versionRange.getIncludeMinimum());
		writeVersion(versionRange.getMaximum(), out);
		out.writeBoolean(versionRange.getIncludeMaximum());
	}

	private boolean writeIndex(Object object, DataOutputStream out) throws IOException {
		if (object == null) {
			out.writeByte(StateReader.NULL);
			return true;
		}
		int index = getFromObjectTable(object);
		if (index == -1)
			return false;
		out.writeByte(StateReader.INDEX);
		out.writeInt(index);
		return true;
	}

	public void saveStateDeprecated(StateImpl state, DataOutputStream output) throws IOException {
		try {
			writeStateDeprecated(state, output);
			state.setDynamicCacheChanged(false);
		} finally {
			output.close();
		}
	}

	private void writeStringOrNull(String string, DataOutputStream out) throws IOException {
		if (string == null)
			out.writeByte(StateReader.NULL);
		else {
			out.writeByte(StateReader.OBJECT);
			out.writeUTF(string);
		}
	}

	private void writeQualifier(String string, DataOutputStream out) throws IOException {
		if (string != null && string.length() == 0)
			string = null;
		writeStringOrNull(string, out);
	}
}
