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
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 1			 Implement Meg TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class TestMetaNode implements MetaNode {

	private boolean	 isLeaf;
	private boolean	 can;
    private int      format;

    // Interior nodes
    public TestMetaNode(boolean can) {
        this.isLeaf        = false;
        this.format        = DmtData.FORMAT_NODE;
        this.can 		   = can;
    }
    
    // Leaf nodes
	public TestMetaNode(int format,boolean can) {
        this.isLeaf        = true;
        this.format        = format;
        this.can 		   = can;
	}

	/* ---------------------------------------------------------------------- */

    @Override
	public boolean can(int operation) {
        return can;
    }       	

    @Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public int getScope() {
		return DYNAMIC;
	}

	@Override
	public String getDescription() {
		return "";
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
		return new DmtData("test");
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

    public String getNamePattern() {
        return null;
    }
    
	public String getPattern() {
		return null;
	}

	@Override
	public String[] getMimeTypes() {
		return null;
	}

	@Override
	public boolean isValidValue(DmtData value) {
		return true;
	}

	@Override
	public boolean isValidName(String name) {
		return true;
	}

	@Override
	public String[] getExtensionPropertyKeys() {
		return null;
	}

	@Override
	public Object getExtensionProperty(String key) {
		return "";
	}

	@Override
	public String[] getRawFormatNames() {
		return null;
	}
}
