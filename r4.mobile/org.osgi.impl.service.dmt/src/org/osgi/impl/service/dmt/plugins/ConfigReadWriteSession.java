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
import info.dmtree.spi.TransactionalDataSession;

import java.io.IOException;
import java.util.*;

import org.osgi.service.cm.Configuration;

class ConfigReadWriteSession extends ConfigReadOnlySession
        implements TransactionalDataSession {

    private Vector changes;
    private HashSet storedConfigurationIdSet;
    
    private String concurrentAccessError;
    
    ConfigReadWriteSession(ConfigPlugin plugin) {
        super(plugin);
        
        init();
    }

    public void commit() throws DmtException {
        if(concurrentAccessError != null)
            throw new DmtException((String)null, DmtException.CONCURRENT_ACCESS,
                    concurrentAccessError);

        // This is an expensive check to detect concurrent modifications on the
        // list of registered Configuration tables.
        if(storedConfigurationIdSet != null && !storedConfigurationIdSet.equals(
                new HashSet(Arrays.asList(super.getConfigurationIds(null)))))
            throw new DmtException((String) null, 
                    DmtException.CONCURRENT_ACCESS, "Configuration table " +
                    "list was changed outside the scope of the session.");

        String[] root = ConfigPluginActivator.PLUGIN_ROOT_PATH; 
        String[] pidPath = new String[root.length + 1];
        // copy plugin root path, last element left empty for configuration PIDs
        System.arraycopy(root, 0, pidPath, 0, root.length);
        
        Iterator i = changes.iterator();
        while (i.hasNext()) {
            Event event = (Event) i.next();
            Conf table = event.getTable();
            String name = event.getName();
            Configuration configuration;
            Dictionary dict;
            pidPath[pidPath.length-1] = name;
            try {
                switch(event.getType()) {
                case Event.EVENT_TYPE_DELETE:
                    plugin.deleteConfiguration(name);
                    break;
                case Event.EVENT_TYPE_UPDATE:
                    configuration = getConfiguration(name, pidPath, true); 
                    dict = table.getDictionary();
                    configuration.update(dict);
                    break;
                case Event.EVENT_TYPE_RECREATE:
                    plugin.deleteConfiguration(name);
                    // continue on the EVENT_TYPE_CREATE case
                case Event.EVENT_TYPE_CREATE:
                    configuration = plugin.createConfiguration(name, 
                            table.getPid(), table.getLocation(), 
                            table.getFactoryPid());
                    dict = table.getDictionary();
                    configuration.update(dict);
                    break;
                default: // cannot happen
                    throw new IllegalStateException("Unknown event type: " +
                            event.getType());
                }
            } catch (ConfigPluginException e) {
                throw e.getDmtException(pidPath);
            } catch (IOException e) {
                throw new DmtException(pidPath, DmtException.DATA_STORE_FAILURE,
                        "Error updating configuration table with the given " +
                        "PID.");
            }
        }

        init();
    }

    public void rollback() throws DmtException {
        changes = new Vector();
        // nothing needs to be done
    }

    public void createInteriorNode(String[] fullPath, String type)
            throws DmtException {
        if(type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior configuration nodes.");
        
        String[] path = chopPath(fullPath);
        // path has at least one component because the root node always exists,
        // so this method is not called
        
        Event event = getEventForConfiguration(path[0]);
        
        if(path.length == 1) {
            if(configurationExists(event, path[0], fullPath))
                throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                        "A configuration table already exists for the given PID.");
            
            if(event == null)
                changes.add(new Event(Event.EVENT_TYPE_CREATE, path[0]));
            else
                event.recreate();
            
            return;
        }
        
        // throw an exception if no event was found and the configuration did 
        // not exist at the beginning of the session, or if a delete event was 
        // found
        if(!configurationExists(event, path[0], fullPath))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "There is no configuration data for the given PID.");
        
        Conf conf = getConf(event, path[0], fullPath);
        
        // path[1].equals(KEYS) (this node is automatically created)
        
        ConfigEntry entry = conf.getEntry(path[2]);
        
        if (path.length == 3) { // create new property
            if(entry != null)
                throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS, 
                        "The specified key already exists in the configuration.");
            
            conf.createEntry(path[2]);
            return;
        }

        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        // path.length == 4   (create VALUES node for non-scalar properties)
        try {
            entry.createValues();
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }

    public void createLeafNode(String[] fullPath, DmtData data, 
            String mimeType) throws DmtException {
        // proper MIME type ensured by meta-data
        
        String[] path = chopPath(fullPath);

        // path has at least two components as the first segment is interior
        Event event = getEventForConfiguration(path[0]);

        // throw an exception if no event was found and the configuration did 
        // not exist at the beginning of the session, or if a delete event was 
        // found
        if(!configurationExists(event, path[0], fullPath))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "There is no configuration data for the given PID.");
        
        Conf conf = getConf(event, path[0], fullPath);
        
        if(path.length == 2) {
            if(data == null)
                throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                        "The specified node has no default value.");
            
            // the format of the data is string, as guaranteed by the meta-data
            String param = data.getString();
            if(param != null && param.length() == 0)
                throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                        "Parameter string must be non-empty.");
            
            if(path[1].equals(LOCATION)) {
                if(!conf.canCreateLocation())
                    throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                            "The '" + LOCATION + "' parameter is already set " +
                            "for the specified configuration table.");
                conf.createLocation(param);
                return;
            }
            
            if(param == null)
                throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                        "PID string must not be null.");
                
            if(path[1].equals(PID)) {
                if(conf.getPid() != null)
                    throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                            "The '" + PID + "' parameter is already set " + 
                            "for the specified configuration table.");
                conf.createPid(param);
                return;
            }
            
            // path[1].equals(FACTORY_PID)
            
            if(!conf.canCreateFactoryPid()) {
                if(conf.getFactoryPid() == null)
                    throw new DmtException(fullPath, DmtException.COMMAND_FAILED, 
                            "The specified configuration table is " +
                            "not managed by a factory.");
                throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                        "The '" + FACTORY_PID + "' parameter is already " + 
                        "set for the specified configuration table.");
            }
            
            conf.createFactoryPid(param);
            
            return;
        }

        // next leaf is at length 4

        // path[1].equals(KEYS)
        // path[2] is a key name
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");

        if (path.length == 4) {

            if(path[3].equals(VALUE)) {
                if(data == null)
                    throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                            "The " + VALUE + " node has no default value.");
                
                try {
                    entry.createValue(data);
                } catch (ConfigPluginException e) {
                    throw e.getDmtException(fullPath);
                }
                
                return;
            }
        
            if (path[3].equals(TYPE)) {
                if(data == null)
                    data = Type.ALL_TYPE_DATA[0];
                
                Type type = Type.getTypeByData(data);
                if(type == null) // should not happen because of meta-data
                    throw new IllegalStateException("Invalid data received " +
                            "for the type: " + data);
                
                try {
                    entry.createType(type);
                } catch (ConfigPluginException e) {
                    throw e.getDmtException(fullPath);
                }
                return;
            }
            
            // path[3].equals(CARDINALITY)
            if(data == null)
                data = Cardinality.ALL_CARDINALITY_DATA[0];
            
            Cardinality cardinality = Cardinality.getCardinalityByData(data);
            if(cardinality == null) // should not happen because of meta-data
                throw new IllegalStateException("Invalid data received for " +
                        "the cardinality: " + data);
            
            try {
                entry.createCardinality(cardinality);
            } catch (ConfigPluginException e) {
                throw e.getDmtException(fullPath);
            }
            return;
        }
        
        // path.length == 5, path[3].equals(VALUES) 
        int index = Integer.parseInt(path[4]);
        try {
            entry.createElementAt(index, data);
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }

    public void setNodeValue(String[] fullPath, DmtData data)
            throws DmtException {
        // only VALUE and VALUES can be replaced, these don't have defaults
        if(data == null) 
            throw new DmtException(fullPath, DmtException.METADATA_MISMATCH, 
                    "The specified node has no default value.");
        
        String[] path = chopPath(fullPath);
        
        // path has at least four components because DmtAdmin only calls this
        // method for modifiable leaf nodes
        Event event = getEventForConfiguration(path[0]);

        // throw an exception if no event was found and the configuration did 
        // not exist at the beginning of the session, or if a delete event was 
        // found
        if(!configurationExists(event, path[0], fullPath))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "There is no configuration data for the given PID.");
        
        Conf conf = getConf(event, path[0], fullPath);
        
        // path[1].equals(KEYS)
        // path[2] is a key name
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");

        if (path.length == 4) {
            try {
                entry.setValue(data);
            } catch(ConfigPluginException e) {
                throw e.getDmtException(fullPath);
            }
            return;
        }

        // path.length == 5
        int index = Integer.parseInt(path[4]);
        try {
            entry.setElementAt(index, data);
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }
    
    public void deleteNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        // path has at least one component because the root node is permanent
        Event event = getEventForConfiguration(path[0]);

        // throw an exception if no event was found and the configuration did 
        // not exist at the beginning of the session, or if a delete event was 
        // found
        if(!configurationExists(event, path[0], fullPath))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "There is no configuration data for the given PID.");
            
        if(path.length == 1) {
            if(event == null)
                changes.add(new Event(Event.EVENT_TYPE_DELETE, path[0]));
            else if(event.getType() == Event.EVENT_TYPE_CREATE)
                changes.remove(event);
            else // EVENT_TYPE_UPDATE or EVENT_TYPE_RECREATE
                event.delete();
            
            return;
        }
        
        Conf conf = getConf(event, path[0], fullPath);
        
        // path has at least three components as KEYS cannot be deleted

        // path[1].equals(KEYS)
        // path[2] is a key name
        
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");

        if(path.length == 3) {
            conf.removeEntry(path[2]);
            return;
        }
        
        // path has five components, nothing on level 4 can be deleted

        // path[3].equals(VALUES)
        // path[4] is an index

        try {
            entry.removeElementAt(Integer.parseInt(path[4]));
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }

    public void setNodeType(String[] fullPath, String type) 
            throws DmtException {
        if(type == null)
            return;
        
        if(!isLeafNode(fullPath))
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                "Cannot set type property of interior configuration nodes.");
        
        // do nothing, meta-data guarantees that type is "text/plain"
    }

    public void setNodeTitle(String[] fullPath, String title) 
            throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                "Title property not supported.");
    }

    public void renameNode(String[] fullPath, String newName)
            throws DmtException {
        String[] path = chopPath(fullPath);
        
        if(path.length == 0)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot rename root node of the configuration tree.");
        
        Event event = getEventForConfiguration(path[0]);
        
        // throw an exception if no event was found and the configuration did 
        // not exist at the beginning of the session, or if a delete event was 
        // found
        if(!configurationExists(event, path[0], fullPath))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "There is no configuration data for the given PID.");
        
        Conf conf = getConf(event, path[0], fullPath);
        
        if(path.length == 1)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot rename configuration table root node.");
        
        if(path.length == 2)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot rename " + KEYS + " node.");

        // path[2] is a key name
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        if(path.length == 3) {
            if(conf.getEntry(newName) != null)
                throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                        "The specified key already exists in the configuration.");
            
            conf.renameEntry(path[2], newName);
            return;
        }

        if (path.length == 4)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot rename the specified node.");

        // path.length == 5
        int index = Integer.parseInt(path[4]);
        int newIndex = Integer.parseInt(newName);
        try {
            entry.renameElementAt(index, newIndex);
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
    }

    public void copy(String[] fullPath, String[] newNodePath, boolean recursive)
            throws DmtException {
        // ENHANCE allow cloning pid, key (on the same level)
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                "Cannot copy configuration nodes.");
    }
    
    
    // Override some read methods to provide up-to-date info in mid-session  
    
    public String[] getChildNodeNames(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        if(path.length == 0)
            return getConfigurationIds(fullPath);

        Event event = getEventForConfiguration(path[0]);
        if(event == null) // no event for ID, use ancestor method
            return super.getChildNodeNames(fullPath);
        
        Conf conf = event.getTable();
        if(conf == null) // event.getType() == Event.EVENT_TYPE_DELETE
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "Configuration data for the given PID has been deleted.");
        
        if (path.length == 1) {
            List l = new ArrayList();
            if(conf.getPid() != null)
                l.add(PID);
            if(!conf.canCreateLocation())
                l.add(LOCATION);
            l.add(KEYS);
            if(conf.getFactoryPid() != null)
                l.add(FACTORY_PID);
            return (String[]) l.toArray(new String[l.size()]);
        }
        
        if(path.length == 2)
            return conf.getKeys();

        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        if(path.length == 3) {
            List l = new ArrayList();
            if(entry.getType() != null)
                l.add(TYPE);
            if(entry.getCardinality() != null)
                l.add(CARDINALITY);
            if(entry.getValue() != null)
                l.add(VALUE);
            if(entry.getValues() != null)
                l.add(VALUES);
            return (String[]) l.toArray(new String[l.size()]);
        }
        
        Integer[] indices;
        try { // getElementAt checks that the entry is non-scalar
            indices = entry.getIndexArray();
        } catch (ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }
        
        String[] children = new String[indices.length];
        for(int i = 0; i < indices.length; i++)
            children[i] = indices[i].toString();

        return children;
    }
    
    public DmtData getNodeValue(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length > 1  because only leaf nodes are given
        Event event = getEventForConfiguration(path[0]);
        if(event == null) // no event for ID, use ancestor method
            return super.getNodeValue(fullPath);
        
        Conf conf = event.getTable();
        if(conf == null) // event.getType() == Event.EVENT_TYPE_DELETE
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "Configuration data for the given PID has been deleted.");
         
        if(path.length == 2) {
            if(path[1].equals(PID)) {
                String pid = conf.getPid();
                if(pid == null)
                    throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                            "The given node has not been created yet.");
                return new DmtData(pid);
            }
            
            if(path[1].equals(LOCATION)) {
                String location = conf.getLocation();
                if(conf.canCreateLocation())
                    throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                            "The given node has not been created yet.");
                return new DmtData(location);
            }

            // path[1].equals(FACTORY_PID)
            String factoryPid = conf.getFactoryPid();
            if(conf.canCreateFactoryPid())
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The given node has not been created yet.");
            if(factoryPid == null)
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The given node does not specify a factory configuration.");
            return new DmtData(factoryPid);
        }

        // path.length > 3
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The specified key does not exist in the configuration.");
        
        if(path.length == 4) {
            if(path[3].equals(TYPE)) {
                Type type = entry.getType();
                if(type == null)
                    throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                            "The given node has not been created yet.");
                return type.getData();
            }
                
            if(path[3].equals(CARDINALITY)) {
                Cardinality cardinality = entry.getCardinality();
                if(cardinality == null)
                    throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                            "The given node has not been created yet.");
                return cardinality.getData();
            }

            // path[3].equals(VALUE))
            if(entry.getValues() != null)
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The specified key contains a non-scalar value.");
            
            Value value = entry.getValue();
            if(value == null)
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "The given node has not been created yet.");
            return value.getData();
        }
        
        // path.length == 5
        if(entry.getValue() != null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The given key specifies scalar data.");
        
        Value element;
        try { // getElementAt checks that the entry is non-scalar
            element = entry.getElementAt(Integer.parseInt(path[4]));
        } catch(ConfigPluginException e) {
            throw e.getDmtException(fullPath);
        }

        if(element == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No element exists in the array/vector with the given " +
                    "index.");

        return element.getData();
    }
    
    public boolean isNodeUri(String[] fullPath) {
        String[] path = chopPath(fullPath);

        if(path.length == 0) // $/Configuration
            return true;
        if(path.length > 5)
            return false;
        
        Event event = getEventForConfiguration(path[0]);
        if(event == null) // no event for ID, use ancestor method
            return super.isNodeUri(fullPath);
        
        Conf conf = event.getTable();
        if(conf == null) // event.getType() == Event.EVENT_TYPE_DELETE
            return false;
        
        if(path.length == 1) // $/Configuration/<pid>
            return true;
        
        if(path.length == 2) {
            if(path[1].equals(PID))
                return conf.getPid() != null;
            if(path[1].equals(LOCATION))
                return !conf.canCreateLocation();
            if(path[1].equals(FACTORY_PID))
                return conf.getFactoryPid() != null;
            
            return path[1].equals(KEYS);
        }
        
        if(!path[1].equals(KEYS))
            return false;
        
        ConfigEntry entry = conf.getEntry(path[2]);
        if(entry == null)
            return false;

        if(path.length == 3)
            return true;
        
        if(path.length == 4) {
            if(path[3].equals(TYPE))
                return entry.getType() != null;
            if(path[3].equals(CARDINALITY))
                return entry.getCardinality() != null;
            if(path[3].equals(VALUE))
                return entry.getValue() != null;
            if(path[3].equals(VALUES))
                return entry.getValues() != null;
            
            return false;
        }
        
        if(!path[3].equals(VALUES))
            return false;
        
        try { // getElementAt checks that the entry is non-scalar
            return entry.getElementAt(Integer.parseInt(path[4])) != null;
        } catch (NumberFormatException e) {
            return false;
        } catch (ConfigPluginException e) {
            return false;
        }
    }

    
    //----- Utility methods -----//

    protected String[] getConfigurationIds(String[] fullPath) 
            throws DmtException {
        Set configurations = getStoredConfigurationIdSet(fullPath);
                
        Iterator i = changes.iterator();
        while (i.hasNext()) {
            Event event = (Event) i.next();
            int type = event.getType();
            if(type == Event.EVENT_TYPE_CREATE)
                configurations.add(event.getName());
            else if(type == Event.EVENT_TYPE_DELETE)
                configurations.remove(event.getName());
        }
        
        return (String[])
            configurations.toArray(new String[configurations.size()]);
    }
    
    private void init() {
        changes = new Vector();
        storedConfigurationIdSet = null;
        concurrentAccessError = null;
    }
    
    private boolean configurationExists(Event event, String nodeName,
            String[] fullPath) throws DmtException {
        return event != null ? event.getType() != Event.EVENT_TYPE_DELETE
                : getStoredConfigurationIdSet(fullPath).contains(nodeName);
    }
     
    private Set getStoredConfigurationIdSet(String[] fullPath) 
            throws DmtException {
        if(storedConfigurationIdSet == null)
            storedConfigurationIdSet = 
                new HashSet(Arrays.asList(super.getConfigurationIds(fullPath)));

        return (Set) storedConfigurationIdSet.clone();
    }
    
    private Event getEventForConfiguration(String name) {
        Event event = null;
        Iterator iter = changes.iterator();
        while (iter.hasNext()) {
            event = (Event) iter.next();
            if(name.equals(event.getName())) // found event for the given name
                return event;
        }
        
        return null;
    }

    // precondition: configurationExists(event, nodeName, fullPath) == true
    private Conf getConf(Event event, String nodeName, String[] fullPath)
            throws DmtException {
        if(event != null)
            return event.getTable();
        
        Conf conf = new Conf(getConfiguration(nodeName, fullPath, false));
        changes.add(new Event(Event.EVENT_TYPE_UPDATE, nodeName, conf));
        return conf;
    }
    
    private Configuration getConfiguration(String nodeName, String[] fullPath,
            boolean isCommit) throws DmtException {
        try {
            return plugin.getConfiguration(nodeName);
        } catch (ConfigPluginException e) {
            if(e.getCode() != DmtException.NODE_NOT_FOUND)
                throw e.getDmtException(fullPath);
            concurrentAccessError = "Accessed configuration table was " +
                    "deleted outside the scope of the session.";
            throw new DmtException(fullPath, 
                    isCommit ? DmtException.CONCURRENT_ACCESS 
                            : DmtException.NODE_NOT_FOUND,
                    concurrentAccessError);
        }
    }
}

