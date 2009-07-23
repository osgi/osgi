package org.osgi.test.cases.jmx.junit;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.permissionadmin.PermissionManagerMBean;
import org.osgi.service.permissionadmin.PermissionAdmin;

public class PermissionManagerMBeanTestCase extends MBeanGeneralTestCase {
	private PermissionManagerMBean pMBean;
	private PermissionAdmin pAdmin;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		Bundle testbundle = super.install("tb2.jar");
		testbundle.start();
		
		super.install("tb1.jar");
		super.waitForRegistering(createObjectName(JmxConstants.PA_SERVICE));
		pMBean = getMBeanFromServer(JmxConstants.PA_SERVICE,
				PermissionManagerMBean.class);
		pAdmin = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));
	}

	public void testGetPermissions() throws IOException {
		assertNotNull(pMBean);

		String[] serviceLocation = pAdmin.getLocations();
		String[] mBeanLocations = pMBean.getLocations();
		assertEquals(
				"got different information from mbean and direct service-call.",
				serviceLocation, mBeanLocations);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(JmxConstants.PA_SERVICE));
	}
}
