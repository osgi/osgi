/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ===========   ==============================================================
 * Apr 25, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.util;

import org.osgi.framework.Version;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;

/**
 * @author Andre Assad
 *
 */
public class TestingDeploymentPackage {
	
	private String name;
	private Version version;
	private String versionString;
	private String filename;
	private TestingBundle[] bundles;
	private TestingResource[] resources;	
	private String signer;
	
	public TestingDeploymentPackage(String name, String version, String filename, TestingBundle[] bundles) {
		this(name,version,filename,bundles,null);
	}
	
	public TestingDeploymentPackage(String name, String version, String filename, TestingBundle[] bundles, TestingResource[] resources) {
		this.name = name;
		this.version = (version!=null && version.length()>0)?new Version(version):null;
		this.versionString = version;
		this.filename = filename;
		this.bundles = bundles;
		this.signer = DeploymentConstants.SIGNER_FILTER;
		this.resources = resources;
	}	
	
	/**
	 * @return Returns the bundles.
	 */
	public TestingBundle[] getBundles() {
		return bundles;
	}
	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Returns the version.
	 */
	public Version getVersion() {
		return version;
	}
	/**
	 * @return Returns the signer.
	 */
	public String getSigner() {
		return signer;
	}
	/**
	 * @return Returns the versionString.
	 */
	public String getVersionString() {
		return versionString;
	}
	/**
	 * @return Returns the resources.
	 */
	public TestingResource[] getResources() {
		return resources;
	}
    
    public TestingBundle getBundle(String bsn) {
        if (bundles == null)
            return null;
        for (int i = 0; i < bundles.length; i++) {
            if (bsn.equals(bundles[i].getName()))
                return bundles[i];
        }
        return null;
    }
}
