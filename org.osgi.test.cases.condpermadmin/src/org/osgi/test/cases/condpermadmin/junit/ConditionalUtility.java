package org.osgi.test.cases.condpermadmin.junit;


import java.lang.reflect.Constructor;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.condpermadmin.testcond.TestCondition;

import junit.framework.AssertionFailedError;


@SuppressWarnings("deprecation")
public class ConditionalUtility {

	private ConditionalTestControl		testControl;
	private PermissionAdmin		   		  pAdmin;
	private ConditionalPermissionAdmin 	cpAdmin;
	private Bundle	 testBundle;
	private boolean	 hasPermissionsPerm;
	
	static String REQUIRED 					  = "RequiredPermission";
	static String CP_PERMISSION 			= "CPAdminPermission";
	static String P_PERMISSION 				= "PAdminPermission";
	static String REQUIRED_CP_PERMISSION 	= "RequiredIntersectCPAdminPermission";
	static String REQUIRED_P_PERMISSION 	= "RequiredIntersectPAdminPermission";
	
	static String VERSION_FLOOR				= "version_range_floor";
	static String VERSION_CEILING			= "version_range_ceiling";
	
	static String DN_S						    = "DNs";
	static String INAPPROPRIATE_DN_S	= "inappropriateDNs";
	static String SEPARATOR           = "\"";
	
	static String BUNDLE_VERSION 			= "Bundle-Version";

	public ConditionalUtility(ConditionalTestControl testControl,
			PermissionAdmin pAdmin, ConditionalPermissionAdmin cpAdmin) {
		this.testControl = testControl;
		this.pAdmin = pAdmin;
		this.cpAdmin = cpAdmin;
	}
  
	public void setTestBunde(Bundle bundle, boolean hasPermissionsPerm) {
		this.testBundle = bundle;
		this.hasPermissionsPerm = hasPermissionsPerm;
	}
	
	
	ConditionInfo createTestCInfo(boolean postponed, boolean satisfied, boolean mutable, String name, long id) {
    return new ConditionInfo(TestCondition.class.getName(), 
					new String[]{String.valueOf(postponed), 
								 String.valueOf(satisfied), 
								 String.valueOf(mutable),
								 name,
								 String.valueOf(id)});
	}
	
	
	void createBadConditionInfo(String message, String encoded,
			Class< ? extends Throwable> exceptionClass) throws Exception {
		try {
			new ConditionInfo(encoded);
			ConditionalTestControl.fail(message + " did not result in a "
					+ exceptionClass.getName());
		} catch (Exception e) {
			ConditionalTestControl.assertException(message, exceptionClass, e);
		}
	}

	void createBadConditionInfo(String message, String type, String[] args,
			Class< ? extends Throwable> exceptionClass) throws Exception {
		try {
			new ConditionInfo(type, args);
			ConditionalTestControl.fail(message + " did not result in a " + exceptionClass.getName());
		} catch (Exception e) {
			ConditionalTestControl.assertException(message, exceptionClass, e);
		}
	}

	void allowed(AdminPermission permission) {
    String message = "allowed " + permToString(permission);
		try {
			checkPermission(permission);
			ConditionalTestControl.pass(message);
		} catch (Throwable e) {
			ConditionalTestControl.fail(message + " but " + e.getClass().getName() + " was thrown");
		}
	}
	
	void notAllowed(AdminPermission permission,
			Class< ? extends Throwable> wanted) {
    String message = "not allowed " + permToString(permission);
		try {
			checkPermission(permission);
      //ConditionalTestControl.pass("FAIL>>>>" + message);
			ConditionalTestControl.failException(message, wanted);
		} catch (Throwable e) {
      if (!e.getClass().equals(wanted.getClass()) && (wanted.isInstance(e))) {
        ConditionalTestControl.pass(message + " and [" + e.getClass() + "] was thrown");
      } else {
        ConditionalTestControl.assertException(message, wanted, e);
      }
		}
	}
  
  protected String permToString(AdminPermission permission) {
    return "permission [" + permission.getName() + ", " + permission.getActions() + "]";
  }
  
  private String cpInfoToString(ConditionalPermissionInfo cpInfo) {
    StringBuffer result = new StringBuffer("ConditionalPermissionInfo: ");
    ConditionInfo[] condInfos = cpInfo.getConditionInfos();
    result.append("\n       with conditional infos:");
    for (int i = 0; i < condInfos.length; i++) {
      result.append("\n         ");
      result.append(condInfos[i]);      
    }
    PermissionInfo[] permInfos = cpInfo.getPermissionInfos();
    result.append("\n       with permission infos:");
    for (int i = 0; i < permInfos.length; i++) {
      result.append("\n         ");
      result.append(permInfos[i]);      
    }
    return result.toString();
  }
	
	private void checkPermission(AdminPermission permission) throws Throwable {
		Permission p = getPermission(permission, testBundle);
		if (hasPermissionsPerm) {
			testControl.permTBC.checkPermission(p);
		} else {
			testControl.tbc.checkPermission(p);
		}
	}
	
