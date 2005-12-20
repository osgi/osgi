package com.nokia.test.unittest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.DAConstants;

public class StubBundle implements org.osgi.framework.Bundle {

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void start() throws BundleException {
		// TODO Auto-generated method stub
		
	}

	public void stop() throws BundleException {
		// TODO Auto-generated method stub
		
	}

	public void update() throws BundleException {
		// TODO Auto-generated method stub
		
	}

	public void update(InputStream in) throws BundleException {
		// TODO Auto-generated method stub
		
	}

	public void uninstall() throws BundleException {
		// TODO Auto-generated method stub
		
	}

	public Dictionary getHeaders() {
		Dictionary ret = new Hashtable();
		ret.put(DAConstants.BUNDLE_SYMBOLIC_NAME, "StubBundle");
		ret.put(DAConstants.BUNDLE_VERSION, "1.2.3");
		return ret;
	}

	public long getBundleId() {
		return 1;
	}

	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceReference[] getRegisteredServices() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceReference[] getServicesInUse() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasPermission(Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

	public URL getResource(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Dictionary getHeaders(String locale) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSymbolicName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getEntryPaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getEntry(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Enumeration findEntries(String path, String filePattern, boolean recurse) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		byte[] ba = "Bundle-Version: ".getBytes();
		for (int i = 0; i < ba.length; i++) {
			System.out.print(ba[i] + ", ");
		}
	}

}
