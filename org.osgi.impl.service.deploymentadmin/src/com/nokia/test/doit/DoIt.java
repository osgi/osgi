package com.nokia.test.doit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;

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
        }
        
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
    
}
