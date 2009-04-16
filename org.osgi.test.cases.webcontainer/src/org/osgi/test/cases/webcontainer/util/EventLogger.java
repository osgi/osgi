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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EventLogger {


    // log the class name, method name and the time-stamp
    public static void logEvent(Event event) {
        String className = event.getClassName();
        String methodName = event.getMethodName() == null ? "" : event.getMethodName();
        String desp = event.getDesp() == null ? "": event.getDesp();

        try {
            if (!ConstantsUtil.getLogFile().exists()) {
                ConstantsUtil.getLogFile().createNewFile();
            }
            Properties properties = EventUtil.loadProperties(ConstantsUtil.getLogFile());
            properties.setProperty(className + "." + methodName, desp + " @ " + String.valueOf(event.getTime()));
            
            FileOutputStream out = new FileOutputStream(ConstantsUtil.getLogFile());
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
        String methodN = event.getMethodName() == null ? "" : event.getMethodName() + " ";
        String despN = event.getDesp() == null ? "" : event.getDesp() + " ";
        return event.getClassName() + "."+ methodN + despN + "@ " + String.valueOf(event.getTime());
    }
}
