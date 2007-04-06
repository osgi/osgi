/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.application;

import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.application.*;

public class ApplicationDescriptorImpl {
	private ApplicationDescriptor descriptor;
	private static Properties			locks;
	private String 								pid;
	
	public synchronized void setApplicationDescriptor(ApplicationDescriptor d, String pid ) {
		descriptor = d;
		this.pid = pid;
	}

	public boolean isLocked() {
		return doLock(true, false );
	}

	synchronized boolean doLock(final boolean query, final boolean newState) {
		try {		  
			return ((Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction() {
			  public Object run() throws Exception {			
				File f = Activator.bc.getDataFile("locks");
				if ( locks == null ) {
					locks = new Properties();
					if ( f.exists() )
						locks.load( new FileInputStream(f));
				}
				boolean current = locks.containsKey( pid ); 
				if ( query || newState == current )
					return new Boolean( current );

				if ( current )
					locks.remove( pid );
				else
					locks.put( pid, "locked");
				locks.save(new FileOutputStream(f), "Saved " + new Date());
				return new Boolean( newState );
			  }
		  })).booleanValue();
		} catch( PrivilegedActionException pe ) {
			pe.printStackTrace();
		}
		return false;
	}
	
	public void lock() {
		SecurityManager sm = System.getSecurityManager();
		if( sm != null )
			sm.checkPermission( new ApplicationAdminPermission( descriptor, ApplicationAdminPermission.LOCK_ACTION ) );
		
		doLock(false, true);
	}

	public void unlock() {
		SecurityManager sm = System.getSecurityManager();
		if( sm != null )
			sm.checkPermission( new ApplicationAdminPermission( descriptor, ApplicationAdminPermission.LOCK_ACTION ) );
		
		doLock(false, false);
	}

	public ScheduledApplication schedule(String schedId, Map args, String topic, String filter, boolean recurs) throws InvalidSyntaxException, ApplicationException {
		checkArguments( args );
		return Activator.scheduler.addScheduledApplication( schedId, descriptor, args, topic, filter, recurs );
	}
	
	private static void checkArguments( Map arguments ) throws IllegalArgumentException {
		if( arguments != null ) {
			Set set = arguments.keySet();
			Iterator iter = set.iterator();
			
			while( iter.hasNext() ) {
				Object o = iter.next();
				if( o == null )
					throw new IllegalArgumentException( "Argument key cannot be null!" );
				
				if( !(o instanceof String ) )
					throw new IllegalArgumentException( "Argument keys must be strings!" );
				
				String s = (String)o;
				if( s.equals("") )
					throw new IllegalArgumentException( "Empty string not allowed as key!" );
			}
		}		
	}
	
	public void launch(Map arguments) throws ApplicationException, IllegalArgumentException {
		SecurityManager sm = System.getSecurityManager();
		if( sm != null )
			sm.checkPermission( new ApplicationAdminPermission(	descriptor, ApplicationAdminPermission.LIFECYCLE_ACTION ) );

		if ( isLocked() )
			throw new ApplicationException( ApplicationException.APPLICATION_LOCKED, "Application is locked, can't launch!");
		
		checkArguments( arguments );
	}
}
