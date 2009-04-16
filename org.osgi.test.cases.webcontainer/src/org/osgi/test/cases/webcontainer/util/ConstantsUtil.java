/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.webcontainer.util;

import java.io.File;

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
        if (warContextPath.equals("/tw2")) {
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
    
}
