package com.nokia.test.doit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.condpermadmin.*;
import org.osgi.util.tracker.ServiceTracker;

import com.nokia.test.db.Db;
import com.nokia.test.exampleresourceprocessor.db.api.DbRpTest;

public class DoIt implements BundleActivator, ServiceListener {
    
    private static final String HOME = 
        "../../org.osgi.impl.service.deploymentadmin.test/res/";
    
    private BundleContext  context;
    private ServiceTracker trackDa;
    private ServiceTracker trackCondPa;
    private ServiceTracker trackPa;
    private ServiceTracker trackTestsRunner;
    private ServiceTracker trackDb;
    private ServiceTracker trackRp;

    private TestDesktop desktop;

    public void start(BundleContext context) throws Exception {
        this.context = context;

        trackDa = new ServiceTracker(context, DeploymentAdmin.class.getName(), null);
        trackDa.open();
        trackPa = new ServiceTracker(context, PermissionAdmin.class.getName(), null);
        trackPa.open();
        trackCondPa = new ServiceTracker(context, ConditionalPermissionAdmin.class.getName(), null);
        trackCondPa.open();
        trackTestsRunner = new ServiceTracker(context, TestRunner.class.getName(), null);
        trackTestsRunner.open();
        trackDb = new ServiceTracker(context, Db.class.getName(), null);
        trackDb.open();
        trackRp = new ServiceTracker(context, ResourceProcessor.class.getName(), null);
        trackRp.open();
        
        context.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + 
                TestRunner.class.getName() + ")");
        
        setPermissions();
        
