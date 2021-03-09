/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc2.tbc.Plugin.LogPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.SimpleTimeZone;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

class LogPlugin implements DataPlugin, TransactionalDataSession, LogListener {
	private static final String FILTER    = "Filter";
	private static final String EXCLUDE   = "Exclude";
	private static final String MAXR      = "MaxRecords";
	private static final String LOGRESULT = "LogResult";

    private static final String SEVERITY  = "Severity";
	private static final String TIME      = "Time";
	private static final String SYSTEM    = "System";
	private static final String SUBSYSTEM = "SubSystem";
	private static final String MESSAGE   = "Message";
	private static final String DATA      = "Data";
    
    private static final String DEFAULT_FILTER  = "";
    private static final String DEFAULT_EXCLUDE = "";
    private static final int    DEFAULT_MAXR    = 0;
    
	private static final List<String>							VALID_EXCLUDE_COMPONENTS	= Arrays
			.asList(
            new String[] { SEVERITY, TIME, SYSTEM, SUBSYSTEM, MESSAGE, DATA });
    
	private BundleContext  bc;
	@SuppressWarnings("unused")
	private ServiceTracker<LogReaderService,LogReaderService>	logReaderTracker;
	private Hashtable<String,LogRequest>						requests;
	
    // the state of the request table is stored here in atomic sessions 
	private Hashtable<String,LogRequest>						savedRequests;
    
	private LinkedList<LogEntry>								logList						= new LinkedList<>();
    
	LogPlugin(BundleContext bc,
			ServiceTracker<LogReaderService,LogReaderService> logReaderTracker)
            throws BundleException {
		this.bc = bc;
		this.logReaderTracker = logReaderTracker;
        
        LogReaderService logReader = 
            logReaderTracker.getService();
        if(logReader == null)
            throw new MissingResourceException("Log Reader service not found.",
                    LogReaderService.class.getName(), null);

        logReader.addLogListener(this);
        
		requests = new Hashtable<>();
	}

	@Override
	public void logged(LogEntry entry) {
		if (logList.size() >= 100) {
			logList.removeLast();
		}
		logList.addFirst(entry);
	}
	
