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
package org.eclipse.osgi.service.resolver;

import java.io.*;
import java.util.Dictionary;
import java.util.Map;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * A factory for states and their component objects.  
 */
public interface StateObjectFactory {
	/**
	 * Creates an empty state. 
	 * 
	 * @return the created state
	 */
	public State createState();

	/**
	 * Creates a new state that is a copy of the given state. The returned state 
	 * will contain copies of all bundle descriptions in the given state. No data 
	 * pertaining to resolution is copied.
	 *  
	 * @param state a state to be copied
	 * @return the created state
	 */
	public State createState(State state);

	/**
	 * Creates a bundle description from the given parameters.
	 * 
	 * @param id id for the bundle 
	 * @param symbolicName symbolic name for the bundle (may be 
	 * <code>null</code>) 
	 * @param version version for the bundle (may be <code>null</code>)
	 * @param location location for the bundle (may be <code>null</code>)
	 * @param required version constraints for all required bundles (may be 
	 * <code>null</code>)
	 * @param host version constraint specifying the host for the bundle to be  
	 * created. Should be <code>null</code> if the bundle is not a fragment
	 * @param imports version constraints for all packages imported 
	 * (may be <code>null</code>)
	 * @param exports package descriptions of all the exported packages
	 * (may be <code>null</code>)
	 * @param providedPackages the list of provided packages (may be <code>null</code>) 
	 * @param singleton whether the bundle created should be a singleton
	 * @return the created bundle description
	 */
	public BundleDescription createBundleDescription(long id, String symbolicName, Version version, String location, BundleSpecification[] required, HostSpecification host, ImportPackageSpecification[] imports, ExportPackageDescription[] exports, String[] providedPackages, boolean singleton);

	/**
	 * Returns a bundle description based on the information in the supplied manifest dictionary.
	 * The manifest should contain String keys and String values which correspond to 
	 * proper OSGi manifest headers and values.
	 * 
	 * @param state the state for which the description is being created
	 * @param manifest a collection of OSGi manifest headers and values
	 * @param location the URL location of the bundle (may be <code>null</code>)
	 * @param id the id of the bundle
	 * @return a bundle description derived from the given information
	 * @throws BundleException if an error occurs while reading the manifest 
	 */
	public BundleDescription createBundleDescription(State state, Dictionary manifest, String location, long id) throws BundleException;

	/**
	 * Returns a bundle description based on the information in the supplied manifest dictionary.
	 * The manifest should contain String keys and String values which correspond to 
	 * proper OSGi manifest headers and values.
	 * 
	 * @param manifest a collection of OSGi manifest headers and values
	 * @param location the URL location of the bundle (may be <code>null</code>)
	 * @param id the id of the bundle
	 * @return a bundle description derived from the given information
	 * @throws BundleException if an error occurs while reading the manifest 
	 * @deprecated use createBundleDescription(state, manifest, location, id)
	 */
	public BundleDescription createBundleDescription(Dictionary manifest, String location, long id) throws BundleException;

	/**
	 * Creates a bundle description that is a copy of the given description.
	 * 
	 * @param original the bundle description to be copied
	 * @return the created bundle description
	 */
	public BundleDescription createBundleDescription(BundleDescription original);

	/**
	 * Creates a bundle specification from the given parameters.
	 * 
	 * @param requiredSymbolicName the symbolic name for the required bundle
	 * @param requiredVersionRange the required version range (may be <code>null</code>)
	 * @param export whether the required bundle should be re-exported 
	 * @param optional whether the constraint should be optional
	 * @return the created bundle specification
	 * @see VersionConstraint for information on the available match rules
	 */
	public BundleSpecification createBundleSpecification(String requiredSymbolicName, VersionRange requiredVersionRange, boolean export, boolean optional);

	/**
	 * Creates a bundle specification that is a copy of the given constraint.
	 *  
	 * @param original the constraint to be copied
	 * @return the created bundle specification
	 */
	public BundleSpecification createBundleSpecification(BundleSpecification original);

	/**
	 * Creates a host specification from the given parameters.
	 *  
	 * @param hostSymbolicName the symbolic name for the host bundle
	 * @param hostVersionRange the version range for the host bundle (may be <code>null</code>)
	 * @return the created host specification
	 * @see VersionConstraint for information on the available match rules 
	 */
	public HostSpecification createHostSpecification(String hostSymbolicName, VersionRange hostVersionRange);

