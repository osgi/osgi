/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.enocean.messages;

/**
 * MessageType_2
 * 
 * @author $Id$
 */
public abstract class MessageType_2 extends Message {

	private Boolean	isTeachin;

	/**  */
	public MessageType_2() {
		setRORG(Message.MessageType_2);
	}

	/**
	 * @return isTeachin.
	 */
	public Boolean isTeachin() {
		return isTeachin;
	}

	/**
	 * @param isTeachin
	 */
	public void setTeachin(Boolean isTeachin) {
		this.isTeachin = isTeachin;
	}

}
