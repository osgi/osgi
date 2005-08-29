/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class implements permissions for manipulating applications and
 * their instances.
 * <P>
 * ApplicationAdminPermission can be targeted to applications that matches the
 * specified filter.
 * <P>
 * ApplicationAdminPermission may be granted for different actions:
 * <code>lifecycle</code>, <code>schedule</code> and <code>lock</code>.
 * The permission <code>schedule</code> implies the permission
 * <code>lifecycle</code>.
 */
public class ApplicationAdminPermission extends BasicPermission {
	private static final long serialVersionUID = 1L;
  
	/**
	 * Allows the lifecycle management of the target applications.
	 */
	public static final String LIFECYCLE = "lifecycle";

	/**
	 * Allows scheduling of the target applications. The permission to
	 * schedule an application implies that the scheduler can also 
	 * manage the lifecycle of that application i.e. <code>schedule</code>
	 * implies <code>lifecycle</code>
	 */
	public static final String SCHEDULE = "schedule";

	/**
	 * Allows setting/unsetting the locking state of the target applications.
	 */
	public static final String LOCK = "lock";

	/**
	 * Constructs an ApplicationAdminPermission. The <code>filter</code>
	 * specifies the target application. If the <code>filter</code> is
	 * <code>null</code> then it matches <code>"*"</code>. If
	 * <code>actions</code> is <code>"*"</code> then it identifies all the
	 * possible actions.
	 * 
	 * @param filter
	 *            filter to identify application. The value <code>null</code>
	 *            indicates "all application".
	 * @param actions
	 *            comma-separated list of the desired actions granted on the
	 *            applications or "*" means all the actions. It must not be
	 *            <code>null</code>. The order of the actions in the list is
	 *            not significant.
	 * 
	 * @exception NullPointerException
	 *                is thrown if the actions parameter is <code>null</code>
	 */
	public ApplicationAdminPermission(String filter, String actions) {
		super(filter == null ? "*" : actions);
		
		actionsVector = actionsVector( actions );

		if (!ACTIONS.containsAll(actionsVector))
      throw new IllegalArgumentException("Illegal action!");
	}

  public boolean implies(Permission p) {
  	  if( p == null )
  	  	return false;
  	  	
      if(!(p instanceof ApplicationAdminPermission))
          return false;

      ApplicationAdminPermission other = (ApplicationAdminPermission) p;
      
      if( getName().equals("<All>") )
      {      	
      	if( !other.getName().equals( getName() ) )
      		return false;
      }
      
      if( !actionsVector.containsAll( other.actionsVector ) )
      	return false;
      
      return true;
  }

  private static final Vector ACTIONS = new Vector();
  private Vector actionsVector;
  static {
      ACTIONS.add(LIFECYCLE.toLowerCase());
      ACTIONS.add(SCHEDULE.toLowerCase());
      ACTIONS.add(LOCK.toLowerCase());
  }

  private static Vector actionsVector(String actions) {
      Vector v = new Vector();
      StringTokenizer t = new StringTokenizer(actions.toUpperCase(), ",");
      while (t.hasMoreTokens()) {
          String action = t.nextToken().trim();
          v.add(action.toLowerCase());
      }
      return v;
  }
}
