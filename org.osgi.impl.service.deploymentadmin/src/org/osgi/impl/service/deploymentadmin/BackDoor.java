/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * (c) Copyright 2005 IBM
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

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.*;

import org.eclipse.osgi.internal.provisional.verifier.CertificateChain;
import org.eclipse.osgi.internal.provisional.verifier.CertificateVerifier;
import org.eclipse.osgi.internal.provisional.verifier.CertificateVerifierFactory;
import org.eclipse.osgi.service.urlconversion.URLConverter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Framework backdoors are implemented here 
 */
public class BackDoor {
	
	public static Filter               FILTER_URL_CONVERTER;
	public static Filter               FILTER_CERT_VER_FACT;
	
	private BundleContext              context;
	private TrackerCertVerifierFactory trackCertVerFact;
	private TrackerURLConverter        trackURLConverter;


	public BackDoor(BundleContext context) {
		this.context = context;
		try {
			FILTER_URL_CONVERTER = BackDoor.this.context.createFilter(
					"(&(" + Constants.OBJECTCLASS + "=" + URLConverter.class.getName() + 
					")(protocol=bundleentry))");
			FILTER_CERT_VER_FACT = BackDoor.this.context.createFilter(
					"(" + Constants.OBJECTCLASS + "=" + CertificateVerifierFactory.class.getName() + ")");
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException("Internal error");
		}
				
		trackCertVerFact = new TrackerCertVerifierFactory();
		trackCertVerFact.open();
		trackURLConverter = new TrackerURLConverter();
		trackURLConverter.open();
	}
	
	/*
     * Class to track the CertificateVerifierFactory
     */
    private class TrackerCertVerifierFactory extends ServiceTracker {
        public TrackerCertVerifierFactory() {
            super(BackDoor.this.context, FILTER_CERT_VER_FACT, null);
        }
    }

    /*
     * Class to track the URLConverter
     */
    private class TrackerURLConverter extends ServiceTracker {
    	public TrackerURLConverter() {
    		super(BackDoor.this.context, FILTER_URL_CONVERTER, null);
    	}
    }
    
    public void destroy() {
    	trackCertVerFact.close();
    	trackURLConverter.close();
    }

    public File getDataFile(final Bundle b) {
		return (File) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				File ret = null;
				try {
					Method m;
					m = b.getClass().getMethod("getBundleData", new Class[] {});
					Object bundleData = m.invoke(b, new Object[] {});
					m = bundleData.getClass().getMethod("getDataFile",
							new Class[] {String.class});
					ret = (File) m.invoke(bundleData, new String[] {""});
				} catch (Exception e) {
				}
				return ret;
			}
		});
	}

	public InputStream getBundleStream(Bundle bundle) {
		URLConverter converter = (URLConverter) trackURLConverter.getService();
		if (null == converter)
			return null;
		URL root = bundle.getEntry("");
		try {
			root = converter.resolve(root);
			if (!"jar".equals(root.getProtocol())) {
				// nothing we can do if it is not a jar URL
				throw new IOException("Bad bundle root URL: " + root.toExternalForm());
			}
			String bundlePath = root.getPath();
			// strip out the file: and !/
			bundlePath = bundlePath.substring(5, bundlePath.lastIndexOf('!'));
			return new FileInputStream(new File(bundlePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getDNChains(Bundle b) {
		CertificateVerifierFactory cvf = (CertificateVerifierFactory) trackCertVerFact.getService();
		if (null == cvf)
			return null;
		CertificateVerifier cv;
		try {
			cv = cvf.getVerifier(b);
		} catch (IOException e) {
			return null;
		}
		if (null == cv)
			return null;
		CertificateChain[] chains = cv.getChains();
		if (null == chains)
			return null;
		String[] ret = new String[chains.length];
		for (int i = 0; i < chains.length; i++)
			ret[i] = chains[i].getChain();
		return ret;
	}

}
