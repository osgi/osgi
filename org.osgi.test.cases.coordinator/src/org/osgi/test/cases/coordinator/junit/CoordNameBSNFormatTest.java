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
package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * A coordination's name must follow the Bundle Symbolic Name pattern.
 * 
 * The syntax of a Bundle Symbolic Name is as follows.
 * 
 * digit ::= [0..9]
 * alpha ::= [a..zA..Z]
 * alphanum ::= alpha | digit
 * token ::= ( alphanum | ’_’ | ’-’ )+
 * symbolic-name ::= token('.'token)*
 *
 */
public class CoordNameBSNFormatTest extends OSGiTestCase {
	Coordinator coordinator;
	private ServiceReference<Coordinator> coordinatorReference;
	
	/**
	 * Coordinator.begin() should throw an IllegalArgumentException if a
	 * coordination's name does not follow the Bundle Symbolic Name pattern.
	 */
	public void testBadNames() {
		assertNameBad("#");
		assertNameBad("01234!6789");
		assertNameBad("abcdef(ghi)jklmnopqrstuvwxyz");
		assertNameBad("ABCDE*FGHIJKLMNOPQRST&UVWXYZ");
		assertNameBad("_ -");
		assertNameBad("org.OSGI.service:CoorDinator");
		assertNameBad("hello_world.goodbye+cruel.world");
	}
	
	/**
	 * Coordinator.begin() should not throw an IllegalArgumentException if a
	 * coordination's name follows the Bundle Symbolic Name pattern.
	 */
	public void testGoodNames() {
		assertNameGood("0123456789");
		assertNameGood("abcdefghijklmnopqrstuvwxyz");
		assertNameGood("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		assertNameGood("_-");
		assertNameGood("org.OSGI.service.CoorDinator");
		assertNameGood("hello_world.goodbye-cruel.world");
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertNameBad(String name) {
		try {
			Coordination c = coordinator.create(name, 0);
			c.end();
			fail("IllegalArgumentException should be thrown if a coordination's name does not match the Bundle Symbolic Name pattern");
		}
		catch (IllegalArgumentException e) {
			// Okay
		}
	}
	
	private void assertNameGood(String name) {
		try {
			Coordination c = coordinator.begin(name, 0);
			c.end();
		}
		catch (IllegalArgumentException e) {
			fail("IllegalArgumentException should not be thrown if a coordination's name matches the Bundle Symbolic Name pattern");
		}
	}
}
