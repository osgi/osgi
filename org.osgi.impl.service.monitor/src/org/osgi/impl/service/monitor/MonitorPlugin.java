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

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.monitor.*;
import org.osgi.util.tracker.ServiceTracker;

// TODO check whether fatal flag should be set in any exceptions
public class MonitorPlugin implements DmtDataPlugin
{
    private BundleContext bc;
    private ServiceTracker tracker;
    private MonitorAdminImpl monitorAdmin;

    private DmtSession session;
    

    public MonitorPlugin(BundleContext bc, ServiceTracker tracker, MonitorAdminImpl monitorAdmin) {
        this.bc = bc;
        this.tracker = tracker;
        this.monitorAdmin = monitorAdmin;

        session = null;
    }


    //----- DmtDataPlugin methods -----//

    public void open(int lockMode, DmtSession session) throws DmtException {
        this.session = session;
    }

    public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
        open(lockMode, session);
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length == 0)
            return new MonitorMetaNodeImpl("Root node of the monitoring subtree.", false, false, false, true);

        Monitorable monitorable = getMonitorable(path[0], nodeUri);

        if(path.length == 1)
            return new MonitorMetaNodeImpl("Root node for a Monitorable service.", false, false, true, false);

        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        if(path.length == 2)
            return new MonitorMetaNodeImpl("Root node for a Performance Indicator.", false, false, true, false);

        if(path.length == 3) {
            if(path[2].equals("TrapID"))
                return new MonitorMetaNodeImpl("Full name of the Performance Indicator.", 
                                               false, false, null, null, DmtData.FORMAT_STRING);

            if(path[2].equals("CM")) {
                DmtData[] validValues = 
                    new DmtData[] { new DmtData("CC"), new DmtData("DER"), 
                                    new DmtData("GAUGE"), new DmtData("SI") };
                return new MonitorMetaNodeImpl("Collection method of data in the Performance Indicator.",
                                               false, false, null, validValues, DmtData.FORMAT_STRING);
            }

            if(path[2].equals("Results"))
                return new MonitorMetaNodeImpl("Current value of the Performance Indicator.",
                                               false, false, null, null, DmtData.FORMAT_XML);

            // path[2].equals("Server")

            return new MonitorMetaNodeImpl("Root node for server monitoring requests.", false, true, false, false);
        }

        Server server = kpi.getServer(path[3], nodeUri);

        if(path.length == 4)
            return new MonitorMetaNodeImpl("Root node of a server monitoring request.", true, false, true, false);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                return new MonitorMetaNodeImpl("Identifier of the DM server that should receive the requested " +
                                               "monitoring data.", true, false, null, null, DmtData.FORMAT_STRING);

            if(path[4].equals("Enabled"))
                return new MonitorMetaNodeImpl("A switch to start and stop monitoring.", 
                                               true, false, new DmtData(Server.DEFAULT_ENABLED), null, DmtData.FORMAT_BOOLEAN);

            if(path[4].equals("Reporting"))
                return new MonitorMetaNodeImpl("Root node for request scheduling parameters.", 
                                               false, false, false, false);

            // path[4].equals("TrapRef")

            return new MonitorMetaNodeImpl("Root node for references to other required monitoring data.",
                                           false, true, false, false);
        }

        if(path.length == 6) {
            if(path[4].equals("Reporting")) {
                if(path[5].equals("Type")) {
                    DmtData defaultData = new DmtData(Server.DEFAULT_TYPE);
                    DmtData[] validValues = new DmtData[] { defaultData, new DmtData("EV") };
                    return new MonitorMetaNodeImpl("Indicates if the data reporting is time or event based.",
                                                   true, false, defaultData, validValues, DmtData.FORMAT_STRING);
                }

                // path[5].equals("Value")

                return new MonitorMetaNodeImpl("Time or occurrence number parameter of the request (depending on " +
                                               "the type).", true, false, new DmtData(Server.DEFAULT_SCHEDULE), null, 
                                               DmtData.FORMAT_INTEGER);
            }

            // path[4].equals("TrapRef")

            server.getTrapRefId(path[5], nodeUri);

            return new MonitorMetaNodeImpl("Placeholder for a reference to other monitoring data.", 
                                           true, false, true, false);
        }

        // path.length == 7
        return new MonitorMetaNodeImpl("A reference to other monitoring data.", 
                                       true, false, null, null, DmtData.FORMAT_STRING);
    }

    public boolean supportsAtomic() {
        return false;
    }


    //----- Dmt methods -----//

    public void commit() throws DmtException {
        // this should never be called if supportsAtomic is false        
    }
    
    public void rollback() throws DmtException {
        // this should never be called if supportsAtomic is false
    }

    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        // TODO
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "Title property not supported.");
    }

    // TODO merge this method with setNodeValue() (maybe pass data=null)
    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length < 5) // TODO replace this with the canReplace check in DmtAdmin based on the meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot change node data at the specified position.");

        Monitorable monitorable = getMonitorable(path[0], nodeUri);
        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);
        // path[2].equals("Server")
        Server server = kpi.getServer(path[3], nodeUri);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                        "The ServerID node has no default value.");

            // path[4].equals("Enabled")
            server.setEnabled(new DmtData(false), nodeUri);

            return;
        }

        if(path.length == 6) {
            // path[4].equals("Reporting")

            if(path[5].equals("Type"))
                server.setType(new DmtData(Server.DEFAULT_TYPE), nodeUri);
            else // path[5].equals("Value")
                server.setValue(new DmtData(0), nodeUri);

            return;
        }

        // path.length == 7, path[4].equals("TrapRef"), path[6].equals("TrapRefID")

        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                "The TrapRefId node has no default value.");
    }
    
    public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length < 5) // TODO replace this with the canReplace check in DmtAdmin based on the meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot change node data at the specified position.");

        Monitorable monitorable = getMonitorable(path[0], nodeUri);
        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);
        // path[2].equals("Server")
        Server server = kpi.getServer(path[3], nodeUri);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                server.setServerId(data, nodeUri);
            else // path[4].equals("Enabled")
                server.setEnabled(data, nodeUri);

            return;
        }

        if(path.length == 6) {
            // path[4].equals("Reporting")

            if(path[5].equals("Type"))
                server.setType(data, nodeUri);
            else // path[5].equals("Value")
                server.setValue(data, nodeUri);

            return;
        }

        // path.length == 7, path[4].equals("TrapRef"), path[6].equals("TrapRefID")

        server.setTrapRefId(path[5], data, nodeUri);
    }

    public void setNodeType(String nodeUri, String type) throws DmtException {
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                               "Cannot set type property of monitoring nodes.");
    }

    public void deleteNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        // path has at least four elements because DmtAdmin only calls this for deletable nodes

        Monitorable monitorable = getMonitorable(path[0], nodeUri);
        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        // path[2].equals("Server")

        if(path.length == 4) {
            kpi.removeServer(path[3], nodeUri);
            return;
        }

        Server server = kpi.getServer(path[3], nodeUri);

        // path.length == 6, path[4].equals("TrapRef") because only this is deletable

        // TODO make TrapRefID node separately deletable if it is separately creatable
        server.deleteTrapRef(path[5], nodeUri);
    }

    public void createInteriorNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length < 4) // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");

        Monitorable monitorable = getMonitorable(path[0], nodeUri);
        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        // path[2].equals("Server") because parent must be an interior node

        if(path.length == 4) {
            kpi.addServer(path[3], nodeUri);
            return;
        }

        Server server = kpi.getServer(path[3], nodeUri);

        // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
        if(path.length == 5 || !path[4].equals("TrapRef"))
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");

        // path[4].equals("TrapRef") 

        if(path.length == 6) {
            server.addTrapRef(path[5], nodeUri);
            return;
        }

        // path.length == 7

        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                               "Cannot add interior node at the specified position.");
    }

    public void createInteriorNode(String nodeUri, String type) throws DmtException {
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                               "Cannot set type property of monitoring nodes.");
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        createLeafNode(nodeUri, null); // no leaf can be created, will throw exception
    }

    public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
        // No leaf node can be created by the caller in the current implementation 
        String[] path = prepareUri(nodeUri);

        if(path.length < 4) // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");

        Monitorable monitorable = getMonitorable(path[0], nodeUri);
        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        // path[2].equals("Server") because parent must be an interior node

        if(path.length == 4)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                                   "Cannot add leaf node at the specified position.");

        Server server = kpi.getServer(path[3], nodeUri);
        
        // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
        if(path.length == 5 || !path[4].equals("TrapRef"))
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");

        // path[4].equals("TrapRef") 

        if(path.length == 6)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add leaf node at the specified position.");

        server.getTrapRefId(path[5], nodeUri);
        
        // path.length == 7, path[5] is not an existing trap reference name

        // TODO allow adding TrapRefID leaf nodes?  Currently automatically created with parent.
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                               "Cannot add TrapRefID nodes manually.");
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        createLeafNode(nodeUri, value); // no leaf node can be created, will throw exception
    }

    public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
        // TODO allow cloning a monitoring job?
    }

    public void renameNode(String nodeUri, String newName) throws DmtException {
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Cannot rename configuration nodes.");
    }


    //----- DmtReadOnly methods -----//

    public void close() throws DmtException {
        // does nothing, because plugin is not transactional
    }

    public boolean isNodeUri(String nodeUri) {
        // TODO check path components for invalid characters (monitorable ID, KPI ID)

        String[] path = prepareUri(nodeUri);

        if(path.length == 0)
            return true;

        Monitorable monitorable;
        try {
            monitorable = getMonitorable(path[0], nodeUri);
        } catch(DmtException e) {
            return false;
        }

        if(path.length == 1)
            return true;

        KpiWrapper kpi;
        try {
            kpi = getKpi(monitorable, path[0], path[1], nodeUri);
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
            server = kpi.getServer(path[3], nodeUri);
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
            return path.length == 6 && (path[5].equals("Type") || path[5].equals("Value"));

        if(path[4].equals("TrapRef")) {
            try {
                server.getTrapRefId(path[5], nodeUri);
            } catch(DmtException e) {
                return false;
            }

            return 
                path.length == 6 ||
                (path.length == 7 && path[6].equals("TrapRefID"));
        }

        return false;
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        // path has at least three elements because the first leaf node is three levels deep

        Monitorable monitorable = getMonitorable(path[0], nodeUri);

        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        if(path.length == 3) {
            KPI kpiValue = kpi.getKpi();

            // TODO this should be a "unique registered identifier" according to the spec (e.g. reverse domain name)
            if(path[2].equals("TrapID"))
                return new DmtData(path[0] + '/' + path[1]);

            if(path[2].equals("CM"))
                return new DmtData(cmName(kpiValue.getCollectionMethod()));

            // path[2].equals("Results")

            return new DmtData(MonitorAdminImpl.createXml(kpiValue), true);
        }

        // path.length > 4, path[2].equals("Server")

        Server server = kpi.getServer(path[3], nodeUri);

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

        return server.getTrapRefId(path[5], nodeUri);
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        // TODO
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "Title property not supported.");
    }

    public String getNodeType(String nodeUri) throws DmtException {
        // TODO
        return null;
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        // TODO
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "Version property not supported.");
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        // TODO
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "Timestamp property not supported.");
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        // TODO
        return 0;
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length == 0) {
            ServiceReference[] monitorables = tracker.getServiceReferences();

            if(monitorables == null)
                return new String[] {};

            String[] children = new String[monitorables.length];
            for(int i = 0; i < monitorables.length; i++) {
                children[i] = (String) monitorables[i].getProperty("service.pid");
                if(children[i] == null) // TODO change this, maybe ignore service
                    throw new IllegalStateException("No service.pid property of Monitorable service.");
            }

            return children;
        }

        Monitorable monitorable = getMonitorable(path[0], nodeUri);

        if(path.length == 1)
            // TODO KPI names should be checked for invalid characters (?)
            return monitorable.getKpiNames();

        KpiWrapper kpi = getKpi(monitorable, path[0], path[1], nodeUri);

        if(path.length == 2)
            return new String[] { "TrapID", "Server", "CM", "Results" };        

        // path[2].equals("Server")

        if(path.length == 3)
            return kpi.getServerNames();

        Server server = kpi.getServer(path[3], nodeUri);

        if(path.length == 4)
            return new String[] { "ServerID", "Enabled", "Reporting", "TrapRef" };

        if(path.length == 5) {
            if(path[4].equals("Reporting"))
                return new String[] { "Type", "Value" };

            // path[4].equals("TrapRef")

            return server.getTrapRefNames();
        }

        // path.length == 6, path[4].equals("TrapRef")

        // TODO make this optional if TrapRefID nodes are creatable/deletable
        return new String[] { "TrapRefID" };
    }


    //----- Private utility methods -----//

    private Monitorable getMonitorable(String id, String nodeUri) throws DmtException {
        // TODO check ID for filter-illegal characters (it should already be checked for DMT-illegal chars by isNodeUri)
        ServiceReference[] refs;
        try {
            refs = bc.getServiceReferences(Monitorable.class.getName(), "(service.pid=" + id + ")");
        } catch(InvalidSyntaxException e) {
            // should not be reached if the above todo is done
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Invalid characters in Monitorable ID.");
        }

        if(refs == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "No Monitorable registered with the given ID.");

        // TODO is it allowed to give the tracker a reference that was not given by it?
        Monitorable monitorable = (Monitorable) tracker.getService(refs[0]);
        if(monitorable == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                                   "The Monitorable with the given ID is no longer registered.");

        return monitorable;
    }

    private KpiWrapper getKpi(Monitorable monitorable, String monId, 
                                     String id, String nodeUri) throws DmtException {

        KPI kpi;
        try {
            kpi = monitorable.getKpi(id);
        } catch(IllegalArgumentException e) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                                   "No KPI with the given name provided by the Monitorable.", e);
        }

        if(kpi == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                                   "No KPI with the given name provided by the Monitorable.");

        return new KpiWrapper(monId + '/' + id, kpi, monitorAdmin);
    }

    private static String[] prepareUri(String nodeUri) {
        // assuming that nodeUri starts with the monitoring root node
        int rootLen = Activator.PLUGIN_ROOT.length();
        if(nodeUri.length() == rootLen)
            return new String[] {};

        String[] path = Splitter.split(nodeUri.substring(rootLen + 1), '/', -1);

        if(path.length == 1 && path[0].equals("")) // shouldn't happen
            return new String[] {};

        return path;
    }

    private static String cmName(int cm) {
        switch(cm) {
        case KPI.CM_CC:    return "CC";
        case KPI.CM_DER:   return "DER";
        case KPI.CM_GAUGE: return "GAUGE";
        case KPI.CM_SI:    return "SI";
        }

        throw new IllegalArgumentException("Unknown collection method constant '" + cm + "' in KPI.");
    }
}

