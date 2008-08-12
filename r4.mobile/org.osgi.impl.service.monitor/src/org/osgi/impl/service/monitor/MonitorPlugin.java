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
package org.osgi.impl.service.monitor;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.osgi.framework.BundleContext;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.*;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;

public class MonitorPlugin implements DataPlugin, ReadWriteDataSession
{
    // ENHANCE use full path instead of chopped, make constants for indices
    // ENHANCE remove segment parameters where the whole path is given anyway
    
    // node size values for fixed size formats
    private static final int BOOLEAN_SIZE = 1;
    private static final int INT_SIZE     = 4;
    
    // maybe this could be made friendly static, to be available from the 
    // StatusVarWrapper and Server classes without passing it to each instance
    private MonitorAdminImpl monitorAdmin;

    public MonitorPlugin(BundleContext bc, MonitorAdminImpl monitorAdmin) {
        this.monitorAdmin = monitorAdmin;
    }


    //----- DataPlugin methods -----//

    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) {
        return this;
    }
    
    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) {
        return this;
    }
    
    public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) {
        return null;
    }
    
    //----- ReadWriteDataSession methods -----//

    public void setNodeTitle(String[] fullPath, String title) 
            throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Title property not supported.");
    }

    // data == null means that the default value must be set
    public void setNodeValue(String[] fullPath, DmtData data) 
            throws DmtException {
        String[] path = chopPath(fullPath);

        // path.length > 4, because higher leaves don't have REPLACE access type in meta-data

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);
        // path[2].equals("Server")
        Server server = var.getServer(path[3], fullPath);

        if(path.length == 5) {
            if(path[4].equals("ServerID")) {
                if(data == null)
                    throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                            "The ServerID node has no default value.");

                server.setServerId(data, fullPath);
            } else {// path[4].equals("Enabled")
                if(data == null)
                    data = new DmtData(false);
                
                server.setEnabled(data, fullPath);
            }
                
            return;
        }

        if(path.length == 6) {
            // path[4].equals("Reporting")

            if(path[5].equals("Type")) {
                if(data == null)
                    data = new DmtData(Server.DEFAULT_TYPE);
                server.setType(data, fullPath);
            } else { // path[5].equals("Value")
                if(data == null)
                    data = new DmtData(Server.DEFAULT_SCHEDULE);
                server.setValue(data, fullPath);
            }

            return;
        }

        // path.length == 7, path[4].equals("TrapRef"), path[6].equals("TrapRefID")

        if(data == null)
            throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                    "The TrapRefId node has no default value.");
        
        server.setTrapRefId(path[5], data, fullPath);
    }

    public void setNodeType(String[] fullPath, String type) 
            throws DmtException {
        // meta-data ensures that type is either null or the only possible value
        if(!isLeafNode(fullPath) && type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior nodes in Monitoring tree.");
    }

    public void deleteNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        // path has at least four elements because DmtAdmin only calls this for deletable nodes

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);

        // path[2].equals("Server")

        if(path.length == 4) {
            var.removeServer(path[3], fullPath);
            return;
        }

        Server server = var.getServer(path[3], fullPath);

        // path.length == 6, path[4].equals("TrapRef") because only this is deletable

        server.deleteTrapRef(path[5], fullPath);
    }

    public void createInteriorNode(String[] fullPath, String type) 
            throws DmtException {
        if(type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior nodes in Monitoring tree.");
        
        String[] path = chopPath(fullPath);

        // path.length > 3, because the higher interior nodes do not have ADD
        // access type in their meta-data
        
        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);

        // path[2].equals("Server") because parent must be an interior node

        if(path.length == 4) {
            var.addServer(path[3], fullPath);
            return;
        }

        Server server = var.getServer(path[3], fullPath);

        // path.length == 6 && path[4].equals("TrapRef"), because only these 
        // nodes have ADD access type and are interior nodes according to their 
        // meta-data 
        
        server.addTrapRef(path[5], fullPath);
    }

    public void createLeafNode(String[] fullPath, DmtData value,
            String mimeType) throws DmtException {
        // No leaf node can be created by the caller in the current implementation
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                "Leaf nodes cannot be created in the monitoring tree.");
    }

    public void copy(String[] fullPath, String[] newFullPath, boolean recursive)
            throws DmtException {
        // on protocol level there is no copy operation, and local users will 
        // use Monitor Admin
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED, 
                               "Cannot copy monitoring nodes.");
    }

    public void renameNode(String[] fullPath, String newName) 
            throws DmtException {
        throw new DmtException(fullPath, DmtException.COMMAND_FAILED, 
                               "Cannot rename monitoring nodes.");
    }


    //----- ReadableDataSession methods -----//

    public void nodeChanged(String[] fullPath) {
        // do nothing - the version and timestamp properties are not supported
    }

    public MetaNode getMetaNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        if(path.length == 0)
            return new MonitorMetaNodeImpl("Root node of the monitoring subtree.", 
                    false, false, MetaNode.PERMANENT);

        try { 
            Path.checkName(path[0], "Monitorable ID");
        } catch (IllegalArgumentException e) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, e.getMessage());
        }
        
        if(path.length == 1) {
            return new MonitorMetaNodeImpl("Root node for a Monitorable service.",
                    false, true, MetaNode.PERMANENT);
        }

        try { 
            Path.checkName(path[1], "Status Variable ID");
        } catch (IllegalArgumentException e) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, e.getMessage());
        }

        if(path.length == 2)
            return new MonitorMetaNodeImpl("Root node for a Performance Indicator.", 
                    false, true, MetaNode.PERMANENT);

        if(path.length == 3) {
            if(path[2].equals("TrapID"))
                return new MonitorMetaNodeImpl("Full name of the Performance Indicator.", 
                                               true, null, null, DmtData.FORMAT_STRING, true);

            if(path[2].equals("CM")) {
                DmtData[] validValues = 
                    new DmtData[] { new DmtData("CC"), new DmtData("DER"), 
                                    new DmtData("GAUGE"), new DmtData("SI") };
                return new MonitorMetaNodeImpl("Collection method of data in the Performance Indicator.",
                                               true, null, validValues, DmtData.FORMAT_STRING, true);
            }

            if(path[2].equals("Results"))
                return new MonitorMetaNodeImpl("Current value of the Performance Indicator.",
                                               true, null, null, 
                                               DmtData.FORMAT_STRING | DmtData.FORMAT_BOOLEAN |
                                               DmtData.FORMAT_INTEGER | DmtData.FORMAT_FLOAT,
                                               true);

            if(path[2].equals("Server"))
                return new MonitorMetaNodeImpl("Root node for server monitoring requests.", 
                                               false, false, MetaNode.PERMANENT);
            
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No such node defined in the monitoring tree");
        }

        if(!path[2].equals("Server"))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the monitoring tree");
        
        if(path.length == 4)
            return new MonitorMetaNodeImpl("Root node of a server monitoring request.", 
                    true, true, MetaNode.DYNAMIC);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                return new MonitorMetaNodeImpl("Identifier of the DM server that should receive the requested " +
                                               "monitoring data.", false, null, null, DmtData.FORMAT_STRING, false);

            if(path[4].equals("Enabled"))
                return new MonitorMetaNodeImpl("A switch to start and stop monitoring.", 
                                               false, new DmtData(Server.DEFAULT_ENABLED), null, DmtData.FORMAT_BOOLEAN, false);

            if(path[4].equals("Reporting"))
                return new MonitorMetaNodeImpl("Root node for request scheduling parameters.", 
                                               false, false, MetaNode.AUTOMATIC);

            if(path[4].equals("TrapRef"))
                return new MonitorMetaNodeImpl("Root node for references to other required monitoring data.",
                                               false, false, MetaNode.AUTOMATIC);

            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the monitoring tree");
        }
        
        if(path.length == 6) {
            if(path[4].equals("Reporting")) {
                if(path[5].equals("Type")) {
                    DmtData[] validValues = new DmtData[] { new DmtData("TM"), new DmtData("EV") };
                    return new MonitorMetaNodeImpl("Indicates if the data reporting is time or event based.",
                                                   false, new DmtData(Server.DEFAULT_TYPE), 
                                                   validValues, DmtData.FORMAT_STRING, false);
                }

                if(path[5].equals("Value"))
                    return new MonitorMetaNodeImpl("Time or occurrence number parameter of the " +
                                                   "request (depending on the type).", false,
                                                   new DmtData(Server.DEFAULT_SCHEDULE), null, 
                                                   DmtData.FORMAT_INTEGER, false);
                
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                        "No such node defined in the monitoring tree");
            }

            if(path[4].equals("TrapRef"))
                return new MonitorMetaNodeImpl("Placeholder for a reference to other monitoring data.", 
                                               true, true, MetaNode.DYNAMIC);


            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                "No such node defined in the monitoring tree");

        }

        if(!path[4].equals("TrapRef") || !path[6].equals("TrapRefID"))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                "No such node defined in the monitoring tree");

        if(path.length == 7)
            return new MonitorMetaNodeImpl("A reference to other monitoring data.", 
                                           false, null, null, 
                                           DmtData.FORMAT_STRING, true);
        
        // path.length > 7
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
            "No such node defined in the monitoring tree");
    }

    public void close() throws DmtException {
        // nothing to clean up
    }

    public boolean isNodeUri(String[] fullPath) {
        String[] path = chopPath(fullPath);

        if(path.length == 0)
            return true;

        if(path.length == 1) {
            try {
                checkMonitorable(path[0], fullPath);
                return true;
            } catch(DmtException e) {
                return false;
            }
        }

        StatusVarWrapper var;
        try {
            var = getStatusVar(path[0], path[1], fullPath);
        } catch(DmtException e) {
            return false;
        }

        if(path.length == 2)
            return true;

        if(!path[2].equals("TrapID") && !path[2].equals("Server") && 
           !path[2].equals("CM")     && !path[2].equals("Results"))
            return false;

        if(path.length == 3)
            return true;

        Server server;
        try {
            server = var.getServer(path[3], fullPath);
        } catch(DmtException e) {
            return false;
        }

        if(path.length == 4)
            return true;

        if(!path[4].equals("ServerID")  && !path[4].equals("Enabled") &&
           !path[4].equals("Reporting") && !path[4].equals("TrapRef"))
            return false;

        if(path.length == 5)
            return true;

        if(path[4].equals("Reporting"))
            return path.length == 6 && 
                    (path[5].equals("Type") || path[5].equals("Value"));

        if(path[4].equals("TrapRef")) {
            try {
                server.getTrapRefId(path[5], fullPath);
            } catch(DmtException e) {
                return false;
            }

            return 
                path.length == 6 ||
                (path.length == 7 && path[6].equals("TrapRefID"));
        }

        return false;
    }

    public boolean isLeafNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        if(path.length == 0)
            return false;
        
        if(path.length == 1) {
            checkMonitorable(path[0], fullPath);
            return false;
        }

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);
        if(path.length == 2)
            return false;
        
        if(path.length == 3)
            return !path[2].equals("Server");

        Server server = var.getServer(path[3], fullPath);
        if(path.length == 4)
            return false;
        
        if(path.length == 5)
            return path[4].equals("ServerID") || path[4].equals("Enabled");
        
        if(path.length == 6 && path[4].equals("Reporting"))
            return true;
            
        // path[4].equals("TrapRef")
        server.getTrapRefId(path[5], fullPath);

        // path[6] is a leaf, path[5] is interior
        return path.length == 7;
        
            
    }
    
    public DmtData getNodeValue(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        // path has at least three elements because the first leaf node is three levels deep

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);

        if(path.length == 3) {
            StatusVariable realVar = var.getStatusVariable();

            // this is a "unique registered identifier" according to the spec
            if(path[2].equals("TrapID"))
                return new DmtData(path[0] + '/' + path[1]);

            if(path[2].equals("CM"))
                return new DmtData(cmName(realVar.getCollectionMethod()));

            // path[2].equals("Results")

            return MonitorAdminImpl.createData(realVar);
        }

        // path.length > 4, path[2].equals("Server")

        Server server = var.getServer(path[3], fullPath);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                return server.getServerId();

            // path[4].equals("Enabled")

            return server.getEnabled();
        }

        if(path.length == 6) {
            // path[4].equals("Reporting")

            if(path[5].equals("Type"))
                return server.getType();

            // path[5].equals("Value")
            return server.getValue();
        }

        // path.length == 7, path[4].equals("TrapRef"), path[6].equals("TrapRefID")

        return server.getTrapRefId(path[5], fullPath);
    }

    public String getNodeTitle(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Title property not supported.");
    }

    public String getNodeType(String[] fullPath) throws DmtException {
        if(isLeafNode(fullPath))
            return MonitorMetaNodeImpl.LEAF_MIME_TYPE;
        
        String[] path = chopPath(fullPath);
        if(path.length == 0)
            return MonitorMetaNodeImpl.MONITOR_MO_TYPE;
        
        return null;
    }

    public int getNodeVersion(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Version property not supported.");
    }

    public Date getNodeTimestamp(String[] fullPath) throws DmtException {
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Timestamp property not supported.");
    }

    public int getNodeSize(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        // path has at least three elements because the first leaf node is three levels deep

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);

        if(path.length == 3) {
            StatusVariable realVar = var.getStatusVariable();

            // this is a "unique registered identifier" according to the spec
            if(path[2].equals("TrapID"))
                return path[0].length() + path[1].length() + 1;

            if(path[2].equals("CM"))
                return cmName(realVar.getCollectionMethod()).length();

            // path[2].equals("Results")

            return MonitorAdminImpl.createData(realVar).getSize();
        }

        // path.length > 4, path[2].equals("Server")

        Server server = var.getServer(path[3], fullPath);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                return server.getServerId().getString().length();

            // path[4].equals("Enabled")

            return BOOLEAN_SIZE;
        }

        if(path.length == 6) {
            // path[4].equals("Reporting")

            if(path[5].equals("Type"))
                return server.getType().getString().length();

            // path[5].equals("Value")
            return INT_SIZE;
        }

        // path.length == 7, path[4].equals("TrapRef"), path[6].equals("TrapRefID")

        return server.getTrapRefId(path[5], fullPath).getString().length();
    }

    public String[] getChildNodeNames(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);

        if(path.length == 0) {
            String[] monitorables = monitorAdmin.getMonitorableNames();
            return monitorables != null ? monitorables : new String[0];
        }

        if(path.length == 1) {
            try {
                return monitorAdmin.getStatusVariableNames(path[0]);
            } catch(IllegalArgumentException e) {
                throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                        "No Monitorable registered with the given ID.", e);
            }
        }

        StatusVarWrapper var = getStatusVar(path[0], path[1], fullPath);

        if(path.length == 2)
            return new String[] { "TrapID", "Server", "CM", "Results" };        

        // path[2].equals("Server")

        if(path.length == 3)
            return var.getServerNames();

        Server server = var.getServer(path[3], fullPath);

        if(path.length == 4)
            return new String[] { "ServerID", "Enabled", "Reporting", "TrapRef" };

        if(path.length == 5) {
            if(path[4].equals("Reporting"))
                return new String[] { "Type", "Value" };

            // path[4].equals("TrapRef")

            return server.getTrapRefNames();
        }

        // path.length == 6, path[4].equals("TrapRef")

        return new String[] { "TrapRefID" };
    }


    //----- Private utility methods -----//
    
    private void checkMonitorable(String id, String[] fullPath) 
            throws DmtException {
        try {
            monitorAdmin.checkMonitorable(id);
        } catch(IllegalArgumentException e) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    e.getMessage());
        }
    }

    private StatusVarWrapper getStatusVar(String monId, String id,
            String[] fullPath) throws DmtException {

        StatusVariable var;
        try {
            var = monitorAdmin.getStatusVariable(monId + "/" + id);
        } catch(IllegalArgumentException e) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No Status Variable with the given name provided by the Monitorable.", e);
        }

        if(var == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "No Status Variable with the given name provided by the Monitorable.");

        return new StatusVarWrapper(monId + '/' + id, var, monitorAdmin);
    }

    private static String[] chopPath(String[] fullPath) {
        // assuming that nodeUri starts with the monitoring root node
        int rootLen = Activator.PLUGIN_ROOT_PATH.length;
        int pathLen = fullPath.length;

        String[] relativePath = new String[pathLen-rootLen];
        System.arraycopy(fullPath, rootLen, relativePath, 0, pathLen-rootLen);
        return relativePath;
    }
    
    private static String cmName(int cm) {
        switch(cm) {
        case StatusVariable.CM_CC:    return "CC";
        case StatusVariable.CM_DER:   return "DER";
        case StatusVariable.CM_GAUGE: return "GAUGE";
        case StatusVariable.CM_SI:    return "SI";
        }

        throw new IllegalArgumentException(
                "Unknown collection method constant '" + cm +
                "' in Status Variable.");
    }
}

