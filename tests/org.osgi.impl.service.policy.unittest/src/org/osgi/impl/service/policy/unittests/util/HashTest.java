/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.unittests.util;

import org.osgi.impl.service.policy.util.HashCalculator;
import junit.framework.TestCase;

public class HashTest extends TestCase {
	
	private HashCalculator	hashCalculator;

	public void setUp() throws Exception {
		hashCalculator = new HashCalculator();
	}
	
	/**
	 * test examples from the RFC
	 * @throws Exception
	 */
	public void testRFC() throws Exception {
		assertEquals("fYWPcayGULN1Dkuqd62c5zkdiu4",hashCalculator.getHash("http://example.com/location1"));
		assertEquals("WovYXjHL_EgRTOVWHgipOk82tt8",
				hashCalculator.getHash(
				"(org.osgi.framework.PackagePermission \"org.osgi.*\" \"IMPORT\")\n"+
				"(org.osgi.framework.ServicePermission \"org.osgi.service.http.HttpService\" \"register\")\n"+
				"[org.osgi.service.condpermadmin.BundleLocationCondition \"http://example.com/loc1\"]\n"));
	}
}
