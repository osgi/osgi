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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.osgi.framework.Bundle;

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

	public static void assertSerializable(PermissionCollection c1) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);

			out.writeObject(c1);
			out.flush();
			out.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bais);

			PermissionCollection c2 = (PermissionCollection) in.readObject();

			assertNotSame(c1, c2);
			assertEquals(enumerationAsSet(c1.elements()), enumerationAsSet(c2
					.elements()));
		}
		catch (Exception e) {
			fail("serialization error", e);
		}
	}

	private static Set enumerationAsSet(Enumeration e) {
		Set result = new HashSet();
		while (e.hasMoreElements()) {
			result.add(e.nextElement());
		}
		return result;
	}
	public static void assertSerializable(Permission p1) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);

			out.writeObject(p1);
			out.flush();
			out.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos
					.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bais);

			Permission p2 = (Permission) in.readObject();

			assertEquals(p1, p2);
			assertEquals(p2, p1);
			assertNotSame(p1, p2);
		} catch (Exception e) {
			fail("serialization error", e);
		}
	}
	
	public static void assertNotSerializable(Object p1) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);

			out.writeObject(p1);
			out.flush();
			out.close();
			fail("serialization did not throw exception");
		}
		catch (Exception e) {
			// expected
		}
	}

	public static Bundle newMockBundle(long id, String name, String location,
			String dn) {
		Map /* <X509Certificate, List<X509Certificate>> */testMap = new HashMap();
		if (dn != null) {
			Principal principal = new MockPrincipal(dn);
			X509Certificate cert = new MockX509Certificate(principal);
			List /* <X509Certificate> */testList = new ArrayList();
			testList.add(cert);
			testMap.put(cert, testList);
		}
		return (Bundle) MockFactory.newMock(Bundle.class, new MockBundle(id,
				name, location, testMap));
	}

	private static class MockBundle {
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

		public Map getSignerCertificates(int type) {
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
