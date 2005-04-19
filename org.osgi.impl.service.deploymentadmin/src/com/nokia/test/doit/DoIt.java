package com.nokia.test.doit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AllPermission;
import java.util.Arrays;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.DeploymentAdminImpl;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.exampleresourceprocessor.db.DbResourceProcessor;

public class DoIt implements BundleActivator {
    
    private static final String HOME = "../../org.osgi.impl.service.deploymentadmin/res/";
    
    private DeploymentAdmin da;
    private BundleContext   context;
    
    private void setPermissions() throws InvalidSyntaxException {
        ServiceReference paRef = context.getServiceReference(PermissionAdmin.class.getName());
        PermissionAdmin pa = (PermissionAdmin) context.getService(paRef);
        
        ServiceReference ref;
        ref = context.getServiceReference(DeploymentAdmin.class.getName());
        String daLoc = ref.getBundle().getLocation();
        pa.setPermissions(daLoc, new PermissionInfo[] {
                new PermissionInfo(DeploymentAdminPermission.class.getName(), "name:* signer:*", "installDeploymentPackage, uninstallDeploymentPackage"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), "", "listDeploymentPackages, inventory"),
                new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
                new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
                new PermissionInfo(AdminPermission.class.getName(), "", "")
                //new PermissionInfo(AllPermission.class.getName(), "", "") // TODO
        	});
        
        ref = context.getServiceReference(Db.class.getName());
        String dbLoc = ref.getBundle().getLocation();
        pa.setPermissions(dbLoc, new PermissionInfo[] {
                new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                new PermissionInfo(ServicePermission.class.getName(), "*", "REGISTER")
            });
        
        ServiceReference[] refs = context.getServiceReferences(ResourceProcessor.class.getName(),
                "(type=db)");
        for (int i = 0; i < refs.length; i++) {
            String loc = refs[i].getBundle().getLocation();
            pa.setPermissions(loc, new PermissionInfo[] {
                    new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                    // to allow RP to read its id from its manifest
                    new PermissionInfo(AdminPermission.class.getName(), "", ""),
                    new PermissionInfo(AllPermission.class.getName(), "", "") // TODO
                });
        }

