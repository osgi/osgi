/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.test.cases.util.tr069.helper;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;

/**
 * Basic implementation of {@link MetaNode} used for the test cases.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public final class TestMetaNode implements MetaNode {

	private int			format;
	private String[]	rawFormatNames;

	/**
	 * Constructs the test meta node with a specified format.
	 * 
	 * @param format the meta node format
	 */
	public TestMetaNode(int format) {
		this(format, null);
	}

	/**
	 * Constructs the test meta node with the specified format and raw format
	 * names.
	 * 
	 * @param format the meta node format
	 * @param rawFormatNames the meta node raw format names
	 */
	public TestMetaNode(int format, String[] rawFormatNames) {
		this.format = format;
		this.rawFormatNames = rawFormatNames;
	}

	/**
	 * @see info.dmtree.MetaNode#getFormat()
	 */
	public int getFormat() {
		return this.format;
	}

	/**
	 * @see info.dmtree.MetaNode#getRawFormatNames()
	 */
	public String[] getRawFormatNames() {
		return this.rawFormatNames;
	}

	/**
	 * Returns always <code>false</code>.
	 * 
	 * @param operation
	 * @return <code>false</code>
	 * @see info.dmtree.MetaNode#can(int)
	 */
	public boolean can(int operation) {
		return false;
	}

	/**
	 * Returns always <code>false</code>.
	 * 
	 * @return <code>false</code>
	 * @see info.dmtree.MetaNode#isLeaf()
	 */
	public boolean isLeaf() {
		return false;
	}

	/**
	 * Returns always {@link MetaNode#PERMANEN}.
	 * 
	 * @return {@link MetaNode#PERMANEN}
	 * @see info.dmtree.MetaNode#getScope()
	 */
	public int getScope() {
		return MetaNode.PERMANENT;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getDescription()
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Returns always 1.
	 * 
	 * @return 1
	 * @see info.dmtree.MetaNode#getMaxOccurrence()
	 */
	public int getMaxOccurrence() {
		return 1;
	}

	/**
	 * Returns always <code>true</code>.
	 * 
	 * @return <code>true</code>
	 * @see info.dmtree.MetaNode#isZeroOccurrenceAllowed()
	 */
	public boolean isZeroOccurrenceAllowed() {
		return true;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getDefault()
	 */
	public DmtData getDefault() {
		return null;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getMimeTypes()
	 */
	public String[] getMimeTypes() {
		return null;
	}

	/**
	 * Returns always <code>Double#MAX_VALUE</code>.
	 * 
	 * @return <code>Double#MAX_VALUE</code>
	 * @see info.dmtree.MetaNode#getMax()
	 */
	public double getMax() {
		return Double.MAX_VALUE;
	}

	/**
	 * Returns always <code>Double#MIN_VALUE</code>.
	 * 
	 * @return <code>Double#MIN_VALUE</code>
	 * @see info.dmtree.MetaNode#getMin()
	 */
	public double getMin() {
		return Double.MIN_VALUE;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code
	 * @see info.dmtree.MetaNode#getValidValues()
	 */
	public DmtData[] getValidValues() {
		return null;
	}

	/**
	 * Returns always <code>false</code>.
	 * 
	 * @param value
	 * @return <code>false</code>
	 * @see info.dmtree.MetaNode#isValidValue(info.dmtree.DmtData)
	 */
	public boolean isValidValue(DmtData value) {
		return false;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getValidNames()
	 */
	public String[] getValidNames() {
		return null;
	}

	/**
	 * Returns always <code>false</code>.
	 * 
	 * @param name
	 * @return <code>false</code>
	 * @see info.dmtree.MetaNode#isValidName(java.lang.String)
	 */
	public boolean isValidName(String name) {
		return false;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getExtensionPropertyKeys()
	 */
	public String[] getExtensionPropertyKeys() {
		return null;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @param key
	 * @return <code>null</code>
	 * @see info.dmtree.MetaNode#getExtensionProperty(java.lang.String)
	 */
	public Object getExtensionProperty(String key) {
		return null;
	}

}