// TODO do not store monitorAdmin refs everywhere
class KpiWrapper {
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
    private KPI kpi;
    private MonitorAdminImpl monitorAdmin;

    private Hashtable servers;

    KpiWrapper(String path, KPI kpi, MonitorAdminImpl monitorAdmin) {
        this.path = path;
        this.kpi = kpi;
        this.monitorAdmin = monitorAdmin;

        servers = (Hashtable) serverLists.get(path);
        if(servers == null) {
            servers = new Hashtable();
            serverLists.put(path, servers);
        }
    }

    KPI getKpi() {
        return kpi;
    }

    String[] getServerNames() {
        return (String[]) servers.keySet().toArray(new String[0]);
    }

    Server getServer(String nodeName, String nodeUri) throws DmtException {
        Server server = (Server) servers.get(nodeName);

        if(server == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "No server request exists with the given ID.");

        return server;
    }

    void addServer(String nodeName, String nodeUri) throws DmtException {
        if(servers.containsKey(nodeName))
            throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS, 
                                   "A server request with the specified node name already exists.");

        servers.put(nodeName, new Server(nodeName, path, monitorAdmin));
    }

    void removeServer(String nodeName, String nodeUri) throws DmtException {
        Server server = (Server) servers.get(nodeName);
        if(server == null)
            throw new DmtException(nodeName, DmtException.NODE_NOT_FOUND, 
                                   "No server request exists for the specified node name.");

        server.destroy();
        servers.remove(nodeName);
    }
}


