package org.osgi.impl.service.monitor;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.monitor.*;
import org.osgi.util.tracker.ServiceTracker;



public class MonitorPlugin implements DmtDataPlugIn
{
    private BundleContext bc;
    private ServiceTracker tracker;
    private MonitorAdminImpl monitorAdmin;

    public MonitorPlugin(BundleContext bc, ServiceTracker tracker, MonitorAdminImpl monitorAdmin) {
        this.bc = bc;
        this.tracker = tracker;
        this.monitorAdmin = monitorAdmin;
    }


    //----- DmtDataPlugIn methods -----//

    public void open(int lockMode, DmtSession session) throws DmtException {
        // TODO
    }

    public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
        open(lockMode, session);
    }

    public DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic) throws DmtException {
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
                                               false, false, null, null, DmtDataType.STRING);
            
            if(path[2].equals("CM")) {
                DmtData[] validValues = 
                    new DmtData[] { new DmtData("CC"), new DmtData("DER"), 
                                    new DmtData("GAUGE"), new DmtData("SI") };
                return new MonitorMetaNodeImpl("Collection method of data in the Performance Indicator.",
                                               false, false, null, validValues, DmtDataType.STRING);
            }
            
            if(path[2].equals("Results"))
                return new MonitorMetaNodeImpl("Current value of the Performance Indicator.",
                                               false, false, null, null, DmtDataType.XML);

            // path[2].equals("Server")

            return new MonitorMetaNodeImpl("Root node for server monitoring requests.", false, true, false, false);
        }

        Server server = kpi.getServer(path[3], nodeUri);

        if(path.length == 4)
            return new MonitorMetaNodeImpl("Root node of a server monitoring request.", true, false, true, false);

        if(path.length == 5) {
            if(path[4].equals("ServerID"))
                return new MonitorMetaNodeImpl("Identifier of the DM server that should receive the requested " +
                                               "monitoring data.", true, false, null, null, DmtDataType.STRING);

            if(path[4].equals("Enabled"))
                return new MonitorMetaNodeImpl("A switch to start and stop monitoring.", 
                                               true, false, null, null, DmtDataType.BOOLEAN);

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
                    DmtData defaultData = new DmtData("TM");
                    DmtData[] validValues = new DmtData[] { defaultData, new DmtData("EV") };
                    return new MonitorMetaNodeImpl("Indicates if the data reporting is time or event based.",
                                                   true, false, defaultData, validValues, DmtDataType.STRING);
                }

                // path[5].equals("Value")

                return new MonitorMetaNodeImpl("Time or occurrence number parameter of the request (depending on " +
                                               "the type).", true, false, new DmtData(60), null, DmtDataType.INTEGER);
            }

            // path[4].equals("TrapRef")

            server.getTrapRefId(path[5], nodeUri);

            return new MonitorMetaNodeImpl("Placeholder for a reference to other monitoring data.", 
                                           true, false, true, false);
        }
            
        // path.length == 7
        return new MonitorMetaNodeImpl("A reference to other monitoring data.", 
                                       true, false, null, null, DmtDataType.STRING);
    }

    public boolean supportsAtomic() {
        return false;
    }


    //----- Dmt methods -----//

    public void rollback() throws DmtException {
        // this should never be called if supportsAtomic is false
    }

    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        // TODO
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "Title property not supported.");
    }

    public void setNode(String nodeUri, DmtData data) throws DmtException {
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

        // TODO set trap ref id leaf, if the node exists
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
        
        // TODO delete trap ref interior node
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
            // TODO add trap ref interior node
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

    public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
        String[] path = prepareUri(nodeUri);

        if(path.length < 4) // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");
        
        if(path.length == 4)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                                   "Cannot add leaf node at the specified position.");
        
        // TODO replace this with the canAdd check in DmtAdmin based on the meta-data
        if(path.length == 5 || !path[4].equals("TrapRef"))
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add nodes at the specified position.");

        // path[4].equals("TrapRef") 

        if(path.length == 6)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                                   "Cannot add leaf node at the specified position.");
        
        // TODO adding TrapRefID leaf nodes
    }

    public void clone(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
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
            
            // TODO return result in proper xml format

            int type = kpiValue.getType();
            switch(type) {
            case KPI.TYPE_INTEGER: return new DmtData(kpiValue.getInteger());
            case KPI.TYPE_STRING:  return new DmtData(kpiValue.getString());
            case KPI.TYPE_OBJECT:
            case KPI.TYPE_FLOAT:
            default:
                throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                       "KPI type '" + type + "' not supported yet.");
            }
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
                if(children[i] == null) // TODO can this ever happen?
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

        return new String[] { "TrapRefId" };
    }
    

    //----- Private utility methods -----//

    private Monitorable getMonitorable(String id, String nodeUri) throws DmtException {
        // TODO check ID for filter-illegal characters (it should already be checked for DMT-illegal chars by isNodeUri)
        ServiceReference[] refs;
        try {
            refs = bc.getServiceReferences(Monitorable.class.getName(), "(service.pid=" + id + ")");
        } catch(InvalidSyntaxException e) {
            // should not be reached if the above TODO is done
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
        int rootLen = MonitorAdminActivator.PLUGIN_ROOT.length();
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

// TODO delete item from serverLists if a KPI is no longer exported or if a Monitorable is unregistered
// TODO do not store monitorAdmin refs everywhere
class KpiWrapper {
    private static Hashtable serverLists = new Hashtable();

    private String path;
    private KPI kpi;
    private MonitorAdmin monitorAdmin;

    private Hashtable servers;

    KpiWrapper(String path, KPI kpi, MonitorAdmin monitorAdmin) {
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
        if(!servers.containsKey(nodeName))
            throw new DmtException(nodeName, DmtException.NODE_NOT_FOUND, 
                                   "No server request exists for the specified node name.");

        servers.remove(nodeName);
    }
}


class Server {
    private String nodeName;
    private String serverId;
    private boolean enabled;
    private String type;
    private int value;          // should be 'long' to store unsigned int
    private Hashtable trapRef;

    private MonitorAdmin monitorAdmin;
    private MonitoringJob job;

    private String path;

    Server(String nodeName, String path, MonitorAdmin monitorAdmin) {
        this.nodeName = nodeName;
        this.path = path;
        this.monitorAdmin = monitorAdmin;
        
        job = null;

        serverId = nodeName;
        enabled = false;
        type = "TM";
        value = 60;
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
        if(data.getFormat() != DmtDataType.STRING)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Server ID leaf must have string format.");
        
        if(data.getString() == null)
            throw new DmtException(nodeUri, DmtException.INVALID_DATA, "Server ID string must be non-null.");

        serverId = data.getString();
    }

    void setEnabled(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtDataType.BOOLEAN)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Enabled leaf must have boolean format.");

        enabled = data.getBoolean();

        // TODO use some back door to set the initiator (principal) of the job
        if(enabled)
            job = monitorAdmin.startJob(new String[] { path }, value / 60, 0);
        else {
            if(job != null)
                job.stop();
            job = null;
        }
    }

    void setType(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtDataType.STRING)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Reporting type leaf must have string format.");
        
        String newType = data.getString();
        if(newType == null || !(newType.equals("TM") || newType.equals("EV")))
            throw new DmtException(nodeUri, DmtException.INVALID_DATA, "Type string must 'TM' or 'EV'.");

        if(newType.equals("EV"))
            throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, 
                                   "Event based reporting currently not supported.");
            
        type = newType;         // "TM"
    }

    void setValue(DmtData data, String nodeUri) throws DmtException {
        if(data.getFormat() != DmtDataType.INTEGER)
            throw new DmtException(nodeUri, DmtException.FORMAT_NOT_SUPPORTED, 
                                   "Reporting schedule value leaf must have integer format.");
        
        int newInt = data.getInt();

        if(newInt < 0)
            throw new DmtException(nodeUri, DmtException.INVALID_DATA, 
                                   "Reporting schedule value parameter must be non-negative.");

        if(newInt == 0)
            throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
                                   "Instant event reporting currently not supported.");

        if(newInt % 60 != 0)
            throw new DmtException(nodeUri, DmtException.INVALID_DATA,
                                   "Interval must be a round minute (divisible by 60).");

        value = data.getInt();
    }

    // TODO TrapRef manipulation methods
}
