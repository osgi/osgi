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

import java.util.Map;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          abstract manifest header processing test class for webcontainer
 */
public abstract class ManifestHeadersTestBundleControl extends
        WebContainerTestBundleControl {
    protected static final String[] EXPORTS1 = {"org.osgi.test.cases.webcontainer.tw1;version=1.0"}; 
    protected static final String[] EXPORTS2 = {"org.osgi.test.cases.webcontainer.tw2;version=1.0", "org.osgi.test.cases.webcontainer.util;version=1.0"}; 
    protected static final String[] EXPORTS3 = {"org.osgi.test.cases.webcontainer.tw3;version=1.0", "org.osgi.test.cases.webcontainer.util;version=1.0"}; 
    protected static final String[] EXPORTS4 = {"org.osgi.test.cases.webcontainer.tw4;version=1.0"}; 
    protected static final String[] EXPORTS5 = {"org.osgi.test.cases.webcontainer.tw5;version=1.0;company=\"OSGi Alliance\";security=false;mandatory:=security"};  
    
    protected static final String[] CLASSPATH1 = {"WEB-INF/lib/org.apache.commons-logging-1.0.4.jar"}; 
    protected static final String[] CLASSPATH2 = {"WEB-INF/lib/org.apache.commons-logging-1.0.4.jar","WEB-INF/lib/org.osgi.test.cases.webcontainer.simple.jar"};
    protected static final String[] CLASSPATH3 = {"WEB-INF/classes/"};
    protected static final String[] CLASSPATH4 = {"WEB-INF/lib/org.apache.commons-logging.jar"};
    protected static final String[] CLASSPATH5 = {"WEB-INF/lib/org.apache.commons-logging-1.0.4.jar", "libs/utiljar"};
    protected static final String[] CLASSPATH6 = {"WEB-INF/lib2/org.osgi.test.cases.webcontainer.simple.jar"};

    protected static final String[] IMPORTS1 = {"javax.servlet; version=2.5", "javax.servlet.http; version=2.5"}; 
    protected static final String[] IMPORTS2 = {"javax.servlet;version=2.5", "javax.servlet.http;version=2.5", "javax.servlet.jsp; version=2.1", "javax.servlet.jsp.tagext; version=2.1"}; 
    protected static final String[] IMPORTS3 = {"javax.servlet; version=(2.1, 2.5]", "javax.servlet.http; version=(2.1, 2.5]"};
    protected static final String[] IMPORTS4 = {"javax.servlet.jsp; version=[2.0,2.1]", "javax.servlet.jsp.tagext; version=[2.0,2.1]"}; 
    protected static final String[] IMPORTS5 = {"org.osgi.service.log", "javax.servlet; version=2.4", "javax.servlet.http; version=2.4"};
    protected static final String[] IMPORTS9 = {"javax.servlet; version=2.6", "javax.servlet.http; version=2.6"};
    protected static final String[] IMPORTS10 = {"org.osgi.service.log;version=2.0"};
    
    protected static final String MANIFESTVERSION1 = "2";
    protected static final String MANIFESTVERSION2 = "4";
    protected static final String MANIFESTVERSION3 = "1";
    
    protected static final String SYMBOLICNAME1 = "ct-testwar1";
    protected static final String SYMBOLICNAME2 = "ct-testwar2asdacakjdlkjasldja;dk;k121pi2910-921-0lkajdlkajsdlsadjlaksdjskajdklsajdklasjdksakdjaksljdaksljd";
    protected static final String SYMBOLICNAME3 = "ct-testwar3-----------aklsdmklajsdl kajskldjaldlasjdklajdlksa;djklajsdkljakldjskaljdkaljsdlksjadklsajdkasdj";
    protected static final String SYMBOLICNAME4 = "ct-testwar4--//laksldkl; laksldk;askd;aslkd;laksdlksaldkl;laksdpqoeiewihrfrhbgsmndb123e32";
    protected static final String SYMBOLICNAME5 = "ct-testwar5";
    protected static final String VERSION10 = "1.0";
    
    protected static final String VERSION1 = "2.0";
    protected static final String VERSION2 = "1.1.1";
    protected static final String VERSION3 = "22.3.58.build-345678";
    protected static final String VERSION4 = "22.1.1.build - 12121";
    protected static final String VERSION5 = "version2.0";
    
    protected static final String WEBCONTEXTPATH1 = "/ct-testwar1";
    protected static final String WEBCONTEXTPATH2 = "/ct-testwar2";
    protected static final String WEBCONTEXTPATH3 = "/ct-testwar3";
    protected static final String WEBCONTEXTPATH4 = "/ct-testwar4";
    protected static final String WEBCONTEXTPATH5 = "/ct-testwar5";
    protected static final String LONGWEBCONTEXTPATH = "/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxy";
    protected static final String LONGWEBCONTEXTPATH2 = "/abcdefghijk/lmnopqrstuvwxyzabcdefgh/ijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrs";
    protected static final String LONGWEBCONTEXTPATH3 = "/abcde/fghijklmnopqrstuvwxyzabcdefghijk/lmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghi/12345678901233543523423424242342342";

    protected static final String JSPEXTRACTLOAC1 = "resources/tw1";
    protected static final String JSPEXTRACTLOAC2 = "resources/tw1/jspextract";
    
    protected Bundle b;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        if (this.b != null && this.b.getState() != Bundle.UNINSTALLED) {
            this.b.uninstall();
        }
        this.b = null;
    }

    /*
     * generalVersionTest to be used by non-error test
     */
    protected Bundle generalHeadersTest(Map options, String warName, boolean start)
            throws Exception {
        // specify install options
        String cp = options.get(WEB_CONTEXT_PATH) == null ? null
                : (String) options.get(WEB_CONTEXT_PATH);
        // install the war file
        log("install and start war file: " + warName + " at contextPath " + cp);
        Bundle b = null;
        try {
            b = installBundle(super.getWarURL(warName, options), start);
        } catch (Exception e) {
            fail("bundle install should succeed. " + e.getCause());
        }
        assertNotNull("Bundle b should not be null", b);
        Manifest originalManifest = super.getManifestFromWarName(warName);

        // check manifest generated correctly
        BundleManifestValidator validator = new BundleManifestValidator(b,
                originalManifest, options, this.debug);
        try {
            validator.validate();
        } catch (IllegalArgumentException e) {
            fail("version format is valid - should not be getting an IllegalArgumentException"
                    + e.getCause());
        } catch (Exception e) {
            fail("should not get any exception during validation "
                    + e.getCause());
        }

        if (cp == null) {
            // let's find out the actual web-contextpath first since it is
            // generated by the system
            cp = (String) b.getHeaders().get(WEB_CONTEXT_PATH);
        }

        if (!start) {
            // test unable to access pathes yet as it is not started
            assertEquals("Bundle status should be Resolved but not Active", b
                    .getState(), Bundle.RESOLVED);
            assertFalse(
                    "Bundle not started yet - should not be able to access "
                            + cp, super.ableAccessPath(cp));
            b.start();
        }
        
        // rough test able to access the app
        assertTrue("should be able to access " + cp, super.ableAccessPath(cp));

        assertEquals("Bundle status should be Active", b.getState(),
                Bundle.ACTIVE);
        try {
            String response = super.getResponse(cp);
            super.checkHomeResponse(response, warName);
        } catch (Exception e) {
            fail("should not be getting an exception here " + e.getMessage());
        }

        b.stop();
        // test unable to access pathes yet as it is not started
        assertEquals("Bundle status should be Resolved but not Active", b
                .getState(), Bundle.RESOLVED);
        assertFalse("Bundle not started yet - should not be able to access "
                + cp, super.ableAccessPath(cp));

        if (start) {
            b.start();
        }
        return b;
    }
    
    /*
     * test ClasspathTestServlet
     */
    protected void classpassServletTest(Bundle b) throws Exception {
        String cp = (String) b.getHeaders().get(WEB_CONTEXT_PATH);
        final String request = cp + "/ClasspathTestServlet";
        String response = super.getResponse(request);
        assertEquals("checking response content", response,"<html><head><title>ClasspathTestServlet</title></head><body>" 
                + ConstantsUtil.ABLEGETLOG + "<br/>" +  ConstantsUtil.ABLEGETSIMPLEHELLO + "<br/></body></html>");
    }

}
