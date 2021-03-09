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
package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import org.osgi.service.dmt.DmtData;

public class MetaNode implements org.osgi.service.dmt.MetaNode {

	// according to bug-fix 1948, this extension-property must not be neccessary anymore
//    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
//        "org.osgi.impl.service.dmt.interior-node-value-support";

    private boolean leaf;
	private int scope;
	private int format;
	private int[] operations = {};
	
	
	public MetaNode( boolean leaf, int scope, int format, int[] operations ) {
		this.leaf = leaf;
		this.scope = scope;
		this.format = format;
		this.operations = operations;
	}
	
	
	@Override
	public boolean can(int operation) {
		for (int i = 0; i < operations.length; i++)
			if ( operation == operations [i])
				return true;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxOccurrence() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isZeroOccurrenceAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DmtData getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DmtData[] getValidValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFormat() {
		return format;
	}

	@Override
	public String[] getRawFormatNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidValue(DmtData value) {
		return true;
	}

	@Override
	public String[] getValidNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	// this stuff seems necessary (for any obscure reasons) to make the OSGi-ref-impl-DMTAdmin avoid NullPointerExceptions
    @Override
	public String[] getExtensionPropertyKeys() {
    	return null;
//        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    @Override
	public Object getExtensionProperty(String key) {
//        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
//            return Boolean.valueOf(true);
//        
//        throw new IllegalArgumentException("Only the '" + 
//                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
//                "' extension property is supported by this plugin.");
    	return null;
    }

}
