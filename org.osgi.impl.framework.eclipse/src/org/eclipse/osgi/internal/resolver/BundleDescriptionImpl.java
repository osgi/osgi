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

import java.io.IOException;
import java.util.*;
import java.util.Iterator;

import org.eclipse.osgi.framework.internal.core.KeyedElement;
import org.eclipse.osgi.service.resolver.*;

public class BundleDescriptionImpl extends BaseDescriptionImpl implements BundleDescription, KeyedElement {
	private static final byte RESOLVED			= 0x01;
	private static final byte SINGLETON			= 0x02;
	private static final byte REMOVAL_PENDING	= 0x04;
	private static final byte FULLY_LOADED		= 0x08;
	private static final byte LAZY_LOADED		= 0x10;

	private byte stateBits = FULLY_LOADED; // set the fully loaded by default

	private long bundleId = -1;
	private String location;
	private HostSpecification host;
	private StateImpl containingState;

	private BundleSpecification[] requiredBundles;
	private ExportPackageDescription[] exportPackages;
	private ImportPackageSpecification[] importPackages;

	private ExportPackageDescription[] selectedExports;
	private BundleDescription[] resolvedRequires;
	private ExportPackageDescription[] resolvedImports;

	private Object userObject;
	private int lazyDataSize = -1;

	private ArrayList dependencies;
	private ArrayList dependents;

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
		return location;
	}

	public ImportPackageSpecification[] getImportPackages() {
		fullyLoad();
		if (importPackages == null)
			return new ImportPackageSpecification[0];
		return importPackages;
	}

	public BundleSpecification[] getRequiredBundles() {
		fullyLoad();
		if (requiredBundles == null)
			return new BundleSpecification[0];
		return requiredBundles;
	}

	public ExportPackageDescription[] getExportPackages() {
		fullyLoad();
		if (exportPackages == null)
			return new ExportPackageDescription[0]; 
		return exportPackages;
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
		return ((StateImpl) containingState).getFragments(this);
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

	public ExportPackageDescription[] getSelectedExports() {
		fullyLoad();
		if (selectedExports == null)
			return new ExportPackageDescription[0];
		return selectedExports;
	}

	public BundleDescription[] getResolvedRequires() {
		fullyLoad();
		if (resolvedRequires == null)
			return new BundleDescription[0];
		return resolvedRequires;
	}

	public ExportPackageDescription[] getResolvedImports() {
		fullyLoad();
		if (resolvedImports == null)
			return new ExportPackageDescription[0];
		return resolvedImports;
	}

	protected void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	protected void setSymbolicName(String symbolicName) {
		setName(symbolicName);
	}

	protected void setLocation(String location) {
		this.location = location;
	}

	protected void setExportPackages(ExportPackageDescription[] exportPackages) {
		this.exportPackages = exportPackages;
		if (exportPackages != null) {
			for (int i = 0; i < exportPackages.length; i++) {
				((ExportPackageDescriptionImpl)exportPackages[i]).setExporter(this);
			}
		}
	}

	protected void setImportPackages(ImportPackageSpecification[] importPackages) {
		this.importPackages = importPackages;
		if (importPackages != null) {
			for (int i = 0; i < importPackages.length; i++) {
				((ImportPackageSpecificationImpl)importPackages[i]).setBundle(this);
			}
		}
	}

	protected void setRequiredBundles(BundleSpecification[] requiredBundles) {
		this.requiredBundles = requiredBundles;
		if (requiredBundles != null)
			for (int i = 0; i < requiredBundles.length; i++)
				((VersionConstraintImpl) requiredBundles[i]).setBundle(this);
	}

	protected void setResolved(boolean resolved) {
		if (resolved)
			stateBits |= RESOLVED;
		else
			stateBits &= ~RESOLVED;
	}

	protected void setContainingState(State value) {
		containingState = (StateImpl) value;
		if (containingState != null && containingState.getReader() != null) {
			if (containingState.getReader().isLazyLoaded())
				stateBits |= LAZY_LOADED;
			else
				stateBits &= ~LAZY_LOADED;
		}
		else {
			stateBits &= ~LAZY_LOADED;
		}
	}

	protected void setHost(HostSpecification host) {
		this.host = host;
		if (host != null)
			((VersionConstraintImpl) host).setBundle(this);
	}

	protected void setSingleton(boolean singleton) {
		if (singleton)
			stateBits |= SINGLETON;
		else
			stateBits &= ~SINGLETON;
	}

	protected void setRemovalPending(boolean removalPending) {
		if (removalPending)
			stateBits |= REMOVAL_PENDING;
		else
			stateBits &= ~REMOVAL_PENDING;
	}

	protected void setSelectedExports(ExportPackageDescription[] selectedExports) {
		this.selectedExports = selectedExports;
		if (selectedExports != null) {
			for (int i = 0; i < selectedExports.length; i++) {
				((ExportPackageDescriptionImpl)selectedExports[i]).setExporter(this);
			}
		}
	}

	protected void setResolvedImports(ExportPackageDescription[] resolvedImports) {
		this.resolvedImports = resolvedImports;
	}

	protected void setResolvedRequires(BundleDescription[] resolvedRequires) {
		this.resolvedRequires = resolvedRequires;
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
			((BundleDescriptionImpl)iter.next()).removeDependent(this);
			iter.remove();
		}
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
			bundle = (BundleDescriptionImpl) ((ExportPackageDescription)dependency).getExporter();
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
			if (dep != this && dep instanceof BundleDescription && ((BundleDescription)dep).getHost() == null)
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
		if (fullyLoaded)
			stateBits |= FULLY_LOADED;
		else
			stateBits &= ~FULLY_LOADED;
	}

	boolean isFullyLoaded() {
		return (stateBits & FULLY_LOADED) != 0;
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
		synchronized (this) {
			if (isFullyLoaded())
				return;
			try {
				containingState.getReader().fullyLoad(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void addDynamicResolvedImport(ExportPackageDescriptionImpl result) {
		// mark the dependency
		addDependency(result);
		// add the export to the list of the resolvedImports
		if (resolvedImports == null) {
			resolvedImports = new ExportPackageDescription[] {result};
			return;
		}
		ExportPackageDescription[] newImports = new ExportPackageDescription[resolvedImports.length + 1];
		System.arraycopy(resolvedImports, 0, newImports, 0, resolvedImports.length);
		newImports[newImports.length - 1] = result;
		resolvedImports = newImports;
	}
}