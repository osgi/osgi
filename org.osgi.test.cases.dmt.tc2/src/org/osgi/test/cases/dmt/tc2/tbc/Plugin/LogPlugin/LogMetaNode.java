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
package org.osgi.test.cases.dmt.tc2.tbc.Plugin.LogPlugin;

import java.util.List;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.MetaNode;

class LogMetaNode implements MetaNode {
    static final boolean MODIFIABLE = true; 
    static final boolean SEARCH_PARAMETER = true; 
    static final boolean ALLOW_INFINITE = true; 

	static final String  LEAF_MIME_TYPE = "text/plain";
	static final String  LOG_MO_TYPE = "org.osgi/1.0/LogManagementObject";
    
	// private fields
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

	private boolean	  canDelete;
	private boolean	  canAdd;
	private boolean	  canGet;
	private boolean	  canReplace;
	private boolean	  canExecute;
	private boolean	  isLeaf;
    
	private int       scope;
    private String    description;
    private int       maxOccurrence;
    private boolean   isZeroOccurrenceAllowed;
    private int       format;
    private String[]  mimeTypes;
    private DmtData   defaultValue;

    // used for checking the components of a comma-separated list
	private List<String>		validComponents;

    // Meta-node constructor for interior nodes
    protected LogMetaNode(int scope, boolean modifiable, 
            boolean allowInfinite, String description) {
        
        this.canDelete       = modifiable;
        this.canAdd          = modifiable;
        this.canGet          = true;
        this.canReplace      = true;
        this.canExecute      = false;
        this.isLeaf          = false;
        
        this.scope           = scope;
        this.description     = description;
        this.maxOccurrence   = allowInfinite ? Integer.MAX_VALUE : 1;
        this.isZeroOccurrenceAllowed = allowInfinite;
        this.format          = DmtData.FORMAT_NODE;
        this.validComponents = null;
        this.mimeTypes       = null;
        this.defaultValue    = null;
    }
    
    // Meta-node constructor for leaf nodes
	protected LogMetaNode(boolean isSearchParameter, int format, 
			DmtData defaultValue, List<String> validComponents,
            String description) {
        this.canDelete       = false;
        this.canAdd          = false;
        this.canGet          = true;
        this.canReplace      = isSearchParameter;
        this.canExecute      = false;
        this.isLeaf          = true;
        
        this.scope           = MetaNode.AUTOMATIC;
        this.description     = description;
        this.maxOccurrence   = 1;
        this.isZeroOccurrenceAllowed = !isSearchParameter;
        this.format          = format;
        this.validComponents = validComponents;
        this.mimeTypes       = new String[] { LEAF_MIME_TYPE };
        this.defaultValue    = defaultValue;
	}

	/* ---------------------------------------------------------------------- */

    @Override
	public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return canDelete;
        case CMD_ADD:     return canAdd;
        case CMD_GET:     return canGet;
        case CMD_REPLACE: return canReplace;
        case CMD_EXECUTE: return canExecute;
        }
        return false;
    }       

    @Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public int getScope() {
		return scope;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	@Override
	public boolean isZeroOccurrenceAllowed() {
		return isZeroOccurrenceAllowed;
	}

	@Override
	public DmtData getDefault() {
		return defaultValue;
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
		return format;
	}

	@Override
	public String[] getRawFormatNames() {
        return null;
    }

    @Override
	public String[] getMimeTypes() {
		return mimeTypes;
	}
    
    @Override
	public boolean isValidName(String name) {
        return true;
    }
    
    @Override
	public boolean isValidValue(DmtData value) {
        if((format & value.getFormat()) == 0)
            return false;
        
        if(validComponents != null) { // format assumed to be FORMAT_STRING
            try {
                String excludes = value.getString();
                String[] components = Splitter.split(excludes, ',', 0);
                for (int i = 0; i < components.length; i++)
                    if(!validComponents.contains(components[i].trim()))
                        return false;
            } catch(DmtIllegalStateException e) {
                // not checking components if format is not string
            }
        }
        
        return true;
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
                "' extension property is supported by this plugin.");
    }
}
