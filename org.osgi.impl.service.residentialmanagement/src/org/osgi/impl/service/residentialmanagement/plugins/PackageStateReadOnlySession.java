/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Hashtable;
import java.util.Date;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.ExportedPackage;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class PackageStateReadOnlySession implements ReadableDataSession {

	private static final String NAME = "Name";
	private static final String VERSION = "Version";
	private static final String REMOVALPENDING = "RemovalPending";
	private static final String EXPORTINGBUNDLE = "ExportingBundle";
	private static final String IMPORTINGBUNDLES = "ImportingBundles";

	private PackageAdmin packageAdmin;
	private Hashtable /* <String <id>, ExportedPackage exportPackage> */exportPackageObjTable = new Hashtable();
	private Hashtable exportPackageTable = new Hashtable();
	private int lastId = 0;

	PackageStateReadOnlySession(PackageStatePlugin plugin, BundleContext context) {
		ServiceReference ref = context.getServiceReference(PackageAdmin.class
				.getName());
		packageAdmin = (PackageAdmin) context.getService(ref);
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		refreshExportPackageTable();

		if (path.length == 1) {
			String[] children = new String[exportPackageObjTable.size()];
			int i = 0;
			for (Enumeration keys = exportPackageObjTable.keys(); keys
					.hasMoreElements(); i++) {
				children[i] = (String) keys.nextElement();
			}
			return children;
		}

		if (path.length == 2) {
			String[] children = new String[5];
			children[0] = NAME;
			children[1] = VERSION;
			children[2] = REMOVALPENDING;
			children[3] = EXPORTINGBUNDLE;
			children[4] = IMPORTINGBUNDLES;
			return children;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the packageState tree.");
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		refreshExportPackageTable();

		if (path.length == 1) // ./OSGi/<instance_id>/PackageState
			return new PackageStateMetaNode("PackageState root node.",
					MetaNode.PERMANENT, !PackageStateMetaNode.CAN_ADD,
					!PackageStateMetaNode.CAN_DELETE,
					!PackageStateMetaNode.ALLOW_ZERO,
					!PackageStateMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/<instance_id>/PackageState/...
			return new PackageStateMetaNode("Exported Package <id> subtree",
					MetaNode.AUTOMATIC, !PackageStateMetaNode.CAN_ADD,
					!PackageStateMetaNode.CAN_DELETE,
					PackageStateMetaNode.ALLOW_ZERO,
					!PackageStateMetaNode.ALLOW_INFINITE);

		if (path.length == 3) { // ./OSGi/<instance_id>/PackageState/<id>/...
			if (path[2].equals(EXPORTINGBUNDLE))
				return new PackageStateMetaNode("ID of ExportingBundles.",
						!PackageStateMetaNode.CAN_DELETE,
						!PackageStateMetaNode.CAN_REPLACE,
						!PackageStateMetaNode.ALLOW_ZERO,
						!PackageStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(IMPORTINGBUNDLES))
				return new PackageStateMetaNode("ID of ImportingBundles.",
						!PackageStateMetaNode.CAN_DELETE,
						!PackageStateMetaNode.CAN_REPLACE,
						!PackageStateMetaNode.ALLOW_ZERO,
						!PackageStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(NAME))
				return new PackageStateMetaNode(
						"Name of the exported package.",
						!PackageStateMetaNode.CAN_DELETE,
						!PackageStateMetaNode.CAN_REPLACE,
						!PackageStateMetaNode.ALLOW_ZERO,
						!PackageStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(VERSION))
				return new PackageStateMetaNode(
						"Version of the exported package.",
						!PackageStateMetaNode.CAN_DELETE,
						!PackageStateMetaNode.CAN_REPLACE,
						!PackageStateMetaNode.ALLOW_ZERO,
						!PackageStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(REMOVALPENDING))
				return new PackageStateMetaNode(
						"RemovalPending of the exported package.",
						!PackageStateMetaNode.CAN_DELETE,
						!PackageStateMetaNode.CAN_REPLACE,
						!PackageStateMetaNode.ALLOW_ZERO,
						!PackageStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the packageState tree.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		refreshExportPackageTable();
		return getNodeValue(nodePath).getSize();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		refreshExportPackageTable();
		if (isLeafNode(nodePath))
			return PackageStateMetaNode.LEAF_MIME_TYPE;

		return PackageStateMetaNode.PACKAGESTATE_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);
		refreshExportPackageTable();

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (Integer.valueOf(path[1]).intValue() < exportPackageObjTable
					.size())
				return true;
		}

		if (path.length == 3) {
			if (path[2].equals(NAME) || path[2].equals(VERSION)
					|| path[2].equals(REMOVALPENDING)
					|| path[2].equals(EXPORTINGBUNDLE)
					|| path[2].equals(IMPORTINGBUNDLES))
				return true;
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		refreshExportPackageTable();

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			if (path[2].equals(NAME) || path[2].equals(VERSION)
					|| path[2].equals(REMOVALPENDING)
					|| path[2].equals(EXPORTINGBUNDLE)
					|| path[2].equals(IMPORTINGBUNDLES))
				return true;
		}
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		refreshExportPackageTable();

		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			ExportedPackage exportedPackage = (ExportedPackage) exportPackageObjTable
					.get(path[1]);

			if (path[2].equals(NAME))
				return new DmtData(exportedPackage.getName());

			if (path[2].equals(VERSION))
				return new DmtData(exportedPackage.getVersion().toString());

			if (path[2].equals(REMOVALPENDING))
				return new DmtData(exportedPackage.isRemovalPending());

			if (path[2].equals(EXPORTINGBUNDLE)) {
				return new DmtData(Long.toString(exportedPackage
						.getExportingBundle().getBundleId()));
			}

			if (path[2].equals(IMPORTINGBUNDLES)) {
				Bundle[] importingBundles = exportedPackage
						.getImportingBundles();
				if(importingBundles.length==0)
					return new DmtData("");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < importingBundles.length; i++) {
					sb.append(Long.toString(importingBundles[i].getBundleId()));
					sb.append(",");
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 1);
				return new DmtData(result.toString());
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the packageState object.");
	}

	// ----- Utilities -----//

	protected String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = 3;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}

	private Hashtable manageExportPackages() {
		Bundle bundle = null;
		ExportedPackage[] exportPackages = packageAdmin
				.getExportedPackages(bundle);
		Hashtable ept = new Hashtable();
		for (int i = 0; i < exportPackages.length; i++) {
			String exportedPkg = exportPackages[i].getName() + ":"
					+ exportPackages[i].getVersion().toString();
			ept.put(exportedPkg, Integer.toString(i));
			this.exportPackageObjTable.put(Integer.toString(i),
					exportPackages[i]);
		}
		return ept;
	}

	private Hashtable manageExportPackages2() {
		Bundle bundle = null;
		ExportedPackage[] exportPackages = packageAdmin
				.getExportedPackages(bundle);
		Hashtable ept = new Hashtable();
		for (int i = 0; i < exportPackages.length; i++) {
			String exportedPkg = exportPackages[i].getName() + ":"
					+ exportPackages[i].getVersion().toString();
			ept.put(exportedPkg, Integer.toString(i));
		}
		return ept;
	}

	public void refreshExportPackageTable() {
		if (exportPackageTable.size() == 0) {
			exportPackageTable = manageExportPackages();
			Bundle bundle = null;
			lastId = packageAdmin.getExportedPackages(bundle).length - 1;
		}
		Bundle bundle = null;
		ExportedPackage[] exportPackages = packageAdmin
				.getExportedPackages(bundle);
		for (int i = 0; i < exportPackages.length; i++) {
			String exportedPkg = exportPackages[i].getName() + ":"
					+ exportPackages[i].getVersion().toString();
			if (!exportPackageTable.containsKey(exportedPkg)) {
				lastId++;
				exportPackageTable.put(exportedPkg, Integer.toString(lastId));
				this.exportPackageObjTable.put(Integer.toString(lastId),
						exportPackages[i]);
			}
		}
		Hashtable exportPackageTableComparison = manageExportPackages2();
		for (Enumeration keys = exportPackageTable.keys(); keys
				.hasMoreElements();) {
			String key = (String) keys.nextElement();
			if (!exportPackageTableComparison.containsKey(key)) {
				exportPackageObjTable.remove((String) exportPackageTable
						.get(key));
				exportPackageTable.remove(key);
			}
		}
	}
}
