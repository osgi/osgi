/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Arrays;
import info.dmtree.DmtData;
import info.dmtree.MetaNode;
/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class ServiceStateMetaNode implements MetaNode {
    static final boolean CAN_ADD        = true;
    static final boolean CAN_DELETE     = true;
    static final boolean CAN_REPLACE    = true;
    static final boolean ALLOW_ZERO     = true;
    static final boolean ALLOW_INFINITE = true;
    static final boolean IS_INDEX       = true;
    
    static final String  LEAF_MIME_TYPE = "text/plain";
    static final String  SERVICESTATE_MO_TYPE = 
        "org.osgi.service.residential.ServiceState.ddf";

    
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
    
	// Leaf node in PackageStatePlugin
	// First element in validValues (if any) is the default value.
	ServiceStateMetaNode(String description, boolean canDelete, 
			boolean canReplace, boolean allowZero, boolean allowInfinite,
			int formats, DmtData[] validValues) {
		leaf = true;
		
        this.canAdd = false;
		this.canDelete = canDelete;
		this.canReplace = canReplace;
		this.scope = AUTOMATIC;
		this.mimeTypes = new String[] { LEAF_MIME_TYPE };
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE /* inf */ : 1;
		this.formats = formats;
		this.validValues = validValues;
		this.defaultData = validValues == null ? null : validValues[0];

	}

	// Interior node in PackageStatePlugin
	ServiceStateMetaNode(String description, int scope, boolean canAdd, 
			boolean canDelete, boolean allowZero, boolean allowInfinite) {
		leaf = false;

		this.canAdd        = canAdd;
		this.canDelete     = canDelete;
		this.scope         = scope;
		this.description   = description;
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE /* inf */ : 1;
		this.formats       = DmtData.FORMAT_NODE;
	}

    
    
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

	public boolean isLeaf() {
		return leaf;
	}

    public int getScope() {
        return scope;        
    }

	public String getDescription() {
		return description;
	}

	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	public boolean isZeroOccurrenceAllowed() {
		return zeroOccurrenceAllowed;
	}

	public DmtData getDefault() {
		return defaultData;
	}

	public double getMax() {
		return Double.MAX_VALUE;
	}

	public double getMin() {
		return Double.MIN_VALUE;
	}

    public String[] getValidNames() {
        return null;
    }
	public DmtData[] getValidValues() {
		return validValues;
	}

	public int getFormat() {
		return formats;
	}

	public String[] getRawFormatNames() {
        return null;
    }

    public String[] getMimeTypes() {
		return mimeTypes;
	}
    
    public boolean isValidName(String name) {
        return true;
    }
    
    public boolean isValidValue(DmtData value) {
        if((formats & value.getFormat()) == 0)
            return false;
        
        return validValues == null ? true : 
            Arrays.asList(validValues).contains(value);
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
}
