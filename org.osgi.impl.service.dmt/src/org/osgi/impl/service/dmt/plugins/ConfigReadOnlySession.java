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

import java.util.*;
import org.osgi.service.cm.Configuration;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

class ConfigReadOnlySession implements ReadableDataSession {
    
    // Constants for the fixed node names in the tree
    protected static final String LOCATION    = "Location";
    protected static final String PID         = "Pid";
    protected static final String FACTORY_PID = "FactoryPid";
    protected static final String KEYS        = "Keys";
    protected static final String TYPE        = "Type";
    protected static final String CARDINALITY = "Cardinality";
    protected static final String VALUE       = "Value";
    protected static final String VALUES      = "Values";


    protected ConfigPlugin plugin;

    ConfigReadOnlySession(ConfigPlugin plugin) {
        this.plugin = plugin;
    }

    public void nodeChanged(String[] fullPath) throws DmtException {
        // do nothing - the version and timestamp properties are not supported
    }

    public void close() throws DmtException {
        // no cleanup needs to be done when closing read-only session
    }

    public String[] getChildNodeNames(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        if (path.length == 0)
            return getConfigurationIds(fullPath);
        
        Configuration configuration;
        try {
            configuration = plugin.getConfiguration(path[0]);
        } catch (ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
        
        if (path.length == 1) {
            List l = new ArrayList();
            l.add(PID);
            l.add(LOCATION);
            l.add(KEYS);
            if(configuration.getFactoryPid() != null)
                l.add(FACTORY_PID);
            return (String[]) l.toArray(new String[l.size()]);
        }
        
        Dictionary entries = configuration.getProperties();
        if(entries == null)
            // this shouldn't happen, empty configs must be ignored at lookup
            return new String[] {};
        
        if(path.length == 2) {
            List keyList = new ArrayList();
            Enumeration keys = entries.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if(!isEmptyVector(entries.get(key)))
                    keyList.add(key);
            }
            return (String[]) keyList.toArray(new String[keyList.size()]);
        }
        
        Object value = entries.get(path[2]);
        if(value == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        if(isEmptyVector(value))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");

        ConfigEntry entry = new ConfigEntry(value);
        
        if(path.length == 3) {
            String[] children = new String[3];
            children[0] = TYPE;
            children[1] = CARDINALITY;
            children[2] = 
                entry.getCardinality() == Cardinality.SCALAR ? VALUE : VALUES;
            return children;
        }
        
        Integer[] indices;
        try { // getIndexArray checks that the entry is non-scalar
            indices = entry.getIndexArray();
        } catch (ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
        String[] children = new String[indices.length];
        for(int i = 0; i < indices.length; i++)
            children[i] = indices[i].toString();

        return children;
    }

    public MetaNode getMetaNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
    
        if (path.length == 0) // ./OSGi/Configuration
            return new ConfigMetaNode(
                    "Root node of the configuration subtree.", 
                    MetaNode.PERMANENT, 
                    !ConfigMetaNode.CAN_ADD, !ConfigMetaNode.CAN_DELETE, 
                    !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE);
        
        if (path.length == 1) // ./OSGi/Configuration/<pid>
            return new ConfigMetaNode(
                    "Root node for a Configuration object.",
                    MetaNode.DYNAMIC,
                    ConfigMetaNode.CAN_ADD, ConfigMetaNode.CAN_DELETE, 
                    ConfigMetaNode.ALLOW_ZERO, ConfigMetaNode.ALLOW_INFINITE);
        
        if (path.length == 2) { // ./OSGi/Configuration/<pid>/...
            if(path[1].equals(LOCATION))
                return new ConfigMetaNode(
                        "Bundle location of the Configuration object.",
                        !ConfigMetaNode.CAN_DELETE, !ConfigMetaNode.CAN_REPLACE,
                        !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, DmtData.FORMAT_STRING, null);
            
            if(path[1].equals(PID))
                return new ConfigMetaNode(
                        "The PID of the Configuration object.",
                        !ConfigMetaNode.CAN_DELETE, !ConfigMetaNode.CAN_REPLACE,
                        !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, DmtData.FORMAT_STRING, null);
            
            if(path[1].equals(FACTORY_PID))
                return new ConfigMetaNode(
                        "The PID of the Managed Service Factory providing this Configuration.",
                        !ConfigMetaNode.CAN_DELETE, !ConfigMetaNode.CAN_REPLACE,
                        ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, DmtData.FORMAT_STRING, null);
            
            if(path[1].equals(KEYS))
                return new ConfigMetaNode(
                        "Root node for the configuration entries.",
                        MetaNode.AUTOMATIC, 
                        !ConfigMetaNode.CAN_ADD, !ConfigMetaNode.CAN_DELETE, 
                        !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE);
        
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
        }
        
        if(!path[1].equals(KEYS))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
        
        if (path.length == 3) // ./OSGi/Configuration/<pid>/Keys/<key>
            return new ConfigMetaNode(
                    "Root node for a configuration entry.",
                    MetaNode.DYNAMIC,  
                    ConfigMetaNode.CAN_ADD, ConfigMetaNode.CAN_DELETE, 
                    ConfigMetaNode.ALLOW_ZERO, ConfigMetaNode.ALLOW_INFINITE);
    
        if (path.length == 4) { // ./OSGi/Configuration/<pid>/Keys/<key>/...
            if (path[3].equals(TYPE))
                return new ConfigMetaNode("Data type of configuration value.",
                        !ConfigMetaNode.CAN_DELETE, !ConfigMetaNode.CAN_REPLACE,
                        !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, DmtData.FORMAT_STRING, 
                        Type.ALL_TYPE_DATA);
            
            if (path[3].equals(CARDINALITY))
                return new ConfigMetaNode(
                        "Cardinality of the configuration value.",
                        !ConfigMetaNode.CAN_DELETE, !ConfigMetaNode.CAN_REPLACE,
                        !ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, DmtData.FORMAT_STRING, 
                        Cardinality.ALL_CARDINALITY_DATA);
            
            if(path[3].equals(VALUE)) {
                int formats = DmtData.FORMAT_STRING | DmtData.FORMAT_BINARY
                        | DmtData.FORMAT_INTEGER | DmtData.FORMAT_BOOLEAN
                        | DmtData.FORMAT_FLOAT;
                return new ConfigMetaNode(
                        "Scalar configuration data.", 
                        !ConfigMetaNode.CAN_DELETE, ConfigMetaNode.CAN_REPLACE,
                        ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE,
                        !ConfigMetaNode.IS_INDEX, formats, null);
            }
            
            if(path[3].equals(VALUES))
                return new ConfigMetaNode(
                        "Root node for elements of a non-scalar configuration item.",
                        MetaNode.DYNAMIC,  
                        ConfigMetaNode.CAN_ADD, !ConfigMetaNode.CAN_DELETE, 
                        ConfigMetaNode.ALLOW_ZERO, !ConfigMetaNode.ALLOW_INFINITE);
        
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
        }
        
        if(!path[3].equals(VALUES))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
        
        if(path.length == 5) { // ./OSGi/Configuration/<pid>/Keys/<key>/Values/<index>
            int formats = DmtData.FORMAT_STRING | DmtData.FORMAT_INTEGER
                    | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_FLOAT;
        
            return new ConfigMetaNode(
                    "Data element for a non-scalar configuration item.", 
                    ConfigMetaNode.CAN_DELETE, ConfigMetaNode.CAN_REPLACE,
                    ConfigMetaNode.ALLOW_ZERO, ConfigMetaNode.ALLOW_INFINITE,
                    ConfigMetaNode.IS_INDEX, formats, null);
        }
    
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                "No such node defined in the configuration tree.");
    }

