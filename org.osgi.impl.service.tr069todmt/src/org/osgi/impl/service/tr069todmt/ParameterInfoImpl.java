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

package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.MetaNode;
import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * 
 *
 */
public class ParameterInfoImpl implements ParameterInfo {

	private Node			node;
	private String			parameterName;
	private TR069Connector	connector;

	/**
	 * @param connector
	 * @param parameterName
	 * @param node
	 */
	public ParameterInfoImpl(TR069Connector connector, String parameterName, Node node) {
		this.connector = connector;
		this.parameterName = parameterName;
		this.node = node;
	}

	@Override
	public String getPath() {
		return parameterName;
	}

	@Override
	public boolean isWriteable() {
		if (isParameter()) {
			return node.can(MetaNode.CMD_REPLACE);
		}
		if (node.isMultiInstanceParent()) {
			return node.canAddChild();
		}
		if (node.isMultiInstanceNode()) {
			return node.can(MetaNode.CMD_DELETE);
		}
		return false;
	}

	@Override
	public boolean isParameter() {
		return node.isLeaf();
	}

	@Override
	public ParameterValue getParameterValue() throws TR069Exception {
		return connector.getParameterValue(getPath());
	}

	@Override
	public String toString() {
		return parameterName;
	}
}
