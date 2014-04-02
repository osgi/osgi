package org.osgi.test.cases.jmx.framework.secure.junit;

import java.io.IOException;
import java.security.AllPermission;
import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.jmx.service.permissionadmin.PermissionAdminMBean;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

public class PermissionAdminMBeanTestCase extends MBeanGeneralTestCase {
	private PermissionAdminMBean pMBean;
	private PermissionAdmin pAdmin;
	private Bundle testbundle;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testbundle = super.install("tb2.jar");
		testbundle.start();
		
		super.waitForRegistering(createObjectName(PermissionAdminMBean.OBJECTNAME));
		pMBean = getMBeanFromServer(PermissionAdminMBean.OBJECTNAME,
				PermissionAdminMBean.class);
		pAdmin = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));
	}

	public void testListLocations() throws IOException {
		assertNotNull(pMBean);

		String[] serviceLocation = pAdmin.getLocations();
		String[] mBeanLocations = pMBean.listLocations();
		assertTrue("got different information from mbean and direct service-call.",
					Arrays.equals(serviceLocation, mBeanLocations));
	}

	public void testListDefaultPermissions() throws IOException {
		assertNotNull(pMBean);

		PermissionInfo[] servicePermissions = pAdmin.getDefaultPermissions();
		String[] encodedServicePermissions = null;
		if (servicePermissions != null) {
			encodedServicePermissions = new String[servicePermissions.length];
			for (int i = 0; i < servicePermissions.length; i++) {
				encodedServicePermissions[i] = servicePermissions[i].getEncoded();
			}
		}
		String[] mBeanPermissions = pMBean.listDefaultPermissions();
		assertTrue("got different information from mbean and direct service-call.",
					Arrays.equals(encodedServicePermissions, mBeanPermissions));
	}
	
	public void testGetPermissions() throws IOException {
		assertNotNull(pMBean);

		PermissionInfo[] servicePermissions = pAdmin.getPermissions(testbundle.getLocation());
		String[] encodedServicePermissions = null;
		if (servicePermissions != null) {
			encodedServicePermissions = new String[servicePermissions.length];
			for (int i = 0; i < servicePermissions.length; i++) {
				encodedServicePermissions[i] = servicePermissions[i].getEncoded();
			}
		}
		String[] mBeanPermissions = pMBean.getPermissions(testbundle.getLocation());
		assertTrue("got different information from mbean and direct service-call.",
					Arrays.equals(encodedServicePermissions, mBeanPermissions));
	}
	
	public void testSetPermissions() throws IOException {
		assertNotNull(pMBean);
        String permissionInfo = (new PermissionInfo(AllPermission.class.getName(), "", "")).getEncoded();
		pMBean.setPermissions(testbundle.getLocation(), new String[] {permissionInfo});
		
		String[] mBeanPermissions = pMBean.getPermissions(testbundle.getLocation());
		boolean found = false;
		if (mBeanPermissions != null) {
			for (int i = 0; i < mBeanPermissions.length; i++) {
				if (permissionInfo.equals(mBeanPermissions[i])) {
					found = true;
					break;
				}
			}
		}
		assertTrue("set permission didn't changed the mbean permissions", found);
	}

	public void testSetDefaultPermissions() throws IOException {
		assertNotNull(pMBean);
		String permissionInfo = (new PermissionInfo(AllPermission.class.getName(), "", "")).getEncoded();		
		pMBean.setDefaultPermissions(new String[] {permissionInfo});
		String[] mBeanPermissions = pMBean.listDefaultPermissions();
		assertTrue("set default permissions doesn't work", (mBeanPermissions != null) && (mBeanPermissions.length == 1) &&
															permissionInfo.equals(mBeanPermissions[0]));
	}
	
	public void testExceptions() {
		assertNotNull(pMBean);
		
		//test listDefaultPermissions method
		try {
			pMBean.listDefaultPermissions();			
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		
		//test listLocations method		
		try {
			pMBean.listLocations();
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		
		//test getPermissions method
		try {
			pMBean.getPermissions(STRING_NULL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.getPermissions(STRING_EMPTY);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			pMBean.getPermissions(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test setDefaultPermissions method		
		try {
			pMBean.setDefaultPermissions(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			pMBean.setDefaultPermissions(new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			pMBean.setDefaultPermissions(new String[] { STRING_NULL });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setDefaultPermissions(new String[] { STRING_EMPTY });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setDefaultPermissions(new String[] { STRING_SPECIAL_SYMBOLS });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		
		//test setPermissions method		
		try {
			pMBean.setPermissions(STRING_NULL, new String[] {});
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setPermissions(STRING_EMPTY, new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			pMBean.setPermissions(STRING_SPECIAL_SYMBOLS, new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			pMBean.setPermissions(STRING_NULL, null);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setPermissions(STRING_NULL, new String[] { STRING_NULL });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setPermissions(STRING_NULL, new String[] { STRING_EMPTY });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.setPermissions(STRING_EMPTY, new String[] { STRING_SPECIAL_SYMBOLS });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(PermissionAdminMBean.OBJECTNAME));
		if (testbundle != null) {
			try {
				super.uninstallBundle(testbundle);
			} catch (Exception io) {}
		}
	}
}
