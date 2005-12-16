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
package org.osgi.impl.service.deploymentadmin.perm;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.osgi.impl.service.deploymentadmin.DAKeyStore;
import org.osgi.impl.service.deploymentadmin.Splitter;


class SignerChainPattern {
    
    private static final Set allowedKeys = new HashSet();
    static {
        String [] sa = new String[] { 
            "commonname",             "cn",     "2.5.4.3",
            "surname",                "sn",     "2.5.4.4",
            "countryname",            "c",      "2.5.4.6",
            "localityname",           "l",      "2.5.4.7",
            "stateorprovincename",    "st",     "2.5.4.8",
            "organizationname",       "o",      "2.5.4.10",
            "organizationalunitname", "ou",     "2.5.4.11",
            "title",                            "2.5.4.12",
            "givenname",                        "2.5.4.42",
            "initials",                         "2.5.4.43",
            "generationqualifier",              "2.5.4.44",
            "dnqualifier",                      "2.5.4.46",
            "streetaddress",          "street",
            "domaincomponent",        "dc",
            "userid",                 "uid",
            "emailaddress",
            "serialnumber" };
        allowedKeys.addAll(Arrays.asList(sa));
    }

    private static Object keystore;
    static {
        try {
            DAKeyStore.getKeyStore();
        }
        catch (Exception e) {
            // keystore.something will check whether keystore is null
        }
    }

    private SignerPattern[] patterns;
        
    SignerChainPattern(String str) {
        String[] sa = Splitter.split(str, ';', 0);
        patterns = new SignerPattern[sa.length];
        for (int i = 0; i < sa.length; i++) {
            if (i > 0 && i < sa.length - 1 && "-".equals(sa[i].trim()))
                throw new IllegalArgumentException("'-' is not allowed in the middle of the " +
                		"chain");
            patterns[i] = new SignerPattern(sa[i]);
        }
    }

   public boolean match(String signerChain) {
       String[] chain;
       if (null == signerChain)
           chain = new String[] {};
       else
           chain = Splitter.split(signerChain, ';', 0);
       int i = patterns.length - 1;
       int j = chain.length - 1;
       for (; i >= 0 && j >= 0;) {
           if (patterns[i].isMinus())
               return true;
           if (!patterns[i].match(chain[j]))
               return false;
           --i; --j;
       }
       return true;
   }
    
    static class SignerPattern {
        private Vector    rdns;
        private boolean   trusted;
        private boolean   minus;
        
        SignerPattern(String pattern) {
            String p = pattern;
            if ("-".equals(p.trim())) {
                minus = true;
                return;
            }
            if (pattern.startsWith("<") && pattern.endsWith(">")) {
                p = p.substring(1, p.length() - 1);
                trusted = true;
            }
            rdns = createRdns(p);
        }
        
        boolean isTrusted() {
            return trusted;
        }
        
        boolean isMinus() {
            return minus;
        }
        
        private Vector createRdns(String str) {
            Vector v = new Vector();
            String[] rdnsArr = Splitter.split(str, ',', 0);
            for (int i = 0; i < rdnsArr.length; i++) {
                rdnsArr[i] = rdnsArr[i].trim();
                if ("*".equals(rdnsArr[i]))
                    v.add(new String[] {"*", null});
                else {
                    int ioe = rdnsArr[i].indexOf("=");
                    String key = rdnsArr[i].substring(0, ioe).trim();
                    String value = rdnsArr[i].substring(ioe + 1).trim();
                    checkRdnKey(key);
                    checkRdnValue(value);
                    v.add(new String[] {key, value});
                }
            }
            return v;
        }
        
        // TODO
        private void checkRdnValue(String value) {
            for (int i = 0; i < value.length(); i++) {
                char ch = value.charAt(i);
                if (ch == '"' ||
                    ch == '\\')
                    throw new IllegalArgumentException("'" + ch + "' character is not allowed " +
                            "in this position");
            }
        }

        private void checkRdnKey(String key) {
            // check characters
            
            for (int i = 0; i < key.length(); i++) {
                char ch = key.charAt(i);
                if (ch >= 'a' && ch <= 'z')
                    continue;
                if (ch >= 'A' && ch <= 'Z')
                    continue;
                if (ch >= '0' && ch <= '9')
                    continue;
                if (ch == '-' || ch == '.')
                    continue;
                throw new IllegalArgumentException("'" + ch + "' character is not allowed " +
                        "in this position");
            }
            
            // check values
            if (!allowedKeys.contains(key.toLowerCase()))
                throw new IllegalArgumentException("Key (" + key + ") is not allowed");
        }

        public boolean match(String signer) {
            Vector v = createRdns(signer);
            boolean skip = false;
            int i = 0;
            int j = 0;
            while (i < rdns.size() && j < v.size()) {
                String pk = ((String[]) rdns.get(i))[0];
                String pv = ((String[]) rdns.get(i))[1];
                String sk = ((String[]) v.get(j))[0];
                String sv = ((String[]) v.get(j))[1];
                if (pk.equalsIgnoreCase(sk)) {
                    NamePattern np = new NamePattern(pv);
                    if (np.match(sv)) {
                        ++i;
                        ++j;
                        skip = false;
                        continue;
                    } 
                    if (skip) {
                        ++j;
                        continue;
                    }
                    return false;
                } else if ("*".equals(pk)) {
                    skip = true;
                    ++i;
                    continue;
                } else if (skip) {
                    ++j;
                } else {
                    return false;
                }
            }
            boolean ret = (skip && i == rdns.size()) || (i == rdns.size() && j == v.size());
            if (ret && trusted) { 
                try {
                    return checkInKeystore(signer);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return ret;
        }
    }

    private static boolean checkInKeystore(final String signer) throws Exception {
        try {
            return ((Boolean) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    if (null == keystore)
                        throw new RuntimeException("There is no keystore");
    		        Method m = keystore.getClass().getMethod("aliases", null);
    		        Enumeration aliases = (Enumeration) m.invoke(keystore, null);
    		        while (aliases.hasMoreElements()) {
    		            String alias = (String) aliases.nextElement();
    		            m = keystore.getClass().getMethod("getCertificate", new Class[] {String.class});
    		            Certificate cert = (Certificate) m.invoke(keystore, new Object[] {alias});
    		            m = cert.getClass().getMethod("getSubjectDN", null);
    		            Principal princ = (Principal) m.invoke(cert, null);
    		            SignerChainPattern.SignerPattern sp = 
    		                	new SignerChainPattern.SignerPattern(princ.toString());
    	            	return new Boolean(sp.match(signer));
    		        }
    		        return new Boolean(false);
                }
            })).booleanValue(); 
        } catch (PrivilegedActionException  e) {
            throw e.getException();
        }       
    }

}