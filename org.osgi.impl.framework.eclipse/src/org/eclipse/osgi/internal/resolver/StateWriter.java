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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Version;

class StateWriter {

	// objectTable will be a hashmap of objects. The objects will be things
	// like BundleDescription, ExportPackageDescription, Version etc.. The integer
	// index value will be used in the cache to allow cross-references in the
	// cached state.
	protected Map objectTable = new HashMap();

	public static final byte NULL = 0;
	public static final byte OBJECT = 1;
	public static final byte INDEX = 2;

	private int addToObjectTable(Object object) {
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
		out.writeByte(OBJECT);
		out.writeInt(index);
		return false;
	}

	private void writeState(StateImpl state, DataOutputStream out) throws IOException {
		out.write(StateReader.STATE_CACHE_VERSION);
		if (writePrefix(state, out))
			return;
		out.writeLong(state.getTimeStamp());
		BundleDescription[] bundles = state.getBundles();
		StateHelperImpl.getInstance().sortBundles(bundles);
		out.writeInt(bundles.length);
		if (bundles.length == 0)
			return;
		for (int i = 0; i < bundles.length; i++)
			writeBundleDescription(bundles[i], out);
		out.writeBoolean(state.isResolved());
		// save the lazy data offset
		out.writeInt(out.size());
		for (int i = 0; i < bundles.length; i++)
			writeBundleDescriptionLazyData(bundles[i], out);
	}

	private void writeBundleDescription(BundleDescription bundle, DataOutputStream out) throws IOException {
		if (writePrefix(bundle, out))
			return;
		// first write out non-lazy loaded data
		out.writeLong(bundle.getBundleId()); // ID must be the first thing
		writeBaseDescription(bundle, out);
		out.writeBoolean(bundle.isResolved());
		out.writeBoolean(bundle.isSingleton());
		writeHostSpec((HostSpecificationImpl) bundle.getHost(), out);

		List dependencies = ((BundleDescriptionImpl)bundle).getBundleDependencies();
		out.writeInt(dependencies.size());
		for (Iterator iter = dependencies.iterator(); iter.hasNext();)
			writeBundleDescription((BundleDescription) iter.next(), out);
		// the rest is lazy loaded data
	}

	private void writeBundleDescriptionLazyData(BundleDescription bundle, DataOutputStream out) throws IOException {
		int index = getFromObjectTable(bundle);
		out.writeInt(index);

		int dataStart = out.size(); // save the offset of lazy data start

		writeStringOrNull(bundle.getLocation(), out);

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
		}
		else {
			out.writeInt(selectedExports.length);
			for (int i = 0; i < selectedExports.length; i++)
				writeExportPackageDesc((ExportPackageDescriptionImpl) selectedExports[i], out);
		}

		ExportPackageDescription[] resolvedImports = bundle.getResolvedImports();
		if (resolvedImports == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(resolvedImports.length);
			for (int i = 0; i < resolvedImports.length; i++)
				writeExportPackageDesc((ExportPackageDescriptionImpl) resolvedImports[i], out);
		}

		BundleDescription[] resolvedRequires = bundle.getResolvedRequires();
		if (resolvedRequires == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(resolvedRequires.length);
			for (int i = 0; i < resolvedRequires.length; i++)
				writeBundleDescription(resolvedRequires[i], out);
		}
		
