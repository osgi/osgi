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

package org.osgi.test.cases.metatype.annotations.junit;

import static org.osgi.test.cases.metatype.annotations.junit.OCDXPathAssert.assertThat;
import static org.osgi.test.support.xpath.XPathAssert.assertThat;

import org.junit.Test;

/**
 * @author $Id$
 */
public class MetatypeAnnotations14TestCase extends AnnotationsTestCase {

	@Test
	public void testNameMapping14() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasNamespace(xmlns_metatype140)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("prefix.hyphen-minus", "String", 0, "-");
	}

	@Test
	public void testSingleElement() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasNamespace(xmlns_metatype140)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("single.element", "Integer", 0);
	}

	@Test
	public void testPidDollar() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("pid.dollar", "Integer", 0);

		String pid = getSelf(ocd);
		Designate designate = designatePids.get(pid);
		assertThat(designate).as("designate pid %s", pid)
				.hasValue("Object/@ocdref", name)
				.doesNotContain("@factoryPid");
	}

	@Test
	public void testFactoryPidDollar() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("factory.pid.dollar", "Integer", 0);

		String factoryPid = getSelf(ocd);
		Designate designate = designateFactoryPids.get(factoryPid);
		assertThat(designate).as("designate factoryPid %s", factoryPid)
				.hasValue("Object/@ocdref", name)
				.doesNotContain("@pid");
	}

}
