/*
 * Copyright (c) OSGi Alliance (2011, 2013). All Rights Reserved.
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
 * Strategy for the {@link FieldReference} annotation.
 * 
 * <p>
 * Specifies if the field reference is handled with the replace or the
 * update strategy.
 * 
 * @since 1.3
 * @author $Id$
 */
public enum FieldReferenceStrategy {
	
	/**
	 * The update strategy is used to update to field when changes
	 * to the referenced service occur.
	 */
	UPDATE("update"),
	
	/**
	 * The replace strategy is used to update to field when changes
	 * to the referenced service occur.
	 */
	REPLACE("replace");

	private final String	value;

	FieldReferenceStrategy(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