    public int getNodeSize(String[] fullPath) throws DmtException {
        return getNodeValue(fullPath).getSize();
    }

    public int getNodeVersion(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                "Version property not supported.");
    }

    public Date getNodeTimestamp(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                "Timestamp property not supported.");
    }

    public String getNodeTitle(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                "Title property not supported.");
    }

    public String getNodeType(String[] fullPath) throws DmtException {
        if(isLeafNode(fullPath))
            return ConfigMetaNode.LEAF_MIME_TYPE;
        
        String[] path = chopPath(fullPath);
        if(path.length == 0) // $/Configuration
            return ConfigMetaNode.CONFIGURATION_MO_TYPE;
        
        return null;
    }

    public boolean isNodeUri(String[] fullPath) {
        String[] path = chopPath(fullPath);

        if(path.length == 0) // $/Configuration
            return true;
        if(path.length > 5)
            return false;
        
        Configuration configuration;
        try {
            configuration = plugin.getConfiguration(path[0]);
        } catch (ConfigPluginException e) {
            return false;
        }
        
        if(configuration == null)
            return false;
        
        if(path.length == 1) // $/Configuration/<pid>
            return true;
        
        if(path.length == 2) {
            if(path[1].equals(FACTORY_PID))
                return configuration.getFactoryPid() != null;
            
            return path[1].equals(PID) || path[1].equals(LOCATION) 
                    || path[1].equals(KEYS);
        }
        
        if(!path[1].equals(KEYS))
            return false;
        
        Dictionary entries = configuration.getProperties();
        if(entries == null)
            return false; // no keys set in the configuration table
        
        Object value = entries.get(path[2]);
        if(value == null)
            return false; // no key with the given name
        
        if(isEmptyVector(value))
            return false; // empty vectors are not shown because type is unknown
        
        if(path.length == 3)
            return true;
        
        ConfigEntry entry = new ConfigEntry(value);
        
        if(path.length == 4) {
            if(path[3].equals(TYPE) || path[3].equals(CARDINALITY))
                return true;
            
            if(path[3].equals(VALUE))
                return isScalar(entry);
            
            if(path[3].equals(VALUES))
                return !isScalar(entry);
            
            return false;
        }
        
        if(!path[3].equals(VALUES))
            return false;
        
        try { // getElementAt checks that the entry is non-scalar
            return entry.getElementAt(Integer.parseInt(path[4])) != null;
        } catch(NumberFormatException e) {
            return false;
        } catch(ConfigPluginException e) {
            return false;
        }
    }

    public boolean isLeafNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        // not checking whether the node still exists - it did exist when 
        // isNodeUri was called on it, and if it got deleted, there will be an
        // error anyway when it is accessed
        return path.length == 2 && !path[1].equals(KEYS) 
            || path.length == 4 && !path[3].equals(VALUES)
            || path.length == 5;
    }

    public DmtData getNodeValue(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length > 1  because only leaf nodes are given
        Configuration configuration;
        try {
            configuration = plugin.getConfiguration(path[0]);
        } catch (ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
        
        if(path.length == 2) {
            if(path[1].equals(PID))
                return new DmtData(configuration.getPid());
            
            if(path[1].equals(LOCATION))
                return new DmtData(configuration.getBundleLocation());

            // path[1].equals(FACTORY_PID)
            String factoryPid = configuration.getFactoryPid();
            if(factoryPid == null)
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The given PID does not point to a factory configuration.");
            return new DmtData(factoryPid);
        }
        
        // path.length > 3
        Dictionary entries = configuration.getProperties();
        if(entries == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        Object value = entries.get(path[2]);
        if(value == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        if(isEmptyVector(value))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        ConfigEntry entry = new ConfigEntry(value);

        if(path.length == 4) {
            if(path[3].equals(TYPE))
                return entry.getType().getData();
                
            if(path[3].equals(CARDINALITY))
                return entry.getCardinality().getData();

            // path[3].equals(VALUE))
            
            if(!isScalar(entry))
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The specified key contains a non-scalar value.");
            
            return entry.getValue().getData();
        }
        
        // path.length == 5
        Value element;
        try { // getElementAt checks that the entry is non-scalar
            element = entry.getElementAt(Integer.parseInt(path[4]));
        } catch (ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
        
        if(element == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No element exists in the array/vector with the given index.");

        return element.getData();
    }

    
    //----- Utility methods -----//

    protected String[] getConfigurationIds(String[] fullPath) 
            throws DmtException {
        try {
            return plugin.listConfigurations();
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }        
    
    protected static String[] chopPath(String[] fullPath) {
        // DmtAdmin only gives us nodes in our subtree
        int rootLength = ConfigPluginActivator.PLUGIN_ROOT_PATH.length;
        String[] path = new String[fullPath.length-rootLength];
        System.arraycopy(fullPath, rootLength, path, 0, 
                fullPath.length-rootLength);
        return path;
    }
    
    protected static boolean isEmptyVector(Object value) {
        return value instanceof Vector && ((Vector)value).size() == 0;
    }
    
    /**
     * Determines whether the given fully defined entry contains scalar data.
     * This method does not work for ConfigEntries being created in the tree!
     * It should only be used when reading.
     * <p>
     * Determines whether the given value falls into the scalar or non-scalar
     * category in the configuration tree.  Returns <code>false</code> for all 
     * vectors, and for all arrays except <code>byte[]</code>.  The 
     * <code>true</code> return value indicates that the parameter is scalar,
     * but this only holds if the parameter is of a valid type.
     */
    private static boolean isScalar(ConfigEntry entry) {
        return entry.getCardinality() == Cardinality.SCALAR;
    }
}

