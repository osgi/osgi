/*
 * Created on May 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.osgi.tools.btool;


import java.io.*;

/**
 * @author Peter Kriens
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PermissionFileResource extends FileResource {

	PermissionFileResource(BTool btool, File file) {
		super(btool, "META-INF/permissions", file, true);
	}

}