		// write the size of the lazy data
		out.writeInt(out.size() - dataStart);
	}

	private void writeBundleSpec(BundleSpecificationImpl bundle, DataOutputStream out) throws IOException {
		writeVersionConstraint(bundle, out);
		writeBundleDescription((BundleDescription) bundle.getSupplier(), out);
		out.writeBoolean(bundle.isExported());
		out.writeBoolean(bundle.isOptional());
	}

	private void writeExportPackageDesc(ExportPackageDescriptionImpl exportPackageDesc, DataOutputStream out) throws IOException {
		if (writePrefix(exportPackageDesc, out))
			return;
		writeBaseDescription(exportPackageDesc, out);
		writeStringOrNull(exportPackageDesc.getBasicGrouping(), out);
		writeStringOrNull(exportPackageDesc.getInclude(), out);
		writeStringOrNull(exportPackageDesc.getExclude(), out);
		out.writeBoolean(exportPackageDesc.isRoot());

		Map attributes = exportPackageDesc.getAttributes();
		if (attributes == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(attributes.size());
			Iterator iter = attributes.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = (String) attributes.get(key);
				writeStringOrNull(key, out);
				writeStringOrNull(value,out);
			}
		}

		String[] mandatory = exportPackageDesc.getMandatory();
		if (mandatory == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(mandatory.length);
			for (int i = 0; i < mandatory.length; i++)
				writeStringOrNull(mandatory[i],out);
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
			out.writeByte(NULL);

		writeStringOrNull(importPackageSpec.getBundleSymbolicName(), out);
		writeVersionRange(importPackageSpec.getBundleVersionRange(), out);
		out.writeInt(importPackageSpec.getResolution());

		String[] propagate = importPackageSpec.getPropagate();
		if (propagate == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(propagate.length);
			for (int i = 0; i < propagate.length; i++)
				writeStringOrNull(propagate[i],out);
		}

		Map attributes = importPackageSpec.getAttributes();
		if (attributes == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(attributes.size());
			Iterator iter = attributes.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = (String) attributes.get(key);
				writeStringOrNull(key, out);
				writeStringOrNull(value,out);
			}
		}
	}

	private void writeHostSpec(HostSpecificationImpl host, DataOutputStream out) throws IOException {
		if (host == null) {
			out.writeByte(NULL);
			return;
		}
		out.writeByte(OBJECT);
		writeVersionConstraint(host, out);
		BundleDescription[] hosts = host.getHosts();
		if (hosts == null) {
			out.writeInt(0);
			return;
		}
		out.writeInt(hosts.length);
		for (int i = 0; i < hosts.length; i++)
			writeBundleDescription(hosts[i], out);
	}

	// called by writers for VersionConstraintImpl subclasses
	private void writeVersionConstraint(VersionConstraint constraint, DataOutputStream out) throws IOException {
		writeStringOrNull(constraint.getName(), out);
		writeVersionRange(constraint.getVersionRange(), out);
	}

	private void writeVersion(Version version, DataOutputStream out) throws IOException {
		if (version == null || version.equals(Version.emptyVersion)) {
			out.writeByte(NULL);
			return;
		}
		out.writeByte(OBJECT);
		out.writeInt(version.getMajor());
		out.writeInt(version.getMinor());
		out.writeInt(version.getMicro());
		writeQualifier(version.getQualifier(), out);
	}

	private void writeVersionRange(VersionRange versionRange, DataOutputStream out) throws IOException {
		if (versionRange == null|| versionRange.equals(VersionRange.emptyRange)) {
			out.writeByte(NULL);
			return;
		}
		out.writeByte(OBJECT);
		writeVersion(versionRange.getMinimum(), out);
		out.writeBoolean(versionRange.getIncludeMinimum());
		writeVersion(versionRange.getMaximum(), out);
		out.writeBoolean(versionRange.getIncludeMaximum());
	}

	private boolean writeIndex(Object object, DataOutputStream out) throws IOException {
		if (object == null) {
			out.writeByte(NULL);
			return true;
		}
		int index = getFromObjectTable(object);
		if (index == -1)
			return false;
		out.writeByte(INDEX);
		out.writeInt(index);
		return true;
	}

	public void saveState(StateImpl state, DataOutputStream output) throws IOException {
		try {
			writeState(state, output);
		} finally {
			output.close();
		}
	}

	private void writeStringOrNull(String string, DataOutputStream out) throws IOException {
		if (string == null)
			out.writeByte(NULL);
		else {
			out.writeByte(OBJECT);
			out.writeUTF(string);
		}
	}

	private void writeQualifier(String string, DataOutputStream out) throws IOException {
		if (string != null && string.length() == 0)
			string = null;
		writeStringOrNull(string, out);
	}
}