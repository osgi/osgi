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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Aug 25, 2005 Andre Assad
 * 147          Rework after formal inspection 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tbc.util;

import org.osgi.framework.Version;


/**
 * @author Andre Assad
 * 
 * Supports Management Objects testing
 *
 */
public class TestingBundle {
    
    private String bsn;
    private String filename;
    private Version version;
    
    public TestingBundle(String bsn, String version,String filename) {
        this.bsn = bsn;
        this.filename = filename;
        this.version = (version!=null && version.length()>0)?new Version(version):null;
    }
    /**
     * @return Returns the bsn.
     */
    public String getBsn() {
        return bsn;
    }
    /**
     * @return Returns the filename.
     */
    public String getFilename() {
        return filename;
    }
    /**
     * @return Returns the version.
     */
    public Version getVersion() {
        return version;
    }
}
