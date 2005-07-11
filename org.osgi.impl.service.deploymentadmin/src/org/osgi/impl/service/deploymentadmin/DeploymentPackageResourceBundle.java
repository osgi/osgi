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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

public class DeploymentPackageResourceBundle extends ResourceBundle implements Serializable {
    
    private transient int       maxFit = -1;
    private           Hashtable table = new Hashtable();
    
    public Enumeration getKeys() {
        return table.keys();
    }

    protected Object handleGetObject(String key) {
        return table.get(key);
    }
    
    void addPropertyFile(String name, ByteArrayOutputStream bos) throws IOException {
        int f = fitLocale(name);
        if (f <= maxFit)
            return;
        maxFit = f;
        table = new Hashtable();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(
            new ByteArrayInputStream(bos.toByteArray())));
        try {
	        String line = br.readLine();
	        while (null != line) {
	            processLine(line);
	            line = br.readLine();
	        }
        } finally {
            if (null != br)
                try {
                    br.close();
                }
                catch (IOException e) {
                }
        }
    }

    private int fitLocale(String name) {
        for (int i = 3; i >= 0; --i) {
            if (name.endsWith(getSuffix(i)))
                return i;
        }
        return -1;
    }
    
    private String getSuffix(int fc) {
        Locale locale = Locale.getDefault();
        StringBuffer ret = new StringBuffer(".properties");
        if (0 == fc)
            return ret.toString();
        String l = locale.getLanguage();
        if (l.length() != 0)
            ret.insert(0, "_" + l);
        if (1 == fc)
            return ret.toString();
        String c = locale.getCountry();
        if (c.length() != 0)
            ret.insert(3, "_" + c);
        if (2 == fc)
            return ret.toString();
        String v = locale.getVariant();
        if (v.length() != 0)
            ret.insert(6, "_" + v);
        return ret.toString();
    }

    private void processLine(String line) {
        if (line.startsWith("#"))
            return;
        if (line.trim().length() == 0)
            return;
        int eqPos = line.indexOf("=");
        String key = line.substring(0, eqPos);
        String value = line.substring(eqPos + 1);
        table.put(key, value);
    }
       
}