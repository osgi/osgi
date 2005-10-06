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
public class ApplicationAdminPermission extends Permission {
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

	private final ApplicationDescriptor	applicationDescriptor;

	/**
	 * Constructs an ApplicationAdminPermission. The <code>filter</code>
	 * specifies the target application. The <code>filter</code> is an
	 * LDAP-style filter, the recognized properties are <code>signer</code>
	 * and <code>pid</code>. The pattern specified in the <code>signer</code>
	 * is matched with the Distinguished Name chain used to sign the application. 
	 * Wildcards in a DN are not matched according to the filter string rules, 
	 * but according to the rules defined for a DN chain. The attribute 
	 * <code>pid</code> is matched with the PID of the application according to
	 * the filter string rules. 
	 * 
	 * If the <code>filter</code> is <code>null</code> then it matches 
	 * <code>"*"</code>. If
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
	 * 
	 * @see ApplicationDescriptor
	 * @see org.osgi.framework.AdminPermission
	 */
	public ApplicationAdminPermission(String filter, String actions) {
		super(filter == null ? "*" : filter);
		
		this.filter = (filter == null ? "*" : filter);
		this.actions = actions;
		
		actionsVector = actionsVector( actions );

		if ( actions.equals("*") )
			actionsVector = actionsVector( LIFECYCLE + "," + SCHEDULE + "," + LOCK );
		else if (!ACTIONS.containsAll(actionsVector))
      throw new IllegalArgumentException("Illegal action!");
		
		applicationID = null;
		this.applicationDescriptor = null;
	}
	
	/**
	 * This contructor should be used when creating <code>ApplicationAdminPermission</code>
	 * instance for <code>checkPermission</code> call. 
	 * @param application the tareget of the operation, it must not be <code>null</code>
	 * @param actions the required operation. it must not be <code>null</code>
	 */
	public ApplicationAdminPermission(ApplicationDescriptor application, String actions) {
		super(application.getApplicationId());
		this.filter = application.getApplicationId();
		this.applicationDescriptor = application;
		this.actions = actions;
	}
	
	/**
	 * This method can be used in the {@link java.security.ProtectionDomain}
	 * implementation in the <code>implies</code> method to insert the
	 * application ID of the current application into the permission being
	 * checked. This enables the evaluation of the 
	 * <code>&lt;$lt;SELF&gt;&gt;</code> pseudo targets.
	 * @param applicationId the ID of the current application
	 * @return the permission updated with the ID of the current application
	 */
	public ApplicationAdminPermission setCurrentApplicationId(String applicationId) {
		ApplicationAdminPermission newPerm = new ApplicationAdminPermission( this.applicationDescriptor, 
				this.actions );
		
		newPerm.applicationID = applicationId;
		
		return newPerm;
	}

  public boolean implies(Permission p) {
  	  if( p == null )
  	  	return false;
  	  	
      if(!(p instanceof ApplicationAdminPermission))
          return false;

      ApplicationAdminPermission other = (ApplicationAdminPermission) p;

      /* TODO filter check */
      if( !filter.equals("*") ) {
      	if( filter.equals( "<<SELF>>") ) {
      		if( other.applicationID == null )
      			return false; /* it cannot be, this might be a bug */
      		if( !other.applicationID.equals( other.filter ) )
      			return false;
      	}
      	else if( !filter.equals( other.filter ) ) /* TODO LDAP match */
      		return false;
      }
      
      if( !actionsVector.containsAll( other.actionsVector ) )
      	return false;
      
      return true;
  }

  private String applicationID;

  private static final Vector ACTIONS = new Vector();
  private Vector actionsVector;
  private final String filter;
  private final String actions;
  
  static {
      ACTIONS.add(LIFECYCLE);
      ACTIONS.add(SCHEDULE);
      ACTIONS.add(LOCK);
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

  public boolean equals(Object with) {
  	if( with == null || !(with instanceof ApplicationAdminPermission) )
  		return false;
  	
  	ApplicationAdminPermission other = (ApplicationAdminPermission)with;  	
  	
  	if( other.actionsVector.size() != actionsVector.size() )
  		return false;
  	
  	for( int i=0; i != actionsVector.size(); i++ )
  		if( !other.actionsVector.contains( actionsVector.get( i ) ) )
  			return false;
  	
  	return filter.equals( other.filter );
  }

  public int hashCode() {
	  return actions.hashCode() + filter.hashCode();
  }

  public String getActions() {
  	return actions;
  }
}
