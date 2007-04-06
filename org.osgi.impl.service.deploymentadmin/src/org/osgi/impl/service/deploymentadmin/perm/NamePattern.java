/*
 * $Id$
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
package org.osgi.impl.service.deploymentadmin.perm;


class NamePattern {
    
    private String pattern;
    
    NamePattern(String str) {
    	if (0 == str.trim().length())
    		throw new IllegalArgumentException("Deployment package name must not be empty");
    	pattern = str;
    }
    
    public boolean match(String str) {
		StringBuffer p = new StringBuffer(pattern);
		StringBuffer s = new StringBuffer(str);
		
		while (p.length() > 0) {
			char pch = p.charAt(0);
			
			if (pch == '*') {
				closeupAsterixs(p);
				String aa = afterAsterix(p);
				if ("".equals(aa))
					return true;
				int pos = find(aa, s);
				if (pos < 0)
					return false;
				// delete '*'
				p.deleteCharAt(0);
				// and replace it with the begining of s
				if (!"".equals(aa))
					p.insert(0, s.substring(0, pos));
				else
					p.insert(0, s);
				continue;
			}
			char sch = s.charAt(0);
			if (pch == sch || pch == '?') {
				p.deleteCharAt(0);
				s.deleteCharAt(0);
				continue;	
			}
			
			return false;
		}
		
		return s.length() <= 0;
	}

    private static void closeupAsterixs(StringBuffer p) {
        while (p.length() > 1 && '*' == p.charAt(1))
            p.deleteCharAt(1);
    }

    private static String afterAsterix(StringBuffer ap) {
        StringBuffer p = new StringBuffer(ap.toString());
        p.deleteCharAt(0);
        StringBuffer ret = new StringBuffer();
        while (p.length() > 0 && p.charAt(0) != '*') {
            ret.append(p.charAt(0));
            p.deleteCharAt(0);
        }   
        return ret.toString();
    }

    private static int find(String afterAsterix, StringBuffer s) {
        for (int i = 0; i < s.length() - afterAsterix.length() + 1; ++i) {
            boolean error = false;
            for (int j = 0; j < afterAsterix.length(); ++j) {
                if (s.charAt(i + j) != afterAsterix.charAt(j)) {
                    if (afterAsterix.charAt(j) != '?') {
                        error = true;
                        break;
                    }
                }
            }
            if (!error) 
                return i;
        }
        
        return -1;
    }
}