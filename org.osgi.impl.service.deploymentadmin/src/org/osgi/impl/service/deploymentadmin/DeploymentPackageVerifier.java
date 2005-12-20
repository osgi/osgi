package org.osgi.impl.service.deploymentadmin;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;

/*
 * Checks whether the deployment package is valid.
 */
public class DeploymentPackageVerifier {
    
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
        checkResourceName(be.getResName());
        
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
    
    private void checkResourceName(String name) throws DeploymentException {
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch >= 'a' && ch <= 'z')
                continue;
            if (ch >= 'A' && ch <= 'Z')
                continue;
            if (ch >= '0' && ch <= '9')
                continue;
            if (ch == '_' || ch == '.' || ch == '-' || ch == '/')
                continue;
            throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                    "Character '" + ch + "' is not allowed in resource names");
        }
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
