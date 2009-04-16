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
import java.util.Map;
import java.util.Properties;


public class EventUtil {
    
    //get all events in the property file
    public static Properties getAllEventsProperties() {
        return loadProperties(ConstantsUtil.getLogFile());    
    }
    
    //load property file
    protected static Properties loadProperties(File f) {
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
    
    //get event by classname and method name
    // @return null if no matching event is found
    public static Event getEvent(String className, String methodName) {
        Properties properties = getAllEventsProperties();
        for (Map.Entry entry : properties.entrySet()) {
            String name = (String)entry.getKey();
            if (name.equalsIgnoreCase(className + "." + methodName)) {
                String value = (String)entry.getValue();
                int i = value.indexOf("@");
                String desp;
                String timeString;
                long time;
                if (i > 0) {
                    desp = value.substring(0, i);
                    timeString = value.substring(i + 2);
                    try {
                        time = Long.parseLong(timeString.trim());
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        return null;
                    }
                    return new Event(className, methodName, desp.trim(), time);
                }
            }            
        }
        return null;
    }

}
