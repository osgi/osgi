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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.webcontainer.WebContainer;

/**
 * @version $Rev$ $Date$
 * 
 *          test Export-Package manifest header processed correctly with various
 *          scenarios
 */
public class BundleExportPackageTest extends ManifestHeadersTestBundleControl {
    private static final String[] EXPORTS1 = {"org.osgi.test.cases.webcontainer.tw1;version=1.0"}; 
    private static final String[] EXPORTS2 = {"org.osgi.test.cases.webcontainer.tw2;version=1.0", "org.osgi.test.cases.webcontainer.util;version=1.0"}; 
    private static final String[] EXPORTS3 = {"org.osgi.test.cases.webcontainer.tw3;version=1.0", "org.osgi.test.cases.webcontainer.util;version=1.0"}; 
    private static final String[] EXPORTS4 = {"org.osgi.test.cases.webcontainer.tw4;version=1.0"}; 
    private static final String[] EXPORTS5 = {"org.osgi.test.cases.webcontainer.tw5;version=1.0;company=\"IBM\";security=false;mandatory:=security"};  
    
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage001() throws Exception {
        this.b = generalExportPackageTest(EXPORTS1, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage002() throws Exception {
        this.b = generalExportPackageTest(EXPORTS2, "/tw2", "tw2.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage003() throws Exception {
        this.b = generalExportPackageTest(EXPORTS3, "/tw3", "tw3.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage004() throws Exception {
        this.b = generalExportPackageTest(EXPORTS4, "/tw4", "tw4.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage005() throws Exception {
        this.b = generalExportPackageTest(EXPORTS5, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage006() throws Exception {
        this.b = generalExportPackageTest(null, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage007() throws Exception {
        this.b = generalExportPackageTest(null, "/tw2", "tw2.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage008() throws Exception {
        this.b = generalExportPackageTest(null, "/tw3", "tw3.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage009() throws Exception {
        this.b = generalExportPackageTest(null, "/tw4", "tw4.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage010() throws Exception {
        this.b = generalExportPackageTest(null, "/tw5", "tw5.war", true);
    }
    
    /*
     * error case, TODO cannot think of one now
     * 
     */


    /*
     * generalExportPackageTest to be used by non-error test
     */
    private Bundle generalExportPackageTest(String[] imports, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.EXPORT_PACKAGE, imports);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }

    // TODO create war manifest that contains the Export-Package header and more
    // tests
}
