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

package org.osgi.test.applet;

import java.io.InputStream;
import java.lang.reflect.Method;

import netscape.application.*;
import netscape.util.*;

public class NPlan {
	Archive		archive;
	Hashtable		nameToComponent;
	Vector			rootViews;
	private Size	documentSize;
	
	public NPlan( InputStream in, final Object loader ) throws Exception {
		archive = new Archive();
		archive.readASCII(in);
		Unarchiver unarchiver = new Unarchiver(archive) {
			public Class classForName(String clazz) {
				try {
					Method m = loader.getClass().getMethod("classForName", new Class[] {String.class} );
					Class c = (Class) m.invoke(loader, new Object[] {clazz} );
					if ( c != null )
						return c;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					return Class.forName(clazz);
				}
				catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		};
	    int [] rootIds = archive.rootIdentifiers();
	    Hashtable documentValues = (Hashtable)unarchiver.unarchiveIdentifier(rootIds[0]);
	    nameToComponent = (Hashtable) documentValues.get("nameToComponent");
	    rootViews = (Vector) documentValues.get("rootComponents");
	    documentSize = (Size) documentValues.get("documentSize");
	}
		
	public View viewWithContents() {
	    View view = new View(0,0,documentSize.width, documentSize.height);
	    for ( Enumeration e = rootViews.elements(); e.hasMoreElements(); ) {
	    	Object o = e.nextElement();
			if (o instanceof View && !(o instanceof Window)) {
				view.addSubview((View) o);
			}
	    }
	    return view;
	}

	public Object componentNamed(String name) {
		return nameToComponent.get(name);
	}
}
