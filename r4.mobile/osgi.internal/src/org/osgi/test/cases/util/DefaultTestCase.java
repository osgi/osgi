/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.util;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.service.*;
import javax.servlet.http.*;
import org.osgi.service.http.*;

/**
A TestCase testing various classpath issues.

@author Ericsson Radio Systems AB
 */

public class DefaultTestCase
	implements
		TestCase,
		HttpContext,
		BundleActivator
{
	BundleContext   		context;
	HttpService 			http;
	boolean 				cont;
	TestBundle  			testbundle;
	TestRun 				run;
	int 					errors;
	long					timeout;
    String                  name;
    String                  desc;
	
	/**
	Creates and registers the TestCase object. Also makes the jar files in
	the bundle available for http access.
	 */
	public void start(BundleContext context) {
		this.context = context; 	

        /* There is a risk that getName() and getDescrption() are called
		after the bundle is stopped and in that case they will fail,
		so the name and desc are cached when the TestCase is started */
        cacheName();
        cacheDescription();

		context.registerService(TestCase.class.getName(), this, null);
	}

	/**
	Deletes and unregisters the TestCase object. Also unregisters the http
	resources.
	 */
	public void stop(BundleContext bc) {}
	public void abort() { cont = false; }
	public String getIconName() { return null; }

	/**
	Runs the TestCase by installing a TestBundleControl bundle on the
	target framework. This bundle will perform the various tests and
	log the results.
	
	@see org.osgi.test.cases.framework.classpath.tbc.TestBundleControl
	 */
	public int test(TestRun run) {
		//System.out.println("Running test case " + getName());
		this.run = run;
		errors = 0;
		cont = true;
		timeout = Integer.parseInt( System.getProperty( "org.osgi.test.testcase.timeout", "60000" ));
		
		try {
			init();
			registerHttpResources();
			testbundle = createTestBundle();

			sendStart();
		
			while(cont) {
				String progress = (String)testbundle.receive(timeout);

				// The remote bundle will send number strings to report
				// progress and the string "ready" when it's done.
				//
				if (progress == null) {
					run.reportMessage("Receive from TestBundleControl timed out or canceled.");
					errors++;
					cont = false;
					progress = "timeout";
				}

				/* If something in the preparations didn't go well (like
				a service missing), indicate that the test case didn't
				run. */
				if (progress.equals("norun")) {
				    run.reportMessage("Some service was missing");
				    errors = -1;
				    cont = false;
                }

				if (progress.equals("ready")) {
				    cont = false;
				}
					
                if(cont) {
    				try {
    					int percent = Integer.parseInt(progress);
    					run.reportProgress(percent);
    				}
    				catch( Exception e ) {
    					run.reportMessage( "Error at " + progress + "%, " + e );
    				}
    			}
			}
			if(errors >= 0) {
			    errors += testbundle.getCompareErrors();
			}
			testbundle.uninstall();
		}
		catch (Throwable e) {
			try { run.reportMessage("Unexpected exception in running testcase: " + e ); } catch( IOException ioe ) {}
			e.printStackTrace();
			errors++;
		}
		finally {
			run = null;
			testbundle = null;
			if ( http!=null) {
				http.unregister("/" + getName() );
			}
			try { deinit(); } catch(Exception e) {}
		}

		return errors;
	}

	protected void init() throws Exception {}
	protected void deinit()  throws Exception {}
	
	protected int guessHttpPort() {
		String port = context.getProperty("org.osgi.service.http.port");
		if ( port == null )
		   port = System.getProperty( "org.osgi.service.http.port" );
		if (port != null) {
			return Integer.parseInt(port);
		}
		
		ServerSocket socket;
		int			 attempts[] = new int[] { 80, 8080, 8081, 8082, 8083, 8000,9000};
		
		for ( int i=0; i<attempts.length; i++ )
		try {
			socket = new ServerSocket(attempts[i]);
			socket.close();
			socket = null;
		}
		catch (IOException ioe) {
			return attempts[i];
		}
		return 0;
	}

	void registerHttpResources() throws IOException, NamespaceException {
		ServiceReference httpRef = context.getServiceReference( HttpService.class.getName() );
		if ( httpRef == null )
			throw new IOException( "No HTTP server found in registry" );
		
		http = (HttpService) context.getService(httpRef);
		if ( http == null )
			throw new IOException( "Could not obtain HTTP Service from reference" );
		
		http.registerResources("/" + getName(), "/www", this );
	}
	
	
	protected TestBundle createTestBundle() throws IOException {
		String name = getName() + "_TBC";
		String tbc="", log="";
		InputStream jar = getClass().getResourceAsStream(tbc=get( "Test-TBC", "/tbc.jar" ));
		InputStream ref = getClass().getResourceAsStream(log=get( "Test-Log", "/log.ref" ));
		if ( jar == null )
			System.out.println( "No such jar file " + tbc );
			
		TestBundle testbundle = getRun().createTestBundle( name, jar, ref );
		if ( testbundle == null )
			throw new IOException( "Cannot createTestBundle " + name );
			
		return testbundle;
	}
	
	String get( String name, String deflt ) {	
		String result = (String) context.getBundle().getHeaders().get(name);
		if ( result != null )
			return result;
		else
			return deflt; 
	}
	
	protected TestRun getRun() {
		return run;
	}
	

	protected void sendStart() throws IOException {
		String host = System.getProperty( "org.osgi.service.http.host" );
		byte address[] = null;
		if ( host == null )
			address = InetAddress.getLocalHost().getAddress();
		else
			address = InetAddress.getByName( host ).getAddress();
			
		StringBuffer sb = new StringBuffer();
		String del="";
		sb.append( "http://" );
		for ( int i=0; i<address.length; i++ ) {
			sb.append( del );
			sb.append( 0xFF & address[i] );
			del = ".";
		}
		int port = guessHttpPort();
		if ( port != 80 )  {
			sb.append( ":" );
			sb.append( port );
		}
		sb.append( "/" );
		sb.append( getName() );
		sb.append( "/" );
		
		//System.out.println( "HTTP server is: " + sb.toString() );
		testbundle.send( sb.toString() );
	}

	public boolean handleSecurity(HttpServletRequest request,
								  HttpServletResponse response) throws IOException {
		return true;
	}
	
	public URL getResource(String name) {
		URL url = null;
		try {
			url = getClass().getResource(name);
			if ( url == null )
				url = getClass().getResource("/"+name);
			
			if ( url == null && name.startsWith("/www") ) {
				name = name.substring(4);
				url = getClass().getResource(name);
			}
			return url;
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMimeType(String name) {
		return null;
	}
	
    public String getDescription() { 
        return desc;
    }
    
    public String getName() { 
        return name;
    }
    
    private void cacheName() {
        Dictionary headers = context.getBundle().getHeaders();
        name = (String) headers.get(Constants.BUNDLE_NAME);
        String prefix = "org.osgi.test.cases.";
        
        /* Remove the package name */
        if(name.startsWith(prefix)) {
            name = name.substring(prefix.length());
        }
    }
    
    private void cacheDescription() {
        Dictionary headers = context.getBundle().getHeaders();
        desc = (String) headers.get(Constants.BUNDLE_DESCRIPTION);
    }
    
	
	protected BundleContext getBundleContext() { return context; }
}
