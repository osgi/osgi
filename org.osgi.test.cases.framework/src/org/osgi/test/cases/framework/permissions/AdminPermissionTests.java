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

package org.osgi.test.cases.framework.permissions;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.Set;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.test.support.AbstractMockBundle;
import org.osgi.test.support.PermissionTestCase;

public class AdminPermissionTests extends PermissionTestCase {

	/*
	 * @param actions <code>class</code>, <code>execute</code>,
	 * <code>extensionLifecycle</code>, <code>lifecycle</code>,
	 * <code>listener</code>, <code>metadata</code>, <code>resolve</code> ,
	 * <code>resource</code>, <code>startlevel</code> or <code>context</code>. A
	 * value of "*" or <code>null</code> indicates all actions.
	 */
	public void testInvalidAdminPermissions() {
		invalidAdminPermission("*", "x");
		invalidAdminPermission("*", "   class  ,  x   ");
		invalidAdminPermission("*", "");
		invalidAdminPermission("*", "      ");
		invalidAdminPermission("*", ",");
		invalidAdminPermission("*", ",xxx");
		invalidAdminPermission("*", "xxx,");
		invalidAdminPermission("*", "execute,");
		invalidAdminPermission("*", "extensionLifecycle,   ");
		invalidAdminPermission("*", "lifecycleme,");
		invalidAdminPermission("*", "listenerme,");
		invalidAdminPermission("*", ",metadata");
		invalidAdminPermission("*", ",resolve");
		invalidAdminPermission("*", "   resourceme   ");
		invalidAdminPermission("*", "   startlevelme     ");
		invalidAdminPermission("*", "   contex");

		invalidAdminPermission("()", "*");
	}

	public void testDefaultAdminPermission() {
		AdminPermission p1 = new AdminPermission();
		AdminPermission p2 = new AdminPermission("*", "*");
		AdminPermission p3 = new AdminPermission((String) null, null);
		AdminPermission p4 = new AdminPermission((String) null, p2.getActions());
		Permission op = new PropertyPermission("java.home", "read");

		shouldImply(p1, p2);
		shouldImply(p2, p1);
		shouldImply(p1, p3);
		shouldImply(p3, p1);
		shouldImply(p3, p2);
		shouldImply(p2, p3);
		shouldImply(p1, p4);
		shouldImply(p4, p1);
		shouldImply(p4, p3);
		shouldImply(p3, p4);
		shouldImply(p3, p4);
		shouldImply(p4, p3);
		shouldImply(p4, p2);
		shouldImply(p2, p4);
		shouldImply(p1, p1);
		shouldImply(p2, p2);
		shouldImply(p3, p3);
		shouldImply(p4, p4);
		shouldNotImply(p1, op);

		shouldEqual(p1, p2);
		shouldEqual(p2, p1);
		shouldEqual(p1, p3);
		shouldEqual(p3, p1);
		shouldEqual(p2, p3);
		shouldEqual(p3, p2);
		shouldEqual(p3, p4);
		shouldEqual(p4, p3);
		shouldEqual(p2, p4);
		shouldEqual(p4, p2);
		shouldEqual(p1, p4);
		shouldEqual(p4, p1);
		shouldNotEqual(p1, op);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p1);

		shouldAdd(pc, p1);
		shouldAdd(pc, p2);
		shouldAdd(pc, p3);
		shouldAdd(pc, p4);
		shouldNotAdd(pc, op);

		pc.setReadOnly();

		shouldNotAdd(pc, new AdminPermission());

		shouldImply(pc, p1);
		shouldImply(pc, p2);
		shouldImply(pc, p3);
		shouldImply(pc, p4);
		shouldNotImply(pc, op);

		checkEnumeration(pc.elements(), false);

