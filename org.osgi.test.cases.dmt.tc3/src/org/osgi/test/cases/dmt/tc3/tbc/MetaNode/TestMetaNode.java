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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 22, 2005  Andre Assad
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

/**
 * @author Andre Assad
 * 
 */
public class TestMetaNode implements MetaNode {

    
    public TestMetaNode() {
        this.isLeaf =false;
        resetDefaultValues();
        resetCanAndScope();
    }
    
    public TestMetaNode(boolean isLeaf,String[] validNames,DmtData[] validValues) {
        this.isLeaf = isLeaf;
        DEFAULT_VALID_NAMES = validNames;
        DEFAULT_VALID_VALUES = validValues;
        resetCanAndScope();
    }
    public TestMetaNode(boolean isLeaf,String[] validNames,DmtData[] validValues,boolean canAdd
            ,boolean canGet,boolean canReplace,boolean canExec,boolean canDelete,int scope) {
        this(isLeaf,validNames,validValues);
        CANADD=canAdd;
        CANGET=canGet;
        CANREPLACE =canReplace;
        CANEXECUTE=canExec;
        CANDELETE=canDelete;
        this.scope=scope;
        
    }
    private boolean isLeaf;
    
	static boolean CANDELETE = true;
    
    int scope = MetaNode.DYNAMIC;

	static boolean CANADD = true;

	static boolean CANGET = true;

	static boolean CANREPLACE = true;

	static boolean CANEXECUTE = true;

	public static final String DEFAULT_VALUE = "Standard Value";

	public static final String DEFAULT_DESCRIPTION = "Standard Description";

	public static final int DEFAULT_MAX_OCCURENCE = 0;

	public static final double DEFAULT_MAX_VALUE = 0;

	public static final double DEFAULT_MIN_VALUE = 0;
	
	public static DmtData[] DEFAULT_VALID_VALUES = { new DmtData(10),
			new DmtData("standard") };

    public static String[] DEFAULT_VALID_NAMES = { "STANDARD1","STANDARD2" };

	public static final String DEFAULT_REGEXP = "[0-9]";

	public static final String DEFAULT_NAME_REGEXP = "^T";

	public static final String[] DEFAULT_MIME_TYPES = { "text/xml",
			"audio/mpeg" };

	public static final String[] DEFAULT_EXTENSION_PROPERTY_KEYS = { "a",	"b" };
	
	public static final String DEFAULT_EXTENSION_PROPERTY ="Extension Property";
	
	public static final String[] DEFAULT_RAW_FORMAT_NAMES  = { "test1",	"test2" };
	
    private void resetDefaultValues() {
        
        DEFAULT_VALID_VALUES = new DmtData[] { new DmtData(10), new DmtData("standard") };
        DEFAULT_VALID_NAMES = new String[]{ "STANDARD1","STANDARD2" };
    }
	private void resetCanAndScope() {
        CANDELETE = true;
        CANADD = true;
        CANGET = true;
        CANREPLACE = true;
        CANEXECUTE = true;
        scope = MetaNode.DYNAMIC;
    }
	@Override
	public boolean can(int perm) {
		switch (perm) {
		case MetaNode.CMD_ADD: {
			return CANADD;
		}
		case MetaNode.CMD_DELETE: {
			return CANDELETE;
		}
		case MetaNode.CMD_EXECUTE: {
			return CANEXECUTE;
		}
		case MetaNode.CMD_GET: {
			return CANGET;
		}
		case MetaNode.CMD_REPLACE: {
			return CANREPLACE;
		}
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
		return DEFAULT_DESCRIPTION;
	}

	@Override
	public int getMaxOccurrence() {
		return DEFAULT_MAX_OCCURENCE;
	}
	
	@Override
	public boolean isZeroOccurrenceAllowed() {
		return false;
	}

	@Override
	public DmtData getDefault() {
		return new DmtData(DEFAULT_VALUE);
	}

	public boolean hasMax() {
		return false;
	}

	public boolean hasMin() {
		return false;
	}

	@Override
	public double getMax() {
		return DEFAULT_MAX_VALUE;
	}

	@Override
	public double getMin() {
		return DEFAULT_MIN_VALUE;
	}

	@Override
	public DmtData[] getValidValues() {
		return DEFAULT_VALID_VALUES;
	}

	@Override
	public String[] getValidNames() {
		return DEFAULT_VALID_NAMES;
	}

	@Override
	public int getFormat() {
		return DmtData.FORMAT_NODE;
	}

	@Override
	public String[] getMimeTypes() {
		return DEFAULT_MIME_TYPES;
	}

	public String getPattern() {
		return DEFAULT_REGEXP;
	}

	public String getNamePattern() {
		return DEFAULT_NAME_REGEXP;
	}

	@Override
	public boolean isValidValue(DmtData value) {
        if (null != DEFAULT_VALID_NAMES) {
    		for (int i=0;i<DEFAULT_VALID_VALUES.length;i++) {
    			if (value.equals(DEFAULT_VALID_VALUES[i])) {
    				return true;
    			}
    		}
    		return false;
        } else {
            return true;
        }
	}

	@Override
	public boolean isValidName(String name) {
		if (null != DEFAULT_VALID_NAMES) {
            for (int i=0;i<DEFAULT_VALID_NAMES.length;i++) {
    			if (name.equals(DEFAULT_VALID_NAMES[i])) {
    				return true;
    			}
    		}
            return false;
        } else {
            return true;
        }
		
	}

	@Override
	public String[] getExtensionPropertyKeys() {
		return DEFAULT_EXTENSION_PROPERTY_KEYS;
	}

	@Override
	public Object getExtensionProperty(String key) {
            return DEFAULT_EXTENSION_PROPERTY;
	}

	@Override
	public String[] getRawFormatNames() {
		return DEFAULT_RAW_FORMAT_NAMES;
	}

}
