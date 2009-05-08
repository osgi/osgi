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

import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.service.webcontainer.WebContainer;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          abstract test class for webcontainer
 */
public abstract class WebContainerTestBundleControl extends
        DefaultTestBundleControl {
    protected Server server;
    protected boolean debug;
    protected long beforeInstall;
    protected Map options = new HashMap();
    protected String warContextPath;
    protected TimeUtil timeUtil;
    private final String WARSCHEMA = "war:";

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.
        this.server = new Server();
        this.debug = true;

        // capture a time before install
        this.beforeInstall = System.currentTimeMillis();
    }
    
    protected void prepare(String wcp) throws Exception {
        this.warContextPath = wcp;
        this.timeUtil = new TimeUtil(this.warContextPath);
        this.options.put(WebContainer.WEB_CONTEXT_PATH, this.warContextPath);
    }

    /*
     * return original manifest from the test war path
     */
    protected Manifest getManifest(String warPath) throws Exception {
        // test bundle manifest is constructed per user's deployment options
        String location = System.getProperty("user.dir") + warPath;
        log("jar:file:" + location + "!/");
        URL url = new URL("jar:file:" + location + "!/");
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        JarFile jarfile = conn.getJarFile();
        Manifest originalManifest = jarfile.getManifest();

        return originalManifest;
    }
    
    /*
     * return original manifest from the test war name
     */
    protected Manifest getManifestFromWarName(String warName) throws Exception {
        return getManifest(getWarPath(warName));
    }

    /*
     * return the warPath based on the warName, for example tw1.war path
     * is /resources/tw1/tw1.war
     */
    private String getWarPath(String warName) throws Exception {
        int i = warName.indexOf(".");
        int j = warName.indexOf("tw");
        if (warName.startsWith("wm")) {
            return "/resources/" + warName.substring(j, i) + "/" + warName;
        } else {
          return "/resources/" + warName.substring(0, i) + "/" + warName;
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

    // TODO: fill in this method when the schema is defined in the RFC 66 spec
    protected String getWarURL(String name, Map options) {
        return getWebServer() + name; // TODO hook in options: + "?" + generateQuery(options);
    }

    protected String getResponse(String path) throws Exception {
        // final String request = warContextPath + "/";
        final URL url = Dispatcher.createURL(path, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        final String response;
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
        } finally {
            conn.disconnect();
        }
        return response;
    }
    
    protected boolean ableAccessPath(String path) throws Exception {
        try {
            getResponse(path);
            return true;
        } catch (Exception e) {
            return false;
        } catch (Error e) {
            return false;
        }
    }

    protected void checkTW1HomeResponse(String response) throws Exception {
        assertEquals(response, "<html><head><title>TestWar1</title></head><body>This is TestWar1.<P><A href=\"BasicTest\">/BasicTest</A><BR><A href=\"404.html\">404.html (static link)</A><BR><A href=\"ErrorTest?target=html\">404.html (through servlet.RequestDispatcher.forward())</A><BR><A href=\"ErrorTest\">404.jsp (through servlet.RequestDispatcher.forward())</A><BR><A href=\"aaa\">Broken Link (for call ErrorPage)</A><BR><A href=\"image.html\">image.html</A></P><BR></body></html>");
    }
    
    protected void checkTW3HomeResponse(String response) throws Exception {       
        assertEquals(response, "<html><head><title>TestWar3</title></head><body>This is TestWar3.<P><A href=\"PostConstructPreDestroyServlet1\">PostConstructPreDestroyServlet1</A><BR><A href=\"PostConstructPreDestroyServlet2\">PostConstructPreDestroyServlet2</A><BR><A href=\"PostConstructPreDestroyServlet3\">PostConstructPreDestroyServlet3</A><BR><A href=\"ResourceServlet1\">ResourceServlet1</A><BR><A href=\"ResourceServlet2\">ResourceServlet2</A><BR><A href=\"ResourceServlet3\">ResourceServlet3</A><BR><A href=\"ResourceServlet4\">ResourceServlet4</A><BR><A href=\"ServletContextListenerServlet\">ServletContextListenerServlet</A><BR><A href=\"RequestListenerServlet\">RequestListenerServlet</A><BR><A href=\"HTTPSessionListenerServlet\">HTTPSessionListenerServlet</A><BR></P></body></html>");
    }
    
    protected void checkHomeResponse(String response, String warName) throws Exception {
        log("verify content of response is correct");
        if (warName.equalsIgnoreCase("tw1.war")) { 
        		checkTW1HomeResponse(response);
        } else if (warName.equalsIgnoreCase("tw2.war")) { 
            checkTW2HomeResponse(response);
        } else if (warName.equalsIgnoreCase("tw3.war")) { 
            checkTW3HomeResponse(response);
        } else if (warName.equalsIgnoreCase("tw4.war")) { 
            checkTW4HomeResponse(response);
        } else if (warName.equalsIgnoreCase("tw5.war")) { 
            checkTW5HomeResponse(response);
        }
    }
    
    protected void checkTW2HomeResponse(String response) throws Exception {
        assertEquals(response, "<html><head><title>TestWar2</title></head><body>This is TestWar2.<P><A href=\"PostConstructPreDestroyServlet1\">PostConstructPreDestroyServlet1</A><BR><A href=\"PostConstructPreDestroyServlet2\">PostConstructPreDestroyServlet2</A><BR><A href=\"PostConstructPreDestroyServlet3\">PostConstructPreDestroyServlet3</A><BR><A href=\"ResourceServlet1\">ResourceServlet1</A><BR><A href=\"ResourceServlet2\">ResourceServlet2</A><BR><A href=\"ResourceServlet3\">ResourceServlet3</A><BR><A href=\"ResourceServlet4\">ResourceServlet4</A><BR><A href=\"PostConstructErrorServlet1\">PostConstructErrorServlet1</A><BR><A href=\"PostConstructErrorServlet2\">PostConstructErrorServlet2</A><BR><A href=\"PostConstructErrorServlet3\">PostConstructErrorServlet3</A><BR><A href=\"PreDestroyErrorServlet1\">PreDestroyErrorServlet1</A><BR><A href=\"PreDestroyErrorServlet2\">PreDestroyErrorServlet2</A><BR><A href=\"PreDestroyErrorServlet3\">PreDestroyErrorServlet3</A><BR><A href=\"ServletContextListenerServlet\">ServletContextListenerServlet</A><BR><A href=\"SecurityTestServlet\">SecurityTestServlet</A><BR><A href=\"RequestListenerServlet\">RequestListenerServlet</A><BR><A href=\"HTTPSessionListenerServlet\">HTTPSessionListenerServlet</A><BR></P></body></html>");
    }
    
    protected void checkTW4HomeResponse(String response) throws Exception {
        assertEquals(response, "<html><head><title>TestWar4</title></head><body>This is TestWar4.<P><a href=\"TestServlet1\">TestServlet1</a><br/><a href=\"TestServlet1/TestServlet2?tc=1&param1=value1&param2=abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz\">TestServlet2?tc=1</a><BR><br/><a href=\"TestServlet1/TestServlet2/TestServlet3\">TestServlet3</a><br/><a href=\"TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=plain\">TestServlet4 plain</a><br/><a href=\"TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=html\">TestServlet4 html</a><br/><a href=\"TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=jpg\">TestServlet4 jpg</a><br/></P></body></html>");
    }
    
    protected void checkTW5HomeResponse(String response) throws Exception {
        assertEquals(response, "<html><head><title>TestWar5</title></head><body>This is TestWar5.<P><A href=\"BundleTestServlet\">/BundleTestServlet</A><BR><A href=\"ClasspathTestServlet\">/ClasspathTestServlet</A><BR></P></body></html>");
    }
    
    // TODO fill this in when the schema is defined in RFC 66
    private String generateQuery(Map options) {
        return "";
    }
}
