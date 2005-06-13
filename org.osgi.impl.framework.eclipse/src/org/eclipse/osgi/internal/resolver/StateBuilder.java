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
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * This class builds bundle description objects from manifests
 */
class StateBuilder {
	static String[] DEFINED_MATCHING_ATTRS = {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, Constants.BUNDLE_VERSION_ATTRIBUTE, Constants.PACKAGE_SPECIFICATION_VERSION, Constants.VERSION_ATTRIBUTE};

	static String[] DEFINED_OSGI_VALIDATE_HEADERS = {Constants.IMPORT_PACKAGE, Constants.DYNAMICIMPORT_PACKAGE, Constants.EXPORT_PACKAGE, Constants.FRAGMENT_HOST, Constants.BUNDLE_SYMBOLICNAME, Constants.REEXPORT_PACKAGE, Constants.REQUIRE_BUNDLE};

	static BundleDescription createBundleDescription(StateImpl state, Dictionary manifest, String location) throws BundleException {
		BundleDescriptionImpl result = new BundleDescriptionImpl();
		String manifestVersionHeader = (String) manifest.get(Constants.BUNDLE_MANIFESTVERSION);
		int manifestVersion = 1;
		if (manifestVersionHeader != null)
			manifestVersion = Integer.parseInt(manifestVersionHeader);
		if (manifestVersion >= 2)
			validateHeaders(manifest);

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
				String fragmentAttachment = symbolicNameElements[0].getDirective(Constants.FRAGMENT_ATTACHMENT_DIRECTIVE);
				if (fragmentAttachment != null) {
					if (fragmentAttachment.equals(Constants.FRAGMENT_ATTACHMENT_RESOLVETIME)) {
						result.setStateBit(BundleDescriptionImpl.ATTACH_FRAGMENTS, true);
						result.setStateBit(BundleDescriptionImpl.DYNAMIC_FRAGMENTS, false);
					} else if (fragmentAttachment.equals(Constants.FRAGMENT_ATTACHMENT_NEVER)) {
						result.setStateBit(BundleDescriptionImpl.ATTACH_FRAGMENTS, false);
						result.setStateBit(BundleDescriptionImpl.DYNAMIC_FRAGMENTS, false);
					}
				}
			}
		}
		// retrieve other headers
		String version = (String) manifest.get(Constants.BUNDLE_VERSION);
		try {
			result.setVersion((version != null) ? Version.parseVersion(version) : Version.emptyVersion);
		} catch (NumberFormatException ex) {
			throw new BundleException(ex.getMessage());
		}
		result.setLocation(location);
		result.setPlatformFilter((String) manifest.get(Constants.ECLIPSE_PLATFORMFILTER));
		ManifestElement[] host = ManifestElement.parseHeader(Constants.FRAGMENT_HOST, (String) manifest.get(Constants.FRAGMENT_HOST));
		if (host != null)
			result.setHost(createHostSpecification(host[0]));
		ManifestElement[] exports = ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, (String) manifest.get(Constants.EXPORT_PACKAGE));
		ManifestElement[] reexports = ManifestElement.parseHeader(Constants.REEXPORT_PACKAGE, (String) manifest.get(Constants.REEXPORT_PACKAGE));
		ManifestElement[] provides = ManifestElement.parseHeader(Constants.PROVIDE_PACKAGE, (String) manifest.get(Constants.PROVIDE_PACKAGE)); // TODO this is null for now until the framwork is updated to handle the new re-export semantics
		boolean strict = state != null && state.inStrictMode();
		ArrayList providedExports = new ArrayList(provides == null ? 0 : provides.length);
		result.setExportPackages(createExportPackages(exports, reexports, provides, providedExports, manifestVersion, strict));
		ManifestElement[] imports = ManifestElement.parseHeader(Constants.IMPORT_PACKAGE, (String) manifest.get(Constants.IMPORT_PACKAGE));
		ManifestElement[] dynamicImports = ManifestElement.parseHeader(Constants.DYNAMICIMPORT_PACKAGE, (String) manifest.get(Constants.DYNAMICIMPORT_PACKAGE));
		result.setImportPackages(createImportPackages(result.getExportPackages(), providedExports, imports, dynamicImports, manifestVersion));
		ManifestElement[] requires = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, (String) manifest.get(Constants.REQUIRE_BUNDLE));
		result.setRequiredBundles(createRequiredBundles(requires));
		return result;
	}

	private static void validateHeaders(Dictionary manifest) throws BundleException {
		for (int i = 0; i < DEFINED_OSGI_VALIDATE_HEADERS.length; i++) {
			String header = (String) manifest.get(DEFINED_OSGI_VALIDATE_HEADERS[i]);
			if (header != null) {
				ManifestElement[] elements = ManifestElement.parseHeader(DEFINED_OSGI_VALIDATE_HEADERS[i], header);
				checkForDuplicateDirectives(elements);
				if (DEFINED_OSGI_VALIDATE_HEADERS[i] == Constants.REEXPORT_PACKAGE)
					checkForUsesDirective(elements);
				if (DEFINED_OSGI_VALIDATE_HEADERS[i] == Constants.IMPORT_PACKAGE || DEFINED_OSGI_VALIDATE_HEADERS[i] == Constants.DYNAMICIMPORT_PACKAGE)
					checkImportExportSyntax(elements, false);
				if (DEFINED_OSGI_VALIDATE_HEADERS[i] == Constants.EXPORT_PACKAGE)
					checkImportExportSyntax(elements, true);
			} else if (DEFINED_OSGI_VALIDATE_HEADERS[i] == Constants.BUNDLE_SYMBOLICNAME) {
				throw new BundleException(NLS.bind(StateMsg.HEADER_REQUIRED, Constants.BUNDLE_SYMBOLICNAME));
			}
		}
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
				result.setDirective(Constants.RESOLUTION_DIRECTIVE, ImportPackageSpecification.RESOLUTION_STATIC);
				allImports.add(result);
			}
		} else {
			allImports = new ArrayList(imported == null ? 0 : imported.length);
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
				while (iter.hasNext())
					if (importNames[i].equals(((ImportPackageSpecification) iter.next()).getName()))
						iter.remove();
			}

			ImportPackageSpecificationImpl result = new ImportPackageSpecificationImpl();
			result.setName(importNames[i]);
			// set common attributes for both dynamic and static imports
			String versionString = importPackage.getAttribute(Constants.VERSION_ATTRIBUTE);
			if (versionString == null) // specification-version aliases to version
				versionString = importPackage.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION);
			result.setVersionRange(getVersionRange(versionString));
			result.setBundleSymbolicName(importPackage.getAttribute(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE));
			result.setBundleVersionRange(getVersionRange(importPackage.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE)));
			result.setAttributes(getAttributes(importPackage, DEFINED_MATCHING_ATTRS));

			if (dynamic) {
				result.setDirective(Constants.RESOLUTION_DIRECTIVE, ImportPackageSpecification.RESOLUTION_DYNAMIC);
			} else {
				result.setDirective(Constants.RESOLUTION_DIRECTIVE, getResolution(importPackage.getDirective(Constants.RESOLUTION_DIRECTIVE)));
			}

			allImports.add(result);
		}
	}

	private static String getResolution(String resolution) {
		String result = ImportPackageSpecification.RESOLUTION_STATIC;
		if (Constants.RESOLUTION_OPTIONAL.equals(resolution))
			result = ImportPackageSpecification.RESOLUTION_OPTIONAL;
		return result;
	}

	static ExportPackageDescription[] createExportPackages(ManifestElement[] exported, ManifestElement[] reexported, ManifestElement[] provides, ArrayList providedExports, int manifestVersion, boolean strict) throws BundleException {
		int numExports = (exported == null ? 0 : exported.length) + (reexported == null ? 0 : reexported.length) + (provides == null ? 0 : provides.length);
		if (numExports == 0)
			return null;
		ArrayList allExports = new ArrayList(numExports);
		if (exported != null)
			for (int i = 0; i < exported.length; i++)
				addExportPackages(exported[i], allExports, manifestVersion, false, strict);
		if (reexported != null)
			for (int i = 0; i < reexported.length; i++)
				addExportPackages(reexported[i], allExports, manifestVersion, true, strict);
		if (provides != null)
			addProvidePackages(provides, allExports, providedExports);
		return (ExportPackageDescription[]) allExports.toArray(new ExportPackageDescription[allExports.size()]);
	}

	private static void addExportPackages(ManifestElement exportPackage, ArrayList allExports, int manifestVersion, boolean reexported, boolean strict) throws BundleException {
		String[] exportNames = exportPackage.getValueComponents();
		for (int i = 0; i < exportNames.length; i++) {
			// if we are in strict mode and the package is marked as internal, skip it.
			if (strict && "true".equals(exportPackage.getDirective(Constants.INTERNAL_DIRECTIVE))) //$NON-NLS-1$
				continue;
			ExportPackageDescriptionImpl result = new ExportPackageDescriptionImpl();
			result.setName(exportNames[i]);
			String versionString = exportPackage.getAttribute(Constants.VERSION_ATTRIBUTE);
			if (versionString == null) // specification-version aliases to version
				versionString = exportPackage.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION);
			if (versionString != null)
				result.setVersion(Version.parseVersion(versionString));
			result.setDirective(Constants.USES_DIRECTIVE, ManifestElement.getArrayFromList(exportPackage.getDirective(Constants.USES_DIRECTIVE)));
			result.setDirective(Constants.INCLUDE_DIRECTIVE, exportPackage.getDirective(Constants.INCLUDE_DIRECTIVE));
			result.setDirective(Constants.EXCLUDE_DIRECTIVE, exportPackage.getDirective(Constants.EXCLUDE_DIRECTIVE));
			result.setDirective(Constants.FRIENDS_DIRECTIVE, ManifestElement.getArrayFromList(exportPackage.getDirective(Constants.FRIENDS_DIRECTIVE)));
			result.setDirective(Constants.INTERNAL_DIRECTIVE, Boolean.valueOf(exportPackage.getDirective(Constants.INTERNAL_DIRECTIVE)));
			result.setDirective(Constants.MANDATORY_DIRECTIVE, ManifestElement.getArrayFromList(exportPackage.getDirective(Constants.MANDATORY_DIRECTIVE)));
			result.setAttributes(getAttributes(exportPackage, DEFINED_MATCHING_ATTRS));
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
		result.setIsMultiHost("true".equals(spec.getDirective("multiple-hosts"))); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}

	private static VersionRange getVersionRange(String versionRange) {
		if (versionRange == null)
			return null;
		return new VersionRange(versionRange);
	}

	private static void checkImportExportSyntax(ManifestElement[] elements, boolean export) throws BundleException {
		if (elements == null)
			return;
		int length = elements.length;
		Set packages = new HashSet(length);
		for (int i = 0; i < length; i++) {
			// check for duplicate imports
			String[] packageNames = elements[i].getValueComponents();
			for (int j = 0; j < packageNames.length; j++) {
				if (packages.contains(packageNames[j]))
					throw new BundleException(StateMsg.HEADER_PACKAGE_DUPLICATES);
				// check for java.*
				if (packageNames[j].startsWith("java.")) //$NON-NLS-1$
					throw new BundleException(StateMsg.HEADER_PACKAGE_JAVA);
				packages.add(packageNames[j]);
			}
			// check for version/specification version mismatch
			String version = elements[i].getAttribute(Constants.VERSION_ATTRIBUTE);
			if (version != null) {
				String specVersion = elements[i].getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION);
				if (specVersion != null && !specVersion.equals(version))
					throw new BundleException(NLS.bind(StateMsg.HEADER_VERSION_ERROR, Constants.VERSION_ATTRIBUTE, Constants.PACKAGE_SPECIFICATION_VERSION));
			}
			// check for bundle-symbolic-name and bundle-verion attibures
			// (failure)
			if (export) {
				if (elements[i].getAttribute(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE) != null)
					throw new BundleException(NLS.bind(StateMsg.HEADER_EXPORT_ATTR_ERROR, Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, Constants.EXPORT_PACKAGE));
				if (elements[i].getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE) != null)
					throw new BundleException(NLS.bind(StateMsg.HEADER_EXPORT_ATTR_ERROR, Constants.BUNDLE_VERSION_ATTRIBUTE, Constants.EXPORT_PACKAGE));
			}
		}
	}

	private static void checkForDuplicateDirectives(ManifestElement[] elements) throws BundleException {
		// check for duplicate directives
		for (int i = 0; i < elements.length; i++) {
			Enumeration keys = elements[i].getDirectiveKeys();
			if (keys != null) {
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					String[] directives = elements[i].getDirectives(key);
					if (directives.length > 1)
						throw new BundleException(StateMsg.HEADER_DIRECTIVE_DUPLICATES);
				}
			}
		}
	}

	private static void checkForUsesDirective(ManifestElement[] elements) throws BundleException {
		for (int i = 0; i < elements.length; i++)
			if (elements[i].getDirective(Constants.USES_DIRECTIVE) != null)
				throw new BundleException(NLS.bind(StateMsg.HEADER_REEXPORT_USES, Constants.USES_DIRECTIVE, Constants.REEXPORT_PACKAGE));
	}
}
