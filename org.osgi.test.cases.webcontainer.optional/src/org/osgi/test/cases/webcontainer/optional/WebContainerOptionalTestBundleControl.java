package org.osgi.test.cases.webcontainer.optional;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.TimeUtil;
import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;

public class WebContainerOptionalTestBundleControl extends WebContainerTestBundleControl {

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
}
