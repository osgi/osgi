/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

package org.osgi.test.cases.bundle.annotations.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.support.dictionary.DictionaryMap.asMap;

import org.junit.Test;

public class HeaderAnnotationsTestClass extends AnnotationsTestCase {

	@Test
	public void testHeaders() {
		assertThat(asMap(impl.getHeaders("")))
				.as("Headers missing from manifest of bundle %s", impl)
				.containsEntry("FooPackage", "bar-package")
				.containsEntry("FooClass", "bar-class");
	}
}
