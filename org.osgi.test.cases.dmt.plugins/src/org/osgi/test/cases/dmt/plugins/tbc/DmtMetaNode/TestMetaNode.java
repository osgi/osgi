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

package org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtMetaNode;

/**
 * @author Andre Assad
 * 
 */
public class TestMetaNode implements DmtMetaNode {

	static final boolean CANDELETE = false;

	static final boolean CANADD = true;

	static final boolean CANGET = true;

	static final boolean CANREPLACE = true;

	static final boolean CANEXECUTE = false;

	public static final String DEFAULT_VALUE = "Standard Value";

	public static final String DEFAULT_DESCRIPTION = "Standard Description";

	public static final int DEFAULT_MAX_OCCURENCE = 0;

	public static final int DEFAULT_MAX_VALUE = 0;

	public static final int DEFAULT_MIN_VALUE = 0;
	
	public static final DmtData[] DEFAULT_VALID_VALUES = { new DmtData(10),
			new DmtData("standard") };

	public static final String[] DEFAULT_VALID_NAMES = { "STANDARD1",
			"STANDARD2" };

	public static final String DEFAULT_REGEXP = "[0-9]";

	public static final String DEFAULT_NAME_REGEXP = "^T";

	public static final String[] DEFAULT_MIME_TYPES = { "text/xml",
			"audio/mpeg" };


	public boolean can(int perm) {
		switch (perm) {
		case DmtMetaNode.CMD_ADD: {
			return CANADD;
		}
		case DmtMetaNode.CMD_DELETE: {
			return CANDELETE;
		}
		case DmtMetaNode.CMD_EXECUTE: {
			return CANEXECUTE;
		}
		case DmtMetaNode.CMD_GET: {
			return CANGET;
		}
		case DmtMetaNode.CMD_REPLACE: {
			return CANREPLACE;
		}
		}
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

	public int getScope() {
		return DmtMetaNode.DYNAMIC;
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

	public int getMax() {
		return DEFAULT_MAX_VALUE;
	}

	public int getMin() {
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

}
