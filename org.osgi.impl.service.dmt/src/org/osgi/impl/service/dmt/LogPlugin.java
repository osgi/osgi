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
package org.osgi.impl.service.dmt;

import java.util.*;
import java.io.*;
import java.net.URL;

import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.log.*;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.impl.service.dmt.wbxmlenc.*;
import java.text.*;

// TODO implement LogResult tree, make nodes automatic

public class LogPlugin implements DmtDataPlugin, DmtExecPlugin {
	private static final String FILTER    = "Filter";
	private static final String EXCLUDE   = "Exclude";
	private static final String MAXR      = "MaxRecords";

    private static final String SEVERITY  = "Severity";
	private static final String TIME      = "Time";
	private static final String SYSTEM    = "System";
	private static final String SUBSYSTEM = "SubSystem";
	private static final String MESSAGE   = "Message";
	private static final String DATA      = "Data";
    
    private static final String DEFAULT_FILTER  = "";
    private static final String DEFAULT_EXCLUDE = "";
    private static final int    DEFAULT_MAXR    = 0;
    
    private static final List VALID_EXCLUDE_COMPONENTS = Arrays.asList(
            new String[] { SEVERITY, TIME, SYSTEM, SUBSYSTEM, MESSAGE, DATA });
    
	private BundleContext  bc;
	private ServiceTracker logReaderTracker;
	private ServiceTracker adminTracker;
	private Hashtable      requests;
	
    private WbxmlCodePages wbxmlCodePages;

    // the state of the request table is stored here in atomic sessions 
    private Hashtable           savedRequests;
    
	LogPlugin(BundleContext bc, ServiceTracker logReaderTracker, 
              ServiceTracker adminTracker) throws BundleException {
		this.bc = bc;
		this.logReaderTracker = logReaderTracker;
		this.adminTracker = adminTracker;
        
		requests = new Hashtable();
		
		String resourceName = getClass().getName();
		int index = resourceName.lastIndexOf( '.' );
		resourceName = resourceName.substring( 0, index );
		resourceName = resourceName.replace( '.', '/' ) + "/wbxmlenc/TND-WBXML.tokens";
		
		URL resourceURL = bc.getBundle().getResource(resourceName);
		if (resourceURL == null)
			throw new BundleException("Can't find " + resourceName + " in the resources!");

		try {
			wbxmlCodePages = new WbxmlCodePages( resourceURL.openStream() );
		}catch( Exception e ) {
			throw new BundleException( e.getMessage(), e );
		}
	}

	//----- DmtDataPlugin methods -----//
	public void open(int lockMode, DmtSession session) throws DmtException {
		// session info not needed, it will come in the exec()
        
        if(lockMode == DmtSession.LOCK_TYPE_ATOMIC)
            savedRequests = copyRequests(requests);
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		open(lockMode, session);
	}
    
