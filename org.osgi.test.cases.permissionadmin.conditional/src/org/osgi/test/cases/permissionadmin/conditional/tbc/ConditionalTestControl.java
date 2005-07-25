/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import org.osgi.test.cases.permissionadmin.conditional.testcond.TestCondition;
import org.osgi.test.cases.util.DefaultTestBundleControl;

import org.osgi.framework.*;
import org.osgi.service.condpermadmin.*;
import org.osgi.service.permissionadmin.*;

import java.security.AccessControlException;
import java.security.Permission;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Contains the test methods of the conditional permission test case.
 * 
 * @author Petia Sotirova
 * @version 1.0
 */
public class ConditionalTestControl extends DefaultTestBundleControl {
  
  private String            testBundleLocation;
  private Bundle            testBundle;
  
  private String            permBundleLocation;
  private Bundle            permBundle;
  
  private ConditionalPermissionAdmin  conditionalAdmin;
  private PermissionAdmin             permissionAdmin;
  private ConditionalTBCService       tbc;
  private ConditionalPermTBCService   permTBC;
  
  private ConditionalUtility          utility;
  
  private String            BUNDLE_LOCATION_CONDITION = BundleLocationCondition.class.getName();
  private String            BUNDLE_SIGNER_CONDITION = BundleSignerCondition.class.getName();
  private static String[] methods = new String[] {"testConditionInfoCreation", // "TC1"
                                                  "testConditionalPermissionAdmin", // "TC2"
                                                  "testNamedConditionalPermissionAdmin", // "TC2_1"
                                                  "testConditionInfoDeletion", // "TC3"
                                                  "testBundleLocationCondition", // "TC4"
                                                  "testBundleSignerCondition", // "TC5"
                                                  "testMoreConditions", // "TC6"
                                                  "testConditionalPA_and_PA", //"TC7"
                                                  "testBundlePermissionInformation", //"TC8"
                                                  "testCPInfosSetBeforeInstallBundle" //"TC9"
                                                  };
  
  
  /**
   * <remove>Prepare for each run. It is important that a test run is properly
   * initialized and that each case can run standalone. To save a lot
   * of time in debugging, clean up all possible persistent remains
   * before the test is run. Clean up is better don in the prepare
   * because debugging sessions can easily cause the unprepare never
   * to be called.</remove> 
   */
  public void prepare() throws Exception {
    testBundle = installBundle("tb1.jar");
    testBundleLocation = testBundle.getLocation();
    
    permBundle = installBundle("tb2.jar");
    permBundleLocation = permBundle.getLocation();
        
    permissionAdmin = (PermissionAdmin)getService(PermissionAdmin.class);
    conditionalAdmin = (ConditionalPermissionAdmin)getService(ConditionalPermissionAdmin.class);
    tbc = (ConditionalTBCService)getService(ConditionalTBCService.class);
    permTBC = (ConditionalPermTBCService)getService(ConditionalPermTBCService.class);
      
    utility = new ConditionalUtility(this, tbc, permissionAdmin, conditionalAdmin, permTBC);
  }

