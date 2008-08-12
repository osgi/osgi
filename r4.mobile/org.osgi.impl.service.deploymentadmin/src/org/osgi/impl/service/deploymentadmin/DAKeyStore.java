/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;

import org.osgi.framework.BundleContext;

public class DAKeyStore {
    
    private KeyStore keystore;
    private Logger   logger;
    
    private static DAKeyStore DA_KEYSTORE;
    
    public static DAKeyStore getKeyStore() throws Exception {
        if (null == DA_KEYSTORE) {
            DA_KEYSTORE = new DAKeyStore();
            DA_KEYSTORE.logger = Logger.getLogger();
            DA_KEYSTORE.init();
        }
        
        return DA_KEYSTORE;
    }
    
    public static DAKeyStore getKeyStore(BundleContext context) throws Exception {
        if (null == context)
            throw new IllegalArgumentException();
        
        if (null == DA_KEYSTORE) {
            DA_KEYSTORE = new DAKeyStore();
            DA_KEYSTORE.logger = Logger.getLogger(context);
            DA_KEYSTORE.init();
        } else 
            DA_KEYSTORE.logger = Logger.getLogger(context);
        
        return DA_KEYSTORE;
    }

    private void init() throws Exception {
        // get the keystore file
        
        String ksFile = System.getProperty(DAConstants.KEYSTORE_PATH);
        if (null == ksFile) {
            String sUrl = System.getProperty(DAConstants.KEYSTORE_FW_URL);
            if (null != sUrl) {
                ksFile = new URL(sUrl).getFile();
                logger.log(Logger.LOG_INFO, "Keystore location is not defined. Set the " + 
                        DAConstants.KEYSTORE_PATH + " system property! Framework keystore will " +
                        "be used (" + ksFile + ").");
            }
        }
        if (null == ksFile) {
            File f = new File(System.getProperty("user.home"));
            f = new File(f, ".keystore");
            if (f.exists()) {
                ksFile = f.getPath();
                logger.log(Logger.LOG_INFO, "Keystore location is not defined. Set the " + 
                        DAConstants.KEYSTORE_PATH + " system property! " + ksFile + 
                        " will be used.");
            }
        }
        if (null == ksFile || !(new File(ksFile).exists())) {
            logger.log(Logger.LOG_WARNING, "Keystore location is not defined. Set the " + 
                    DAConstants.KEYSTORE_PATH + " system property! Deployment Admin will " +
                    "not work properly.");
            return;
        }

        // get the keystore pwd
        
        String pwd = System.getProperty(DAConstants.KEYSTORE_PWD);
        if (null == pwd)
            logger.log(Logger.LOG_INFO, "There is no keystore password set. Set the " +
                    DAConstants.KEYSTORE_PWD + " system property! Keystore integrity will " +
                    "not be checked.");
        
        // get the keystore type
        
        String ksType = System.getProperty(DAConstants.KEYSTORE_TYPE);
        if (null == ksType) {
            ksType = "JKS";
            logger.log(Logger.LOG_INFO, "There is no keystore type set. Set the " +
                    DAConstants.KEYSTORE_TYPE + " system property! Default keystore type " +
                    "(JKS) will be used.");
        }
        
        // load the keystore
        
        keystore = KeyStore.getInstance(ksType);
        InputStream is = new FileInputStream(new File(ksFile));
        try {
            keystore.load(is, null == pwd ? null : pwd.toCharArray());
        } catch (Exception e) {
            keystore = null;
            logger.log(Logger.LOG_WARNING, "Cannot load the keystore (" + 
                    ksFile + ") Deployment Admin will " +
                    "not work properly");
        }
        finally {
            if (null != is)
                is.close();
        }
    }

    public String getCertificateAlias(Certificate cert) throws KeyStoreException {
        if (null == keystore)
            return null;
        return keystore.getCertificateAlias(cert);
    }

    public Certificate getCertificate(String alias) throws KeyStoreException {
        if (null == keystore)
            return null;
        return keystore.getCertificate(alias);
    }

}