    public void nodeChanged(String nodeUri) {
        // do nothing - the version and timestamp properties are not supported
    }

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length == 0) // OSGi/Log
            return new LogPluginMetanode(DmtMetaNode.PERMANENT, 
                                         !LogPluginMetanode.MODIFIABLE, 
                                         !LogPluginMetanode.ALLOW_INFINITE, 
                                         "Root node for log search requests.");
        
        if (path.length == 1) // OSGi/Log/<search_id>
            return new LogPluginMetanode(DmtMetaNode.DYNAMIC,
                                         LogPluginMetanode.MODIFIABLE,
                                         LogPluginMetanode.ALLOW_INFINITE,
                                         "Root node of a log search request.");
        
        if (path.length == 2) { // OSGi/Log/<search_id>/<param>
            if(path[1].equals(FILTER))
                return new LogPluginMetanode(LogPluginMetanode.SEARCH_PARAMETER,
                        DmtData.FORMAT_STRING, new DmtData(DEFAULT_FILTER), null,  
                        "Filter expression to select log records included in the result.");
                
            if(path[1].equals(EXCLUDE))
                return new LogPluginMetanode(LogPluginMetanode.SEARCH_PARAMETER,
                        DmtData.FORMAT_STRING, new DmtData(DEFAULT_EXCLUDE), VALID_EXCLUDE_COMPONENTS,
                        "A list of log entry attributes not to include in the result records.");
            
            if(path[1].equals(MAXR))
                return new LogPluginMetanode(LogPluginMetanode.SEARCH_PARAMETER,
                        DmtData.FORMAT_INTEGER, new DmtData(DEFAULT_MAXR), null,
                        "The maximum number of records to be included in the result.");
            
            // TODO LogResult node
            
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                    "No such node defined in the logging tree");
        }
            
        // TODO LogResult subtree
        
        // path.length > 2
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                               "No such node defined in the logging tree");
    }

	public boolean supportsAtomic() {
		return true;
	}

	//----- Dmt methods -----//
    
    public void commit() throws DmtException {
        // only called if session lock type is atomic
        savedRequests = copyRequests(requests);
    }    
    
	public void rollback() throws DmtException {
        // only called if session lock type is atomic
	    requests = copyRequests(savedRequests);
    }

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length >= 2 because there are no leaf nodes above this
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request exists");
        }
        String leaf = path[1];
        if (leaf.equals(EXCLUDE))
            lr.exclude = data.getString();
        else if (leaf.equals(FILTER))
            lr.filter = data.getString();
        else if (leaf.equals(MAXR))
            lr.maxrecords = data.getInt();
        else {
            // should never happen because of meta-data
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                "Node cannot be modified");
        }
	}

    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length >= 2 because there are no leaf nodes above this
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request exists");
        }
        String leaf = path[1];
        if (leaf.equals(FILTER))
            lr.filter = DEFAULT_FILTER;
        else if (leaf.equals(EXCLUDE))
            lr.exclude = DEFAULT_EXCLUDE;
        else if (leaf.equals(MAXR))
            lr.maxrecords = DEFAULT_MAXR;
        
        // should never happen because of meta-data
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
            "Node cannot be modified");
    }
    
	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				               "Cannot set type property of log nodes.");
	}

	public void deleteNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length == 1 because only search request root is deletable
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request, cannot be deleted");
        
        requests.remove(id);
    }

	public void createInteriorNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);

        // path.length == 1 because interior nodes cannot be added elsewhere
        String id = path[0];
        if (requests.containsKey(id)) {
            throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
                                   "Request ID already exists");
        }
        LogRequest lr = new LogRequest();
        lr.uri = nodeUri;
        requests.put(id, lr);
    }

	public void createInteriorNode(String nodeUri, String type)
            throws DmtException {
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                "Cannot set type property of search request nodes.");
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        // should never be reached because of meta-data, every leaf is automatic
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                               "All leaf nodes are created automatically.");
    }
    
	public void createLeafNode(String nodeUri, DmtData value)
            throws DmtException {
        // should never be reached because of meta-data, every leaf is automatic
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                               "All leaf nodes are created automatically.");
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        // should never be reached because of meta-data, every leaf is automatic
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                               "All leaf nodes are created automatically.");
    }
    
	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO allow cloning
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
                               "Cannot copy log request nodes.");
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				               "Cannot rename log request nodes.");
	}

	//----- DmtReadOnly methods -----//
	public void close() throws DmtException {
		// nothing to do
	}

	public boolean isNodeUri(String nodeUri) {
        String[] path = prepareUri(nodeUri);
        
        if (path.length == 0) // ./OSGi/Log
            return true;
        
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if(lr == null)
            return false;
        
        if (path.length == 1)
            return true;
        
        if (path.length == 2)
            // TODO LogResult node
            return 
                path[1].equals(FILTER) || 
                path[1].equals(EXCLUDE) || 
                path[1].equals(MAXR);

        // TODO LogResult subtree
        
        return false;
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        if(path.length == 0) // ./OSGi/Log
            return false;
        
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if(path.length == 1) // ./OSGi/Log/<search_id>
            return false;
        
        if(path.length == 2)
            return true; // TODO LogResult node
        
        // TODO LogResult subtree
        
        // should never be reached
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }
    
	public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length > 1 because there are no leaf node above this
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        String leaf = path[1];
        if (leaf.equals(FILTER))
            return new DmtData(lr.filter);
        if (leaf.equals(EXCLUDE))
            return new DmtData(lr.exclude);
        if (leaf.equals(MAXR))
            return new DmtData(lr.maxrecords);

        // TODO LogResult subtree
        
        // should never be reached
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }

	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        if(path.length == 0)
            // TODO return URL to log tree DDF
            return null;
        
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");

        if(path.length == 1)
            return null;
        
        // path.length == 2
        // TODO LogResult node
        return LogPluginMetanode.LEAF_MIME_TYPE;
        
        // TODO LogResult subtree
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
        return getNodeValue(nodeUri).getSize();
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length == 0) {
            String[] requestArray = (String[]) 
                    requests.keySet().toArray(new String[requests.size()]);
            // escape '/' and '\' characters in request IDs before returning 
            // TODO is this needed; if it is, won't it be done by DmtAdmin?
            for(int i = 0; i < requestArray.length; i++)
                requestArray[i] = escape(requestArray[i]);

            return requestArray;
        }

        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if (path.length == 1)
            // TODO LogResult node
            return new String[] { FILTER, EXCLUDE, MAXR };

        // TODO LogResult subtree
        
        // should never be reached
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                               "The specified URI points to a leaf node.");
    }

	//  ----- DmtExecPlugin methods -----//
	public void execute(DmtSession session, String nodeUri, String correlator, 
            String data) throws DmtException {
		String[] path = prepareUri(nodeUri);
		if (path.length != 1) {
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot execute node on this level");
		}
		LogRequest lr = (LogRequest) requests.get(path[0]);
		if (lr == null)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					               "No such log request");

		Vector records = getResult(lr);
		String result = formatResult(records);
		DmtAlertItem[] items = new DmtAlertItem[1];
		items[0] = new DmtAlertItem(nodeUri, null, null, new DmtData(result));
        
        getDmtAdmin().sendAlert(session.getPrincipal(), 1224, correlator, items);
	}

	//----- Private utility methods -----//
	private static String[] prepareUri(String nodeUri) {
		String relativeUri = Utils.relativeUri(LogPluginActivator.PLUGIN_ROOT,
				                               nodeUri);
		// relativeUri will not be null because the DmtAdmin only gives us nodes
		// in our subtree
		String[] path = Utils.splitUri(relativeUri);
		if (path.length == 1 && path[0].equals(""))
			return new String[] {};
		return path;
	}
    
    private static String escape(String nodeName) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < nodeName.length(); i++) {
            char ch = nodeName.charAt(i);
            if(ch == '/' || ch == '\\')
                sb.append('\\');
            sb.append(ch);
        }
        return sb.toString();
    }

    private DmtAdmin getDmtAdmin() {
        DmtAdmin admin = (DmtAdmin) adminTracker.getService();
        if(admin == null)
            throw new MissingResourceException("Dmt Admin service not found.",
                    DmtAdmin.class.getName(), null);
        return admin;
    }
    
    private Hashtable copyRequests(Hashtable original) {
        Hashtable copy = new Hashtable();
        Iterator i = original.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            copy.put(entry.getKey(), ((LogRequest) entry.getValue()).clone());
        }
        return copy;
    }
    
	private String createRTProperties( String format ) {
		String type = "text/plain";
		
		if( format.equals( "node" ) )
			type = "";
		
		return "<RTProperties><Format><" + format + "/></Format><Type>" + type +
		       "</Type></RTProperties>";
	}
	
	private String createNode( String name, String format, String value, String embed ) {
		String encoded = "<Node><NodeName>" + name + "</NodeName>";
		if( format != null )
			encoded += createRTProperties( format );
		if( embed != null )
			encoded += embed;
		if( value != null )
			encoded += "<Value>" + value + "</Value>";
		encoded += "</Node>";
		return encoded;
	}
	
	/*
	 * Formats the vector of logentries into a string to be sent within an alert
	 */
	private String formatResult(Vector records) {
		String result = "";

		for (int i = 0; i < records.size(); i++) {
			LogEntry e = (LogEntry) records.elementAt(i);
			if (e == null)
				continue; //should not happen
			
			String logEncode = "";

            // TODO this might use the default locale to determine the time, which is not good 
			SimpleDateFormat sdf = new SimpleDateFormat( "yyMMdd'T'HHmmss'Z'" );
			
			logEncode += createNode( "time", "chr", sdf.format( new Date( e.getTime() ) ) , null );
			
			logEncode += createNode( "severity", "int", Integer.toString( e.getLevel() ), null );

			String symName = e.getBundle().getSymbolicName();
			if( symName == null )
				symName = Long.toString( e.getBundle().getBundleId() );
			logEncode += createNode( "system", "chr", symName, null );

			ServiceReference servRef = e.getServiceReference();
			String refName = "";
			if( servRef != null ) {
				String []objects = (String [])servRef.getProperty( Constants.OBJECTCLASS );
				if( objects != null ) {
					for( int j = 0; j != objects.length; j++ )
						refName += ( ( j == 0 ) ? "" : "," ) + objects[ j ];
				}					
			}			
			logEncode += createNode( "subsystem", "chr", refName, null );
			
			logEncode += createNode( "message", "chr", e.getMessage(), null );
			
			Throwable t = e.getException();
			String data = "";
			if( t != null ) {
				StringWriter writer = new StringWriter();
				t.printStackTrace( new PrintWriter( writer ) );
				data = writer.toString();
			}			
			logEncode += createNode( "data", "chr", data , null );
			
			logEncode += createNode( "id", "chr", Integer.toString( i ), null );
			
			result += createNode( Integer.toString( i ), "node", null, logEncode );			
		}

		result = createNode( "logresult", "node", null, result );
		result = "<MgmtTree>" + result + "</MgmtTree>";
		return result;
	}

    //TODO use the exclude filter also within the loop so that it need not be done separately
	/*
	 * Calculates the result of a log request. Uses the filter and maxrecords.
	 * Returns a vector of logentries 
	 */
	private Vector getResult(LogRequest lr) throws DmtException {
		Vector ret = new Vector();
        
		Filter filter = null;
		try {
			if (!lr.filter.equals(""))
                filter = bc.createFilter(lr.filter);
		} catch (InvalidSyntaxException e) {
			throw new DmtException(lr.uri, DmtException.OTHER_ERROR,
					"Cannot parse filter", e);
		}
        
        LogReaderService logReader = 
            (LogReaderService) logReaderTracker.getService();
        if(logReader == null)
            throw new MissingResourceException("Log Reader service not found.",
                    LogReaderService.class.getName(), null);
        
		Enumeration e = logReader.getLog();
		int max = Integer.MAX_VALUE;
		if (lr.maxrecords != 0)
			max = lr.maxrecords;
		while (e.hasMoreElements() && ret.size() <= max) {
			LogEntry le = (LogEntry) e.nextElement();
			if (filter != null) {
				//create a dictionary for matching with the filter
				Hashtable dict = new Hashtable();
				dict.put(SEVERITY, new Integer(le.getLevel()));
				if (le.getBundle() != null)
					dict.put(SYSTEM, le.getBundle().toString());
				if (le.getServiceReference() != null)
					dict.put(SUBSYSTEM, le.getServiceReference().toString());
				dict.put(MESSAGE, le.getMessage());
				if (le.getException() != null)
					dict.put(DATA, le.getException().toString());
				dict.put(TIME, timeToString(le.getTime()));
				if (!filter.match(dict)) {
					continue;
				}
			}
			ret.add(le);
		}
		return ret;
	}

    private String timeToString(long time) {
        SimpleTimeZone utc = new SimpleTimeZone(0, "GMT+00");
        Calendar utcCal = Calendar.getInstance(utc);
        utcCal.setTime(new Date(time));
        
        StringBuffer sb = new StringBuffer();
        pad(sb, utcCal.get(Calendar.YEAR), 4);
        pad(sb, utcCal.get(Calendar.MONTH)+1, 2);
        pad(sb, utcCal.get(Calendar.DAY_OF_MONTH), 2);
        sb.append("T");
        pad(sb, utcCal.get(Calendar.HOUR_OF_DAY), 2);
        pad(sb, utcCal.get(Calendar.MINUTE), 2);
        pad(sb, utcCal.get(Calendar.SECOND), 2);
        sb.append("Z");

        return sb.toString(); 
    }
    
    private void pad(StringBuffer sb, int num, int width) {
        String numStr = String.valueOf(num);
        width -= numStr.length();
        while(width > 0)
            sb.append('0');
        sb.append(numStr);
    }
    
	class LogRequest implements Cloneable {
		String uri        = null;
		String filter     = DEFAULT_FILTER;
		String exclude    = DEFAULT_EXCLUDE;
		int    maxrecords = DEFAULT_MAXR;
        
        public Object clone() {
            LogRequest lr = new LogRequest();
            
            lr.uri        = uri;
            lr.filter     = filter;
            lr.exclude    = exclude;
            lr.maxrecords = maxrecords;
            
            return lr;
        }
	}
}
