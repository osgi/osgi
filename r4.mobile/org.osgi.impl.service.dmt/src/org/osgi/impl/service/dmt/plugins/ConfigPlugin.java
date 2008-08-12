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

import info.dmtree.*;
import info.dmtree.spi.*;

import java.io.IOException;
import java.util.*;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.*;
import org.osgi.util.tracker.ServiceTracker;

class ConfigPlugin implements DataPlugin, ManagedService {
    private ServiceTracker configTracker;

    /**
     * Manages the different IDs related to the configuration tables.  Stores 
     * the mapping between the factory configuration IDs (node names) specified
     * by the management server and the service PIDs assigned by the 
     * Configuration Admin implementation.  Also handles mangling of long PIDs.
     * <p>
     * The mapping must always be one-to-one, care must be taken that no entry 
     * is added where the value is already in the table.
     */
    private ConfigIdHandler idHandler;

    ConfigPlugin(ServiceTracker configTracker, ServiceTracker logTracker) {
        this.configTracker = configTracker;
        
        idHandler = new ConfigIdHandler(configTracker, logTracker);
    }

    
    //----- DataPlugin methods -----//

    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return new ConfigReadOnlySession(this);
    }

    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return null; // non-atomic write sessions not supported
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        
        if(sessionRoot.length > 
                ConfigPluginActivator.PLUGIN_ROOT_PATH.length + 1)
            throw new DmtException(sessionRoot, DmtException.COMMAND_FAILED,
                    "Fine-grained locking not supported, session subtree " +
                    "must contain at least one whole configuration table.");

        return new ConfigReadWriteSession(this);
    }


    //----- ManagedService methods -----//

    public synchronized void updated(Dictionary properties)
            throws ConfigurationException {
        idHandler.updated(properties);
    }


    //----- methods for the plugin session classes -----//

    synchronized Configuration createConfiguration(String nodeName, String pid, 
            String location, String factoryPid) throws ConfigPluginException {
        
        ConfigurationAdmin ca = (ConfigurationAdmin) configTracker.getService();
        if(ca == null)
            throw new ConfigPluginException(DmtException.COMMAND_FAILED,
                    "Cannot create configuration because Configuration Admin " +
                    "service is not registered.");
        
        // ENHANCE handle case when mangled PID eclipses the real PID (possibly factory generated)
        Configuration configuration;
        if(factoryPid != null) {
            try {
                configuration = 
                    ca.createFactoryConfiguration(factoryPid, location);
            } catch(IOException e) {
                throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                        "Error creating the factory configuration with the " +
                        "given PID.");
            }
            try {
                if(!idHandler.addMapping(nodeName, configuration.getPid()))
                    throw new ConfigPluginException(DmtException.COMMAND_FAILED,
                            "Cannot create configuration, node name or PID " +
                            "conflicts with an existing configuration.");
            } catch(IOException e) {
                throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                        "Error updating persistent ID mapping table.");
            }
        } else {
            if(pid == null)
                // this would normally require unescaping, but \ and / are not 
                // allowed in PIDs, so it will result in an error anyway
                pid = nodeName;
            else if(!ConfigIdHandler.matchingId(nodeName, pid))
                throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                        "Invalid PID specified, the name of the configuration " +
                        "root node must be equal to the mangled PID.");
            
            if(getConfigurationByPid(pid) != null)
                throw new ConfigPluginException(DmtException.CONCURRENT_ACCESS,
                        "The configuration table for the given PID has been " +
                        "created outside the scope of the session.");
            
            try {
                configuration = ca.getConfiguration(pid, location);
            } catch(IOException e) {
                throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                        "Error creating the configuration with the given PID");
            }
        }
        
        return configuration;
    }
    
    synchronized Configuration deleteConfiguration(String nodeName) 
            throws ConfigPluginException {
        Configuration configuration = findConfiguration(nodeName, true);
        if(configuration == null)
            throw new ConfigPluginException(DmtException.CONCURRENT_ACCESS,
                    "The configuration table for the given PID has " +
                    "been deleted outside the scope of the session.");
        try {
            configuration.delete();
        } catch (IOException e) {
            throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                    "Error deleting the configuration table with the given " +
                    "PID.");
        }
        return configuration;
    }
    
    synchronized Configuration getConfiguration(String nodeName) 
            throws ConfigPluginException {
        Configuration configuration = findConfiguration(nodeName, false);
        if(configuration == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "There is no configuration data for the given PID.");
        return configuration;
    }

    synchronized String[] listConfigurations() throws ConfigPluginException {
        Configuration[] configs = getConfigurations();
        
        if(configs == null)
            return new String[] {};
        
        Set pids = new HashSet();
        for (int i = 0; i < configs.length; i++)
            pids.add(configs[i].getPid());

        try { // remove maps for all PIDs not in the list
            idHandler.cleanupMap(pids);
        } catch (IOException e) {
            throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                    "Error updating persistent ID mapping table.");
        }

        String[] nodeNames = new String[pids.size()];
        Iterator iter = pids.iterator();
        for(int i = 0; iter.hasNext(); i++)
            nodeNames[i] = idHandler.getNodeNameForPid((String) iter.next());

        return nodeNames;
    }
    

    //----- private utility methods -----//

    // ENHANCE handle case when a PID mapping eclipses a real PID
    // ENHANCE handle case when mangled PID eclipses real PID
    private Configuration findConfiguration(String nodeName,
            boolean removeMapping) throws ConfigPluginException {
        String pid = idHandler.findMappedPidByNodeName(nodeName);
        if(pid != null) { // a mapped pid of a factory configuration
            Configuration config = getConfigurationByPid(pid);

            try {
                if(config != null) {
                    if(removeMapping) // if service is found but will be removed
                        idHandler.removeMapping(nodeName);
                    
                    // if there is a match, there must be exactly one
                    return config; 
                }
    
                // remove ID from mapping if service is not registered any more,
                // and fall back to looking for the node name itself
                idHandler.removeMapping(nodeName);
            } catch(IOException e) {
                throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                        "Error updating persistent ID mapping table.");
            }
        }
        
        // there is no valid mapping stored for the requested name
        
        Configuration[] configs = getConfigurations();
        
        if(configs == null)
            return null;
        
        for(int i = 0; i < configs.length; i++)
            if(ConfigIdHandler.matchingId(nodeName, configs[i].getPid()))
                return configs[i];
            
        return null;
    }
    
    private Configuration getConfigurationByPid(String pid) 
            throws ConfigPluginException {
        ConfigurationAdmin ca = (ConfigurationAdmin) configTracker.getService();
        if(ca == null)
            return null;
        
        Configuration[] configs = null;
        try {
            configs = ca.listConfigurations("(service.pid=" + 
                    escapeFilterValue(pid) + ")");
        } catch (IOException e) {
            throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                    "Error looking up the configuration with the given PID.");
        } catch (InvalidSyntaxException e) {
            // should not happen, the outside parameter (pid) is escaped
            throw new IllegalStateException("Syntax error in configuration " +
                    "lookup: " + e.getMessage());
        }

        if(configs == null)
            return null;
        
        return configs[0]; // if there is a match, there must be exactly one
    }
    
    private Configuration[] getConfigurations() throws ConfigPluginException {
        ConfigurationAdmin ca = (ConfigurationAdmin) configTracker.getService();
        if(ca == null)
            return null;
        
        try {
            return ca.listConfigurations(null);
        } catch (IOException e) {
            throw new ConfigPluginException(DmtException.DATA_STORE_FAILURE,
                    "Error listing configurations.");
        } catch (InvalidSyntaxException e) {
            // cannot happen with a null filter
            return null;
        }
    }
    
    private static String escapeFilterValue(String pid) {
        StringBuffer sb = new StringBuffer(pid);
        int i = 0;
        while(i < sb.length()) { // length of buffer may change in loop
            if("()*".indexOf(sb.charAt(i)) != -1)
                sb.insert(i++, '\\');
            i++;
        }
        return sb.toString();
    }
}
