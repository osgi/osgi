/*
 * $Id$
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
import org.osgi.test.cases.permissionadmin.conditional.testcond.TestConditionRecursive;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;


import org.osgi.framework.*;
import org.osgi.service.condpermadmin.*;
import org.osgi.service.permissionadmin.*;

import java.security.AccessControlException;
import java.security.AllPermission;
import java.security.Permission;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.AssertionFailedError;

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

  private String            domBundleLocation;
  private Bundle            domBundle;

  private ConditionalPermissionAdmin  conditionalAdmin;
  private PermissionAdmin             permissionAdmin;
  protected ConditionalTBCService     tbc;
  protected ConditionalPermTBCService permTBC;
  protected ConditionalDomTBCService  domTBC;

  private ConditionalUtility          utility;
  
  private String            BUNDLE_LOCATION_CONDITION = BundleLocationCondition.class.getName();
  private String            BUNDLE_SIGNER_CONDITION = BundleSignerCondition.class.getName();

  /**
   * Creates correct and incorrect ConditionInfo.
   * Check if conditioninfos created from encoded are identical to the original
   */
  public void testConditionInfoCreation() throws Exception {//TC1
    trace("Test correct conditional infos creation:");
    try {
      trace("Create only with type '[type]'");
      new ConditionInfo("[conditionType]");
      new ConditionInfo("  [  conditionType  ]  ");
      new ConditionInfo("\t[\rconditionType\n]\t");
      new ConditionInfo("  [  conditionType  \"  location  \"  ]  ");
      new ConditionInfo("  [  conditionType  \"  arg1 ] arg2  \"  ]  ");
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

    // Bad ConditionInfo
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
    utility.createBadConditionInfo(" with comma separation between type and args ", "[" + conditionType + " ," +
        "\"" + location + "\"]", IllegalArgumentException.class);
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
    ConditionInfo testCInfo = utility.createTestCInfo(false, false, false, "TestCondition", testBundle.getBundleId());
    AdminPermission perm1 = new AdminPermission("*", "*");
    AdminPermission perm2 = new AdminPermission("*", AdminPermission.LIFECYCLE);
    PermissionInfo pInfo1 = new PermissionInfo(perm1.getClass().getName(), perm1.getName(), perm1.getActions());
    PermissionInfo pInfo2 = new PermissionInfo(perm2.getClass().getName(), perm2.getName(), perm2.getActions());

    ConditionInfo[] conditions = new ConditionInfo[]{testCInfo};

    int numOfInfos = 10;
    ConditionalPermissionInfo cpInfos[] = new ConditionalPermissionInfo[numOfInfos];
    //create conditional permission infos
    for (int i = 0; i < cpInfos.length; i++) {
      cpInfos[i] = conditionalAdmin.addConditionalPermissionInfo(conditions, new PermissionInfo[]{pInfo1});
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
      = conditionalAdmin.setConditionalPermissionInfo("cpInfo", conditions1, new PermissionInfo[]{pInfo1});
    ConditionalPermissionInfo cpInfo2 = null;
    try {
      ConditionalPermissionInfo recievedCPInfo = conditionalAdmin.getConditionalPermissionInfo("cpInfo");
      assertEquals("ConditionInfos ", arrayToString(cpInfo1.getConditionInfos()),
                   arrayToString(recievedCPInfo.getConditionInfos()));
      assertEquals("PermissionInfos ", arrayToString(cpInfo1.getPermissionInfos()),
                   arrayToString(recievedCPInfo.getPermissionInfos()));

      //create second with the same name (so only change the condition infos)
      cpInfo2 = conditionalAdmin.setConditionalPermissionInfo("cpInfo", conditions2, new PermissionInfo[]{pInfo2});

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

      //test if the second permission is now allowed (and if the first is not)
      utility.setTestBunde(testBundle, false);
      utility.allowed(perm2);
      utility.notAllowed(perm1, SecurityException.class);

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
      Class.forName(TestCondition.class.getName());
    } catch (Exception ex) {
      pass(ex.getMessage());
      //it doesn't work because loadClass method in RI works with the system class loader only
      if (ex instanceof ClassNotFoundException)
        fail("Please use Target_tck95.launch for successful run of 'testMoreConditions' test case");
    }

    // TODO This assumes an ordering not mandated by the specification
    // implementation assumption:
    // TestCondition_0 and TestCondion_2 will be executed before TestCondition_1 because they are not postponed
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, true, false, "TestCondition_0", testBundle.getBundleId()), // not postponed, satisfied, not mutable
        utility.createTestCInfo(true,  true, false, "TestCondition_1", testBundle.getBundleId()), // postponed, satisfied, not mutable
        utility.createTestCInfo(false, true, true,  "TestCondition_2", testBundle.getBundleId())  // not postponed, satisfied, mutable
      },
      new AdminPermission[]{permission},
      new AdminPermission[]{permission},     //allowed
      new AdminPermission[]{allPermissions}, //not allowed
      new String[] {"TestCondition_0", "TestCondition_2", "TestCondition_1"});

    // TODO This assumes an ordering not mandated by the specification
    // implementation assumption:
    // TestCondition_0_1 and TestCondion_1_1 will be executed before TestCondition_2_1 because they are not postponed
    // TestConditoin_1_1 will be executed again on a second permission check because it is mutable
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, true, false, "TestCondition_0_1", testBundle.getBundleId()), // not postponed, satisfied, not mutable
        utility.createTestCInfo(false, true, true,  "TestCondition_1_1", testBundle.getBundleId()), // not postponed, satisfied, mutable
        utility.createTestCInfo(true,  true, false, "TestCondition_2_1", testBundle.getBundleId())  // postponed, satisfied, not mutable
      },
      new AdminPermission[]{permission, permission2},
      new AdminPermission[]{permission, permission2}, //allowed
      new AdminPermission[]{allPermissions},          //not allowed
      new String[] {"TestCondition_0_1", "TestCondition_1_1", "TestCondition_2_1", "TestCondition_1_1"});

    // TODO This assumes an ordering not mandated by the specification
    //Check at creation 3 and 4_2 and 4_3, and then 4 and 4_1 because they are postponed
    utility.testPermissions(
        new ConditionInfo[]{
          utility.createTestCInfo(false, true, false, "TestCondition_3", testBundle.getBundleId()), // not postponed, satisfied, not mutable
          utility.createTestCInfo(true,  true, false, "TestCondition_4", testBundle.getBundleId()), // postponed, satisfied, not mutable
          utility.createTestCInfo(true,  true, true,  "TestCondition_4_1", testBundle.getBundleId()), // postponed, satisfied, mutable
          utility.createTestCInfo(false, true, true, "TestCondition_4_2", testBundle.getBundleId()), // not postponed, satisfied, mutable
          utility.createTestCInfo(false, true, true, "TestCondition_4_3", testBundle.getBundleId()), // not postponed, satisfied, mutable
        },
        new AdminPermission[] {permission},
        new AdminPermission[]{permission},     //allowed
        new AdminPermission[]{allPermissions}, //not allowed
        new String[] {"TestCondition_3", "TestCondition_4_2", "TestCondition_4_3", "TestCondition_4", "TestCondition_4_1"});

    // TODO This assumes an ordering not mandated by the specification
    // implementation note:
    // first permission check:
    // we do not ever check TestCondition_5 because a non-postponed condition 7 is not satisfied
    // second permission check:
    // we do not check 6 and 7 because while postponing 5 we check to see if the permission applies first
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(true, true, false,  "TestCondition_5", testBundle.getBundleId()), // postponed, satisfied, not mutable
        utility.createTestCInfo(false, true, true,  "TestCondition_6", testBundle.getBundleId()), // not postponed, satisfied, mutable
        utility.createTestCInfo(false, false, true, "TestCondition_7", testBundle.getBundleId()), // not postponed, not satisfied, mutable
      },
      new AdminPermission[] {permission},
      new AdminPermission[]{},               //allowed
      new AdminPermission[]{permission, allPermissions}, //not allowed
      new String[] {"TestCondition_6", "TestCondition_7"});

    //Don't check 2nd because 1st is not satisfied
    utility.testPermissions(
        new ConditionInfo[]{
          utility.createTestCInfo(false, false, false, "TestCondition_8", testBundle.getBundleId()), // not postponed, not satisfied, not mutable
          utility.createTestCInfo(false, true, false,  "TestCondition_9", testBundle.getBundleId()), // not postponed, satisfied, not mutable
        },
        new AdminPermission[] {permission},
        new AdminPermission[]{},
        new AdminPermission[]{permission},
        new String[] {"TestCondition_8"});

    // TODO This assumes an ordering not mandated by the specification
    // implementation note:
    // we don not ever check TestCondition_11 because a non-postponed condition 10 is not satisfied
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(false, false, true, "TestCondition_10", testBundle.getBundleId()), // not postponed, not satisfied, mutable
        utility.createTestCInfo(true, true, false,  "TestCondition_11", testBundle.getBundleId()), // postponed, satisfied, not mutable
      },
      new AdminPermission[] {permission},
      new AdminPermission[]{},//allowed
      new AdminPermission[]{permission, allPermissions},//permission, allPermissions
      new String[] {"TestCondition_10", "TestCondition_10"});

    // TODO This assumes an ordering not mandated by the specification
    // Don't check TestCondition_14 or 15 because TestCondition_12 is not satisfied
    // implementation note:
    // first permission check:
    // we do not ever check 14 and 15 because a non-postponed condition 12 is not satisfied
    // second permission check:
    // we do not check 12 14 and 15 because while postponing 12 we check to see if the permission applies first
    utility.testPermissions(
      new ConditionInfo[]{
        utility.createTestCInfo(true,  false, true, "TestCondition_12", testBundle.getBundleId()), // postponed, not satisfied, mutable
        utility.createTestCInfo(false, true, false, "TestCondition_13", testBundle.getBundleId()), // not postponed, satisfied, not mutable
        utility.createTestCInfo(true,  true, true,  "TestCondition_14", testBundle.getBundleId()), // postponed, satisfied, mutable
        utility.createTestCInfo(true,  true, false, "TestCondition_15", testBundle.getBundleId()), // postponed, satisfied, not mutable
      },
      new AdminPermission[] {permission},
      new AdminPermission[]{},//allowed
      new AdminPermission[]{permission, allPermissions},
      new String[] {"TestCondition_13", "TestCondition_12"});//according 9.5.1 and fig 9.38
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
      fail(e.getMessage());
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
      tbc = (ConditionalTBCService)getService(ConditionalTBCService.class);
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
   * Tests the case when multiple bundles are on the call stack and all
   * combinations of the tuples should be evaluated.
   */
  public void testMultipleBundlesOnStack() {//TC10
    try {
        Class.forName(TestCondition.class.getName());
    } catch (Exception ex) {
      pass(ex.getMessage());
      //it doesn't work because loadClass method in RI works with the system class loader only
      if (ex instanceof ClassNotFoundException)
        fail("Please use Target_tck95.launch for successful run of 'testMultipleBundlesOnStack' test case");
    }

    ConditionInfo blcA = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { testBundleLocation });
    ConditionInfo blcB = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { permBundleLocation });
    ConditionInfo blcC = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { domBundleLocation });

    AdminPermission permissionP = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission permissionQ = new AdminPermission("*", AdminPermission.EXECUTE);
    AdminPermission permissionR = new AdminPermission("*", AdminPermission.RESOLVE);
    AdminPermission permissionS = new AdminPermission("*", AdminPermission.STARTLEVEL);

    ConditionalPermissionInfo[][] cpi = new ConditionalPermissionInfo[3][4];
    PackagePermission pp = new PackagePermission("*", "import,export");
    ServicePermission sp = new ServicePermission("*", "get,register");
    ConditionInfo cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { testBundleLocation });
    cpi[0][0] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo },
        new Permission[] { pp, sp });

    cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { permBundleLocation });
    cpi[1][0] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo },
        new Permission[] { pp, sp });

    cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { domBundleLocation });
    cpi[2][0] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo },
        new Permission[] { pp, sp });

    ConditionInfo ic  = utility.createTestCInfo(false, true, false, "TestCondition_100", testBundle.getBundleId()); // not postponed, satisfied, immutable
    ConditionInfo ic0 = utility.createTestCInfo(false, false, true, "TestCondition_101", testBundle.getBundleId()); // not postponed, not satisfied, mutable
    ConditionInfo ic1 = utility.createTestCInfo(false, true,  true, "TestCondition_102", testBundle.getBundleId()); // not postponed, satisfied, mutable
    ConditionInfo ic2 = utility.createTestCInfo(false, true,  true, "TestCondition_103", testBundle.getBundleId()); // not postponed, satisfied, mutable
    ConditionInfo pc1 = utility.createTestCInfo(true,  false, true, "TestCondition_104", testBundle.getBundleId()); // postponed, not satisfied, mutable
    ConditionInfo pc2 = utility.createTestCInfo(true,  true,  true, "TestCondition_105", testBundle.getBundleId()); // postponed, satisfied, mutable

    cpi[0][1] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcA, ic1, pc1 },
        new Permission[] { permissionP, permissionQ });
    cpi[0][2] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcA, ic2 },
        new Permission[] { permissionP, permissionR });
    cpi[0][3] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcA, ic },
        new Permission[] { permissionS });

    ic  = utility.createTestCInfo(false, true, false, "TestCondition_100", permBundle.getBundleId()); // not postponed, satisfied, immutable
    ic0 = utility.createTestCInfo(false, false, true, "TestCondition_101", permBundle.getBundleId()); // not postponed, not satisfied, mutable
    ic1 = utility.createTestCInfo(false, true,  true, "TestCondition_102", permBundle.getBundleId()); // not postponed, satisfied, mutable
    ic2 = utility.createTestCInfo(false, true,  true, "TestCondition_103", permBundle.getBundleId()); // not postponed, satisfied, mutable
    pc1 = utility.createTestCInfo(true,  false, true, "TestCondition_104", permBundle.getBundleId()); // postponed, not satisfied, mutable
    pc2 = utility.createTestCInfo(true,  true,  true, "TestCondition_105", permBundle.getBundleId()); // postponed, satisfied, mutable

    cpi[1][1] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcB, ic1, pc1, pc2 },
        new Permission[] { permissionP, permissionR });
    cpi[1][2] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcB, pc2 },
        new Permission[] { permissionP, permissionR });
    cpi[1][3] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcB, ic },
        new Permission[] { permissionQ });

    ic  = utility.createTestCInfo(false, true, false, "TestCondition_100", domBundle.getBundleId()); // not postponed, satisfied, immutable
    ic0 = utility.createTestCInfo(false, false, true, "TestCondition_101", domBundle.getBundleId()); // not postponed, not satisfied, mutable
    ic1 = utility.createTestCInfo(false, true,  true, "TestCondition_102", domBundle.getBundleId()); // not postponed, satisfied, mutable
    ic2 = utility.createTestCInfo(false, true,  true, "TestCondition_103", domBundle.getBundleId()); // not postponed, satisfied, mutable
    pc1 = utility.createTestCInfo(true,  false, true, "TestCondition_104", domBundle.getBundleId()); // postponed, not satisfied, mutable
    pc2 = utility.createTestCInfo(true,  true,  true, "TestCondition_105", domBundle.getBundleId()); // postponed, satisfied, mutable

    cpi[2][1] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcC, ic },
        new Permission[] { permissionQ });
    cpi[2][2] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcC, ic0 },
        new Permission[] { permissionP });
    cpi[2][3] = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { blcC, pc2 },
        new Permission[] { permissionP });

    String message = "allowed " + utility.permToString(permissionP);
    try {
      tbc.checkStack(permissionP);
      pass(message);
    } catch (Throwable e) {
      fail(message + " but " + e.getClass().getName() + " was thrown");
    }

    String[] satisfOrder = TestCondition.getSatisfOrder();
    try {
      String[] order = new String[] {
          "TestCondition_100", "TestCondition_101", // immediate for tb3; postponed 105
          "TestCondition_102", "TestCondition_100", // immediate for tb2; postponed 104 and 105
          "TestCondition_102", "TestCondition_103", // immediate for tb1
          "TestCondition_105", // postponed for tb3
          "TestCondition_104", // postponed for tb2; only evaluated 104 because it is not satisfied
      };
      utility.testEqualArrays(order, satisfOrder);
    } catch (AssertionFailedError e) {
      // added for J9 
      String[] order = new String[] {
          "TestCondition_100", "TestCondition_100", "TestCondition_100",
          "TestCondition_102", "TestCondition_103", "TestCondition_102",
          "TestCondition_101", "TestCondition_104", "TestCondition_105",
      };
      utility.testEqualArrays(order, satisfOrder);
    }

    for (int i = 0; i < cpi.length; i++) {
      for (int j = 0; j < cpi[i].length; j++) {
        cpi[i][j].delete();
      }
    }
    TestCondition.satisfOrder.removeAllElements();
  }

  /**
   * Tests handling of a recursions in permission checks inside
   * Condition.isSatisfied.
   */
  public void testRecursionInChecks() {//TC11
    try {
        Class.forName(TestCondition.class.getName());
    } catch (Exception ex) {
      pass(ex.getMessage());
      //it doesn't work because loadClass method in RI works with the system class loader only
      if (ex instanceof ClassNotFoundException)
        fail("Please use Target_tck95.launch for successful run of 'testRecursionInChecks' test case");
    }

    ConditionInfo cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { domBundleLocation });
    ConditionInfo tc1 =utility.createTestCInfo(true, true, true, "TestConditionNR", domBundle.getBundleId());
    ConditionInfo tc2 = new ConditionInfo(TestConditionRecursive.class.getName(), 
        new String[] { "true", "true", "true", "TestConditionR", String.valueOf(domBundle.getBundleId())}); // postponed, satisfied, mutable

    AdminPermission perm1 = new AdminPermission("*", AdminPermission.EXECUTE);
    AdminPermission perm2 = new AdminPermission("*", AdminPermission.LIFECYCLE);

    ConditionalPermissionInfo cpi1 = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc1 },
        new Permission[] { perm1 });
    ConditionalPermissionInfo cpi2 = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc2 },
        new Permission[] { perm2 });
    
    TestConditionRecursive.setService(domTBC);
    TestConditionRecursive.setPermission(perm1);
    String message = "allowed " + utility.permToString(perm2);
    try {
      domTBC.checkPermission(perm2);
      pass(message);
    } catch (Throwable e) {
      fail(message + " but " + e.getClass().getName() + " was thrown");
    }
    utility.testEqualArrays(new String[] { "TestConditionR", "TestConditionNR" }, TestCondition.getSatisfOrder());

    TestConditionRecursive.setPermission(perm2);
    message = "not allowed " + utility.permToString(perm2);
    try {
      domTBC.checkPermission(perm2);
      pass(message);
    } catch (Throwable e) {
      fail(message + " but " + e.getClass().getName() + " was thrown");
    }
    utility.testEqualArrays(new String[] { "TestConditionR", "java.lang.SecurityException" }, TestCondition.getSatisfOrder());

    cpi1.delete();
    cpi2.delete();
  }

  /**
   * Check a condition that starts as a mutable and later becomes immutable.
   */
  public void testMutable2Immutable() {//TC12
	// This test assumes an implementation ordering of the calls to isMutable and then isSatisfied
	// TODO this testcase mandates that implementations optimize out immutable conditions, this is optional in the spec.
    utility.setTestBunde(testBundle, false);
    TestCondition.satisfOrder.removeAllElements();
    
    try {
        Class.forName(TestCondition.class.getName());
    } catch (Exception ex) {
      pass(ex.getMessage());
      //it doesn't work because loadClass method in RI works with the system class loader only
      if (ex instanceof ClassNotFoundException)
        fail("Please use Target_tck95.launch for successful run of 'testMoreConditions' test case");
    }

    ConditionInfo cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, new String[] { testBundleLocation });
    ConditionInfo tc1  = utility.createTestCInfo(false, false, true, "TestCondition_200", testBundle.getBundleId()); // not postponed, not satisfied, mutable
    ConditionInfo tc2  = utility.createTestCInfo(false,  true, true, "TestCondition_201", testBundle.getBundleId()); // not postponed, satisfied, mutable
    ConditionInfo tc3  = utility.createTestCInfo( true, false, true, "TestCondition_202", testBundle.getBundleId()); // postponed, not satisfied, mutable
    ConditionInfo tc4  = utility.createTestCInfo( true,  true, true, "TestCondition_203", testBundle.getBundleId()); // postponed, satisfied, mutable

    AdminPermission perm1 = new AdminPermission("*", AdminPermission.EXECUTE);
    AdminPermission perm2 = new AdminPermission("*", AdminPermission.LIFECYCLE);
    AdminPermission perm3 = new AdminPermission("*", AdminPermission.LISTENER);
    AdminPermission perm4 = new AdminPermission("*", AdminPermission.STARTLEVEL);

    ConditionalPermissionInfo cpi_df = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo },
        new Permission[] { new AdminPermission("*", AdminPermission.EXTENSIONLIFECYCLE) });

    TestCondition.changeToImmutable(true);
    ConditionalPermissionInfo cpi = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc1 },
        new Permission[] { perm1 });
    utility.notAllowed(perm1, SecurityException.class);
    utility.notAllowed(perm1, SecurityException.class);
    utility.notAllowed(perm1, SecurityException.class);
    utility.testEqualArrays(new String[] { "TestCondition_200", "TestCondition_200" }, TestCondition.getSatisfOrder());
    cpi.delete();

    cpi = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc2 },
        new Permission[] { perm2 });
    utility.allowed(perm2);
    utility.allowed(perm2);
    utility.allowed(perm2);
    utility.testEqualArrays(new String[] { "TestCondition_201", "TestCondition_201" }, TestCondition.getSatisfOrder());
    cpi.delete();

    cpi = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc3 },
        new Permission[] { perm3 });
    utility.notAllowed(perm3, SecurityException.class);
    utility.notAllowed(perm3, SecurityException.class);
    utility.notAllowed(perm3, SecurityException.class);
    utility.testEqualArrays(new String[] { "TestCondition_202", "TestCondition_202" }, TestCondition.getSatisfOrder());
    cpi.delete();

    cpi = utility.setPermissionsByCPermissionAdmin(new ConditionInfo[] { cInfo, tc4 },
        new Permission[] { perm4 });
    utility.allowed(perm4);
    utility.allowed(perm4);
    utility.allowed(perm4);
    utility.testEqualArrays(new String[] { "TestCondition_203", "TestCondition_203" }, TestCondition.getSatisfOrder());
    cpi.delete();
    
    TestCondition.changeToImmutable(false);
    cpi_df.delete();
  }

  /**
   * <remove>Prepare for each method. It is important that each method can
   * be executed independently of each other method. Do not keep
   * state between methods, if possible. This method can be used
   * to clean up any possible remaining state.</remove>
   */
  public void setState() throws Exception{
	assertTrue("Must have a security manager", System.getSecurityManager() != null);
	assertTrue(serviceAvailable(PermissionAdmin.class)); 
	assertTrue(serviceAvailable(ConditionalPermissionAdmin.class));

	installBundle("testcond.jar", false);

    testBundle = installBundle("tb1.jar");
    testBundleLocation = testBundle.getLocation();

    permBundle = installBundle("tb2.jar");
    permBundleLocation = permBundle.getLocation();

    domBundle = installBundle("tb3.jar");
    domBundleLocation = domBundle.getLocation();

    permissionAdmin = (PermissionAdmin)getService(PermissionAdmin.class);
    conditionalAdmin = (ConditionalPermissionAdmin)getService(ConditionalPermissionAdmin.class);
    tbc = (ConditionalTBCService)getService(ConditionalTBCService.class);
    permTBC = (ConditionalPermTBCService)getService(ConditionalPermTBCService.class);
    domTBC = (ConditionalDomTBCService)getService(ConditionalDomTBCService.class);

    utility = new ConditionalUtility(this, permissionAdmin, conditionalAdmin);
    // make sure this bundle has all permissions
    permissionAdmin.setPermissions(getContext().getBundle().getLocation(), new PermissionInfo[] {new PermissionInfo("(" + AllPermission.class.getName() +")")});

    // make sure the test bundles do not have any location permissions set; SimplePermissionPolicy sets these
    permissionAdmin.setPermissions(testBundleLocation, null);
    permissionAdmin.setPermissions(permBundleLocation, null);
    permissionAdmin.setPermissions(domBundleLocation, null);
  }
 
  /**
   * Clean up after each method. Notice that during debugging
   * many times the unsetState is never reached.
   */
  public void clearState() throws Exception{
	  ungetService(tbc);
	  ungetService(permTBC);
	  ungetService(domTBC);
	  testBundle.uninstall();
	  permBundle.uninstall();
	  domBundle.uninstall();
	ConditionalPermissionUpdate update = conditionalAdmin.newConditionalPermissionUpdate();
    update.getConditionalPermissionInfos().clear();
    update.commit();
    String[] locations = permissionAdmin.getLocations();
    if (locations != null)
    	for (int i = 0; i < locations.length; i++)
    		permissionAdmin.setPermissions(locations[i], null);
    ungetAllServices();
  }

}
