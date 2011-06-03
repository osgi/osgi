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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class ServiceStateReadOnlySession implements ReadableDataSession {

	private static final String SERVICESTATE = "ServiceState";
	private static final String PROPERTY = "Property";
	private static final String REGISTERINGBUNDLE = "RegisteringBundle";
	private static final String USINGBUNDLES = "UsingBundles";
	private static final String KEY = "Key";
	private static final String VALUES = "Values";
	private static final String TYPE = "Type";
	private static final String CARDINALITY = "Cardinality";
	private static final String SCALAR = "scalar";
	private static final String ARRAY = "array";
	private static final String COLLECTION = "collection";
	private static final String LIST_MIME_TYPE = "org.osgi/1.0/ListSubtree";
	private static final String NODE_TYPE = "org.osgi/1.0/ServiceStateManagementObject";

	private Hashtable serviceRefTable = new Hashtable();/*
														 * <String <service_id>,
														 * ServiceReference
														 * serviceRef>
														 */
	private Hashtable keysList = new Hashtable();
	private Hashtable valuesList = new Hashtable();
	private Hashtable usingBundlesTable = new Hashtable();

	ServiceStateReadOnlySession(ServiceStatePlugin plugin, BundleContext context) {
		try {
			Filter filter = context.createFilter("(" + Constants.OBJECTCLASS
					+ "=*)");
			ServiceStateServiceTracker smt = new ServiceStateServiceTracker(
					context, filter);
			smt.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1) {
			String[] children = new String[serviceRefTable.size()];
			int i = 0;
			for (Enumeration keys = serviceRefTable.keys(); keys
					.hasMoreElements(); i++) {
				children[i] = (String) keys.nextElement();
			}
			return children;
		}

		if (path.length == 2) {
			String[] children = new String[3];
			children[0] = USINGBUNDLES;
			children[1] = PROPERTY;
			children[2] = REGISTERINGBUNDLE;
			return children;
		}

		if (path.length == 3 && path[2].equals(USINGBUNDLES)) {
			manageUsingBundlesTable();
			Hashtable table = (Hashtable) usingBundlesTable.get(path[1]);
			if (table.size() == 0) {
				return new String[0];
			}
			// throw new DmtException(nodePath,
			// DmtException.NODE_NOT_FOUND,"No such node in the current serviceState tree.");
			String[] children = new String[table.size()];
			int i = 0;
			for (Enumeration enu = table.keys(); enu.hasMoreElements();) {
				children[i] = (String) enu.nextElement();
				i++;
			}
			return children;
		}

		if (path.length == 3 && path[2].equals(PROPERTY)) {
			ServiceReference serviceRef = (ServiceReference) serviceRefTable
					.get(path[1]);
			if (serviceRef == null) {
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"No such node in the current serviceState tree.");
			}
			if (path[2].equals(PROPERTY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				String[] keyIds = new String[keyList.size()];
				Enumeration enu = keyList.keys();
				for (int i = 0; enu.hasMoreElements(); i++) {
					keyIds[i] = (String) enu.nextElement();
				}
				if (keyIds != null) {
					return keyIds;
				}

			}
		}
		if (path.length == 4) {
			if (path[2].equals(PROPERTY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				for (Enumeration enu = keyList.keys(); enu.hasMoreElements();) {
					if (path[3].equals((String) enu.nextElement())) {
						String[] children = new String[4];
						children[0] = KEY;
						children[1] = TYPE;
						children[2] = CARDINALITY;
						children[3] = VALUES;
						return children;
					}
				}
			} else {
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"No such node in the current serviceState tree.");
			}
		}

		if (path.length == 5 && path[4].equals(VALUES)) {
			Hashtable valueTable = (Hashtable) valuesList.get(path[1]);
			if (!valueTable.containsKey(path[3])) {
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"No such node in the current serviceState tree.");
			}
			Hashtable values = (Hashtable) valueTable.get(path[3]);
			String[] children = new String[values.size()];
			int i = 0;
			for (Enumeration enu = values.keys(); enu.hasMoreElements();) {
				children[i] = (String) enu.nextElement();
				i++;
			}
			return children;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node in the current serviceState tree.");
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1) // ./OSGi/<instance_id>/ServiceState
			return new ServiceStateMetaNode("ServiceState root node.",
					MetaNode.PERMANENT, !ServiceStateMetaNode.CAN_ADD,
					!ServiceStateMetaNode.CAN_DELETE,
					!ServiceStateMetaNode.ALLOW_ZERO,
					!ServiceStateMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/<instance_id>/ServiceState/<service_id>
			return new ServiceStateMetaNode("<service_id> subtree",
					MetaNode.AUTOMATIC, !ServiceStateMetaNode.CAN_ADD,
					!ServiceStateMetaNode.CAN_DELETE,
					ServiceStateMetaNode.ALLOW_ZERO,
					!ServiceStateMetaNode.ALLOW_INFINITE);

		if (path.length == 3) { // ./OSGi/<instance_id>/ServiceState/<service_id>/...
			if (path[2].equals(PROPERTY))
				return new ServiceStateMetaNode("Properties subtree.",
						MetaNode.AUTOMATIC, !ServiceStateMetaNode.CAN_ADD,
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(REGISTERINGBUNDLE))
				return new ServiceStateMetaNode(
						"The BundleID of the RegisteringBundle.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

			if (path[2].equals(USINGBUNDLES))
				return new ServiceStateMetaNode("UsingBundles subtree.",
						MetaNode.AUTOMATIC, !ServiceStateMetaNode.CAN_ADD,
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) { // ./OSGi/<instance_id>/ServiceState/<service_id>/.../...

			if (path[2].equals(PROPERTY))
				return new ServiceStateMetaNode("Property keys subtree.",
						MetaNode.AUTOMATIC, !ServiceStateMetaNode.CAN_ADD,
						!ServiceStateMetaNode.CAN_DELETE,
						ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE);

			if (path[2].equals(USINGBUNDLES))
				return new ServiceStateMetaNode(
						"The BundleID of the UsingBundles.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null, null);

		}

		if (path.length == 5) { // ./OSGi/<instance_id>/ServiceState/<service_id>/Properties/<id>/...
			if (path[4].equals(VALUES))
				return new ServiceStateMetaNode(
						"The values of the seervice property.",
						MetaNode.AUTOMATIC, !ServiceStateMetaNode.CAN_ADD,
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE);

			if (path[4].equals(TYPE))
				return new ServiceStateMetaNode(
						"The Type of the value of the seervice property.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[4].equals(CARDINALITY))
				return new ServiceStateMetaNode(
						"The Cardinality of the seervice property.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[4].equals(KEY))
				return new ServiceStateMetaNode(
						"The key of the seervice property.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						!ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);
		}

		if (path.length == 6) { // ./OSGi/<instance_id>/ServiceState/<service_id>/Properties/<id>/Values/...
			if (path[4].equals(VALUES))
				return new ServiceStateMetaNode(
						"The values of the seervice property.",
						!ServiceStateMetaNode.CAN_DELETE,
						!ServiceStateMetaNode.CAN_REPLACE,
						ServiceStateMetaNode.ALLOW_ZERO,
						!ServiceStateMetaNode.ALLOW_INFINITE,
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
		if (path.length == 3 && path[2].equals(USINGBUNDLES))
			return LIST_MIME_TYPE;

		if (path.length == 5 && path[4].equals(VALUES))
			return LIST_MIME_TYPE;
		if (isLeafNode(nodePath))
			return ServiceStateMetaNode.LEAF_MIME_TYPE;
		return ServiceStateMetaNode.SERVICESTATE_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);
		if (path.length == 1 && path[0].equals(SERVICESTATE))
			return true;

		if (path.length == 2) {
			ServiceReference serviceRef = (ServiceReference) serviceRefTable
					.get(path[1]);
			if (serviceRef != null)
				return true;
		}

		if (path.length == 3) {
			ServiceReference serviceRef = (ServiceReference) serviceRefTable
					.get(path[1]);
			if (serviceRef != null) {
				if (path[2].equals(PROPERTY)
						|| path[2].equals(REGISTERINGBUNDLE)
						|| path[2].equals(USINGBUNDLES))
					return true;
			}
		}

		if (path.length == 4) {
			if (path[2].equals(PROPERTY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				for (Enumeration enu = keyList.keys(); enu.hasMoreElements();) {
					String keyId = (String) enu.nextElement();
					if (path[3].equals(keyId))
						return true;
				}
			}

			if (path[2].equals(USINGBUNDLES)) {
				manageUsingBundlesTable();
				Hashtable table = (Hashtable) usingBundlesTable.get(path[1]);
				if (table.size() != 0)
					for (Enumeration enu = table.keys(); enu.hasMoreElements();) {
						if (path[3].equals((String) enu.nextElement()))
							return true;
					}
			}
		}

		if (path.length == 5) {
			if (path[2].equals(PROPERTY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				for (Enumeration enu = keyList.keys(); enu.hasMoreElements();) {
					String keyId = (String) enu.nextElement();
					if (path[3].equals(keyId))
						if (path[4].equals(VALUES) || path[4].equals(TYPE)
								|| path[4].equals(CARDINALITY)
								|| path[4].equals(KEY))
							return true;
				}
			}
		}
		if (path.length == 6) {
			if (path[4].equals(VALUES)) {
				Hashtable table = (Hashtable) valuesList.get(path[1]);
				Hashtable values = (Hashtable) table.get(path[3]);
				if (values.size() != 0)
					for (Enumeration enu = values.keys(); enu.hasMoreElements();) {
						if (path[5].equals((String) enu.nextElement()))
							return true;
					}
			}
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length <= 2)
			return false;

		if (path.length == 3 && (path[2].equals(REGISTERINGBUNDLE)))
			return true;

		if (path.length == 4 && path[2].equals(USINGBUNDLES))
			return true;

		if (path.length == 5
				&& (path[4].equals(TYPE) || path[4].equals(CARDINALITY) || path[4]
						.equals(KEY)))
			return true;

		if (path.length == 6 && path[4].equals(VALUES))
			return true;

		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		ServiceReference serviceRef = (ServiceReference) serviceRefTable
				.get(path[1]);
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(REGISTERINGBUNDLE)) {
				Bundle registerBundle = serviceRef.getBundle();
				if (registerBundle == null)
					return new DmtData(-1);
				return new DmtData(registerBundle.getBundleId());
			}
			if (path[2].equals(USINGBUNDLES))
				throw new DmtException(nodePath,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The given path indicates an interior node.");
		}

		if (path.length == 4) {
			if (path[2].equals(PROPERTY))
				throw new DmtException(nodePath,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The given path indicates an interior node.");

			if (path[2].equals(USINGBUNDLES)) {
				manageUsingBundlesTable();
				Hashtable table = (Hashtable) usingBundlesTable.get(path[1]);
				if (table.size() == 0)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"There is no leaf node at the given path.");
				String usingBundleIdStr = (String) table.get(path[3]);
				long usingBundleId = Long.parseLong(usingBundleIdStr);
				return new DmtData(usingBundleId);
			}
		}

		if (path.length == 5) {
			if (path[4].equals(TYPE)) {
				String type = null;
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				String key = (String) keyList.get(path[3]);
				Object values = serviceRef.getProperty(key);
				if (values instanceof String) {
					type = String.class.getName();
				} else if (values instanceof String[]) {
					type = String.class.getName();
				} else if (values instanceof Long) {
					type = Long.TYPE.getName();
				} else if (values instanceof Integer) {
					type = Integer.TYPE.getName();
				} else {
					throw new DmtException(nodePath,
							DmtException.FEATURE_NOT_SUPPORTED,
							"Invalid setviceState entry type.");
				}
				return new DmtData(type);

			}
			if (path[4].equals(CARDINALITY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				String key = (String) keyList.get(path[3]);
				Object values = serviceRef.getProperty(key);
				Class cl = values.getClass();
				if (cl.isArray() && !cl.equals(byte[].class)) {
					return new DmtData(ARRAY);
				} else if (values instanceof Vector) {
					return new DmtData(COLLECTION);
				} else if (cl.equals(byte[].class)
						|| (!cl.equals(Vector.class) && !cl.isArray())) {
					return new DmtData(SCALAR);
				}

			}

			if (path[4].equals(KEY)) {
				Hashtable keyList = (Hashtable) keysList.get(path[1]);
				String key = (String) keyList.get(path[3]);
				if (key == null)
					throw new DmtException(nodePath,
							DmtException.NODE_NOT_FOUND,
							"No such node in the current serviceState tree.");
				return new DmtData(key);
			}

			if (path[4].equals(VALUES))
				throw new DmtException(nodePath,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The given path indicates an interior node.");

			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		}

		if (path.length == 6) {
			if (path[4].equals(VALUES)) {
				Hashtable valueTable = (Hashtable) valuesList.get(path[1]);
				Hashtable values = (Hashtable) valueTable.get(path[3]);
				Object value = values.get(path[5]);
				if (value instanceof String) {
					String stValue = value.toString();
					return new DmtData(stValue);
				}
				if (value instanceof Long) {
					Long loValue = (Long) value;
					return new DmtData(loValue.longValue());
				}
				if (value instanceof Integer) {
					Integer ioValue = (Integer) value;
					return new DmtData(ioValue.intValue());
				}
				if (value instanceof Boolean) {
					Boolean bValue = (Boolean) value;
					return new DmtData(bValue.booleanValue());
				}
			}
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the serviceState object.");
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

	private void manageProperties() {
		for (Enumeration enu = serviceRefTable.keys(); enu.hasMoreElements();) {
			Hashtable keysId = new Hashtable();
			Hashtable valuesTable = new Hashtable();
			String serviceId = (String) enu.nextElement();
			ServiceReference serviceRef = (ServiceReference) serviceRefTable
					.get(serviceId);
			String[] keys = (String[]) serviceRef.getPropertyKeys();
			for (int i = 0; i < keys.length; i++) {
				keysId.put(Integer.toString(i + 1), keys[i]);
				Hashtable valuesId = new Hashtable();
				if (serviceRef.getProperty(keys[i]).getClass().isArray()) {
					Object[] values = (Object[]) serviceRef
							.getProperty(keys[i]);
					for (int j = 0; j < values.length; j++) {
						valuesId.put(Integer.toString(j + 1), values[j]);
					}
				} else {
					Object value = serviceRef.getProperty(keys[i]);
					valuesId.put("1", value);
				}
				valuesTable.put(Integer.toString(i + 1), valuesId);
				valuesList.put(serviceId, valuesTable);
			}
			keysList.put(serviceId, keysId);
		}
	}

	private void manageUsingBundlesTable() {
		for (Enumeration enu = serviceRefTable.keys(); enu.hasMoreElements();) {
			Hashtable bundleIdList = new Hashtable();
			String serviceId = (String) enu.nextElement();
			ServiceReference serviceRef = (ServiceReference) serviceRefTable
					.get(serviceId);
			Bundle[] usingBundles = serviceRef.getUsingBundles();
			if (usingBundles == null)
				usingBundlesTable.put(serviceId, bundleIdList);
			else {
				for (int i = 0; i < usingBundles.length; i++) {
					bundleIdList.put(Integer.toString(i + 1),
							Long.toString(usingBundles[i].getBundleId()));
				}
				usingBundlesTable.put(serviceId, bundleIdList);
			}
		}
	}

	public class ServiceStateServiceTracker extends ServiceTracker {

		public ServiceStateServiceTracker(BundleContext context, Filter filter) {
			super(context, filter, null);
		}

		public Object addingService(ServiceReference reference) {
			Long lServiceId = (Long) reference
					.getProperty(Constants.SERVICE_ID);
			serviceRefTable.put(lServiceId.toString(), reference);
			manageUsingBundlesTable();
			manageProperties();
			return context.getService(reference);
		}

		public void modifiedService(ServiceReference reference, Object service) {
			Long lServiceId = (Long) reference
					.getProperty(Constants.SERVICE_ID);
			manageUsingBundlesTable();
			manageProperties();
			serviceRefTable.put(lServiceId.toString(), reference);
		}

		public void removedService(ServiceReference reference, Object service) {
			Long lServiceId = (Long) reference
					.getProperty(Constants.SERVICE_ID);
			serviceRefTable.remove(lServiceId.toString());
			manageUsingBundlesTable();
			manageProperties();
			context.ungetService(reference);
		}
	}
}
