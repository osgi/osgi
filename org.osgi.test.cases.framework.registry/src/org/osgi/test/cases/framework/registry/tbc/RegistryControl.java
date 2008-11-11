/*
 * $Id$
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

package org.osgi.test.cases.framework.registry.tbc;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class RegistryControl 
	extends DefaultTestBundleControl 
    implements ServiceListener {

    BundleContext       _other;
    ServiceReference    _otherRef;
    
    Dictionary          _properties = new Hashtable();
    boolean             _ignore;
    Bundle              tb2;
    
    static String [] classes = new String[] {
            Marker1.class.getName(),
            Marker2.class.getName(),
        };

    static String[] methods = new String[] {
    	"prepare",
        "testBasicRegistration",
        "testBasicFactory",
        "testFactoryException",
        "testWrongClass",
        "testManyRegistrations",
        "testNullService",
        "testEventSpan"
    };

    public String[] getMethods() {
        return methods;
    }
    
    public void setUp() throws Exception {
    	tb2 = installBundle( "tb2.jar" );
        tb2.start();
        _otherRef   = getContext().getServiceReference( BundleContext.class.getName() );
        _other      = (BundleContext) getContext().getService( _otherRef );
        _properties.put( "test", "yes" );
    
        getContext().addServiceListener( this, "(test=*)" );
    }

    static boolean visited;
    
    public void testEventSpan() throws Exception {
        log( "Check for synchronous service events" );
        ServiceListener sl = new ServiceListener() {
            public void serviceChanged( ServiceEvent e ) {
                try {
                    Thread.sleep(2000);
                    visited = true;
                }
                catch( InterruptedException ie ) {}
            }
        };
        
        getContext().addServiceListener(sl);
        visited = false;
        ServiceRegistration registration = getContext().registerService( classes, new ConcreteMarker(30), _properties  );
        log( "Visited should now be true after registration " + visited );
        
        visited = false;
        registration.setProperties( _properties );
        log( "Visited should now be true after setProperties " + visited );
        
        visited = false;
        registration.unregister();
        log( "Visited should now be true after unregistration " + visited );
        
        getContext().removeServiceListener(sl);
    }
    

    public void testBasicRegistration() throws Exception {
        ServiceRegistration registration = getContext().registerService( classes, new ConcreteMarker(1), _properties  );
        registration.setProperties(_properties);
        registration.unregister();
    }
    
    public void testBasicFactory() throws IOException
    {
        ServiceRegistration registration = getContext().registerService( classes, new ServiceFactory() {
            int counter=2;
            
            public Object getService( Bundle bundle, ServiceRegistration registration ) {
                log( "Getting service from factory " + counter );
                return new ConcreteMarker(counter++);
            }
            public void ungetService( Bundle bundle, ServiceRegistration registration, Object service ) {
                log( "Ungetting correct object = " + service );
            }
        }, _properties );
        
        log( "Getting reference via Marker1" );
        ServiceReference    reference = getContext().getServiceReference( Marker1.class.getName() );
        log( "Getting service Marker1" );
        Object service   = getContext().getService( reference );
        log( "Checking factory result : " + print( reference ) + " Service=" + service );
        log( "Ungetting service Marker1" );
        getContext().ungetService( reference );
        
        log( "Getting reference via Marker2" );
        reference = getContext().getServiceReference( Marker2.class.getName() );
        log( "Getting service Marker2" );
        service   = getContext().getService( reference );
        log( "Checking factory result with Marker2 : " + print( reference ) + " Service=" + service );
        log( "Ungetting service Marker2" );
        getContext().ungetService( reference );
        

        log( "Getting reference via Marker2 via other bundle" );
        ServiceReference otherRef = _other.getServiceReference( Marker2.class.getName() );
        log( "Getting service Marker2 via other bundle" );
        service = _other.getService( otherRef );
        log( "Check for get via other bundle's context " + service );
        log( "Ungetting service Marker2 of other bundle" );
        _other.ungetService( reference );
        
        log( "Multiple gets" );
        log( "Checking factory service, getting it once " + getContext().getService( reference ) );
        log( "Checking factory service, getting it twice " + getContext().getService( reference ) );
        log( "Checking factory service, getting it thrice " + getContext().getService( reference ) );
        log( "Ungetting it once too often" );
        for ( int i=0; i<4; i++ )
            getContext().ungetService( reference );

        log( "Check for unget via other bundle's context " );
        _other.ungetService( otherRef );

        log( "Unregistering");
        registration.unregister();
        
        log( "Unregister once more (invalid)" );
        try {
            registration.unregister();
            log( "! Invalid, should have thrown IllegalStateException" );
        }
        catch( IllegalStateException e ) {
            log( "OK Correctly thrown IllegalStateException" );
        }
    }

    
    public void testFactoryException() throws IOException
    {
        log( "Factory exception" );
        
        ServiceRegistration registration = getContext().registerService( classes, new ServiceFactory() {
            int counter=10;
            
            public Object getService( Bundle bundle, ServiceRegistration registration ) {
                log( "Throwing exception in getService()" );
                throw new RuntimeException("getService" );
            }
            public void ungetService( Bundle bundle, ServiceRegistration registration, Object service ) {
                log( "Throwing exception in ungetService()" );
                throw new RuntimeException("ungetService" );
            }
        }, _properties );
        
        ServiceReference    reference = getContext().getServiceReference( Marker1.class.getName() );
        log( "Checking factory result with Marker1 : " + print( reference ) );
        
        registration.unregister();      
    }

    public void testWrongClass() throws IOException
    {
        log( "Wrong class registration" );
        try {
            ServiceRegistration registration = getContext().registerService( classes, "wrong class", _properties  );
            log( "! Invalid, registration due to wrong classes" );
        }
        catch( IllegalArgumentException e )
        {
            log( "OK thrown IllegalArgumentException" );
        }
    }
    
    public void testNullService() throws IOException {
        log( "Null service registration" );
        try {
            ServiceRegistration registration = getContext().registerService( classes, "wrong class", _properties  );
            log( "! Invalid, registration due to null service" );
        }
        catch( IllegalArgumentException e )
        {
            log( "OK thrown IllegalArgumentException due to null service" );
        }
    }
    
    
    
    public void testManyRegistrations() throws Exception {
        log( "Registering 1000 services" );
        ServiceRegistration[] registrations = new ServiceRegistration[ 1000 ];
        for ( int i=0; i<1000; i++)
            registrations[i] = getContext().registerService( classes, new ConcreteMarker(i+1000), null );
        
        ServiceReference    reference[] = getContext().getServiceReferences( Marker1.class.getName(), null );
        log( "Should have 1000 references : " + reference.length );
        
        for ( int i=0; i<500; i++ )
            registrations[i].unregister();
        
        reference = getContext().getServiceReferences( null, "(objectClass=" + Marker1.class.getName() + ")" );
        log( "Should have 500 references (via filter): " + reference.length );
        reference = getContext().getServiceReferences( Marker1.class.getName(), null );
        log( "Should have 500 references (via class): " + reference.length );
        
        for ( int i=0; i<500; i++ )
            registrations[i+500].unregister();
        
        reference = getContext().getServiceReferences( Marker1.class.getName(), null );
        log( "Should have 0 references (=null) : " + reference );
    }
    
    
    
    public void unprepare() throws Exception
    {
        log( "Cleaning up after bundle dies" );              
        ServiceReference list[] = getContext().getServiceReferences( Marker1.class.getName(), null );
        log( "Should have no registrations registered (null) " + list );
        
        ServiceRegistration factory = _other.registerService( String.class.getName(), new ServiceFactory() {
            int counter=60;
            
            public Object getService( Bundle bundle, ServiceRegistration registration ) {
                log( "Getting service from factory " + counter );
                return "Counter="+ counter++;
            }
            public void ungetService( Bundle bundle, ServiceRegistration registration, Object service ) {
                log( "Ungetting object" );
            }
        }, _properties );
        
        ServiceReference    otherReference  = _other.getServiceReference( String.class.getName() );
        ServiceReference    reference       = getContext().getServiceReference( String.class.getName() );
        Object otherService                 = _other.getService( otherReference );
        Object service                      = getContext().getService( reference );
        
        log( "Stopping dummy bundle, should unget 1 service, and unregister 1" );
        _other.getBundle().stop();
        
        log( "Trying to get a service again, first via our BundleContext" );
        service                             = getContext().getService( reference );
        log( "Should get null service : " + service );
        
        log( "Trying to get a service again, now via other BundleContext" );
        try {
            otherService                        = _other.getService( otherReference );
            log( "! Should have gotten exception" );
        }
        catch( IllegalStateException e ) {
            log( "OK Correct illegal state exception because bundlecontext is invalid" );
        }
        
        
        list = getContext().getServiceReferences( Marker1.class.getName(), null );
        log( "Should have no registrations left (null) " + list );
        
    }   
    
    
    public void serviceChanged( ServiceEvent event )
    {
        if ( _ignore )
            return;
        
        if ( event.getServiceReference().getProperty( "ignore" ) != null )
            return;
            
        try
        {
            ServiceReference reference = event.getServiceReference();
            
            switch( event.getType() )
            {
            case ServiceEvent.REGISTERED:       log( "REGISTERED     " +  print(reference) ); break;
            case ServiceEvent.UNREGISTERING:    log( "UNREGISTERING  " +  print(reference) ); break;
            case ServiceEvent.MODIFIED:         log( "MODIFIED       " +  print(reference) ); break;
            default:                            log( "UNKNOWN EVENT  " + event.getType() + " "  + print(reference)); break;
            }
        }
        catch( Exception e )
        {
            log( "Exception in service event listener: " + e );
        }
    }
    
    
    
    String print( ServiceReference reference ) throws IOException
    {
        StringBuffer    sb = new StringBuffer();
        boolean mine = getContext().getBundle().equals( reference.getBundle() );
        
        Object classes = reference.getProperty( "objectClass" );
        if ( classes instanceof String )
            sb.append( classes );
        else if ( classes instanceof String[] )
        {
            String [] array = (String[]) classes;
            String del = " ";
            for ( int i=0; i<array.length; i++)
            {
                sb.append( del );
                sb.append( array[i] );
                del = ", ";
            }
        }

        sb.append( " " );
        
        if ( mine )
            sb.append( " OWNER" );
            
        return sb.toString();
    }
    
}

interface Marker1 {}
interface Marker2 {}
class ConcreteMarker implements Marker1, Marker2 {
    int _n;
    ConcreteMarker( int n ) { _n = n; }
    public String toString() { return "MARKER " + _n; }
}

