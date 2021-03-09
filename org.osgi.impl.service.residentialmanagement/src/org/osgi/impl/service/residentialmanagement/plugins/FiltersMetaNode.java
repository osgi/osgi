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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Arrays;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
class FiltersMetaNode implements MetaNode {
    static final boolean CAN_ADD        = true;
    static final boolean CAN_DELETE     = true;
    static final boolean CAN_REPLACE    = true;
    static final boolean ALLOW_ZERO     = true;
    static final boolean ALLOW_INFINITE = true;
    static final boolean IS_INDEX       = true;
    
    static final String  LEAF_MIME_TYPE = "text/plain";
    static final String  FILTERS_MO_TYPE = 
        "org.osgi.service.residential.filters.ddf";

    
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    private boolean		canAdd                  = false;
	private boolean		canDelete               = false;
	private boolean		canGet                  = true;
	private boolean		canReplace              = false;
	private boolean		canExecute              = false;
	
	private boolean		leaf; // there is no meaningful default
	private int         scope                   = PERMANENT;
	private String		description				= null;
	private boolean		zeroOccurrenceAllowed	= false;
	private int			maxOccurrence			= 1;
	private DmtData		defaultData				= null;
	private DmtData[]	validValues				= null;
	private int			formats					= DmtData.FORMAT_NULL;
	private String[]	mimeTypes				= null;
    
	// Leaf node in Filters MO
	// First element in validValues (if any) is the default value.
	FiltersMetaNode(String description, int scope,  boolean canAdd, boolean canDelete, 
			boolean canReplace, boolean allowZero, boolean allowInfinite,
			int formats, DmtData[] validValues) {
		leaf = true;
		
        this.canAdd = canAdd;
		this.canDelete = canDelete;
		this.canReplace = canReplace;
		this.scope = scope;
		this.mimeTypes = new String[] { LEAF_MIME_TYPE };
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE : 1;
		this.formats = formats;
		this.validValues = validValues;
		this.defaultData = validValues == null ? null : validValues[0];

	}

	// Interior node in FrameworkPlugin
	FiltersMetaNode(String description, int scope, boolean canAdd, 
			boolean canDelete, boolean allowZero, boolean allowInfinite) {
		leaf = false;

		this.canAdd        = canAdd;
		this.canDelete     = canDelete;
		this.scope         = scope;
		this.description   = description;
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE : 1;
		this.formats       = DmtData.FORMAT_NODE;
	}

    
    
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
		return leaf;
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
		return zeroOccurrenceAllowed;
	}

	@Override
	public DmtData getDefault() {
		return defaultData;
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
		return validValues;
	}

	@Override
	public int getFormat() {
		return formats;
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
        if((formats & value.getFormat()) == 0)
            return false;
        
        return validValues == null ? true : 
            Arrays.asList(validValues).contains(value);
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
