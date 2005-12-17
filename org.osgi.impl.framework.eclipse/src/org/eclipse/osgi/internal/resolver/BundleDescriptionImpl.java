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

import java.io.IOException;
import java.util.*;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.internal.core.KeyedElement;
import org.eclipse.osgi.service.resolver.*;

public class BundleDescriptionImpl extends BaseDescriptionImpl implements BundleDescription, KeyedElement {
	static final int RESOLVED = 0x01;
	static final int SINGLETON = 0x02;
	static final int REMOVAL_PENDING = 0x04;
	static final int FULLY_LOADED = 0x08;
	static final int LAZY_LOADED = 0x10;
	static final int HAS_DYNAMICIMPORT = 0x20;
	static final int ATTACH_FRAGMENTS = 0x40;
	static final int DYNAMIC_FRAGMENTS = 0x80;

	// set to fully loaded and allow dynamic fragments by default
	private int stateBits = FULLY_LOADED | ATTACH_FRAGMENTS | DYNAMIC_FRAGMENTS;

	private long bundleId = -1;
	private HostSpecification host; //null if the bundle is not a fragment
	private StateImpl containingState;

	private Object userObject;
	private int lazyDataOffset = -1;
	private int lazyDataSize = -1;

	//TODO These could be arrays
	private ArrayList dependencies;
	private ArrayList dependents;

	private LazyData lazyData;
	private long lazyTimeStamp;

	public BundleDescriptionImpl() {
		// 
	}

	public long getBundleId() {
		return bundleId;
	}

	public String getSymbolicName() {
		return getName();
	}

	public String getLocation() {
		fullyLoad();
		return lazyData.location;
	}

	public String getPlatformFilter() {
		fullyLoad();
		return lazyData.platformFilter;
	}

	public ImportPackageSpecification[] getImportPackages() {
		fullyLoad();
		if (lazyData.importPackages == null)
			return new ImportPackageSpecification[0];
		return lazyData.importPackages;
	}

	public BundleSpecification[] getRequiredBundles() {
		fullyLoad();
		if (lazyData.requiredBundles == null)
			return new BundleSpecification[0];
		return lazyData.requiredBundles;
	}

	public ExportPackageDescription[] getExportPackages() {
		fullyLoad();
		ExportPackageDescription[] result = lazyData.exportPackages;
		if (Constants.getInternalSymbolicName().equals(getSymbolicName()))
			result = mergeSystemExports(result);
		if (result == null)
			return new ExportPackageDescription[0];
		return result;
	}

	private ExportPackageDescription[] mergeSystemExports(ExportPackageDescription[] existingExports) {
		if (containingState == null)
			return existingExports;
		ExportPackageDescription[] systemExports = containingState.getSystemPackages();
		if (systemExports == null || systemExports.length == 0)
			return existingExports;
		for (int i = 0; i < systemExports.length; i++)
			((ExportPackageDescriptionImpl) systemExports[i]).setExporter(this);
		ExportPackageDescription[] allExports = new ExportPackageDescription[existingExports.length + systemExports.length];
		System.arraycopy(existingExports, 0, allExports, 0, existingExports.length);
		System.arraycopy(systemExports, 0, allExports, existingExports.length, systemExports.length);
		return allExports;
	}

	public boolean isResolved() {
		return (stateBits & RESOLVED) != 0;
	}

	public State getContainingState() {
		return containingState;
	}

	public BundleDescription[] getFragments() {
		if (host != null)
			return new BundleDescription[0];
		return containingState.getFragments(this);
	}

	public HostSpecification getHost() {
		return host;
	}

	public boolean isSingleton() {
		return (stateBits & SINGLETON) != 0;
	}

	public boolean isRemovalPending() {
		return (stateBits & REMOVAL_PENDING) != 0;
	}

	public boolean hasDynamicImports() {
		return (stateBits & HAS_DYNAMICIMPORT) != 0;
	}

	public boolean attachFragments() {
		return (stateBits & ATTACH_FRAGMENTS) != 0;
	}

	public boolean dynamicFragments() {
		return (stateBits & DYNAMIC_FRAGMENTS) != 0;
	}

	public ExportPackageDescription[] getSelectedExports() {
		fullyLoad();
		if (lazyData.selectedExports == null)
			return new ExportPackageDescription[0];
		return lazyData.selectedExports;
	}

	public BundleDescription[] getResolvedRequires() {
		fullyLoad();
		if (lazyData.resolvedRequires == null)
			return new BundleDescription[0];
		return lazyData.resolvedRequires;
	}

	public ExportPackageDescription[] getResolvedImports() {
		fullyLoad();
		if (lazyData.resolvedImports == null)
			return new ExportPackageDescription[0];
		return lazyData.resolvedImports;
	}

	protected void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	protected void setSymbolicName(String symbolicName) {
		setName(symbolicName);
	}

	protected void setLocation(String location) {
		checkLazyData();
		lazyData.location = location;
	}

	protected void setPlatformFilter(String platformFilter) {
		checkLazyData();
		lazyData.platformFilter = platformFilter;
	}