	List<String> getWildcardString(String value) {
		List<String> result = new ArrayList<>();
		result.add(value);
    int beginIndex = 0;
    if (value.startsWith("http://")) {
      beginIndex = "http://".length();
    }
    int index = value.indexOf("/", beginIndex);
    int lastIndex;
    
    while (index != -1) {
      lastIndex = index + 1;
			result.add(value.substring(0, lastIndex) + "*");
      index = value.indexOf("/", lastIndex); 
    }               
    return result;
}

	
	// Returns vector whose values are version-ranges, where version belong 
	List<String> getCorrectVersionRanges(String version, String floor,
			String ceiling) {
		List<String> result = new ArrayList<>();
		
		result.add(new String("\"" + floor + "\""));
		result.add(new String("\"" + version + "\""));
		
		result.add(new String("\"[" + floor + "," + ceiling + "\"]"));
		result.add(new String("\"[" + floor + "," + ceiling + "\")"));
		result.add(new String("\"(" + floor + "," + ceiling + "\"]"));
		result.add(new String("\"(" + floor + "," + ceiling + "\")"));

		result.add(new String("\"[" + version + "," + ceiling + "\"]"));
		result.add(new String("\"[" + version + "," + ceiling + "\")"));

		result.add(new String("\"[" + floor + "," + version + "\"]"));
		result.add(new String("\"(" + floor + "," + version + "\"]"));

		
		return result;
	}
	
	// Returns vector whose values are version-ranges, where version not belong 
	List<String> getNotCorrectVersionRanges(String version, String floor,
			String ceiling) {
		List<String> result = new ArrayList<>();
		
		result.add(new String("\"" + ceiling + "\""));

		result.add(new String("\"(" + version + "," + ceiling + "\"]"));
		result.add(new String("\"(" + version + "," + ceiling + "\")"));

		result.add(new String("\"[" + floor + "," + version + "\")"));
		result.add(new String("\"(" + floor + "," + version + "\")"));

		
		return result;
	}
	
	AdminPermission getPermission(String resourceName) throws Exception {
		PermissionInfo pi = new PermissionInfo(ConditionResource.getString(resourceName)); 

		return getPermission(pi);
	}
	
	AdminPermission getPermission(PermissionInfo pi) throws Exception {
		String actions = pi.getActions();
		String name = pi.getName();
		String type = pi.getType();
		
		Class< ? > pClass = Class.forName(type);
		Constructor< ? > constructor = pClass.getConstructor(new Class[] {
				String.class, String.class});
		AdminPermission permission = (AdminPermission)constructor.newInstance(
				new Object[] {name, actions});

		return permission;
	}
	
	void setPermissionsByPermissionAdmin(String bundleLocation, Permission permission) {
		PermissionInfo pInfo = new PermissionInfo(permission.getClass().getName(), 
									permission.getName(), permission.getActions());
		pAdmin.setPermissions(bundleLocation, new PermissionInfo[]{pInfo});
	}
	
	ConditionalPermissionInfo setPermissionsByCPermissionAdmin(ConditionInfo[] cInfos, Permission[] permissions) {
		PermissionInfo[] permInfos = createPermissionInfos(permissions);
		return cpAdmin.addConditionalPermissionInfo(cInfos, permInfos);
	}

	ConditionalPermissionInfo createConditionalPermissionInfo(ConditionInfo[] condInfos, Permission[] permissions) {
		PermissionInfo[] permInfos = createPermissionInfos(permissions);
		return cpAdmin.newConditionalPermissionInfo(null, condInfos, permInfos, ConditionalPermissionInfo.ALLOW);
	}

	PermissionInfo[] createPermissionInfos(Permission[] permissions) {
	    PermissionInfo[] permInfos = null;
	    if (permissions != null) {
	      permInfos = new PermissionInfo[permissions.length];
	      for (int i = 0; i < permissions.length; i++) {
	        Permission perm = permissions[i];
	        permInfos[i] = new PermissionInfo(perm.getClass().getName(), perm.getName(), perm.getActions());
	      }
	    }
	    return permInfos;
	}

	void testPermissions(ConditionInfo[] conditions, Permission permission, AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission) {
    testPermissions(conditions, new Permission[]{permission}, allowedPermission, notAllowedPermission);
	}
  
