/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

import info.dmtree.*;
import java.util.*;

public class ApplicationPluginBaseNode implements MetaNode {
	protected String 	name;
	protected String 	type;
	protected int    	scope = -1;
	protected int    	format = -1;
	protected int    	maxOccurrence = 1;
	protected boolean	zeroOccurrenceAllowed = false;
	
	protected boolean	canDelete = false;
	protected boolean	canAdd = false;
	protected boolean	canGet = false;
	protected boolean	canReplace = false;
	protected boolean	canExecute = false;
	protected boolean	isLeaf = false;
	
	protected Vector  children = new Vector();
	
	private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
		"org.osgi.impl.service.dmt.interior-node-value-support";

	protected ApplicationPluginBaseNode() {
		canDelete = canAdd = canReplace = canExecute = isLeaf = false;
		canGet = true;
		this.name = null;
		this.type = null;
		this.format = DmtData.FORMAT_NODE;
	}

	protected ApplicationPluginBaseNode( String name ) {
		init( name, null, null, null );
	}
	
	protected ApplicationPluginBaseNode( String name, String type, int format ) {
		canDelete = canAdd = canReplace = canExecute = false;
		canGet = isLeaf = true;
		this.format = format;
		this.name = name;
		this.type = type;
	}
	
	protected void init( String name, ApplicationPluginBaseNode child1, ApplicationPluginBaseNode child2, 
			            ApplicationPluginBaseNode child3 ) {		
		canDelete = canAdd = canReplace = canExecute = isLeaf = false;
		canGet = true;
		this.name = name;
		this.type = null;
		this.format = DmtData.FORMAT_NODE;
		
		if( child1 != null )
			addChildNode( child1 );
		if( child2 != null )
			addChildNode( child2 );
		if( child3 != null )
			addChildNode( child3 );
	}
	
	protected ApplicationPluginBaseNode( String name, ApplicationPluginBaseNode child1 ) {
		init( name, child1, null, null );
	}

	protected ApplicationPluginBaseNode( String name, ApplicationPluginBaseNode child1,
			                                 ApplicationPluginBaseNode child2 ) {
		init( name, child1, child2, null );
	}
	
	protected ApplicationPluginBaseNode( String name, ApplicationPluginBaseNode child1,
                   ApplicationPluginBaseNode child2, ApplicationPluginBaseNode child3 ) {
    init( name, child1, child2, child3 );
  }
	
	protected String [] getNames( String []path ) {
		return new String[] { name };
	}
	
	final public ApplicationPluginBaseNode search( String path[], int ndx ) {
		String names[] = getNames( path );
		
		for( int i=0; i != names.length; i++ )
			if( names[ i ].equals( path[ ndx ] ) ) {
				if( path.length == ndx + 1 ) /* found myself */
					return this;					
				
			for( int j=0; j != children.size(); j++ ) {
				ApplicationPluginBaseNode child = (ApplicationPluginBaseNode)children.get(j);
				ApplicationPluginBaseNode childResult = child.search( path, ndx + 1 );
				if( childResult != null )
					return childResult;
			}					
			return null;
		}
		return null;		
	}
	
	void recursiveSetScope( ApplicationPluginBaseNode node, int parentScope ) {
		if( node.scope == -1 ) {
			if( parentScope == MetaNode.DYNAMIC )
				node.scope = MetaNode.AUTOMATIC;
			else
				node.scope = parentScope;
		}
		if( node.scope != -1 ) {
			for( int j=0; j != node.children.size(); j++ )
				recursiveSetScope( (ApplicationPluginBaseNode)node.children.get(j), node.scope );
		}
	}
	
	protected void addChildNode( ApplicationPluginBaseNode node ) {
		recursiveSetScope( node, scope );
		children.add( node );
	}

	void setScope( int value ) {
		scope = value;
	}
	
	void setZeroOccurrenceAllowed( boolean allowed ) {
		zeroOccurrenceAllowed = allowed;
	}
	
	void setMaxOccurrence( int max ) {
		maxOccurrence = max;
	}
	
	public String [] getChildNodeNames( String path[] ) {
		Vector names = new Vector();
		
		for( int i=0; i != children.size(); i++ ) {
			ApplicationPluginBaseNode child = (ApplicationPluginBaseNode)children.get(i);
			String nms[] = child.getNames( path );
			for( int j=0; j != nms.length; j++ )
				names.add( nms[ j ]);
		}
		
		String result[] = new String[ names.size() ];
		for( int k=0; k != result.length; k++ )
			result[ k ] = (String)names.get( k );
		return result;
	}
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		throw new DmtException(path, DmtException.COMMAND_FAILED, "Cannot execute the node.");
	}
	
	public void createInteriorNode(String path[], String type) throws DmtException {
		throw new DmtException(path, DmtException.METADATA_MISMATCH, "Operation is not allowed.");		
	}
	
	public void createLeafNode(String path[], DmtData value, String type) throws DmtException {		
    throw new DmtException(path, DmtException.METADATA_MISMATCH, "Operation is not allowed.");
	}
	
	public void deleteNode(String path[]) throws DmtException {
    throw new DmtException(path, DmtException.METADATA_MISMATCH, "Operation is not allowed.");		
	}
	
	public void setNodeType(String path[], String type) throws DmtException {
		this.type = type;
	}
	
	public void setNodeValue(String path[], DmtData value) throws DmtException {
		throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot change the value of the node!" );		
	}

	public DmtData getNodeValue(String path[]) throws DmtException {
		throw new DmtException(path, DmtException.METADATA_MISMATCH, "Node has no value!" );		
	}	
	
	public ApplicationPluginBaseNode getAdditiveChild() throws DmtException {
		for( int i=0; i != children.size(); i++ ) {
			ApplicationPluginBaseNode child = (ApplicationPluginBaseNode)children.get(i);
			if( child.canAdd )
				return child;
		}
		return null;
	}
	
	public String getNodeType(String path[]) {
		return type;
	}
	
	/* ---------------------------------------------------------------------- */
  public boolean can(int operation) {
    switch(operation) {
    case CMD_DELETE:  return canDelete;
    case CMD_ADD:     return canAdd;
    case CMD_GET:     return canGet;
    case CMD_REPLACE: return canReplace;
    case CMD_EXECUTE: return canExecute;
    }
    return false;
  }
  
	public boolean isLeaf() {
		return isLeaf;
	}

	public int getScope() {
		return scope;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	public boolean isZeroOccurrenceAllowed() {
		return zeroOccurrenceAllowed;
	}

	public DmtData getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasMax() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMin() {
		// TODO Auto-generated method stub
		return false;
	}

	public double getMax() {
		// TODO Auto-generated method stub
		return Double.MAX_VALUE;
	}

	public double getMin() {
		// TODO Auto-generated method stub
		return Double.MIN_VALUE;
	}

	public DmtData[] getValidValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFormat() {
		return format;
	}

	public String[] getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValidNames() {
		// TODO Auto-generated method stub
		return null;
	}

  public boolean isValidValue(DmtData arg0) {
    // TODO Auto-generated method stub
    return true;
  }

  public boolean isValidName(String arg0) {
    // TODO Auto-generated method stub
    return true;
  }

	public String getPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamePattern() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getRawFormatNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getExtensionPropertyKeys() {
		return new String[] {INTERIOR_NODE_VALUE_SUPPORT_PROPERTY};
	}

	public Object getExtensionProperty(String key) {
		if (key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
			return new Boolean(false); // :)
		throw new IllegalArgumentException("Only the '" + 
				INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
				"' extension property is supported by this plugin");
	}  
}
