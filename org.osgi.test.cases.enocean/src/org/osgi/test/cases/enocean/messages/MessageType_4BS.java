/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.test.cases.enocean.messages;

/**
 * Prototype of a 4BS telegram
 * 
 * Teach-in procedure:
 * 
 * - if DB0.3 is 0, then it's a teach-in telegram.
 * 
 * - if DB0.7 is also 0, no manufacturer info.
 * 
 * - if DB0.7 is 1, manufacturer info is present.
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public abstract class MessageType_4BS extends Message {

	private Boolean	isTeachin;

	/**  */
	public MessageType_4BS() {
		setRORG(Message.MESSAGE_4BS);
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
