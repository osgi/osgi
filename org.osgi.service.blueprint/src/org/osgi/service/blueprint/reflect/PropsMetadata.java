/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.reflect;

import java.util.List;

/**
 * A java.util.Properties based value. The properties are defined as string to
 * string. This means that the actual value can be returned.
 *
 * ### I do not like it that you loose the original order. And potential errors
 * (like duplicate keys). I think this one should just go away. The
 * CollectionMetadata has a collection type so the instantiation can ensure
 *
 * Defined in the <code>props</code> element.
 *
 */
public interface PropsMetadata extends NonNullMetadata {

	/**
	 * This is the same as getValuesMetadata but more type safe.
	 *
	 * Defined in <code>prop</code> sub elements.
	 *
	 * @return
	 */
	List/*<MapEntry>*/ getEntries();
}
