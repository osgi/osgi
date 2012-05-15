/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.*;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadableDataSession;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class LogReadOnlySession implements ReadableDataSession, LogListener {

	protected static final String LOG = "Log";
	protected static final String LOGENTRIES = "LogEntries";
	protected static final String BUNDLE = "Bundle";
	protected static final String TIME = "Time";
	protected static final String LEVEL = "Level";
	protected static final String MESSAGE = "Message";
	protected static final String EXCEPTION = "Exception";
	protected static final String LOG_NODE_TYPE = "org.osgi/1.0/LogManagementObject";
	protected static final String KEY_OF_RMT_ROOT_URI = "org.osgi.dmt.residential";
	protected int rootLength = 1;
	
	protected Vector logEntries = new Vector();
	protected BundleContext context;
	

	LogReadOnlySession(LogPlugin plugin, BundleContext context) {
		this.context = context;
		ServiceReference sr = context.getServiceReference(LogReaderService.class.getName());
		LogReaderService lr = (LogReaderService)context.getService(sr);
		lr.addLogListener(this);
		
		String root = System.getProperty(KEY_OF_RMT_ROOT_URI);
		if (root != null) {
			String[] rootArray = pathToArrayUri(root + "/");
			rootLength = rootArray.length;
		}
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and time stamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			String[] children = new String[1];
			children[0] = LOGENTRIES;
			return children;
		}

		if (path.length == 2) {
			int entrySize = logEntries.size();
			String[] children = new String[entrySize];
			for(int i=0;i<entrySize;i++){
				children[i] = Integer.toString(i);
			}
			return children;
		}
		
		if (path.length == 3){
			try{
				LogEntry le = (LogEntry)logEntries.get(Integer.parseInt(path[2]));
				if(le.getException()!=null){
					String[] children = new String[5];
					children[0] = MESSAGE;
					children[1] = BUNDLE;
					children[2] = TIME;
					children[3] = LEVEL;
					children[4] = EXCEPTION;
					return children;
				}else if(le.getException()==null){
					String[] children = new String[4];
					children[0] = MESSAGE;
					children[1] = BUNDLE;
					children[2] = TIME;
					children[3] = LEVEL;
					return children;
				}
			}catch(ArrayIndexOutOfBoundsException ae){
				String[] children = new String[0];
				return children;
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Log MO.");
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return new LogMetaNode("Log root node.",
					MetaNode.PERMANENT, 
					!LogMetaNode.CAN_ADD,
					!LogMetaNode.CAN_DELETE,
					LogMetaNode.ALLOW_ZERO,
					!LogMetaNode.ALLOW_INFINITE);

		if (path.length == 2)
			return new LogMetaNode("LogEntries node.",
					MetaNode.AUTOMATIC, 
					!LogMetaNode.CAN_ADD,
					!LogMetaNode.CAN_DELETE,
					!LogMetaNode.ALLOW_ZERO,
					!LogMetaNode.ALLOW_INFINITE);
		
		if (path.length == 3)
			return new LogMetaNode("List of LogEntry.",
					MetaNode.DYNAMIC, 
					!LogMetaNode.CAN_ADD,
					!LogMetaNode.CAN_DELETE,
					LogMetaNode.ALLOW_ZERO,
					LogMetaNode.ALLOW_INFINITE);

		if (path.length == 4) {
			if (path[3].equals(BUNDLE))
				return new LogMetaNode(
						"The location of the bundle that originated this log.",
						!LogMetaNode.CAN_DELETE,
						!LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO,
						!LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[3].equals(TIME))
				return new LogMetaNode(
						"Time of Log Entry.",
						!LogMetaNode.CAN_DELETE,
						!LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO,
						!LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATE_TIME, null, null);
			
			if (path[3].equals(LEVEL))
				return new LogMetaNode(
						"The severity od the log entry.",
						!LogMetaNode.CAN_DELETE,
						!LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO,
						!LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null, null);
			
			if (path[3].equals(MESSAGE))
				return new LogMetaNode(
						"Human-readable description of the log.",
						!LogMetaNode.CAN_DELETE,
						!LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO,
						!LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);
			
			if (path[3].equals(EXCEPTION))
				return new LogMetaNode(
						"Human readable information about an exception.",
						!LogMetaNode.CAN_DELETE,
						!LogMetaNode.CAN_REPLACE,
						LogMetaNode.ALLOW_ZERO,
						!LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Log MO.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported in the Log MO.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported in the Log MO.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported in the Log MO.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			return LOG_NODE_TYPE;
		}
		if (path.length == 2) {
			return DmtConstants.DDF_LIST;
		}
		
		if (isLeafNode(nodePath))
			return LogMetaNode.LEAF_MIME_TYPE;
		
		return LogMetaNode.LOG_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2)
			if (path[1].equals(LOGENTRIES))
				return true;

		if (path.length == 3) {
			try{
				logEntries.get(Integer.parseInt(path[2]));
				return true;
				}catch(ArrayIndexOutOfBoundsException ae){
					return false;
			}
		}
		
		if (path.length == 4) {
			try{
				LogEntry le = (LogEntry)logEntries.get(Integer.parseInt(path[2]));
				if(le.getException()!=null){
					if(path[3].equals(BUNDLE)
							|| path[3].equals(TIME)
							|| path[3].equals(LEVEL)
							|| path[3].equals(MESSAGE)
							|| path[3].equals(EXCEPTION))
						return true;
				} else if (le.getException()==null){
					if(path[3].equals(BUNDLE)
							|| path[3].equals(TIME)
							|| path[3].equals(LEVEL)
							|| path[3].equals(MESSAGE))
						return true;
				}
			}catch(ArrayIndexOutOfBoundsException ae){
				return false;
			}
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length <= 3)
			return false;

		if (path.length == 4) {
			if(path[3].equals(BUNDLE)
					|| path[3].equals(TIME)
					|| path[3].equals(LEVEL)
					|| path[3].equals(MESSAGE)
					|| path[3].equals(EXCEPTION))
				return true;
		}
		
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length <= 3)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 4) {
			try{
				LogEntry le = (LogEntry)logEntries.get(Integer.parseInt(path[2]));
				if(path[3].equals(BUNDLE)){
					return new DmtData(le.getBundle().getLocation());
				}
				if(path[3].equals(TIME)){
					return new DmtData(new Date(le.getTime()));
				}
				if(path[3].equals(LEVEL)){
					return new DmtData(le.getLevel());
				}
				if(path[3].equals(MESSAGE)){
					return new DmtData(le.getMessage());
				}
				if(path[3].equals(EXCEPTION)){
					if(le.getException()!=null)
						return new DmtData(le.getException().getMessage());
				}
				}catch(ArrayIndexOutOfBoundsException ae){
					throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
					"The specified leaf node does not exist in the log object.");					
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the Log MO.");
	}

	// ----- Utilities -----//
	protected String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = rootLength;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}
	
	protected String[] pathToArrayUri(String path) {
		Vector vector = new Vector();
		while (path.indexOf("/") != -1) {
			String start_path = path.substring(0, path.indexOf("/"));
			vector.add(start_path);
			path = path.substring(path.indexOf("/") + 1, path.length());
		}
		String[] arrayPath = new String[vector.size()];
		int i = 0;
		for (Iterator it = vector.iterator(); it.hasNext(); i++) {
			arrayPath[i] = (String) it.next();
		}
		return arrayPath;
	}

	public void logged(LogEntry entry) {
		logEntries.add(0,entry);
	}
}
