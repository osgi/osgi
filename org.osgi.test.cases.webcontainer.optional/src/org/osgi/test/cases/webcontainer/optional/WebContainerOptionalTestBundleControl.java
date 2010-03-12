package org.osgi.test.cases.webcontainer.optional;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.TimeUtil;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;

public class WebContainerOptionalTestBundleControl extends ManifestHeadersTestBundleControl {

    public static final String[] IMPORTS_ANNOTATION = {"javax.annotation"};
    protected TimeUtil timeUtil;
    
    protected void prepare(String wcp) throws Exception {
        super.prepare(wcp);
        this.timeUtil = new TimeUtil(this.warContextPath);
    }
    
    /*
     * check the response of ResourceServlet2 of tw2
     */
    protected void checkTW2ResourceServlet2Response(String response) throws Exception {
        // check if content of response is correct
        log("verify content of ResourceServlet2 response from tw2 is correct");
        if (debug) {
            log("response is " + response);
        }
        assertTrue(response.indexOf("ResourceServlet2") > 0);
        assertTrue(response.indexOf("Welcome String from env-entry!") > 0);
        assertTrue(response.indexOf("5 + 5 = 10 that is true") > 0);
        assertEquals(-1, response.indexOf("null"));    
    }
    
    /*
     * check the response of ResourceServlet2 of tw3
     */
    protected void checkTW3ResourceServlet2Response(String response) throws Exception {
        // check if content of response is correct
        log("verify content of ResourceServlet2 response from tw3 is correct");
        assertTrue(response.indexOf("ResourceServlet2") > 0);
        assertTrue(response.indexOf(ConstantsUtil.NULL + " "
                + ConstantsUtil.NULL) > 0);
        assertTrue(response.indexOf(ConstantsUtil.NULL + " + "
                + ConstantsUtil.NULL + " = " + ConstantsUtil.NULL + " that is "
                + ConstantsUtil.NULL) > 0); 
    }
    
    /* 
     * check one dynamic page is correct to make sure classes are loaded correctly
     */
    protected void checkDynamicPageResponse(String response, String warName) 
        throws Exception {
        if (warName.indexOf("tw2.war") > -1) {
            checkTW2ResourceServlet2Response(response);
        } else if (warName.indexOf("tw3.war") > -1) {
            checkTW3ResourceServlet2Response(response);
        }
    }
    
    protected static void cleanupPropertyFile() {
        // clean up the property file.
        boolean success = ConstantsUtil.removeLogFile();
        if (!success) {
            log("Deleting File: " + ConstantsUtil.getLogFile() + " failed.");
        } else {
            log(ConstantsUtil.getLogFile() + " file is deleted.");
        }
    }
    

      
    /*
     * check static content index.html is correct to make sure war is deployed correctly
     */
    protected void checkHomeResponse(String response, String warName)
            throws Exception {
        log("verify content of index.html response is correct");
        super.checkHomeResponse(response, warName);
        if (warName.indexOf("tw2.war") > -1) {
            checkTW2HomeResponse(response);
        } else if (warName.indexOf("tw3.war") > -1) {
            checkTW3HomeResponse(response);
        }
    }
    
    
    public static void checkTW2HomeResponse(String response) throws Exception {
        assertEquals(
                "<html><head><title>TestWar2</title></head><body>This is TestWar2.<P><A href=\"PostConstructPreDestroyServlet1\">PostConstructPreDestroyServlet1</A><BR><A href=\"PostConstructPreDestroyServlet2\">PostConstructPreDestroyServlet2</A><BR><A href=\"PostConstructPreDestroyServlet3\">PostConstructPreDestroyServlet3</A><BR><A href=\"ResourceServlet2\">ResourceServlet2</A><BR><A href=\"ResourceServlet4\">ResourceServlet4</A><BR><A href=\"PostConstructErrorServlet1\">PostConstructErrorServlet1</A><BR><A href=\"PostConstructErrorServlet2\">PostConstructErrorServlet2</A><BR><A href=\"PostConstructErrorServlet3\">PostConstructErrorServlet3</A><BR><A href=\"PreDestroyErrorServlet1\">PreDestroyErrorServlet1</A><BR><A href=\"PreDestroyErrorServlet2\">PreDestroyErrorServlet2</A><BR><A href=\"PreDestroyErrorServlet3\">PreDestroyErrorServlet3</A><BR><A href=\"ServletContextListenerServlet\">ServletContextListenerServlet</A><BR><A href=\"SecurityTestServlet\">SecurityTestServlet</A><BR><A href=\"RequestListenerServlet\">RequestListenerServlet</A><BR><A href=\"HTTPSessionListenerServlet\">HTTPSessionListenerServlet</A><BR></P></body></html>",
                response);
    }

    public static void checkTW3HomeResponse(String response) throws Exception {
        assertEquals(
                "<html><head><title>TestWar3</title></head><body>This is TestWar3.<P><A href=\"PostConstructPreDestroyServlet1\">PostConstructPreDestroyServlet1</A><BR><A href=\"PostConstructPreDestroyServlet2\">PostConstructPreDestroyServlet2</A><BR><A href=\"PostConstructPreDestroyServlet3\">PostConstructPreDestroyServlet3</A><BR><A href=\"ResourceServlet2\">ResourceServlet2</A><BR><A href=\"ResourceServlet4\">ResourceServlet4</A><BR><A href=\"ServletContextListenerServlet\">ServletContextListenerServlet</A><BR><A href=\"RequestListenerServlet\">RequestListenerServlet</A><BR><A href=\"HTTPSessionListenerServlet\">HTTPSessionListenerServlet</A><BR></P></body></html>",
                response);
    }
}
