package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.osgi.impl.service.deploymentadmin.api.DownloadAgent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DownloadThread extends Thread {
    
    public static final int OK                        =  0;
    public static final int NOT_ACCEPTABLE_CONTENT    =  PluginConstants.RESULT_NOT_ACCEPTABLE_CONTENT;
    public static final int DWNL_SERVER_NOT_AVAILABLE =  PluginConstants.RESULT_DWNL_SERVER_NOT_AVAILABLE;
    public static final int DWNLD_DESCR_ERROR         =  PluginConstants.RESULT_DWNLD_DESCR_ERROR;
    public static final int USER_CANCELLED            =  PluginConstants.RESULT_USER_CANCELLED;
    public static final int UNDEFINED_ERROR           =  PluginConstants.RESULT_UNDEFINED_ERROR;
    
    private SAXParser     parser;
    private Handler       handler = new Handler();
    private DownloadAgent dwnlAgent;
    private String        descrUri;
    
    private InputStream  inputStream;
    private int          status = -1;
    
    private class Handler extends DefaultHandler {
        
        private String       actElement;

        private StringBuffer objectURI;
        private StringBuffer name;
        private StringBuffer vendor;
        private StringBuffer size;
        private StringBuffer type;
        private StringBuffer description;
        
        public String getObjectURI() {
            if (null == objectURI)
                return null;
            return objectURI.toString().trim();
        }
        
        public String getName() {
            if (null == name)
                return null;
            return name.toString().trim();
        }
        
        public String getVendor() {
            if (null == vendor)
                return null;
            return vendor.toString().trim();
        }

        public String getSize() {
            if (null == size)
                return null;
            return size.toString().trim();
        }

        public String getType() {
            if (null == type)
                return null;
            return type.toString().trim();
        }
        
        public String getDescription() {
            if (null == description)
                return null;
            return description.toString().trim();
        }
        
        public String getMIMEType() {
            return handler.getType();
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException 
        {
            actElement = localName;
        }
        
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (actElement.equals("objectURI")) {
                if (null == objectURI)
                    objectURI = new StringBuffer();
                String s = new String(ch, start, length).trim();
                objectURI.append(s);
            } else if (actElement.equals("name")) {
                if (null == name)
                    name = new StringBuffer();
                String s = new String(ch, start, length).trim();
                name.append(s);
            } else if (actElement.equals("vendor")) {
                if (null == vendor)
                    vendor = new StringBuffer();
                String s = new String(ch, start, length).trim();
                vendor.append(s);
            } else if (actElement.equals("size")) {
                if (null == size)
                    size = new StringBuffer();
                String s = new String(ch, start, length).trim();
                size.append(s);
            } else if (actElement.equals("type")) {
                if (null == type)
                    type = new StringBuffer();
                String s = new String(ch, start, length).trim();
                type.append(s);
            } else if (actElement.equals("description")) {
                if (null == description)
                    description = new StringBuffer();
                String s = new String(ch, start, length).trim();
                description.append(s);
            }
        }

    }

    DownloadThread(SAXParser parser, DownloadAgent dwnlAgent, String descrUri) {
        this.parser = parser;
        this.dwnlAgent = dwnlAgent;
        this.descrUri = descrUri;
    }
    
    public void run() {
        // parse DLOTA descriptor
        
        try {
            parseDescriptor();
        } catch (SAXException e) {
            setStatus(DWNLD_DESCR_ERROR);
            return;
        } catch (Exception e) {
            setStatus(UNDEFINED_ERROR);
            return;
        }
        
        Set allowed = new HashSet();
        allowed.add("application/java-archive");
        allowed.add("application/vnd.osgi.dp");
        if (!allowed.contains(handler.getType())) {
            setStatus(NOT_ACCEPTABLE_CONTENT);
            return;
        }

        // ask user
        String result = UserPrompt.prompt("Download info:\n" +
                " Name: " + (null == handler.getName() ? "" : handler.getName()) + "\n" +
                " Vendor: " + (null == handler.getVendor() ? "" : handler.getVendor()) + "\n" +
                " Size: " + (null == handler.getSize() ? "" : handler.getSize()) + "\n" +
                " Type: " + (null == handler.getType() ? "" : handler.getType()) + "\n" +
                " Description: " + (null == handler.getDescription() ? "" : handler.getDescription()),
                "Do you allow to download it", "yes");
        boolean ok = result.trim().equalsIgnoreCase("yes");
        if (!ok) {
            setStatus(USER_CANCELLED);
            return;
        }

        // create the InputStream
        
        InputStream is = null;
        Hashtable props = new Hashtable();
        props.put("url", handler.getObjectURI());
        try {
            is = dwnlAgent.download("url", props);
        }
        catch (Exception e) {
            setStatus(DWNL_SERVER_NOT_AVAILABLE);
            return;
        }
        
        setStatus(OK, is);
    }
    
    private void parseDescriptor() throws Exception {
        InputStream is = null;
        try {
            Hashtable props = new Hashtable();
            props.put("url", descrUri);
            is = dwnlAgent.download("url", props);
            parser.parse(is, handler);
        } finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
    
    private synchronized void setStatus(int status) {
        if (OK == status)
            throw new IllegalArgumentException();
        this.status = status;
    }
    
    private synchronized void setStatus(int status, InputStream is) {
        if (OK != status)
            throw new IllegalArgumentException();
        this.status = status;
        this.inputStream = is;
    }

    public synchronized int getStatus() {
        return status;
    }

    public synchronized InputStream getInputStream() {
        return inputStream;
    }
    
    public String getMimeType() {
        return handler.getType();
    }

}