class StatusVarWrapper {
    private static Hashtable serverLists = new Hashtable();

    static void removeMonitorable(String pid) {
        String pathPrefix = pid + "/";

        Iterator i = serverLists.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            if(((String) entry.getKey()).startsWith(pathPrefix)) {
                Iterator j = ((Hashtable) entry.getValue()).values().iterator();
                while(j.hasNext())
                    ((Server) j.next()).destroy();
                i.remove();
            }
        }
    }


    private String path;
    private StatusVariable var;
    private MonitorAdminImpl monitorAdmin;

    private Hashtable servers;

    StatusVarWrapper(String path, StatusVariable var, 
            MonitorAdminImpl monitorAdmin) {
        this.path = path;
        this.var = var;
        this.monitorAdmin = monitorAdmin;

        servers = (Hashtable) serverLists.get(path);
        if(servers == null) {
            servers = new Hashtable();
            serverLists.put(path, servers);
        }
    }

    StatusVariable getStatusVariable() {
        return var;
    }

    String[] getServerNames() {
        return (String[]) servers.keySet().toArray(new String[0]);
    }

    Server getServer(String nodeName, String[] fullPath) throws DmtException {
        Server server = (Server) servers.get(nodeName);

        if(server == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No server request exists with the given ID.");

        return server;
    }

    void addServer(String nodeName, String[] fullPath) throws DmtException {
        if(servers.containsKey(nodeName))
            throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                    "A server request with the specified node name already exists.");

        servers.put(nodeName, new Server(nodeName, path, monitorAdmin));
    }

    void removeServer(String nodeName, String[] fullPath) throws DmtException {
        Server server = (Server) servers.get(nodeName);
        if(server == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No server request exists for the specified node name.");

        server.destroy();
        servers.remove(nodeName);
    }
}