class Server {
    static final boolean DEFAULT_ENABLED = false;
    static final String DEFAULT_TYPE = "TM";
    static final int DEFAULT_SCHEDULE = 60;

    private String nodeName;
    private String serverId;
    private boolean enabled;
    private String type;
    private int value;          // should be 'long' to store unsigned int
    private Hashtable trapRef;

    private MonitorAdminImpl monitorAdmin;
    private MonitoringJob job;

    private String path;

    Server(String nodeName, String path, MonitorAdminImpl monitorAdmin) {
        this.nodeName = nodeName;
        this.path = path;
        this.monitorAdmin = monitorAdmin;

        job = null;

        serverId = nodeName;
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

    DmtData getTrapRefId(String name, String nodeUri) throws DmtException {
        String n = (String) trapRef.get(name);

        if(n == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "The trap reference with the specified ID does not exist.");

        return new DmtData(n);
    }

    void setServerId(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtData.FORMAT_STRING)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Server ID leaf must have string format.");

        if(data.getString() == null)
            throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH, "Server ID string must be non-null.");

        serverId = data.getString();
    }

    void setEnabled(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtData.FORMAT_BOOLEAN)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Enabled leaf must have boolean format.");

        enabled = data.getBoolean();

        // TODO do something with start/stop exceptions
        if(enabled)
            startJob();
        else
            stopJob();
    }

    void setType(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtData.FORMAT_STRING)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Reporting type leaf must have string format.");

        String newType = data.getString();
        if(newType == null || !(newType.equals("TM") || newType.equals("EV")))
            throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH, "Type string must 'TM' or 'EV'.");

        type = newType;
    }

    void setValue(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtData.FORMAT_INTEGER)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Reporting schedule value leaf must have integer format.");

        int newInt = data.getInt();

        if(newInt < 0)
            throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH, 
                                   "Reporting schedule value parameter must be non-negative.");

        value = data.getInt();
    }

    void setTrapRefId(String name, DmtData data, String nodeUri) throws DmtException {
        if(!trapRef.containsKey(name))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "The trap reference with the specified ID does not exist.");
        
        if(data.getFormat() != DmtData.FORMAT_STRING)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED,
                                   "Trap reference leaf must have string format.");
        
        // TODO check if data is "null" if this is valid in a DmtData
        trapRef.put(name, data.getString());
    }

    // TODO allow separate creation of TrapRef/<X>/TrapRefID nodes?
    void addTrapRef(String name, String nodeUri) throws DmtException {
        // TODO node name is not a good default, ref has to contain a / anyway
    	if(trapRef.put(name, name) != null)
    		throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
                                   "A trap reference with the given ID already exists.");
    }
    
    void deleteTrapRef(String name, String nodeUri) throws DmtException {
    	if(trapRef.remove(name) == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
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
        String[] kpiNames = new String[trapRefIds.length + 1];
        
        kpiNames[0] = path;
        for (int i = 0; i < trapRefIds.length; i++)
			kpiNames[i+1] = trapRefIds[i];
                
        job = monitorAdmin.startJob(serverId, kpiNames, schedule, count, false);
    }

    private void stopJob() {
        if(job == null)
            return;

        job.stop();
        job = null;
    }
}
