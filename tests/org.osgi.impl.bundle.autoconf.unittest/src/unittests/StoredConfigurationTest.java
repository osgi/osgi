/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package unittests;

import java.io.File;
import java.util.List;

import org.osgi.impl.bundle.autoconf.StoredConfiguration;
import org.osgi.impl.bundle.autoconf.StoredConfigurations;

import junit.framework.TestCase;

public class StoredConfigurationTest extends TestCase {

	File dataFile;
	StoredConfigurations sc;
	
	protected void setUp() throws Exception {
		super.setUp();
		dataFile = new File("storedConfiguration.testdata");
		dataFile.delete();
		sc = new StoredConfigurations(dataFile);
	}

	protected void tearDown() throws Exception {
		dataFile = null;
		sc = null;
		super.tearDown();
	}

	public void testRemoveByPid() {
		StoredConfiguration s = new StoredConfiguration("package1","res1","a.b.c",null,null);
		sc.add(s);
		StoredConfiguration s2 = new StoredConfiguration("package1","res1","a.b.d",null,null);
		sc.add(s2);
		List l = sc.getByPackageName("package1");
		assertEquals(2,l.size());
		sc.remove("a.b.e");
		l = sc.getByPackageName("package1");
		assertEquals(2,l.size());
		sc.remove("a.b.c");
		l = sc.getByPackageName("package1");
		assertEquals(1,l.size());
	}
}
