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
package org.osgi.meg.demo.app.dmtclient;

//import java.io.File;
import java.util.*;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.service.dmt.*;
import org.osgi.service.event.*;
import org.osgi.service.monitor.*;
//import org.w3c.dom.Document;
//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.Attributes;

public class SimpleClient implements ManagedService, Monitorable, EventHandler
{
    private BundleContext bc;

    private DmtAdmin factory;

    private UpdateListener updateListener;

    private int updateCount = 0;

    public SimpleClient(DmtAdmin factory, UpdateListener updateListener, BundleContext bc) {
        this.factory = factory;
        this.updateListener = updateListener;
        this.bc = bc;        
    }

    /*
    private Object getService(Class serviceClass) throws Exception {
        String fullName = serviceClass.class.getName();
        String shortName = fullName.substring(fullName.lastIndexOf('.') + 1);

        ServiceReference ref = bc.getServiceReference(fullName);
        if(ref == null)
            throw new Exception("Cannot find " + shortName + " service.");

        Object service = bc.getService(ref);
        if(service == null)
            throw new Exception(shortName + " service no longer registered.");

        return service;
    }
    */

    public void run()
    {
        String pid = ClientActivator.SERVICE_PID;

        try {
            /*

            ServiceReference refs[] = bc.getServiceReferences( SAXParserFactory.class.getName(), 
                    "(&(parser.namespaceAware=true)(parser.validating=false))" ); 

            if(refs == null)
                throw new Exception("Cannot find SaxParserFactory service.");

            SAXParserFactory saxParserFactory = (SAXParserFactory) bc.getService(refs[0]);
            if(saxParserFactory == null)
                throw new Exception("No matching SAXParserFactory service found.");
                        
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new File("/tmp/proba.xml"), new DefaultHandler() {
                public void characters(char[] chars, int start, int len) {
                    System.out.print("chars: ");
                    System.out.println(chars);
                }
                public void startElement(String uri, String localName, 
                                         String qName, Attributes attributes) {
                    System.out.println("startElement(" + uri + ", ...)" );
                }
            });
            */

            /*
            ServiceReference domRef = bc.getServiceReference(DocumentBuilderFactory.class.getName());
            if(domRef == null)
                throw new Exception("Cannot find DocumentBuilderFactory service.");
            
            DocumentBuilderFactory domBuilderFactory = (DocumentBuilderFactory) bc.getService(domRef);
            if(domBuilderFactory == null)
                throw new Exception("DocumentBuilderFactory service no longer registered.");
            
            //domBuilderFactory.setValidating(false);
            DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
            
            Document dom = domBuilder.parse(new File("/tmp/proba.xml"));
            
            System.out.println("Root tag in XML: " + dom.getDocumentElement().getTagName());
            
            bc.ungetService(domRef);
            */
            
            /*
            Filter filter = bc.createFilter("(a=b)");

            Dictionary d = new Hashtable();
            d.put("a", new String[] { "b", "c", "d" });
            System.out.println("Element: " + filter.match(d));

            d = new Hashtable();
            d.put("a", new String[] {});
            System.out.println("Empty: " + filter.match(d));
            */

            ServiceReference monitorRef = bc.getServiceReference(MonitorAdmin.class.getName());
            if(monitorRef == null)
                throw new Exception("Cannot find MonitorAdmin service.");

            MonitorAdmin ma = (MonitorAdmin) bc.getService(monitorRef);
            if(ma == null)
                throw new Exception("MonitorAdmin service no longer registered.");

            //ma.startJob("schedule-listener", new String[] { pid + "/NumOfChanges" }, 60, 5);

            ma.startJob("2nd-change-listener", new String[] { pid + "/NumOfChanges" }, 0, 2);

            bc.ungetService(monitorRef);

            ServiceReference configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
            if(configRef == null)
                throw new Exception("Cannot find ConfigurationAdmin service.");

            ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
            if(ca == null)
                throw new Exception("ConfigurationAdmin service no longer registered.");

            Configuration config = ca.getConfiguration(pid);
            Hashtable properties = new Hashtable();
            properties.put("my.int.array", new int[] { 3, 2, 1 });
            /*
            properties.put("my.bigBoolean", new Boolean(true));
            properties.put("my.byte.array", "bytes".getBytes());
            properties.put("my.Integer", new Integer(42));
            properties.put("my.Integer.array", new Integer[] { new Integer(4), new Integer(5) });
            properties.put("my.boolean.array", new boolean[] { false });
            properties.put("my.bigBoolean.array", new Boolean[] { new Boolean(true), new Boolean(false) });

            Vector v = new Vector();
            v.add(new Double(1.1));
            v.add(new Double(2.3));
            v.add(new Double(5.8));
            properties.put("my.Double.vector", v);

            v = new Vector();
            v.add(new Boolean(true));
            properties.put("my.Boolean.vector", v);

            v = new Vector();
            v.add(new Character('c'));
            v.add(new Long(124));
            properties.put("my.mixed.vector", v);
            */
            config.update(properties);

            DmtSession session = factory.getSession(".");
            System.out.println("Retrieved session, id=" + session.getSessionId() +
                               ", lock type=" + session.getLockType() +
                               ", principal=" + session.getPrincipal() +
                               ", root=" + session.getRootUri());
            //printTree();

            session.createInteriorNode("./OSGi/cfg/future.service.pid");
            System.out.println("Created new configuration dictionary for service 'future.service.pid'.");

            Configuration[] configs = ca.listConfigurations(null);
            System.out.println("Configuration objects in Configuration Admin: (" + configs.length + ")");
            for(int i = 0; i < configs.length; i++)
                System.out.println("  " + i + ": " + configs[i].getProperties());

            bc.ungetService(configRef);

            /*
            session.createInteriorNode("./OSGi/log/interior");
            System.out.println("Created node: ./OSGi/log/interior");
            */

            session.close();
            System.out.println("Closed session.");

            //printTree();

            /*
            session = factory.getSession("OSGi/log/");
            System.out.println("Retrieved session, id=" + session.getSessionId() +
                               ", lock type=" + session.getLockType() +
                               ", principal=" + session.getPrincipal() +
                               ", root=" + session.getRootUri());

            session.createLeafNode("interior/stringLeaf", new DmtData("A string leaf"));
            session.createLeafNode("interior/binaryLeaf", new DmtData("Hi".getBytes()));
            session.createLeafNode("interior/boolLeaf", new DmtData(false));
            session.createLeafNode("interior/intLeaf", new DmtData(13));
            session.createLeafNode("interior/nullLeaf", new DmtData());
            System.out.println("Created leaf nodes.");
            //printTree();

            try {
                session.createLeafNode("interior/dummy/leaf", new DmtData());
            } catch(DmtException e) {
                if(e.getCode() != DmtException.NODE_NOT_FOUND)
                    throw e;
            }

            //session.execute("./OSGi/dm/deploy/install" ,"ftp://ftpuser:ftp@artemis/target.txt");

            session.close();
            System.out.println("Closed session.");
            */

        } catch(DmtException e) {
            System.out.println("DMT exception caught in client:");
            e.printStackTrace(System.out);
        } catch(Exception e) {
            System.out.println("Exception caught in client:");
            e.printStackTrace(System.out);
        }
    }

