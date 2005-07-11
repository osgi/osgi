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

package org.osgi.impl.service.deploymentadmin;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.*;


public class CaseInsensitiveMap extends Hashtable {
    
    private DeploymentPackageImpl dp;

    // TODO make other methods case insensitive

    public CaseInsensitiveMap(Map other, DeploymentPackageImpl dp) {
        this.dp = dp;
        fill(other);
    }

    private void fill(Map map) {
        if (null == map)
            return;
        
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            if (!(key instanceof String) && !(key instanceof Attributes.Name))
                throw new IllegalArgumentException("Only String and java.util.jar.Attributes.Name " +
                		"keys are allowed in the parameter map");
            put(key.toString().toUpperCase(), map.get(key));
        }
    }

    public Object put(Object key, Object value) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String skey = ((String) key).toUpperCase(); 
        return super.put(skey, value);
    }
    
    public Object get(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String skey = ((String) key).toUpperCase(); 
        String rawValue = (String) super.get(skey);
        if (null == rawValue)
            return null;
        if (null == dp.getResourceBundle())
            return rawValue;
        if (rawValue.startsWith("%")) {
            return dp.getResourceBundle().getString(rawValue.substring(1));
        }
        return rawValue;
    }
    
}