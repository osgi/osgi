package org.osgi.test.cases.permissionadmin.signature.tbc;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Vector;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.service.permissionadmin.PermissionInfo;


public class PermissionSignatureUtility {
	
	// AdminPermission actions
/*	
	static final String METADATA 	= AdminPermission.METADATA;
	static final String RESOURCE 	= AdminPermission.RESOURCE;	
	static final String CLASS 		= AdminPermission.CLASS;
	static final String LIFECYCLE 	= AdminPermission.LIFECYCLE;
	static final String EXECUTE 	= AdminPermission.EXECUTE;
	static final String LISTENER 	= AdminPermission.LISTENER;
	static final String PERMISSION 	= AdminPermission.PERMISSION;
	static final String RESOLVE 	= AdminPermission.RESOLVE;
	static final String SLART_LEVEL = AdminPermission.STARTLEVEL;

	static final String EXTENSION_LIFECYCLE = AdminPermission.EXTENSIONLIFECYCLE;
*/
	
	static final String METADATA 	= "metadata";
	static final String RESOURCE 	= "resource";	
	static final String CLASS 		= "class";
	static final String LIFECYCLE 	= "lifecycle";
	static final String EXECUTE 	= "execute";
	static final String LISTENER 	= "listener";
	static final String PERMISSION 	= "permission";
	static final String RESOLVE 	= "resolve";
	static final String SLART_LEVEL = "startlevel";

	static final String EXTENSION_LIFECYCLE = "extensionLifecycle";

	
	// Signature keys
	static final String ID 		 = "id";
	static final String LOCATION = "location";	
	static final String NAME 	 = "name";
	static final String SIGNER   = "signer";
	static final String DN_S	 = "DNs";
	
	//static final String[] AttributeTypeX_500 = {"CN", "L", "ST", "O", "OU", "C", "STREET", "DC", "UID"}; 
	
	static final String CONFIG_FPID = "permission.config.test.fpid";
	static final String	CONFIG_PROPERTY = "config.property";
	
	private PermissionSignatureTestControl 	control;
	private PermissionSignatureTBCService 	tbc;
	private BundleContext					context;
	
