/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.service.component.annotations;

/**
 * Change options for the {@link FieldReference} annotation.
 * 
 * @since 1.3
 * @author $Id$
 */
public enum FieldReferenceChangeOption {
	
	/**
	 * The update change option is used to update the collection referenced by
	 * the field when there are changes to the bounds services.
	 * 
	 * <p>
	 * This change option can only be used when the field reference is has
	 * dynamic policy, multiple cardinality and the component implementation
	 * sets the field value to a collection object in its constructor.
	 */
	UPDATE("update"),
	
	/**
	 * The replace change option is used to replace the field value with a new
	 * value when there are changes to the bounds services.
	 */
	REPLACE("replace");

	private final String	value;

	FieldReferenceChangeOption(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