		testSerialization(p1);
		testSerialization(p2);
		testSerialization(p3);
		testSerialization(p4);
	}

	public void testFilterAdminPermission() {
		AdminPermission p1 = new AdminPermission("(id=2)", "class");
		AdminPermission p2 = new AdminPermission(" (id =2)", "class");
		AdminPermission p3 = new AdminPermission(newMockBundle(2, "test.bsn",
				"test.location", null), "resolve");
		AdminPermission p4 = new AdminPermission("(name=test.*)", "resource");
		AdminPermission p5 = new AdminPermission("(location=test.*)", "*");
		shouldImply(p1, p3);
		shouldImply(p1, p3);
		shouldImply(p4, p3);
		shouldImply(p5, p3);
		invalidImply(p1, p2);
		invalidImply(p2, p1);
		unsupportedImply(p3, p2);
		unsupportedImply(p3, p1);

		shouldEqual(p1, p2);
		shouldEqual(p2, p1);
		shouldNotEqual(p1, p3);
		shouldNotEqual(p2, p3);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p3);
		invalidImply(pc, p1);

		shouldAdd(pc, new AdminPermission("(id=2)", "class"));
		shouldAdd(pc, new AdminPermission("(id=2)", "resource"));

		Bundle testBundle1 = newMockBundle(2, "test.bsn", "test.location", null);
		Bundle testBundle2 = newMockBundle(1, "test.bsn", "test.location", null);
		shouldImply(pc, new AdminPermission(testBundle1, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle1, "class"));
		shouldImply(pc, new AdminPermission(testBundle1, "resource"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "resolve"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "class"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "resource"));
		shouldNotImply(pc, new AdminPermission("*", "resource"));

		shouldAdd(pc, new AdminPermission());
		shouldImply(pc, new AdminPermission(testBundle1, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle1, "class"));
		shouldImply(pc, new AdminPermission(testBundle1, "resource"));
		shouldImply(pc, new AdminPermission(testBundle2, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle2, "class"));
		shouldImply(pc, new AdminPermission(testBundle2, "resource"));
		shouldImply(pc, new AdminPermission("*", "resource"));

		invalidImply(pc, p1);

		checkEnumeration(pc.elements(), false);

		testSerialization(p1);
		testSerialization(p2);
		testSerialization(p4);
		testSerialization(p5);
	}

	public void testSigners() {
		AdminPermission ap = new AdminPermission("(signer=\\*, o=ACME, c=US)",
				"*");

		shouldImply(ap, new AdminPermission(newMockBundle(1, "test.bsn",
				"test.location", "cn=Bugs Bunny, o=ACME, c=US"), "*"));
		shouldImply(ap, new AdminPermission(newMockBundle(2, "test.bsn",
				"test.location", "ou = Carrots, cn=Daffy Duck, o=ACME, c=US"),
		"*"));
		shouldImply(ap, new AdminPermission(newMockBundle(3, "test.bsn",
				"test.location", "dc=www,dc=acme,dc=com,o=ACME,c=US"), "*"));
		shouldNotImply(ap, new AdminPermission(newMockBundle(4, "test.bsn",
				"test.location",
		"street = 9C\\, Avenue St. Drézéry, o=ACME, c=FR"), "*"));
		shouldNotImply(ap, new AdminPermission(newMockBundle(5, "test.bsn",
				"test.location", "dc=www, dc=acme, dc=com, c=US"), "*"));

		ap = new AdminPermission("(signer=cn=\\*,o=ACME,c=\\*)", "*");
		
		shouldImply(ap, new AdminPermission(newMockBundle(6, "test.bsn",
				"test.location", "cn = Daffy Duck , o = ACME , c = US"), "*"));
		shouldImply(ap, new AdminPermission(newMockBundle(7, "test.bsn",
				"test.location", "cn=Road Runner, o=ACME, c=NL"), "*"));
		shouldNotImply(ap, new AdminPermission(newMockBundle(8, "test.bsn",
				"test.location", "o=ACME, c=NL"), "*"));
		shouldNotImply(ap, new AdminPermission(newMockBundle(9, "test.bsn",
				"test.location", "dc=acme.com, cn=Bugs Bunny, o=ACME, c=US"),
				"*"));
	}

	private void invalidAdminPermission(String name, String actions) {
		try {
			AdminPermission p = new AdminPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private void invalidImply(Permission p1, Permission p2) {
		try {
			p1.implies(p2);
			fail("implies did not throw exception");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private void unsupportedImply(Permission p1, Permission p2) {
		try {
			p1.implies(p2);
			fail("implies did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
	}

	private void invalidImply(PermissionCollection pc, Permission p2) {
		try {
			pc.implies(p2);
			fail("implies did not throw exception");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private Bundle newMockBundle(long id, String name, String location,
			String dn) {
		Map /* <X509Certificate, List<X509Certificate>> */testMap = new HashMap();
		if (dn != null) {
			Principal principal = new MockPrincipal(dn);
			X509Certificate cert = new MockX509Certificate(principal);
			List /* <X509Certificate> */testList = new ArrayList();
			testList.add(cert);
			testMap.put(cert, testList);
		}
		return new MockBundle(id, name, location, testMap);
	}

	private static class MockBundle extends AbstractMockBundle {
		private final long		id;
		private final String	name;
		private final String	location;
		private final Map		signers;

		MockBundle(long id, String name, String location, Map signers) {
			this.id = id;
			this.name = name;
			this.location = location;
			this.signers = signers;
		}

		public long getBundleId() {
			return id;
		}

		public String getLocation() {
			return location;
		}

		public Map getSignerCertificates(int arg0) {
			return new HashMap(signers);
		}

		public String getSymbolicName() {
			return name;
		}
	}

	private static class MockX509Certificate extends X509Certificate {
		private final Principal	principal;

		MockX509Certificate(Principal principal) {
			this.principal = principal;
		}

		public Principal getSubjectDN() {
			return principal;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof MockX509Certificate) {
				return principal.equals(((MockX509Certificate) obj).principal);
			}
			return false;
		}

		public int hashCode() {
			return principal.hashCode();
		}

		public String toString() {
			return principal.toString();
		}

		public void checkValidity() throws CertificateExpiredException,
				java.security.cert.CertificateNotYetValidException {
			throw new UnsupportedOperationException();
		}

		public void checkValidity(Date var0)
				throws java.security.cert.CertificateExpiredException,
				java.security.cert.CertificateNotYetValidException {
			throw new UnsupportedOperationException();
		}

		public int getBasicConstraints() {
			throw new UnsupportedOperationException();
		}

		public Principal getIssuerDN() {
			throw new UnsupportedOperationException();
		}

		public boolean[] getIssuerUniqueID() {
			throw new UnsupportedOperationException();
		}

		public boolean[] getKeyUsage() {
			throw new UnsupportedOperationException();
		}

		public Date getNotAfter() {
			throw new UnsupportedOperationException();
		}

		public Date getNotBefore() {
			throw new UnsupportedOperationException();
		}

		public BigInteger getSerialNumber() {
			throw new UnsupportedOperationException();
		}

		public String getSigAlgName() {
			throw new UnsupportedOperationException();
		}

		public String getSigAlgOID() {
			throw new UnsupportedOperationException();
		}

		public byte[] getSigAlgParams() {
			throw new UnsupportedOperationException();
		}

		public byte[] getSignature() {
			throw new UnsupportedOperationException();
		}

		public boolean[] getSubjectUniqueID() {
			throw new UnsupportedOperationException();
		}

		public byte[] getTBSCertificate() throws CertificateEncodingException {
			throw new UnsupportedOperationException();
		}

		public int getVersion() {
			throw new UnsupportedOperationException();
		}

		public byte[] getEncoded() throws CertificateEncodingException {
			throw new UnsupportedOperationException();
		}

		public PublicKey getPublicKey() {
			throw new UnsupportedOperationException();
		}

		public void verify(PublicKey var0)
				throws java.security.InvalidKeyException,
				java.security.NoSuchAlgorithmException,
				java.security.NoSuchProviderException,
				java.security.SignatureException,
				java.security.cert.CertificateException {
			throw new UnsupportedOperationException();
		}

		public void verify(PublicKey var0, String var1)
				throws InvalidKeyException, NoSuchAlgorithmException,
				NoSuchProviderException, SignatureException,
				CertificateException {
			throw new UnsupportedOperationException();
		}

		public Set getCriticalExtensionOIDs() {
			throw new UnsupportedOperationException();
		}

		public byte[] getExtensionValue(String var0) {
			throw new UnsupportedOperationException();
		}

		public Set getNonCriticalExtensionOIDs() {
			throw new UnsupportedOperationException();
		}

		public boolean hasUnsupportedCriticalExtension() {
			throw new UnsupportedOperationException();
		}
	}

	private static class MockPrincipal implements Principal {
		private final String	name;

		MockPrincipal(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof MockPrincipal) {
				return name.equals(((MockPrincipal) obj).name);
			}
			return false;
		}

		public int hashCode() {
			return name.hashCode();
		}

		public String toString() {
			return getName();
		}
	}
}
