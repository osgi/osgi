/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZigBeeException;

public class ZCLReadStatusRecordImpl implements ZCLReadStatusRecord {

	private ZCLAttributeInfo	attributeInfo;
	private ZCLException		failure;
	private Object				value;

	public ZCLReadStatusRecordImpl(ZCLAttributeInfo attributeInfo, ZCLException failure) {
		this.attributeInfo = attributeInfo;
		this.failure = failure;
	}

	public ZCLReadStatusRecordImpl(ZCLAttributeInfo attributeInfo, ZCLException failure, Object value) {
		this.attributeInfo = attributeInfo;
		this.failure = failure;
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public ZigBeeException getFailure() {
		return failure;
	}

	@Override
	public ZCLAttributeInfo getAttributeInfo() {
		return attributeInfo;
	}
}