	//----- DataPlugin methods -----//
    
    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        return this;
    }

    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        return this;
    }
    
    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        // session info not needed, it will come in the exec()
        
        if(!isEqualPath(sessionRoot, LogPluginActivator.PLUGIN_ROOT_PATH))
            throw new DmtException(sessionRoot, DmtException.COMMAND_FAILED,
                    "Fine-grained locking not supported in atomic sessions, " +
                    "session root must not be below \"./OSGi/Log\".");

        savedRequests = copyRequests(requests);

        return this;
    }
    
	//----- TransactionalDataSession methods -----//
    
    @Override
	public void commit() throws DmtException {
        // only called if session lock type is atomic
        savedRequests = copyRequests(requests);
    }    
    
	@Override
	public void rollback() throws DmtException {
        // only called if session lock type is atomic
	    requests = copyRequests(savedRequests);
    }

    //----- ReadWriteDataSession methods -----//

    @Override
	public void setNodeTitle(String[] fullPath, String title) 
            throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	@Override
	public void setNodeValue(String[] fullPath, DmtData data) 
            throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length >= 2 because there are no leaf nodes above this
        String id = path[0];
        LogRequest lr = requests.get(id);
        if (lr == null) {
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request exists");
        }
        String leaf = path[1];
        if (leaf.equals(EXCLUDE))
            lr.setExclude( data == null ? DEFAULT_EXCLUDE : data.getString() );
        else if (leaf.equals(FILTER)) {
            lr.filter = data == null ? DEFAULT_FILTER : data.getString();
            lr.logrecords = null; // reevaluate the log results
        }
        else if (leaf.equals(MAXR)) {
            lr.maxrecords = data == null ? DEFAULT_MAXR : data.getInt();
            lr.logrecords = null; // reevaluate the log results
        }
        else {
            // should never happen because of meta-data
            throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                "Node cannot be modified");
        }
	}

	@Override
	public void setNodeType(String[] fullPath, String type) 
            throws DmtException {
        // meta-data ensures that type is either null or the only possible value
        if(!isLeafNode(fullPath) && type != null)
            throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
                    "Cannot set type property of interior nodes in Log tree.");
	}

	@Override
	public void deleteNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length == 1 because only search request root is deletable
        String id = path[0];
        LogRequest lr = requests.get(id);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request, cannot be deleted");
        
        requests.remove(id);
    }

	@Override
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

    @Override
	public void createLeafNode(String[] fullPath, DmtData value, 
            String mimeType) throws DmtException {
        // should never be reached because of meta-data, every leaf is automatic
        throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                               "All leaf nodes are created automatically.");
    }
    
	@Override
	public void copy(String[] fullPath, String[] newFullPath, boolean recursive)
			throws DmtException {
		// ENHANCE allow cloning
        throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
                               "Cannot copy log request nodes.");
	}

	@Override
	public void renameNode(String[] fullPath, String newName) 
            throws DmtException {
		throw new DmtException(fullPath, DmtException.COMMAND_FAILED,
				               "Cannot rename log request nodes.");
	}

	//----- ReadableDataSession methods -----//

    @Override
	public void nodeChanged(String[] fullPath) {
        // do nothing - the version and timestamp properties are not supported
    }

	@Override
	public void close() throws DmtException {
		// nothing to do
	}

    @Override
	public MetaNode getMetaNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        if (path.length == 0) // OSGi/Log
            return new LogMetaNode(MetaNode.PERMANENT, 
                    !LogMetaNode.MODIFIABLE, !LogMetaNode.ALLOW_INFINITE, 
                    "Root node for log search requests.");
        
        if (path.length == 1) // OSGi/Log/<search_id>
            return new LogMetaNode(MetaNode.DYNAMIC,
                    LogMetaNode.MODIFIABLE, LogMetaNode.ALLOW_INFINITE,
                    "Root node of a log search request.");
        
        if (path.length == 2) { // OSGi/Log/<search_id>/<param>
            if(path[1].equals(FILTER))
                return new LogMetaNode(LogMetaNode.SEARCH_PARAMETER,
                        DmtData.FORMAT_STRING, new DmtData(DEFAULT_FILTER), null,  
                        "Filter expression to select log records included in the result.");
                
            if(path[1].equals(EXCLUDE))
                return new LogMetaNode(LogMetaNode.SEARCH_PARAMETER,
                        DmtData.FORMAT_STRING, new DmtData(DEFAULT_EXCLUDE), VALID_EXCLUDE_COMPONENTS,
                        "A list of log entry attributes not to include in the result records.");
            
            if(path[1].equals(MAXR))
                return new LogMetaNode(LogMetaNode.SEARCH_PARAMETER,
                        DmtData.FORMAT_INTEGER, new DmtData(DEFAULT_MAXR), null,
                        "The maximum number of records to be included in the result.");
            
            if(path[1].equals(LOGRESULT))
                return new LogMetaNode(MetaNode.AUTOMATIC,
                        !LogMetaNode.MODIFIABLE, !LogMetaNode.ALLOW_INFINITE, 
                        "Root node for log results.");
            
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND, 
                    "No such node defined in the logging tree");
        }

        // OSGi/Log/<search_id>/LogResult/<result_id>
        if (path.length == 3 && path[1].equals(LOGRESULT)) {
                return new LogMetaNode(MetaNode.AUTOMATIC,
                        !LogMetaNode.MODIFIABLE, LogMetaNode.ALLOW_INFINITE,
                        "Log result item node.");
        }
            
        // OSGi/Log/<search_id>/LogResult/<result_id>/<param>
        if(path.length == 4 && path[1].equals(LOGRESULT)) {
        	if( path[ 3 ].equals( SEVERITY ) )
                return new LogMetaNode(false, DmtData.FORMAT_INTEGER, null, null,
                          "The severity of a log result item.");
        	if( path[ 3 ].equals( TIME ) )
                return new LogMetaNode(false, DmtData.FORMAT_STRING, null, null,
                          "The timestamp of a log result item.");
        	if( path[ 3 ].equals( SYSTEM ) )
                return new LogMetaNode(false, DmtData.FORMAT_STRING, null, null,
                          "The system of a log result item.");
        	if( path[ 3 ].equals( SUBSYSTEM ) )
                return new LogMetaNode(false, DmtData.FORMAT_STRING, null, null,
                          "The subsystem of a log result item.");
        	if( path[ 3 ].equals( MESSAGE ) )
                return new LogMetaNode(false, DmtData.FORMAT_STRING, null, null,
                          "The message of a log result item.");
        	if( path[ 3 ].equals( DATA ) )
                return new LogMetaNode(false, DmtData.FORMAT_STRING, null, null,
                          "The data of a log result item.");
        }
        
        // path.length > 2
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                "No such node defined in the logging tree");
    }

    @Override
	public boolean isNodeUri(String[] fullPath) {
        String[] path = chopPath(fullPath);
        
        if (path.length == 0) // ./OSGi/Log
            return true;
        
        LogRequest lr = requests.get(path[0]);
        if(lr == null)
            return false;
        
        if (path.length == 1)
            return true;
        
        if (path.length == 2)
            return 
                path[1].equals(FILTER) || 
                path[1].equals(EXCLUDE) || 
                path[1].equals(MAXR) ||
				path[1].equals(LOGRESULT);

        if( lr.logrecords == null ) {
        	try {
        	    evaluateLogRequest( lr );
        	}catch( DmtException e ) {
        		return false;
        	}
        }
        	
        if( lr.logrecords.get(path[ 2 ] ) == null )
        	return false;
        
        if( path.length == 3 )
        	return true;
        
        if( path.length == 4 )
            return 
				(lr.include[LogRequest.SEVERITY_ID]  && path[3].equals(SEVERITY)  ) || 
				(lr.include[LogRequest.SYSTEM_ID]    && path[3].equals(SYSTEM)    ) || 
				(lr.include[LogRequest.SUBSYSTEM_ID] && path[3].equals(SUBSYSTEM) ) ||
				(lr.include[LogRequest.MESSAGE_ID]   && path[3].equals(MESSAGE)   ) ||
				(lr.include[LogRequest.DATA_ID]      && path[3].equals(DATA)      ) ||
				(lr.include[LogRequest.TIME_ID]      && path[3].equals(TIME)      );
        
        return false;
    }

    @Override
	public boolean isLeafNode(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        if(path.length == 0) // ./OSGi/Log
            return false;
        
        String id = path[0];
        LogRequest lr = requests.get(id);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if(path.length == 1) // ./OSGi/Log/<search_id>
            return false;
        
        if(path.length == 2)
            return !path[1].equals( LOGRESULT );
        
        if(path.length == 3 )
        	return false;
        if(path.length == 4 )
        	return true;
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }
    
	@Override
	public DmtData getNodeValue(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        
        // path.length > 1 because there are no leaf node above this
        LogRequest lr = requests.get(path[0]);
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

        if (path[1].equals(LOGRESULT)) {
        	if( lr.logrecords == null )
        		evaluateLogRequest( lr );

        	LogResultItem lri = lr.logrecords.get( path[2] );        	
        	leaf = path[ 3 ];
        	
        	if( lr.include[LogRequest.TIME_ID] && leaf.equals( TIME ) )
        		return new DmtData( lri.time );
        	if( lr.include[LogRequest.SYSTEM_ID] && leaf.equals( SYSTEM ) )
        		return new DmtData( lri.system );
        	if( lr.include[LogRequest.SUBSYSTEM_ID] && leaf.equals( SUBSYSTEM ) )
        		return new DmtData( lri.subsystem );
        	if( lr.include[LogRequest.SEVERITY_ID] && leaf.equals( SEVERITY ) )
        		return new DmtData( lri.severity );
        	if( lr.include[LogRequest.DATA_ID] && leaf.equals( DATA ) )
        		return new DmtData( lri.data );
        	if( lr.include[LogRequest.MESSAGE_ID] && leaf.equals( MESSAGE ) )
        		return new DmtData( lri.message );
        }
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                               "No such node");
    }

	@Override
	public String getNodeTitle(String[] fullPath) throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				               "Title property not supported.");
	}

	@Override
	public String getNodeType(String[] fullPath) throws DmtException {
        if(isLeafNode(fullPath))
            return LogMetaNode.LEAF_MIME_TYPE;
        
        String[] path = chopPath(fullPath);
        if(path.length == 0) // ./OSGi/Log
            return LogMetaNode.LOG_MO_TYPE;
        
        return null;
	}

	@Override
	public int getNodeVersion(String[] fullPath) throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	@Override
	public Date getNodeTimestamp(String[] fullPath) throws DmtException {
		throw new DmtException(fullPath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	@Override
	public int getNodeSize(String[] fullPath) throws DmtException {
        return getNodeValue(fullPath).getSize();
	}

	@Override
	public String[] getChildNodeNames(String[] fullPath) throws DmtException {
        String[] path = chopPath(fullPath);
        if (path.length == 0) {
            String[] requestArray = requests.keySet().toArray(new String[requests.size()]);
            // escape '/' and '\' characters in request IDs before returning 
            for(int i = 0; i < requestArray.length; i++)
                requestArray[i] = escape(requestArray[i]);

            return requestArray;
        }

        LogRequest lr = requests.get(path[0]);
        if (lr == null)
            throw new DmtException(fullPath, DmtException.NODE_NOT_FOUND,
                                   "No such log request");
        
        if (path.length == 1)
            return new String[] { FILTER, EXCLUDE, MAXR, LOGRESULT };

    	if( lr.logrecords == null )
    		evaluateLogRequest( lr );
    	
        if (path.length == 2) {
            String[] recordIDArray = lr.logrecords.keySet().toArray(new String[0]);
        	return recordIDArray;
        }
        
        if( path.length == 3 ) {
        	int availableItems = 0;
        	for( int q = 0; q != LogRequest.MAX_IDS; q++ )
        		if( lr.include[ q ] )
        			availableItems ++;
        	String[] childNames = new String[ availableItems ];
        	int cnt = 0;
        	
        	if( lr.include[ LogRequest.SEVERITY_ID ] )
        		childNames[ cnt++ ] = SEVERITY;
        	if( lr.include[ LogRequest.TIME_ID ] )
        		childNames[ cnt++ ] = TIME;
        	if( lr.include[ LogRequest.SYSTEM_ID ] )
        		childNames[ cnt++ ] = SYSTEM;
        	if( lr.include[ LogRequest.SUBSYSTEM_ID ] )
        		childNames[ cnt++ ] = SUBSYSTEM;
        	if( lr.include[ LogRequest.MESSAGE_ID ] )
        		childNames[ cnt++ ] = MESSAGE;
        	if( lr.include[ LogRequest.DATA_ID ] )
        		childNames[ cnt++ ] = DATA;
        	
        	return childNames;
        }
        
        // should never be reached
        throw new DmtException(fullPath, DmtException.METADATA_MISMATCH,
                               "The specified URI points to a leaf node.");
    }

	//----- Private utility methods -----//
	private static String[] chopPath(String[] absolutePath) {
		// DmtAdmin only gives us nodes in our subtree
        int rootLength = LogPluginActivator.PLUGIN_ROOT_PATH.length;
        String[] path = new String[absolutePath.length-rootLength];
        System.arraycopy(absolutePath, rootLength, path, 0, 
                absolutePath.length-rootLength);
        return path;
	}
    
    private static boolean isEqualPath(String[] path1, String[] path2) {
        if(path1.length != path2.length)
            return false;
        
        for(int i = 0; i < path1.length; i++)
            if(!path1[i].equals(path2[i]))
                return false;
            
        return true;
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
    
	private Hashtable<String,LogRequest> copyRequests(
			Hashtable<String,LogRequest> original) {
		Hashtable<String,LogRequest> copy = new Hashtable<>();
		Iterator<Entry<String,LogRequest>> i = original.entrySet().iterator();
        while (i.hasNext()) {
			Entry<String,LogRequest> entry = i.next();
			copy.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }

	/*
	 * Evaluate the result of a log request. Uses the filter and maxrecords.
	 * Returns a vector of logentries 
	 */
	private void evaluateLogRequest(LogRequest lr) throws DmtException {
		Filter filter = null;
		try {
			if (!lr.filter.equals(""))
                filter = bc.createFilter(lr.filter);
		} catch (InvalidSyntaxException e) {
			throw new DmtException(lr.path, DmtException.COMMAND_FAILED,
					"Cannot parse filter", e);
		}
        
// Changed to use LogListener to get logs.
//        LogReaderService logReader = 
//            (LogReaderService) logReaderTracker.getService();
//        if(logReader == null)
//            throw new MissingResourceException("Log Reader service not found.",
//                    LogReaderService.class.getName(), null);
//        
//		Enumeration e = logReader.getLog();
Enumeration<LogEntry> e = Collections.enumeration(logList);

		int max = Integer.MAX_VALUE;
		
		int logIDCounter = 1;
		lr.logrecords = new Hashtable<>();
		
		if (lr.maxrecords != 0)
			max = lr.maxrecords;

		while (e.hasMoreElements() && lr.logrecords.size() < max) {
			LogEntry le = e.nextElement();
			if (filter != null) {
				//create a dictionary for matching with the filter
				Hashtable<String,Object> dict = new Hashtable<>();
				dict.put(SEVERITY, Integer.valueOf(le.getLevel()));
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
			Bundle loggerBundle = le.getBundle();
			String bundleStr = (loggerBundle == null) ? "" : loggerBundle.toString(); 
			
			ServiceReference< ? > servRef = le.getServiceReference();
			String servRefStr = (servRef == null ) ? "" : servRef.toString();
			
			Throwable exception = le.getException();
			String exceptionStr;
            if(exception == null)
                exceptionStr = "";
            else {
                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                exceptionStr = sw.toString();
            }
			
			lr.logrecords.put( "LOG" + logIDCounter++, 
    			new LogResultItem( le.getLevel(), timeToString(le.getTime()), 
    				bundleStr, servRefStr, le.getMessage(), exceptionStr ) );
		}
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
        while(width > 0) {
            sb.append('0');
            width--;
        }
        sb.append(numStr);
    }
    
    class LogResultItem implements Cloneable {
    	int      severity   = 1;
    	String   time       = "";
    	String   system     = "";
    	String   subsystem  = "";
    	String   message    = "";
    	String   data       = "";

    	LogResultItem( int severity, String time, String system, 
    			              String subsystem, String message, String data ) {
    		this.severity  = severity;
    		this.time      = time;
    		this.system    = system;
    		this.subsystem = subsystem;
    		this.message   = message;
    		this.data      = data;    		
    	}
    	
    	@Override
		public LogResultItem clone() {
    		return new LogResultItem( severity, time, system, subsystem, message, data);
    	}
    }
    
	class LogRequest implements Cloneable {
		final static int  MAX_IDS          = 6;
		final static int  SEVERITY_ID      = 0;
		final static int  TIME_ID          = 1;
		final static int  MESSAGE_ID       = 2;
		final static int  SYSTEM_ID        = 3;
		final static int  SUBSYSTEM_ID     = 4;
		final static int  DATA_ID          = 5;
		
		String[]   path                    = null;
		String     filter                  = DEFAULT_FILTER;
		String     exclude                 = DEFAULT_EXCLUDE;
		boolean    include[]               = new boolean[ MAX_IDS ];
		int        maxrecords              = DEFAULT_MAXR;
		Hashtable<String,LogResultItem>	logrecords		= null;
        
		LogRequest() {
			for( int i=0; i!= MAX_IDS; i++ )
				include[ i ] = true;
		}
		
		void setExclude( String exclude ) {
			this.exclude = exclude;

			for( int j=0; j!= MAX_IDS; j++ )
				include[ j ] = true;
			
			String [] excludeStrs = Splitter.split( exclude, ',', 0);
			for( int i = 0; i != excludeStrs.length; i++ ) {
				String excludeItem = excludeStrs[ i ].trim();
				if( excludeItem.equals( TIME ))
					include[ TIME_ID ] = false;
				if( excludeItem.equals( DATA ))
					include[ DATA_ID ] = false;
				if( excludeItem.equals( SYSTEM ))
					include[ SYSTEM_ID ] = false;
				if( excludeItem.equals( SUBSYSTEM ))
					include[ SUBSYSTEM_ID ] = false;
				if( excludeItem.equals( MESSAGE ))
					include[ MESSAGE_ID ] = false;
				if( excludeItem.equals( SEVERITY ))
					include[ SEVERITY_ID ] = false;
			}
		}
		
		@Override
		public LogRequest clone() {
            LogRequest lr = new LogRequest();
            
            lr.path       = path;
            lr.filter     = filter;
            lr.exclude    = exclude;
            lr.maxrecords = maxrecords;
            
            for( int i=0; i != MAX_IDS; i++ )
            	lr.include[ i ] = include[ i ];
            
            if( logrecords != null ) {
				lr.logrecords = new Hashtable<>();
				Iterator<String> keys = logrecords.keySet().iterator();
            	
            	while( keys.hasNext() ) {
            		String newKey = new String( keys.next() );
            		LogResultItem newValue = logrecords.get( newKey ).clone();
            		lr.logrecords.put( newKey, newValue );
            	}
            } else         
                lr.logrecords = null;
            return lr;
        }
	}
}