class Event {
    static final int EVENT_TYPE_CREATE   = 0;
    static final int EVENT_TYPE_UPDATE   = 1;
    static final int EVENT_TYPE_DELETE   = 2;
    static final int EVENT_TYPE_RECREATE = 3;
    
    int type;
    String name;
    Conf table;
    
    Event(int type, String name) {
        this(type, name, null);
    }
    
    Event(int type, String name, Conf table) {
        switch(type) {
        case EVENT_TYPE_DELETE: 
            break;
        case EVENT_TYPE_CREATE:
            if(table == null)
                table = new Conf();
            break;
        case EVENT_TYPE_UPDATE:
            if(table == null)
                throw new IllegalArgumentException("Update events must have " +
                        "a configuration table parameter.");
            break;
        case EVENT_TYPE_RECREATE:
            throw new IllegalArgumentException("Cannot make 'recreate' events" +
                    "directly, these can only be changed from 'delete' events.");
        default:
            throw new IllegalStateException("Unknown event type.");
        }
        
        this.type = type;
        this.name = name;
        this.table = table;
    }
    
    void delete() {
        if(type != EVENT_TYPE_UPDATE && type != EVENT_TYPE_RECREATE)
            throw new IllegalStateException("Only 'update' or 'recreate' " +
                    "events can be changed to 'delete'.");
        
        type = EVENT_TYPE_DELETE;
        table = null;
    }
    
