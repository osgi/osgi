/*
 * $Header$
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

package org.osgi.impl.service.metatype;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.osgi.service.metatype.*;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class OCDProxy implements ObjectClassDefinition {
	AttributeDefinition	EMPTY_ADS[]	= new AttributeDefinition[0];
	OCD					ocd;
	URL					resource;
	Properties			properties;

	OCDProxy(OCD ocd, URL resource) {
		this.ocd = ocd;
		this.resource = resource;
	}

	public String getName() {
		return translate(ocd.name);
	}

	public String getID() {
		return ocd.id;
	}

	public String getDescription() {
		return translate(ocd.description);
	}

	String translate(String source) {
		source = source.trim().substring(1);
		if ( resource != null ) 		
		try {
			synchronized (this) {
				if (properties == null) {
					properties = new Properties();
					properties.load( resource.openStream() );
				}
			}
			
			String translation = properties.getProperty(source);
			if ( translation != null )
				return translation;
		}
		catch( IOException e ) {
			// TODO log
		}
		return source;
	}

	public AttributeDefinition[] getAttributeDefinitions(int filter) {
		Vector v = new Vector();
		for (Iterator i = ocd.attributes.values().iterator(); i.hasNext();) {
			AD ad = (AD) i.next();
			if (filter == ALL || ad.required == filter)
				v.add(new ADProxy(this, ad));
		}
		return (AttributeDefinition[]) v.toArray(EMPTY_ADS);
	}

	public InputStream getIcon(int size) throws IOException {
		if ( ocd.icons == null )
			return null;

		Object [] sizes = ocd.icons.keySet().toArray();
		Arrays.sort(sizes);
		
		int n = Arrays.binarySearch(sizes, new Integer(size));		
		String icon = (String) ocd.icons.get( sizes[n] );
		icon = translate(icon);

		URL url = ocd.mti.bundle.getResource(icon);
		if ( url == null )
			// TODO log
			return null;
		

		
		return url.openStream();
	}

	public String toString() {
		return ocd.toString();
	}
}
