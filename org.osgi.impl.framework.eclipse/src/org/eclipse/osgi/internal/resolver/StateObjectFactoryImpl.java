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
import java.util.Dictionary;
import java.util.Map;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class StateObjectFactoryImpl implements StateObjectFactory {

	public BundleDescription createBundleDescription(Dictionary manifest, String location, long id) throws BundleException {
		BundleDescriptionImpl result = (BundleDescriptionImpl) StateBuilder.createBundleDescription(manifest, location);
		result.setBundleId(id);
		return result;
	}

	public BundleDescription createBundleDescription(long id, String symbolicName, Version version, String location, BundleSpecification[] required, HostSpecification host, ImportPackageSpecification[] imports, ExportPackageDescription[] exports, String[] providedPackages, boolean singleton) {
		BundleDescriptionImpl bundle = new BundleDescriptionImpl();
		bundle.setBundleId(id);
		bundle.setSymbolicName(symbolicName);
		bundle.setVersion(version);
		bundle.setLocation(location);
		bundle.setRequiredBundles(required);
		bundle.setHost(host);
		bundle.setImportPackages(imports);
		bundle.setExportPackages(exports);
		bundle.setSingleton(singleton);
		return bundle;
	}

	public BundleDescription createBundleDescription(BundleDescription original) {
		BundleDescriptionImpl bundle = new BundleDescriptionImpl();
		bundle.setBundleId(original.getBundleId());
		bundle.setSymbolicName(original.getSymbolicName());
		bundle.setVersion(original.getVersion());
		bundle.setLocation(original.getLocation());
		BundleSpecification[] originalRequired = original.getRequiredBundles();
		BundleSpecification[] newRequired = new BundleSpecification[originalRequired.length];
		for (int i = 0; i < newRequired.length; i++)
			newRequired[i] = createBundleSpecification(originalRequired[i]);
		bundle.setRequiredBundles(newRequired);
		ExportPackageDescription[] originalExports = original.getExportPackages();
		ExportPackageDescription[] newExports = new ExportPackageDescription[originalExports.length];
		for (int i = 0; i < newExports.length; i++)
			newExports[i] = createExportPackageDescription(originalExports[i]);
		bundle.setExportPackages(newExports);
		ImportPackageSpecification[] originalImports = original.getImportPackages();
		ImportPackageSpecification[] newImports = new ImportPackageSpecification[originalImports.length];
		for (int i = 0; i < newImports.length; i++)
			newImports[i] = createImportPackageSpecification(originalImports[i]);
		bundle.setImportPackages(newImports);
		if (original.getHost() != null)
			bundle.setHost(createHostSpecification(original.getHost()));
		bundle.setSingleton(original.isSingleton());
		return bundle;
	}

	public BundleSpecification createBundleSpecification(String requiredSymbolicName, VersionRange requiredVersionRange,boolean export, boolean optional) {
		BundleSpecificationImpl bundleSpec = new BundleSpecificationImpl();
		bundleSpec.setName(requiredSymbolicName);
		bundleSpec.setVersionRange(requiredVersionRange);
		bundleSpec.setExported(export);
		bundleSpec.setOptional(optional);
		return bundleSpec;
	}

	public BundleSpecification createBundleSpecification(BundleSpecification original) {
		BundleSpecificationImpl bundleSpec = new BundleSpecificationImpl();
		bundleSpec.setName(original.getName());
		bundleSpec.setVersionRange(original.getVersionRange());
		bundleSpec.setExported(original.isExported());
		bundleSpec.setOptional(original.isOptional());
		return bundleSpec;
	}

	public HostSpecification createHostSpecification(String hostSymbolicName, VersionRange versionRange) {
		HostSpecificationImpl hostSpec = new HostSpecificationImpl();
		hostSpec.setName(hostSymbolicName);
		hostSpec.setVersionRange(versionRange);
		return hostSpec;
	}

	public HostSpecification createHostSpecification(HostSpecification original) {
		HostSpecificationImpl hostSpec = new HostSpecificationImpl();
		hostSpec.setName(original.getName());
		hostSpec.setVersionRange(original.getVersionRange());
		return hostSpec;
	}

	public ImportPackageSpecification createImportPackageSpecification(String packageName, VersionRange versionRange, String bundleSymbolicName, VersionRange bundleVersionRange, String[] propagate, int resolution, Map attributes) {
		ImportPackageSpecificationImpl packageSpec = new ImportPackageSpecificationImpl();
		packageSpec.setName(packageName);
		packageSpec.setVersionRange(versionRange);
		packageSpec.setBundleSymbolicName(bundleSymbolicName);
		packageSpec.setBundleVersionRange(bundleVersionRange);
		packageSpec.setPropagate(propagate);
		packageSpec.setResolution(resolution);
		packageSpec.setAttributes(attributes);
		return packageSpec;
	}

	public ImportPackageSpecification createImportPackageSpecification(ImportPackageSpecification original) {
		ImportPackageSpecificationImpl packageSpec = new ImportPackageSpecificationImpl();
		packageSpec.setName(original.getName());
		packageSpec.setVersionRange(original.getVersionRange());
		packageSpec.setBundleSymbolicName(original.getBundleSymbolicName());
		packageSpec.setBundleVersionRange(original.getBundleVersionRange());
		packageSpec.setPropagate(original.getPropagate());
		packageSpec.setResolution(original.getResolution());
		packageSpec.setAttributes(original.getAttributes());
		return packageSpec;
	}

	public ExportPackageDescription createExportPackageDescription(String packageName, Version version, String grouping, String include, String exclude, Map attributes, String[] mandatory, boolean root) {
		return createExportPackageDescription(packageName, version, grouping, include, exclude, attributes, mandatory, root, null);
	}

	public ExportPackageDescription createExportPackageDescription(ExportPackageDescription original) {
		return createExportPackageDescription(original.getName(), original.getVersion(), original.getGrouping(), original.getInclude(), original.getExclude(), original.getAttributes(), original.getMandatory(), original.isRoot(), null);
	}

	public ExportPackageDescription createExportPackageDescription(String packageName, Version version, String grouping, String include, String exclude, Map attributes, String[] mandatory, boolean root, BundleDescription exporter) {
		ExportPackageDescriptionImpl exportPackage = new ExportPackageDescriptionImpl();
		exportPackage.setName(packageName);
		exportPackage.setVersion(version);
		exportPackage.setGrouping(grouping);
		exportPackage.setInclude(include);
		exportPackage.setExclude(exclude);
		exportPackage.setAttributes(attributes);
		exportPackage.setMandatory(mandatory);
		exportPackage.setRoot(root);
		exportPackage.setExporter(exporter);
		return exportPackage;
	}

	public SystemState createSystemState() {
		SystemState state = new SystemState();
		state.setFactory(this);
		return state;
	}

	public State createState() {
		return internalCreateState();
	}

	public State createState(State original) {
		StateImpl newState = internalCreateState();
		newState.setTimeStamp(original.getTimeStamp());
		BundleDescription[] bundles = original.getBundles();
		for (int i = 0; i < bundles.length; i++)
			newState.basicAddBundle(createBundleDescription(bundles[i]));
		newState.setResolved(false);
		return newState;
	}

	private StateImpl internalCreateState() {
		StateImpl state = new UserState();
		state.setFactory(this);
		return state;
	}

	public SystemState readSystemState(File stateLocation, boolean lazyLoad, long expectedTimeStamp) throws IOException {
		StateReader reader = new StateReader(stateLocation, lazyLoad);
		SystemState restoredState = new SystemState();
		restoredState.setReader(reader);
		restoredState.setFactory(this);
		if (!reader.loadState(restoredState, expectedTimeStamp))
			return null;
		return restoredState;
	}

	public State readState(InputStream stream) throws IOException {
		return internalReadState(internalCreateState(), new DataInputStream(stream), -1);
	}

	public State readState(DataInputStream stream) throws IOException {
		return internalReadState(internalCreateState(), stream, -1);
	}

	private State internalReadState(StateImpl toRestore, DataInputStream stream, long expectedTimestamp) throws IOException {
		StateReader reader = new StateReader();
		if (!reader.loadState(toRestore, stream, expectedTimestamp))
			return null;
		return toRestore;
	}

	public void writeState(State state, DataOutputStream stream) throws IOException {
		internalWriteState(state, stream);
	}

	public void writeState(State state, OutputStream stream) throws IOException {
		internalWriteState(state, new DataOutputStream(stream));
	}

	public void internalWriteState(State state, DataOutputStream stream) throws IOException {
		if (state.getFactory() != this)
			throw new IllegalArgumentException();
		StateWriter writer = new StateWriter();
		writer.saveState((StateImpl) state, stream);
	}
}