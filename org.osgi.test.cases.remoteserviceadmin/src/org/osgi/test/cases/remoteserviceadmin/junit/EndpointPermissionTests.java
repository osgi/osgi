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

package org.osgi.test.cases.remoteserviceadmin.junit;

import static org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_URI;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointPermission;
import org.osgi.test.support.PermissionTestCase;

public class EndpointPermissionTests extends PermissionTestCase {

	public void testInvalid() {
		// invalid actions
		invalidEndpointPermission("(a=b)", "x");
		invalidEndpointPermission("(a=b)", "   import  ,  x   ");
		invalidEndpointPermission("(a=b)", "");
		invalidEndpointPermission("(a=b)", "      ");
		invalidEndpointPermission("(a=b)", null);
		invalidEndpointPermission("(a=b)", ",");
		invalidEndpointPermission("(a=b)", ",xxx");
		invalidEndpointPermission("(a=b)", "xxx,");
		invalidEndpointPermission("(a=b)", "import,");
		invalidEndpointPermission("(a=b)", "export,   ");
		invalidEndpointPermission("(a=b)", "read,   ");
		invalidEndpointPermission("(a=b)", "importme,");
		invalidEndpointPermission("(a=b)", "exportme,");
		invalidEndpointPermission("(a=b)", "readme,");
		invalidEndpointPermission("(a=b)", ",import");
		invalidEndpointPermission("(a=b)", ",export");
		invalidEndpointPermission("(a=b)", ",read");
		invalidEndpointPermission("(a=b)", "   importme   ");
		invalidEndpointPermission("(a=b)", "   exportme     ");
		invalidEndpointPermission("(a=b)", "   readme     ");
		invalidEndpointPermission("(a=b)", "   impor");
		invalidEndpointPermission("(a=b)", "   expor");
		invalidEndpointPermission("(a=b)", "   rea");

		// invalid filter
		invalidEndpointPermission("()", "import");
		invalidEndpointPermission("foo", "import");
		invalidEndpointPermission("(foo=bar", "import");

		// null arg
		invalidEndpointPermission((String) null, "import");
		invalidEndpointPermission((EndpointDescription) null, "import");
		invalidEndpointPermission("(a=b)", null);
	}