        desktop = new TestDesktop(this);
    }

    public void stop(BundleContext context) throws Exception {
        trackDa.close();
        trackPa.close();
        trackCondPa.close();
        trackTestsRunner.close();
        trackDb.close();
        trackRp.close();
        
        desktop.setVisible(false);
        desktop.dispose();
        
        this.context = null;
    }

    public String[] getTestIds() {
        TestRunner tr = (TestRunner) trackTestsRunner.getService();
        return tr.getTestIds();
    }

    public String getDescription(String testId) {
        TestRunner tr = (TestRunner) trackTestsRunner.getService();
        TestCaseClass tc = tr.getTest(testId);
        return tc.getDescription();
    }

    public String[] getAsserts(String testId) {
        TestRunner tr = (TestRunner) trackTestsRunner.getService();
        TestCaseClass tc = tr.getTest(testId);
        return tc.getAsserts();
    }

    public void runTest(String testId) throws Exception {
        TestRunner tr = (TestRunner) trackTestsRunner.getService();
        TestCaseClass tc = tr.getTest(testId);
        ConditionalPermissionAdmin cpa = getConditionalPermissionAdmin();
        getDb().reset(null);
        
        ConditionalPermissionInfo cpi = cpa.addConditionalPermissionInfo(
                new ConditionInfo[] {
                    new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {
                        trackTestsRunner.getServiceReference().getBundle().getLocation()
                    })
                },
                tc.getNeededPermissions()
        );

        try {
            tc.doTest((Db) trackDb.getService(), (DeploymentAdmin) trackDa.getService());
        } finally {
            cpi.delete();
        }
    }

    private ServiceReference getDbRef() {
        ServiceReference ret = trackDb.getServiceReference();
        if (null == ret)
            throw new RuntimeException("There is no Db");
        return ret;
    }

    private Db getDb() {
        Db ret = (Db) trackDb.getService();
        return ret;
    }

    private ServiceReference getDeploymentAdminRef() {
        ServiceReference ret = trackDa.getServiceReference();
        if (null == ret)
            throw new RuntimeException("There is no DeploymentAdmin");
        return ret;
    }

    private DeploymentAdmin getDeploymentAdmin() {
        DeploymentAdmin ret = (DeploymentAdmin) trackDa.getService();
        if (null == ret)
            throw new RuntimeException("There is no DeploymentAdmin");
        return ret;
    }
    
    private PermissionAdmin getPermissionAdmin() {
        PermissionAdmin ret = (PermissionAdmin) trackPa.getService();
        return ret;
    }
    

    
    private ConditionalPermissionAdmin getConditionalPermissionAdmin() {
        ConditionalPermissionAdmin ret = (ConditionalPermissionAdmin) trackCondPa.getService();
        return ret;
    }

    private void setPermissions() throws Exception {
        PermissionAdmin pa = getPermissionAdmin();
        ConditionalPermissionAdmin cpa = getConditionalPermissionAdmin();
        ServiceReference ref;
        
        if (null == pa || null == cpa)
            throw new RuntimeException("Thereis no PermAdmin or CondPermAdmin");
        
        cpa.setConditionalPermissionInfo("cpi_DoIt",
                new ConditionInfo[] {new ConditionInfo(
                        BundleLocationCondition.class.getName(),
                        new String[] {context.getBundle().getLocation()})},
                new PermissionInfo[] {new PermissionInfo(AllPermission.class
                        .getName(), "*", "*")});

        ref = getDeploymentAdminRef();
        cpa.setConditionalPermissionInfo("cpi_Deployment_Admin",
                new ConditionInfo[] {new ConditionInfo(
                        BundleLocationCondition.class.getName(),
                        new String[] {ref.getBundle().getLocation()})},
                new PermissionInfo[] {new PermissionInfo(AllPermission.class
                        .getName(), "*", "*")});

        ref = getDbRef();
        cpa.setConditionalPermissionInfo("cpi_Database",
                new ConditionInfo[] {new ConditionInfo(
                        BundleLocationCondition.class.getName(),
                        new String[] {ref.getBundle().getLocation()})},
                new PermissionInfo[] {
                        new PermissionInfo(PackagePermission.class.getName(),
                                "*", "export, import"),
                        new PermissionInfo(ServicePermission.class.getName(),
                                "*", "register")});

        ServiceReference[] refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(type=db)");
        for (int i = 0; i < refs.length; i++) {
            cpa.setConditionalPermissionInfo("cpi_Preinstalled_RP_"
                    + refs[i].getBundle().getLocation(),
                    new ConditionInfo[] {new ConditionInfo(
                            BundleLocationCondition.class.getName(),
                            new String[] {refs[i].getBundle().getLocation()})},
                    new PermissionInfo[] {
                            new PermissionInfo(PackagePermission.class
                                    .getName(), "*", "export, import"),
                            new PermissionInfo(AdminPermission.class.getName(),
                                    "*", "metadata"),
                            new PermissionInfo(ServicePermission.class
                                    .getName(), "*", "get"),
                            new PermissionInfo(ServicePermission.class
                                    .getName(), "*", "register")});
        }

        // ### minor hack
        cpa.setConditionalPermissionInfo(
                        "cpi_Customizer_1",
                        new ConditionInfo[] {new ConditionInfo(
                                BundleLocationCondition.class.getName(),
                                new String[] {"osgi-dp:com.nokia.test.exampleresourceprocessor.db.DbResourceProcessor_db_test_03"})},
                        new PermissionInfo[] {
                                new PermissionInfo(PackagePermission.class
                                        .getName(), "*", "export, import"),
                                new PermissionInfo(AdminPermission.class
                                        .getName(), "*", "metadata"),
                                new PermissionInfo(ServicePermission.class
                                        .getName(), "*", "get"),
                                new PermissionInfo(ServicePermission.class
                                        .getName(), "*", "register")});
    }

    void command() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.print("command: ");
        String line = in.readLine();
        
        if ("ins".equalsIgnoreCase(line)) {
            System.out.print("dp: ");
            line = in.readLine();
			FileInputStream is = new FileInputStream(HOME + line);
			try {
			    getDeploymentAdmin().installDeploymentPackage(is);                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
			is.close();
        } else if ("dps".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
        } else if ("dpbundles".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            line = in.readLine();
            String[][] bs = dps[Integer.parseInt(line)].getBundleSymNameVersionPairs();
            for (int i = 0; i < bs.length; i++)
                System.out.println(" " + bs[i][0] + " " + bs[i][1]);
        } else if ("geth".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            String dp = in.readLine();
            System.out.print(" header: ");
            String header = in.readLine();
            String hval = dps[Integer.parseInt(dp)].getHeader(header);
            System.out.println(" " + hval);
        } else if ("getrh".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            String dp = in.readLine();
            System.out.print(" resource: ");
            String res = in.readLine();
            System.out.print(" header: ");
            String header = in.readLine();
            String hval = dps[Integer.parseInt(dp)].getResourceHeader(res, header);
            System.out.println(" " + hval);
        } else if ("getrp".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            String dp = in.readLine();
            System.out.print(" resource: ");
            String res = in.readLine();
            ServiceReference rp = dps[Integer.parseInt(dp)].getResourceProcessor(res);
            System.out.println(" " + rp);
        } else if ("res".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            String dp = in.readLine();
            String [] sa = dps[Integer.parseInt(dp)].getResources();
            System.out.println(Arrays.asList(sa));
        } else if ("uni".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            line = in.readLine();
            if ("all".equalsIgnoreCase(line)) {
                for (int i = 0; i < dps.length; i++) {
                    DeploymentPackage dp = dps[i];
                    dp.uninstall();
                }
            } else {
                DeploymentPackage dp = dps[Integer.parseInt(line)];
                dp.uninstall();
            }
        } else if ("unif".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            line = in.readLine();
            if ("all".equalsIgnoreCase(line)) {
                for (int i = 0; i < dps.length; i++) {
                    DeploymentPackage dp = dps[i];
                    dp.uninstallForced();
                }
            } else {
                DeploymentPackage dp = dps[Integer.parseInt(line)];
                dp.uninstallForced();
            }
        } else if ("tables".equalsIgnoreCase(line)) {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            String[] tables = db.tableNames(null);
            for (int i = 0; i < tables.length; i++)
                System.out.println(" " + tables[i]);
            context.ungetService(ref);
        } 
    }

    public void bad_db_test_01() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_01.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    public void bad_db_test_02() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_02.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    public void bad_db_test_03() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_03.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    /*public void bad_db_test_04() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_04.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }*/
    
    public void bad_db_test_05() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_05.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    public void bad_db_test_07() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_07.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            if (DeploymentException.CODE_ORDER_ERROR == e.getCode())
                return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    public void bad_db_test_08() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_08.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            if (DeploymentException.CODE_MISSING_HEADER == e.getCode())
                return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }
    
    public void bad_db_test_09() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_09.dp");
        try {
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        } catch (DeploymentException e) {
            if (DeploymentException.CODE_SIGNING_ERROR == e.getCode())
                return;
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
        }
        throw new Exception("Negative test failed");
    }

    public static final String bad_db_test_10 = "Installing a Deployment Package with " +
    		"name version identical to the existing Deployment Package will not " +
    		"perform any actions.";
    public void bad_db_test_10() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_10.dp");
        DeploymentPackage dp1 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        is = new FileInputStream(HOME + "bad_db_test_10.dp");
        DeploymentPackage dp2 = getDeploymentAdmin().installDeploymentPackage(is);
        
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        db.reset(null);
        
        dp1.uninstall();
    }

    public static final String bad_db_test_11 = "Tests bundle sharing violation";
    public void bad_db_test_11() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_11_1.dp");
        DeploymentPackage dp1 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        is = new FileInputStream(HOME + "bad_db_test_11_2.dp");
        try {
            DeploymentPackage dp2 = getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Negative test failed");
        } catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_BUNDLE_SHARING_VIOLATION)
                throw new Exception("Negative test failed");
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
            
            dp1.uninstall();
        }
    }

    public static final String bad_db_test_12 = "Tests resource sharing violation";
    public void bad_db_test_12() throws Exception {
        InputStream is = new FileInputStream(HOME + "bad_db_test_12_1.dp");
        DeploymentPackage dp1 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        is = new FileInputStream(HOME + "bad_db_test_12_2.dp");
        try {
            getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Negative test failed");
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_RESOURCE_SHARING_VIOLATION)
                throw new Exception("Negative test failed");
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
            
            dp1.uninstall();
        }
    }

    public static final String bad_db_test_13 = "deployment package installs a bundle, " +
			"which symbolic name is not the same as defined by the deployment package " +
			"manifest";
    public void bad_db_test_13() throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(HOME + "bad_db_test_13.dp");
            dp = getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Negative test failed");
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_BUNDLE_NAME_ERROR)
                throw new Exception("Negative test failed");
        } finally {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);
            
            is.close();
        }
    }
    
    public static final String bad_db_test_14 = "Fix-Pack manifest says that 'bad_db_test_14.dbscript' " +
    		"resource is missing but it is not present in the old (target) DP.";
    public void bad_db_test_14() throws Exception {
        InputStream is = null;
        DeploymentPackage dp1 = null;
        DeploymentPackage dp2 = null;
        try {
            is = new FileInputStream(HOME + "bad_db_test_14_1.dp");
            dp1 = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
            is = new FileInputStream(HOME + "bad_db_test_14_2.dp");
            dp2 = getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Negative test failed");
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_MISSING_RESOURCE)
                throw new Exception("Negative test failed");
        }
        finally {
            ServiceReference ref = context.getServiceReference(Db.class
                    .getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);

            is.close();
            dp1.uninstall();
        }
    }

    public static final String bad_db_test_15 = "Fix-Pack manifest says that 'easygame' bundle " +
    		"is missing but it is not present in the old (target) DP.";
    public void bad_db_test_15() throws Exception {
        InputStream is = null;
        DeploymentPackage dp1 = null;
        DeploymentPackage dp2 = null;
        try {
            is = new FileInputStream(HOME + "bad_db_test_15_1.dp");
            dp1 = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
            is = new FileInputStream(HOME + "bad_db_test_15_2.dp");
            dp2 = getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Negative test failed");
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_MISSING_BUNDLE)
                throw new Exception("Negative test failed");
        }
        finally {
            ServiceReference ref = context.getServiceReference(Db.class
                    .getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);

            is.close();
            dp1.uninstall();
        }
    }

    public static final String bad_db_test_16 = "Bundle name section contains illegal character";
    public void bad_db_test_16() throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(HOME + "bad_db_test_16.dp");
            dp = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_BAD_HEADER)
                throw new Exception("Negative test failed");
        }
        finally {
            ServiceReference ref = context.getServiceReference(Db.class
                    .getName());
            Db db = (Db) context.getService(ref);
            db.reset(null);

            is.close();
        }
    }

    public void bad_db_test_17() throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        
        is = new FileInputStream(HOME + "bad_db_test_17_1.dp");
        dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        try {
            is = new FileInputStream(HOME + "bad_db_test_17_2.dp");
            dp = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_MISSING_RESOURCE)
                throw new Exception("Negative test failed");
        }
        
        dp.uninstall();
    }

    public void bad_db_test_18() throws Exception {
        InputStream is = null;
        DeploymentPackage dp1 = null;
        DeploymentPackage dp2 = null;
        
        is = new FileInputStream(HOME + "bad_db_test_18_1.dp");
        dp1 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        try {
            is = new FileInputStream(HOME + "bad_db_test_18_2.dp");
            dp2 = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_MISSING_BUNDLE)
                throw new Exception("Negative test failed");
        }
        
        dp1.uninstall();
    }

    public static final String bad_db_test_19 = "MANIFEST IS NOT THE FIRST\n";
    public void bad_db_test_19() throws Exception {
        InputStream is = null;
        
        try {
            is = new FileInputStream(HOME + "bad_db_test_19.dp");
            DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
            is.close();
        }
        catch (DeploymentException e) {
            if (e.getCode() != DeploymentException.CODE_ORDER_ERROR)
                throw new Exception("Negative test failed");
        }
    }

    public static final String db_test_01 = "COMPOUND\n" +
        "Uses default RP, two resource files (one of them \n" +
    	"is updated the other removed)\n" +
        "ASSERTS\n" +
        " - tables exist and certains disappear after update\n" +
        " - rows in tables exist\n" +
        " - DP version changes\n" +
        " - main header changes\n" +
        " - resource header changes";
    public void db_test_01() throws Exception {
        Db db = null;
        
        try {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            db = (Db) context.getService(ref);
            
            InputStream is = new FileInputStream(HOME + "db_test_01.dp");
    		DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
            
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is missing");
            if (null == db.findRow(null, "player", new Integer(1)))
                throw new TestCaseException("Row with '1' primary key is missing");
            if (null == db.findRow(null, "game", new Integer(1)))
                throw new TestCaseException("Row with '1' primary key is missing");
            if (null == db.findRow(null, "score", new Integer(1)))
                throw new TestCaseException("Row with '1' primary key is missing");
            if (null == db.findRow(null, "tmp", new Integer(1)))
                throw new TestCaseException("Row with '1' primary key is missing");
            if (!dp.getVersion().equals(new Version("1.0")))
                throw new TestCaseException("Version should be 1.0");
            if (!dp.getHeader("Other-Main-header").equals("1"))
                throw new TestCaseException("Header value (Other-Main-header) should be 1");
            if (!dp.getResourceHeader("db_test_01.dbscript", "Other-header").equals("1"))
                throw new TestCaseException("Header value (Other-header) should be 1");
    
            is = new FileInputStream(HOME + "db_test_01_update_01.dp");
    		dp = getDeploymentAdmin().installDeploymentPackage(is);
    		
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is present");
            if (-1 != Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is present");
            if (!db.findRow(null, "player", new Integer(1))[1].equals("Joe_Upd"))
                throw new TestCaseException("Row with '1' primary key is not updated");
            if (!db.findRow(null, "game", new Integer(1))[1].equals("chess_Upd"))
                throw new TestCaseException("Row with '1' primary key is not updated");
            if (!dp.getVersion().equals(new Version("2.0")))
                throw new TestCaseException("Version should be 2.0");
            if (!dp.getHeader("Other-Main-header").equals("2"))
                throw new TestCaseException("Header value (Other-Main-header) should be 2");
            if (!dp.getResourceHeader("db_test_01.dbscript", "Other-header").equals("2"))
                throw new TestCaseException("Header value (Other-header) shoul dbe 2");
            
            dp.uninstall();
        } finally {
            if (null != db)
                db.reset(null);    
        }
        
    }

    public static final String db_test_06 = "PRIVATE AREA\n";
    public void db_test_06() throws Exception {
        DeploymentPackage dp = null;
        InputStream is = new FileInputStream(HOME + "db_test_06.dp");
		dp = getDeploymentAdmin().installDeploymentPackage(is);
		
        ServiceReference[] refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(" + Constants.SERVICE_PID + "=db_test_06)");
        ResourceProcessor rp = (ResourceProcessor) context.getService(refs[0]);
        
        File f = ((DbRpTest) rp).getBundlePrivateArea();
        if (null == f)
            throw new Exception("Private area error: null returned");
        if (!f.exists())
            throw new Exception("Private area error: does not exist");
        File[] fs = f.listFiles();
        System.out.println(Arrays.asList(fs));
        
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        db.reset(null);
        dp.uninstall();
    }
    
    public static final String db_test_07 = "LIST DEPLOYMENT PACKAGES\n" +
        "Shows only those DPs it has permissions to." +
        "ASSERTS\n" +
        " - 'System' DP should NOT be visble\n" +
        " - 'db_test_01' DP should be visble";
    public void db_test_07() throws Exception {
        if (null == System.getSecurityManager())
            return;
        
        InputStream is = new FileInputStream(HOME + "db_test_01.dp");
        DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        
		DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
		boolean b1 = false;
		boolean b2 = false;
		for (int i = 0; i < dps.length; i++) {
            if (dps[i].getName().equals("db_test_01"))
                b1 = true;
            if (dps[i].getName().equalsIgnoreCase("system"))
                b2 = true;
        }
		if (!b1)
            throw new Exception("'db_test_01' DP should be visble");
		if (b2)
            throw new Exception("'System' DP should NOT be visble");
		
		dp.uninstall();
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        db.reset(null);
    }
    
    public static final String db_test_09 = "GET BUNDLE SYMBOLIC NAME VERSION PAIRS\n" +
        "Updates bundles and version has to be changed in \n" +
    	"getBundleSymNameVersionPairs() result\n" +
        "ASSERTS\n" +
        " - bundle version changes from 1.0.0 to 1.5.0";
    public void db_test_09() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_09.dp");
        DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        dp = getDeploymentAdmin().getDeploymentPackage("db_test_09");
		String[][] snvps = dp.getBundleSymNameVersionPairs();
		boolean b1 = snvps[0][1].equals("1.0.0");

        is = new FileInputStream(HOME + "db_test_09_update_01.dp");
        dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        dp = getDeploymentAdmin().getDeploymentPackage("db_test_09");
		snvps = dp.getBundleSymNameVersionPairs();
		boolean b2 = snvps[0][1].equals("1.5.0");
		
		if (!(b1 && b2))
		    throw new Exception("Test failed");

		dp.uninstall();
    }

    public static final String db_test_10 = "INSTALL RESOURCE THAT HAS NO PID";
    public void db_test_10() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_10.dp");
        DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        is = new FileInputStream(HOME + "db_test_10_update_01.dp");
        dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        dp.uninstall();
    }

    public static final String db_test_11 = "NEW BUNDLE DURING UPDATE\n" +
            "Update adds a new bundle\n" +
            " - new element in getBundleSymNameVersionPairs() result";
    public void db_test_11() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_11.dp");
        DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        String[][] bsnvps = dp.getBundleSymNameVersionPairs();
        if (bsnvps.length != 2)
            throw new Exception("Test failed");

        is = new FileInputStream(HOME + "db_test_11_update_01.dp");
        dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        bsnvps = dp.getBundleSymNameVersionPairs();
        if (bsnvps.length != 3)
            throw new Exception("Test failed");

        dp.uninstall();
    }

    public static final String db_test_12 = "\n" +
        "DeploymentPackage-Missing: false EQUALS WITH the lack of the header";
    public void db_test_12() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_12.dp");
        DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        is = new FileInputStream(HOME + "db_test_12_update_01.dp");
        dp = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        dp.uninstall();
    }

    public static final String db_test_14 = "When a client requests a new session \n" +
    		"with an install or uninstall operation, it must block that call until \n" +
    		"the earlier session is completed. The Deployment Admin service must \n" +
    		"throw a DeploymentException when the session can not be created after \n" +
    		"an appropriate time out period.";
    public void db_test_14() throws Exception {
        final DeploymentException[] ex = new DeploymentException[2];
        
        // INSTALL
        
        // creates a thread that calls install
        Thread installThread = new Thread(new Runnable() {
            public void run() {
                try {
                    try {Thread.sleep(1000);} catch (Exception e) {}
                    InputStream is = new FileInputStream(HOME + "db_test_14_02.dp");
                    DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
                    is.close();
                } catch (DeploymentException e) {
                    ex[0] = e;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        installThread.start();

        InputStream is = new FileInputStream(HOME + "db_test_14_01.dp");
        final DeploymentPackage dp1 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();

        if (null == ex[0] || ex[0].getCode() != DeploymentException.CODE_TIMEOUT)
            throw new Exception("Test failed");
        
        // UNINSTALL
        
        // creates a thread that calls uninstall
        Thread uninstallThread = new Thread(new Runnable() {
            public void run() {
                try {
                    try {Thread.sleep(1000);} catch (Exception e) {}
                    dp1.uninstall();
                } catch (DeploymentException e) {
                    ex[1] = e;
                }
            }
        });
        uninstallThread.start();
        
        is = new FileInputStream(HOME + "db_test_14_02.dp");
        DeploymentPackage dp2 = getDeploymentAdmin().installDeploymentPackage(is);
        is.close();
        
        if (null == ex[1] || ex[1].getCode() != DeploymentException.CODE_TIMEOUT)
            throw new Exception("Test failed");
        
        dp1.uninstall();
        dp2.uninstall();
    }

    public static final String db_test_15 = "Tests localization";
    public void db_test_15() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_15.dp");
		DeploymentPackage dp = getDeploymentAdmin().installDeploymentPackage(is);
		
		String s;
		s = dp.getHeader("MHColor");	
		if (!"COLOR".equals(s))
		    throw new Exception("Negative test failed");
		
		s = dp.getResourceHeader("db_test_15.dbscript", "RHR");
		if (!"RED".equals(s))
		    throw new Exception("Negative test failed");

		s = dp.getResourceHeader("db_test_15.dbscript", "RHW");
		if (!"WHITE".equals(s))
		    throw new Exception("Negative test failed");

		s = dp.getResourceHeader("db_test_15.dbscript", "RHG");
		if (!"GREEN".equals(s))
		    throw new Exception("Negative test failed");

		dp.uninstall();
    }
    
    public static final String db_test_16 = "BUNDLE START FAILS\n";
    public void db_test_16() throws Exception {
        InputStream is = new FileInputStream(HOME + "db_test_16.dp");
        DeploymentPackage dp = null;
        try {
            dp = getDeploymentAdmin().installDeploymentPackage(is);
            throw new Exception("Test failed");
        } catch (DeploymentException e) {
            // DeploymentException.CODE_BUNDLE_START means that Dp is installed
            // but some bundles didn't start
            dp = getDeploymentAdmin().getDeploymentPackage("db_test_16");
            dp.uninstall();
            
            if (e.getCode() != DeploymentException.CODE_BUNDLE_START)
                throw new Exception("Test failed");
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////

    public void serviceChanged(ServiceEvent event) {
        ((TestRunner) trackTestsRunner.getService()).setDoIt(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    
    public File getFile(final String file) {
        try { 
            return (File) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    return new File(new File(HOME).getCanonicalFile(), file);
                }
            });
        }
        catch (PrivilegedActionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResourceProcessor getRp(String aPid) {
        ServiceReference[] refs = trackRp.getServiceReferences();
        for (int i = 0; i < refs.length; i++) {
            String pid = (String) refs[i].getProperty(Constants.SERVICE_PID);
            if (pid.equals(aPid))
                return (ResourceProcessor) trackRp.getService(refs[i]);
        }
        return null;
    }

    public Bundle[] getBundles() {
        return context.getBundles();
    }

}
