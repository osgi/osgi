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

package org.osgi.service.tr069todmt;

/**
 * Maps to the TR-069 {@code ParameterValueStruct}
 */
public interface ParameterValue {

	/**
	 * This is the path of a Parameter. In TR-069 this is called the Parameter
	 * Name.
	 * 
	 * @return The path of the parameter
	 */
	String getPath();

	/**
	 * This is the value of the parameter. The returned value must be in a
	 * representation defined by the TR-069 protocol.
	 * 
	 * @return The value of the parameter
	 */
	public String getValue();

	/**
	 * The type of the parameter. One of {@link TR069Connector#TR069_INT},
	 * {@link TR069Connector#TR069_UNSIGNED_INT},
	 * {@link TR069Connector#TR069_LONG},
	 * {@link TR069Connector#TR069_UNSIGNED_LONG},
	 * {@link TR069Connector#TR069_STRING},
	 * {@link TR069Connector#TR069_DATETIME},
	 * {@link TR069Connector#TR069_BASE64},
	 * {@link TR069Connector#TR069_HEXBINARY}. This method is not part of the
	 * {@code ParameterValueStruct} but is necessary to encode the type in the
	 * XML.
	 * 
	 * @return The parameter type
	 */
	public int getType();

}
