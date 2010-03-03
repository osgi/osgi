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

package org.osgi.test.cases.webcontainer.optional.annotation.junit;

import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;

/**
 * @version $Rev$ $Date$
 */
public class PreDestroyAnnotationTest extends WebContainerOptionalTestBundleControl {
    long beforeUninstall;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        super.cleanupPropertyFile();
        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw2.war", this.options), true);
        
        assertTrue("should be able to see the servlet context associated with /tw2 web contextpath",
                super.checkServiceRegistered("/tw2"));
        // capture a time before uninstall
        beforeUninstall = System.currentTimeMillis();
    }

    /*
     * test @preDestroy annotated public method is called
     */
    public void testPreDestroy001() throws Exception {
        super.tearDown();
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