  void testPermissions(ConditionInfo[] conditions, Permission[] permissions, AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, permissions);
    ConditionalTestControl.trace("Test " + cpInfoToString(cpInfo));
    ConditionalTestControl.trace("Test for allowed permissions:");
    for (int i = 0; i < allowedPermission.length; i++) {
      allowed(allowedPermission[i]);
    }
    ConditionalTestControl.trace("Test for not allowed permissions:");
    for (int k = 0; k < notAllowedPermission.length; k++) {
      notAllowed(notAllowedPermission[k], SecurityException.class);
    }
    cpInfo.delete();
  }
  
	void testPermissions(ConditionInfo[] conditions, Permission[] permissions,
      AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission, String[] order1, String[] order2) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, permissions);
    ConditionalTestControl.trace("Test " + cpInfoToString(cpInfo));
    boolean testForAllowed = allowedPermission.length>0;
    ConditionalTestControl.trace("Test for allowed permissions:");
    for (int i = 0; i < allowedPermission.length; i++) {
      allowed(allowedPermission[i]);
    }
    if (testForAllowed) testEqualArrays(order1, order2, TestCondition.getSatisfOrder());
    ConditionalTestControl.trace("Test for not allowed permissions:");
    for (int k = 0; k < notAllowedPermission.length; k++) {
      notAllowed(notAllowedPermission[k], SecurityException.class);
    }
    if (testForAllowed) {
      //already order is tested, so only clear the vector
			TestCondition.satisfOrder.clear();
    } else {
      //order not tested yet, test it here
      testEqualArrays(order1, order2, TestCondition.getSatisfOrder());
    }
    cpInfo.delete();
  }
  
  private void testEqualArrays(String[] order1, String[] order2,
		String[] satisfOrder) {
	try {
		testEqualArrays(order1, satisfOrder);
	} catch (AssertionFailedError e) {
		if (order2 == null) 
			throw e;
		testEqualArrays(order2, satisfOrder);
	}
	
}

void deletePermissions(ConditionInfo[] conditions, Permission permission) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, new Permission[] {permission});
    ConditionalTestControl.pass("New permissions are set: " + cpInfoToString(cpInfo));
		Enumeration<ConditionalPermissionInfo> infos = cpAdmin
				.getConditionalPermissionInfos();
    ConditionalTestControl.pass("All set conditional permissions are: ");
    while (infos.hasMoreElements()) {
      ConditionalTestControl.pass(cpInfoToString(infos.nextElement()));
    }
    cpInfo.delete();
    ConditionalTestControl.pass("ConditionalPermissionInfo deleted");
    int cpCounter = 0;
    while (infos.hasMoreElements()) {
      cpCounter++;
      ConditionalTestControl.pass(cpInfoToString(infos.nextElement()));
    }
    ConditionalTestControl.assertEquals("After delete there have to be no conditional permissions: ", 0, cpCounter);
  }
  
  protected void testEqualArrays(String[] array1, String[] array2) {
    if (array1 != null && array2 != null) {
      ConditionalTestControl.assertEquals("The number of checked TestConditions: ", array1.length, array2.length);
      for (int i=0; i<array1.length; i++) {
        ConditionalTestControl.assertEquals("checked condition: ", array1[i], array2[i]);
      }
    } else {
      ConditionalTestControl.fail("Not correct check of TestConditions.");
    }
  }
  
	List<String> createWildcardDNs(String value) {
		List<String> result = new ArrayList<>();
		String semicolon = ";";
		//String asterisk = "*";		
		
		int lastIndex = 0;
		int semicolonIndex = value.indexOf(semicolon);
		String element;
		//String rdns;
		String prefix;
		String suffix;
		while (semicolonIndex != -1) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex, semicolonIndex);
			suffix = value.substring(semicolonIndex);
			
			//result.addElement(prefix + asterisk + suffix);
			result.addAll(addList(createWildcardRDNs(element), prefix, suffix));
			
			lastIndex = semicolonIndex + 1;
			semicolonIndex = value.indexOf(semicolon, lastIndex);
		}
		
		if (lastIndex > 0) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex);
			
			//result.addElement(prefix + asterisk);
			result.addAll(addList(createWildcardRDNs(element), prefix, ""));
		} else {
			result.addAll(createWildcardRDNs(value));
		}
		
		return result;
	}
	
	private List<String> addList(List<String> vec, String prefix,
			String suffix) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < vec.size(); ++i) {
			result.add(prefix + vec.get(i) + suffix);
		}
		
		return result;
	}
	
	
	private List<String> createWildcardRDNs(String value) {
		List<String> result = new ArrayList<>();
		String comma = ",";
		//String semicolon = ";";
		String equal = "=";
		String asterisk = "*";
		
		int lastIndex = 0;
		int commaIndex = value.indexOf(comma, lastIndex); 
		int equalIndex = value.indexOf(equal, lastIndex);
		
		while (commaIndex != -1) {
			result.add(asterisk + value.substring(commaIndex));
			if ((equalIndex != -1) && (equalIndex < commaIndex)) {
				result.add(value.substring(0, equalIndex + 1) + asterisk +
								  value.substring(commaIndex));
			}
			lastIndex = commaIndex + 1;
			commaIndex = value.indexOf(comma, lastIndex); 
			equalIndex = value.indexOf(equal, lastIndex);
		}
		
		if (lastIndex > 0) {
			result.add(asterisk);
		}
		if (equalIndex > 0) {
			result.add(value.substring(0, equalIndex + 1) + asterisk);
		}
		return result;
  }
	
	private Permission getPermission(AdminPermission permission, Bundle bundle) {
		return new AdminPermission(bundle, permission.getActions());
	}
	
	
}
