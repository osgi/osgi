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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TimeUtil {
    
    String contextPath;
    
    public TimeUtil(String contextPath) {
        this.contextPath = contextPath;
    }

    /*
     * get the time in long format from the response.   
     * the servlet prints the time at the end after the @ sign
     */
    public long getTimeFromResponse(String response) {
        int start = response.indexOf("@") + 2;
        int end = response.indexOf("</body");
        long time;
        try {
            time = Long.parseLong(response.substring(start, end));
        } catch (NumberFormatException ex) {
            time = 0;
            ex.printStackTrace();
        }
       
        return time;
    }
    
    /*
     * get the called/injection time in long format from servlet's property file   
     * for the particular servlet class and method
     */
    public long getTimeFromLog(String className, String methodName) {
        try {
            Properties properties = loadProperties(ConstantsUtil.getLogFile());
            String fullClassName = ConstantsUtil.getFullClassName(this.contextPath, className);
            String value = properties.getProperty(fullClassName + "." + methodName);
            
            if (value != null && value.indexOf("@") > 0) {
                int start = value.indexOf("@") + 2;
                return Long.parseLong(value.substring(start));
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /*
     * get the description in String format from servlet's property file   
     * for the particular servlet class and method
     */
    public String getDespFromLog(String className, String methodName) {
        try {
            Properties properties = loadProperties(ConstantsUtil.getLogFile());
            String fullClassName = ConstantsUtil.getFullClassName(this.contextPath, className);
            String value = properties.getProperty(fullClassName + "." + methodName);
            
            if (value != null && value.indexOf("@") > 0) {
                int end = value.lastIndexOf("@");
                return value.substring(0, end).trim();
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    //load property file
    protected Properties loadProperties(File f) {
        Properties properties = new Properties();
        try {
            if (!f.exists()) {
                throw new FileNotFoundException();//f.createNewFile();
            }
            InputStream in = new FileInputStream(f);
            try {
                properties.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            //not much to do
        }   
        return properties;
    }
}
