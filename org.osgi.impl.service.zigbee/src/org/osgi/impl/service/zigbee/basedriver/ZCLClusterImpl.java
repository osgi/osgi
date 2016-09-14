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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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

	public ZCLClusterImpl(int[] commandIds, ZCLAttribute[] attributes, ZCLClusterDescription desc) {
		this.id = desc.getId();
		this.commandIds = commandIds;
		this.attributes = attributes;
		this.description = desc;
	}

	public int getId() {
		return id;
	}

	public Promise getAttribute(int attributeId) {

		for (int i = 0; i < attributes.length; i++) {
			if (attributeId == attributes[i].getId()) {
				return Promises.resolved(attributes[i]);
			}
		}
		return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
				ZCLException.FAILURE,
				"the AttributeId is not valid"));
	}

	public Promise getAttributes() {
		return Promises.resolved(attributes);
	}

	public Promise getCommandIds() {
		return Promises.resolved(this.commandIds.clone());
	}

	public Promise readAttributes(ZCLAttributeInfo[] attributesInfoArray) {
		// Map<Integer, byte[]> response = new HashMap<Integer, byte[]>();
		// FIX Should we check for a null value?
		/*
		 * if (attributes == null ) { handler.onFailure(new
		 * NullPointerException("attributes cannot be null")); }
		 */

		if (attributesInfoArray == null) {
			return Promises.failed(new NullPointerException("attributes cannot be null"));
		} else if (attributesInfoArray.length == 0) {
			return Promises.failed(new IllegalArgumentException("attributes array cannot be empty"));
		}
		Map response = new HashMap();

		Set code = new HashSet();

		for (int i = 0; i < attributesInfoArray.length; i++) {
			ZCLAttributeInfo attributeInfo = attributesInfoArray[i];
			code.add(new Integer(attributeInfo.getManufacturerCode()));
			if (code.size() != 1) {
				return Promises.failed(new IllegalArgumentException(
						attributeInfo.getId() + " has a different manufacturer code compared to the others"));
			}
			int attrId = attributeInfo.getId();
			ZCLAttribute attribute = getAttributeFromId(attrId);
			if (attribute == null) {
				return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
						ZCLException.FAILURE,
						"the AttributeId is not valid"));
			}
			if (attributeInfo.getDataType() != null && attribute.getDataType() != null) {

				if (attributeInfo.getDataType().getId() != attribute.getDataType().getId()) {
					return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
							ZCLException.FAILURE,
							"the Attribute datatype is not valid"));
				}
			}
		}

		int i = 0;
		// for (int i : attributesIds) {
		ZCLAttributeInfo attribute = attributesInfoArray[i];
		byte[] attributeValue = {0};
		response.put(Integer.valueOf(Integer.toString(attribute.getId())), attributeValue);
		// }
		return Promises.resolved(response);
	}

	public Promise writeAttributes(boolean undivided, Map attributesIdsAndValues) {
		return Promises.resolved(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(ZCLFrame frame) {
		// mocked invocation.
		return Promises.resolved(frame);
	}

	public Promise invoke(ZCLFrame frame, String exportedServicePID) {
		// mocked invocation.
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
				ZCLAttribute attr = attributes[i];
				if (attr != null && attr.getId() == attrId) {
					return attr;
				}
			}
		}
		return null;
	}
}
