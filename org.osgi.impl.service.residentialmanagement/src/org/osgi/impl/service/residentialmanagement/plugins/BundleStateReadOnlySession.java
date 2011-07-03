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

import info.dmtree.DmtConstants;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;
import java.util.*;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.*;
import org.osgi.service.startlevel.*;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class BundleStateReadOnlySession implements ReadableDataSession,
		SynchronousBundleListener {

	private static final String ID = "ID";
	private static final String SYMBOLICNAME = "SymbolicName";
	private static final String VERSION = "Version";
	private static final String BUNDLETYPE = "BundleType";
	private static final String MANIFEST = "Manifest";
	private static final String LOCATION = "Location";

	private static final String STATUS = "Status";
	private static final String STATE = "State";
	private static final String STARTLEVEL = "StartLevel";
	private static final String PERSISTENTLYSTARTED = "PersistentlyStarted";
	private static final String ACTIVATIONPOLICYUSED = "ActivationPolicyUsed";
	private static final String LASTMODIFIED = "LastModified";

	private static final String HOSTS = "Hosts";
	private static final String FRAGMENTS = "Fragments";
	private static final String REQUIRED = "Required";
	private static final String REQUIRING = "Requiring";
	private static final String TRUSTEDSIGNERCERTIFICATE = "TrustedSignerCertificate";
	private static final String NONTRUSTEDSIGNERCERTIFICATE = "NonTrustedSignerCertificate";
	private static final String CERTIFICATECHAIN = "CertificateChain";

	private static final String LIST_MIME_TYPE = DmtConstants.DDF_LIST_SUBTREE;
	private static final String NODE_TYPE = "org.osgi/1.0/BundleStateManagementObject";

	private BundleContext context;
	private PackageAdmin packageAdmin;
	private StartLevel startLevel;

	/* <String bundleID + 1, Bundle bundle> */
	private Hashtable bundlesTable = new Hashtable();
	/* <String bundleID + 1, List<Stirng n, Long bundleId>> */
	private Hashtable hostBundleList = new Hashtable();
	/* <String bundleID + 1, List<Stirng n, Long bundleId>> */
	private Hashtable fragmentsBundleList = new Hashtable();
	/* <String bundleID + 1, List<Stirng n, Long bundleId>> */
	private Hashtable requiredBundleList = new Hashtable();
	/* <String bundleID + 1, List<Stirng n, Long bundleId>> */
	private Hashtable requiringBundleList = new Hashtable();
	/* <String bundleID + 1, List<Stirng id, List<Stirng n, String DN>>> */
	private Hashtable trustedSignerCertificateList = new Hashtable();
	/* <String bundleID + 1, List<Stirng id, List<Stirng n, String DN>>> */
	private Hashtable nonTrustedSignerCertificateList = new Hashtable();
	private boolean managedFlag = false;

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

		// .../BundleState/
		if (path.length == 1) {
			if (bundlesTable.size() == 0) {
				return new String[0];
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
			String[] children = new String[13];
			children[0] = STATUS;
			children[1] = HOSTS;
			children[2] = FRAGMENTS;
			children[3] = REQUIRED;
			children[4] = REQUIRING;
			children[5] = TRUSTEDSIGNERCERTIFICATE;
			children[6] = SYMBOLICNAME;
			children[7] = VERSION;
			children[8] = BUNDLETYPE;
			children[9] = MANIFEST;
			children[10] = NONTRUSTEDSIGNERCERTIFICATE;
			children[11] = ID;
			children[12] = LOCATION;
			return children;
		}

		// .../BundleState/<bundle_id>/.../...
		if (path.length == 3) {
			if (path[2].equals(STATUS)) {
				String[] children = new String[5];
				children[0] = ACTIVATIONPOLICYUSED;
				children[1] = STATE;
				children[2] = STARTLEVEL;
				children[3] = PERSISTENTLYSTARTED;
				children[4] = LASTMODIFIED;
				return children;
			}
			if (path[2].equals(HOSTS)) {
				Hashtable table = (Hashtable) hostBundleList.get(path[1]);
				if (table == null)
					return new String[0];
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(FRAGMENTS)) {
				Hashtable table = (Hashtable) fragmentsBundleList.get(path[1]);
				if (table == null)
					return new String[0];
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(REQUIRED)) {
				Hashtable table = (Hashtable) requiredBundleList.get(path[1]);
				if (table == null)
					return new String[0];
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(REQUIRING)) {
				Hashtable table = (Hashtable) requiringBundleList.get(path[1]);
				if (table == null)
					return new String[0];					
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)) {
				Hashtable table = (Hashtable) trustedSignerCertificateList
						.get(path[1]);
				if (table == null)
					return new String[0];
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
				Hashtable table = (Hashtable) nonTrustedSignerCertificateList
						.get(path[1]);
				if (table == null)
					return new String[0];
				String[] children = new String[table.size()];
				int i = 0;
				for (Enumeration enu = table.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
		}
		if (path.length == 4) {
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
				String[] children = new String[1];
				children[0] = CERTIFICATECHAIN;
			}
		}
		if (path.length == 5) {
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)) {
				Hashtable idTable = (Hashtable) trustedSignerCertificateList
						.get(path[1]);
				Hashtable nTable = (Hashtable) idTable.get(path[3]);
				if (nTable.size() == 0)
					return new String[0];
				String[] children = new String[nTable.size()];
				int i = 0;
				for (Enumeration enu = nTable.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}
			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
				Hashtable idTable = (Hashtable) nonTrustedSignerCertificateList
						.get(path[1]);
				Hashtable nTable = (Hashtable) idTable.get(path[3]);
				if (nTable.size() == 0)
					return new String[0];
				String[] children = new String[nTable.size()];
				int i = 0;
				for (Enumeration enu = nTable.keys(); enu.hasMoreElements(); i++) {
					children[i] = (String) enu.nextElement();
				}
				return children;
			}

		}
		return new String[0];
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) // ./OSGi/<instance_id>/BundleState
			return new BundleStateMetaNode("BundleState root node.",
					MetaNode.PERMANENT, !BundleStateMetaNode.CAN_ADD,
					!BundleStateMetaNode.CAN_DELETE,
					!BundleStateMetaNode.ALLOW_ZERO,
					!BundleStateMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/<instance_id>/BundleState/<id>
			return new BundleStateMetaNode("<bundle_id> subtree",
					MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
					!BundleStateMetaNode.CAN_DELETE,
					BundleStateMetaNode.ALLOW_ZERO,
					BundleStateMetaNode.ALLOW_INFINITE);

		if (path.length == 3) { // ./OSGi/<instance_id>/BundleState/<id>/...
			if (path[2].equals(ID))
				return new BundleStateMetaNode("The BundleID of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(STATUS))
				return new BundleStateMetaNode("Status subtree.",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(HOSTS))
				return new BundleStateMetaNode("Host Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(FRAGMENTS))
				return new BundleStateMetaNode("Fragments Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(REQUIRED))
				return new BundleStateMetaNode("Required Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(REQUIRING))
				return new BundleStateMetaNode("Requiring Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(SYMBOLICNAME))
				return new BundleStateMetaNode(
						"The SymbolicName of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[2].equals(VERSION))
				return new BundleStateMetaNode("The Version of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[2].equals(BUNDLETYPE))
				return new BundleStateMetaNode("The type of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[2].equals(MANIFEST))
				return new BundleStateMetaNode("The Manifest of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[2].equals(LOCATION))
				return new BundleStateMetaNode("Bundle Location",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE))
				return new BundleStateMetaNode(
						"A trusted signer of the Bundle", MetaNode.AUTOMATIC,
						!BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE))
				return new BundleStateMetaNode(
						"A non trasted signer of the Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) { // ./OSGi/<instance_id>/BundleState/<id>/.../...

			if (path[3].equals(STATE))
				return new BundleStateMetaNode("The state of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[3].equals(STARTLEVEL))
				return new BundleStateMetaNode("The StartLevel of the bundle.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null, null);

			if (path[3].equals(PERSISTENTLYSTARTED))
				return new BundleStateMetaNode("PersistentlyStarted",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null, null);

			if (path[3].equals(LASTMODIFIED))
				return new BundleStateMetaNode("The date of last modified.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATETIME, null, null);

			if (path[3].equals(ACTIVATIONPOLICYUSED))
				return new BundleStateMetaNode("The date of last modified.",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null, null);

			if (path[2].equals(HOSTS))
				return new BundleStateMetaNode("bundle_id of Host Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(FRAGMENTS))
				return new BundleStateMetaNode("bundle_id of Fragments Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(REQUIRED))
				return new BundleStateMetaNode("bundle_id of Required Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(REQUIRING))
				return new BundleStateMetaNode("bundle_id of Requiring Bundle",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE))
				return new BundleStateMetaNode(
						"A trusted signer of the Bundle", MetaNode.AUTOMATIC,
						!BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE))
				return new BundleStateMetaNode(
						"A non trasted signer of the Bundle",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 5) { // ./OSGi/<instance_id>/BundleState/<id>/.../.../...
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN))
				return new BundleStateMetaNode("Certificate Chain",
						MetaNode.AUTOMATIC, !BundleStateMetaNode.CAN_ADD,
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 6) { // ./OSGi/<instance_id>/BundleState/<id>/TrustedSignerCertifications
								// or
								// NonTrustedSignerCertifications/<id>/CertificateChain/...
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN))
				return new BundleStateMetaNode("DN",
						!BundleStateMetaNode.CAN_DELETE,
						!BundleStateMetaNode.CAN_REPLACE,
						BundleStateMetaNode.ALLOW_ZERO,
						!BundleStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the ServiceState tree.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
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
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			return NODE_TYPE;

		if (path.length == 3) {
			if (path[2].equals(FRAGMENTS) || path[2].equals(HOSTS)
					|| path[2].equals(REQUIRED) || path[2].equals(REQUIRING))
				;
			return LIST_MIME_TYPE;
		}

		if (path.length == 5) {
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN))
				;
			return LIST_MIME_TYPE;
		}

		if (isLeafNode(nodePath))
			return BundleStateMetaNode.LEAF_MIME_TYPE;

		return BundleStateMetaNode.BUNDLESTATE_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

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
				if (path[2].equals(STATUS) || path[2].equals(HOSTS)
						|| path[2].equals(FRAGMENTS)
						|| path[2].equals(REQUIRED)
						|| path[2].equals(REQUIRING)
						|| path[2].equals(TRUSTEDSIGNERCERTIFICATE)
						|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
						|| path[2].equals(SYMBOLICNAME)
						|| path[2].equals(VERSION)
						|| path[2].equals(BUNDLETYPE)
						|| path[2].equals(LOCATION) || path[2].equals(ID)
						|| path[2].equals(MANIFEST))
					return true;
			}
		}

		if (path.length == 4) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {
				if (path[2].equals(STATUS)) {
					if (path[3].equals(ACTIVATIONPOLICYUSED)
							|| path[3].equals(STATE)
							|| path[3].equals(STARTLEVEL)
							|| path[3].equals(PERSISTENTLYSTARTED)
							|| path[3].equals(LASTMODIFIED))
						return true;
				}
				if (path[2].equals(HOSTS)) {
					Hashtable table = (Hashtable) hostBundleList.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(FRAGMENTS)) {
					Hashtable table = (Hashtable) fragmentsBundleList
							.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(REQUIRED)) {
					Hashtable table = (Hashtable) requiredBundleList
							.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(REQUIRING)) {
					Hashtable table = (Hashtable) requiringBundleList
							.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)) {
					Hashtable table = (Hashtable) trustedSignerCertificateList
							.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
					Hashtable table = (Hashtable) nonTrustedSignerCertificateList
							.get(path[1]);
					if (table.size() != 0)
						for (Enumeration enu = table.keys(); enu
								.hasMoreElements();) {
							if (path[3].equals((String) enu.nextElement()))
								return true;
						}
				}
			}

		}
		if (path.length == 5) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {
				if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
						&& path[4].equals(CERTIFICATECHAIN)
						|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
						&& path[4].equals(CERTIFICATECHAIN))
					return true;
			}
		}
		if (path.length == 6) {
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {
				if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)) {
					Hashtable idTable = (Hashtable) trustedSignerCertificateList
							.get(path[1]);
					Hashtable nTable = (Hashtable) idTable.get(path[3]);
					if (nTable.size() != 0)
						for (Enumeration enu = nTable.keys(); enu
								.hasMoreElements();) {
							if (path[5].equals((String) enu.nextElement()))
								return true;
						}
				}
				if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
					Hashtable idTable = (Hashtable) nonTrustedSignerCertificateList
							.get(path[1]);
					Hashtable nTable = (Hashtable) idTable.get(path[3]);
					if (nTable.size() != 0)
						for (Enumeration enu = nTable.keys(); enu
								.hasMoreElements();) {
							if (path[5].equals((String) enu.nextElement()))
								return true;
						}
				}
			}
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			if (path[2].equals(SYMBOLICNAME) || path[2].equals(VERSION)
					|| path[2].equals(BUNDLETYPE) || path[2].equals(LOCATION)
					|| path[2].equals(ID) || path[2].equals(MANIFEST))
				return true;
		}

		if (path.length == 4) {
			if (path[2].equals(STATUS)) {
				if (path[3].equals(ACTIVATIONPOLICYUSED)
						|| path[3].equals(STATE) || path[3].equals(STARTLEVEL)
						|| path[3].equals(PERSISTENTLYSTARTED)
						|| path[3].equals(LASTMODIFIED))
					return true;
			}
			if (path[2].equals(FRAGMENTS) || path[2].equals(HOSTS)
					|| path[2].equals(REQUIRED) || path[2].equals(REQUIRING))
				return true;
		}
		if (path.length == 6) {
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN)
					|| path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)
					&& path[4].equals(CERTIFICATECHAIN))
				return true;
		}

		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(ID)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				return new DmtData(targetBundle.getBundleId());
			}
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
			if (path[2].equals(LOCATION)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				return new DmtData(targetBundle.getLocation());
			}
		}

		if (path.length == 4) {
			if (path[2].equals(STATUS)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[1]);
				if (path[3].equals(ACTIVATIONPOLICYUSED)) {
					Dictionary headers = targetBundle.getHeaders();
					String ap = (String) headers
							.get(Constants.BUNDLE_ACTIVATIONPOLICY);
					if (ap != null && ap.equals("lazy"))
						return new DmtData(true);
					else
						return new DmtData(false);
				}
				if (path[3].equals(STATE)) {
					return new DmtData(targetBundle.getState());
				}
				if (path[3].equals(STARTLEVEL)) {
					return new DmtData(
							startLevel.getBundleStartLevel(targetBundle));
				}
				if (path[3].equals(PERSISTENTLYSTARTED)) {
					return new DmtData(
							startLevel
									.isBundlePersistentlyStarted(targetBundle));
				}
				if (path[3].equals(LASTMODIFIED)) {
					Date d = new Date(targetBundle.getLastModified());
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyyMMdd'T'hhmmss");
					String returnDate = sdf.format(d);
					return new DmtData(returnDate, DmtData.FORMAT_DATETIME);
				}
			}
			if (path[2].equals(HOSTS)) {
				Hashtable table = (Hashtable) hostBundleList.get(path[1]);
				if (table == null)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				Long lValue = (Long) table.get(path[3]);
				long value = lValue.longValue();
				return new DmtData(value);

			}
			if (path[2].equals(FRAGMENTS)) {
				Hashtable table = (Hashtable) fragmentsBundleList.get(path[1]);
				if (table == null)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				Long lValue = (Long) table.get(path[3]);
				long value = lValue.longValue();
				return new DmtData(value);
			}
			if (path[2].equals(REQUIRED)) {
				Hashtable table = (Hashtable) requiredBundleList.get(path[1]);
				if (table == null)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				Long lValue = (Long) table.get(path[3]);
				long value = lValue.longValue();
				return new DmtData(value);
			}
			if (path[2].equals(REQUIRING)) {
				Hashtable table = (Hashtable) requiringBundleList.get(path[1]);
				if (table == null)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				Long lValue = (Long) table.get(path[3]);
				long value = lValue.longValue();
				return new DmtData(value);
			}
		}
		if (path.length == 6) {
			if (path[2].equals(TRUSTEDSIGNERCERTIFICATE)) {
				Hashtable idTable = (Hashtable) trustedSignerCertificateList
						.get(path[1]);
				Hashtable nTable = (Hashtable) idTable.get(path[3]);
				if (nTable.size() == 0)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				String value = (String) nTable.get(path[5]);
				return new DmtData(value);
			}
			if (path[2].equals(NONTRUSTEDSIGNERCERTIFICATE)) {
				Hashtable idTable = (Hashtable) nonTrustedSignerCertificateList
						.get(path[1]);
				Hashtable nTable = (Hashtable) idTable.get(path[3]);
				if (nTable.size() == 0)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleState tree.");
				String value = (String) nTable.get(path[5]);
				return new DmtData(value);
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

	public void bundleChanged(BundleEvent event) {
		if (!this.managedFlag) {
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String id = Long.toString(bundles[i].getBundleId() + 1);
				this.bundlesTable.put(id, bundles[i]);
				manageHodsBundleList(id);
				manageFragmentsBundleList(id);
				manageRequiredBundleList(id);
				manageRequiringBundleList(id);
				manageTrustedSignerCertificateList(id);
				manageNonTrustedSignerCertificateList(id);
				this.managedFlag = true;
			}
			return;
		}
		Bundle bundle = event.getBundle();
		if (event.getType() == BundleEvent.INSTALLED
				|| event.getType() == BundleEvent.RESOLVED) {
			this.bundlesTable.put(Long.toString(bundle.getBundleId() + 1),
					bundle);
			Enumeration enu = bundlesTable.keys();
			while (enu.hasMoreElements()) {
				String id = (String) enu.nextElement();
				manageHodsBundleList(id);
				manageFragmentsBundleList(id);
				manageRequiredBundleList(id);
				manageRequiringBundleList(id);
				manageTrustedSignerCertificateList(id);
				manageNonTrustedSignerCertificateList(id);
			}

		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			String id = Long.toString(bundle.getBundleId() + 1);
			this.bundlesTable.remove(id);
			Enumeration enu = bundlesTable.keys();
			while (enu.hasMoreElements()) {
				manageHodsBundleList(id);
				manageFragmentsBundleList(id);
				manageRequiredBundleList(id);
				manageRequiringBundleList(id);
				manageTrustedSignerCertificateList(id);
				manageNonTrustedSignerCertificateList(id);
			}
		}
	}

	private void manageHodsBundleList(String id) {
		Hashtable list = new Hashtable();
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		Bundle[] hostBundles = packageAdmin.getHosts(targetBundle);
		if (hostBundles != null) {
			for (int i = 0; i < hostBundles.length; i++) {
				list.put(Integer.toString(i + 1),
						new Long(hostBundles[i].getBundleId()));
			}
			hostBundleList.put(id, list);
		} else if (targetBundle == null) {
			hostBundleList.remove(id);
		}
	}

	private void manageFragmentsBundleList(String id) {
		Hashtable list = new Hashtable();
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		Bundle[] fragmentBundles = packageAdmin.getFragments(targetBundle);
		if (fragmentBundles != null) {
			for (int i = 0; i < fragmentBundles.length; i++) {
				list.put(Integer.toString(i + 1),
						new Long(fragmentBundles[i].getBundleId()));
			}
			fragmentsBundleList.put(id, list);
		} else if (targetBundle == null) {
			fragmentsBundleList.remove(id);
		}
	}

	private void manageRequiredBundleList(String id) {
		Hashtable list = new Hashtable();
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		RequiredBundle[] requiredBundles = packageAdmin
				.getRequiredBundles(null);
		if (requiredBundles != null) {
			int k = 1;
			for (int i = 0; i < requiredBundles.length; i++) {
				Bundle[] requiringBundles = requiredBundles[i]
						.getRequiringBundles();
				if (requiringBundles != null) {
					if (requiredBundles[i].getBundle().getBundleId() == targetBundle
							.getBundleId()) {
						for (int j = 0; j < requiringBundles.length; j++) {
							list.put(Integer.toString(k), new Long(
									requiringBundles[j].getBundleId()));
							k++;
						}
					}
				}
			}
			requiredBundleList.put(id, list);
		} else if (targetBundle == null) {
			requiredBundleList.remove(id);
		}
	}

	private void manageRequiringBundleList(String id) {
		Hashtable list = new Hashtable();
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		RequiredBundle[] requiredBundles = packageAdmin
				.getRequiredBundles(null);
		if (requiredBundles != null) {
			int k = 1;
			for (int i = 0; i < requiredBundles.length; i++) {
				Bundle[] requiringBundles = requiredBundles[i]
						.getRequiringBundles();
				for (int j = 0; j < requiringBundles.length; j++) {
					if (requiringBundles[j].getBundleId() == targetBundle
							.getBundleId()) {
						list.put(Integer.toString(k), new Long(
								requiredBundles[i].getBundle().getBundleId()));
						k++;
					}
				}
			}
			requiringBundleList.put(id, list);
		} else if (targetBundle == null) {
			requiringBundleList.remove(id);
		}
	}

	private void manageTrustedSignerCertificateList(String id) {
		Hashtable certList = new Hashtable();// <<id>, nList>
		Hashtable nList = new Hashtable();// <<n>, DN>
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		Map signers = targetBundle
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Iterator it = signers.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signers.get(cert);
			Iterator itCert = certificateChane.iterator();
			for (int j = 0; itCert.hasNext(); j++) {
				X509Certificate certs = (X509Certificate) itCert.next();
				Principal pri = certs.getIssuerDN();
				String name = pri.getName();
				nList.put(Integer.toString(j + 1), name);
			}
			certList.put(Integer.toString(i + 1), nList);
		}
		trustedSignerCertificateList.put(id, certList);
	}

	private void manageNonTrustedSignerCertificateList(String id) {
		Hashtable certList = new Hashtable();// <<id>, nList>
		Hashtable nList = new Hashtable();// <<n>, DN>
		Bundle targetBundle = (Bundle) bundlesTable.get(id);
		Map signers = targetBundle.getSignerCertificates(Bundle.SIGNERS_ALL);
		Map signersTrusted = targetBundle
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Iterator itPre = signersTrusted.keySet().iterator();
		for (int i = 0; itPre.hasNext(); i++) {
			signers.remove(itPre.next());
		}
		Iterator it = signers.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signers.get(cert);
			Iterator itCert = certificateChane.iterator();
			for (int j = 0; itCert.hasNext(); j++) {
				X509Certificate certs = (X509Certificate) itCert.next();
				Principal pri = certs.getIssuerDN();
				String name = pri.getName();
				nList.put(Integer.toString(j + 1), name);
			}
			certList.put(Integer.toString(i + 1), nList);
		}
		nonTrustedSignerCertificateList.put(id, certList);
	}
}
