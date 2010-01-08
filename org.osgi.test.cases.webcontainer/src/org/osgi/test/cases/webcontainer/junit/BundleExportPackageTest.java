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
package org.osgi.test.cases.webcontainer.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Export-Package manifest header processed correctly with various
 *          scenarios
 */
public class BundleExportPackageTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String[] exports, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.EXPORT_PACKAGE, exports);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage001() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS1, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage002() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS2, "/tw2");
        this.b = super.installWar(options, "tw2.war", true);
        super.generalHeadersTest(options, "tw2.war", true, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage003() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS3, "/tw3");
        this.b = super.installWar(options, "tw3.war", false);
        super.generalHeadersTest(options, "tw3.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage004() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS4, "/tw4");
        this.b = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage005() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS5, "/tw5");
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage006() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS1, "/tw1");
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage007() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS2, "/tw2");
        this.b = super.installWar(options, "wmtw2.war", true);
        super.generalHeadersTest(options, "wmtw2.war", true, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage008() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS3, "/tw3");
        this.b = super.installWar(options, "wmtw3.war", false);
        super.generalHeadersTest(options, "wmtw3.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage009() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS4, "/tw4");
        this.b = super.installWar(options, "wmtw4.war", true);
        super.generalHeadersTest(options, "wmtw4.war", true, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Export-Package
     */
    public void testBundleExportPackage0010() throws Exception {
        final Map<String, Object> options = createOptions(EXPORTS5, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        super.generalHeadersTest(options, "wmtw5.war", false, this.b);
    }
}
