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

import java.util.*;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;
/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class BundleResourcesReadOnlySession implements ReadableDataSession {
	private BundleContext context;
	private Hashtable bundlesTable = new Hashtable();

	// private Hashtable bundlesTableComparison = new Hashtable();

	BundleResourcesReadOnlySession(BundleResourcesPlugin plugin,
			BundleContext context) {
		this.context = context;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath,3);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		// .../BundleResouces/
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

		// .../BundleResouces/<bundle_id>/
		if (path.length >= 2) {
			String[] children = getResorcesChildren(path);
			return children;
		}

		// other case
		String[] children = new String[0];
		return children;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath,3);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		if (path.length == 1) // ./OSGi/<instance_id>/BundleResources
			return new BundleResourcesMetaNode("BundleResources root node.",
					MetaNode.PERMANENT, !BundleResourcesMetaNode.CAN_ADD,
					!BundleResourcesMetaNode.CAN_DELETE,
					!BundleResourcesMetaNode.ALLOW_ZERO,
					!BundleResourcesMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/<instance_id>/BundleResources/<bundle_id>
			return new BundleResourcesMetaNode("<bundle_id> subtree",
					MetaNode.AUTOMATIC, !BundleResourcesMetaNode.CAN_ADD,
					!BundleResourcesMetaNode.CAN_DELETE,
					BundleResourcesMetaNode.ALLOW_ZERO,
					BundleResourcesMetaNode.ALLOW_INFINITE);

		if (path.length >= 3) { // ./OSGi/<instance_id>/BundleResources/<bundle_id>/...
			if (isNodeUri(nodePath)) {
				if (!isLeafNode(nodePath))
					return new BundleResourcesMetaNode("BundleResources subtree.",
							MetaNode.AUTOMATIC,
							!BundleResourcesMetaNode.CAN_ADD,
							!BundleResourcesMetaNode.CAN_DELETE,
							!BundleResourcesMetaNode.ALLOW_ZERO,
							!BundleResourcesMetaNode.ALLOW_INFINITE);
				if (isLeafNode(nodePath))
					return new ServiceStateMetaNode(
							"The SymbolicName of the bundle.",
							!BundleResourcesMetaNode.CAN_DELETE,
							!BundleResourcesMetaNode.CAN_REPLACE,
							BundleResourcesMetaNode.ALLOW_ZERO,
							BundleResourcesMetaNode.ALLOW_INFINITE,
							DmtData.FORMAT_BASE64, null);
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the BundelResources tree.");
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
			return BundleResourcesMetaNode.LEAF_MIME_TYPE;

		return BundleResourcesMetaNode.BUNDLERESOURCES_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath,3);
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

		if (path.length >= 3) {
			String[] resourcePathArray = shapedPath(path,2);
			String resourcePath = pathConversionToUri(resourcePathArray);
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle != null) {
				String rootPath = bundle.getEntry("/").toString();
				for (Enumeration enumeration = bundle.findEntries("/", "*",
						true); enumeration.hasMoreElements();) {
					URL url = (URL) enumeration.nextElement();
					String stringUrl = url.toString();
					String insideJarPath = this.replaceAll(stringUrl, rootPath,
							"/");
					if (insideJarPath.startsWith(resourcePath)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath,3);
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if (path.length <= 2)
			return false;
		
		if (path.length >= 3) {
			String[] preResourcePath = shapedPath(nodePath,5);
			String resourcePath = pathConversionToUri(preResourcePath);
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			
			if (bundle != null) {
				URL url = bundle.getResource(resourcePath);
				try {
					InputStream in = url.openStream();
					byte[] b = new byte[300000];
					int i = in.read(b);
					if (i==-1){
						if(Util.DEBUG)
							Util.log("Fail to read the file @ isLeafNode().");
						return false;
					}
				}catch (NullPointerException ne) {
					//if(url!=null)
					//System.out.println("######"+url.toString());
					//System.out.println("######"+resourcePath);
				}catch (IOException e) {
					return false;
				}
			}
			if(Util.DEBUG)
				Util.log("Succeed to read the file @ isLeafNode().");
			return true;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
		"No such node defined in the BundelResources tree.");
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath,3);
		byte[] result = {};
		try {
			refreshBundlesTable();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		if (path.length >= 3) {
			if (!isLeafNode(nodePath))
				throw new DmtException(nodePath,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The given path indicates an interior node.");
			
			String[] preResourcePath = shapedPath(nodePath,5);
			String resourcePath = pathConversionToUri(preResourcePath);
			Bundle bundle = (Bundle) bundlesTable.get(path[1]);
			if (bundle == null)
				throw new DmtException("./OSGi/1/BundleResources"
						+ resourcePath, DmtException.NODE_NOT_FOUND,
						"No such node in the current BundleResorces tree.");
			if (bundle != null) {
				URL url = bundle.getResource(resourcePath);
				try {
					InputStream in = url.openStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
					String line;
					String fileResource = "";
					while((line = br.readLine()) != null){
						fileResource = fileResource + line;
					}
					result = Base64.base64ToByteArray(fileResource);
					}catch (IOException e) {
					throw new DmtException("./OSGi/1/BundleResources"
							+ resourcePath, DmtException.NODE_NOT_FOUND,
							"No such node in the current BundleResorces tree.");
				}
			}
			return new DmtData(result,true);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the BundleResorces object.");
	}

	// ----- Utilities -----//

	private String[] getResorcesChildren(String[] path) throws DmtException {
		String[] resourcePathArray = shapedPath(path,2);
		String resourcePath = pathConversionToUri(resourcePathArray)+"/";
		Hashtable list = new Hashtable();
		Bundle bundle = (Bundle) bundlesTable.get(path[1]);
		if (bundle == null) {
			throw new DmtException("./OSGi/1/BundleResources" + resourcePath,
					DmtException.NODE_NOT_FOUND,
					"No such node in the current BundleResorces tree.");
		}
		String rootPath = bundle.getEntry("/").toString();
		for (Enumeration enumeration = bundle.findEntries("/", "*", true); enumeration
				.hasMoreElements();) {
			URL url = (URL) enumeration.nextElement();
			String stringUrl = url.toString();
			String insideJarPath = this.replaceAll(stringUrl, rootPath, "/");
			if (insideJarPath.startsWith(resourcePath)
					&& insideJarPath.length() != resourcePath.length()) {
				int p = insideJarPath.indexOf("/", resourcePath.length());
				if (p == -1) {
					String elem = insideJarPath
							.substring(resourcePath.length());
					list.put(elem, "");
				} else {
					String elem = insideJarPath.substring(
							resourcePath.length(), insideJarPath.indexOf("/",
									resourcePath.length()));
					list.put(elem, "");
				}
			}
		}

		String[] children = new String[list.size()];
		int s = 0;
		for (Enumeration enumeration = list.keys(); enumeration
				.hasMoreElements(); s++) {
			children[s] = (String) enumeration.nextElement();
		}
		if (children.length == 0)
			throw new DmtException("./OSGi/1/BundleResources" + resourcePath,
					DmtException.NODE_NOT_FOUND,
					"There are no children in the current BundleResorces sub-tree.");
		return children;
	}

	private String pathConversionToUri(String[] resourcePathArray) {
		String resourcePath = "";
		for (int i = 0; i < resourcePathArray.length; i++) {
			resourcePath = resourcePath.concat("/");
			resourcePath = resourcePath.concat(resourcePathArray[i]);
		}
		return resourcePath;
	}
	
	private String replaceAll(String value, String old_str, String new_str) {
		if (value == null || old_str == null || "".equals(old_str)) {
			return value;
		}
		StringBuffer ret = new StringBuffer();
		int old_len = old_str.length();
		int from_index = 0;
		int index = 0;
		boolean loop_flg = true;
		while (loop_flg) {
			index = value.indexOf(old_str, from_index);
			if (-1 < index) {
				ret.append(value.substring(from_index, index));
				ret.append(new_str);
				from_index = index + old_len;
			} else {
				ret.append(value.substring(from_index));
				loop_flg = false;
			}
		}
		return ret.toString();
	}

	private String[] shapedPath(String[] nodePath,int sp) {
		int size = nodePath.length;
		int srcPos = sp;
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

	private void refreshBundlesTable() throws InvalidSyntaxException {
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
