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
import org.osgi.impl.service.dmt.wbxmlenc.*;
import java.text.*;

public class LogPlugin implements DmtDataPlugin, DmtExecPlugin {
	static final String	FILTER	= "filter";
	static final String	EXCLUDE	= "exclude";
	static final String	MAXR	= "maxrecords";
	static final String	MAXS	= "maxsize";
    
    // TODO int size should be defined elsewhere (at least in the getSize() javadoc)
    private static final int INT_SIZE = 4;
    
	private BundleContext		bc;
	private LogService			logservice;
	private LogReaderService	logreaderservice;
	private DmtAdmin		    alertsender;
	private Hashtable			requests;
	private boolean				debug	= false;
	private WbxmlCodePages wbxmlCodePages;

    // the state of the request table is stored here in atomic sessions 
    private Hashtable           savedRequests;
    
	LogPlugin(BundleContext bc, LogService ls, LogReaderService lrs,
			DmtAdmin da) throws BundleException {
		this.bc = bc;
		this.logservice = ls;
		this.logreaderservice = lrs;
		this.alertsender = da;
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

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length == 0) // OSGi/log
            return new LogPluginMetanode(LogPluginMetanode.IS_PERMANENT, 
                                         !LogPluginMetanode.CAN_EXECUTE, 
                                         !LogPluginMetanode.ALLOW_INFINITE, 
                                         "Root node for log search requests.");
        
        if (path.length == 1) // OSGi/log/<search_id>
            return new LogPluginMetanode(!LogPluginMetanode.IS_PERMANENT,
                                         LogPluginMetanode.CAN_EXECUTE,
                                         LogPluginMetanode.ALLOW_INFINITE,
                                         "Root node of a log search request.");
        
