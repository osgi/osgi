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
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.log.*;
import java.text.*;

public class LogPlugin implements DmtDataPlugin, DmtExecPlugin {
	private BundleContext		bc;
	private LogService			logservice;
	private LogReaderService	logreaderservice;
	private DmtAdmin		    alertsender;
	private Hashtable			requests;
	private final static String	FILTER	= "filter";
	private final static String	EXCLUDE	= "exclude";
	private final static String	MAXR	= "maxrecords";
	private final static String	MAXS	= "maxsize";
	private boolean				debug	= false;

	LogPlugin(BundleContext bc, LogService ls, LogReaderService lrs,
			DmtAdmin da) {
		this.bc = bc;
		this.logservice = ls;
		this.logreaderservice = lrs;
		this.alertsender = da;
		requests = new Hashtable();
	}

	//----- DmtDataPlugin methods -----//
	public void open(int lockMode, DmtSession session) throws DmtException {
		// TODO lockmode
		// session info not needed, it will come in the exec()
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		open(lockMode, session);
	}

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        // TODO, it is just a quick hack
        String[] path = prepareUri(nodeUri);
        if (path.length == 0) { // OSGi/log
            return new LogPluginMetanode(!LogPluginMetanode.CANDELETE,
                    !LogPluginMetanode.CANADD, LogPluginMetanode.CANGET,
                    !LogPluginMetanode.CANREPLACE,
                    !LogPluginMetanode.CANEXECUTE, !LogPluginMetanode.ISLEAF);
        }
        else if (path.length == 1) { // OSGi/log/<search_id>
            return new LogPluginMetanode(LogPluginMetanode.CANDELETE,
                    LogPluginMetanode.CANADD, LogPluginMetanode.CANGET,
                    LogPluginMetanode.CANREPLACE, LogPluginMetanode.CANEXECUTE,
                    !LogPluginMetanode.ISLEAF);
        }
        else if (path.length == 2) { // OSGi/log/<search_id>/filter
            return new LogPluginMetanode(LogPluginMetanode.CANDELETE,
                    LogPluginMetanode.CANADD, LogPluginMetanode.CANGET,
                    LogPluginMetanode.CANREPLACE,
                    !LogPluginMetanode.CANEXECUTE, LogPluginMetanode.ISLEAF);
        }
        else {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
                    "Can not get metadata");
        }
    }

	public boolean supportsAtomic() {
		return false;
	}

	//----- Dmt methods -----//
    
    public void commit() throws DmtException {
    }    
    
	public void rollback() throws DmtException {
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		// TODO
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
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such log request, cannot be deleted");
        }
        if (path.length == 1) {
            requests.remove(id);
            return;
        }
        if (path.length == 2) {
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
            return;
        }
        //never reached
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                "Cannot be deleted");
    }

	public void createInteriorNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length != 1) {
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                    "Cannot create node on this level");
        }
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
        if (!type.equals("chr"))
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                    "Only chr type allowed for log request IDs.");
        createInteriorNode(nodeUri);
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                "The specified node has no default value.");
    }
    
	public void createLeafNode(String nodeUri, DmtData value)
            throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length != 2) {
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                    "Leaf nodes not allowed on this level");
        }
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
        else
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                    "No such node allowed");
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        // TODO check mime type?
        createLeafNode(nodeUri, value);
    }
    
	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO allow cloning
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
        if (!requests.containsKey(path[0]))
            return false;
        if (path.length == 1) {
            return true;
        }
        else if (path.length == 2) {
            LogRequest lr = (LogRequest) requests.get(path[0]);
            String leaf = path[1];
            if (leaf.equals(EXCLUDE) && lr.exclude != null)
                return true;
            if (leaf.equals(FILTER) && lr.filter != null)
                return true;
            if (leaf.equals(MAXS) && lr.maxsize != 0)
                return true;
            if (leaf.equals(MAXR) && lr.maxrecords != 0)
                return true;
            return false;
        }
        else
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
        String id = path[0];
        LogRequest lr = (LogRequest) requests.get(id);
        if (lr == null) {
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such log request");
        }
        String leaf = path[1];
        DmtData ret;
        if (leaf.equals(EXCLUDE)) {
            if (lr.exclude != null) {
                ret = new DmtData(lr.exclude);
            }
            else
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "No such node");
        }
        else if (leaf.equals(FILTER)) {
            if (lr.filter != null) {
                ret = new DmtData(lr.filter);
            }
            else
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "No such node");
        }
        else if (leaf.equals(MAXR)) {
            if (lr.maxrecords != 0) {
                ret = new DmtData(lr.maxrecords);
            }
            else
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "No such node");
        }
        else if (leaf.equals(MAXS)) {
            if (lr.maxsize != 0) {
                ret = new DmtData(lr.maxsize);
            }
            else
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "No such node");
        }
        else
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such node");
        return ret;
    }

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO!
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO!
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        Vector ch = new Vector();
        if (path.length == 0) {
            Enumeration e = requests.keys();
            while (e.hasMoreElements())
                ch.add(e.nextElement());
            return toStringArray(ch);
        }
        if (path.length == 1) {
            LogRequest lr = (LogRequest) requests.get(path[0]);
            if (lr == null) {
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "No such log request");
            }
            if (lr.exclude != null)
                ch.add(EXCLUDE);
            if (lr.filter != null)
                ch.add(FILTER);
            if (lr.maxsize != 0)
                ch.add(MAXS);
            if (lr.maxrecords != 0)
                ch.add(MAXR);
            return toStringArray(ch);
        }
        // should not happen because of !isLeafNode...
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                "The specified URI points to a leaf node.");
    }

	//  ----- DmtDataPlugin methods -----//
	public void execute(DmtSession session, String nodeUri, String data)
			throws DmtException {
		String[] path = prepareUri(nodeUri);
		if (path.length != 1) {
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot execute node on this level");
		}
		LogRequest lr = (LogRequest) requests.get(path[0]);
		if (lr == null) {
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"No such log request");
		}
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
	private String formatResultNew(Vector records) {
		String result = "";

		for (int i = 0; i < records.size(); i++) {
			LogEntry e = (LogEntry) records.elementAt(i);
			if (e == null)
				continue; //should not happen
			
			String logEncode = "";

			SimpleDateFormat sdf = new SimpleDateFormat( "yyMMdd'T'HHmmss'Z'" );
			
			logEncode += createNode( "time", "chr", sdf.format( new Date( e.getTime() ) ) , null );
			
			logEncode += createNode( "severity", "int", Integer.toString( e.getLevel() ), null );

			logEncode += createNode( "system", "chr", e.getBundle().toString(), null );

			logEncode += createNode( "subsystem", "chr", e.getServiceReference().toString(), null );
			
			logEncode += createNode( "message", "chr", e.getMessage(), null );
			
			Throwable t = e.getException();
			String data = t == null ? "" : t.toString();			
			logEncode += createNode( "data", "chr", data , null );
			
			logEncode += createNode( "id", "chr", Integer.toString( i ), null );
			
			result += createNode( Integer.toString( i ), "node", null, logEncode );			
		}

		result = createNode( "logresult", "node", null, result );
		result = "<MgmtTree>" + result + "</MgmtTree>";
		return result;
	}

	/*
	 * Formats the vector of logentries into a string to be sent within an alert
	 */
	private String formatResult(Vector records) {
		String ret = "Records:\r\n";
		String s = ","; //separator
		//TODO proper formatting per spec, this is just like toString()
		//TODO check maxsize and exclude within loop
		for (int i = 0; i < records.size(); i++) {
			LogEntry e = (LogEntry) records.elementAt(i);
			if (e == null)
				continue; //should not happen
			ret = ret + "id:" + i + s + "time:" + e.getTime() + s + "severity:"
					+ e.getLevel() + s + "system:" + e.getBundle() + s
					+ "subsystem:" + e.getServiceReference() + s + "message:"
					+ e.getMessage() + s + "data:" + e.getException() + "\r\n";
		}
		d("Formatted: " + ret);		
		return ret;
	}

	/*
	 * calculates the result of a log request. Uses the filter and maxrecords.
	 * Returns a vector of logentries TODO use the maxsize and exclude filters
	 * also within the loop so that it need not be done separately
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

	private String[] toStringArray(Vector v) {
		String[] ret = new String[v.size()];
		for (int i = 0; i < v.size(); i++)
			ret[i] = (String) v.elementAt(i);
		return ret;
	}

	private void d(String s) {
		if (debug)
			System.out.println("debug> " + s);
	}

	class LogRequest {
		String	uri			= null;
		String	filter		= null;
		String	exclude		= null;
		int		maxrecords	= 0;
		int		maxsize		= 0;
	}
}
