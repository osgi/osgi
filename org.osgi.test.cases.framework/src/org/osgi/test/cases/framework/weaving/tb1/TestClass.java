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
package org.osgi.test.cases.framework.weaving.tb1;

/**
 * This class is used as a basic weavable entity. The
 * TCK changes the value of the constant returned by
 * {@link #toString()}.
 * 
 * @author IBM
 */
public class TestClass {

	public String toString() {
		return "f31aa0ab-f572-4c3a-b564-4e47f5935603_a5d56cb7-8987-416e-9212-26631f2924cd"
				.trim();
	}
}
