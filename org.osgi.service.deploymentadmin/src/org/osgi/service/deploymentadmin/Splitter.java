/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.service.deploymentadmin;

import java.util.Vector;


public class Splitter {
    public static String[] split(String input, char sep, int limit) {
        Vector v = new Vector();
        boolean limited = (limit > 0);
        int applied = 0;
        int index = 0;
        StringBuffer part = new StringBuffer();
        while (index < input.length()) {
            char ch = input.charAt(index);
            boolean esc = (index - 1 > 0 && '\\' == input.charAt(index - 1));
            if (ch != sep || esc) {
                if (esc && ch == sep)
                    part.deleteCharAt(part.length() - 1);
                part.append(ch);
            } else {
                ++applied;
                v.add(part.toString());
                part = new StringBuffer();
            }
            ++index;
            if (limited && applied == limit - 1)
                break;
        }
        while (index < input.length()) {
            char ch = input.charAt(index);
            part.append(ch);
            ++index;
        }
        v.add(part.toString());
        int last = v.size();
        if (0 == limit) {
            for (int j = v.size() - 1; j >= 0; --j) {
                String s = (String) v.elementAt(j);
                if ("".equals(s))
                    --last;
                else
                    break;
            }
        }
        String[] ret = new String[last];
        for (int i = 0; i < last; ++i)
            ret[i] = (String) v.elementAt(i);
        return ret;
    }
}