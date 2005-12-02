package org.osgi.tools.fileinstall;

import org.osgi.framework.*;
import java.io.*;
import java.util.*;

/**
    This clever little bundle watches a directory and will
    install any jar file if finds in that directory.

    We maintain a hashtable of files we found in the load directory
    where the key is the file name and the value is the date. When
    we see a change from the directory by polling it every [poll]
    seconds we update the framework.
*/


class Track {
    long            cachedModified;
    long            lastModified;
    long            lastAlive;
    long            signature;
    Init            init;
    File            file;
    Bundle          bundle;
    BundleContext   context;
    
    Track( Init init, BundleContext context, File file ) {
        this.init = init;
        this.context = context;
        this.file = file;
        cachedModified = file.lastModified();
    }
    
    /**
     * When the file is detected in the watched directory.
     */
    void phase1() { 
        lastAlive = System.currentTimeMillis();

        if ( cachedModified != file.lastModified() ) {
            cachedModified = file.lastModified();
            lastModified = lastAlive;
            System.out.println( this + " was modified right now" );
            return;
        }

        if ( System.currentTimeMillis() - lastModified < init.delay ) {
            System.out.println( this + "was modified recently " + (System.currentTimeMillis() - lastModified) );
            return;
        }
        
        try {
            if ( bundle == null ) {
                System.out.println( this + " Installing bundle");
                bundle = context.installBundle( "file:" + file.getAbsolutePath() );
                signature = file.lastModified();
            }
            else if ( signature != file.lastModified() ) {
                System.out.println( this + " Updating bundle");
                bundle.update( );
                signature = file.lastModified();
            }

            if ( bundle.getState() != Bundle.ACTIVE ) {
                System.out.println( this + " Starting bundle" );
                bundle.start();
            }
        }
        catch( BundleException e ) {
            System.out.println( "Could not install " + this + " cause : " + e + " nested "+ e.getNestedException() );
            Throwable ee = e.getNestedException();
            if ( ee != null )
                ee.printStackTrace();
        }
    }
    
    /**
     * When all files are processed in the directory.
     */
    boolean phase2(long pollStart) {
        // Check if file is absent for delay time
        if ( pollStart - lastAlive > init.delay ) 
        {
            System.out.println( this + " is no longer alive " + (System.currentTimeMillis() - lastAlive) );
            // File exists for at least delay ms
            if ( bundle != null ) 
            try {
                System.out.println( this + " uninstalling " );
                bundle.uninstall();
                bundle = null;
            }
            catch( BundleException e ) {
                System.out.println( "Could not install " + file + " cause : " + e + " nested "+ e.getNestedException() );
                Throwable ee = e.getNestedException();
                if ( ee != null )
                    ee.printStackTrace();
            }
            else
                System.out.println( this + " not present " );
            return false;
        }
        return true;
    }
    
    public String toString() {
        return file.getName();
    }
}

    
public class Init 
    implements 
        BundleActivator,
        Runnable
{
    BundleContext           context;
    Hashtable               bundles = new Hashtable();
    File                    jardir;
    Thread                  thread;
    boolean                 cont = true;
    long                    poll = 2000;
    long                    delay = 2500;
    long                    debug;
    
    public final static String  POLL        = "org.osgi.fileinstall.poll";
    public final static String  DIR         = "org.osgi.fileinstall.dir";
    public final static String  DELAY       = "org.osgi.fileinstall.delay";
    public final static String  DEBUG       = "org.osgi.fileinstall.debug";
    
    public void start( BundleContext context ) throws Exception {
        this.context = context;

        poll   = getTime( POLL, poll );
        delay  = getTime( DELAY,   delay );
        debug  = getTime( DEBUG,   -1 );
        
        String dir = System.getProperty( DIR );
        if ( dir == null )
            dir = "./load";
            
        jardir = new File( dir  );
        if ( ! jardir.exists() )
            throw new RuntimeException( "No directory name " + dir + " in property " + DIR );
            
        thread = new Thread( this );
        thread.start();
    }


    long getTime( String property, long dflt ) {
        String value = System.getProperty( property );
        if ( value != null )
        try {
                return Long.parseLong( value );
        }
        catch( Exception e ) {
            System.out.println( property + " set, but not a long: " + value );
        }
        return dflt;
    }       



    public void run() {
        System.out.println( "FILEINSTALL | OSGi (c) 2001 v1.0" );
        System.out.println( POLL + "  (ms)   " + poll );
        System.out.println( DELAY+  " (ms)   " + delay );
        System.out.println( DIR  +"            " + jardir.getAbsolutePath() );
        System.out.println( DEBUG+  "          " + debug );
        Hashtable       bundles = new Hashtable();
        
        while( cont )
        try {
            long            now         = System.currentTimeMillis();
            
            String list[]           = jardir.list();
            if ( list != null ) {
				long pollStart = System.currentTimeMillis();
				
                for ( int i=0; i<list.length; i++)
                try
                {
                    if ( list[i].toLowerCase().endsWith( ".jar" ) )
                    {
                        Track t = (Track) bundles.get( list[i] );
                        if ( t == null ) {
                            t = new Track(this,context,new File( jardir, list[i] ));
                            bundles.put( list[i], t );
                        }
                        t.phase1();
                    }
                    else
                        if ( debug > 0 )
                            System.out.println( "Not a jar file: " + list[i] );
                }
                catch( Throwable e ) {
                    System.out.println( "Could not install " + list[i] + " cause : " + e );
                }
                
                Hashtable   newBundles = new Hashtable();
                
                for ( Enumeration e= bundles.keys(); e.hasMoreElements(); )
                try {
                    String path = (String) e.nextElement();
                    Track track = (Track) bundles.get( path );
                    if ( track.phase2(pollStart)) {
                        newBundles.put( path, track );
                    }
                        
                }
                catch( Throwable eee ) {
                    eee.printStackTrace();
                }
                bundles = newBundles;
            }
            Thread.currentThread().sleep( poll );
            
        }
        catch( Throwable e ) {
            e.printStackTrace();
        }
    }

    public void stop( BundleContext  context ) throws Exception {
        System.out.println("INIT exit" );
        cont = false;
        thread.interrupt();
    }
}


