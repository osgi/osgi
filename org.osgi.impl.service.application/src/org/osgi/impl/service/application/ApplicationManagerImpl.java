package org.osgi.impl.service.application;

import java.io.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;

/* TODO: catch the uninstall signal if and unlock the application */

public class ApplicationManagerImpl implements ApplicationManager
{
    private BundleContext bc;
    private Vector lockedApplications;
    
    public ApplicationManagerImpl( BundleContext bc ) throws Exception
    {      
      this.bc = bc;
      lockedApplications = new Vector();
      loadLockedApplications();
    }
    
    public ApplicationDescriptor[] getAppDescriptors()
    {
      try
      {
        ServiceReference []references = bc.getServiceReferences( "org.osgi.service.application.ApplicationDescriptor", null );
        if( references == null || references.length == 0 )
          return null;
      
        ApplicationDescriptor appDescs[] = new ApplicationDescriptor[ references.length ];
        for( int i=0; i != references.length; i++ )
        {
          appDescs[i] = (ApplicationDescriptor)bc.getService( references [ i ] );
          bc.ungetService( references [ i ] );
        }
      
        return appDescs;
      }catch( Exception e )
      {
        return null;
      }
    }
    
    public ApplicationDescriptor getAppDescriptor(String appUID) throws Exception
    {
      ApplicationDescriptor appDescs[] = getAppDescriptors();
      if( appDescs == null )
        throw new Exception( "Application ID not found!" );
      
      for( int i=0; i != appDescs.length; i++ )
        if( appDescs[i].getUniqueID().equals( appUID ) )
          return appDescs[ i ];
      
      throw new Exception( "Application ID not found!" );
    }
        
    public ApplicationHandle launchApplication(ApplicationDescriptor appDescriptor, Map args) throws SingletonException, Exception
    {
      if( isLocked( appDescriptor ) )
        throw new Exception( "Application is locked, can't launch!" );
        
      Map props = appDescriptor.getProperties( "en" );
      Object isSingleton = props.get( "singleton" );
      if( isSingleton != null && isSingleton.equals( "true" ) )
      {
        ServiceReference []appHandles = bc.getServiceReferences( "org.osgi.service.application.ApplicationHandle", null );
        if( appHandles != null )
          for( int k=0; k != appHandles.length; k++ )
          {
            ApplicationHandle handle = (ApplicationHandle)bc.getService( appHandles[ k ] );
            ApplicationDescriptor appDesc = handle.getAppDescriptor();
            bc.ungetService( appHandles[ k ] );
            if( appDesc == appDescriptor )
              throw new Exception( "Singleton Exception!" );
          }
      }
      
      ServiceReference []references = bc.getServiceReferences( "org.osgi.service.application.ApplicationContainer", 
                                                               "(application_type=" + appDescriptor.getContainerID() + ")" );
      if( references == null || references.length == 0 )
        throw new Exception( "Container " + appDescriptor.getContainerID() + " not found!" );
        
      ApplicationContainer container = (ApplicationContainer)bc.getService( references[ 0 ] );
      if( container == null )
        throw new Exception( "Container " + appDescriptor.getContainerID() + " not found!" );            
      ApplicationHandleImpl appHandle = new ApplicationHandleImpl( appDescriptor, bc );
      ApplicationContext context = new ApplicationContextImpl( args );
      
      Application application = container.createApplication( context, appHandle );
      if( application == null )
        throw new Exception( "Invalid application object received!" );
        
      bc.ungetService( references[ 0 ] );

      appHandle.setApplication( application );
      application.startApplication();
      appHandle.applicationStarted();
      appHandle.registerAppHandle();
         
      return (ApplicationHandle)appHandle;
    }
    
    public ScheduledApplication addScheduledApplication(ApplicationDescriptor appDescriptor, Map arguments, Date date)
    {
      /* TODO TODO TODO TODO TODO */
      return null;
    }
        
    public void removeScheduledApplication(ScheduledApplication scheduledApplication) throws Exception
    {
      /* TODO TODO TODO TODO TODO */
      return;
    }
  
    public ScheduledApplication[] getScheduledApplications() throws IOException
    {
      /* TODO TODO TODO TODO TODO */
      return null;
    }
    
    public void lock(ApplicationDescriptor appDescriptor) throws Exception
    {
      String appUID = appDescriptor.getUniqueID();
      
      if( !lockedApplications.contains( appUID ) )
      {
        lockedApplications.add( appUID );
        saveLockedApplications();
      }
    }
    
    public void unLock(ApplicationDescriptor appDescriptor) throws Exception
    {
      String appUID = appDescriptor.getUniqueID();
      
      if( lockedApplications.contains( appUID ) )
      {
        lockedApplications.remove( appUID );
        saveLockedApplications();
      }
    }

    public boolean isLocked(ApplicationDescriptor appDescriptor) throws Exception
    {      
      return lockedApplications.contains( appDescriptor.getUniqueID() );
    }

    public String[] getSupportedMimeTypes()
    {
      /* TODO TODO TODO TODO TODO */
      return null;
    }       
    
    private void loadLockedApplications() throws Exception
    {
      File lockedApps = bc.getDataFile( "LockedApplications" );
      if( lockedApps.exists() )
      {
        FileInputStream stream = new FileInputStream( lockedApps );
    
        int length;
      
        while( ( length = stream.read() ) != -1 )
        {
          byte []b = new byte [ length ];
        
          int received = 0;
          while( received != length )
          { 
            int len = stream.read( b, received, length-received );
            received += len;
            if( len == -1 )
              break;
          }
        
          lockedApplications.add( new String( b ) );
        }
      
        stream.close();
      }
    }
    
    private void saveLockedApplications() throws Exception
    {
      File lockedApps = bc.getDataFile( "LockedApplications" );
      FileOutputStream stream = new FileOutputStream( lockedApps );
    
      for( int i=0; i != lockedApplications.size(); i++ )
      {
        String appUID = (String)lockedApplications.get( i );
        stream.write( appUID.length() );
        stream.write( appUID.getBytes() );
      }
      
      stream.close();
    }
}