  /**
   * <remove>Prepare for each method. It is important that each method can
   * be executed independently of each other method. Do not keep
   * state between methods, if possible. This method can be used
   * to clean up any possible remaining state.</remove> 
   */
  public void setState() {
    log("#before each method");
    
    Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();       
    while ((infos != null) && infos.hasMoreElements()) {
      ((ConditionalPermissionInfo)infos.nextElement()).delete();
    }
    
    permissionAdmin.setPermissions(permBundleLocation, null);
    permissionAdmin.setPermissions(testBundleLocation, null);
  }

  
  /**
   * Creates correct and incorrect ConditionInfo.
   * Check if conditioninfos created from encoded are identical to the original
   */
  public void testConditionInfoCreation() throws Exception {//TC1
    trace("Test correct conditional infos creation:");    
    try {
      trace("Create only with type '[type]'");
      new ConditionInfo("[conditionType]");
    } catch (Exception e) {
      fail("ConditonInfo not created. " + e.getClass() + ": " + e.getMessage());
    }
    //Assert Equals
    String conditionType = BUNDLE_LOCATION_CONDITION;
    String location = "test.location";
    ConditionInfo info1 = new ConditionInfo(conditionType, new String[]{location});
    ConditionInfo info2 = new ConditionInfo("[" + conditionType + " " +
                        "\"" + location + "\"]");
    assertEquals("Constructed from a string ", info1, info2);
    assertEquals("toString ", info2.getEncoded(), info1.toString());
    assertEquals("Identical hashcodes ", info1.hashCode(), info2.hashCode());
    assertEquals("Identical types ", info1.getType(), info2.getType());
    assertEquals("Identical args ", arrayToString(info1.getArgs()), arrayToString(info2.getArgs()));
    
    // Bad CondittionInfo
    trace("Test incorrect conditional infos creation:");
    utility.createBadConditionInfo(" with null type", null, new String[]{location}, 
                NullPointerException.class);
    utility.createBadConditionInfo(" with missing type ", "[" + " " + " " +
        "\"" + location + "\"]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with missing open square brace ", " " + conditionType + " " +
        "\"" + location + "\"]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with missing closing square brace ", "[" + conditionType + " " +
        "\"" + location + "\" ", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with missing args quote ", "[" + conditionType + " " +
        location + "]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with argument after closing square brace ", "[" + conditionType + " " +
        "\"" + location + "\"]" + "\"" + location + "\"", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with missing open args quote ", "[" + conditionType + " " +
        location + "\"]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with missing closing args quote ", "[" + conditionType + " " +
        "\"" + location + "]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with too much whitespace between type and args ", "[" + conditionType + "    " +
        "\"" + location + "]", IllegalArgumentException.class);
    utility.createBadConditionInfo(" with comma separation between type and args ", "[" + conditionType + " ," +
        "\"" + location + "]", IllegalArgumentException.class);
  }
  
  /**
   * Check if ConditionalPermissionInfos added in CoditionalPermissionAdmin
   * are identical to the original.
   */
  public void testConditionalPermissionAdmin() {//TC2
    ConditionInfo cInfo1 = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
        new String[]{testBundleLocation});    
    ConditionInfo cInfo2 = new ConditionInfo(BUNDLE_SIGNER_CONDITION,
        new String[]{ConditionResource.getString(ConditionalUtility.DN_S)});
        
    PermissionInfo pInfo = new PermissionInfo(AdminPermission.class.getName(), "*", "*");
    
    ConditionInfo[] conditions = new ConditionInfo[]{cInfo1, cInfo2 };
    PermissionInfo[] permissions = new PermissionInfo[]{pInfo}; 
    
    ConditionalPermissionInfo cpInfo 
      = conditionalAdmin.addConditionalPermissionInfo(conditions, permissions);
    
    assertEquals("ConditionInfos ", arrayToString(cpInfo.getConditionInfos()), 
                                    arrayToString(conditions));
    assertEquals("PermissionInfos ", arrayToString(cpInfo.getPermissionInfos()), 
                                     arrayToString(permissions));
    
    Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();
    
    boolean addConditionalPermission = false;
    ConditionalPermissionInfo addedCpInfo = null;   
    while (infos.hasMoreElements()) {
      addedCpInfo = (ConditionalPermissionInfo)infos.nextElement();
      if (addedCpInfo.equals(cpInfo)) {
        addConditionalPermission = true;
        break;
      }
    }
    
    assertTrue("addConditionalPermission correct ", addConditionalPermission);

    assertEquals("ConditionInfos ", arrayToString(cpInfo.getConditionInfos()), 
                                    arrayToString(addedCpInfo.getConditionInfos()));
    assertEquals("PermissionInfos ", arrayToString(cpInfo.getPermissionInfos()), 
                                     arrayToString(addedCpInfo.getPermissionInfos()));
  }
  
  /**
   * Test if ConditionalPermissionInfos are created with unique names and set (or create) 
   * a Conditional Permission Info with conditions and permissions.
   */
  public void testNamedConditionalPermissionAdmin() {//TC2_1
    //1: Test unique names
    ConditionInfo testCInfo = utility.createTestCInfo(false, false, false, "TestCondition");        
    PermissionInfo pInfo = new PermissionInfo(AdminPermission.class.getName(), "*", "*");
    
    ConditionInfo[] conditions = new ConditionInfo[]{testCInfo};//cInfo1, cInfo2 };
    PermissionInfo[] permissions = new PermissionInfo[]{pInfo};    
    
    int numOfInfos = 10;
    ConditionalPermissionInfo cpInfos[] = new ConditionalPermissionInfo[numOfInfos];
    //create conditional permission infos
    for (int i = 0; i < cpInfos.length; i++) {
      cpInfos[i] = conditionalAdmin.addConditionalPermissionInfo(conditions, permissions);
    }
    //get name of the created conditional permission infos
    String[] names = new String[numOfInfos];
    for (int i = 0; i < names.length; i++) {
      names[i] = cpInfos[i].getName();
    }
    //see if the names are unique
    try {      
      String name, namePrev;
      for (int i = 1; i < names.length; i++) {
        name = names[i];
        namePrev = names[i - 1];
        pass("Test unique name" + i + " and name" + (i - 1) + " in addConditionalPermissionInfo");
        if (name.equals(namePrev)) {
          fail("The names of CPI are same: " + name);
        }
      }
    } finally {
      for (int i = 0; i < cpInfos.length; i++) {
        cpInfos[i].delete();
      }
      TestCondition.satisfOrder.removeAllElements();
    }  
    
    //2: Test set (or create) a Conditional Permission Info with conditions and permissions
    ConditionInfo cInfo1 = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
        new String[]{""});//testBundleLocation
    ConditionInfo cInfo2 = new ConditionInfo(BUNDLE_SIGNER_CONDITION,
        new String[]{ConditionResource.getString(ConditionalUtility.DN_S)});
    
    ConditionInfo[] conditions1 = new ConditionInfo[]{cInfo1};
    ConditionInfo[] conditions2 = new ConditionInfo[]{cInfo2 };
    
    //create first
    ConditionalPermissionInfo cpInfo1 
      = conditionalAdmin.setConditionalPermissionInfo("cpInfo", conditions1, permissions);
    ConditionalPermissionInfo cpInfo2 = null;
    try {
      ConditionalPermissionInfo recievedCPInfo = conditionalAdmin.getConditionalPermissionInfo("cpInfo");
      assertEquals("ConditionInfos ", arrayToString(cpInfo1.getConditionInfos()),
          arrayToString(recievedCPInfo.getConditionInfos()));
      assertEquals("PermissionInfos ", arrayToString(cpInfo1.getPermissionInfos()),
          arrayToString(recievedCPInfo.getPermissionInfos()));

      //create second with the same name (so only change the condition infos)
      cpInfo2 = conditionalAdmin.setConditionalPermissionInfo("cpInfo",
          conditions2, permissions);

      Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();
      int setInfosNumber = 0;
      while (infos.hasMoreElements()) {
        setInfosNumber++;
        infos.nextElement();
      }
      //test if really a new one is not set
      assertEquals("setConditionalPermissionInfo with one name ", 1, setInfosNumber);
      //test if the permissions for cpInfo1 are replaced with the second ones (of cpInfo2)
      recievedCPInfo = conditionalAdmin.getConditionalPermissionInfo("cpInfo");
      assertEquals("ConditionInfos ", arrayToString(cpInfo2.getConditionInfos()),
          arrayToString(recievedCPInfo.getConditionInfos()));
      assertEquals("PermissionInfos ", arrayToString(cpInfo2.getPermissionInfos()),
          arrayToString(recievedCPInfo.getPermissionInfos()));
    } catch (Exception e) {
      cpInfo1.delete();
      cpInfo2.delete();
    }    
  }
  
