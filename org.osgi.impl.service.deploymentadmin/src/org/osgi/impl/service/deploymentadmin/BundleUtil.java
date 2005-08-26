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
import java.lang.reflect.Method;
import java.security.*;
import java.security.cert.Certificate;
import org.osgi.framework.Bundle;

public class BundleUtil {
    private Class bundleClazz;
    private Method getBundleData;
    private Class bundleDataClazz;
    private Method getBaseBundleFile;
    private Method getDataFile;
    private Class bundleFileClazz;
    private Method getSigningCertificates;
    private Method getSigningCertificateChains;

    /**
     * Returns the certificate chains that signed the bundle. Only
     * validated certificate chains are returned. Each element of the returned
     * array will contain a chain of distinguished names (DNs) separated by
     * semicolons. The first DN is the signer and the last is the root
     * Certificate Authority.
     * 
     * @param bundle the bundle object
     * @return the certificate chains that signed the bundle
     */
    public String[] getDNChains(Bundle bundle) {
        Object signedBundle = getSignedBundle(bundle);
        if (signedBundle == null || getSigningCertificateChains == null)
            return null;
        try {
            return (String[]) getSigningCertificateChains.invoke(signedBundle, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Retrieves the Certificates that signed this repository. Only validated
     * certificates are returned.
     * 
     * @param bundle the bundle object
     * @return the Certificates that signed this repository.
     */
    public Certificate[] getSignerCertificates(Bundle bundle) {
        Object signedBundle = getSignedBundle(bundle);
        if (signedBundle == null || getSigningCertificates == null)
            return null;
        try {
            return (Certificate[]) getSigningCertificates.invoke(signedBundle, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a <code>File</code> object for the persistent storage
     * area provided for the bundle by the Framework. This method will return
     * <code>null</code> if the platform does not have file system support.
     * @param bundle the bundle object
     * @return a <code>File</code> object for the persisten storage area for the bundle
     */
    public File getDataFile(Bundle bundle) {
        getSignedBundle(bundle);
        final Object bundleData = getBundleData(bundle);
        if (bundleData == null || getDataFile == null)
            return null;
        try {
            return (File) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception{
                    return getDataFile.invoke(bundleData, new String[] {""});
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getBundleData(Bundle bundle) {
        if (bundle.getBundleId() == 0)
            return null;
        if (bundleClazz == null)
            bundleClazz = bundle.getClass();
        if (getBundleData == null)
            try {
                getBundleData = bundleClazz.getMethod("getBundleData", new Class[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        try {
            return getBundleData.invoke(bundle, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object getSignedBundle(Bundle bundle) {
        Object bundleData = getBundleData(bundle);
        if (bundleData == null)
            return null;
        if (bundleDataClazz == null)
            bundleDataClazz =  bundleData.getClass();
        if (getBaseBundleFile == null || getDataFile == null)
            try {
                getBaseBundleFile = bundleDataClazz.getMethod("getBaseBundleFile", new Class[0]);
                getDataFile = bundleDataClazz.getMethod("getDataFile", new Class[] {String.class});
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        Object bundleFile;
        try {
            bundleFile = getBaseBundleFile.invoke(bundleData, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        bundleFileClazz = bundleFile.getClass();
        if (getSigningCertificateChains == null || getSigningCertificates == null)
            try {
                getSigningCertificateChains = bundleFileClazz.getMethod("getSigningCertificateChains", new Class[0]);
                getSigningCertificates = bundleFileClazz.getMethod("getSigningCertificates", new Class[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return bundleFile;
    }
}
