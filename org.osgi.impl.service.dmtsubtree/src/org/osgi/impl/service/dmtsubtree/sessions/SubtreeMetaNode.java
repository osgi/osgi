/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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


package org.osgi.impl.service.dmtsubtree.sessions;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;

public class SubtreeMetaNode implements MetaNode {

    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    private String uri;
	
	public SubtreeMetaNode( String uri ) {
		this.uri = uri;
	}
	
	public boolean can(int operation) {
		// can only get
		return operation == CMD_GET;
	}

	public DmtData getDefault() {
		return null;
	}

	public String getDescription() {
		String desc = null;
		desc = "An emulated interior node as ancestor for mapped rootnodes or vendor data plugins";
				
		return desc;
	}

    public String[] getExtensionPropertyKeys() {
        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    public Object getExtensionProperty(String key) {
        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
            return new Boolean(false);
        
        throw new IllegalArgumentException("Only the '" + 
                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
                "' extension property is supported by this plugin.");
    }

	public int getFormat() {
		return DmtData.FORMAT_NODE;
	}

	public double getMax() {
		return 0;
	}

	public int getMaxOccurrence() {
		return 1;
	}

	public String[] getMimeTypes() {
		return null;
	}

	public double getMin() {
		return 0;
	}

	public String[] getRawFormatNames() {
		return null;
	}

	public int getScope() {
		return MetaNode.AUTOMATIC;
	}

	public String[] getValidNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public DmtData[] getValidValues() {
		return null;
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isValidName(String name) {
		return true;
	}
	

	public boolean isValidValue(DmtData value) {
		return false;
	}

	public boolean isZeroOccurrenceAllowed() {
		return true;
	}

}
