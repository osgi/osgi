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
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class LogReadOnlySession implements ReadableDataSession, LogListener {

	protected int rootLength = 1;
	protected Vector<LogEntry>	logEntries				= new Vector<>();
	protected LogReaderService lr = null;
	protected int				lengthOfLogEntryList	= Integer
																.parseInt(RMTConstants
																		.getProperty(
																				"logEntry.list.length",
																				"200"));
	protected boolean keepLogEntriesFlag = false;

	LogReadOnlySession(BundleContext context) {
		ServiceReference<LogReaderService> sr = context
				.getServiceReference(LogReaderService.class);
		if (sr != null) {
			lr = context.getService(sr);
			lr.addLogListener(this);
		}
		if (RMTConstants.RMT_ROOT != null) {
			String[] rootArray = RMTUtil.pathToArrayUri(RMTConstants.RMT_ROOT + "/");
			rootLength = rootArray.length;
		}
	}

	public void removeListener() {
		if (lr != null)
			lr.removeLogListener(this);
	}

	@Override
	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and time stamp properties are not supported
	}

	@Override
	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			String[] children = new String[1];
			children[0] = RMTConstants.LOGENTRIES;
			return children;
		}

		if (path.length == 2) {
			int entrySize = logEntries.size();
			String[] children = new String[entrySize];
			for (int i = 0; i < entrySize; i++) {
				children[i] = Integer.toString(i);
			}
			return children;
		}

		if (path.length == 3) {
			try {
				LogEntry le = logEntries.get(Integer
						.parseInt(path[2]));
				if (le.getException() != null) {
					String[] children = new String[5];
					children[0] = RMTConstants.MESSAGE;
					children[1] = RMTConstants.BUNDLE;
					children[2] = RMTConstants.TIME;
					children[3] = RMTConstants.LEVEL;
					children[4] = RMTConstants.EXCEPTION;
					return children;
				} else if (le.getException() == null) {
					String[] children = new String[4];
					children[0] = RMTConstants.MESSAGE;
					children[1] = RMTConstants.BUNDLE;
					children[2] = RMTConstants.TIME;
					children[3] = RMTConstants.LEVEL;
					return children;
				}
			} catch (ArrayIndexOutOfBoundsException ae) {
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified node does not exist in the log object.");
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node does not exist in the log object.");
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return new LogMetaNode("Log root node.", MetaNode.PERMANENT,
					!LogMetaNode.CAN_ADD, !LogMetaNode.CAN_DELETE,
					LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE);

		if (path.length == 2 && path[1].equals(RMTConstants.LOGENTRIES))
			return new LogMetaNode("LogEntries node.", MetaNode.AUTOMATIC,
					!LogMetaNode.CAN_ADD, !LogMetaNode.CAN_DELETE,
					!LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE);

		if (path.length == 3 && path[1].equals(RMTConstants.LOGENTRIES))
			return new LogMetaNode("List of LogEntry.", MetaNode.DYNAMIC,
					!LogMetaNode.CAN_ADD, !LogMetaNode.CAN_DELETE,
					LogMetaNode.ALLOW_ZERO, LogMetaNode.ALLOW_INFINITE);

		if (path.length == 4) {
			if (path[3].equals(RMTConstants.BUNDLE))
				return new LogMetaNode(
						"The location of the bundle that originated this log.",
						!LogMetaNode.CAN_DELETE, !LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[3].equals(RMTConstants.TIME))
				return new LogMetaNode("Time of Log Entry.",
						!LogMetaNode.CAN_DELETE, !LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATE_TIME, null, null);

			if (path[3].equals(RMTConstants.LEVEL))
				return new LogMetaNode("The severity od the log entry.",
						!LogMetaNode.CAN_DELETE, !LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null, null);

			if (path[3].equals(RMTConstants.MESSAGE))
				return new LogMetaNode(
						"Human-readable description of the log.",
						!LogMetaNode.CAN_DELETE, !LogMetaNode.CAN_REPLACE,
						!LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);

			if (path[3].equals(RMTConstants.EXCEPTION))
				return new LogMetaNode(
						"Human readable information about an exception.",
						!LogMetaNode.CAN_DELETE, !LogMetaNode.CAN_REPLACE,
						LogMetaNode.ALLOW_ZERO, !LogMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null, null);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Log log object.");
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported in the Log log object.");
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported in the Log log object.");
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported in the Log log object.");
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			return RMTConstants.LOG_NODE_TYPE;
		}
		if (path.length == 2) {
			if (path[1].equals(RMTConstants.LOGENTRIES))
				return DmtConstants.DDF_LIST;
		}

		if (path.length == 3) {
			if (path[1].equals(RMTConstants.LOGENTRIES))
				return RMTConstants.LOG_NODE_TYPE;
		}

		if (isLeafNode(nodePath) && 
				(path[3].equals(RMTConstants.BUNDLE)
				||path[3].equals(RMTConstants.MESSAGE)
				||path[3].equals(RMTConstants.EXCEPTION)))
			return LogMetaNode.LEAF_MIME_TYPE;
		
		if (isLeafNode(nodePath) && 
				(path[3].equals(RMTConstants.TIME)
				||path[3].equals(RMTConstants.LEVEL)))
			return null;

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node is not found in the Log log object.");
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2)
			if (path[1].equals(RMTConstants.LOGENTRIES))
				return true;

		if (path.length == 3) {
			try {
				logEntries.get(Integer.parseInt(path[2]));
				return true;
			} catch (ArrayIndexOutOfBoundsException ae) {
				return false;
			} catch (NumberFormatException nfe) {
				return false;
			}

		}

		if (path.length == 4) {
			try {
				LogEntry le = logEntries.get(Integer
						.parseInt(path[2]));
				if (le.getException() != null) {
					if (path[3].equals(RMTConstants.BUNDLE)
							|| path[3].equals(RMTConstants.TIME)
							|| path[3].equals(RMTConstants.LEVEL)
							|| path[3].equals(RMTConstants.MESSAGE)
							|| path[3].equals(RMTConstants.EXCEPTION))
						return true;
				} else if (le.getException() == null) {
					if (path[3].equals(RMTConstants.BUNDLE)
							|| path[3].equals(RMTConstants.TIME)
							|| path[3].equals(RMTConstants.LEVEL)
							|| path[3].equals(RMTConstants.MESSAGE))
						return true;
				}
			} catch (ArrayIndexOutOfBoundsException ae) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length <= 3)
			return false;

		if (path.length == 4) {
			if (path[3].equals(RMTConstants.BUNDLE)
					|| path[3].equals(RMTConstants.TIME)
					|| path[3].equals(RMTConstants.LEVEL)
					|| path[3].equals(RMTConstants.MESSAGE)
					|| path[3].equals(RMTConstants.EXCEPTION))
				return true;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node is not found in the Log log object.");
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length <= 3)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 4) {
			try {
				LogEntry le = logEntries.get(Integer
						.parseInt(path[2]));
				if (path[3].equals(RMTConstants.BUNDLE)) {
					return new DmtData(le.getBundle().getLocation());
				}
				if (path[3].equals(RMTConstants.TIME)) {
					return new DmtData(new Date(le.getTime()));
				}
				if (path[3].equals(RMTConstants.LEVEL)) {
					return new DmtData(le.getLevel());
				}
				if (path[3].equals(RMTConstants.MESSAGE)) {
					return new DmtData(le.getMessage());
				}
				if (path[3].equals(RMTConstants.EXCEPTION)) {
					if (le.getException() != null)
						return new DmtData(le.getException().getMessage());
				}
			} catch (ArrayIndexOutOfBoundsException ae) {
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
	
	public void setKeepLogEntry(){
		this.keepLogEntriesFlag = true;
	}
	
	public void resetKeepLogEntry(){
		this.keepLogEntriesFlag = false;
	}

	@Override
	public void logged(LogEntry entry) {
		if(this.keepLogEntriesFlag)
			return;
		if(logEntries.size()>=lengthOfLogEntryList)
			for(int i = lengthOfLogEntryList-1;i<logEntries.size();i++)
				logEntries.remove(i);
		logEntries.add(0, entry);
	}
}