  /**
   * Check if ConditionalPermissionInfos deleted from CoditionalPermissionAdmin
   * are really removed.
   */
  public void testConditionInfoDeletion() {//TC3
    ConditionInfo cInfo1 = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
        new String[]{testBundleLocation});    
    ConditionInfo cInfo2 = new ConditionInfo(BUNDLE_SIGNER_CONDITION,
        new String[]{ConditionResource.getString(ConditionalUtility.DN_S)});
        
    ConditionInfo[] conditions = new ConditionInfo[]{cInfo1, cInfo2 };
    AdminPermission permission = new AdminPermission("*", AdminPermission.LIFECYCLE);
    utility.deletePermissions(new ConditionInfo[]{cInfo1}, permission);
    utility.deletePermissions(conditions, permission);
  }
  
  
  /**
   * Check if only the bundle with the appropriate location 
   * has(only) corresponding permissions.
   */
  public void testBundleLocationCondition() {//TC4
    utility.setTestBunde(testBundle, false);
    //remove all permissions
    permissionAdmin.setPermissions(testBundleLocation, null);
    
    ConditionInfo cInfo = null;
    AdminPermission permission = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission allPermissions = new AdminPermission("*", "*");
  
    Vector locations = utility.getWildcardString(testBundleLocation);
    for (int i = 0; i < locations.size(); ++i) {
      //permissionAdmin.setPermissions((String)locations.elementAt(i), null);
      cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
                                new String[]{(String)locations.elementAt(i)});
      utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
               new AdminPermission[]{permission}, new AdminPermission[]{allPermissions});
    }
    
    //Sets permission with not satisfied conditions and checks if the permission is not allowed
    
    //This block will be commented for now because it is not clear if in this case
    //there are no permissions or the default permissions (all permissions now)
