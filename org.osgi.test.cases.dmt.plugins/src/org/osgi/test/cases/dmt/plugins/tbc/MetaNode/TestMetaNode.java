/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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

package org.osgi.test.cases.dmt.plugins.tbc.MetaNode;

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

	public boolean isLeaf() {
		return isLeaf;
	}

	public int getScope() {
		return scope;
	}

	public String getDescription() {
		return DEFAULT_DESCRIPTION;
	}

	public int getMaxOccurrence() {
		return DEFAULT_MAX_OCCURENCE;
	}
	
	public boolean isZeroOccurrenceAllowed() {
		return false;
	}

	public DmtData getDefault() {
		return new DmtData(DEFAULT_VALUE);
	}

	public boolean hasMax() {
		return false;
	}

	public boolean hasMin() {
		return false;
	}

	public double getMax() {
		return DEFAULT_MAX_VALUE;
	}

	public double getMin() {
		return DEFAULT_MIN_VALUE;
	}

	public DmtData[] getValidValues() {
		return DEFAULT_VALID_VALUES;
	}

	public String[] getValidNames() {
		return DEFAULT_VALID_NAMES;
	}

	public int getFormat() {
		return DmtData.FORMAT_NODE;
	}

	public String[] getMimeTypes() {
		return DEFAULT_MIME_TYPES;
	}

	public String getPattern() {
		return DEFAULT_REGEXP;
	}

	public String getNamePattern() {
		return DEFAULT_NAME_REGEXP;
	}

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

}
