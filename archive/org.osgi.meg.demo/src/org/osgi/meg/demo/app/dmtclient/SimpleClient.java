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

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtEvent;
import info.dmtree.DmtEventListener;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.Uri;
import info.dmtree.notification.NotificationService;
import info.dmtree.registry.DmtServiceFactory;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.monitor.*;
//import info.dmtree.Acl;
//import java.io.File;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
//import org.w3c.dom.Document;
//import org.osgi.service.permissionadmin.PermissionInfo;
//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.Attributes;
//import info.dmtree.security.ProtectedUri;

public class SimpleClient implements ManagedService, ManagedServiceFactory,
        Monitorable, EventHandler
{
    private static final int ALL_EVENTS = DmtEvent.ADDED | DmtEvent.DELETED |
        DmtEvent.REPLACED | DmtEvent.RENAMED | DmtEvent.COPIED |
        DmtEvent.SESSION_OPENED | DmtEvent.SESSION_CLOSED;
    
    private BundleContext bc;

    private DmtAdmin factory;

    private MonitorListener monitorListener;

    private int updateCount = 0;

    private String dummy = "Dummy string 1";
    
    public SimpleClient(DmtAdmin factory, MonitorListener monitorListener, BundleContext bc) {
        this.factory = factory;
        this.monitorListener = monitorListener;
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
            System.out.println("DMT root: " + 
                    System.getProperty("info.dmtree.osgi.root"));
            
            System.out.println("mangle: "
                    + Uri.mangle("almafa,balmafa,hosszufa,megmegyegyfa"));
            
            // checking local (non-OSGi) API, i.e. service lookup via DmtServiceFactory
            DmtAdmin lAdmin = DmtServiceFactory.getDmtAdmin();
            DmtSession lSession = lAdmin.getSession("./OSGi/Configuration");
            System.out.println("Configuration trees: " + 
                    Arrays.asList(lSession.getChildNodeNames("")));
            lSession.close();
            
            SimpleListener simpleListener = new SimpleListener();
            lAdmin.addEventListener(DmtEvent.ADDED, ".", simpleListener);
            lAdmin.addEventListener("proba", ALL_EVENTS, ".", simpleListener);
            //lAdmin.removeEventListener(simpleListener);
            
            NotificationService lNotification = DmtServiceFactory.getNotificationService();
            //lNotification.sendNotification("proba", 1226, null, null);
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

            ma.startJob("2nd-change-listener", new String[] { pid + "/NumOfChanges" }, 2);
            ma.startScheduledJob("scheduled-listener", new String[] { pid + "/Dummy" }, 5, 1);
            
            System.out.println("Running jobs:");
            MonitoringJob[] jobs = ma.getRunningJobs();
            for(int i = 0; i < jobs.length; i++)
                System.out.println("* " + jobs[i]);
            
            ma.switchEvents("meg*/Num*", false);
            
            // need to wait a bit with updating Dummy, because events are not 
            // delivered to this bundle while its start() method is running
            delayedUpdate();
            
            bc.ungetService(monitorRef);

            ServiceReference configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
            if(configRef == null)
                throw new Exception("Cannot find ConfigurationAdmin service.");

            ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
            if(ca == null)
                throw new Exception("ConfigurationAdmin service no longer registered.");

            Configuration factConfig = ca.createFactoryConfiguration(pid);
            Hashtable properties = new Hashtable();
            properties.put("alma", "bela");
            factConfig.update(properties);
            factConfig.delete();
            
            Configuration dummy = ca.getConfiguration("CM_GENERATED_PID_0");
            System.out.println("Bad pid factory: " + dummy.getFactoryPid() +
                    "; pid: " + dummy.getPid());
            
            Configuration config = ca.getConfiguration(pid);
            properties = new Hashtable();
            properties.put("my.int.array", new int[] { 3, 2, 1 });
            
            /*
            Vector v = new Vector();
            v.add(null);
            v.add(null);
            properties.put("my.null.vector", v);
            
            v = new Vector();
            properties.put("my.empty.vector", v);

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

            /*
            config = ca.getConfiguration("org.osgi.impl.service.dmt.permissions", null);
            properties = new Hashtable();
            properties.put("server", new String[] { 
                    new PermissionInfo(AdminPermission.class.getName(), "", "").getEncoded() 
            });
            config.update(properties);
      
            ServiceReference pRef = bc.getServiceReference("org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin");
            if(pRef == null)
                throw new Exception("Cannot find DmtPrincipalPermissionAdmin service.");

            DmtPrincipalPermissionAdmin p = (DmtPrincipalPermissionAdmin) bc.getService(pRef);
            if(p == null)
                throw new Exception("DmtPrincipalPermissionAdmin service no longer registered.");
            
            properties = new Hashtable();
            properties.put("server", new PermissionInfo[] { 
                    new PermissionInfo(AdminPermission.class.getName(), "", "")
            });
            p.setPrincipalPermissions(properties);
            
            bc.ungetService(pRef);
            */
            
            config = ca.getConfiguration("proba123");
            System.out.println(config.getFactoryPid() == null ? "sima" : "factory");
            Configuration[] cs = ca.listConfigurations("(service.pid=proba123)");
            System.out.println(cs == null ? "nincs meg" : "megvan");
            properties = new Hashtable();
            properties.put("my.int.array", new int[] { 3, 2, 1 });
            config.update(properties);
            cs = ca.listConfigurations("(service.pid=proba123)");
            System.out.println(cs == null ? "nincs meg" : "megvan");
            
            config = ca.getConfiguration("veryveryveryveryveryveryveryloong");
            properties = new Hashtable();
            properties.put("myProperty", new Integer(1));
            config.update(properties);
            
            config = ca.getConfiguration("EvQbuBkml0LWohXyCCnYAwyP93E");
            properties = new Hashtable();
            properties.put("myProperty", new Integer(2));
            config.update(properties);


            // SESSION_OPENED event is not delivered to the handler here as 
            // the bundle is still in STARTING state
            DmtSession session = factory.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
            System.out.println("Retrieved session, id=" + session.getSessionId() +
                               ", lock type=" + session.getLockType() +
                               ", principal=" + session.getPrincipal() +
                               ", root=" + session.getRootUri());

            //session.getNodeValue("./OSGi/Configuration");
            
            //printTree();

            /*
            try {
                session.createInteriorNode(".");
            } catch(DmtException e) {
                System.out.println("createInteriorNode(\".\"): " + e.getMessage());
            }
            
            try {
                session.createLeafNode(".");
            } catch(DmtException e) {
                System.out.println("createLeafNode(\".\"): " + e.getMessage());
            }
            
            try {
                session.deleteNode(".");
            } catch(DmtException e) {
                System.out.println("deleteNode(\".\"): " + e.getMessage());
            }

            try {
                session.renameNode(".", "alma");
            } catch(DmtException e) {
                System.out.println("renameNode(\".\"): " + e.getMessage());
            }

            try {
                session.setNodeAcl(".", new Acl("Add=*&Replace=*&Get=*&Delete=*"));
                session.setNodeAcl(".", null);
            } catch(DmtException e) {
                System.out.println("setNodeAcl(\".\", null): " + e.getMessage());
            }
            
            try {
                session.setNodeAcl(".", new Acl());
            } catch(DmtException e) {
                System.out.println("setNodeAcl(\".\", new Acl()): " + e.getMessage());
            }
            */
            
            session.createInteriorNode("./OSGi/Configuration/future.service.pid");
            System.out.println("Created new configuration dictionary for service 'future.service.pid'.");

            Configuration[] configs = ca.listConfigurations(null);
            System.out.println("Configuration objects in Configuration Admin: (" + configs.length + ")");
            for(int i = 0; i < configs.length; i++)
                System.out.println("  " + i + ": " + configs[i].getProperties());

            bc.ungetService(configRef);

            /*
            session.createInteriorNode("./OSGi/Log/interior");
            System.out.println("Created node: ./OSGi/Log/interior");
            */

            System.out.println("Node types for management objects:");
            System.out.println("OSGi: " + session.getNodeType("./OSGi"));
            String[] roots = new String[] { "Deployment", "Policy", 
                    "Configuration", "Log", "Monitor", "Application" };
            for(int i = 0; i < roots.length; i++)
                System.out.println(roots[i] + ": " + 
                        session.getNodeType("./OSGi/" + roots[i]));
                
            session.close();
            System.out.println("Closed session.");

            //sessionOpenTests();
            
            //printTree();

            /*
            session = factory.getSession("OSGi/Log/");
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

            //session.execute("./OSGi/Deployment/install" ,"ftp://ftpuser:ftp@artemis/target.txt");

            session.close();
            System.out.println("Closed session.");
            */
            
            session = factory.getSession(ClientActivator.PLUGIN_ROOT);
            System.out.println("Retrieved session, id=" + session.getSessionId() +
                               ", lock type=" + session.getLockType() +
                               ", principal=" + session.getPrincipal() +
                               ", root=" + session.getRootUri());
            
            DmtData interiorValue = session.getNodeValue("");
            System.out.println("Interior node value: ");
            System.out.println("* Format: " + interiorValue.getFormat());
            System.out.println("* Format name: " + interiorValue.getFormatName());
            System.out.println("* Content: " + (String) interiorValue.getNode());

            session.setNodeValue("", new DmtData((Object) "Yo to y'all!"));
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));
            
            session.setNodeValue(ReadWriteDataPlugin.LEAF, 
                    new DmtData(ReadWriteDataPlugin.RAW_FORMAT_NAME, "I'm plain..."));
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));

            session.setNodeValue(ReadWriteDataPlugin.LEAF, 
                    new DmtData(ReadWriteDataPlugin.RAW_FORMAT_NAME, "I'm RAW!"));
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));

            session.setNodeValue("", null);
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));

            session.setDefaultNodeValue("");
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));

            session.setDefaultNodeValue(ReadWriteDataPlugin.LEAF);
            System.out.println("Updated value: " + session.getNodeValue(
                    ReadWriteDataPlugin.LEAF));

            System.out.println("Titles: \"" + session.getNodeTitle("") + "\", " +
                    "\"" + session.getNodeTitle(ReadWriteDataPlugin.LEAF) + "\"");
            
            System.out.println("Version: " + session.getNodeVersion("") +
                    ", Timestamp: " + session.getNodeTimestamp(""));
            
            session.close();
            
        } catch(DmtException e) {
            System.out.println("DMT exception caught in client:");
            e.printStackTrace(System.out);
        } catch(Exception e) {
            System.out.println("Exception caught in client:");
            e.printStackTrace(System.out);
        }
    }

    private void delayedUpdate() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                setDummy("Dummy string 2");
                setDummy("Dummy string 3");   
            }
        }).start();
    }
    
    /*
    private void sessionOpenTests() {
        // parallel sessions
        sessionOpenTest("./OSGi/Monitor", DmtSession.LOCK_TYPE_EXCLUSIVE,
                        "./OSGi/Configuration", DmtSession.LOCK_TYPE_EXCLUSIVE);
        
        sessionOpenTest("./OSGi",     DmtSession.LOCK_TYPE_SHARED,
                        "./OSGi/Configuration", DmtSession.LOCK_TYPE_SHARED);

        // error in session initialization
        sessionOpenTest("./OSGi/manci", DmtSession.LOCK_TYPE_SHARED,
                        "./OSGi/Configuration", DmtSession.LOCK_TYPE_EXCLUSIVE);
        
        // conflicting sessions
        sessionOpenTest("./OSGi",     DmtSession.LOCK_TYPE_SHARED,
                        "./OSGi/Configuration", DmtSession.LOCK_TYPE_EXCLUSIVE);
        
        System.out.println("-----");
        SessionThread a = new SessionThread(factory, "./OSGi/Monitor", DmtSession.LOCK_TYPE_EXCLUSIVE);
        SessionThread b = new SessionThread(factory, "./OSGi",         DmtSession.LOCK_TYPE_ATOMIC);
        SessionThread c = new SessionThread(factory, "./OSGi/Monitor", DmtSession.LOCK_TYPE_SHARED);
        SessionThread d = new SessionThread(factory, "./OSGi",         DmtSession.LOCK_TYPE_SHARED);
        
        a.start();
        b.start();
        c.start();
        d.start();
        
        sleep(500);
        
        a.halt();
        b.halt();
        c.halt();
        d.halt();
    }

    private void sessionOpenTest(String root1, int lockMode1, 
                                 String root2, int lockMode2) {
        System.out.println("-----");
        SessionThread a = new SessionThread(factory, root1, lockMode1);
        SessionThread b = new SessionThread(factory, root2, lockMode2);
        
        a.start();
        b.start();
        sleep(500);
        
        a.halt();
        b.halt();
        sleep(500);
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
            case DmtData.FORMAT_NULL:   
                System.out.println("NULL"); 
                break;
            case DmtData.FORMAT_XML: 
                System.out.print("XML STRING: '" + value.getXml() + "'");
                break;
            case DmtData.FORMAT_STRING: 
                System.out.println("STRING: '" + value.getString() + "'");
                break;
            case DmtData.FORMAT_BOOLEAN:
                System.out.println("BOOLEAN: " + value.getBoolean());
                break;
            case DmtData.FORMAT_INTEGER:
                System.out.println("INTEGER: " + value.getInt());
                break;
            case DmtData.FORMAT_FLOAT:
                System.out.println("FLOAT: " + value.getFloat());
                break;
            case DmtData.FORMAT_BINARY:
                System.out.println("BINARY: '" + new String(value.getBinary()) + "'");
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
    
    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {}
    }
    */

    static class SessionThread extends Thread {
        private DmtAdmin factory;
        private String root;
        private int lockMode;
        private boolean quit;

        SessionThread(DmtAdmin factory, String root, int lockMode) {
            this.factory = factory;
            this.root = root;
            this.lockMode = lockMode;
        }
        
        public synchronized void run() {
            String params = "(" + root + ", " + lockMode + ")";

            DmtSession session;
            try {
                System.out.println("Opening session " + params);
                session = factory.getSession(root, lockMode);
                System.out.println("Opened session " + params);
            } catch (DmtException e) {
                e.printStackTrace(System.out);
                return;
            }

            quit = false;
            while(!quit) {
                try {
                    wait();
                    quit = true;
                } catch (InterruptedException e) {}
            }

            try {
                System.out.println("Closing session " + params);
                session.close();
                System.out.println("Closed session " + params);
            } catch (DmtException e) {
                e.printStackTrace(System.out);
                return;
            }
        }

        public synchronized void halt() {
            notify();
        }
    }

    public String getName()
    {
        return "Managed Service Factory test of SimpleClient";
    }
    
    public void deleted(String pid)
    {
        System.out.println("Factory configuration '" + pid + "' deleted.");
    }
    
    public void updated(String pid, Dictionary properties)
    {
        String factoryPid = (String) 
                properties.get(ConfigurationAdmin.SERVICE_FACTORYPID);
        
        System.out.println("Factory configuration '" + pid + "' updated.");
        System.out.println("PIDs: configuration: " + 
                properties.get("service.pid") + "; factory: " + factoryPid);
    }
    
    public void updated(Dictionary properties)
    {
        System.out.println("Received new configuration dictionary: " + properties);

        setUpdateCount(updateCount + 1);
    }

    public StatusVariable getStatusVariable(String id)
    {
        if(id.equals("NumOfChanges"))
            return getUpdateVar();
        
        if(id.equals("CPULoad"))
            return getLoadVar();
        
        if(id.equals("Dummy"))
            return getDummyVar();
        
        throw new IllegalArgumentException("Status Variable '" + id + 
                "' not found in Monitorable '" + ClientActivator.SERVICE_PID + 
                "'.");
    }
 
	public String[] getStatusVariableNames()
    {
        return new String[] { "NumOfChanges", "CPULoad", "Dummy" };
    }

    public boolean notifiesOnChange(String id) 
    {
        if(id.equals("NumOfChanges"))
            return true;
        
        if(id.equals("CPULoad"))
            return false;

        if(id.equals("Dummy"))
            return true;

        throw new IllegalArgumentException("Status Variable '" + id + 
                "' not found in Monitorable '" + ClientActivator.SERVICE_PID + 
                "'.");
    }

    public boolean resetStatusVariable(String id)
    {
        if(id.equals("NumOfChanges")) {
            setUpdateCount(0);
            return true;
        }

        if(id.equals("CPULoad"))
            return false;
        
        if(id.equals("Dummy"))
            return false;

        throw new IllegalArgumentException("Status Variable '" + id + 
                "' not found in Monitorable '" + ClientActivator.SERVICE_PID + 
                "'.");
    }
    
    public String getDescription(String id) {
        if(id.equals("NumOfChanges"))
            return "Number of times the configuration has been updated.";
        
        if(id.equals("CPULoad"))
            return "CPU load percentage indicator (dummy)";
        
        if(id.equals("Dummy"))
            return "Just a string.";

        throw new IllegalArgumentException("Status Variable '" + id + 
                "' not found in Monitorable '" + ClientActivator.SERVICE_PID + 
                "'.");
    }

    private void setUpdateCount(int newUpdateCount)
    {
        updateCount = newUpdateCount;
        monitorListener.updated(ClientActivator.SERVICE_PID, getUpdateVar());
    }

    private void setDummy(String newDummy)
    {
        dummy = newDummy;
        monitorListener.updated(ClientActivator.SERVICE_PID, getDummyVar());
    }
    
    private static Random random = new Random();

    private StatusVariable getLoadVar() {
        return new StatusVariable("CPULoad", 
                       StatusVariable.CM_SI, random.nextInt(101));
    }

    private StatusVariable getUpdateVar() {
        return new StatusVariable("NumOfChanges", 
                StatusVariable.CM_CC, updateCount);
    }

    private StatusVariable getDummyVar() {
        return new StatusVariable("Dummy", StatusVariable.CM_SI, dummy);
    }
    
    /*
    private StatusVariable getDoubles() {
        double[] doubles = new double[random.nextInt(4)];
        for(int i = 0; i < doubles.length; i++)
			doubles[i] = random.nextDouble();
    	return new StatusVariable(ClientActivator.SERVICE_PID, "Doubles",
                       "Some pretty doubles!", StatusVariable.CM_SI, doubles);
    }
    */

    public void handleEvent(Event event) {
        String topic = event.getTopic();

        if(topic.equals("org/osgi/service/monitor/MonitorEvent"))
            monitorEvent(event);
        else if(topic.startsWith("info/dmtree/DmtEvent/"))
            dmtEvent(event);
        else
            System.out.println("Unexpected event received on topic '" + topic + "'.");
    }

	private void dmtEvent(Event event) {
	    String[] nodes = (String[])event.getProperty("nodes");
        String[] newNodes = (String[]) event.getProperty("newnodes");
        System.out.println("DMT event for session '" + event.getProperty("session.id") + 
                "' received on topic '" + event.getProperty(EventConstants.EVENT_TOPIC) + "'" +
                (nodes == null ? "" : " for nodes '" + Arrays.asList(nodes) + "'") +
                (newNodes == null ? "" : ", new node names are '" + Arrays.asList(newNodes) + "'") + ".");
	}

	private void monitorEvent(Event event) {
        String path = event.getProperty("mon.monitorable.pid") + "/" + 
                event.getProperty("mon.statusvariable.name");
        Object listeners = event.getProperty("mon.listener.id");

        if(listeners == null)
            System.out.println("Change event received for Status Variable '" + 
                    path + "'.");
        else {
            String listenerStr;
            if(listeners instanceof String)
                listenerStr = (String) listeners;
            else                // String[]
                listenerStr = Arrays.asList((Object[])listeners).toString();
            System.out.println("Monitor event received for Status Variable '" +
                    path + "' addressed to the following listener(s): " + 
                    listenerStr);
        }

        String error = null;
        StatusVariable var = null;

        // TODO get var from event
        try {
            ServiceReference ref = bc.getServiceReference(MonitorAdmin.class.getName());
            if(ref != null) {
                MonitorAdmin ma = (MonitorAdmin) bc.getService(ref);
                if(ma != null)
                    var = ma.getStatusVariable(path);
                else
                    error = "MonitorAdmin service no longer registered.";
            } else
                error = "Cannot find MonitorAdmin service.";
        } catch(IllegalArgumentException e) {
            error = "Status Variable for given path no longer exists.";
        }

        if(error != null)
            System.out.println("Error retrieving new value of Status Variable from admin: " + error);
        else
            System.out.println("Value in admin: " + var);
        
        
        String eventVar = (String) event.getProperty("mon.statusvariable.value");

        if(eventVar == null)
            System.out.println("Error retrieving new value of Status Variable from event.");
        else
            System.out.println("Value in event: " + eventVar);

        // Temporary code for testing the filterability of an event property
        // with ProtectedUri class
        //String proba = ((ProtectedUri) event.getProperty("proba")).getUri();
        //if(proba == null)
        //    System.out.println("Error retrieving value of proba.");
        //else
        //    System.out.println("Value of proba: " + proba);
    }
    
    static class SimpleListener implements DmtEventListener {
        public void changeOccurred(DmtEvent event) {
            System.out.println("Received event on DmtEventListener: " + event);
        }
    }
}