class Server {
    static final boolean DEFAULT_ENABLED = false;
    static final String DEFAULT_TYPE = "TM";
    static final int DEFAULT_SCHEDULE = 60;

    private String serverId;
    private boolean enabled;
    private String type;
    private int value;
    private Hashtable trapRef;

    private MonitorAdminImpl monitorAdmin;
    private MonitoringJob job;

    private String path;

    Server(String nodeName, String path, MonitorAdminImpl monitorAdmin) {
        this.path = path;
        this.monitorAdmin = monitorAdmin;

        job = null;

        // default server ID is the name of the node
        serverId = unescape(nodeName); 
        enabled = false;
        type = DEFAULT_TYPE;
        value = DEFAULT_SCHEDULE;
        trapRef = new Hashtable();
    }


    DmtData getServerId() {
        return new DmtData(serverId);
    }

    DmtData getEnabled() {
        return new DmtData(enabled);
    }

    DmtData getType() {
        return new DmtData(type);
    }

    DmtData getValue() {
        return new DmtData(value);
    }

    String[] getTrapRefNames() {
        return (String[]) trapRef.keySet().toArray(new String[0]);
    }

    DmtData getTrapRefId(String name, String[] fullPath) throws DmtException {
        String n = (String) trapRef.get(name);

        if(n == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The trap reference with the specified ID does not exist.");

        return new DmtData(n);
    }

    void setServerId(DmtData data, String[] fullPath) throws DmtException {
        // data format is FORMAT_STRING, data is non-empty because of meta-data
        serverId = data.getString();
    }

    void setEnabled(DmtData data, String[] fullPath) throws DmtException {
        // data format is FORMAT_BOOLEAN because of meta-data
        
        boolean toBeEnabled = data.getBoolean();

        if(toBeEnabled) {
            try { // expecting SecurityException and IllegalArgumentException
                startJob();
            } catch(Exception e) {
                throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                                       "Error starting monitoring job.", e);
            }
        } else {
            try { // not expecting any specific exceptions
                stopJob();
            } catch(Exception e) {
                throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                                       "Error stopping monitoring job.", e);
            }
        }
        
