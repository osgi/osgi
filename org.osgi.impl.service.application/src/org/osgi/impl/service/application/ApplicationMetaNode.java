/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.application;

import org.osgi.service.dmt.*;

/**
 * DMT meta node for the Application Admin
 */
public class ApplicationMetaNode implements DmtMetaNode {
	static final boolean	CANDELETE	= true;
	static final boolean	CANADD		= true;
	static final boolean	CANGET		= true;
	static final boolean	CANREPLACE	= true;
	static final boolean	CANEXECUTE	= true;
	static final boolean	ISLEAF		= true;

	protected ApplicationMetaNode(boolean isLeaf, boolean canGet) {
		this.isLeaf = isLeaf;
		this.canGet = canGet;
	}

	protected ApplicationMetaNode(boolean canDelete, boolean canAdd,
			boolean canGet, boolean canReplace, boolean canExecute,
			boolean isLeaf) {
		this.canDelete = canDelete;
		this.canAdd = canAdd;
		this.canGet = canGet;
		this.canReplace = canReplace;
		this.canExecute = canExecute;
		this.isLeaf = isLeaf;
	}

	// private fields
	private boolean	canDelete;
	private boolean	canAdd;
	private boolean	canGet;
	private boolean	canReplace;
	private boolean	canExecute;
	private boolean	isLeaf;

	/* ---------------------------------------------------------------------- */
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
		return isLeaf;
	}

	public int getScope() {
		// TODO Auto-generated method stub
		return DmtMetaNode.DYNAMIC;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxOccurrence() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isZeroOccurrenceAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public DmtData getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasMax() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMin() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMax() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	public int getMin() {
		// TODO Auto-generated method stub
		return Integer.MIN_VALUE;
	}

	public DmtData[] getValidValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValidNames() {
		// TODO Auto-generated method stub
		return null;
	}

    public boolean isValidValue(DmtData arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isValidName(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

	public String getPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamePattern() {
		// TODO Auto-generated method stub
		return null;
	}
}