//    String bundleLocation = getContext().getBundle().getLocation();
//    cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[]{bundleLocation});
//    utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
//             new AdminPermission[]{}, //allowed
//             new AdminPermission[]{permission, allPermissions}); //not allowed
  }
  
  /**
   * Check if only the bundle with the appropriate certificates 
   * has(only) corresponding permissions.
   */
  public void testBundleSignerCondition() {//TC5
    utility.setTestBunde(testBundle, false);
    permissionAdmin.setPermissions(testBundleLocation, null); 
    
    ConditionInfo cInfo = null;
    AdminPermission permission = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission allPermissions = new AdminPermission("*", "*");    
 
    //test with appropriate certificates 
    pass("Test with appropriate certificates");
    String dn_s_value = ConditionResource.getString(ConditionalUtility.DN_S);
    Vector dn_s = utility.createWildcardDNs(dn_s_value);

    String element;
    for (int i = 0; i < dn_s.size(); ++i) {
      element = (String)dn_s.elementAt(i);
      cInfo = new ConditionInfo(BUNDLE_SIGNER_CONDITION, new String[]{element});    
      utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
          new AdminPermission[]{permission}, new AdminPermission[]{allPermissions});
    }
    
    // test with inappropriate certificates 
    cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[]{testBundleLocation});
    ConditionalPermissionInfo condition = 
      utility.setPermissionsByCPermissionAdmin(new ConditionInfo[]{cInfo}, 
          new Permission[]{new PropertyPermission("java.vm", "read")});
    pass("Test with inappropriate certificates");
    try {
      dn_s_value = ConditionResource.getString(ConditionalUtility.INAPPROPRIATE_DN_S);
  
      StringTokenizer st = new StringTokenizer(dn_s_value, ConditionalUtility.SEPARATOR);
      while (st.hasMoreTokens()) {
        cInfo = new ConditionInfo(BUNDLE_SIGNER_CONDITION, new String[]{st.nextToken()});
        utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
              new AdminPermission[]{}, new AdminPermission[]{permission, allPermissions});
      }
    } finally {
      condition.delete();
    }
  }  
  
  /**
   * Check different cases with satisfied, postponed and mutable conditions.
   */
  public void testMoreConditions() {//TC6
    utility.setTestBunde(testBundle, false);
    
    AdminPermission permission = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission permission2 = new AdminPermission("*", AdminPermission.LISTENER);
    AdminPermission allPermissions = new AdminPermission("*", "*");

    try {
      TestCondition.setTestBundleLocation(testBundleLocation);
    } catch (Exception ex) {
      pass(ex.getMessage());
      //it doesn't work because loadClass method in RI works with the system class loader only
      if (ex instanceof ClassNotFoundException)
        fail("Please use Target_tck95.launch for successful run of 'testMoreConditions' test case");
    }
    
    //Check TestCondition_0 and TestCondition_1 at creation and later TestCondition_2 (when the check is performed)
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, true, false, "TestCondition_0"), // not postponed, satisfied, not mutable
        utility.createTestCInfo(true,  true, false, "TestCondition_1"), // postponed, satisfied, not mutable    
        utility.createTestCInfo(false, true, true,  "TestCondition_2")  // not postponed, satisfied, mutable 
      }, 
      new AdminPermission[]{permission}, 
      new AdminPermission[]{permission},     //allowed
      new AdminPermission[]{allPermissions}, //not allowed
      new String[] {"TestCondition_0", "TestCondition_1", "TestCondition_2"}); 
    
    //Check TestCondition_0_1 and TestCondition_2_1 at creation and later two times 
    //TestCondition_1_1 (when every check is performed)
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, true, false, "TestCondition_0_1"), // not postponed, satisfied, not mutable
        utility.createTestCInfo(false, true, true,  "TestCondition_1_1"), // not postponed, satisfied, mutable    
        utility.createTestCInfo(true,  true, false, "TestCondition_2_1")  // npostponed, satisfied, not mutable  
      }, 
      new AdminPermission[]{permission, permission2}, 
      new AdminPermission[]{permission, permission2}, //allowed
      new AdminPermission[]{allPermissions},          //not allowed
      new String[] {"TestCondition_0_1", "TestCondition_2_1", "TestCondition_1_1", "TestCondition_1_1"});    

    //Check at creation 3 and 4, and then, 4_2, 4_3 and finally 4_1 because it is postponed
    utility.testPermissions(
        new ConditionInfo[]{
          utility.createTestCInfo(false, true, false, "TestCondition_3"), // not postponed, satisfied, not mutable
          utility.createTestCInfo(true,  true, false, "TestCondition_4"), // postponed, satisfied, not mutable
          utility.createTestCInfo(true,  true, true,  "TestCondition_4_1"), // postponed, satisfied, mutable
          utility.createTestCInfo(false, true, true, "TestCondition_4_2"), // not postponed, satisfied, mutable
          utility.createTestCInfo(false, true, true, "TestCondition_4_3"), // not postponed, satisfied, mutable
        }, 
        new AdminPermission[] {permission}, 
        new AdminPermission[]{permission},     //allowed
        new AdminPermission[]{allPermissions}, //not allowed
        new String[] {"TestCondition_3", "TestCondition_4", "TestCondition_4_2", "TestCondition_4_3", "TestCondition_4_1"});

    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(true, true, false,  "TestCondition_5"), // postponed, satisfied, not mutable
        utility.createTestCInfo(false, true, true,  "TestCondition_6"), // not postponed, satisfied, mutable
        utility.createTestCInfo(false, false, true, "TestCondition_7"), // not postponed, not satisfied, mutable
      }, 
      new AdminPermission[] {permission}, 
      new AdminPermission[]{},               //allowed
      new AdminPermission[]{permission, allPermissions}, //not allowed
      new String[] {"TestCondition_5", "TestCondition_6", "TestCondition_7", "TestCondition_6", "TestCondition_7"});
    
    //Don't check 2nd because 1st is not satisfied
    utility.testPermissions(
        new ConditionInfo[]{
          utility.createTestCInfo(false, false, false, "TestCondition_8"), // not postponed, not satisfied, not mutable
          utility.createTestCInfo(false, true, false,  "TestCondition_9"), // not postponed, satisfied, not mutable    
        }, 
        new AdminPermission[] {permission}, 
        new AdminPermission[]{allPermissions}, //because they are by default
        new AdminPermission[]{},//permission, allPermissions
        new String[] {"TestCondition_8"});
  
    
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, false, true, "TestCondition_10"), // not postponed, not satisfied, mutable   
        utility.createTestCInfo(true, true, false,  "TestCondition_11"), // postponed, satisfied, not mutable
      }, 
      new AdminPermission[] {permission}, 
      new AdminPermission[]{},//allowed
      new AdminPermission[]{permission, allPermissions},//permission, allPermissions
      new String[] {"TestCondition_11", "TestCondition_10", "TestCondition_10"});
    
    // Don't check TestCondition_14 because TestCondition_12 is not satisfied
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(true,  false, true, "TestCondition_12"), // postponed, not satisfied, mutable   
        utility.createTestCInfo(false, true, false, "TestCondition_13"), // postponed, satisfied, not mutable
        utility.createTestCInfo(true,  true, true,  "TestCondition_14"), // postponed, satisfied, not mutable
        utility.createTestCInfo(true,  true, false, "TestCondition_15"), // postponed, satisfied, not mutable
      }, 
      new AdminPermission[] {permission}, 
      new AdminPermission[]{},//allowed
      new AdminPermission[]{permission, allPermissions},//permission, allPermissions
      new String[] {"TestCondition_13", "TestCondition_15", "TestCondition_12"});//according 9.5.1 and fig 9.38
  }
  
  /**
   * Tests interaction between ConditionalPermissionAdmin and PermissionAdmin.
   */
  public void testConditionalPA_and_PA() throws Exception {//TC7
    utility.setTestBunde(testBundle, false);
    
    AdminPermission pCPA = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission pPA = new AdminPermission("*", AdminPermission.LISTENER); 
    //get permissions before test
    PermissionInfo[] origPermissions = permissionAdmin.getPermissions(testBundleLocation);
        
    ConditionInfo cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[]{testBundleLocation});
    utility.testPermissions(new ConditionInfo[]{cInfo}, pCPA, 
        new AdminPermission[]{pCPA}, new AdminPermission[]{pPA});
    
    utility.setPermissionsByPermissionAdmin(testBundleLocation, pPA);
    utility.testPermissions(new ConditionInfo[]{cInfo}, pCPA, 
        new AdminPermission[]{}, new AdminPermission[]{pCPA});
    
    utility.allowed(pPA);   
    //restore permissions after test
    permissionAdmin.setPermissions(testBundleLocation, origPermissions);
  }
  
  
  /**
   * Tests permissions when exists file OSGI-INF/permissions.perm  
   */
  public void testBundlePermissionInformation() throws Exception {//TC8
    utility.setTestBunde(permBundle, true);
    
    AdminPermission pFromFile = utility.getPermission(ConditionalUtility.REQUIRED);
    AdminPermission pCP = utility.getPermission(ConditionalUtility.CP_PERMISSION);
    AdminPermission pCPIntersection = utility.getPermission(ConditionalUtility.REQUIRED_CP_PERMISSION);
    ConditionInfo cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[]{permBundleLocation});
    
    // ConditionalPermissionAdmin && permissions.perm
    utility.testPermissions(new ConditionInfo[]{cInfo}, pCP, 
        new AdminPermission[]{pCPIntersection}, new AdminPermission[]{pFromFile, pCP});
    
    AdminPermission pAP = utility.getPermission(ConditionalUtility.P_PERMISSION);
    AdminPermission pAPIntersection = utility.getPermission(ConditionalUtility.REQUIRED_P_PERMISSION);
    
    // PermissionAdmin && ConditionalPermissionAdmin && permissions.perm
    utility.setPermissionsByPermissionAdmin(permBundleLocation, pAP);
    utility.testPermissions(new ConditionInfo[]{cInfo}, pCP, 
        new AdminPermission[]{}, new AdminPermission[]{pFromFile, pCP, pCPIntersection});
    
    utility.setPermissionsByCPermissionAdmin(new ConditionInfo[]{cInfo}, new AdminPermission[] {pCP});
    utility.allowed(pAPIntersection);
  }
  
  /**
   * Tests if the conditions permissioninfos set before bundle instalation 
   * are automatically applied to bundle when it is installed.
   */
  public void testCPInfosSetBeforeInstallBundle() {//TC9
    //remove all permissions
    permissionAdmin.setPermissions(testBundleLocation, null);
    try {
      testBundle.uninstall();
      pass("Bundle tb1 uninstalled");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    //set permissions
    ConditionInfo cInfo = null;
    AdminPermission allPermissions = new AdminPermission("*", "*");
    AdminPermission execPermissions = new AdminPermission("*", AdminPermission.EXECUTE);
    PackagePermission pp = new PackagePermission("*", "import,export");
    ServicePermission sp = new ServicePermission("*", "get,register");
    
    cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[]{testBundleLocation});
    ConditionalPermissionInfo condition = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[]{cInfo}, 
                                                                     new Permission[]{execPermissions, pp, sp});
    
    //install bundle again and see if the conditions permissions are applied to it
    try {
      testBundle = installBundle("tb1.jar");
      utility.setConditionalTBCService((ConditionalTBCService)getService(ConditionalTBCService.class));
      utility.setTestBunde(testBundle, false);
      utility.allowed(execPermissions);
      utility.notAllowed(allPermissions, SecurityException.class);
    } catch (Exception e1) {
      String message = "Exception thrown while installing bundle again and testing permisions. " 
                       + e1.getClass() + ": " + e1.getMessage();
      if (e1 instanceof SecurityException || e1 instanceof AccessControlException) {
        pass(message);
      } else {
        fail(message);        
      }
    } finally {
      condition.delete();
    }
  }
  
  /**
   * Clean up after each method. Notice that during debugging
   * many times the unsetState is never reached.
   */
  public void unsetState() {
    log("#after each method");
  }

  /**
   * Clean up after a run. 
   */
  public void unprepare() {
    log("#after each run");
    Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();
    
    while ((infos != null) && infos.hasMoreElements()) {
      ((ConditionalPermissionInfo)infos.nextElement()).delete();
    }
    
    permissionAdmin.setPermissions(testBundleLocation, null);
    permissionAdmin.setPermissions(permBundleLocation, null);    
  }
  

  protected String[] getMethods() {
    return methods;
  }
}
