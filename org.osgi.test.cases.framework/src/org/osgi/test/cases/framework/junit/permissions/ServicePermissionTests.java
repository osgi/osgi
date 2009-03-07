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

package org.osgi.test.cases.framework.junit.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.PermissionTestCase;

public class ServicePermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidServicePermission("a.b.c", "x");
		invalidServicePermission("a.b.c", "   get  ,  x   ");
		invalidServicePermission("a.b.c", "");
		invalidServicePermission("a.b.c", "      ");
		invalidServicePermission("a.b.c", null);
		invalidServicePermission("a.b.c", ",");
		invalidServicePermission("a.b.c", ",xxx");
		invalidServicePermission("a.b.c", "xxx,");
		invalidServicePermission("a.b.c", "get,");
		invalidServicePermission("a.b.c", "register,   ");
		invalidServicePermission("a.b.c", "getme,");
		invalidServicePermission("a.b.c", "registerme,");
		invalidServicePermission("a.b.c", ",get");
		invalidServicePermission("a.b.c", ",register");
		invalidServicePermission("a.b.c", "   getme   ");
		invalidServicePermission("a.b.c", "   registerme     ");
		invalidServicePermission("a.b.c", "   ge");
		invalidServicePermission("a.b.c", "   registe"); 
		invalidServicePermission("()", "get");
		invalidServicePermission("(objectClass=a.b.c)", "register");
		invalidServicePermission("(objectClass=a.b.c)", "register,GET");
		invalidServicePermission("(objectClass=a.b.c)", "geT,Register");

		invalidServicePermission((ServiceReference) null, "get");
		Map properties = new HashMap();
		properties.put("service.id", new Long(1));
		properties.put("objectClass", new String[] {"test.Service"});
		invalidServicePermission(newMockServiceReference(null, properties),
				"register");
		invalidServicePermission(newMockServiceReference(null, properties),
				"register,get");
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read"); 

		ServicePermission p11 = new ServicePermission("com.foo.service1",
				"    GET,register   ");
		ServicePermission p12 = new ServicePermission("com.foo.service1",
				"REGISTER  ,   get");
		ServicePermission p13 = new ServicePermission("com.foo.service1",
				"regisTER   ");
		ServicePermission p14 = new ServicePermission("com.foo.service1",
				"    Get    "); 

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);

		assertImplies(p12, p11);
		assertImplies(p12, p12);
		assertImplies(p12, p13);
		assertImplies(p12, p14);

		assertImplies(p13, p13);
		assertImplies(p14, p14);

		assertNotImplies(p13, p11);
		assertNotImplies(p13, p12);

		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);

		assertNotImplies(p13, p14);
		assertNotImplies(p14, p13);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p12, p11);
		assertEquals(p12, p12);
		assertEquals(p13, p13);
		assertEquals(p14, p14);

		assertNotEquals(p11, p13);
		assertNotEquals(p11, p14);
		assertNotEquals(p12, p13);
		assertNotEquals(p12, p14);
		assertNotEquals(p13, p11);
		assertNotEquals(p13, p12);
		assertNotEquals(p13, p14);
		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p11);

		assertAddPermission(pc, p14);
		assertImplies(pc, p14);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p13);

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertImplies(pc, p13);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p14);

		assertAddPermission(pc, p14);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertSerializable(pc);

		pc = p11.newPermissionCollection();

		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);
		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(pc);
	}

	public void testNames() {
		ServicePermission p21 = new ServicePermission("com.foo.service2", "get");
		ServicePermission p22 = new ServicePermission("com.foo.*", "get");
		ServicePermission p23 = new ServicePermission("com.*", "get");
		ServicePermission p24 = new ServicePermission("*", "get"); 

		assertImplies(p21, p21);
		assertImplies(p22, p21);
		assertImplies(p23, p21);
		assertImplies(p24, p21);

		assertImplies(p22, p22);
		assertImplies(p23, p22);
		assertImplies(p24, p22);

		assertImplies(p23, p23);
		assertImplies(p24, p23);

		assertImplies(p24, p24);

		assertNotImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);

		assertNotImplies(p22, p23);
		assertNotImplies(p22, p24);

		assertNotImplies(p23, p24);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertSerializable(pc);

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertSerializable(pc);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertSerializable(pc);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
		assertSerializable(pc);
	}
	
	public void testActionImplications() {
		ServicePermission register = new ServicePermission("*", "register");
		ServicePermission get = new ServicePermission("*", "get");

		assertImplies(register, register);
		assertNotImplies(register, get);
		assertNotImplies(get, register);
		assertImplies(get, get);

		ServicePermission comfooget = new ServicePermission("com.foo.*", "get");
		ServicePermission both = new ServicePermission("com.foo.Bar",
				"register,get");
		PermissionCollection pc = register.newPermissionCollection();
		assertAddPermission(pc, register);
		assertAddPermission(pc, comfooget);
		assertImplies(pc, both);
	}

	public void testFiltersName() {
		ServicePermission p31 = new ServicePermission(
				"  (objectClass  =com.foo.Service2)", "get");
		ServicePermission p32 = new ServicePermission(
				"(objectClass=com.foo.*)", "get");
		ServicePermission p33 = new ServicePermission("(objectClass=com.*)",
				"get");
		ServicePermission p34 = new ServicePermission("(objectClass=*)",
				"get");
		ServicePermission p35 = new ServicePermission("com.foo.Service2",
				"get");
		ServicePermission p36 = new ServicePermission("com.foo.*", "get");
		ServicePermission p37 = new ServicePermission("com.*", "get");
		ServicePermission p38 = new ServicePermission("*", "get");

		assertImplies(p31, p35);
		assertImplies(p32, p35);
		assertImplies(p33, p35);
		assertImplies(p34, p35);
		assertImplies(p36, p35);
		assertImplies(p37, p35);
		assertImplies(p38, p35);

		assertNotImplies(p31, p31);
		assertNotImplies(p31, p32);
		assertNotImplies(p31, p33);
		assertNotImplies(p31, p34);

		assertNotImplies(p31, p36);
		assertImplies(p32, p36);
		assertImplies(p33, p36);
		assertImplies(p34, p36);
		assertNotImplies(p35, p36);
		assertImplies(p36, p36);
		assertImplies(p37, p36);
		assertImplies(p38, p36);

		assertNotImplies(p31, p37);
		assertNotImplies(p32, p37);
		assertImplies(p33, p37);
		assertImplies(p34, p37);
		assertNotImplies(p35, p37);
		assertNotImplies(p36, p37);
		assertImplies(p37, p37);
		assertImplies(p38, p37);

		assertNotImplies(p31, p38);
		assertNotImplies(p32, p38);
		assertNotImplies(p33, p38);
		assertImplies(p34, p38);
		assertNotImplies(p35, p38);
		assertNotImplies(p36, p38);
		assertNotImplies(p37, p38);
		assertImplies(p38, p38);

		PermissionCollection pc = p31.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p31);

		assertNotImplies(pc, p31);
		assertNotImplies(pc, p32);
		assertNotImplies(pc, p33);
		assertNotImplies(pc, p34);

		assertImplies(pc, p35);
		assertNotImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertImplies(pc, p38);

		checkEnumeration(pc.elements(), false);
		assertSerializable(pc);

		pc = p32.newPermissionCollection();
		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p33.newPermissionCollection();
		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p34.newPermissionCollection();
		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertImplies(pc, p38);

		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertSerializable(p35);
		assertSerializable(p36);
		assertSerializable(pc);
	}

	public void testFiltersServiceReference() {
		ServicePermission p41 = new ServicePermission("(id=2)", "get");
		ServicePermission p42 = new ServicePermission("(location=test.*)",
				"get");
		ServicePermission p43 = new ServicePermission("(name=test.*)", "get");
		ServicePermission p44 = new ServicePermission(
				"(signer=\\*, o=ACME, c=US)", "get");
		ServicePermission p45 = new ServicePermission(
				"(objectClass=com.foo.*)", "get");

		Bundle b46 = newMockBundle(2, "test.bsn", "test.location",
				"cn=Bugs Bunny, o=ACME, c=US");
		Map m46 = new HashMap();
		m46.put("service.id", new Long(2));
		m46.put("objectClass", new String[] {"com.foo.Service2"});
		ServiceReference r46 = newMockServiceReference(b46, m46);
		ServicePermission p46 = new ServicePermission(r46, "get");

		Bundle b47 = newMockBundle(3, "not.bsn", "not.location",
				"cn=Bugs Bunny, o=NOT, c=US");
		Map m47 = new HashMap();
		m47.put("service.id", new Long(3));
		m47.put("objectClass", new String[] {"com.bar.Service2"});
		ServiceReference r47 = newMockServiceReference(b47, m47);
		ServicePermission p47 = new ServicePermission(r47, "get");

		assertImplies(p41, p46);
		assertImplies(p42, p46);
		assertImplies(p43, p46);
		assertImplies(p44, p46);
		assertImplies(p45, p46);

		assertNotImplies(p41, p47);
		assertNotImplies(p42, p47);
		assertNotImplies(p43, p47);
		assertNotImplies(p44, p47);
		assertNotImplies(p45, p47);

		assertNotImplies(p41, p41);
		assertNotImplies(p41, p42);
		assertNotImplies(p41, p43);
		assertNotImplies(p41, p44);
		assertNotImplies(p41, p45);

		PermissionCollection pc = p41.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p44);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);

		assertNotImplies(pc, p41);
		assertNotImplies(pc, p42);
		assertNotImplies(pc, p43);
		assertNotImplies(pc, p44);
		assertNotImplies(pc, p45);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p43);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p42);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p41);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		checkEnumeration(pc.elements(), false);

		// throw IllegalArgumentException If specified permission to be
		// added was constructed with service references.
		assertNotAddPermission(pc, p46);
		assertNotAddPermission(pc, p47);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p44);
		assertSerializable(p45);
		assertNotSerializable(p46);
		assertNotSerializable(p47);
		assertSerializable(pc);
	}

	public void testPermissionCollection() {
		ServicePermission p51 = new ServicePermission(
				"  (objectClass  =com.foo.Service2)", "get");
		ServicePermission p52 = new ServicePermission(
				"(objectClass=com.foo.*)", "get");
		ServicePermission p53 = new ServicePermission("(objectClass=com.*)",
				"get");
		ServicePermission p54 = new ServicePermission("(objectClass=*)",
				"get");
		ServicePermission p55 = new ServicePermission("com.foo.Service2",
				"get");

		ServicePermission p56 = new ServicePermission("com.bar.Service2",
				"get");
		ServicePermission p57 = new ServicePermission("com.bar.*", "get");
		ServicePermission p58 = new ServicePermission("com.*", "get");
		ServicePermission p59 = new ServicePermission("*", "get");

		Bundle b5a = newMockBundle(2, "test.bsn", "test.location",
		"cn=Bugs Bunny, o=ACME, c=US");
		Map m5a = new HashMap();
		m5a.put("service.id", new Long(2));
		m5a.put("objectClass", new String[] {"com.foo.Service2"});
		ServiceReference r5a = newMockServiceReference(b5a, m5a);
		ServicePermission p5a = new ServicePermission(r5a, "get");
		
		Bundle b5b = newMockBundle(3, "not.bsn", "not.location",
				"cn=Bugs Bunny, o=NOT, c=US");
		Map m5b = new HashMap();
		m5b.put("service.id", new Long(3));
		m5b.put("objectClass", new String[] {"com.bar.Service1",
				"com.bar.Service2"});
		ServiceReference r5b = newMockServiceReference(b5b, m5b);
		ServicePermission p5b = new ServicePermission(r5b, "get");

		PermissionCollection pc;
		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(p51, p5a);
		assertImplies(pc, p5a);
		assertImplies(p51, p55);
		assertImplies(pc, p55);
		assertNotImplies(p51, p5b);
		assertNotImplies(pc, p5b);
		assertNotImplies(p51, p56);
		assertNotImplies(pc, p56);
		assertSerializable(pc);

		pc = p56.newPermissionCollection();
		assertAddPermission(pc, p56);
		assertNotImplies(p56, p5a);
		assertNotImplies(pc, p5a);
		assertNotImplies(p56, p55);
		assertNotImplies(pc, p55);
		assertImplies(p56, p5b);
		assertImplies(pc, p5b);
		assertImplies(p56, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertAddPermission(pc, p56);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertAddPermission(pc, p57);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertImplies(p52, p5a);
		assertImplies(pc, p5a);
		assertImplies(p52, p55);
		assertImplies(pc, p55);
		assertNotImplies(p52, p5b);
		assertNotImplies(pc, p5b);
		assertNotImplies(p52, p56);
		assertNotImplies(pc, p56);
		assertSerializable(pc);

		pc = p57.newPermissionCollection();
		assertAddPermission(pc, p57);
		assertNotImplies(p57, p5a);
		assertNotImplies(pc, p5a);
		assertNotImplies(p57, p55);
		assertNotImplies(pc, p55);
		assertImplies(p57, p5b);
		assertImplies(pc, p5b);
		assertImplies(p57, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertAddPermission(pc, p58);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertImplies(p53, p5a);
		assertImplies(pc, p5a);
		assertImplies(p53, p55);
		assertImplies(pc, p55);
		assertImplies(p53, p5b);
		assertImplies(pc, p5b);
		assertImplies(p53, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p58.newPermissionCollection();
		assertAddPermission(pc, p58);
		assertImplies(p58, p5a);
		assertImplies(pc, p5a);
		assertImplies(p58, p55);
		assertImplies(pc, p55);
		assertImplies(p58, p5b);
		assertImplies(pc, p5b);
		assertImplies(p58, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertAddPermission(pc, p59);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertImplies(p54, p5a);
		assertImplies(pc, p5a);
		assertImplies(p54, p55);
		assertImplies(pc, p55);
		assertImplies(p54, p5b);
		assertImplies(pc, p5b);
		assertImplies(p54, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p59.newPermissionCollection();
		assertAddPermission(pc, p59);
		assertImplies(p59, p5a);
		assertImplies(pc, p5a);
		assertImplies(p59, p55);
		assertImplies(pc, p55);
		assertImplies(p59, p5b);
		assertImplies(pc, p5b);
		assertImplies(p59, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);
	}
	
	public void testServiceProperties() {
		ServicePermission p61 = new ServicePermission(
				"(&(@Name=expected)(@@AT=atat)(name=test.bsn))", "get");
		Bundle b62 = newMockBundle(2, "test.bsn", "test.location",
				"cn=Bugs Bunny, o=ACME, c=US");
		Map m62 = new HashMap();
		m62.put("service.id", new Long(2));
		m62.put("objectClass", new String[] {"com.foo.Service2"});
		m62.put("name", "expected");
		m62.put("@at", "atat");
		ServiceReference r62 = newMockServiceReference(b62, m62);
		ServicePermission p62 = new ServicePermission(r62, "get");
		assertImplies(p61, p62);
	}
	
	private static void invalidServicePermission(String name, String actions) {
		try {
			ServicePermission p = new ServicePermission(name, actions);
			fail(p + " created with invalid arguments"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	private static void invalidServicePermission(ServiceReference service,
			String actions) {
		try {
			ServicePermission p = new ServicePermission(service, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
