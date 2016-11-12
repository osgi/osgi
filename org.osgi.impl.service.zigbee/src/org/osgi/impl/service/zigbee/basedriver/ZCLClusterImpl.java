/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.basedriver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Mock implementation of the ZCLCluster interface.
 * 
 * @author $Id$
 */
public class ZCLClusterImpl implements ZCLCluster {

	private int							id;
	private ZCLAttribute[]				attributes;
	private int[]						commandIds;
	private ZCLClusterDescription		description;
	private ZCLAttributeDescription[]	attributeDescriptions;

	private boolean						isServer;

	private ZCLGlobalClusterDescription	global;

	public ZCLClusterImpl(ZCLGlobalClusterDescription global, boolean isServer) {

		this.global = global;
		this.isServer = isServer;

		if (isServer) {
			attributeDescriptions = global.getServerClusterDescription().getAttributeDescriptions();
		} else {
			attributeDescriptions = global.getClientClusterDescription().getAttributeDescriptions();
		}

		this.attributes = toZCLAttributes(attributeDescriptions);
	}

	public int getId() {
		return global.getClusterId();
	}

	public Promise getAttribute(int attributeId) {

		ZCLAttribute attribute = this.lookupAttribute(attributeId);
		if (attribute != null) {
			return Promises.resolved(attribute);
		}

		// FIXME: CT must check type of returned value or exception
		return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
				ZCLException.FAILURE,
				"the AttributeId is not valid"));
	}

	public Promise getAttributes() {
		// FIXME: CT must check type of returned value or exception
		return Promises.resolved(attributes);
	}

	public Promise getCommandIds() {
		// FIXME: CT must check type of returned value or exception

		// FIXME this implementation is wrong because the commands id are not
		// here!

		return Promises.resolved(this.commandIds.clone());
	}

	public Promise readAttributes(ZCLAttributeInfo[] attributesInfoArray) {
		// FIXME: CT must check type of returned value or exception
		if (attributesInfoArray == null) {
			return Promises.failed(new NullPointerException("attributes cannot be null"));
		} else if (attributesInfoArray.length == 0) {
			return Promises.failed(new IllegalArgumentException("attributes array cannot be empty"));
		}

		Map readStatusMap = new HashMap();

		ZCLReadStatusRecord[] readStatusRecords = new ZCLReadStatusRecord[attributesInfoArray.length];

		int manufacturerCode = attributesInfoArray[0].getManufacturerCode();

		for (int i = 0; i < attributesInfoArray.length; i++) {
			ZCLAttributeInfo attributeInfo = attributesInfoArray[i];
			if (manufacturerCode != attributeInfo.getManufacturerCode()) {
				return Promises.failed(new IllegalArgumentException(
						attributeInfo.getId() + " has a different manufacturer code compared to the others"));
			}

			int attrId = attributeInfo.getId();

			ZCLAttribute attribute = getAttributeFromId(attrId);
			if (attribute == null) {
				ZCLException failure = new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE, "unknown attributeId");
				ZCLReadStatusRecordImpl readStatusRecord = new ZCLReadStatusRecordImpl(attributeInfo, failure);
				readStatusRecords[i] = readStatusRecord;
				readStatusMap.put(new Integer(attrId), readStatusRecord);
				continue;
			} else if (attributeInfo.getDataType() != null && attribute.getDataType() != null) {
				if (attributeInfo.getDataType().getId() != attribute.getDataType().getId()) {
					ZCLException failure = new ZCLException(ZCLException.INVALID_DATA_TYPE, "requested attribute data type does not match returned one");
					ZCLReadStatusRecordImpl readStatusRecord = new ZCLReadStatusRecordImpl(attributeInfo, failure);
					readStatusRecords[i] = readStatusRecord;
					readStatusMap.put(new Integer(attribute.getId()), readStatusRecord);
					continue;
				}
			}

			Object value;
			try {
				value = attribute.getValue().getValue();
			} catch (InvocationTargetException e) {
				ZCLException failure = new ZCLException(ZCLException.SOFTWARE_FAILURE, "got InvocationTargetException: " + e.getMessage());
				ZCLReadStatusRecordImpl readStatusRecord = new ZCLReadStatusRecordImpl(attributeInfo, failure);
				readStatusRecords[i] = readStatusRecord;
				readStatusMap.put(new Integer(attribute.getId()), readStatusRecord);
				continue;
			} catch (InterruptedException e) {
				ZCLException failure = new ZCLException(ZCLException.SOFTWARE_FAILURE, "got InterruptedException: " + e.getMessage());
				ZCLReadStatusRecordImpl readStatusRecord = new ZCLReadStatusRecordImpl(attributeInfo, failure);
				readStatusRecords[i] = readStatusRecord;
				readStatusMap.put(new Integer(attribute.getId()), readStatusRecord);
				continue;
			}

			ZCLReadStatusRecordImpl readStatusRecord = new ZCLReadStatusRecordImpl(attributeInfo, null, value);
			readStatusRecords[i] = readStatusRecord;
			readStatusMap.put(new Integer(attribute.getId()), readStatusRecord);
		}
		return Promises.resolved(readStatusMap);
	}

	public Promise writeAttributes(boolean undivided, Map attributesIdsAndValues) {
		if (attributesIdsAndValues == null) {
			return Promises.failed(new NullPointerException("the Map argument cannot be null."));
		} else if (attributesIdsAndValues.size() == 0) {
			return Promises.resolved(new HashMap());
		}
		boolean first = true;
		boolean manufacturerSpecific = false;

		Set keys = attributesIdsAndValues.keySet();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			ZCLAttributeInfo attributeInfo = (ZCLAttributeInfo) iterator.next();
			if (first) {
				manufacturerSpecific = attributeInfo.isManufacturerSpecific();
				first = false;
			} else {
				if (manufacturerSpecific != attributeInfo.isManufacturerSpecific()) {
					return Promises.failed(new IllegalArgumentException("the passed attribute info map must contain all not or all manufacturer specific attributes"));
				}
			}
		}

		/*
		 * Create the result map
		 */

		Map resultMap = new HashMap();
		keys = attributesIdsAndValues.keySet();

		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			ZCLAttributeInfo attributeInfo = (ZCLAttributeInfo) iterator.next();
			ZCLAttribute attribute = lookupAttribute(attributeInfo.getId());
			ZCLAttributeDescription attributeDescription = lookupAttributeDescription(attributeInfo.getId());
			int status;
			if (attribute != null) {
				if (!attributeDescription.isReadOnly()) {
					attribute.setValue(attributesIdsAndValues.get(attributeInfo));
					continue;
				} else {
					status = ZCLException.READ_ONLY;
				}

			} else {
				status = ZCLException.UNSUPPORTED_ATTRIBUTE;
			}
			resultMap.put(new Integer(attributeInfo.getId()), new Integer(status));
		}

		return Promises.resolved(resultMap);
	}

	public Promise invoke(ZCLFrame frame) {
		// FIXME: implement this!
		return Promises.resolved(frame);
	}

	public Promise invoke(ZCLFrame frame, String exportedServicePID) {
		// FIXME: implement this!
		return Promises.resolved(frame);
	}

	public String toString() {
		String attributesAsAString = null;
		if (attributes != null) {
			attributesAsAString = "[";
			int i = 0;
			while (i < attributes.length) {
				attributesAsAString = attributesAsAString + attributes[i].toString();
				i = i + 1;
			}
			attributesAsAString = attributesAsAString + "]";
		}

		String commandIdsAsAString = null;
		if (commandIds != null) {
			commandIdsAsAString = "[";
			int i = 0;
			while (i < commandIds.length) {
				commandIdsAsAString = commandIdsAsAString + commandIds[i];
				i = i + 1;
			}
			commandIdsAsAString = commandIdsAsAString + "]";
		}

		return "" + this.getClass().getName() + "[id: " + id + ", attributes: " + attributesAsAString + ", commandIds: "
				+ commandIdsAsAString + ", description: " + description + "]";
	}

	public Promise getAttribute(int attributeId, int code) {
		if (code == -1) {
			return getAttribute(attributeId);
		}
		return Promises.failed(new UnsupportedOperationException("getAttribute:Please implement it"));
	}

	public Promise getAttributes(int code) {
		if (code == -1) {
			return getAttributes();
		}
		return Promises.failed(new UnsupportedOperationException("getAttributes:Please implement it"));
	}

	private ZCLAttribute getAttributeFromId(int attrId) {
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				ZCLAttribute attribute = attributes[i];
				if (attribute != null && attribute.getId() == attrId) {
					return attribute;
				}
			}
		}
		return null;
	}

	/**
	 * Given an array of ZCLAttributeDescriptions convert it in an array of
	 * ZCLAttribute objects.
	 * 
	 * @param attributeDescriptions An array of ZCLAttributeDescription objects.
	 * @return The array of corresponding ZCLAttribute objects
	 */
	private ZCLAttribute[] toZCLAttributes(ZCLAttributeDescription[] attributeDescriptions) {
		ZCLAttribute[] a = new ZCLAttribute[attributeDescriptions.length];

		for (int i = 0; i < attributeDescriptions.length; i++) {
			a[i] = new ZCLAttributeImpl(attributeDescriptions[i]);
		}

		return a;
	}

	private ZCLAttribute lookupAttribute(int attributeId) {
		for (int i = 0; i < attributes.length; i++) {
			if (attributeId == attributes[i].getId()) {
				return attributes[i];
			}
		}
		return null;
	}

	private ZCLAttributeDescription lookupAttributeDescription(int attributeId) {
		for (int i = 0; i < attributeDescriptions.length; i++) {
			if (attributeId == attributeDescriptions[i].getId()) {
				return attributeDescriptions[i];
			}
		}
		return null;
	}
}
