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
package org.osgi.test.cases.dmt.tc4.tb1.intf;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;

public abstract class InteriorNode extends Node {
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	@Override
	public int getNodeSize() {
		return 0;
	}
	
	@Override
	public DmtData getNodeValue() {
		return null;
	}
	
	@Override
	public void setNodeValue(DmtData value) throws DmtException {
		throw new DmtException(getNodePath(), DmtException.FEATURE_NOT_SUPPORTED, 
				"Operation is not allowed - setting the value of an Interior Node isn't allowed");
	}
	
	@Override
	public String getNodeType() {
		return null;
	}
}
