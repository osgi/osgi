/**
 * Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import java.util.*;
import org.osgi.framework.*;

/**
 * Here we handle all the java packages that are imported and exported within
 * framework.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class Packages {
	/**
	 * Framework for bundle.
	 */
	final Framework		framework;
	/**
	 * All exported and imported packages.
	 */
	private HashMap	/* String->Pkg */packages		= new HashMap();
	/**
	 * List of temporary resolved bundles.
	 */
	private ArrayList	/* BundleImpl */tempResolved	= null;
	/**
	 * Map of temporary package providers.
	 */
	private HashMap		tempProvider					= null;

	/**
	 * Construct Packages object.
	 */
	Packages(Framework fw) {
		framework = fw;
	}

	/**
	 * Register all packages a bundle needs to export and import. If it is
	 * registered by the system bundle, export it immediately.
	 * 
	 * @param exports Exported packages.
	 * @param imports Imported packages.
	 */
	synchronized void registerPackages(Iterator exports, Iterator imports) {
		while (exports.hasNext()) {
			PkgEntry pe = (PkgEntry) exports.next();
			Pkg p = (Pkg) packages.get(pe.name);
			if (p == null) {
				p = new Pkg(pe.name);
				packages.put(pe.name, p);
			}
			p.addExporter(pe);
		}
		while (imports.hasNext()) {
			PkgEntry pe = (PkgEntry) imports.next();
			Pkg p = (Pkg) packages.get(pe.name);
			if (p == null) {
				p = new Pkg(pe.name);
				packages.put(pe.name, p);
			}
			p.addImporter(pe);
		}
	}

	/**
	 * Dynamicly check and register a dynamic package import.
	 * 
	 * @param pe PkgEntry import to add.
	 * @return PkgEntry for package provider.
	 */
	synchronized PkgEntry registerDynamicImport(PkgEntry pe) {
		Pkg p = (Pkg) packages.get(pe.name);
		if (p != null && p.provider != null) {
			p.addImporter(pe);
			return p.provider;
		}
		return null;
	}

	/**
	 * Unregister bundle packages in framework. If we find exported packages
	 * that has been selected as providers don't unregister them unless the
	 * parameter force is true. If not all exporters were removed, the don't
	 * remove any importers
	 * 
	 * @param exports Exported packages.
	 * @param imports Imported packages.
	 * @param force If true force unregistration of package providers.
	 * @return True if all packages were succesfully unregistered, otherwise
	 *         false.
	 */
	synchronized boolean unregisterPackages(Iterator exports, Iterator imports,
			boolean force) {
		boolean allRemoved = true;
		while (exports.hasNext()) {
			PkgEntry pe = (PkgEntry) exports.next();
			Pkg p = pe.pkg;
			if (p != null) {
				if (!p.removeExporter(pe)) {
					if (force) {
						p.provider = null;
						p.removeExporter(pe);
					}
					else {
						allRemoved = false;
						p.zombie = true;
						continue;
					}
				}
				if (p.isEmpty()) {
					packages.remove(pe.name);
				}
			}
		}
		if (allRemoved) {
			while (imports.hasNext()) {
				PkgEntry pe = (PkgEntry) imports.next();
				Pkg p = pe.pkg;
				if (p != null) {
					p.removeImporter(pe);
					if (p.isEmpty()) {
						packages.remove(pe.name);
					}
				}
			}
		}
		return allRemoved;
	}

	/**
	 * Check if a list packages can be resolved.
	 * 
	 * @param bundle Bundle owning packages.
	 * @param pkgs List of packages to be resolved.
	 * @return List of packages not resolvable or null if all were resolved.
	 */
	synchronized List checkResolve(Bundle bundle, Iterator pkgs) {
		if (tempResolved != null) {
			// If we entry with tempResolved set, it means that we already have
			// resolved bundles. Check that it is true!
			if (!tempResolved.contains(bundle)) {
				framework.listeners.frameworkError(bundle, new Exception(
						"checkResolve: InternalError1!"));
			}
			return null;
		}
		tempProvider = new HashMap();
		tempResolved = new ArrayList();
		tempResolved.add(bundle);
		List res = resolvePackages(pkgs);
		if (res.size() == 0) {
			for (Iterator i = tempProvider.values().iterator(); i.hasNext();) {
				PkgEntry pe = (PkgEntry) i.next();
				pe.pkg.provider = pe;
			}
			tempResolved.remove(0);
			for (Iterator i = tempResolved.iterator(); i.hasNext();) {
				BundleImpl bs = (BundleImpl) i.next();
				if (bs.getUpdatedState() == Bundle.INSTALLED) {
					framework.listeners.frameworkError(bs, new Exception(
							"checkResolve: InternalError2!"));
				}
			}
			res = null;
		}
		tempProvider = null;
		tempResolved = null;
		return res;
	}

	/**
	 * Get selected provider of a package.
	 * 
	 * @param pkg Exported package.
	 * @return PkgEntry that exports the package, null if no provider.
	 */
	synchronized PkgEntry getProvider(String pkg) {
		Pkg p = (Pkg) packages.get(pkg);
		if (p != null) {
			return p.provider;
		}
		else {
			return null;
		}
	}

	/**
	 * Check if PkgEntry is provider of a package.
	 * 
	 * @param pe Package to check.
	 * @return True if pkg exports the package.
	 */
	synchronized boolean isProvider(PkgEntry pe) {
		return pe.pkg != null && pe.pkg.provider == pe;
	}

	/**
	 * Check if a package is in zombie state.
	 * 
	 * @param pe Package to check.
	 * @return True if pkg is zombie exported.
	 */
	synchronized boolean isZombiePackage(PkgEntry pe) {
		return pe.pkg != null && pe.pkg.zombie;
	}

	/**
	 * Get all packages exported by the system.
	 * 
	 * @return Export-package string for system bundle.
	 */
	synchronized String systemPackages() {
		StringBuffer res = new StringBuffer();
		for (Iterator i = packages.values().iterator(); i.hasNext();) {
			Pkg p = (Pkg) i.next();
			PkgEntry pe = p.provider;
			if (pe != null && framework.systemBundle == pe.bundle) {
				if (res.length() > 0) {
					res.append(", ");
				}
				res.append(pe.pkgString());
			}
		}
		return res.toString();
	}

	/**
	 * Get specification version of an exported package.
	 * 
	 * @param pkg Exported package.
	 * @return Version of package or null if unspecified.
	 */
	synchronized String getPackageVersion(String pkg) {
		Pkg p = (Pkg) packages.get(pkg);
		PkgEntry pe = p.provider;
		return pe != null && pe.version.isSpecified() ? pe.version.toString()
				: null;
	}

	/**
	 * Get packages provide by bundle. If bundle is null, get all.
	 * 
	 * @param b Bundle exporting packages or null for all packages.
	 * @return List of packages exported.
	 */
	synchronized Collection getPackagesProvidedBy(Bundle b) {
		ArrayList res = new ArrayList();
		for (Iterator i = packages.values().iterator(); i.hasNext();) {
			Pkg p = (Pkg) i.next();
			if (p.provider != null && (b == null || b == p.provider.bundle)) {
				res.add(p.provider);
			}
		}
		return res;
	}

	/**
	 * Get active importers of a package.
	 * 
	 * @param pkg Package.
	 * @return List of bundles importering.
	 */
	synchronized Collection getPackageImporters(String pkg) {
		Pkg p = (Pkg) packages.get(pkg);
		Set res = new HashSet();
		if (p != null && p.provider != null) {
			List i = p.importers;
			for (int x = 0; x < i.size(); x++) {
				PkgEntry pe = (PkgEntry) i.get(x);
				if (pe.bundle.state != Bundle.INSTALLED) {
					res.add(pe.bundle);
				}
			}
		}
		return res;
	}

	/**
	 * Get bundles affected by zombie packages. Compute a graph of bundles
	 * starting with the specified bundles. If no bundles are specified, compute
	 * a graph of bundles starting with all exporting a zombie package. Any
	 * bundle that imports a package that is currently exported by a bundle in
	 * the graph is added to the graph. The graph is fully constructed when
	 * there is no bundle outside the graph that imports a package from a bundle
	 * in the graph. The graph may contain <tt>UNINSTALLED</tt> bundles that
	 * are currently still exporting packages.
	 * 
	 * @param bundles Initial bundle set.
	 * @return List of bundles affected.
	 */
	synchronized Collection getZombieAffected(Bundle[] bundles) {
		HashSet affected = new HashSet();
		if (bundles == null) {
			for (Iterator i = packages.values().iterator(); i.hasNext();) {
				Pkg p = (Pkg) i.next();
				PkgEntry pe = p.provider;
				if (pe != null && p.zombie) {
					affected.add(pe.bundle);
				}
			}
		}
		else {
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i] != null) {
					affected.add(bundles[i]);
				}
			}
		}
		ArrayList moreBundles = new ArrayList(affected);
		for (int i = 0; i < moreBundles.size(); i++) {
			BundleImpl b = (BundleImpl) moreBundles.get(i);
			for (Iterator j = b.getExports(); j.hasNext();) {
				PkgEntry pe = (PkgEntry) j.next();
				if (pe.pkg.provider == pe) {
					for (Iterator k = getPackageImporters(pe.name).iterator(); k
							.hasNext();) {
						Bundle ib = (Bundle) k.next();
						if (!affected.contains(ib)) {
							moreBundles.add(ib);
							affected.add(ib);
						}
					}
				}
			}
		}
		return affected;
	}

	//
	// Private methods.
	//
	/**
	 * Check if a bundle has all its package dependencies resolved.
	 * 
	 * @param pkgs List of packages to be resolved.
	 * @return List of packages not resolvable.
	 */
	private List resolvePackages(Iterator pkgs) {
		ArrayList res = new ArrayList();
		while (pkgs.hasNext()) {
			PkgEntry pe = (PkgEntry) pkgs.next();
			PkgEntry provider = pe.pkg.provider;
			if (provider == null) {
				provider = (PkgEntry) tempProvider.get(pe.name);
				if (provider == null) {
					provider = pickProvider(pe.pkg);
				}
			}
			if (provider == null || provider.compareVersion(pe) < 0) {
				res.add(pe);
			}
		}
		return res;
	}

	/**
	 * Find a provider for specified package.
	 * 
	 * @param pkg Package to find provider for.
	 * @return Package entry that can provide.
	 */
	private PkgEntry pickProvider(Pkg p) {
		PkgEntry provider = null;
		for (Iterator i = p.exporters.iterator(); i.hasNext();) {
			PkgEntry pe = (PkgEntry) i.next();
			if ((pe.bundle.state & (Bundle.RESOLVED | Bundle.STARTING
					| Bundle.ACTIVE | Bundle.STOPPING)) != 0) {
				provider = pe;
				break;
			}
			if (pe.bundle.state == Bundle.INSTALLED) {
				if (tempResolved.contains(pe.bundle)) {
					provider = pe;
					break;
				}
				int oldTempStartSize = tempResolved.size();
				HashMap oldTempProvider = (HashMap) tempProvider.clone();
				tempResolved.add(pe.bundle);
				List r = resolvePackages(pe.bundle.getImports());
				if (r.size() == 0) {
					provider = pe;
					break;
				}
				else {
					tempProvider = oldTempProvider;
					for (int x = tempResolved.size() - 1; x >= oldTempStartSize; x--) {
						tempResolved.remove(x);
					}
				}
			}
		}
		if (provider != null) {
			tempProvider.put(p.pkg, provider);
		}
		return provider;
	}
}
