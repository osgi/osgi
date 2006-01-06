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
package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.osgi.impl.service.dwnl.DownloadAgent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DownloadThread extends Thread {
    
    public static final int RESULT_OK =  0;
    
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
        } catch (MalformedURLException e) {
        	setStatus(PluginConstants.RESULT_MALFORMED_URL);
        	return;
        } catch (SAXException e) {
            setStatus(PluginConstants.RESULT_DWNLD_DESCR_ERROR);
            return;
        } catch (Exception e) {
            setStatus(PluginConstants.RESULT_UNDEFINED_ERROR);
            return;
        }
        
        Set allowed = new HashSet();
        allowed.add("application/java-archive");
        allowed.add("application/vnd.osgi.dp");
        if (!allowed.contains(handler.getType())) {
            setStatus(PluginConstants.RESULT_NOT_ACCEPTABLE_CONTENT);
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
            setStatus(PluginConstants.RESULT_CANCELLED);
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
            setStatus(PluginConstants.RESULT_DWNL_SERVER_NOT_AVAILABLE);
            return;
        }
        
        setStatus(RESULT_OK, is);
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
        if (RESULT_OK == status)
            throw new IllegalArgumentException();
        this.status = status;
    }
    
    private synchronized void setStatus(int status, InputStream is) {
        if (RESULT_OK != status)
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