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

package org.osgi.test.cases.webcontainer.optional.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 */
public class TimeUtil {

    String contextPath;

    public TimeUtil(String contextPath) {
        this.contextPath = contextPath;
    }

    /*
     * get the time in long format from the response. the servlet prints the
     * time at the end after the @ sign
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
            String fullClassName = ConstantsUtil.getFullClassName(
                    this.contextPath, className);
            String value = properties.getProperty(fullClassName + "."
                    + methodName);

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
     * get the description in String format from servlet's property file for the
     * particular servlet class and method
     */
    public String getDespFromLog(String className, String methodName) {
        try {
            Properties properties = loadProperties(ConstantsUtil.getLogFile());
            String fullClassName = ConstantsUtil.getFullClassName(
                    this.contextPath, className);
            String value = properties.getProperty(fullClassName + "."
                    + methodName);

            if (value != null && value.indexOf("@") > 0) {
                int end = value.lastIndexOf("@");
                return value.substring(0, end).trim();
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /*
     * Load the property file. If the file doesn't exist, create an empty file
     */
    protected Properties loadProperties(File f) {
        Properties properties = new Properties();
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            InputStream in = new FileInputStream(f);
            try {
                properties.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // not much to do
        }
        return properties;
    }
}