        if (path.length == 2) { // OSGi/log/<search_id>/<param>
            if(path[1].equals(FILTER))
                return new LogPluginMetanode(DmtData.FORMAT_STRING, 
                        "Filter expression to select log records included in the result.");
                
            if(path[1].equals(EXCLUDE))
                return new LogPluginMetanode(DmtData.FORMAT_STRING, 
                        "A list of log entry attributes not to include in the result records.");
            
            if(path[1].equals(MAXR))
                return new LogPluginMetanode(DmtData.FORMAT_INTEGER,
                        "The maximum number of records to be included in the result.");
            
            if(path[1].equals(MAXS))
                return new LogPluginMetanode(DmtData.FORMAT_INTEGER,
                        "The maximum size of the result.");
            
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                    "No such node defined in the logging tree");
        }
            
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
		createLeafNode(nodeUri, data);
	}

    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                               "The specified node has no default value.");
    }
    
	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				               "Cannot set type property of log nodes.");
	}

	public void deleteNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length > 0 because log tree root is not deletable
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request, cannot be deleted");
        
        if (path.length == 1) {
            requests.remove(id);
            return;
        }
        
        // path.length == 2 
        String leaf = path[1];
        if (leaf.equals(EXCLUDE))
            lr.exclude = null;
        else if (leaf.equals(FILTER))
            lr.filter = null;
        else if (leaf.equals(MAXR))
            lr.maxrecords = 0;
        else if (leaf.equals(MAXS))
            lr.maxsize = 0;
        else
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such node, cannot be deleted");
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
        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                               "The specified node has no default value.");
    }
    
	public void createLeafNode(String nodeUri, DmtData value)
            throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        // path.length == 2 because there are no leaf nodes on other levels
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request exists");
        }
        String leaf = path[1];
        if (leaf.equals(EXCLUDE))
            lr.exclude = value.getString();
        else if (leaf.equals(FILTER))
            lr.filter = value.getString();
        else if (leaf.equals(MAXR))
            lr.maxrecords = value.getInt();
        else if (leaf.equals(MAXS))
            lr.maxsize = value.getInt();
        else // should never happen if admin checks "validNames"
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                                   "No such node allowed");
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        // mimeType must be "text/plain" as given in meta-data, ensured by admin 
        createLeafNode(nodeUri, value);
    }
    
	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO allow cloning
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
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
        
        if (path.length == 0) // ./OSGi/log
            return true;
        
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if(lr == null)
            return false;
        
        if (path.length == 1)
            return true;
        
        if (path.length == 2) {
            String leaf = path[1];
            if (leaf.equals(EXCLUDE))
                return lr.exclude != null;
            if (leaf.equals(FILTER))
                return lr.filter != null;
            if (leaf.equals(MAXS))
                return lr.maxsize != 0;
            if (leaf.equals(MAXR))
                return lr.maxrecords != 0;
            
            return false;
        }

        return false;
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        
        if(path.length == 0) // ./OSGi/log
            return false;
        
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        }
        
        if(path.length == 1) // ./OSGi/log/<search_id>
            return false;
        
        String leaf = path[1];
        if(leaf.equals(EXCLUDE)) {
            if(lr.exclude != null)
                return true;
        } else if(leaf.equals(FILTER)) {
            if(lr.filter != null)
                return true;
        } else if(leaf.equals(MAXR)) {
            if(lr.maxrecords != 0)
                return true;
        } else if(leaf.equals(MAXS)) {
            if(lr.maxsize != 0)
                return true;
        }
        
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }
    
	public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        String leaf = path[1];
        if (leaf.equals(EXCLUDE)) {
            if(lr.exclude != null)
                return new DmtData(lr.exclude);
        } else if (leaf.equals(FILTER)) {
            if(lr.filter != null)
                return new DmtData(lr.filter);
        } else if (leaf.equals(MAXR)) {
            if(lr.maxrecords != 0)
                return new DmtData(lr.maxrecords);
        } else if (leaf.equals(MAXS)) {
            if(lr.maxsize != 0)
                return new DmtData(lr.maxsize);
        }

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
        return LogPluginMetanode.LEAF_MIME_TYPE;
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
        String[] path = prepareUri(nodeUri);
        
        // path.length == 2 because this method is only valid for leaf nodes
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such log request");
        
        String leaf = path[1];
        if (leaf.equals(EXCLUDE) && lr.exclude != null)
            return lr.exclude.length();
        else if (leaf.equals(FILTER) && lr.filter != null)
            return lr.filter.length();
        else if (leaf.equals(MAXR) && lr.maxrecords != 0)
            return INT_SIZE;
        else if (leaf.equals(MAXS) && lr.maxsize != 0)
            return INT_SIZE;

        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                              "No such node");
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length == 0)
            return (String[]) 
                requests.keySet().toArray(new String[requests.size()]);

        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if (path.length == 1) {
            Vector ch = new Vector();
            if (lr.exclude != null)
                ch.add(EXCLUDE);
            if (lr.filter != null)
                ch.add(FILTER);
            if (lr.maxsize != 0)
                ch.add(MAXS);
            if (lr.maxrecords != 0)
                ch.add(MAXR);
            return (String[]) ch.toArray(new String[ch.size()]);
        }

        // should not happen because of !isLeafNode...
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                               "The specified URI points to a leaf node.");
    }

	//  ----- DmtExecPlugin methods -----//
	public void execute(DmtSession session, String nodeUri, String data)
			throws DmtException {
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
		items[0] = new DmtAlertItem(nodeUri, null, null, result);
		alertsender.sendAlert(session.getPrincipal(), 1224, items);
	}

	//----- Private utility methods -----//
	private static String[] prepareUri(String nodeUri) {
		String relativeUri = Utils.relativeUri(LogPluginActivator.PLUGIN_ROOT,
				                               nodeUri);
		// relativeUri will not be null because the DmtAdmin only gives us nodes
		// in our subtree
		String[] path = Splitter.split(relativeUri, '/', -1);
		if (path.length == 1 && path[0].equals(""))
			return new String[] {};
		return path;
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

    //TODO use the maxsize and exclude filters also within the loop so that it need not be done separately
	/*
	 * Calculates the result of a log request. Uses the filter and maxrecords.
	 * Returns a vector of logentries 
	 */
	private Vector getResult(LogRequest lr) throws DmtException {
		Vector ret = new Vector();
		Filter filter = null;
		try {
			if (lr.filter != null) {
				filter = bc.createFilter(lr.filter);
			}
		}
		catch (InvalidSyntaxException e) {
			throw new DmtException(lr.uri, DmtException.OTHER_ERROR,
					"Cannot parse filter", e);
		}
		Enumeration e = logreaderservice.getLog();
		int max = Integer.MAX_VALUE;
		if (lr.maxrecords != 0)
			max = lr.maxrecords;
		while (e.hasMoreElements() && ret.size() <= max) {
			LogEntry le = (LogEntry) e.nextElement();
			if (filter != null) {
				//create a dictionary for matching with the filter
				Hashtable dict = new Hashtable();
				dict.put("severity", new Integer(le.getLevel()));
				if (le.getBundle() != null)
					dict.put("system", le.getBundle().toString());
				if (le.getServiceReference() != null)
					dict.put("subsystem", le.getServiceReference().toString());
				dict.put("message", le.getMessage());
				if (le.getException() != null)
					dict.put("data", le.getException().toString());
				//TODO convert timestamp to iso 8601
				dict.put("time", new Long(le.getTime()));
				if (!filter.match(dict)) {
					continue;
				}
			}
			ret.add(le);
		}
		return ret;
	}

	private void d(String s) {
		if (debug)
			System.out.println("debug> " + s);
	}

	class LogRequest implements Cloneable {
		String uri        = null;
		String filter     = null;
		String exclude    = null;
		int    maxrecords = 0;
		int    maxsize    = 0;
        
        public Object clone() {
            LogRequest lr = new LogRequest();
            
            lr.uri        = uri;
            lr.filter     = filter;
            lr.exclude    = exclude;
            lr.maxrecords = maxrecords;
            lr.maxsize    = maxsize;
            
            return lr;
        }
	}
}
