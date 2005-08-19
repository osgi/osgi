/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 13/04/2005   Leonardo Barros
 * 33           Implement MEG TCK
 */
package org.osgi.test.cases.policy.tbc.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public class TestBundle implements Bundle {
	public int getState() {return 0; }
	public void start() throws BundleException { }
	public void stop() throws BundleException { }
	public void update() throws BundleException { }
	public void update(InputStream in) throws BundleException { }
	public void uninstall() throws BundleException { }
	public Dictionary getHeaders() {return null; }
	public long getBundleId() { return 0; }
	public String getLocation() {return ""; }
	public ServiceReference[] getRegisteredServices() {return null; }
	public ServiceReference[] getServicesInUse() {return null; }
	public boolean hasPermission(Object permission) {return true; }
	public URL getResource(String name) { return null;}
	public Dictionary getHeaders(String locale) { return null;}
	public String getSymbolicName() {return ""; }
	public Class loadClass(String name) throws ClassNotFoundException { return null;}
	public Enumeration getResources(String name) {return null; }
	public Enumeration getEntryPaths(String path) { return null; }
	public URL getEntry(String name) { return null; }
	public long getLastModified() { return 0; }
	public Enumeration findEntries(String path, String filePattern, boolean recurse) { return null; }
}
