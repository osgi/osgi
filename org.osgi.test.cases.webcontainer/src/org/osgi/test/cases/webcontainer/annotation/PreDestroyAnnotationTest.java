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

import org.osgi.framework.Bundle;
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
    Bundle b;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw2";
        this.timeUtil = new TimeUtil(this.warContextPath);

        // clean up the property file.
        /*
         boolean success = ConstantsUtil.removeLogFile(); 
         if (!success) {
             log("Deleting File: " + ConstantsUtil.getLogFile() + " failed."); 
         }
         else { 
              log (ConstantsUtil.getLogFile() + " file is deleted."); 
         }*/

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        b = installBundle(getWebServer()
                + "tw2.war", true);
        
        // capture a time before uninstall
        beforeUninstall = System.currentTimeMillis();
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw2.war at context path " + this.warContextPath);
        uninstallBundle(b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test @preDestroy annotated public method is called
     */
    public void testPreDestroy001() throws Exception {
        uninstallWar();
        // TODO do we need to wait till the war is uninstalled properly?
        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet1", "cleanup") > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet2", "cleanup") > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet3", "cleanup") > beforeUninstall);
    }
}