	public PermissionSignatureUtility(PermissionSignatureTestControl control, 
									  PermissionSignatureTBCService tbc,
									  BundleContext context){
		this.control = control;
		this.tbc = tbc;
		this.context = context;
	}

	
	//	 returns true if 'method' succeed
	public Object allowed_Bundle_getHeaders(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.getHeaders() " + message, "callBundle_getHeaders",
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	
	//	 returns true if 'method' failed
	public boolean not_allowed_Bundle_getHeaders(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.getHeaders() " + message, "callBundle_getHeaders",
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}

	
	public Object allowed_Bundle_getHeaders_byLocation(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.getHeaders(String) " + message, "callBundle_getHeaders",
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, null});
	}

	public boolean not_allowed_Bundle_getHeaders_byLocation(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.getHeaders(String) " + message, "callBundle_getHeaders",
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, null}, SecurityException.class);
	}
	
	public Object allowed_Bundle_getLocation(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.getLocation() " + message, "callBundle_getLocation",
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	public boolean not_allowed_Bundle_getLocation(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.getLocation() " + message, "callBundle_getLocation",
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getResource(String message, Bundle bundle, String name) throws Throwable {
		return control.allowed_call_assertNotNull("call Bundle.getResource(String) " + message, "callBundle_getResource",  
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}
	
	// if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getResource(String message, Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull("call Bundle.getResource(String) " + message, "callBundle_getResource",
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}

	//	 if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getResources(String message, Bundle bundle, String name) throws Throwable {
		return control.allowed_call_assertNotNull("call Bundle.getResources(String) " + message, "callBundle_getResources", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}
	
	//	 if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getResources(String message, Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull("call Bundle.getResources(String) " + message, "callBundle_getResources",
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}

	//	 if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getEntry(String message, Bundle bundle, String name) throws Throwable {
		return control.allowed_call_assertNotNull("call Bundle.getEntry(String) " + message, "callBundle_getEntry", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}
	
	//	 if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getEntry(String message, Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull("call Bundle.getEntry(String) " + message, "callBundle_getEntry",
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}

	//	 if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getEntryPaths(String message, Bundle bundle, String name) throws Throwable {
		return control.allowed_call_assertNotNull("call Bundle.getEntryPaths(String) " + message, "callBundle_getEntryPaths", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}
	
	//	 if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getEntryPaths(String message, Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull("call Bundle.getEntryPaths(String) " + message, "callBundle_getEntryPaths", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}
	
	public Object allowed_Bundle_loadClass(String message, Bundle bundle, String name) throws Exception {
		return control.allowed_call("call Bundle.loadClass(String) " + message, "callBundle_loadClass", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name});
	}

	public boolean not_allowed_Bundle_loadClass(String message, Bundle bundle, String name) throws Exception {
		return control.not_allowed_call("call Bundle.loadClass(String) " + message, "callBundle_loadClass", 
				new Class[]{Bundle.class, String.class}, new Object[]{bundle, name}, java.lang.ClassNotFoundException.class);
	}
	
	public Object allowed_Bundle_stop(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.stop() " + message, "callBundle_stop", 
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	public boolean not_allowed_Bundle_stop(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.stop() " + message, "callBundle_stop", 
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_uninstall(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.uninstall() " + message, "callBundle_uninstall", 
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	public boolean not_allowed_Bundle_uninstall(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.uninstall() " + message, "callBundle_uninstall",
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_update(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.update() " + message, "callBundle_update", 
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	public boolean not_allowed_Bundle_update(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.update() " + message, "callBundle_update", 
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_update_by_InputStream(String message, Bundle bundle, InputStream is) throws Exception {
		return control.allowed_call("call Bundle.update(InputStream) " + message, "callBundle_update", 
				new Class[]{Bundle.class, InputStream.class}, new Object[]{bundle, is});
	}

	public boolean not_allowed_Bundle_update_by_InputStream(String message, Bundle bundle, InputStream is) throws Exception {
		return control.not_allowed_call("call Bundle.update(InputStream) " + message, "callBundle_update",
				new Class[]{Bundle.class, InputStream.class}, new Object[]{bundle, is}, SecurityException.class);
	}

	public Object allowed_Bundle_start(String message, Bundle bundle) throws Exception {
		return control.allowed_call("call Bundle.start() " + message, "callBundle_start", 
				new Class[]{Bundle.class}, new Object[]{bundle});
	}

	public boolean not_allowed_Bundle_start(String message, Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.start() " + message,"callBundle_start", 
				new Class[]{Bundle.class}, new Object[]{bundle}, SecurityException.class);
	}
	
	
	public Object allowed_BundleContext_installBundle(String message, String location) throws Exception {
		return control.allowed_call("call BundleContext.installBundle(String) " + message,  
				"callBundleContext_installBundle", new Class[]{BundleContext.class, String.class}, 
				new Object[]{context, location});
	}


	public boolean not_allowed_BundleContext_installBundle(String message, String location) throws Exception {
		return control.not_allowed_call("call BundleContext.installBundle(String) " + message, 
				"callBundleContext_installBundle", new Class[]{BundleContext.class, String.class}, 
				new Object[]{context, location}, SecurityException.class);
	}

	public Object allowed_BundleContext_installBundle_by_InputStream(String message, String location, InputStream is) throws Exception {
		return control.allowed_call("call BundleContext.installBundle(String, InputStream) " + message,  
				"callBundleContext_installBundle", new Class[]{BundleContext.class, String.class, InputStream.class}, 
				new Object[]{context, location, is});
	}


	public boolean not_allowed_BundleContext_installBundle_by_InputStream(String message, String location, InputStream is) throws Exception {
		return control.not_allowed_call("call BundleContext.installBundle(String, InputStream) " + message, 
				"callBundleContext_installBundle", new Class[]{BundleContext.class, String.class, InputStream.class}, 
				new Object[]{context, location, is}, SecurityException.class);
	}
	
	public Object allowed_BundleContext_addBundleListener(String message) throws Exception {
		return control.allowed_call("call BundleContext.addBundleListener(SynchronousBundleListener) " + message,  
				"callBundleContext_addBundleListener", new Class[]{BundleContext.class}, 
				new Object[]{context});
	}


	public boolean not_allowed_BundleContext_addBundleListener(String message) throws Exception {
		return control.not_allowed_call("call BundleContext.addBundleListener(SynchronousBundleListener) " + message, 
				"callBundleContext_addBundleListener", new Class[]{BundleContext.class}, 
				new Object[]{context}, SecurityException.class);
	}

	public Object allowed_BundleContext_removeBundleListener(String message) throws Exception {
		return control.allowed_call("call BundleContext.removeBundleListener(SynchronousBundleListener) " + message,  
				"callBundleContext_removeBundleListener", new Class[]{BundleContext.class}, 
				new Object[]{context});
	}


	public boolean not_allowed_BundleContext_removeBundleListener(String message) throws Exception {
		return control.not_allowed_call("call BundleContext.removeBundleListener(SynchronousBundleListener) " + message, 
				"callBundleContext_removeBundleListener", new Class[]{BundleContext.class}, 
				new Object[]{context}, SecurityException.class);
	}

	public Object allowed_StartLevel_setBundleStartLevel(String message, Bundle bundle, int startlevel) throws Exception {
		return control.allowed_call("call StartLevel.setBundleStartLevel(Bundle, int) " + message,  
				"callStartLevel_setBundleStartLevel", new Class[]{Bundle.class, Integer.class}, 
				new Object[]{bundle, new Integer(startlevel)});
	}


	public boolean not_allowed_StartLevel_setBundleStartLevel(String message, Bundle bundle, int startlevel) throws Exception {
		return control.not_allowed_call("call StartLevel.setBundleStartLevel(Bundle, int) " + message, 
				"callStartLevel_setBundleStartLevel", new Class[]{Bundle.class, Integer.class}, 
				new Object[]{bundle, new Integer(startlevel)}, SecurityException.class);
	}
	
	public Object allowed_StartLevel_setStartLevel(String message, int startlevel) throws Exception {
		return control.allowed_call("call StartLevel.setStartLevel(int) " + message,  
				"callStartLevel_setStartLevel", new Class[]{Integer.class}, 
				new Object[]{new Integer(startlevel)});
	}


	public boolean not_allowed_StartLevel_setStartLevel(String message, int startlevel) throws Exception {
		return control.not_allowed_call("call StartLevel.setBundleStartLevel(int) " + message, 
				"callStartLevel_setStartLevel", new Class[]{Integer.class}, 
				new Object[]{new Integer(startlevel)}, SecurityException.class);
	}

	public Object allowed_StartLevel_setInitialBundleStartLevel(String message, int startlevel) throws Exception {
		return control.allowed_call("call StartLevel.setInitialBundleStartLevel(int) " + message,  
				"callStartLevel_setInitialBundleStartLevel", new Class[]{Integer.class}, 
				new Object[]{new Integer(startlevel)});
	}


	public boolean not_allowed_StartLevel_setInitialBundleStartLevel(String message, int startlevel) throws Exception {
		return control.not_allowed_call("call StartLevel.setInitialBundleStartLevel(int) " + message, 
				"callStartLevel_setInitialBundleStartLevell", new Class[]{Integer.class}, 
				new Object[]{new Integer(startlevel)}, SecurityException.class);
	}


	public Object allowed_PermissionAdmin_setPermissions(String message, String location, PermissionInfo[] permissions) throws Exception {
		return control.allowed_call("call PermissionAdmin.setPermissions(String, PermissionInfo[]) " + message,  
				"callPermissionAdmin_setPermissions", new Class[]{String.class, PermissionInfo[].class}, 
				new Object[]{location, permissions});
	}


	public boolean not_allowed_PermissionAdmin_setPermissions(String message, String location, PermissionInfo[] permissions) throws Exception {
		return control.not_allowed_call("call PermissionAdmin.setPermissions(String, PermissionInfo[]) " + message, 
				"callPermissionAdmin_setPermissions", new Class[]{String.class, PermissionInfo[].class}, 
				new Object[]{location, permissions}, SecurityException.class);
	}

	public Object allowed_PermissionAdmin_setDefaultPermissions(String message, PermissionInfo[] permissions) throws Exception {
		return control.allowed_call("call PermissionAdmin.setDefaultPermissions(PermissionInfo[]) " + message,  
				"callPermissionAdmin_setDefaultPermissions", new Class[]{PermissionInfo[].class}, 
				new Object[]{permissions});
	}


	public boolean not_allowed_PermissionAdmin_setDefaultPermissions(String message, PermissionInfo[] permissions) throws Exception {
		return control.not_allowed_call("call PermissionAdmin.setDefaultPermissions(PermissionInfo[]) " + message, 
				"callPermissionAdmin_setDefaultPermissions", new Class[]{PermissionInfo[].class}, 
				new Object[]{permissions}, SecurityException.class);
	}

	public Object allowed_PackageAdmin_refreshPackages(String message, Bundle[] bundles) throws Exception {
		return control.allowed_call("call PackageAdmin.refreshPackages(Bundle[]) " + message,  
				"callPackageAdmin_refreshPackages", new Class[]{Bundle[].class}, 
				new Object[]{bundles});
	}


	public boolean not_allowed_PackageAdmin_refreshPackages(String message, Bundle[] bundles) throws Exception {
		return control.not_allowed_call("call PackageAdmin.refreshPackages(Bundle[]) " + message, 
				"callPackageAdmin_refreshPackages", new Class[]{Bundle[].class}, 
				new Object[]{bundles}, SecurityException.class);
	}

	public Object allowed_PackageAdmin_resolveBundles(String message, Bundle[] bundles) throws Exception {
		return control.allowed_call("call PackageAdmin.resolveBundles(Bundle[]) " + message,  
				"callPackageAdmin_resolveBundles", new Class[]{Bundle[].class}, 
				new Object[]{bundles});
	}


	public boolean not_allowed_PackageAdmin_resolveBundles(String message, Bundle[] bundles) throws Exception {
		return control.not_allowed_call("call PackageAdmin.resolveBundles(Bundle[]) " + message, 
				"callPackageAdmin_resolveBundles", new Class[]{Bundle[].class}, 
				new Object[]{bundles}, SecurityException.class);
	}
	
	public Object allowed_ConfigurationAdmin_getConfiguration(String message, String pid) throws Exception {
		return control.allowed_call("call ConfigurationAdmin.getConfiguration(String) " + message,  
				"callConfigurationAdmin_getConfiguration", new Class[]{String.class}, 
				new Object[]{pid});
	}

	public boolean not_allowed_ConfigurationAdmin_getConfiguration(String message, String pid) throws Exception {
		return control.not_allowed_call("call ConfigurationAdmin.getConfiguration(String) " + message,  
				"callConfigurationAdmin_getConfiguration", new Class[]{String.class}, 
				new Object[]{pid}, SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_getConfiguration(String message, String pid, String location) throws Exception {
		return control.allowed_call("call ConfigurationAdmin.getConfiguration(String, String) " + message,  
				"callConfigurationAdmin_getConfiguration", new Class[]{String.class, String.class}, 
				new Object[]{pid, location});
	}

	public boolean not_allowed_ConfigurationAdmin_getConfiguration(String message, String pid, String location) throws Exception {
		return control.not_allowed_call("call ConfigurationAdmin.getConfiguration(String, String) " + message,  
				"callConfigurationAdmin_getConfiguration", new Class[]{String.class, String.class}, 
				new Object[]{pid, location}, SecurityException.class);
	}

	// TO DO SecurityException ???
	public Object allowed_ConfigurationAdmin_listConfigurations(String message, String filter) throws Exception {
		return control.allowed_call("call ConfigurationAdmin.listConfigurations(String) " + message,  
				"callConfigurationAdmin_listConfigurations", new Class[]{String.class}, 
				new Object[]{filter});
	}

	//	 TO DO SecurityException ???
	public boolean not_allowed_ConfigurationAdmin_listConfigurations(String message, String filter) throws Exception {
		return control.not_allowed_call("call ConfigurationAdmin.listConfigurations(String) " + message,  
				"callConfigurationAdmin_listConfigurations", new Class[]{String.class}, 
				new Object[]{filter}, SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_createFactoryConfiguration(String message, String factoryPid) throws Exception {
		return control.allowed_call("call ConfigurationAdmin.createFactoryConfiguration(String) " + message,  
				"callConfigurationAdmin_createFactoryConfiguration", new Class[]{String.class}, 
				new Object[]{factoryPid});
	}

	public boolean not_allowed_ConfigurationAdmin_createFactoryConfiguration(String message, String factoryPid) throws Exception {
		return control.not_allowed_call("call ConfigurationAdmin.createFactoryConfiguration(String) " + message,  
				"callConfigurationAdmin_createFactoryConfiguration", new Class[]{String.class}, 
				new Object[]{factoryPid}, SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_createFactoryConfiguration(String message, String factoryPid, String location) throws Exception {
		return control.allowed_call("call ConfigurationAdmin.createFactoryConfiguration(String, String) " + message,  
				"callConfigurationAdmin_createFactoryConfiguration", new Class[]{String.class, String.class}, 
				new Object[]{factoryPid, location});
	}

	public boolean not_allowed_ConfigurationAdmin_createFactoryConfiguration(String message, String factoryPid, String location) throws Exception {
		return control.not_allowed_call("call ConfigurationAdmin.createFactoryConfiguration(String, String) " + message,  
				"callConfigurationAdmin_createFactoryConfiguration", new Class[]{String.class, String.class}, 
				new Object[]{factoryPid, location}, SecurityException.class);
	}

	public Object allowed_Configuration_delete(String message, String pid) throws Exception {
		return control.allowed_call("call Configuration.delete() " + message,  
				"callConfiguration_delete", new Class[]{String.class}, 
				new Object[]{pid});
	}

	public boolean not_allowed_Configuration_delete(String message, String pid) throws Exception {
		return control.not_allowed_call("call Configuration.delete() " + message,  
				"callConfiguration_delete", new Class[]{String.class}, 
				new Object[]{pid}, SecurityException.class);
	}

	
	public Object allowed_Configuration_update(String message, String pid) throws Exception {
		return control.allowed_call("call Configuration.update() " + message,  
				"callConfiguration_update", new Class[]{String.class}, 
				new Object[]{pid});
	}

	public boolean not_allowed_Configuration_update(String message, String pid) throws Exception {
		return control.not_allowed_call("call Configuration.update() " + message,  
				"callConfiguration_update", new Class[]{String.class}, 
				new Object[]{pid}, SecurityException.class);
	}

	public Object allowed_Configuration_update(String message, String pid, Dictionary properties) throws Exception {
		return control.allowed_call("call Configuration.update(Dictionary) " + message,  
				"callConfiguration_update", new Class[]{String.class, Dictionary.class}, 
				new Object[]{pid, properties});
	}

	public boolean not_allowed_Configuration_update(String message, String pid, Dictionary properties) throws Exception {
		return control.not_allowed_call("call Configuration.update(Dictionary) " + message,  
				"callConfiguration_update", new Class[]{String.class, Dictionary.class}, 
				new Object[]{pid, properties}, SecurityException.class);
	}

	public Object allowed_Configuration_setBundleLocation(String message, String pid) throws Exception {
		return control.allowed_call("call Configuration.setBundleLocation(String) " + message,  
				"callConfiguration_setBundleLocation", new Class[]{String.class}, 
				new Object[]{pid});
	}

	public boolean not_allowed_Configuration_setBundleLocation(String message, String pid) throws Exception {
		return control.not_allowed_call("call Configuration.setBundleLocation(String) " + message,  
				"callConfiguration_setBundleLocation", new Class[]{String.class}, 
				new Object[]{pid}, SecurityException.class);
	}
	
	
	Vector createWildcardPermissionInfo(Class permission, String name, String action, String value) {
		Vector infos = new Vector();
		if (value == null) return infos;
		String key = "";
		if (permission.getName().equals(AdminPermission.class.getName())) {
			key = name + "=";
		}
		
		PermissionInfo info = new PermissionInfo(permission.getName(), key + value, action);
		infos.addElement(info);
		
		int index = value.indexOf(".");
		int lastIndex;
		
		while (index != -1) {
			lastIndex = index + 1;
			info = new PermissionInfo(permission.getName(), 
						 			  key + value.substring(0, lastIndex) + "*", 
									  action);
			infos.addElement(info);
			index = value.indexOf(".", lastIndex);
		}
		
		return infos;
	}

// !!! AdminPermission(id, action) must be the first element in the Vector		
// name -> id, location, Name, signer
	Vector getPInfosForAdminPermisssion(String action, long bundleId, String location, String symbolicName) {
		Vector permissions = new Vector();
		
		// TO DO -> AdminPermission(id, action) ???
		if (bundleId != -1) {
			PermissionInfo info = new PermissionInfo(AdminPermission.class.getName(),
													 String.valueOf(bundleId), action);
			permissions.addElement(info);
			info = new PermissionInfo(AdminPermission.class.getName(),
									  ID + "=" + bundleId, action);
			permissions.addElement(info);
		}
		
		if (location != null) {
			permissions.addAll(createWildcardPermissionInfo(AdminPermission.class, 
															LOCATION, action, location));
		}
		
		if (symbolicName != null) {
			permissions.addAll(createWildcardPermissionInfo(AdminPermission.class, 
															NAME, action, symbolicName));
		}
		
		permissions.addAll(getSignerFilter(action));
				
		return permissions;
	}
	
	Vector getSignerFilter(String action) {
		Vector dns = createWildcardDNs(SignatureResource.getString(DN_S));
		Vector infos = new Vector();
		String filter = SIGNER + "=";
		String key;
		PermissionInfo info;
		for (int i = 0; i < dns.size(); ++i) {
			info = new PermissionInfo(AdminPermission.class.getName(),
					   SIGNER + "=" + dns.elementAt(i), action);
			infos.addElement(info);
		}
		
		return infos;
	}
	
	
	
	Vector createWildcardDNs(String value) {
		Vector result = new Vector();
		String semicolon = ";";
		String asterisk = "*";
		
		int lastIndex = 0;
		int semicolonIndex = value.indexOf(semicolon);
		String element;
		String rdns;
		String prefix;
		String suffix;
		while (semicolonIndex != -1) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex, semicolonIndex);
			suffix = value.substring(semicolonIndex);
			
			//result.addElement(prefix + asterisk + suffix);
			result.addAll(addVector(createWildcardRDNs(element), prefix, suffix));
			
			lastIndex = semicolonIndex + 1;
			semicolonIndex = value.indexOf(semicolon, lastIndex);
		}
		
		if (lastIndex > 0) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex);
			
			//result.addElement(prefix + asterisk);
			result.addAll(addVector(createWildcardRDNs(element), prefix, ""));
		} else {
			result.addAll(createWildcardRDNs(value));
		}
		
		return result;
	}
	
	private Vector addVector(Vector vec, String prefix, String suffix) {
		Vector result = new Vector();
		for (int i = 0; i < vec.size(); ++i) {
			result.addElement(prefix + (String)vec.elementAt(i) + suffix);
		}
		
		return result;
	}
	
	
	private Vector createWildcardRDNs(String value) {
		Vector result = new Vector();
		String comma = ",";
		//String semicolon = ";";
		String equal = "=";
		String space = " ";
		String asterisk = "*";
		
		int lastIndex = 0;
		int commaIndex = value.indexOf(comma, lastIndex); 
		int equalIndex = value.indexOf(equal, lastIndex);
		
		while (commaIndex != -1) {
			result.addElement(asterisk + value.substring(commaIndex));
			if ((equalIndex != -1) && (equalIndex < commaIndex)) {
				result.addElement(value.substring(0, equalIndex + 1) + asterisk + 
								  value.substring(commaIndex));
			}
			lastIndex = commaIndex + 1;
			commaIndex = value.indexOf(comma, lastIndex); 
			equalIndex = value.indexOf(equal, lastIndex);
		}
		
		if (lastIndex > 0) {
			result.addElement(asterisk);
		}
		if (equalIndex > 0) {
			result.addElement(value.substring(0, equalIndex + 1) + asterisk);
		}

		
		return result;
	}
	
	
	
}