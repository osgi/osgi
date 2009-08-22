package org.osgi.test.cases.cm.setallpermission;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;

public class SetAllPermissionActivator implements BundleActivator{
	
	private PermissionAdmin permAdmin;
	
	public void start(BundleContext context) throws Exception{
		
		System.out.println("SetAllPermission Bundle is going to start.");
		
	}
	
	public void stop(BundleContext context) throws Exception{
		
		ServiceReference ref = context.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of " + PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);
		
		String[] location = permAdmin.getLocations();
		if(location==null){
			System.out.println("no location is set in PermissionAdmin.");
			return;
		}
		for (int i = 0; i < location.length; i++) {
			permAdmin.setDefaultPermissions(null);
			permAdmin.setPermissions(location[i], null);
		}
		
	}

}