        enabled = toBeEnabled;
    }

    void setType(DmtData data, String[] fullPath) throws DmtException {
        // data format is FORMAT_STRING because of meta-data
        // data content is "TM" or "EV" because of meta-data
        type = data.getString();
    }

    void setValue(DmtData data, String[] fullPath) throws DmtException {
        // data format is FORMAT_INTEGER and data is not <0 because of meta-data
        value = data.getInt();
    }

    void setTrapRefId(String name, DmtData data, String[] fullPath) 
            throws DmtException {
        if(!trapRef.containsKey(name))
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "The trap reference with the specified ID does not exist.");
        
        // data format is FORMAT_STRING because of meta-data
        trapRef.put(name, data.getString());
    }

    void addTrapRef(String name, String[] fullPath) throws DmtException {
    	if(trapRef.put(name, unescape(name)) != null)
    		throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                    "A trap reference with the given ID already exists.");
    }
    
    void deleteTrapRef(String name, String[] fullPath) throws DmtException {
    	if(trapRef.remove(name) == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                    "The trap reference with the specified ID does not exist.");
    }
    
    void destroy() {
        stopJob();
    }

    private void startJob() {
        if(job != null)
            return;

        int schedule = type.equals("TM") ? value : 0;
        int count;

        if(schedule == 0)
            count = value == 0 ? 1 : value;
        else
            count = 0;

        // this is not very efficient...
        String[] trapRefIds = (String[]) trapRef.values().toArray(new String[0]);
        String[] varNames = new String[trapRefIds.length + 1];
        
        varNames[0] = path;
        for (int i = 0; i < trapRefIds.length; i++)
			varNames[i+1] = trapRefIds[i];
                
        // throws SecurityException, IllegalArgumentException
        job = monitorAdmin.startJob(serverId, varNames, schedule, count, false);
    }

    private void stopJob() {
        if(job == null)
            return;

        job.stop();
        job = null;
    }
    
    // Substitutes \\ and \/ sequences in path segment with \ and /, does not
    // check whether segment is valid (i.e. does not contain other escapes, does
    // not end with \, etc.).  Validity must be guaranteed by Dmt Admin.
    private String unescape(String pathSegment) {
        StringBuffer sb = new StringBuffer(pathSegment);
        for(int i = 0; i < sb.length(); i++) {
            if(sb.charAt(i) == '\\')
                sb.deleteCharAt(i);
        }
        
        return sb.toString();
    }
}
