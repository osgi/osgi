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

package org.osgi.tools.cmscr;

import java.io.*;
import java.net.URL;
import java.util.*;

import nanoxml.XMLElement;

import org.osgi.framework.Bundle;

public class BundleProxy {
	Bundle	bundle;
	boolean	verified;

	BundleProxy(Bundle b) {
		this.bundle = b;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(":");
		sb.append(bundle.getBundleId());
		sb.append(get("Service-Component"));
		return sb.toString();
	}

	String getName() {
		String name = get("Bundle-SymbolicName");
		if (name == null)
			name = get("Bundle-Name");
		if (name == null)
			name = bundle.getLocation();
		return name;
	}

	String get(String key) {
		return (String) bundle.getHeaders().get(key);
	}

	void verify() {
		StringBuffer		report = new StringBuffer();
		StringTokenizer	st = new StringTokenizer( get("Service-Component"), "," );
		while ( st.hasMoreTokens() ) try {
			String file = st.nextToken();
			URL	url = bundle.getEntry(st.nextToken());
			if ( url == null ) 
				error("No XML for " + file );
			else {
				try {
					XMLElement		element = new XMLElement();
					InputStream in = url.openStream();
					element.parseFromReader( new InputStreamReader(in));
					findComponents(element);
				}
				catch( Exception e ) {
					error( "Exception " + e );
				}
			}
		} catch( Exception e ) {
			error( "Exception " + e );			
		}
	}

	private void findComponents(XMLElement element) {
		if ( element.getTagName().equals("component")) 
			doComponent(element);
		else {
			for ( Enumeration e = element.enumerateChildren(); e.hasMoreElements(); ) {
				XMLElement	child = (XMLElement) e.nextElement();
				findComponents(child);
			}
		}		
	}

	private void doComponent(XMLElement component) {
		Enumeration e = component.enumerateChildren();
		if ( ! e.hasMoreElements() ) 
			error("No element, expected implementation");
		else {
			doImplementation((XMLElement) e.nextElement());
			if ( e.hasMoreElements() ) {
				XMLElement next = (XMLElement) e.nextElement();
				while ( next.getTagName().equals("property") ) {
					next = (XMLElement) e.nextElement();
				}
			}
		}		
	}

	private void doImplementation(XMLElement element) {
		// TODO Auto-generated method stub
		
	}

	private Object XMLElements() {
		// TODO Auto-generated method stub
		return null;
	}

	private void error(String string) {
		// TODO Auto-generated method stub
		
	}
}