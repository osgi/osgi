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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.dmt.wbxmlenc.WbxmlCodePages;
import org.osgi.service.dmt.*;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.dmt.spi.*;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.DataPluginFactory;
import org.osgi.service.dmt.spi.TransactionalDataSession;

// TODO implement LogResult tree, make nodes automatic

public class LogPlugin implements DataPluginFactory, TransactionalDataSession, 
        ExecPlugin {
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

	//----- DataPluginFactory methods -----//
    
    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        return this;
    }

    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        return this;
    }
    
    public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        
        if(!Utils.isEqualPath(sessionRoot, LogPluginActivator.PLUGIN_ROOT_PATH))
            throw new DmtException(sessionRoot, DmtException.COMMAND_FAILED,
                    "Fine-grained locking not supported in atomic sessions, " +
                    "session root must not be below \"./OSGi/Log\".");

        savedRequests = copyRequests(requests);

        return this;
    }
    
	//----- TransactionalDataSession methods -----//
    
    public void commit() throws DmtException {
        // only called if session lock type is atomic
        savedRequests = copyRequests(requests);
    }    
    
	public void rollback() throws DmtException {
        // only called if session lock type is atomic
	    requests = copyRequests(savedRequests);
    }

    //----- ReadWriteDataSession methods -----//

    public void setNodeTitle(String[] fullPath, String title) 
            throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	public void setNodeValue(String[] fullPath, DmtData data) 
            throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length >= 2 because there are no leaf nodes above this
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request exists");
        }
        String leaf = path[1];
        if (leaf.equals(EXCLUDE))
            lr.exclude = data == null ? DEFAULT_EXCLUDE : data.getString();
        else if (leaf.equals(FILTER))
            lr.filter = data == null ? DEFAULT_FILTER : data.getString();
        else if (leaf.equals(MAXR))
            lr.maxrecords = data == null ? DEFAULT_MAXR : data.getInt();
        else {
            // should never happen because of meta-data
            throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                "Node cannot be modified");
        }
	}

	public void setNodeType(String[] fullPath, String type) 
            throws DmtException {
        // meta-data ensures that type is either null or the only possible value
        if(!isLeafNode(fullPath) && type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior nodes in Log tree.");
	}

	public void deleteNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length == 1 because only search request root is deletable
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request, cannot be deleted");
        
        requests.remove(id);
    }

	public void createInteriorNode(String[] fullPath, String type)
            throws DmtException {
        if(type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior nodes in Log tree.");
        
        String[] path = chopPath(fullPath);

        // path.length == 1 because interior nodes cannot be added elsewhere
        String id = path[0];
        if (requests.containsKey(id)) {
            throw new DmtException(fullPath, DmtException.NODE_ALREADY_EXISTS,
                                   "Request ID already exists");
        }
        LogRequest lr = new LogRequest();
        lr.path = fullPath;
        requests.put(id, lr);
    }

    public void createLeafNode(String[] fullPath, DmtData value, 
            String mimeType) throws DmtException {
        // should never be reached because of meta-data, every leaf is automatic
        throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                               "All leaf nodes are created automatically.");
    }
    
	public void copy(String[] fullPath, String[] newFullPath, boolean recursive)
			throws DmtException {
		// TODO allow cloning
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                               "Cannot copy log request nodes.");
	}

	public void renameNode(String[] fullPath, String newName) 
            throws DmtException {
		throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
				               "Cannot rename log request nodes.");
	}

	//----- ReadableDataSession methods -----//

    public void nodeChanged(String[] fullPath) {
        // do nothing - the version and timestamp properties are not supported
    }

	public void close() throws DmtException {
		// nothing to do
	}

    public MetaNode getMetaNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        if (path.length == 0) // OSGi/Log
            return new LogPluginMetanode(MetaNode.PERMANENT, 
                                         !LogPluginMetanode.MODIFIABLE, 
                                         !LogPluginMetanode.ALLOW_INFINITE, 
                                         "Root node for log search requests.");
        
        if (path.length == 1) // OSGi/Log/<search_id>
            return new LogPluginMetanode(MetaNode.DYNAMIC,
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
            
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No such node defined in the logging tree");
        }
            
        // TODO LogResult subtree
        
        // path.length > 2
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                               "No such node defined in the logging tree");
    }

    public boolean isNodeUri(String[] fullPath) {
        String[] path = chopPath(fullPath);
        
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

    public boolean isLeafNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        if(path.length == 0) // ./OSGi/Log
            return false;
        
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if(path.length == 1) // ./OSGi/Log/<search_id>
            return false;
        
        if(path.length == 2)
            return true; // TODO LogResult node
        
        // TODO LogResult subtree
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }
    
	public DmtData getNodeValue(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length > 1 because there are no leaf node above this
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
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
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }

	public String getNodeTitle(String[] fullPath) throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	public String getNodeType(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        if(path.length == 0)
            // TODO return URL to log tree DDF
            return null;
        
        LogRequest lr = (LogRequest) requests.get(path[0]);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request");

        if(path.length == 1)
            return null;
        
        // path.length == 2
        // TODO LogResult node
        return LogPluginMetanode.LEAF_MIME_TYPE;
        
        // TODO LogResult subtree
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
        return getNodeValue(fullPath).getSize();
	}

	public String[] getChildNodeNames(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
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
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if (path.length == 1)
            // TODO LogResult node
            return new String[] { FILTER, EXCLUDE, MAXR };

        // TODO LogResult subtree
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                               "The specified URI points to a leaf node.");
    }

	//  ----- DmtExecPlugin methods -----//
	public void execute(DmtSession session, String[] nodePath, 
            String correlator, String data) throws DmtException {
		String[] path = chopPath(nodePath);
		if (path.length != 1) {
            // should never be reached
			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
					"Cannot execute node on this level");
		}
		LogRequest lr = (LogRequest) requests.get(path[0]);
		if (lr == null)
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
					               "No such log request");

		Vector records = getResult(lr);
		String result = formatResult(records);
		AlertItem[] items = new AlertItem[1];
		items[0] = new AlertItem(Utils.tempAbsolutePathToUri(nodePath), null, 
                null, new DmtData(result));
        
        getDmtAdmin().sendAlert(session.getPrincipal(), 1224, correlator, items);
	}

	//----- Private utility methods -----//
	private static String[] chopPath(String[] absolutePath) {
		// result not null because DmtAdmin only gives us nodes in our subtree
		return Utils.relativePath(LogPluginActivator.PLUGIN_ROOT_PATH, 
                absolutePath);
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
			throw new DmtException(lr.path, DmtException.COMMAND_FAILED,
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
		String[] path     = null;
		String filter     = DEFAULT_FILTER;
		String exclude    = DEFAULT_EXCLUDE;
		int    maxrecords = DEFAULT_MAXR;
        
        public Object clone() {
            LogRequest lr = new LogRequest();
            
            lr.path       = path;
            lr.filter     = filter;
            lr.exclude    = exclude;
            lr.maxrecords = maxrecords;
            
            return lr;
        }
	}
}
