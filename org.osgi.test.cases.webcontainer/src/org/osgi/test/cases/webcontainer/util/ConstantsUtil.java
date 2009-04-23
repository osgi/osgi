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

package org.osgi.test.cases.webcontainer.util;

import java.io.File;

/**
 * @version $Rev$ $Date$
 */
public class ConstantsUtil {
    public static String getBaseDir() {
        // put it in user.home for now.  TODO is there a better solution?
        // please note that the baseDir is also needed for the test war files.
        return System.getProperty("user.home");
    }
    
    public static File getLogFile() {
        return new File(getBaseDir(), Constants.LOGFILE);
    }
    
    /*
     * this method returns the full classname based on the warContextPath and class name
     */
    public static String getFullClassName(String warContextPath, String name) {
        if (warContextPath.equals("/tw2") || warContextPath.equals("/tw3")) {
            return "org.osgi.test.cases.webcontainer.tw2." 
                   + (isServlet(name) == true ? "servlet." : "") + name;
        }
        if (warContextPath.startsWith("/")) {
            return "org.osgi.test.cases.webcontainer." + warContextPath.substring(1) + "." 
            + (isServlet(name) == true ? "servlet." : "") + name;
        }
        return name;
    }
    
    /*
     * check to see if we need to add "servlet." to the full class name.
     */
    private static boolean isServlet(String name) {
        int i = name.lastIndexOf("Servlet");
        if (i + "Servlet".length() == name.length() || i + "Servlet".length() == name.length() - 1 ) {
            return true;
        }
        return false;
    }
    
    public static boolean removeLogFile() { 
        // clean up the property file.
        boolean success = false;
        if (ConstantsUtil.getLogFile().exists()) {
            success = ConstantsUtil.getLogFile().delete();
        }
        return success;
    }
    
}
