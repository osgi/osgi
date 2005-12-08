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

package unittests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.util.gsm.IMEICondition;
import junit.framework.TestCase;

public class TestIMEI extends TestCase {
	public static final String SYSTEM_IMEI = System.getProperty("org.osgi.util.gsm.imei");
	public static final String OTHER_IMEI = "123456789012345";
	public static final Bundle bundle = new DummyBundle();
	
	
	public static class DummyBundle implements Bundle {
		public int getState() { throw new IllegalStateException(); }
		public void start() throws BundleException { throw new IllegalStateException(); }
		public void stop() throws BundleException { throw new IllegalStateException(); }
		public void update() throws BundleException { throw new IllegalStateException(); }
		public void update(InputStream in) throws BundleException { throw new IllegalStateException(); }
		public void uninstall() throws BundleException { throw new IllegalStateException(); }
		public Dictionary getHeaders() { throw new IllegalStateException(); }
		public long getBundleId() { throw new IllegalStateException(); }
		public String getLocation() { throw new IllegalStateException(); }
		public ServiceReference[] getRegisteredServices() { throw new IllegalStateException(); }
		public ServiceReference[] getServicesInUse() { throw new IllegalStateException(); }
		public boolean hasPermission(Object permission) { throw new IllegalStateException(); }
		public URL getResource(String name) { throw new IllegalStateException(); }
		public Dictionary getHeaders(String locale) { throw new IllegalStateException(); }
		public String getSymbolicName() { throw new IllegalStateException(); }
		public Class loadClass(String name) throws ClassNotFoundException { throw new IllegalStateException(); }
		public Enumeration getResources(String name) throws IOException { throw new IllegalStateException(); }
		public Enumeration getEntryPaths(String path) { throw new IllegalStateException(); }
		public URL getEntry(String name) { throw new IllegalStateException(); }
		public long getLastModified() { throw new IllegalStateException(); }
		public Enumeration findEntries(String path, String filePattern, boolean recurse) { throw new IllegalStateException(); }
	}
	
	public void testBasic() throws Exception {
		Condition imei = (Condition) IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_IMEI}));
		assertFalse(imei.isPostponed());
		assertTrue(imei.isSatisfied());
		
		imei = (Condition) IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{OTHER_IMEI}));
		assertFalse(imei.isPostponed());
		assertFalse(imei.isSatisfied());
	}
	
	public void testIMEIValidator() throws Exception {
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{""}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890123456"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234a"}));
			fail();
		} catch (IllegalArgumentException e) {}

		IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"01234567891234*"}));

		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"012345678901234*"}));
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	public void testWildcards() throws Exception {
		Condition imei = (Condition) IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_IMEI.substring(0,5)+"*"}));
		assertTrue(imei.isSatisfied());	

		imei = (Condition) IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"777*"}));
		assertFalse(imei.isSatisfied());
		
	}
}
