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

package junit.test;

import java.io.*;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.framework.Bundle;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class TestBundle implements Bundle {

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getState()
	 */
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @throws org.osgi.framework.BundleException
	 * @see org.osgi.framework.Bundle#start()
	 */
	public void start() throws BundleException {
		// TODO Auto-generated method stub

	}

	/**
	 * @throws org.osgi.framework.BundleException
	 * @see org.osgi.framework.Bundle#stop()
	 */
	public void stop() throws BundleException {
		// TODO Auto-generated method stub

	}

	/**
	 * @throws org.osgi.framework.BundleException
	 * @see org.osgi.framework.Bundle#update()
	 */
	public void update() throws BundleException {
		// TODO Auto-generated method stub

	}

	/**
	 * @param in
	 * @throws org.osgi.framework.BundleException
	 * @see org.osgi.framework.Bundle#update(java.io.InputStream)
	 */
	public void update(InputStream in) throws BundleException {
		// TODO Auto-generated method stub

	}

	/**
	 * @throws org.osgi.framework.BundleException
	 * @see org.osgi.framework.Bundle#uninstall()
	 */
	public void uninstall() throws BundleException {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getHeaders()
	 */
	public Dictionary getHeaders() {
		// TODO Auto-generated method stub
		return new Hashtable();
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getBundleId()
	 */
	public long getBundleId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getLocation()
	 */
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getRegisteredServices()
	 */
	public ServiceReference[] getRegisteredServices() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getServicesInUse()
	 */
	public ServiceReference[] getServicesInUse() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param permission
	 * @return
	 * @see org.osgi.framework.Bundle#hasPermission(java.lang.Object)
	 */
	public boolean hasPermission(Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param name
	 * @return
	 * @see org.osgi.framework.Bundle#getResource(java.lang.String)
	 */
	public URL getResource(String name) {
		return getClass().getResource(name);
	}

	/**
	 * @param localeString
	 * @return
	 * @see org.osgi.framework.Bundle#getHeaders(java.lang.String)
	 */
	public Dictionary getHeaders(String localeString) {
		// TODO Auto-generated method stub
		return new Hashtable();
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getSymbolicName()
	 */
	public String getSymbolicName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param name
	 * @return
	 * @throws java.lang.ClassNotFoundException
	 * @see org.osgi.framework.Bundle#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param name
	 * @return
	 * @see org.osgi.framework.Bundle#getResources(java.lang.String)
	 */
	public Enumeration getResources(String name) {
		try {
			return getClass().getClassLoader().getResources(name);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Vector().elements();
	}

	/**
	 * @param path
	 * @return
	 * @see org.osgi.framework.Bundle#getEntryPaths(java.lang.String)
	 */
	public Enumeration getEntryPaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param name
	 * @return
	 * @see org.osgi.framework.Bundle#getEntry(java.lang.String)
	 */
	public URL getEntry(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see org.osgi.framework.Bundle#getLastModified()
	 */
	public long getLastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

}
