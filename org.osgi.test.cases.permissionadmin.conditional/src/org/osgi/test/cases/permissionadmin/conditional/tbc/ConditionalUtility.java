
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.util.Vector;

import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.permissionadmin.PermissionAdmin;


public class ConditionalUtility {

	private ConditionalTestControl 		testControl;
	private ConditionalTBCService  		tbc;
	private PermissionAdmin		   		pAdmin;
	private ConditionalPermissionAdmin 	cpAdmin;
	
	static String REQUIRED 					= "RequiredPermission";
	static String CP_PERMISSION 			= "CPAdminPermission";
	static String P_PERMISSION 				= "PAdminPermission";
	static String REQUIRED_CP_PERMISSION 	= "RequiredIntersectCPAdminPermission";
	static String REQUIRED_P_PERMISSION 	= "RequiredIntersectPAdminPermission";
	
	static String VERSION_FLOOR				= "version_range_floor";
	static String VERSION_CEILING			= "version_range_ceiling";
	
	static String DN_S						= "DNs";
	static String INAPPROPRIATE_DN_S		= "inappropriateDNs";
	static String SEPARATOR                 = "\"";
	
	static String BUNDLE_VERSION 			= "Bundle-Version";

	public ConditionalUtility(ConditionalTestControl testControl, ConditionalTBCService  tbc, 
							  PermissionAdmin pAdmin, ConditionalPermissionAdmin cpAdmin) {
		this.testControl = testControl;
		this.tbc = tbc;
		this.pAdmin = pAdmin;
		this.cpAdmin = cpAdmin;
	}
	
	
	ConditionInfo createTestCInfo(boolean evaluated, boolean satisfied, boolean mutable) {
		return new ConditionInfo(TestCondition.class.getName(), 
					new String[]{String.valueOf(evaluated), 
								 String.valueOf(satisfied), 
								 String.valueOf(mutable)});
	}
	
	
	void createBadConditionInfo(String message, String encoded, Class exceptionClass) throws Exception {
		try {
			new ConditionInfo(encoded);
			testControl.fail(message + " did not result in a " + exceptionClass.getName());
		} catch (Exception e) {
			testControl.assertException(message, exceptionClass, e);
		}
	}

	void createBadConditionInfo(String message, String type, String[] args, Class exceptionClass) throws Exception {
		try {
			new ConditionInfo(type, args);
			testControl.fail(message + " did not result in a " + exceptionClass.getName());
		} catch (Exception e) {
			testControl.assertException(message, exceptionClass, e);
		}
	}

	void allowed(String message, Permission permission) {
		try {
			tbc.checkPermission(permission);
			testControl.pass(message);
		} catch (Throwable e) {
			testControl.fail(message + " but " + e.getClass().getName() + " was thrown");
		}
	}
	
	void notAllowed(String message, Permission permission, Class wanted) {
		try {
			tbc.checkPermission(permission);
			testControl.failException(message, wanted);
		} catch (Throwable e) {
			testControl.assertException(message, wanted, e);
		}
	}
	
	Vector getWildcardString(String value) {
		Vector result = new Vector();
		result.addElement(value);
		
		int index = value.indexOf(".");
		int lastIndex;
		
		while (index != -1) {
			lastIndex = index + 1;
			result.addElement(value.substring(0, lastIndex) + "*");
			index = value.indexOf(".", lastIndex);
		}
		
		return result;
	}
	
	// Returns vector whose values are version-ranges, where version belong 
	Vector getCorrectVersionRanges(String version, String floor, String ceiling) {
		Vector result = new Vector();
		
		result.addElement(new String("\"" + floor + "\""));
		result.addElement(new String("\"" + version + "\""));
		
		result.addElement(new String("\"[" + floor + "," + ceiling + "\"]"));
		result.addElement(new String("\"[" + floor + "," + ceiling + "\")"));
		result.addElement(new String("\"(" + floor + "," + ceiling + "\"]"));
		result.addElement(new String("\"(" + floor + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + version + "," + ceiling + "\"]"));
		result.addElement(new String("\"[" + version + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + floor + "," + version + "\"]"));
		result.addElement(new String("\"(" + floor + "," + version + "\"]"));

		
		return result;
	}
	
	// Returns vector whose values are version-ranges, where version not belong 
	Vector getNotCorrectVersionRanges(String version, String floor, String ceiling) {
		Vector result = new Vector();
		
		result.addElement(new String("\"" + ceiling + "\""));

		result.addElement(new String("\"(" + version + "," + ceiling + "\"]"));
		result.addElement(new String("\"(" + version + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + floor + "," + version + "\")"));
		result.addElement(new String("\"(" + floor + "," + version + "\")"));

		
		return result;
	}
	
	Permission getPermission(String resourceName) throws Exception {
		PermissionInfo pi = new PermissionInfo(ConditionResource.getString(resourceName)); 

		return getPermission(pi);
	}
	
	Permission getPermission(PermissionInfo pi) throws Exception {
		String actions = pi.getActions();
		String name = pi.getName();
		String type = pi.getType();
		
		Class pClass = Class.forName(type);
		Constructor constructor = pClass.getConstructor(new Class[]{
				String.class, String.class});
		Permission permission = (Permission)constructor.newInstance(
				new Object[] {name, actions});

		return permission;
	}
	
	void setPermissionsByPermissionAdmin(String bundleLocation, Permission permission) {
		PermissionInfo pInfo = new PermissionInfo(permission.getClass().getName(), 
									permission.getName(), permission.getActions());
		pAdmin.setPermissions(bundleLocation, new PermissionInfo[]{pInfo});
	}
	
	ConditionalPermissionInfo setPermissionsByCPermissionAdmin(ConditionInfo[] cInfos, Permission permission) {
		PermissionInfo pInfo = new PermissionInfo(permission.getClass().getName(), 
				permission.getName(), permission.getActions());
		return cpAdmin.addConditionalPermissionInfo(cInfos, new PermissionInfo[]{pInfo});
	}
	
	void testPermissions(ConditionInfo[] conditions, Permission permission, Permission[] allowedPermission, Permission[] notAllowedPermission) {
		ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, permission);
		for (int i = 0; i < allowedPermission.length; ++i) {
			allowed(cpInfo.toString(), allowedPermission[i]);
		}
		for (int k = 0; k < notAllowedPermission.length; ++k) {
			notAllowed(cpInfo.toString(), notAllowedPermission[k], SecurityException.class);
		}
		cpInfo.delete();
		for (int j = 0; j < allowedPermission.length; ++j) {
			notAllowed(cpInfo.toString(), allowedPermission[j], SecurityException.class);
		}
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
		String semicolon = ";";
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