	protected void setExportPackages(ExportPackageDescription[] exportPackages) {
		checkLazyData();
		lazyData.exportPackages = exportPackages;
		if (exportPackages != null) {
			for (int i = 0; i < exportPackages.length; i++) {
				((ExportPackageDescriptionImpl) exportPackages[i]).setExporter(this);
			}
		}
	}

	protected void setImportPackages(ImportPackageSpecification[] importPackages) {
		checkLazyData();
		lazyData.importPackages = importPackages;
		if (importPackages != null) {
			for (int i = 0; i < importPackages.length; i++) {
				if (Constants.OSGI_SYSTEM_BUNDLE.equals(importPackages[i].getBundleSymbolicName()))
					((ImportPackageSpecificationImpl) importPackages[i]).setBundleSymbolicName(Constants.getInternalSymbolicName());
				((ImportPackageSpecificationImpl) importPackages[i]).setBundle(this);
				if (ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(importPackages[i].getDirective(Constants.RESOLUTION_DIRECTIVE)))
					stateBits |= HAS_DYNAMICIMPORT;
			}
		}
	}

	protected void setRequiredBundles(BundleSpecification[] requiredBundles) {
		checkLazyData();
		lazyData.requiredBundles = requiredBundles;
		if (requiredBundles != null)
			for (int i = 0; i < requiredBundles.length; i++) {
				if (Constants.OSGI_SYSTEM_BUNDLE.equals(requiredBundles[i].getName()))
					((VersionConstraintImpl) requiredBundles[i]).setName(Constants.getInternalSymbolicName());
				((VersionConstraintImpl) requiredBundles[i]).setBundle(this);
			}
	}

	protected int getStateBits() {
		return stateBits;
	}

	protected void setStateBit(int stateBit, boolean on) {
		if (on)
			stateBits |= stateBit;
		else
			stateBits &= ~stateBit;
	}

	protected void setContainingState(State value) {
		containingState = (StateImpl) value;
		if (containingState != null && containingState.getReader() != null) {
			if (containingState.getReader().isLazyLoaded())
				stateBits |= LAZY_LOADED;
			else
				stateBits &= ~LAZY_LOADED;
		} else {
			stateBits &= ~LAZY_LOADED;
		}
	}

	protected void setHost(HostSpecification host) {
		this.host = host;
		if (host != null) {
			if (Constants.OSGI_SYSTEM_BUNDLE.equals(host.getName()))
				((VersionConstraintImpl) host).setName(Constants.getInternalSymbolicName());
			((VersionConstraintImpl) host).setBundle(this);
		}
	}

	protected void setLazyLoaded(boolean lazyLoad) {
		fullyLoad();
		if (lazyLoad)
			stateBits |= LAZY_LOADED;
		else
			stateBits &= ~LAZY_LOADED;
	}

	protected void setSelectedExports(ExportPackageDescription[] selectedExports) {
		checkLazyData();
		lazyData.selectedExports = selectedExports;
		if (selectedExports != null) {
			for (int i = 0; i < selectedExports.length; i++) {
				((ExportPackageDescriptionImpl) selectedExports[i]).setExporter(this);
			}
		}
	}

	protected void setResolvedImports(ExportPackageDescription[] resolvedImports) {
		checkLazyData();
		lazyData.resolvedImports = resolvedImports;
	}

	protected void setResolvedRequires(BundleDescription[] resolvedRequires) {
		checkLazyData();
		lazyData.resolvedRequires = resolvedRequires;
	}

	public String toString() {
		if (getSymbolicName() == null)
			return "[" + getBundleId() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		return getSymbolicName() + "_" + getVersion(); //$NON-NLS-1$
	}

	public Object getKey() {
		return new Long(bundleId);
	}

	public boolean compare(KeyedElement other) {
		if (!(other instanceof BundleDescriptionImpl))
			return false;
		BundleDescriptionImpl otherBundleDescription = (BundleDescriptionImpl) other;
		return bundleId == otherBundleDescription.bundleId;
	}

	public int getKeyHashCode() {
		return (int) (bundleId % Integer.MAX_VALUE);
	}

	/* TODO Determine if we need more than just Object ID type of hashcode.
	 public int hashCode() {
	 if (getSymbolicName() == null)
	 return (int) (bundleId % Integer.MAX_VALUE);
	 return (int) ((bundleId * (getSymbolicName().hashCode())) % Integer.MAX_VALUE);
	 }
	 */

	protected synchronized void removeDependencies() {
		if (dependencies == null)
			return;
		Iterator iter = dependencies.iterator();
		while (iter.hasNext()) {
			((BundleDescriptionImpl) iter.next()).removeDependent(this);
		}
		dependencies = null;
	}

	protected void addDependencies(BaseDescription[] newDependencies) {
		if (newDependencies == null)
			return;
		for (int i = 0; i < newDependencies.length; i++) {
			addDependency((BaseDescriptionImpl) newDependencies[i]);
		}
	}

