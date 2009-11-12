package org.osgi.test.cases.jmx.junit;

import java.io.*;
import java.util.Arrays;

import org.osgi.framework.*;
import org.osgi.jmx.service.permissionadmin.*;
import org.osgi.service.permissionadmin.*;

public class PermissionAdminMBeanTestCase extends MBeanGeneralTestCase {
	private PermissionAdminMBean pMBean;
	private PermissionAdmin pAdmin;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		Bundle testbundle = super.install("tb2.jar");
		testbundle.start();
		
		super.install("tb1.jar");
		super.waitForRegistering(createObjectName(PermissionAdminMBean.OBJECTNAME));
		pMBean = getMBeanFromServer(PermissionAdminMBean.OBJECTNAME,
				PermissionAdminMBean.class);
		pAdmin = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));
	}

	public void testGetPermissions() throws IOException {
		assertNotNull(pMBean);

		String[] serviceLocation = pAdmin.getLocations();
		String[] mBeanLocations = pMBean.listLocations();
		assertTrue(
				"got different information from mbean and direct service-call.",
				Arrays.equals(serviceLocation, mBeanLocations));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(PermissionAdminMBean.OBJECTNAME));
	}
}
