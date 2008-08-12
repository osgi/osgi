package org.osgi.impl.service.deploymentadmin;

import java.security.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.deploymentadmin.DeploymentException;

/*
 * Checks whether the deployment package is valid.
 */
public class DeploymentPackageVerifier {
	
	// allowed chars in DeploymentPackage-SymbolicName values
	String allowedDpSnChars = "abcdefghijklmnopqrstuvwxyz" +
	                          "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + 
	                          "0123456789" + 
	                          "-_./";
    
	// allowed chars in resource names
	String allowedResNameChars = "abcdefghijklmnopqrstuvwxyz" +
	                             "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
	                             "0123456789" +
	                             "-_./";
	
    private DeploymentPackageCtx  dpCtx;
    private DeploymentPackageImpl dp;
    
    DeploymentPackageVerifier(DeploymentPackageImpl dp) {
    	this.dpCtx = dp.getDpCtx();
    	this.dp = dp;
    }

	public void verify() throws DeploymentException {
        checkMainSection();            
        checkNameSections();
    }
    
    private void checkMainSection()
    		throws DeploymentException
    {
        if (null == dp.getName())
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                    "Missing deployment package name");
        checkHeaderChars(DAConstants.DP_NAME, dp.getName(), allowedDpSnChars);
        
        if (null == dp.getVersion())
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                    "Missing deployment package version in: " + dp.getName());
        
        try {
            String s = (String) dp.getMainSection().get(DAConstants.DP_VERSION);
            new Version(s);
        }
        catch (Exception e) {
            throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                    "Bad version in: " + dp, e);
        }
        
        if (null != dp.getFixPackRange()) {
            try {
                String s = (String) dp.getMainSection().get(DAConstants.DP_FIXPACK);
                new VersionRange(s);
            }
            catch (Exception e) {
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                        "Bad version range in: " + dp, e);
            }
        }
        
        if (null == dp.getName())
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header in: " + dp + " header: " + DAConstants.DP_NAME);
     
        if (null == dp.getVersion())
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header in: " + dp + " header: " + DAConstants.DP_VERSION);        
    }

	private void checkNameSections()
    		throws DeploymentException
    {
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            checkBundleEntry(dp, be);
        }
        
        for (Iterator iter = dp.getResourceEntries().iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            checkResourceEntry(dp, re);
        }
        
        // this check only inside one dp
        Set s = new HashSet();
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (s.contains(be.getSymbName()))
                throw new DeploymentException(
                        DeploymentException.CODE_BUNDLE_SHARING_VIOLATION,
                        "Bundle with symbolic name \"" + be.getSymbName() + "\" " +
                        "is allready present in the system");
            s.add(be.getSymbName());
        }
    }

    private void checkBundleEntry(DeploymentPackageImpl dp, BundleEntry be) 
			throws DeploymentException
    {
    	checkHeaderChars(DAConstants.RES_NAME, be.getResName(), allowedResNameChars);
        
        if (null == dp.getFixPackRange() && be.isMissing())
            	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                    DAConstants.MISSING + " header is only allowed in fix-pack (" +
                    dp + ")");
        
        // among DPs?
        Set bundlesInDps = new HashSet();
        for (Iterator iter = dpCtx.getDeploymentPackages().iterator(); iter.hasNext();) {
            DeploymentPackageImpl eDp = (DeploymentPackageImpl) iter.next();

            for (Iterator iterDp = eDp.getBundleEntries().iterator(); iterDp.hasNext();) {
                BundleEntry	eBe	= (BundleEntry) iterDp.next();
                bundlesInDps.add(DeploymentAdminImpl.location(
                		eBe.getSymbName(), eBe.getVersion()));

                // it's the DP is going to be updated
                if (dp.getName().equals(eDp.getName()))
                    continue;

                if (be.getSymbName().equals(eBe.getSymbName()))
                    throw new DeploymentException(DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, 
                            "Bundle " + be + " is already installed by the package " + eDp);
            }
        }
        // standalone bundle with the same location?
        Bundle[] bundles  = (Bundle[]) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return dpCtx.getBundleContext().getBundles();
			}});
        String location = DeploymentAdminImpl.location(be.getSymbName(), be.getVersion());
        for (int i = 0; i < bundles.length; i++) {
        	final Bundle bundle = bundles[i];
        	String l = (String) AccessController.doPrivileged(new PrivilegedAction() {
        		public Object run() {
        			return bundle.getLocation();
        		}
        	});

        	// it is not standalone
        	if (bundlesInDps.contains(l))
        		continue;

			if (l.equals(location))
				throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
						"Standalone bundle " + bundles[i].getBundleId() + 
						" already uses location '" + location + "'.");
		}
    }
    
	private void checkHeaderChars(String header, String value, String allowed) 
			throws DeploymentException 
	{
		if (!checkAllowedChars(value, allowed))
			throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
					"Illegal character in '" + header + "' header");
	}

    private boolean checkAllowedChars(String str, String allowed) 
    		throws DeploymentException 
    {
    	for (int i = 0; i < str.length(); i++) {
    		String ch = "" + str.charAt(i);
    		if (allowed.indexOf(ch) == -1)
    			return false;
    	}
    	return true;
	}

    private void checkResourceEntry(DeploymentPackageImpl dp, ResourceEntry re) 
    		throws DeploymentException 
    {
        if (null == dp.getFixPackRange() && re.isMissing())
        	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                DAConstants.MISSING + " header is only allowed in fix-pack (" +
                dp + ")");
    }

}