	protected synchronized void addDependency(BaseDescriptionImpl dependency) {
		if (dependencies == null)
			dependencies = new ArrayList(10);
		BundleDescriptionImpl bundle;
		if (dependency instanceof ExportPackageDescription)
			bundle = (BundleDescriptionImpl) ((ExportPackageDescription) dependency).getExporter();
		else
			bundle = (BundleDescriptionImpl) dependency;
		if (!dependencies.contains(bundle)) {
			bundle.addDependent(this);
			dependencies.add(bundle);
		}
	}

	/*
	 * Gets all the bundle dependencies as a result of import-package or require-bundle.
	 * Self and fragment bundles are removed.
	 */
	synchronized List getBundleDependencies() {
		if (dependencies == null)
			return new ArrayList(0);
		ArrayList required = new ArrayList(dependencies.size());
		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			Object dep = iter.next();
			if (dep != this && dep instanceof BundleDescription && ((BundleDescription) dep).getHost() == null)
				required.add(dep);
		}
		return required;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	protected synchronized void addDependent(BundleDescription dependent) {
		if (dependents == null)
			dependents = new ArrayList(10);
		if (!dependents.contains(dependent))
			dependents.add(dependent);
	}

	protected synchronized void removeDependent(BundleDescription dependent) {
		if (dependents == null)
			return;
		dependents.remove(dependent);
	}

	public synchronized BundleDescription[] getDependents() {
		if (dependents == null)
			return new BundleDescription[0];
		return (BundleDescription[]) dependents.toArray(new BundleDescription[dependents.size()]);
	}

	void setFullyLoaded(boolean fullyLoaded) {
		if (fullyLoaded) {
			stateBits |= FULLY_LOADED;
			lazyTimeStamp = System.currentTimeMillis();
		} else {
			stateBits &= ~FULLY_LOADED;
		}
	}

	boolean isFullyLoaded() {
		return (stateBits & FULLY_LOADED) != 0;
	}

	void setLazyDataOffset(int lazyDataOffset) {
		this.lazyDataOffset = lazyDataOffset;
	}

	int getLazyDataOffset() {
		return this.lazyDataOffset;
	}

	void setLazyDataSize(int lazyDataSize) {
		this.lazyDataSize = lazyDataSize;
	}

	int getLazyDataSize() {
		return this.lazyDataSize;
	}

	private void fullyLoad() {
		if ((stateBits & LAZY_LOADED) == 0)
			return;
		if (isFullyLoaded())
			return;
		try {
			containingState.getReader().fullyLoad(this);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage()); // TODO not sure what to do here!!
		}
	}

	synchronized void addDynamicResolvedImport(ExportPackageDescriptionImpl result) {
		// mark the dependency
		addDependency(result);
		// add the export to the list of the resolvedImports
		checkLazyData();
		if (lazyData.resolvedImports == null) {
			lazyData.resolvedImports = new ExportPackageDescription[] {result};
			return;
		}
		ExportPackageDescription[] newImports = new ExportPackageDescription[lazyData.resolvedImports.length + 1];
		System.arraycopy(lazyData.resolvedImports, 0, newImports, 0, lazyData.resolvedImports.length);
		newImports[newImports.length - 1] = result;
		lazyData.resolvedImports = newImports;
	}

	void unload(long currentTime, long expireTime) {
		if ((stateBits & LAZY_LOADED) == 0)
			return;
		if (!isFullyLoaded() || (currentTime - lazyTimeStamp - expireTime) <= 0)
			return;
		setFullyLoaded(false);
		LazyData tempData = lazyData;
		lazyData = null;
		if (tempData == null || tempData.selectedExports == null)
			return;
		for (int i = 0; i < tempData.selectedExports.length; i++)
			containingState.getReader().objectTable.remove(new Integer(((ExportPackageDescriptionImpl) tempData.selectedExports[i]).getTableIndex()));
	}

	void setDynamicStamps(HashMap dynamicStamps) {
		lazyData.dynamicStamps = dynamicStamps;
	}

	void setDynamicStamp(String requestedPackage, Long timestamp) {
		checkLazyData();
		if (lazyData.dynamicStamps == null) {
			if (timestamp == null)
				return;
			lazyData.dynamicStamps = new HashMap();
		}
		if (timestamp == null)
			lazyData.dynamicStamps.remove(requestedPackage);
		else
			lazyData.dynamicStamps.put(requestedPackage, timestamp);
	}

	long getDynamicStamp(String requestedPackage) {
		fullyLoad();
		Long stamp = lazyData.dynamicStamps == null ? null : (Long) lazyData.dynamicStamps.get(requestedPackage);
		return stamp == null ? 0 : stamp.longValue();
	}

	HashMap getDynamicStamps() {
		fullyLoad();
		return lazyData.dynamicStamps;
	}

	private void checkLazyData() {
		if (lazyData == null)
			lazyData = new LazyData();
	}

	private final class LazyData {
		String location;
		String platformFilter;

		BundleSpecification[] requiredBundles;
		ExportPackageDescription[] exportPackages;
		ImportPackageSpecification[] importPackages;

		ExportPackageDescription[] selectedExports;
		BundleDescription[] resolvedRequires;
		ExportPackageDescription[] resolvedImports;

		HashMap dynamicStamps;
	}
}
