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
package org.osgi.impl.service.dmt;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class ScaffoldMetaNode implements MetaNode {
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    @Override
	public boolean can(int operation) {
        return operation == CMD_GET;
    }
    
    @Override
	public boolean isLeaf() {
        return false;
    }

    @Override
	public int getScope() {
        return PERMANENT;        
    }

    @Override
	public String getDescription() {
        return null;
    }

    @Override
	public int getMaxOccurrence() {
        return 1;
    }

    @Override
	public boolean isZeroOccurrenceAllowed() {
        return true;
    }

    @Override
	public DmtData getDefault() {
        return null;
    }

    @Override
	public double getMax() {
        return Double.MAX_VALUE;
    }

    @Override
	public double getMin() {
        return Double.MIN_VALUE;
    }

    @Override
	public String[] getValidNames() {
        return null;
    }
    
    @Override
	public DmtData[] getValidValues() {
        return null;
    }

    @Override
	public int getFormat() {
        return DmtData.FORMAT_NODE;
    }

    @Override
	public String[] getRawFormatNames() {
        return null;
    }

    @Override
	public String[] getMimeTypes() {
        return new String[] { DmtConstants.DDF_SCAFFOLD };
    }
    
    @Override
	public boolean isValidName(String name) {
        return true;
    }
    
    @Override
	public boolean isValidValue(DmtData value) {
        return false;
    }

    @Override
	public String[] getExtensionPropertyKeys() {
        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    @Override
	public Object getExtensionProperty(String key) {
        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
            return Boolean.valueOf(false);
        
        throw new IllegalArgumentException("Only the '" + 
                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
                "' extension property is supported by this plugin");
    }
}
