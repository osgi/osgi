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

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class SharedMountPointMetaNode implements MetaNode {
    private int scope;
    
    public SharedMountPointMetaNode( int scope ) {
    	this.scope = scope;
    }
    
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
        return scope;        
    }

    @Override
	public String getDescription() {
        return null;
    }

    @Override
	public int getMaxOccurrence() {
        return Integer.MAX_VALUE;
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
        return null;
    }
    
    @Override
	public boolean isValidName(String name) {
    	int index = -1;
    	try {
        	index = Integer.parseInt(name);
		} catch (NumberFormatException e) {
			return false;
		}
        return index >= 1 && index < Integer.MAX_VALUE;
    }
    
    @Override
	public boolean isValidValue(DmtData value) {
        return false;
    }

    @Override
	public String[] getExtensionPropertyKeys() {
    	return null;
    }

    @Override
	public Object getExtensionProperty(String key) {
    	return null;
    }
}