	/**
	 * Creates a host specification that is a copy of the given constraint.
	 * 
	 * @param original the constraint to be copied
	 * @return the created host specification
	 */
	public HostSpecification createHostSpecification(HostSpecification original);

	/**
	 * Creates an import package specification from the given parameters.
	 *  
	 * @param packageName the package name
	 * @param versionRange the package versionRange (may be <code>null</code>).
	 * @param bundleSymbolicName the Bundle-SymbolicName of the bundle that must export the package (may be <code>null</code>)
	 * @param bundleVersionRange the bundle versionRange (may be <code>null</code>).
	 * @param directives the directives for this package
	 * @param attributes the arbitrary attributes for the package import (may be <code>null</code>)
	 * @param importer the importing bundle
	 * @return the created package specification
	 */
	public ImportPackageSpecification createImportPackageSpecification(String packageName, VersionRange versionRange, String bundleSymbolicName, VersionRange bundleVersionRange, Map directives, Map attributes, BundleDescription importer);

	/**
	 * Creates an import package specification that is a copy of the given import package
	 * @param original the import package to be copied
	 * @return the created package specification 
	 */
	public ImportPackageSpecification createImportPackageSpecification(ImportPackageSpecification original);

	/**
	 * Used by the Resolver to dynamically create ExportPackageDescription objects during the resolution process.
	 * The Resolver needs to create ExportPackageDescriptions dynamally for a host when a fragment.
	 * exports a package<p>
	 * 
	 * @param packageName
	 * @param version
	 * @param directives
	 * @param attributes
	 * @param root
	 * @param exporter
	 * @return the created package
	 */
	public ExportPackageDescription createExportPackageDescription(String packageName, Version version, Map directives, Map attributes, boolean root, BundleDescription exporter);

	/**
	 * Creates an import package specification that is a copy of the given constraint
	 * @param original the export package to be copied
	 * @return the created package
	 */
	public ExportPackageDescription createExportPackageDescription(ExportPackageDescription original);

	/**
	 * Persists the given state in the given output stream. Closes the stream.
	 * 
	 * @param state the state to be written
	 * @param stream the stream where to write the state to
	 * @throws IOException if an IOException happens while writing the state to 
	 * the stream
	 * @throws IllegalArgumentException if the state provided was not created by 
	 * this factory
	 * @deprecated use #writeState(State, File) instead
	 * @since 3.1
	 */
	public void writeState(State state, OutputStream stream) throws IOException;

	/**
	 * Persists the given state in the given output stream. Closes the stream.
	 * 
	 * @param state the state to be written
	 * @param stream the stream where to write the state to
	 * @throws IOException if an IOException happens while writing the state to 
	 * the stream
	 * @throws IllegalArgumentException if the state provided was not created by 
	 * this factory
	 * @deprecated use #writeState(State, File) instead
	 * @see #writeState(State, OutputStream)
	 */
	public void writeState(State state, DataOutputStream stream) throws IOException;

	/**
	 * Persists the given state in the given directory.
	 * 
	 * @param state the state to be written
	 * @param stateDirectory the directory where to write the state to
	 * @throws IOException if an IOException happens while writing the state to 
	 * the stream
	 * @throws IllegalArgumentException if the state provided was not created by 
	 * this factory
	 */
	public void writeState(State state, File stateDirectory) throws IOException;

	/**
	 * Reads a persisted state from the given stream. Closes the stream.
	 * 
	 * @param stream the stream where to read the state from
	 * @return the state read
	 * @throws IOException if an IOException happens while reading the state from 
	 * the stream
	 * @deprecated use #readState(File) instead
	 * @since 3.1
	 */
	public State readState(InputStream stream) throws IOException;

	/**
	 * Reads a persisted state from the given stream. Closes the stream.
	 * 
	 * @param stream the stream where to read the state from
	 * @return the state read
	 * @throws IOException if an IOException happens while reading the state from 
	 * the stream
	 * @deprecated use #readState(File) instead
	 * @see #readState(InputStream)
	 */
	public State readState(DataInputStream stream) throws IOException;

	/**
	 * Reads a persisted state from the given directory.
	 * 
	 * @param stateDirectory the directory where to read the state from
	 * @return the state read
	 * @throws IOException if an IOException happens while reading the state from 
	 * the stream
	 */
	public State readState(File stateDirectory) throws IOException;

}
