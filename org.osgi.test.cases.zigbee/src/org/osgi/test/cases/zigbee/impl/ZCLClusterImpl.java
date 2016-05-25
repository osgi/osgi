/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLClusterImpl implements ZCLCluster {

	private int						id;
	protected ZCLAttribute[]		attributes;
	private int[]					commandIds;
	private ZCLClusterDescription	description;

	/**
	 * @param id
	 */
	public ZCLClusterImpl(Integer id) {
		this.id = id.intValue();
	}

	/**
	 * @param commandIds
	 * @param attributes
	 * @param desc
	 */
	public ZCLClusterImpl(int[] commandIds, ZCLAttribute[] attributes, ZCLClusterDescription desc) {
		this.id = desc.getId();
		this.commandIds = commandIds;
		this.attributes = attributes;
		this.description = desc;
	}

	public int getId() {
		return id;
	}

	public void getAttribute(int attributeId, ZigBeeHandler handler) {

		boolean hasBeenFound = false;
		for (int i = 0; i < attributes.length; i++) {
			if (attributeId == attributes[i].getId()) {
				handler.onSuccess(attributes[i]);
				hasBeenFound = true;
			}
		}
		if (!hasBeenFound) {
			handler.onFailure(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
					ZCLException.FAILURE,
					"the AttributeId is not valid"));
		}
	}

	public void getAttributes(ZigBeeHandler handler) {
		handler.onSuccess(attributes);
	}

	public void getCommandIds(ZigBeeHandler handler) {
		// TODO copy the array before
		handler.onSuccess(this.commandIds);
	}

	public void readAttributes(ZCLAttributeInfo[] attributesInfoArray, ZigBeeHandler handler) {
		// Map<Integer, byte[]> response = new HashMap<Integer, byte[]>();
		// FIX Should we check for a null value?
		/*
		 * if (attributes == null ) { handler.onFailure(new
		 * NullPointerException("attributes cannot be null")); }
		 */

		if (attributesInfoArray == null) {
			throw new NullPointerException("attributes cannot be null");
		} else if (attributesInfoArray.length == 0) {
			throw new IllegalArgumentException("attributes array cannot be empty");
		}
		Map response = new HashMap();
		// if (attributes.length == 0) {
		// handler.onSuccess(response);
		// return;
		// }
		Set code = new HashSet();

		for (int i = 0; i < attributesInfoArray.length; i++) {
			ZCLAttributeInfo attributeInfo = attributesInfoArray[i];
			code.add(new Integer(attributeInfo.getManufacturerCode()));
			if (code.size() != 1) {
				handler.onFailure(new IllegalArgumentException(
						attributeInfo.getId() + " has a different manufacturer code compared to the others"));
				return;
			}
			int attrId = attributeInfo.getId();
			ZCLAttribute attribute = getAttributeFromId(attrId);
			if (attribute == null) {
				handler.onFailure(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
						ZCLException.FAILURE,
						"the AttributeId is not valid"));
				return;
			}
			if (attributeInfo.getDataType() != null && attribute.getDataType() != null) {

				if (attributeInfo.getDataType().getId() != attribute.getDataType().getId()) {
					handler.onFailure(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
							ZCLException.FAILURE,
							"the Attribute datatype is not valid"));
					return;
				}
			}

		}

		// FIX We should check values of attributes and if attributes exists or
		// not
		int i = 0;
		// for (int i : attributesIds) {
		ZCLAttributeInfo attribute = attributesInfoArray[i];
		byte[] attributeValue = {0};
		response.put(Integer.valueOf(Integer.toString(attribute.getId())), attributeValue);
		// }
		handler.onSuccess(response);
	}

	public void writeAttributes(boolean undivided, Map attributesIdsAndValues, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void invoke(ZCLFrame frame, ZCLCommandHandler handler) {
		// mocked invocation.
		handler.notifyResponse(frame, null);
	}

	public void invoke(ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID) {
		// mocked invocation.
		handler.notifyResponse(frame, null);
	}

	// public String toString() {
	// return description.getGlobalClusterDescription().getClusterName();
	// }

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

	public void getAttribute(int attributeId, int code, ZigBeeHandler handler) {
		if (code == -1) {
			getAttribute(attributeId, handler);
		}
		throw new UnsupportedOperationException("getAttribute:Please implement it");
	}

	public void getAttributes(int code, ZigBeeHandler handler) {
		if (code == -1) {
			getAttributes(handler);
		}
		// TODO Auto-generated method stub, in the meanwhile it throws runtime
		// exception
		throw new UnsupportedOperationException("getAttributes:Please implement it");
	}

	private ZCLAttribute getAttributeFromId(int attrId) {
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				ZCLAttribute attr = attributes[i];
				if (attr != null && attr.getId() == attrId) {
					return attr;
				}
			}
		}
		return null;
	}
}
