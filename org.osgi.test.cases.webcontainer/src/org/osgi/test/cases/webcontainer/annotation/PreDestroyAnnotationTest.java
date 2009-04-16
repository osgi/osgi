/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.webcontainer.annotation;

import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 */
public class PreDestroyAnnotationTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeUninstall;
    TimeUtil timeUtil;
    
    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.
        
        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw2";
        this.timeUtil = new TimeUtil(this.warContextPath);
        
        // TODO install the war file
        
        // capture a time before uninstall
        beforeUninstall = System.currentTimeMillis();
    }
    
    private void uninstallWar() throws Exception {

        // TODO uninstall the war file?
        
    }
    
    public void tearDown() throws Exception {
      
    }
    
    
    /*
     * test @preDestroy annotated public method is called
     * test @postConstruct annotated private method is called
     * test @postConstruct annotated protected method is called
     */
    public void testPreDestroy001() throws Exception {
            uninstallWar();
            // TODO do we need to wait till the war is uninstalled properly?
            
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet1", "cleanup") > beforeUninstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet2", "cleanup") > beforeUninstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet3", "cleanup") > beforeUninstall);
    }
}