    private void printTree()
    {
        DmtSession session;
        try {
            session = factory.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        } catch(DmtException e) {
            System.out.println("Error retrieving DM tree:");
            e.printStackTrace(System.out);
            return;
        }
        System.out.println("===== Tree dump =====");
        printTree(session, "", ".", 0);
        System.out.println("=====================");
        try {
            session.close();
        } catch(DmtException e) {
            System.out.println("Error closing session:");
            e.printStackTrace(System.out);
            return;
        }
    }

    private void printTree(DmtSession session, String base, String name, int indent)
    {
        indent(indent);

        String uri = base + name;

        boolean isLeaf;
        try {
            isLeaf = session.isLeafNode(uri);
        } catch(DmtException e) {
            System.out.println("<specified name '" + name + "' does not point to a node>");
            e.printStackTrace(System.out);
            return;
        }

        System.out.println(name);

        if(isLeaf) {
            indent(indent + 2);
            DmtData value;
            try {
                value = session.getNodeValue(uri);
            } catch(DmtException e) {
                System.out.println("Error retrieving leaf node value for '" + uri + "':");
                e.printStackTrace(System.out);
                return;
            }
            printLeaf(value, uri);
        }
        else {
            String[] children;
            try {
                children = session.getChildNodeNames(uri);
            } catch(DmtException e) {
                System.out.println("Error retrieving child node names for internal node '" + uri + "':");
                e.printStackTrace(System.out);
                return;
            }
            for(int i = 0; i < children.length; i++)
                printTree(session, uri + "/", children[i], indent + 2);
        }
    }

    private void printLeaf(DmtData value, String uri)
    {
        if(value == null) {
            System.out.println("<null data>");
            return;
        }

        try {
            int format = value.getFormat();
            switch(format) {
            case DmtDataType.NULL:   
                System.out.println("NULL"); 
                break;
            case DmtDataType.XML: 
                System.out.print("XML "); // display as STRING in next branch
            case DmtDataType.STRING: 
                System.out.println("STRING (mime=" + value.getMimeType() + "): '" + value.getString() + "'");
                break;
            case DmtDataType.BOOLEAN:
                System.out.println("BOOLEAN: " + value.getBoolean());
                break;
            case DmtDataType.INTEGER:
                System.out.println("INTEGER: " + value.getInt());
                break;
            case DmtDataType.BINARY:
                System.out.println("BINARY (mime=" + value.getMimeType() + "): '" + new String(value.getBinary()) + "'");
                break;
            default:
                System.out.println("<unknown data format identifier '" + format + "'>");
            }
        } catch(DmtException e) {
            System.out.println("Error retrieving leaf node value information for '" + uri + "':");
            e.printStackTrace(System.out);
        }
    }

    private void indent(int indent)
    {
        for(int i = 0; i < indent; i++)
            System.out.print(' ');
    }

