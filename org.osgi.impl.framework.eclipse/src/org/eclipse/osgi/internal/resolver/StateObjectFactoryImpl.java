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
import java.util.Dictionary;
import java.util.Map;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class StateObjectFactoryImpl implements StateObjectFactory {

	public BundleDescription createBundleDescription(Dictionary manifest, String location, long id) throws BundleException {
		return createBundleDescription(null, manifest, location, id);
	}

	public BundleDescription createBundleDescription(State state, Dictionary manifest, String location, long id) throws BundleException {
		BundleDescriptionImpl result = (BundleDescriptionImpl) StateBuilder.createBundleDescription((StateImpl)state, manifest, location);
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
		bundle.setStateBit(BundleDescriptionImpl.SINGLETON, singleton);
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
		bundle.setStateBit(BundleDescriptionImpl.SINGLETON, original.isSingleton());
		bundle.setStateBit(BundleDescriptionImpl.ATTACH_FRAGMENTS, original.attachFragments());
		bundle.setStateBit(BundleDescriptionImpl.DYNAMIC_FRAGMENTS, original.dynamicFragments());
		bundle.setStateBit(BundleDescriptionImpl.HAS_DYNAMICIMPORT, original.hasDynamicImports());
		bundle.setPlatformFilter(original.getPlatformFilter());
		return bundle;
	}

	public BundleSpecification createBundleSpecification(String requiredSymbolicName, VersionRange requiredVersionRange, boolean export, boolean optional) {
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

	public ImportPackageSpecification createImportPackageSpecification(String packageName, VersionRange versionRange, String bundleSymbolicName, VersionRange bundleVersionRange, Map directives, Map attributes, BundleDescription importer) {
		ImportPackageSpecificationImpl packageSpec = new ImportPackageSpecificationImpl();
		packageSpec.setName(packageName);
		packageSpec.setVersionRange(versionRange);
		packageSpec.setBundleSymbolicName(bundleSymbolicName);
		packageSpec.setBundleVersionRange(bundleVersionRange);
		packageSpec.setDirectives(directives);
		packageSpec.setAttributes(attributes);
		packageSpec.setBundle(importer);
		return packageSpec;
	}

	public ImportPackageSpecification createImportPackageSpecification(ImportPackageSpecification original) {
		ImportPackageSpecificationImpl packageSpec = new ImportPackageSpecificationImpl();
		packageSpec.setName(original.getName());
		packageSpec.setVersionRange(original.getVersionRange());
		packageSpec.setBundleSymbolicName(original.getBundleSymbolicName());
		packageSpec.setBundleVersionRange(original.getBundleVersionRange());
		packageSpec.setDirectives(original.getDirectives());
		packageSpec.setAttributes(original.getAttributes());
		return packageSpec;
	}

	public ExportPackageDescription createExportPackageDescription(ExportPackageDescription original) {
		return createExportPackageDescription(original.getName(), original.getVersion(), original.getDirectives(), original.getAttributes(), original.isRoot(), null);
	}

	public ExportPackageDescription createExportPackageDescription(String packageName, Version version, Map directives, Map attributes, boolean root, BundleDescription exporter) {
		ExportPackageDescriptionImpl exportPackage = new ExportPackageDescriptionImpl();
		exportPackage.setName(packageName);
		exportPackage.setVersion(version);
		exportPackage.setDirectives(directives);
		exportPackage.setAttributes(attributes);
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

	public SystemState readSystemState(File stateFile, File lazyFile, boolean lazyLoad, long expectedTimeStamp) throws IOException {
		StateReader reader = new StateReader(stateFile, lazyFile, lazyLoad);
		SystemState restoredState = new SystemState();
		restoredState.setReader(reader);
		restoredState.setFactory(this);
		if (!reader.loadState(restoredState, expectedTimeStamp))
			return null;
		return restoredState;
	}

	public State readState(InputStream stream) throws IOException {
		return internalReadStateDeprecated(internalCreateState(), new DataInputStream(stream), -1);
	}

	public State readState(DataInputStream stream) throws IOException {
		return internalReadStateDeprecated(internalCreateState(), stream, -1);
	}

	public State readState(File stateDirectory) throws IOException {
		return internalReadState(internalCreateState(), stateDirectory, -1);
	}

	private State internalReadStateDeprecated(StateImpl toRestore, DataInputStream stream, long expectedTimestamp) throws IOException {
		StateReader reader = new StateReader();
		if (!reader.loadStateDeprecated(toRestore, stream, expectedTimestamp))
			return null;
		return toRestore;
	}

	private State internalReadState(StateImpl toRestore, File stateDirectory, long expectedTimestamp) throws IOException {
		File stateFile = new File(stateDirectory, StateReader.STATE_FILE);
		File lazyFile = new File(stateDirectory, StateReader.LAZY_FILE);
		StateReader reader = new StateReader(stateFile, lazyFile, false);
		if (!reader.loadState(toRestore, expectedTimestamp))
			return null;
		return toRestore;
	}

	public void writeState(State state, DataOutputStream stream) throws IOException {
		internalWriteStateDeprecated(state, stream);
	}

	public void writeState(State state, File stateDirectory) throws IOException {
		if (stateDirectory == null)
			throw new IOException();
		StateWriter writer = new StateWriter();
		File stateFile = new File(stateDirectory, StateReader.STATE_FILE);
		File lazyFile = new File(stateDirectory, StateReader.LAZY_FILE);
		writer.saveState((StateImpl) state, stateFile, lazyFile);
	}

	public void writeState(State state, OutputStream stream) throws IOException {
		internalWriteStateDeprecated(state, new DataOutputStream(stream));
	}

	public void writeState(State state, File stateFile, File lazyFile) throws IOException {
		StateWriter writer = new StateWriter();
		writer.saveState((StateImpl) state, stateFile, lazyFile);
	}

	public void internalWriteStateDeprecated(State state, DataOutputStream stream) throws IOException {
		if (state.getFactory() != this)
			throw new IllegalArgumentException();
		StateWriter writer = new StateWriter();
		writer.saveStateDeprecated((StateImpl) state, stream);
	}
}
