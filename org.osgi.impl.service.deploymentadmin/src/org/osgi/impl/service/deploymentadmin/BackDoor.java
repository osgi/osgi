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
import java.net.URL;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	private BundleContext              context;
	private TrackerURLConverter        trackURLConverter;


	public BackDoor(BundleContext context) {
		this.context = context;
		try {
			FILTER_URL_CONVERTER = BackDoor.this.context.createFilter(
					"(&(" + Constants.OBJECTCLASS + "=" + URLConverter.class.getName() + 
					")(protocol=bundleentry))");
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException("Internal error");
		}
				
		trackURLConverter = new TrackerURLConverter();
		trackURLConverter.open();
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
    	trackURLConverter.close();
    }

    public File getDataFile(final Bundle b) {
		return (File) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return b.getDataFile("");
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
		Map signers = b.getSignerCertificates(Bundle.SIGNERS_ALL);
		if (signers.isEmpty()) {
			return null;
		}
		List ret = new ArrayList(signers.size());
		for (Iterator iChains = signers.values().iterator(); iChains.hasNext();) {
			ret.add(getChain((List) iChains.next()));
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	private String getChain(List chain) {
		StringBuffer sb = new StringBuffer();
		for (Iterator iChain = chain.iterator(); iChain.hasNext();) {
			X509Certificate cert = (X509Certificate) iChain.next();
			sb.append(cert.getSubjectDN().getName());
			if (iChain.hasNext()) {
				sb.append("; ");
			}
		}
		return sb.toString();
	}
}