	public void testActions() {
		String someURI = "someuri";
		Map<String, Object> ep = new HashMap<String, Object>();
		ep.put(ENDPOINT_URI, someURI);
		EndpointDescription ed = new EndpointDescription(ep);
		String filterString = "(" + ENDPOINT_URI + "=" + someURI + ")";
		Permission op = new PropertyPermission("java.home", "read");

		EndpointPermission p11 = new EndpointPermission(filterString,
				"    IMPORT,read   ");
		EndpointPermission p12 = new EndpointPermission(filterString,
				"READ  ,   import");
		EndpointPermission p13 = new EndpointPermission(filterString,
				"expORT   ");

		EndpointPermission p14 = new EndpointPermission(filterString,
				"    Import    ");
		EndpointPermission p15 = new EndpointPermission(filterString, "rEAd   ");
		EndpointPermission p16 = new EndpointPermission(filterString,
				"rEAd,export");
		EndpointPermission p17 = new EndpointPermission(filterString,
				"impORt, EXport");
		EndpointPermission p18 = new EndpointPermission(filterString,
				"rEAd, impORt, EXport");

		EndpointPermission p21 = new EndpointPermission(ed,
				"    IMPORT,read   ");
		EndpointPermission p22 = new EndpointPermission(ed, "READ  ,   import");
		EndpointPermission p23 = new EndpointPermission(ed, "expORT   ");

		EndpointPermission p24 = new EndpointPermission(ed, "    Import    ");
		EndpointPermission p25 = new EndpointPermission(ed, "rEAd   ");
		EndpointPermission p26 = new EndpointPermission(ed, "rEAd,export");
		EndpointPermission p27 = new EndpointPermission(ed, "impORt, EXport");
		EndpointPermission p28 = new EndpointPermission(ed,
				"rEAd, impORt, EXport");

		assertEquals("read,import", p11.getActions());
		assertEquals("read,import", p12.getActions());
		assertEquals("read,export", p13.getActions());
		assertEquals("read,import", p14.getActions());
		assertEquals("read", p15.getActions());
		assertEquals("read,export", p16.getActions());
		assertEquals("read,import,export", p17.getActions());
		assertEquals("read,import,export", p18.getActions());
		assertEquals("read,import", p21.getActions());
		assertEquals("read,import", p22.getActions());
		assertEquals("read,export", p23.getActions());
		assertEquals("read,import", p24.getActions());
		assertEquals("read", p25.getActions());
		assertEquals("read,export", p26.getActions());
		assertEquals("read,import,export", p27.getActions());
		assertEquals("read,import,export", p28.getActions());

		assertImplies(p11, p21);
		assertImplies(p11, p22);
		assertNotImplies(p11, p23);
		assertImplies(p11, p24);
		assertImplies(p11, p25);
		assertNotImplies(p11, p26);
		assertNotImplies(p11, p27);
		assertNotImplies(p11, p28);

		assertNotImplies(p11, p11);
		assertNotImplies(p11, p12);
		assertNotImplies(p11, p13);
		assertNotImplies(p11, p14);
		assertNotImplies(p11, p15);
		assertNotImplies(p11, p16);
		assertNotImplies(p11, p17);
		assertNotImplies(p11, p18);

		assertImplies(p15, p25);

		assertNotImplies(p14, p14);
		assertNotImplies(p15, p15);

		assertNotImplies(p13, p21);
		assertNotImplies(p13, p22);
		assertImplies(p13, p23);
		assertNotImplies(p13, p24);
		assertImplies(p13, p25);
		assertImplies(p13, p26);
		assertNotImplies(p13, p27);
		assertNotImplies(p13, p28);

		assertImplies(p14, p21);
		assertImplies(p14, p22);
		assertNotImplies(p14, p23);
		assertImplies(p14, p24);
		assertImplies(p14, p25);
		assertNotImplies(p14, p26);
		assertNotImplies(p14, p27);
		assertNotImplies(p14, p28);

		assertImplies(p18, p21);
		assertImplies(p18, p22);
		assertImplies(p18, p23);
		assertImplies(p18, p24);
		assertImplies(p18, p26);
		assertImplies(p18, p27);
		assertImplies(p18, p28);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertNotEquals(p11, p13);
		assertEquals(p11, p14);
		assertNotEquals(p11, p15);
		assertNotEquals(p11, p16);
		assertNotEquals(p11, p17);
		assertNotEquals(p11, p18);

		assertEquals(p12, p11);
		assertEquals(p12, p14);

		assertEquals(p13, p16);
		assertNotEquals(p13, p14);
		assertNotEquals(p13, p15);

		assertEquals(p14, p11);
		assertEquals(p14, p12);
		assertNotEquals(p14, p15);

		assertNotEquals(p15, p11);
		assertNotEquals(p15, p14);

		assertEquals(p16, p13);
		assertNotEquals(p16, p14);

		assertEquals(p17, p18);
		assertNotEquals(p17, p15);
		assertEquals(p18, p17);

		assertEquals(p21, p21);
		assertEquals(p21, p22);
		assertNotEquals(p21, p23);
		assertEquals(p21, p24);
		assertNotEquals(p21, p25);
		assertNotEquals(p21, p26);
		assertNotEquals(p21, p27);
		assertNotEquals(p21, p28);

		assertEquals(p22, p21);
		assertEquals(p22, p24);

		assertEquals(p23, p26);
		assertNotEquals(p23, p24);
		assertNotEquals(p23, p25);

		assertEquals(p24, p21);
		assertEquals(p24, p22);
		assertNotEquals(p24, p25);

		assertNotEquals(p25, p21);
		assertNotEquals(p25, p24);

		assertEquals(p26, p23);
		assertNotEquals(p26, p24);

		assertEquals(p27, p28);
		assertNotEquals(p27, p25);
		assertEquals(p28, p27);

		PermissionCollection pc = p14.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p21);

		assertAddPermission(pc, p14);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertNotImplies(pc, p27);
		assertNotImplies(pc, p28);

		assertAddPermission(pc, p13);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertImplies(pc, p26);
		assertImplies(pc, p27);
		assertImplies(pc, p28);
		assertSerializable(pc);

		pc = p17.newPermissionCollection();
		assertAddPermission(pc, p17);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertImplies(pc, p26);
		assertImplies(pc, p27);
		assertImplies(pc, p28);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc = p18.newPermissionCollection();

