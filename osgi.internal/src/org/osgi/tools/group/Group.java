package org.osgi.tools.group;

import org.osgi.framework.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
This bundle will load all embedded JAR files that are listed
in the Group-Load manifest header. JAR file path names should
be separated with a comma (,).
 */
	
public class Group
	implements
		BundleActivator
{
	Vector		bundles = new Vector();
	
	public void start( final BundleContext context ) throws Exception {
		String		list = (String) context.getBundle().getHeaders().get( "Group-Load" );
		try {
			if ( list == null )
				throw new IOException( "No Manifest header: Group-Load " + context.getBundle().getHeaders() );		
				
			StringTokenizer st = new StringTokenizer( list, ", " );
			if ( ! st.hasMoreTokens() )
				System.err.println( "No files specified  in Group-Load manifest tag: " + list );
				
			while ( st.hasMoreTokens() ) {
				String path = st.nextToken();
				URL		url = getClass().getResource( "/" + path );
                String location = "";

				if ( url == null )
					System.err.println( "No url for " + path);
				else
				try {
					InputStream in = url.openStream();
					File stored = context.getDataFile(  path );
					stored.getParentFile().mkdirs(); // make directory for file
					OutputStream out = new FileOutputStream( stored );
					copy( in, out );
					in.close();
					out.close();

					location = stored.toURL().toExternalForm();
					if ( System.getProperty( "org.osgi.tools.group.info" )!=null )
						System.err.println( "Installing " + url + " from "  + location );
					
					Bundle b = context.installBundle( location );
					bundles.addElement( b );
				}
				catch( IOException e ) {
					System.err.println( "No inputstream for " + url );
					e.printStackTrace();
				}
				catch( BundleException ee ) {
					System.out.println( "Cannot install " + location + " because " + ee);
				    Throwable t = ee.getNestedException();
				    
					System.out.println( "Cannot start " + location + " because " + ee);
					
					if ( t != null ) {
					    System.err.println("Nested ");
					    t.printStackTrace();
					}
				}
			}
			for ( Enumeration e = bundles.elements(); e.hasMoreElements(); ) {
				Bundle b = (Bundle) e.nextElement();
				try {
					if ( System.getProperty( "org.osgi.tools.group.info" )!=null )
						System.err.println( "Starting " + b.getLocation()  );					
					b.start();
					if ( System.getProperty( "org.osgi.tools.group.info" )!=null )
						System.err.println( "Started " + b.getLocation()  );					
				}
				catch( BundleException ee ) {
				    Throwable t = ee.getNestedException();
				    
					System.out.println( "Cannot start " + b.getLocation() + " because " + ee);
					
					if ( t != null ) {
					    System.err.println("Nested ");
					    t.printStackTrace();
					}
				}
			}
		}
		catch( Throwable t ) {
			System.err.println( "Cannot install bundle " + t );
			t.printStackTrace();
		}
	}

	public void stop( BundleContext  context ) throws Exception {
		for ( Enumeration e = bundles.elements(); e.hasMoreElements(); ) {
			Bundle b = (Bundle) e.nextElement();
            try {
                b.uninstall();
            }
            catch( BundleException ee ) {
                System.out.println( "Cannot uninstall " + b.getLocation() + " because " + ee);
            }
		}
	}
	
	
	void copy( InputStream in, OutputStream out ) throws IOException  {
		byte buffer[] = new byte[ 1024];
		int size = in.read( buffer );
		while ( size > 0 ) {
			out.write( buffer, 0, size );
			size = in.read( buffer );
		}
	}
}


