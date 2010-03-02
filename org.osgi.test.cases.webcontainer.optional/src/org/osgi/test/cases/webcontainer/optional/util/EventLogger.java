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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 */
public class EventLogger {

    // log the class name, method name and the time-stamp
    public static void logEvent(Event event) {
        String className = event.getClassName();
        String methodName = event.getMethodName() == null ? "" : event
                .getMethodName();
        String desp = event.getDesp() == null ? "" : event.getDesp();

        try {
            if (!ConstantsUtil.getLogFile().exists()) {
                ConstantsUtil.getLogFile().createNewFile();
            }
            Properties properties = EventUtil.loadProperties(ConstantsUtil
                    .getLogFile());
            properties.setProperty(className + "." + methodName, desp + " @ "
                    + String.valueOf(event.getTime()));

            FileOutputStream out = new FileOutputStream(ConstantsUtil
                    .getLogFile());
            try {
                properties.store(out, null);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("hmm IOException " + e.getCause());
            e.printStackTrace();
        }
    }

    public static String printEvent(Event event) {
        String methodN = event.getMethodName() == null ? "" : event
                .getMethodName()
                + " ";
        String despN = event.getDesp() == null ? "" : event.getDesp() + " ";
        return event.getClassName() + "." + methodN + despN + "@ "
                + String.valueOf(event.getTime());
    }
}
