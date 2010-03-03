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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 */
public class EventUtil {

    // get all events in the property file
    public static Properties getAllEventsProperties() {
        return loadProperties(ConstantsUtil.getLogFile());
    }

    // load property file
    protected static Properties loadProperties(File f) {
        Properties properties = new Properties();
        try {
            if (!f.exists()) {
                throw new FileNotFoundException();// f.createNewFile();
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

    // get event by classname and method name
    // @return null if no matching event is found
    public static Event getEvent(String className, String methodName) {
        Properties properties = getAllEventsProperties();
        for (Map.Entry entry : properties.entrySet()) {
            String name = (String) entry.getKey();
            if (name.equalsIgnoreCase(className + "." + methodName)) {
                String value = (String) entry.getValue();
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
