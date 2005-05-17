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
	public int getState() { throw new IllegalStateException(); }
	public void start() throws BundleException { throw new IllegalStateException(); }
	public void stop() throws BundleException { throw new IllegalStateException(); }
	public void update() throws BundleException { throw new IllegalStateException(); }
	public void update(InputStream in) throws BundleException { throw new IllegalStateException(); }
	public void uninstall() throws BundleException { throw new IllegalStateException(); }
	public Dictionary getHeaders() { throw new IllegalStateException(); }
	public long getBundleId() { throw new IllegalStateException(); }
	public String getLocation() { throw new IllegalStateException(); }
	public ServiceReference[] getRegisteredServices() { throw new IllegalStateException(); }
	public ServiceReference[] getServicesInUse() { throw new IllegalStateException(); }
	public boolean hasPermission(Object permission) { throw new IllegalStateException(); }
	public URL getResource(String name) { throw new IllegalStateException(); }
	public Dictionary getHeaders(String locale) { throw new IllegalStateException(); }
	public String getSymbolicName() { throw new IllegalStateException(); }
	public Class loadClass(String name) throws ClassNotFoundException { throw new IllegalStateException(); }
	public Enumeration getResources(String name) { throw new IllegalStateException(); }
	public Enumeration getEntryPaths(String path) { throw new IllegalStateException(); }
	public URL getEntry(String name) { throw new IllegalStateException(); }
	public long getLastModified() { throw new IllegalStateException(); }
	public Enumeration findEntries(String path, String filePattern, boolean recurse) { throw new IllegalStateException(); }
}
