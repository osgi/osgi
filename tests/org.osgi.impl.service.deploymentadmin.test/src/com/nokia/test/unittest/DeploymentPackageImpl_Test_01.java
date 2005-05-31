package com.nokia.test.unittest;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarInputStream;

import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;

import junit.framework.TestCase;

public class DeploymentPackageImpl_Test_01 extends TestCase {
    
    private static final String   HOME = "res/unittest/";
    
    private JarInputStream        jis;
    private DeploymentPackageImpl dp;
    
    public void setUp() throws Exception {
        jis = new JarInputStream(new FileInputStream(HOME + "dp_01.dp"));
        dp = new DeploymentPackageImpl(jis.getManifest(), 99, null, null);
    }
    
    public void tearDown() throws Exception {
        if (null != jis)
            jis.close();
        dp = null;
    }

    public void testGetBundleSymNameVersionPairs() {
        String[][] bs = dp.getBundleSymNameVersionPairs();
        assertTrue(bs[0][0].equals("dbrp_01"));
        assertTrue(bs[0][1].equals("1.0"));
        assertTrue(bs[1][0].equals("dbrp_01_2"));
        assertTrue(bs[1][1].equals("2.0"));
    }
    
    public void testGetResources() {
        Set resources = new HashSet();
        resources.add("res1");
        resources.add("res2");
        
        String[] rss = dp.getResources();
        Set set = new HashSet(); 
        for (int i = 0; i < rss.length; i++)
            set.add(rss[i]);
        
        assertTrue(set.containsAll(resources));
    }

    public void testGetHeader() {
        assertTrue(dp.getHeader("DeploymentPackage-Name").equals("dp_01"));
        
        assertFalse(dp.getHeader("DeploymentPackage-Name").equals("error"));
    }
    
    public void testGetResourceHeader() {
        assertTrue(dp.getResourceHeader("res1", "header1").equals("val1"));
        assertTrue(dp.getResourceHeader("res1", "header2").equals("val2"));
        assertTrue(dp.getResourceHeader("res2", "header3").equals("val3"));
        assertTrue(dp.getResourceHeader("res2", "header4").equals("val4"));
        
        assertTrue(null == dp.getResourceHeader("res2", "error"));
    }
    
    public void testGetName() {
        assertTrue(dp.getName().equals("dp_01"));
    }
    
    public void testGetVersion() {
        Version v1 = new Version("1.0");
        Version v2 = dp.getVersion();
        assertTrue(v1.equals(v2));
    }

}