    void recreate() {
        if(type != EVENT_TYPE_DELETE)
            throw new IllegalStateException("Only 'delete' events can be " + 
                    "changed to 'recreate'.");
        
        type = EVENT_TYPE_RECREATE;
        
        this.table = new Conf();
    }
    
    int getType() {
        return type;
    }
    
    String getName() {
        return name;
    }
    
    Conf getTable() {
        return table;
    }
}

class Conf {
    private String pid; // PID of the configuration object
    
    private String location;
    private boolean canCreateLocation;
    
    private String factoryPid;
    private boolean canCreateFactoryPid;
    
    private Map properties;
    
    Conf() {
        pid = null;
        location = null;
        canCreateLocation = true;
        factoryPid = null;
        canCreateFactoryPid = true;
        properties = new TreeMap(new CaseInsensitiveStringComparator());
    }
    
    Conf(Configuration configuration) {
        pid = configuration.getPid();
        location = configuration.getBundleLocation();
        canCreateLocation = false;
        factoryPid = configuration.getFactoryPid();
        canCreateFactoryPid = false;
        properties = new TreeMap(new CaseInsensitiveStringComparator());

        Dictionary propertyDictionary = configuration.getProperties();
        Enumeration e = propertyDictionary.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            Object value = propertyDictionary.get(key);
            if(value instanceof Vector && ((Vector)value).size() == 0)
                continue;
            properties.put(key, new ConfigEntry(value));
        }
    }
    
    String getPid() {
        return pid;
    }
    
    /**
     * Returns the location bound to the configuration, or <code>null</code> if
     * the location has not been set yet or the configuration is not bound to 
     * any location.  The method {@link #canCreateLocation} only returns
     * <code>true</code> if the location node has not been created for a 
     * configuration that is just under construction.
     */
    String getLocation() {
        return location;
    }
    
    /**
     * Returns <code>true</code> if the location node has not been created for a 
     * configuration that is just under construction.
     */
    boolean canCreateLocation() {
        return canCreateLocation;
    }
    
    /**
     * Returns the factory PID of the configuration, or <code>null</code> if the
     * factory PID has not been set yet or the configuration is not a factory.
     * The method {@link #canCreateFactoryPid} only returns true if the factory
     * PID node has not been created for a configuration that is just under 
     * construction. 
     */
    String getFactoryPid() {
        return factoryPid;
    }
    
    /**
     * Returns true if the factory PID node has not been created for a 
     * configuration that is just under construction. 
     */
    boolean canCreateFactoryPid() {
        return canCreateFactoryPid;
    }
    
    String[] getKeys() {
        Set keySet = properties.keySet();
        return (String[]) keySet.toArray(new String[keySet.size()]);
    }
    
    void createPid(String pid) {
        if(this.pid != null)
            throw new IllegalStateException("PID already set.");
        if(pid == null || pid.length() == 0)
            throw new IllegalArgumentException("PID parameter cannot be null " +
                    "or empty.");
        
        this.pid = pid;
    }
    
    void createFactoryPid(String factoryPid) {
        if(!canCreateFactoryPid)
            throw new IllegalStateException("Cannot set factory PID, not a " +
                    "factory configuration or PID already set.");
        if(factoryPid == null || factoryPid.length() == 0)
            throw new IllegalArgumentException("Factory PID parameter cannot " +
                    "be null or empty.");
        
        canCreateFactoryPid = false;
        this.factoryPid = factoryPid;
    }
    
    void createLocation(String location) {
        if(!canCreateLocation)
            throw new IllegalStateException("Location already set.");
        if(location != null && location.length() == 0)
            throw new IllegalArgumentException("Location parameter cannot be " +
                    "empty.");
        
        canCreateLocation = false;
        this.location = location;
    }
    
    ConfigEntry createEntry(String key) {
        return (ConfigEntry) properties.put(key, new ConfigEntry());
    }
    
    ConfigEntry getEntry(String key) {
        return (ConfigEntry) properties.get(key);
    }
    
    ConfigEntry renameEntry(String oldKey, String newKey) {
        return (ConfigEntry) properties.put(newKey, properties.remove(oldKey));
    }
    
    ConfigEntry removeEntry(String key) {
        return (ConfigEntry) properties.remove(key);
    }
    
    Dictionary getDictionary() throws ConfigPluginException {
        Dictionary dict = new Hashtable();
        
        Iterator i = properties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Object value = ((ConfigEntry) entry.getValue()).getObject();
            dict.put(entry.getKey(), value);
        }
        
        return dict;
    }
    
    class CaseInsensitiveStringComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
            return ((String) obj1).compareToIgnoreCase((String) obj2);
        }
    }
}
