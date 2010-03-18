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

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;
import java.util.*;
import java.text.SimpleDateFormat;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.*;
import org.osgi.service.startlevel.*;
/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class BundleStateReadOnlySession implements ReadableDataSession {

	private static final String STATUS = "Status";
	private static final String HOST = "Host";
	private static final String BUNDLESTATEEXT = "BundleStateExt";
	private static final String FRAGMENTS = "Fragments";
	private static final String REQUIRED = "Required";
	private static final String REQUIRING = "Requiring";
	private static final String SYMBOLICNAME = "SymbolicName";
	private static final String VERSION = "Version";
	private static final String BUNDLETYPE = "BundleType";
	private static final String LOCATION = "Location";
	private static final String STATE = "State";
	private static final String STARTLEVEL = "StartLevel";
	private static final String PERSISTENTLYSTARTED = "PersistentlyStarted";
	private static final String LASTMODIFIED = "LastModified";
	private static final String MANIFEST = "Manifest";
	private static final String TRUSTEDSIGNERCERTIFICATIONS = "TrustedSignerCertifications";
	private static final String NONTRUSTEDSIGNERCERTIFICATIONS = "NonTrustedSignerCertifications";

	private BundleContext context;
	private Hashtable bundlesTable = new Hashtable();
	private PackageAdmin packageAdmin;
	private StartLevel startLevel;

	BundleStateReadOnlySession(BundleStatePlugin bundleStatePlugin,
			BundleContext context) {
		this.context = context;
		ServiceReference pkgServiceRef = context
				.getServiceReference(PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) context.getService(pkgServiceRef);
		ServiceReference slvServiceRef = context
				.getServiceReference(StartLevel.class.getName());
		startLevel = (StartLevel) context.getService(slvServiceRef);

	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		// .../BundleState/
		if (path.length == 1) {
			if (bundlesTable.size() == 0) {
				String[] children = new String[1];
				children[0] = "";
				return children;
			}
			String[] children = new String[bundlesTable.size()];
			int i = 0;
			for (Enumeration keys = bundlesTable.keys(); keys.hasMoreElements(); i++) {
				children[i] = (String) keys.nextElement();
			}
			return children;
		}

		// .../BundleState/<bundle_id>/...
		if (path.length == 2) {
			String[] children = new String[12];
			children[0] = STATUS;
			children[1] = HOST;
			children[2] = BUNDLESTATEEXT;
			children[3] = FRAGMENTS;
			children[4] = REQUIRED;
			children[5] = REQUIRING;
			children[6] = TRUSTEDSIGNERCERTIFICATIONS;
			children[7] = SYMBOLICNAME;
			children[8] = VERSION;
			children[9] = BUNDLETYPE;
			children[10] = MANIFEST;
			children[11] = NONTRUSTEDSIGNERCERTIFICATIONS;
			return children;
		}

		// .../BundleState/<bundle_id>/.../...
		if (path.length == 3) {
			if (path[2].equals(STATUS)) {
				String[] children = new String[5];
				children[0] = LOCATION;
				children[1] = STATE;
				children[2] = STARTLEVEL;
				children[3] = PERSISTENTLYSTARTED;
				children[4] = LASTMODIFIED;
				return children;
			}
		}
		// other case
		String[] children = new String[1];
		children[0] = "";
		return children;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		if (path.length == 1) // ./OSGi/<instance_id>/BundleState
			return new BundleStateMetaNode("BundleState root node.",
					MetaNode.PERMANENT, !BundleStateMetaNode.CAN_ADD,
					!BundleStateMetaNode.CAN_DELETE,
					!BundleStateMetaNode.ALLOW_ZERO,
					!BundleStateMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/<instance_id>/BundleState/<bundle_id>
			return new BundleStateMetaNode("<bundle_id> subtree",
					MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
					!BundleStateMetaNode.CAN_DELETE,
					BundleStateMetaNode.ALLOW_ZERO,
					BundleStateMetaNode.ALLOW_INFINITE);

		if (path.length == 3) { // ./OSGi/<instance_id>/BundleState/<bundle_id>/...
			if (path[2].equals(STATUS))
				return new BundleStateMetaNode("Status subtree.",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);
			
			if (path[2].equals(HOST))
				return new ServiceStateMetaNode("bundle_id of Host Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(FRAGMENTS))
				return new ServiceStateMetaNode(
						"bundle_id of Fragments Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(REQUIRED))
				return new ServiceStateMetaNode("bundle_id of Required Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(REQUIRING))
				return new ServiceStateMetaNode(
						"bundle_id of Requiring Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(BUNDLESTATEEXT))
				return new BundleStateMetaNode(
						"BundleStatusExtension subtree.", MetaNode.AUTOMATIC,
						!BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(SYMBOLICNAME))
				return new ServiceStateMetaNode(
						"The SymbolicName of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(VERSION))
				return new ServiceStateMetaNode("The Version of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[2].equals(BUNDLETYPE))
				return new ServiceStateMetaNode("The type of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(MANIFEST))
				return new ServiceStateMetaNode("The Manifest of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATIONS))
				return new ServiceStateMetaNode("A trusted signer of the Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATIONS))
				return new ServiceStateMetaNode("A non trasted signer of the Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
		}

		if (path.length == 4) { // ./OSGi/<instance_id>/BundleState/<bundle_id>/.../...
			if (path[3].equals(LOCATION))
				return new ServiceStateMetaNode("Bundle Location",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(STATE))
				return new ServiceStateMetaNode("The state of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(STARTLEVEL))
				return new ServiceStateMetaNode(
						"The StartLevel of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(PERSISTENTLYSTARTED))
				return new ServiceStateMetaNode("PersistentlyStarted",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[3].equals(LASTMODIFIED))
				return new ServiceStateMetaNode("The date of last modified.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATE, null);
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the ServiceState tree.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
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
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if (isLeafNode(nodePath))
			return BundleStateMetaNode.LEAF_MIME_TYPE;

		return BundleStateMetaNode.BUNDLESTATE_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null)
				return true;
		}

		if (path.length == 3) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {

				if (path[2].equals(STATUS) || path[2].equals(HOST)
						|| path[2].equals(BUNDLESTATEEXT)
						|| path[2].equals(FRAGMENTS)
						|| path[2].equals(REQUIRED)
						|| path[2].equals(REQUIRING)
						|| path[2].equals(TRUSTEDSIGNERCERTIFICATIONS)
						|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATIONS)
						|| path[2].equals(SYMBOLICNAME)
						|| path[2].equals(VERSION)
						|| path[2].equals(BUNDLETYPE)
						|| path[2].equals(MANIFEST))
					return true;

			}
		}

		if (path.length == 4) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {
				if (path[2].equals(STATUS)) {
					if (path[3].equals(LOCATION)
							|| path[3].equals(STATE)
							|| path[3].equals(STARTLEVEL)
							|| path[3].equals(PERSISTENTLYSTARTED)
							|| path[3].equals(LASTMODIFIED))
						return true;
				}
			}
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			if (path[2].equals(SYMBOLICNAME) 
					|| path[2].equals(VERSION)
					|| path[2].equals(BUNDLETYPE) 
					|| path[2].equals(MANIFEST)
					|| path[2].equals(HOST)
					|| path[2].equals(FRAGMENTS)
					|| path[2].equals(REQUIRED)
					|| path[2].equals(REQUIRING)
					|| path[2].equals(TRUSTEDSIGNERCERTIFICATIONS)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATIONS))
				return true;
		}

		if (path.length == 4) {
			if (path[2].equals(STATUS)) {
				if (path[3].equals(LOCATION) || path[3].equals(STATE)
						|| path[3].equals(STARTLEVEL)
						|| path[3].equals(PERSISTENTLYSTARTED)
						|| path[3].equals(LASTMODIFIED))
					return true;
			}
		}
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(SYMBOLICNAME)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				return new DmtData(targetBundle.getSymbolicName());
			}
			if (path[2].equals(VERSION)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				return new DmtData(targetBundle.getVersion().toString());
			}
			if (path[2].equals(BUNDLETYPE)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				return new DmtData(packageAdmin.getBundleType(targetBundle));
			}
			if (path[2].equals(MANIFEST)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				Dictionary headers = targetBundle.getHeaders();
				StringBuffer sb = new StringBuffer();
				for (Enumeration keys = headers.elements(); keys
						.hasMoreElements();) {
					String header = (String) keys.nextElement();
					sb.append(header);
					sb.append("\n");
				}
				return new DmtData(sb.toString());
			}
			if (path[2].equals(HOST)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				Bundle[] hostBundles = packageAdmin.getHosts(targetBundle);
				if (hostBundles == null) {
					return new DmtData("");
				}
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < hostBundles.length; i++) {
					sb.append(Long.toString(hostBundles[i].getBundleId()));
					sb.append(",");
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 1);
				return new DmtData(result.toString());
			}
			if (path[2].equals(FRAGMENTS)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				Bundle[] fragmentBundles = packageAdmin
						.getFragments(targetBundle);
				if (fragmentBundles == null) {
					return new DmtData("");
				}
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < fragmentBundles.length; i++) {
					sb.append(Long.toString(fragmentBundles[i].getBundleId()));
					sb.append(",");
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 1);
				return new DmtData(result.toString());
			}
			if (path[2].equals(REQUIRED)) {
				Hashtable list = new Hashtable();
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				RequiredBundle[] requiredBundles = packageAdmin.getRequiredBundles(null);
				if (requiredBundles == null) {
					return new DmtData("");
				}
				for (int i = 0; i < requiredBundles.length; i++) {
					Bundle[] requiringBundles = requiredBundles[i]
							.getRequiringBundles();
					if (requiringBundles == null)
						continue;
					for (int j = 0; j < requiringBundles.length; j++) {
						if (requiredBundles[i].getBundle().getBundleId()==targetBundle.getBundleId())
							list.put(Long.toString(requiringBundles[j].getBundleId()), "");
					}
				}
				if(list.size()==0)
					return new DmtData("");
				StringBuffer sb = new StringBuffer();
				for (Enumeration enumeration = list.keys(); enumeration
						.hasMoreElements();) {
					sb.append((String)enumeration.nextElement());
					sb.append(",");
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 1);
				return new DmtData(result.toString());
			}
			if (path[2].equals(REQUIRING)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);//B8
				RequiredBundle[] requiredBundles = packageAdmin.getRequiredBundles(null);
				if (requiredBundles == null)
					return new DmtData("");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < requiredBundles.length; i++) {
					Bundle[] requiringBundles = requiredBundles[i].getRequiringBundles();
					for (int j = 0; j < requiringBundles.length; j++) {
						if(requiringBundles[j].getBundleId()==targetBundle.getBundleId()){
							sb.append(Long.toString(requiredBundles[i].getBundle().getBundleId()));
							sb.append(",");
						}
					}
				}
				if (sb.length() == 0)
					return new DmtData("");
				StringBuffer result = sb.deleteCharAt(sb.length() - 1);
				return new DmtData(result.toString());
			}
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATIONS)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				Map signers = targetBundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
				Iterator it = signers.keySet().iterator();
				StringBuffer sb = new StringBuffer();
				sb.append("<");
				while(it.hasNext()){
					String signer = (String)it.next();
					sb.append((String)signers.get(signer));
					sb.append(">,<");
				}
				if(sb.length()==1){
					StringBuffer result = sb.deleteCharAt(sb.length() - 1);
					return new DmtData(result.toString());
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 2);
				return new DmtData(result.toString());
			}
			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATIONS)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				Map signersTrust = targetBundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
				Map signersAll = targetBundle.getSignerCertificates(Bundle.SIGNERS_ALL);
				Iterator iTrust = signersTrust.keySet().iterator();
				while(iTrust.hasNext()){
					signersAll.remove(iTrust.next());
				}
				Iterator it = signersAll.keySet().iterator();
				StringBuffer sb = new StringBuffer();
				sb.append("<");
				while(it.hasNext()){
					String signer = (String)it.next();
					sb.append((String)signersAll.get(signer));
					sb.append(">,<");
				}
				if(sb.length()==1){
					StringBuffer result = sb.deleteCharAt(sb.length() - 1);
					return new DmtData(result.toString());
				}
				StringBuffer result = sb.deleteCharAt(sb.length() - 2);
				return new DmtData(result.toString());
			}
		}

		if (path.length == 4) {
			if (path[2].equals(STATUS)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				if (path[3].equals(LOCATION)) {
					return new DmtData(targetBundle.getLocation());
				}
				if (path[3].equals(STATE)) {
					return new DmtData(targetBundle.getState());
				}
				if (path[3].equals(STARTLEVEL)) {
					return new DmtData(startLevel
							.getBundleStartLevel(targetBundle));
				}
				if (path[3].equals(PERSISTENTLYSTARTED)) {
					return new DmtData(startLevel
							.isBundlePersistentlyStarted(targetBundle));
				}
				if (path[3].equals(LASTMODIFIED)) {
					Date d = new Date(targetBundle.getLastModified());
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
					int i = Integer.parseInt(sdf1.format(d).substring(0, 2)) + 1;
					String CC = Integer.toString(i);
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMdd");
					String returnDate = CC + sdf2.format(d);
					return new DmtData(returnDate, DmtData.FORMAT_DATE);
				}
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the BundleState object.");
	}

	// ----- Utilities -----//
	private String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = 3;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}

	private Hashtable manageBundles() throws InvalidSyntaxException {
		Bundle[] bundles = context.getBundles();
		Hashtable bundlesTable = new Hashtable();
		for (int i = 0; i < bundles.length; i++) {
			long bundleIdLong = bundles[i].getBundleId();
			String bundleId = Long.toString(bundleIdLong);
			bundlesTable.put(bundleId, bundles[i]);
		}
		return bundlesTable;
	}

	public void refreshBundlesTable() throws InvalidSyntaxException {
		if (bundlesTable.size() == 0) {
			bundlesTable = manageBundles();
		}
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			long bundleIdLong = bundles[i].getBundleId();
			String bundleId = Long.toString(bundleIdLong);
			if (!bundlesTable.containsKey(bundleId)) {
				this.bundlesTable.put(bundleId, bundles[i]);
			}
		}
	}
}