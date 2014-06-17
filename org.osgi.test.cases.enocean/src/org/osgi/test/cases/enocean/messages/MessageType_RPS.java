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
 * Prototype of a 1BS telegram
 * 
 * The teach-in telegram bears no particular information.
 * 
 * @author $Id$
 */
public abstract class MessageType_RPS extends Message {

	/**  */
	public MessageType_RPS() {
		setRORG(Message.MESSAGE_RPS);
	}

	/* All RPS messages are also teach-in messages */
	/**
	 * @return isTeachin.
	 */
	public Boolean isTeachin() {
		return Boolean.TRUE;
	}

	/**
	 * @param isTeachin
	 */
	public void setTeachin(Boolean isTeachin) {
		// Do nothing
	}

}
