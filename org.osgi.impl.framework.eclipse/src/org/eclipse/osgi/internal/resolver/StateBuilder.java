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

import java.util.*;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * This class builds bundle description objects from manifests
 */
class StateBuilder {
	static String[] DEFINED_MATCHING_ATTRS = {
			Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE,
			Constants.BUNDLE_VERSION_ATTRIBUTE,
			Constants.PACKAGE_SPECIFICATION_VERSION,
			Constants.VERSION_ATTRIBUTE
	};

	static BundleDescription createBundleDescription(Dictionary manifest, String location) throws BundleException {
		BundleDescriptionImpl result = new BundleDescriptionImpl();
		String manifestVersionHeader = (String) manifest.get(Constants.BUNDLE_MANIFESTVERSION);
		int manifestVersion = 1;
		if (manifestVersionHeader != null)
			manifestVersion = Integer.parseInt(manifestVersionHeader);
		// retrieve the symbolic-name and the singleton status 
		String symbolicNameHeader = (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME);
		if (symbolicNameHeader != null) {
			ManifestElement[] symbolicNameElements = ManifestElement.parseHeader(Constants.BUNDLE_SYMBOLICNAME, symbolicNameHeader);
			if (symbolicNameElements.length > 0) {
				result.setSymbolicName(symbolicNameElements[0].getValue());
				String singleton = symbolicNameElements[0].getDirective(Constants.SINGLETON_DIRECTIVE);
				if (singleton == null) // TODO this is for backward compatibility; need to check manifest version < 2 to allow this after everyone has converted to new syntax
					singleton = symbolicNameElements[0].getAttribute(Constants.SINGLETON_DIRECTIVE);
				result.setStateBit(BundleDescriptionImpl.SINGLETON, "true".equals(singleton)); //$NON-NLS-1$
			}
		}
		// retrieve other headers
		String version = (String) manifest.get(Constants.BUNDLE_VERSION);
		result.setVersion((version != null) ? Version.parseVersion(version) : Version.emptyVersion);
		result.setLocation(location);
		result.setPlatformFilter((String) manifest.get(Constants.ECLIPSE_PLATFORMFILTER));
		ManifestElement[] host = ManifestElement.parseHeader(Constants.FRAGMENT_HOST, (String) manifest.get(Constants.FRAGMENT_HOST));
		if (host != null)
			result.setHost(createHostSpecification(host[0]));
		ManifestElement[] exports = ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, (String) manifest.get(Constants.EXPORT_PACKAGE));
		ManifestElement[] reexports = ManifestElement.parseHeader(Constants.REEXPORT_PACKAGE, (String) manifest.get(Constants.REEXPORT_PACKAGE));
		ManifestElement[] provides = ManifestElement.parseHeader(Constants.PROVIDE_PACKAGE, (String) manifest.get(Constants.PROVIDE_PACKAGE)); // TODO this is null for now until the framwork is updated to handle the new re-export semantics
		ArrayList providedExports = new ArrayList(provides == null ? 0 : provides.length);
		result.setExportPackages(createExportPackages(result, exports, reexports, provides, providedExports, manifestVersion));
		ManifestElement[] imports = ManifestElement.parseHeader(Constants.IMPORT_PACKAGE, (String) manifest.get(Constants.IMPORT_PACKAGE));
		ManifestElement[] dynamicImports = ManifestElement.parseHeader(Constants.DYNAMICIMPORT_PACKAGE, (String) manifest.get(Constants.DYNAMICIMPORT_PACKAGE));
		result.setImportPackages(createImportPackages(result.getExportPackages(), providedExports, imports, dynamicImports, manifestVersion));
		ManifestElement[] requires = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, (String) manifest.get(Constants.REQUIRE_BUNDLE));
		result.setRequiredBundles(createRequiredBundles(requires));
		return result;
	}

	private static BundleSpecification[] createRequiredBundles(ManifestElement[] specs) {
		if (specs == null)
			return null;
		BundleSpecification[] result = new BundleSpecification[specs.length];
		for (int i = 0; i < specs.length; i++)
			result[i] = createRequiredBundle(specs[i]);
		return result;
	}

	private static BundleSpecification createRequiredBundle(ManifestElement spec) {
		BundleSpecificationImpl result = new BundleSpecificationImpl();
		result.setName(spec.getValue());
		result.setVersionRange(getVersionRange(spec.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE)));
		result.setExported(Constants.VISIBILITY_REEXPORT.equals(spec.getDirective(Constants.VISIBILITY_DIRECTIVE)) || "true".equals(spec.getAttribute(Constants.REPROVIDE_ATTRIBUTE))); //$NON-NLS-1$
		result.setOptional(Constants.RESOLUTION_OPTIONAL.equals(spec.getDirective(Constants.RESOLUTION_DIRECTIVE)) || "true".equals(spec.getAttribute(Constants.OPTIONAL_ATTRIBUTE))); //$NON-NLS-1$
		return result;
	}

	private static String[] createProvidedPackages(ManifestElement[] specs) {
		if (specs == null || specs.length == 0)
			return null;
		String[] result = new String[specs.length];
		for (int i = 0; i < specs.length; i++)
			result[i] = specs[i].getValue();
		return result;
	}

	private static ImportPackageSpecification[] createImportPackages(ExportPackageDescription[] exported, ArrayList providedExports, ManifestElement[] imported, ManifestElement[] dynamicImported, int manifestVersion) throws BundleException {
		ArrayList allImports = null;
		if (manifestVersion < 2) {
			// add implicit imports for each exported package if manifest verions is less than 2.
			if (exported.length == 0 && imported == null && dynamicImported == null)
				return null;
			allImports = new ArrayList(exported.length + (imported == null ? 0 : imported.length));
			for (int i = 0; i < exported.length; i++) {
				if (providedExports.contains(exported[i].getName()))
					continue;
				ImportPackageSpecificationImpl result = new ImportPackageSpecificationImpl();
				result.setName(exported[i].getName());
				result.setVersionRange(getVersionRange(exported[i].getVersion().toString()));
				result.setResolution(ImportPackageSpecification.RESOLUTION_STATIC);
				allImports.add(result);
			}
		}
		else {
			allImports = new ArrayList(imported == null ? 0 :imported.length);
		}

		// add dynamics first so they will get overriden by static imports if
		// the same package is dyanamically imported and statically imported.
		if (dynamicImported != null) 
			for (int i = 0; i < dynamicImported.length; i++)
				addImportPackages(dynamicImported[i], allImports, manifestVersion, true);
		if (imported != null)
			for (int i = 0; i < imported.length; i++)
				addImportPackages(imported[i], allImports, manifestVersion, false);
		return (ImportPackageSpecification[]) allImports.toArray(new ImportPackageSpecification[allImports.size()]);
	}

	private static void addImportPackages(ManifestElement importPackage, ArrayList allImports, int manifestVersion, boolean dynamic) throws BundleException {
		String[] importNames = importPackage.getValueComponents();
		for (int i = 0; i < importNames.length; i++) {
			// do not allow for multiple imports of same package of manifest version < 2
			if (manifestVersion < 2) {
				Iterator iter = allImports.iterator();
				while(iter.hasNext())
					if (importNames[i].equals(((ImportPackageSpecification)iter.next()).getName()))
						iter.remove();
			}

			ImportPackageSpecificationImpl result = new ImportPackageSpecificationImpl();
			result.setName(importNames[i]);
			// set common attributes for both dynamic and static imports
			result.setVersionRange(getVersionRange(manifestVersion < 2 ? importPackage.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION) : importPackage.getAttribute(Constants.VERSION_ATTRIBUTE)));
			result.setBundleSymbolicName(importPackage.getAttribute(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE));
			result.setBundleVersionRange(getVersionRange(importPackage.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE)));
			result.setAttributes(getAttributes(importPackage,DEFINED_MATCHING_ATTRS));

			if (dynamic) {
				result.setResolution(ImportPackageSpecification.RESOLUTION_DYNAMIC);
			}
			else {
				result.setPropagate(ManifestElement.getArrayFromList(importPackage.getDirective(Constants.GROUPING_DIRECTIVE)));
				result.setResolution(getResolution(importPackage.getDirective(Constants.RESOLUTION_DIRECTIVE)));
			}

			allImports.add(result);
		}
	}

	private static int getResolution(String resolution) {
		int result = ImportPackageSpecification.RESOLUTION_STATIC;
		if (Constants.RESOLUTION_OPTIONAL.equals(resolution))
			result = ImportPackageSpecification.RESOLUTION_OPTIONAL;
		return result;
	}

	private static ExportPackageDescription[] createExportPackages(BundleDescriptionImpl bundle, ManifestElement[] exported, ManifestElement[] reexported, ManifestElement[] provides, ArrayList providedExports, int manifestVersion) throws BundleException {
		int numExports = (exported == null ? 0 : exported.length) + (reexported == null ? 0 : reexported.length) + (provides == null ? 0 : provides.length); 
		if (numExports == 0)
			return null;
		ArrayList allExports = new ArrayList(numExports);
		if (exported != null)
			for (int i = 0; i < exported.length; i++)
				addExportPackages(exported[i], allExports, manifestVersion, false);
		if (reexported != null)
			for (int i = 0; i < reexported.length; i++)
				addExportPackages(reexported[i], allExports, manifestVersion, true);
		if (provides != null)
			addProvidePackages(provides, allExports, providedExports);
		return (ExportPackageDescription[]) allExports.toArray(new ExportPackageDescription[allExports.size()]);
	}

	private static void addExportPackages(ManifestElement exportPackage, ArrayList allExports, int manifestVersion, boolean reexported) throws BundleException {
		String[] exportNames = exportPackage.getValueComponents();
		for (int i = 0; i < exportNames.length; i++) {
			ExportPackageDescriptionImpl result = new ExportPackageDescriptionImpl();
			result.setName(exportNames[i]);
			String versionString = manifestVersion < 2 ? exportPackage.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION) : exportPackage.getAttribute(Constants.VERSION_ATTRIBUTE);
			if (versionString != null)
				result.setVersion(Version.parseVersion(versionString));

			// alway setting the grouping here even for manifestVersion==1 because if it is null
			// the result will return a grouping equal to the package name which is unique and will
			// give the same behavior as OSGi R3.
			// TODO remove grouping !!!
			result.setGrouping(exportPackage.getDirective(Constants.GROUPING_DIRECTIVE));
			result.setUses(exportPackage.getDirectives(Constants.USES_DIRECTIVE));

			// set the rest of the attributes
			result.setInclude(exportPackage.getDirective(Constants.INCLUDE_DIRECTIVE));
			result.setExclude(exportPackage.getDirective(Constants.EXCLUDE_DIRECTIVE));
			result.setAttributes(getAttributes(exportPackage,DEFINED_MATCHING_ATTRS));
			result.setMandatory(ManifestElement.getArrayFromList(exportPackage.getDirective(Constants.MANDATORY_DIRECTIVE)));
			result.setRoot(!reexported);
			allExports.add(result);
		}
	}

	private static void addProvidePackages(ManifestElement[] provides, ArrayList allExports, ArrayList providedExports) throws BundleException {
		ExportPackageDescription[] currentExports = (ExportPackageDescription[]) allExports.toArray(new ExportPackageDescription[allExports.size()]);
		for (int i = 0; i < provides.length; i++) {
			boolean duplicate = false;
			for (int j = 0; j < currentExports.length; j++)
				if (provides[i].getValue().equals(currentExports[j].getName())) {
					duplicate = true;
					break;
				}
			if (!duplicate) {
				ExportPackageDescriptionImpl result = new ExportPackageDescriptionImpl();
				result.setName(provides[i].getValue());
				result.setRoot(true);
				allExports.add(result);
			}
			providedExports.add(provides[i].getValue());
		}
	}

	private static Map getAttributes(ManifestElement exportPackage, String[] definedAttrs) {
		Enumeration keys = exportPackage.getKeys();
		Map arbitraryAttrs = null;
		if (keys == null)
			return null;
		while (keys.hasMoreElements()) {
			boolean definedAttr = false;
			String key = (String) keys.nextElement();
			for (int i = 0; i < definedAttrs.length; i++) {
				if (definedAttrs[i].equals(key)) {
					definedAttr = true;
					break;
				}
			}
			if (!definedAttr) {
				if (arbitraryAttrs == null)
					arbitraryAttrs = new HashMap();
				arbitraryAttrs.put(key, exportPackage.getAttribute(key));
			}
		}
		return arbitraryAttrs;
	}

	private static HostSpecification createHostSpecification(ManifestElement spec) {
		if (spec == null)
			return null;
		HostSpecificationImpl result = new HostSpecificationImpl();
		result.setName(spec.getValue());
		result.setVersionRange(getVersionRange(spec.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE)));
		return result;
	}

	private static VersionRange getVersionRange(String versionRange) {
		if (versionRange == null)
			return null;
		return new VersionRange(versionRange);
	}
}