		assertAddPermission(pc, p18);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertImplies(pc, p26);
		assertImplies(pc, p27);
		assertImplies(pc, p28);
		assertSerializable(pc);

		assertNotAddPermission(pc, p22);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(p15);
		assertSerializable(p16);
		assertSerializable(p17);
		assertSerializable(p18);
		assertSerializable(pc);

		assertNotSerializable(p21);
		assertNotSerializable(p22);
		assertNotSerializable(p23);
		assertNotSerializable(p24);
		assertNotSerializable(p25);
		assertNotSerializable(p26);
		assertNotSerializable(p27);
		assertNotSerializable(p28);
	}

	public void testActionImplications() {
		EndpointPermission read = new EndpointPermission("*", "read");
		EndpointPermission export = new EndpointPermission("*", "export");
		EndpointPermission importx = new EndpointPermission("*", "import");

		assertEquals("read", read.getActions());
		assertEquals("read,export", export.getActions());
		assertEquals("read,import", importx.getActions());

		assertImplies(export, export);
		assertImplies(importx, importx);
		assertImplies(read, read);

		assertNotImplies(export, importx);
		assertImplies(export, read);

		assertNotImplies(importx, export);
		assertImplies(importx, read);

		assertNotImplies(read, importx);
		assertNotImplies(read, export);

		String someURI = "someuri";
		Map<String, Object> ep = new HashMap<String, Object>();
		ep.put(ENDPOINT_URI, someURI);
		EndpointDescription ed = new EndpointDescription(ep);
		String filterString = "(" + ENDPOINT_URI + "=" + someURI + ")";

		EndpointPermission importuri = new EndpointPermission(filterString,
				"import");
		EndpointPermission both = new EndpointPermission(ed, "import,export");
		PermissionCollection pc = importx.newPermissionCollection();
		assertAddPermission(pc, export);
		assertAddPermission(pc, importuri);
		assertImplies(pc, both);
	}

	public void testFiltersName() {
		EndpointPermission p31 = new EndpointPermission(
				"  (endpoint.uri  =com.foo.service2)", "import");
		EndpointPermission p32 = new EndpointPermission(
				"(endpoint.uri=com.foo.*)", "import");
		EndpointPermission p33 = new EndpointPermission("(endpoint.uri=com.*)",
				"import");
		EndpointPermission p34 = new EndpointPermission("(endpoint.uri=*)",
				"import");
		Map<String, Object> ep = new HashMap<String, Object>();
		ep.put(ENDPOINT_URI, "com.foo.service2");
		EndpointDescription ed = new EndpointDescription(ep);
		EndpointPermission p35 = new EndpointPermission(ed, "import");
		EndpointPermission p38 = new EndpointPermission("*", "import");

		assertImplies(p31, p35);
		assertImplies(p32, p35);
		assertImplies(p33, p35);
		assertImplies(p34, p35);
		assertImplies(p38, p35);

		assertNotImplies(p31, p31);
		assertNotImplies(p31, p32);
		assertNotImplies(p31, p33);
		assertNotImplies(p31, p34);

		assertNotImplies(p31, p38);
		assertNotImplies(p32, p38);
		assertNotImplies(p33, p38);
		assertNotImplies(p34, p38);
		assertNotImplies(p35, p38);
		assertImplies(p38, p38);

		PermissionCollection pc = p31.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p31);

		assertNotImplies(pc, p31);
		assertNotImplies(pc, p32);
		assertNotImplies(pc, p33);
		assertNotImplies(pc, p34);

		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		checkEnumeration(pc.elements(), false);
		assertSerializable(pc);

		pc = p32.newPermissionCollection();
		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p33.newPermissionCollection();
		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p34.newPermissionCollection();
		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertNotSerializable(p35);
		assertSerializable(p38);
		assertSerializable(pc);
	}

	public void testFiltersProperties() {

		EndpointPermission p41 = new EndpointPermission("(id=2)", "import");
		EndpointPermission p42 = new EndpointPermission("(location=test.*)",
				"import");
		EndpointPermission p43 = new EndpointPermission("(name=test.*)",
				"import");
		EndpointPermission p45 = new EndpointPermission(
				"(endpoint.uri=com.foo.*)", "import");

		Map<String, Object> ep1 = new HashMap<String, Object>();
		ep1.put(ENDPOINT_URI, "com.foo.service2");
		ep1.put("id", "2");
		ep1.put("location", "test.location");
		ep1.put("name", "test.bsn");
		EndpointPermission p46 = new EndpointPermission(
				new EndpointDescription(ep1), "import");
		Map<String, Object> ep2 = new HashMap<String, Object>();
		ep2.put(ENDPOINT_URI, "com.bar.service2");
		ep2.put("id", "3");
		ep2.put("location", "not.location");
		ep2.put("name", "not.bsn");
		EndpointPermission p47 = new EndpointPermission(
				new EndpointDescription(ep2), "import");

		assertImplies(p41, p46);
		assertImplies(p42, p46);
		assertImplies(p43, p46);
		assertImplies(p45, p46);

		assertNotImplies(p41, p47);
		assertNotImplies(p42, p47);
		assertNotImplies(p43, p47);
		assertNotImplies(p45, p47);

		assertNotImplies(p41, p41);
		assertNotImplies(p41, p42);
		assertNotImplies(p41, p43);
		assertNotImplies(p41, p45);

		PermissionCollection pc = p41.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

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
		// added was constructed with EndpointDescription.
		assertNotAddPermission(pc, p46);
		assertNotAddPermission(pc, p47);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p45);
		assertNotSerializable(p46);
		assertNotSerializable(p47);
		assertSerializable(pc);
	}

	public void testPermissionCollection() {
		EndpointPermission p51 = new EndpointPermission(
				"  (endpoint.uri  =com.foo.service2)", "import");
		EndpointPermission p52 = new EndpointPermission(
				"(endpoint.uri=com.foo.*)", "import");
		EndpointPermission p53 = new EndpointPermission("(endpoint.uri=com.*)",
				"import");
		EndpointPermission p54 = new EndpointPermission("(endpoint.uri=*)",
				"import");
		Map<String, Object> ep = new HashMap<String, Object>();
		ep.put(ENDPOINT_URI, "com.foo.service2");
		EndpointDescription ed = new EndpointDescription(ep);
		EndpointPermission p55 = new EndpointPermission(ed, "import");
		EndpointPermission p59 = new EndpointPermission("*", "import");

		Map<String, Object> ep1 = new HashMap<String, Object>();
		ep1.put(ENDPOINT_URI, "com.foo.service2");
		ep1.put("id", "2");
		ep1.put("location", "test.location");
		ep1.put("name", "test.bsn");
		EndpointPermission p5a = new EndpointPermission(
				new EndpointDescription(ep1), "import");
		Map<String, Object> ep2 = new HashMap<String, Object>();
		ep2.put(ENDPOINT_URI, "com.bar.service2");
		ep2.put("id", "3");
		ep2.put("location", "not.location");
		ep2.put("name", "not.bsn");
		EndpointPermission p5b = new EndpointPermission(
				new EndpointDescription(ep2), "import");

		PermissionCollection pc;
		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(p51, p5a);
		assertImplies(pc, p5a);
		assertImplies(p51, p55);
		assertImplies(pc, p55);
		assertNotImplies(p51, p5b);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertImplies(p52, p5a);
		assertImplies(pc, p5a);
		assertImplies(p52, p55);
		assertImplies(pc, p55);
		assertNotImplies(p52, p5b);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertImplies(p53, p5a);
		assertImplies(pc, p5a);
		assertImplies(p53, p55);
		assertImplies(pc, p55);
		assertImplies(p53, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertAddPermission(pc, p59);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertImplies(p54, p5a);
		assertImplies(pc, p5a);
		assertImplies(p54, p55);
		assertImplies(pc, p55);
		assertImplies(p54, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p59.newPermissionCollection();
		assertAddPermission(pc, p59);
		assertImplies(p59, p5a);
		assertImplies(pc, p5a);
		assertImplies(p59, p55);
		assertImplies(pc, p55);
		assertImplies(p59, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);
	}

	private void invalidEndpointPermission(String filterString, String actions) {
		try {
			EndpointPermission p = new EndpointPermission(filterString, actions);
			fail(p + " created with invalid actions");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private static void invalidEndpointPermission(EndpointDescription endpoint,
			String actions) {
		try {
			EndpointPermission p = new EndpointPermission(endpoint, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

}
