package com.nokia.test.doit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
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
        	System.out.println("TODO");
//            DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
//            for (int i = 0; i < dps.length; i++)
//                System.out.println(" " + i + " " + dps[i]);
//            System.out.print(" which: ");
//            line = in.readLine();
//            String[][] bs = dps[Integer.parseInt(line)].getBundleSymNameVersionPairs();
//            for (int i = 0; i < bs.length; i++)
//                System.out.println(" " + bs[i][0] + " " + bs[i][1]);
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
