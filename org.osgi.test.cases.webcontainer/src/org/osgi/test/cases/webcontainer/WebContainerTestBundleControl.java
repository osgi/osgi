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
package org.osgi.test.cases.webcontainer;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          abstract test class for webcontainer
 */
public abstract class WebContainerTestBundleControl extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    protected Server server;
    protected boolean debug;
    protected long beforeInstall;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.
        this.server = new Server();
        this.debug = true;
        
        // capture a time before install
        this.beforeInstall = System.currentTimeMillis();
    }

    /*
     * return original manifest from the test war file
     */
    protected Manifest getManifest(String warPath) throws Exception {
        // test bundle manifest is constructed per user's deployment options
        String location = System.getProperty("user.dir") + warPath;
        log("jar:file:" + location + "!/");
        URL url = new URL("jar:file:" + location + "!/");
        JarURLConnection conn = (JarURLConnection)url.openConnection();
        JarFile jarfile = conn.getJarFile();
        Manifest originalManifest = jarfile.getManifest();
        
        return originalManifest;
    }
    
    protected static void cleanupPropertyFile() {
        // clean up the property file.        
         boolean success = ConstantsUtil.removeLogFile(); 
         if (!success) {
             log("Deleting File: " + ConstantsUtil.getLogFile() + " failed."); 
         }
         else { 
              log (ConstantsUtil.getLogFile() + " file is deleted."); 
         }
    }
}
