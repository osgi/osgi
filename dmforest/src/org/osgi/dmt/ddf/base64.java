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

package org.osgi.dmt.ddf;

/**
 * A helper class to distinguish between bin and base64 types in Dmt Admin. This
 * class just wraps a byte array.
 * 
 */
public class base64 {
	final byte[]	data;

	/**
	 * The constructor, just wrap the byte array to give it a new value
	 * 
	 * @param data the byte array to wrap
	 */
	public base64(byte[] data) {
		this.data = data;
	}

	/**
	 * The wrapped byte array.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
}
