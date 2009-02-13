/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public abstract class PermissionTestCase extends OSGiTestCase {

	public static void checkEnumeration(Enumeration en, boolean isEmpty) {
		assertEquals(en + " empty state is invalid", !isEmpty, en
				.hasMoreElements()); 
		try {
			while (en.hasMoreElements()) {
				en.nextElement();
			}
		} catch (NoSuchElementException e) {
			fail(en + " threw NoSuchElementException"); 
		}

		try {
			en.nextElement();
			fail(en + " is empty but didn't throw NoSuchElementException"); 
		} catch (NoSuchElementException e) {
			// expected
		}
	}

	public static void assertImplies(Permission p1, Permission p2) {
		assertTrue(p1 + " does not imply " + p2, p1.implies(p2)); 
	}

	public static void assertNotImplies(Permission p1, Permission p2) {
		assertFalse(p1 + " does imply " + p2, p1.implies(p2)); 
	}

	public static void assertImplies(PermissionCollection p1, Permission p2) {
		assertTrue(p1 + " does not imply " + p2, p1.implies(p2)); 
	}

	public static void assertNotImplies(PermissionCollection p1, Permission p2) {
		assertFalse(p1 + " does imply " + p2, p1.implies(p2)); 
	}

	public static void assertEquals(Permission p1, Permission p2) {
		assertEquals(p1 + " does not equal " + p2, p1, p2); 
		assertEquals(p1 + " hashcodes do not equal " + p2, p1.hashCode(), p2
				.hashCode()); 
	}

	public static void assertNotEquals(Permission p1, Permission p2) {
		assertFalse(p1 + " does equal " + p2, p1.equals(p2)); 
		assertFalse(p1 + " hashcodes equal " + p2, p1.hashCode() == p2
				.hashCode()); 
	}

	public static void assertAddPermission(PermissionCollection p1,
			Permission p2) {
		try {
			p1.add(p2);
		} catch (Exception e) {
			fail(p1 + " will not add " + p2); 
		}
	}

	public static void assertNotAddPermission(PermissionCollection p1,
			Permission p2) {
		try {
			p1.add(p2);
			fail(p1 + " will add " + p2); 
		} catch (Exception e) {
			// expected
		}
	}

	public static void assertSerializable(Permission p1) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);

			out.writeObject(p1);
			out.flush();
			out.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bais);

			Permission p2 = (Permission) in.readObject();

			assertEquals(p1, p2);
			assertEquals(p2, p1);
			assertNotSame(p1, p2);
		} catch (Exception e) {
			fail("serialization error", e);
		}
	}
}
