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
package org.osgi.impl.service.dmt.plugins;

import java.io.*;
import java.util.*;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import info.dmtree.Uri;

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

class ConfigIdHandler {
    private static final String ID_MAP_KEY = "IDmap";
    
    private ServiceTracker configTracker;
    private ServiceTracker logTracker;
    private Hashtable idMap;
    
    ConfigIdHandler(ServiceTracker configTracker, ServiceTracker logTracker) {
        this.configTracker = configTracker;
        this.logTracker = logTracker;
        idMap = new Hashtable();
    }
    
    void updated(Dictionary properties) throws ConfigurationException {
        if (properties == null) {
            idMap = new Hashtable();
            return;
        }
        byte[] bytes = (byte[]) properties.get(ID_MAP_KEY);
        if (bytes == null) {
            idMap = new Hashtable();
            return;
        }
        ObjectInputStream stream;
        try {
            stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            idMap = (Hashtable) stream.readObject();
        } catch (Exception e) {
            throw new ConfigurationException(ID_MAP_KEY,
                    "Unable to deserialize ID mapping table.", e);
        }
    }
    
    void update() throws IOException {
        ConfigurationAdmin ca = (ConfigurationAdmin) configTracker.getService();
        if(ca == null) {
            log(LogService.LOG_WARNING, "Cannot find Configuration Admin " +
                    "service, not persisting ID mapping.", null);
            return; 
        }

        Configuration config = ca.getConfiguration(
                ConfigPluginActivator.DMT_CONFIG_PLUGIN_SERVICE_PID);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        new ObjectOutputStream(stream).writeObject(idMap);
        stream.flush();
        byte[] bytes = stream.toByteArray();
        stream.close();
        
        Hashtable properties = new Hashtable();
        properties.put(ID_MAP_KEY, bytes);
        config.update(properties);
    }

    String findMappedPidByNodeName(String nodeName) {
        return (String) idMap.get(nodeName);
    }
    
    String getNodeNameForPid(String pid) {
        Iterator i = idMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            if(pid.equals(entry.getValue()))
                return (String) entry.getKey(); 
        }
        return Uri.mangle(pid);
    }
    
    void removeMapping(String nodeName) throws IOException {
        if(idMap.remove(nodeName) != null)
            update();
    }
    
    // returns false if the nodeName or value already existed in the table
    boolean addMapping(String nodeName, String pid) throws IOException {
        if(idMap.containsKey(nodeName) || idMap.containsValue(pid))
            return false;
        
        idMap.put(nodeName, pid);
        update();
        return true;
    }

    void cleanupMap(Set pids) throws IOException {
        boolean dirty = false;
        Iterator i = idMap.entrySet().iterator();
        while (i.hasNext())
            if(!pids.contains(((Map.Entry) i.next()).getValue())) {
                i.remove();
                dirty = true;
            }
        if(dirty)
            update();
    }
    
    private boolean log(int severity, String message, Throwable throwable) {
        LogService logService = (LogService) logTracker.getService();
        
        if (logService != null)
            logService.log(severity, message, throwable);
        
        return logService != null;
    }

    
    static boolean matchingId(String nodeName, String pid) {
        return Uri.mangle(pid).equals(nodeName);
    }
}