        ref = context.getServiceReference(Db.class.getName());
        String doitLoc = context.getBundle().getLocation();
        pa.setPermissions(doitLoc, new PermissionInfo[] {
                new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                // to find the Deployment Admin
                new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
                // to load files that are passed to the Deployment Admin
                new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
                // to install deployment packages 
                new PermissionInfo(DeploymentAdminPermission.class.getName(), "name:*", "installDeploymentPackage"),
                //new PermissionInfo(DeploymentAdminPermission.class.getName(), "name:db_test_01", "installDeploymentPackage"),
                //new PermissionInfo(DeploymentAdminPermission.class.getName(), "name:db_test_02", "installDeploymentPackage"),
                //new PermissionInfo(DeploymentAdminPermission.class.getName(), "name:db_test_03", "installDeploymentPackage"),
                
                // to be able to set permissions during next run
                // and because "In addition to DeploymentAdminPermission, the caller 
                // of Deployment Admin must in addition hold the appropriate AdminPermissions."
                new PermissionInfo(AdminPermission.class.getName(), "", "") 
            });
    }

    public void stop(BundleContext context) throws Exception {
    }

    public void start(BundleContext context) throws Exception {
        this.context = context;
        //setPermissions();
        
        ServiceReference refDa = context.getServiceReference(DeploymentAdmin.class.getName());
		da = (DeploymentAdmin) context.getService(refDa);
		
        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.print("command: ");
        String line = in.readLine();
        
        if ("ins".equalsIgnoreCase(line)) {
            System.out.print("dp: ");
            line = in.readLine();
			FileInputStream is = new FileInputStream(HOME + line);
			da.installDeploymentPackage(is);
			is.close();
        } else if ("dps".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = da.listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
        } else if ("uni".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = da.listDeploymentPackages();
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
        } else if ("tables".equalsIgnoreCase(line)) {
            ServiceReference ref = context.getServiceReference(Db.class.getName());
            Db db = (Db) context.getService(ref);
            String[] tables = db.tableNames(null);
            for (int i = 0; i < tables.length; i++)
                System.out.println(" " + tables[i]);
            context.ungetService(refDa);
        } else if ("tdb".equalsIgnoreCase(line)) {
            int ok = 0;
            int error = 0;
            
            try {db_test_01(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            try {db_test_02(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            try {db_test_03(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            try {db_test_04(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            try {db_test_05(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            try {db_test_06(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            
            System.out.println("\n=====================================");
            System.out.println("RESULT: OK = " + ok + " ERROR = " + error);
            System.out.println("=====================================");
        }
        
    }

    private void db_test_01() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        
        InputStream is = new FileInputStream(HOME + "db_test_01.dp");
		DeploymentPackage dp = da.installDeploymentPackage(is);
		
		String[] tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        if (null == db.findRow(null, "player", new Integer(1)))
            throw new Exception("Row with '1' primary key is missing");
        if (null == db.findRow(null, "game", new Integer(1)))
            throw new Exception("Row with '1' primary key is missing");
        if (null == db.findRow(null, "score", new Integer(1)))
            throw new Exception("Row with '1' primary key is missing");
        if (null == db.findRow(null, "tmp", new Integer(1)))
            throw new Exception("Row with '1' primary key is missing");

        is = new FileInputStream(HOME + "db_test_01_update_01.dp");
		dp = da.installDeploymentPackage(is);
		
		tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is present");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is present");
        if (!((Object[]) db.findRow(null, "player", new Integer(1)))[1].equals("Joe_Upd"))
            throw new Exception("Row with '1' primary key is not updated");
        if (!((Object[]) db.findRow(null, "game", new Integer(1)))[1].equals("chess_Upd"))
            throw new Exception("Row with '1' primary key is not updated");
        
        dp.uninstall();
        db.reset(null);
    }

    private void db_test_02() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        
        InputStream is = new FileInputStream(HOME + "db_test_02.dp");
        DeploymentPackage dp = da.installDeploymentPackage(is);
		
		String[] tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
      
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");

        is = new FileInputStream(HOME + "db_test_02_update_01.dp");
		dp = da.installDeploymentPackage(is);
		
		tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        
        dp.uninstall();
        db.reset(null);
    }
    
    private void db_test_03() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        DeploymentPackage dp = null;
        
        InputStream is = new FileInputStream(HOME + "db_test_03.dp");
		dp = da.installDeploymentPackage(is);
		
		String[] tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        
        ServiceReference[] refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(" + Constants.SERVICE_PID + "=db_test_03)");
        ResourceProcessor rp = (ResourceProcessor) context.getService(refs[0]);
        Set s = ((DbResourceProcessor) rp).getResources(dp, "db_test_01_t.dbscript");
        if (null == s || !s.contains("tmp"))
            throw new Exception("RP with id 'db_test_03' HASN'T receive the " +
            		"'db_test_01_t.dbscript' resource");
        refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(" + Constants.SERVICE_PID + "=default_pid)");
        rp = (ResourceProcessor) context.getService(refs[0]);
        s = ((DbResourceProcessor) rp).getResources(dp, "db_test_01_t.dbscript");
        if (null != s && s.contains("tmp"))
            throw new Exception("RP with id 'default_id' HAS receive the " +
            		"'db_test_01_t.dbscript' resource");
        
        is = new FileInputStream(HOME + "db_test_03_update_01.dp");
		dp = da.installDeploymentPackage(is);
        
		dp.uninstall();
        db.reset(null);
    }

    private void db_test_04() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        DeploymentPackage dp = null;
        
        // creates a thread that calls the cancel() methods
        Thread cancelThread = new Thread(new Runnable() {
            public void run() {
        		try {Thread.sleep(3000);} catch (Exception e) {}
        	    da.cancel();                
            }
        });
        cancelThread.start();
        
        InputStream is = new FileInputStream(HOME + "db_test_04.dp");
		dp = da.installDeploymentPackage(is);
		
		if (null != dp)
		    throw new Exception("Operation has not been cancelled");
		
		DeploymentPackage[] dps = da.listDeploymentPackages();
		for (int i = 0; i < dps.length; i++) {
            if (dps[i].getName().equals("db_test_04"))
                throw new Exception("Operation has not been cancelled");
        }
		
        db.reset(null);
    }
    
    private void db_test_05() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        DeploymentPackage dp = null;
        
        InputStream is = new FileInputStream(HOME + "db_test_05.dp");
		dp = da.installDeploymentPackage(is);
		
		String[] tables = db.tableNames(null);
		for (int i = 0; i < tables.length; i++) {
		    System.out.println("TABLE: " + tables[i]);
		    db.printTableHeader(null, tables[i], System.out);
			db.printTableContent(null, tables[i], System.out);
			System.out.println();                
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp2"))
            throw new Exception("Table 'tmp2' is missing");
        
        dp.uninstall();
        db.reset(null);
    }
    
    private void db_test_06() throws Exception {
        DeploymentPackage dp = null;
        InputStream is = new FileInputStream(HOME + "db_test_06.dp");
		dp = da.installDeploymentPackage(is);
		
        ServiceReference[] refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(" + Constants.SERVICE_PID + "=default_pid)");
        ResourceProcessor rp = (ResourceProcessor) context.getService(refs[0]);
        
        File f = ((DbResourceProcessor) rp).getBundlePrivateArea();
        if (null == f)
            throw new Exception("Private area error: null returned");
        if (!f.exists())
            throw new Exception("Private area error: does not exist");
        File[] fs = f.listFiles();
        System.out.println(Arrays.asList(fs));
        
        dp.uninstall();
    }

    // FOR TEST ONLY
    /*public static void main(String[] args) throws IOException {
        FileInputStream is = null;
        try {
	        is = new FileInputStream("/eclipse/workspaceMEG/org.osgi.impl.service.deploymentadmin/res/db_test_02_update_01.dp");
	        JarInputStream jis = new JarInputStream(is);
	        
	        System.out.println(jis.getManifest().getEntries());
	        
	        JarEntry je = jis.getNextJarEntry();
	        jis.closeEntry();
	        je = jis.getNextJarEntry();
	        System.out.println(je);
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }*/
    
}