    public void updated(Dictionary properties)
    {
        System.out.println("Received new configuration dictionary: " + properties);

        setUpdateCount(updateCount + 1);
    }

    public KPI getKpi(String id)
    {
        if(matchingId(id, "NumOfChanges"))
            return getUpdateKpi();
        
        if(matchingId(id, "CPULoad"))
            return getLoadKpi();
        
        if(matchingId(id, "Doubles"))
            return getDoubles();
        
        throw new IllegalArgumentException("KPI '" + id + "' not found in Monitorable '" + 
                                           ClientActivator.SERVICE_PID + "'.");
    }
 
	public String[] getKpiNames()
    {
        return new String[] { "NumOfChanges", "CPULoad", "Doubles" };
    }

    public String[] getKpiPaths()
    {
        return new String[] {
            ClientActivator.SERVICE_PID + "/NumOfChanges",
            ClientActivator.SERVICE_PID + "/CPULoad",
            ClientActivator.SERVICE_PID + "/Doubles"
        };
    }

    public KPI[] getKpis()
    {
        return new KPI[] { getUpdateKpi(), getLoadKpi(), getDoubles() };
    }

    public boolean notifiesOnChange(String id) 
    {
        if(matchingId(id, "NumOfChanges"))
            return true;
        
        if(matchingId(id, "CPULoad") || matchingId(id, "Doubles"))
            return false;

        throw new IllegalArgumentException("KPI '" + id + "' not found in Monitorable '" + 
                                           ClientActivator.SERVICE_PID + "'.");
    }

    public boolean resetKpi(String id)
    {
        if(matchingId(id, "NumOfChanges")) {
            setUpdateCount(0);
            return true;
        }

        if(matchingId(id, "CPULoad") || matchingId(id, "Doubles"))
            return false;

        throw new IllegalArgumentException("KPI '" + id + "' not found in Monitorable '" + 
                                           ClientActivator.SERVICE_PID + "'.");
    }

    private boolean matchingId(String id, String name) {
        return id.equals(name)
                || id.equals(ClientActivator.SERVICE_PID + '/' + name);
    }
    
    private void setUpdateCount(int newUpdateCount)
    {
        updateCount = newUpdateCount;
        updateListener.updated(getUpdateKpi());
    }

    private static Random random = new Random();

    private KPI getLoadKpi() {
        return new KPI(ClientActivator.SERVICE_PID, "CPULoad", 
                       "CPU load percentage indicator (dummy)", KPI.CM_SI,
                       random.nextInt(101));
    }

    private KPI getUpdateKpi() {
        return new KPI(ClientActivator.SERVICE_PID, "NumOfChanges", 
                       "Number of times the configuration has been updated.", KPI.CM_CC, updateCount);
    }

    private KPI getDoubles() {
        double[] doubles = new double[random.nextInt(4)];
        for(int i = 0; i < doubles.length; i++)
			doubles[i] = random.nextDouble();
    	return new KPI(ClientActivator.SERVICE_PID, "Doubles",
                       "Some pretty doubles!", KPI.CM_SI, doubles);
    }

    public void handleEvent(Event event) {
        String topic = event.getTopic();

        if(topic.equals("org/osgi/service/monitor/MonitorEvent"))
            monitorEvent(event);
        else if(topic.startsWith("org/osgi/service/dmt/DmtEvent/"))
            dmtEvent(event);
        else
            System.out.println("Unexpected event received on topic '" + topic + "'.");
    }

	private void dmtEvent(Event event) {
        System.out.println("DMT event for session '" + event.getProperty("session.id") + 
                           "' received on topic '" + event.getProperty("topic") +
                           "' for nodes '" + Arrays.asList((String[])event.getProperty("nodes")) + "'.");
	}

	private void monitorEvent(Event event) {
        String path = event.getProperty("monitorable.pid") + "/" + event.getProperty("kpi.name");
        Object listeners = event.getProperty("listener.id");

        if(listeners == null)
            System.out.println("Change event received for KPI '" + path + "'.");
        else {
            String listenerStr;
            if(listeners instanceof String)
                listenerStr = (String) listeners;
            else                // String[]
                listenerStr = Arrays.asList((Object[])listeners).toString();
            System.out.println("Monitor event received for KPI '" + path + 
                               "' addressed to the following listener(s): " + listenerStr);
        }

        String error = null;
        KPI kpi = null;

        try {
            ServiceReference ref = bc.getServiceReference(MonitorAdmin.class.getName());
            if(ref != null) {
                MonitorAdmin ma = (MonitorAdmin) bc.getService(ref);
                if(ma != null)
                    kpi = ma.getKPI(path);
                else
                    error = "MonitorAdmin service no longer registered.";
            } else
                error = "Cannot find MonitorAdmin service.";
        } catch(IllegalArgumentException e) {
            error = "KPI for given path no longer exists.";
        }

        if(error != null)
            System.out.println("Error retrieving new value of KPI: " + error);
        else
            System.out.println("Value: " + kpi);
    }
}
