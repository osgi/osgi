package com.nokia.test.doit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

import com.nokia.test.db.Db;
import com.nokia.test.exampleresourceprocessor.db.DbResourceProcessor;

public class DoIt implements BundleActivator {
    
    private static final String HOME = "../../org.osgi.impl.service.deploymentadmin/res/";
    
    private DeploymentAdmin da;
    private BundleContext   context;

    public void stop(BundleContext context) throws Exception {
    }

    public void start(BundleContext context) throws Exception {
        this.context = context;
        ServiceReference ref = context.getServiceReference(DeploymentAdmin.class.getName());
		da = (DeploymentAdmin) context.getService(ref);
		
        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.print("command: ");
        String line = in.readLine();
        
        if ("ins".equalsIgnoreCase(line)) {
			FileInputStream is = new FileInputStream(HOME + "easygame.dp");
			da.installDeploymentPackage(is);
			is.close();
        } else if ("dps".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = da.listDeploymentPackages();
            System.out.println(Arrays.asList(dps));
        } else if ("uni".equalsIgnoreCase(line)) {
            DeploymentPackage[] dps = da.listDeploymentPackages();
            for (int i = 0; i < dps.length; i++)
                System.out.println(" " + i + " " + dps[i]);
            System.out.print(" which: ");
            line = in.readLine();
            DeploymentPackage dp = dps[Integer.parseInt(line)];
            dp.uninstall();
        } else if ("upd".equalsIgnoreCase(line)) {
            FileInputStream is = new FileInputStream(HOME + "easygame_update.dp");
			da.installDeploymentPackage(is);
			is.close();
        } else if ("com1".equalsIgnoreCase(line)) {
            FileInputStream is = new FileInputStream(HOME + "easygame.dp");
			da.installDeploymentPackage(is);
			is.close();
			
            is = new FileInputStream(HOME + "easygame_update.dp");
			da.installDeploymentPackage(is);
			is.close();
        } else if ("com2".equalsIgnoreCase(line)) {
            FileInputStream is = new FileInputStream(HOME + "easygame.dp");
			da.installDeploymentPackage(is);
			is.close();
			
            is = new FileInputStream(HOME + "easygame_update_nr.dp");
			da.installDeploymentPackage(is);
			is.close();
        } else if ("test".equalsIgnoreCase(line)) {
            boolean ret;
            int ok = 0;
            int error = 0;
            
            if (ret = test_simple_01_01()) ++ok; else ++error;
            System.out.println("** test_simple_01_01 *********************** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_simple_01_02()) ++ok; else ++error;
            System.out.println("** test_simple_01_02 *********************** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_simple_01_03()) ++ok; else ++error;
            System.out.println("** test_simple_01_03 *********************** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_simple_01_04()) ++ok; else ++error;
            System.out.println("** test_simple_01_04 *********************** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_customizer_01_01()) ++ok; else ++error;
            System.out.println("** test_customizer_01_01 ******************* " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_drop_bundles_01()) ++ok; else ++error;
            System.out.println("** test_drop_bundles_01 ******************** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_drop_bundles_and_resources_01()) ++ok; else ++error;
            System.out.println("** test_drop_bundles_and_resources_01 ****** " + (ret ? "ok" : "ERROR"));
            
            if (ret = test_bundlefilter_01()) ++ok; else ++error;
            System.out.println("** test_bundlefilter_01 ******************** " + (ret ? "ok" : "ERROR"));
            
            System.out.println("\n================================");
            System.out.println("RESULT: OK = " + ok + " ERROR = " + error);
            System.out.println("================================");
        } else if ("tdb".equalsIgnoreCase(line)) {
            int ok = 0;
            int error = 0;
            
            //try {db_test_01(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            //System.out.println("*******************************************************************");
            //try {db_test_02(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            //System.out.println("*******************************************************************");
            try {db_test_03(); ++ok;} catch (Exception e) {e.printStackTrace(); ++error;}
            System.out.println("*******************************************************************");
            
            System.out.println("\n=====================================");
            System.out.println("RESULT: OK = " + ok + " ERROR = " + error);
            System.out.println("=====================================");
        }
        
    }

    private void db_test_01() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        
        InputStream is = null;
        try {
	        is = new FileInputStream(HOME + "db_test_01.dp");
			da.installDeploymentPackage(is);
			
			String[] tables = db.tableNames(null);
			for (int i = 0; i < tables.length; i++) {
			    System.out.println("TABLE: " + tables[i]);
			    db.printTableHeader(null, tables[i], System.out);
				db.printTableContent(null, tables[i], System.out);
				System.out.println();                
            }
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
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

        try {
	        is = new FileInputStream(HOME + "db_test_01_update_01.dp");
			da.installDeploymentPackage(is);
			
			String[] tables = db.tableNames(null);
			for (int i = 0; i < tables.length; i++) {
			    System.out.println("TABLE: " + tables[i]);
			    db.printTableHeader(null, tables[i], System.out);
				db.printTableContent(null, tables[i], System.out);
				System.out.println();                
            }
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        if (!((Object[]) db.findRow(null, "player", new Integer(1)))[1].equals("Joe_Upd"))
            throw new Exception("Row with '1' primary key is not updated");
        if (!((Object[]) db.findRow(null, "game", new Integer(1)))[1].equals("chess_Upd"))
            throw new Exception("Row with '1' primary key is not updated");
        
        db.reset(null);
    }

    private void db_test_02() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        
        InputStream is = null;
        try {
	        is = new FileInputStream(HOME + "db_test_02.dp");
			da.installDeploymentPackage(is);
			
			String[] tables = db.tableNames(null);
			for (int i = 0; i < tables.length; i++) {
			    System.out.println("TABLE: " + tables[i]);
			    db.printTableHeader(null, tables[i], System.out);
				db.printTableContent(null, tables[i], System.out);
				System.out.println();                
            }
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");

        try {
	        is = new FileInputStream(HOME + "db_test_02_update_01.dp");
			da.installDeploymentPackage(is);
			
			String[] tables = db.tableNames(null);
			for (int i = 0; i < tables.length; i++) {
			    System.out.println("TABLE: " + tables[i]);
			    db.printTableHeader(null, tables[i], System.out);
				db.printTableContent(null, tables[i], System.out);
				System.out.println();                
            }
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        
        db.reset(null);
    }
    
    private void db_test_03() throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        Db db = (Db) context.getService(ref);
        DeploymentPackage dp = null;
        
        InputStream is = null;
        try {
	        is = new FileInputStream(HOME + "db_test_03.dp");
			dp = da.installDeploymentPackage(is);
			
			String[] tables = db.tableNames(null);
			for (int i = 0; i < tables.length; i++) {
			    System.out.println("TABLE: " + tables[i]);
			    db.printTableHeader(null, tables[i], System.out);
				db.printTableContent(null, tables[i], System.out);
				System.out.println();                
            }
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
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
                ResourceProcessor.class.getName(), "(id=db_test_03)");
        ResourceProcessor rp = (ResourceProcessor) context.getService(refs[0]);
        Set s = ((DbResourceProcessor) rp).getResources(dp, "db_test_01_t.dbscript");
        if (null == s || !s.contains("tmp"))
            throw new Exception("RP with id 'db_test_03' HASN'T receive the " +
            		"'db_test_01_t.dbscript' resource");
        refs = context.getServiceReferences(
                ResourceProcessor.class.getName(), "(id=default_id)");
        rp = (ResourceProcessor) context.getService(refs[0]);
        s = ((DbResourceProcessor) rp).getResources(dp, "db_test_01_t.dbscript");
        if (null != s && s.contains("tmp"))
            throw new Exception("RP with id 'default_id' HAS receive the " +
            		"'db_test_01_t.dbscript' resource");
        
        db.reset(null);
    }

    private boolean test_bundlefilter_01() {
        FileInputStream is = null;
        try {
            // install
            
	        is = new FileInputStream(HOME + "test_bundlefilter_01.dp");
			da.installDeploymentPackage(is);
			
			// cleanup
			
			DeploymentPackage[] dps = da.listDeploymentPackages();
			for (int i = 0; i < dps.length; i++) {
			    if (dps[i].getName().equals("test_bundlefilter_01")) {
			        dps[i].uninstall();
			        break;
			    }
            }
			
			return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    private boolean test_drop_bundles_and_resources_01() {
        FileInputStream is1 = null;
        FileInputStream is2 = null;
        try {
            // install
            
	        is1 = new FileInputStream(HOME + "test_drop_bundles_and_resources_01.dp");
			da.installDeploymentPackage(is1);
			
			Bundle[] bs = context.getBundles();
			StringBuffer found = new StringBuffer("");
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("example_rp_test_drop_bundles_01"))
                    found.append("01");
                if (b.getLocation().equals("example_rp_test_drop_bundles_02"))
                    found.append("02");
            }
			
			if ( !(found.toString().equals("0102") || found.toString().equals("0201")) )
			    return false;
			
			// update
			
			is2 = new FileInputStream(HOME + "test_drop_bundles_and_resources_02.dp");
			da.installDeploymentPackage(is2);
			
			bs = context.getBundles();
			found = new StringBuffer("");
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("example_rp_test_drop_bundles_01"))
                    found.append("01");
                if (b.getLocation().equals("example_rp_test_drop_bundles_02"))
                    found.append("02");
            }
			
			// cleanup
			
			DeploymentPackage[] dps = da.listDeploymentPackages();
			for (int i = 0; i < dps.length; i++) {
			    if (dps[i].getName().equals("test_drop_bundles_and_resources_01")) {
			        dps[i].uninstall();
			        break;
			    }
            }
			
			return found.toString().equals("01");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is1)
                try {
                    is1.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    private boolean test_drop_bundles_01() {
        FileInputStream is1 = null;
        FileInputStream is2 = null;
        try {
            // install
            
	        is1 = new FileInputStream(HOME + "test_drop_bundles_01.dp");
			da.installDeploymentPackage(is1);
			
			Bundle[] bs = context.getBundles();
			StringBuffer found = new StringBuffer("");
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("example_rp_test_drop_bundles_01"))
                    found.append("01");
                if (b.getLocation().equals("example_rp_test_drop_bundles_02"))
                    found.append("02");
            }
			
			if ( !(found.toString().equals("0102") || found.toString().equals("0201")) )
			    return false;
			
			// update
			
			is2 = new FileInputStream(HOME + "test_drop_bundles_02.dp");
			da.installDeploymentPackage(is2);
			
			bs = context.getBundles();
			found = new StringBuffer("");
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("example_rp_test_drop_bundles_01"))
                    found.append("01");
                if (b.getLocation().equals("example_rp_test_drop_bundles_02"))
                    found.append("02");
                if (b.getLocation().equals("example_rp_test_drop_bundles_03"))
                    found.append("03");
            }
			
			// cleanup
			
			DeploymentPackage[] dps = da.listDeploymentPackages();
			for (int i = 0; i < dps.length; i++) {
			    if (dps[i].getName().equals("test_drop_bundles_01")) {
			        dps[i].uninstall();
			        break;
			    }
            }
			
			return found.toString().equals("0103") || found.toString().equals("0301");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is1)
                try {
                    is1.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    private boolean test_simple_01_01() {
        FileInputStream is = null;
        try {
	        is = new FileInputStream(HOME + "test_simple_01_01.dp");
			da.installDeploymentPackage(is);
			
			Bundle[] bs = context.getBundles();
			boolean found = false;
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("com.nokia.easygame")) {
                    found = true;
                    break;
                }
            }
			
			// cleanup
			
			DeploymentPackage[] dps = da.listDeploymentPackages();
			for (int i = 0; i < dps.length; i++) {
			    if (dps[i].getName().equals("test_simple_01_01")) {
			        dps[i].uninstall();
			        break;
			    }
            }
			
			return found;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }
    
    private boolean test_simple_01_02() {
        FileInputStream is = null;
        try {
	        is = new FileInputStream(HOME + "test_simple_01_02.dp");
			da.installDeploymentPackage(is);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        // cleanup
		
		DeploymentPackage[] dps = da.listDeploymentPackages();
		for (int i = 0; i < dps.length; i++) {
		    if (dps[i].getName().equals("test_simple_01_02")) {
		        dps[i].uninstall();
		        break;
		    }
        }
		
		return true;
    }

    private boolean test_simple_01_03() {
        FileInputStream is = null;
        boolean found = false;
        try {
	        is = new FileInputStream(HOME + "test_simple_01_03.dp");
			da.installDeploymentPackage(is);
			
			Bundle[] bs = context.getBundles();
			for (int i = 0; i < bs.length; i++) {
                Bundle b = bs[i];
                if (b.getLocation().equals("com.nokia.easygame")) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        // cleanup
		
		DeploymentPackage[] dps = da.listDeploymentPackages();
		for (int i = 0; i < dps.length; i++) {
		    if (dps[i].getName().equals("test_simple_01_03")) {
		        dps[i].uninstall();
		        break;
		    }
        }
        
        return !found;
    }
    
    private boolean test_simple_01_04() {
        FileInputStream is = null;
        try {
	        is = new FileInputStream(HOME + "test_simple_01_04.dp");
			da.installDeploymentPackage(is);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is)
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        // cleanup
		
		DeploymentPackage[] dps = da.listDeploymentPackages();
		for (int i = 0; i < dps.length; i++) {
		    if (dps[i].getName().equals("test_simple_01_04")) {
		        dps[i].uninstall();
		        break;
		    }
        }
		
		return true;
    }
    
    private boolean test_customizer_01_01() {
        FileInputStream is2 = null;
        try {
	        is2 = new FileInputStream(HOME + "test_customizer_01_01.dp");
			da.installDeploymentPackage(is2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is2)
                try {
                    is2.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        
        // cleanup
        
        DeploymentPackage[] dps = da.listDeploymentPackages();
		for (int i = 0; i < dps.length; i++) {
		    if (dps[i].getName().equals("test_customizer_01_01")) {
		        dps[i].uninstall();
		        break;
		    }
        }

		return true;
    }
    
    public static void main(String[] args) throws IOException {
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
    }
    
}
