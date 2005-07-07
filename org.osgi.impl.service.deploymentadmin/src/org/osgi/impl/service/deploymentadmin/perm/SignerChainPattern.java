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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.Splitter;


class SignerChainPattern {

    private static Object keystore;
    static {
        try {
            String ksType = (String) AccessController.doPrivileged(new PrivilegedAction() {
	                public Object run() {
	                    return System.getProperty(DAConstants.KEYSTORE_TYPE);
	                }
                }); 
            if (null == ksType)
                ksType = "JKS";
            String ksPath = (String) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(DAConstants.KEYSTORE_PATH);
                }
            }); 
            if (null == ksPath)
                throw new RuntimeException("Keystore path is not defined. Set the " +
                        DAConstants.KEYSTORE_PATH + " system property!");
            File file = new File(ksPath);
            if (!file.exists())
                throw new RuntimeException("Keystore is not found: " + file);
            String pwd = (String) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(DAConstants.KEYSTORE_PWD);
                }
            }); 
            if (null == pwd)
                throw new RuntimeException("There is no keystore password. Set the " +
                        DAConstants.KEYSTORE_PWD + " system property!");
            
            final String ksTypeF = ksType;
            final String pwdF = pwd;
            final File fileF = file;
            try {
	            AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                public Object run() throws Exception {
	                    Class c = Class.forName("java.security.KeyStore");
	                    Method m = c.getDeclaredMethod("getInstance", new Class[] {String.class});
	                    keystore = m.invoke(null, new Object[] {ksTypeF});
	                    m = keystore.getClass().getDeclaredMethod("load", new Class[] {InputStream.class,
	                            char[].class});
	                    m.invoke(keystore, new Object[] {new FileInputStream(fileF), pwdF.toCharArray()});
	                    return null;
	                }
	            }); 
            } catch (PrivilegedActionException  e) {
                throw e.getException();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
                    v.add(new String[] {key, value});
                }
            }
            return v;
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
                    // TODO
                    e.printStackTrace();
                }
            }
            return ret;
        }
    }

    private static boolean checkInKeystore(final String signer) throws Exception {
        try {
            return ((Boolean) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
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