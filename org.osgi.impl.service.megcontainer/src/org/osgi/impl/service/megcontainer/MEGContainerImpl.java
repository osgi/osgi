package org.osgi.impl.service.megcontainer;

import org.osgi.service.application.*;
import org.osgi.service.event.*;
import org.osgi.service.packageadmin.*;
import org.osgi.framework.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.lang.reflect.*;
import nanoxml.*;

class EventSubscribe
{
  public final static int        START = 1;
  public final static int        STOP = 2;  
  public final static int        SUSPEND = 3;
  public final static int        RESUME = 4;
  public final static int        LISTENER = 5;

  public String []               eventTopic;
  public int []                  eventAction;
}

class Dependencies
{
  public String []               requiredServices;
  public String []               requiredPackages;
}

class MEGBundleDescriptor
{
  public ApplicationDescriptor[] applications;
  public ServiceRegistration[]   serviceRegistrations;
  public EventSubscribe[]        eventSubscribes;
  public Dependencies[]          dependencies;
  public long                    bundleID;
}

public class MEGContainerImpl implements MEGContainer, BundleListener, ChannelListener
{
  private BundleContext bc;
  private String containerID;
  private Vector bundleIDs;
  private Vector uninstalledAppUIDs;
  private Hashtable bundleHash;
  private int height;
  private int width;
  
  public MEGContainerImpl(BundleContext bc, String cntID ) throws Exception 
  {
    this.bc = bc;
    this.containerID = cntID;
    
    bundleHash = new Hashtable();
    
    bundleIDs = loadVector( "BundleIDs" );
    uninstalledAppUIDs = loadVector( "UninstalledAppUIDs" );
    
    for( int i=0; i != bundleIDs.size(); i++ )
      registerBundle( Long.parseLong( (String)bundleIDs.get( i ) ), false );
      
    bc.addBundleListener( this );              
  }

  public ApplicationDescriptor[] installApplication( InputStream inputStream) throws IOException, Exception
  {
    throw new Exception( "Installing from stream is not allowed for MEG applications!" );
  }

  public ApplicationDescriptor[] installApplication( long bundleID ) throws IOException, Exception
  {    
    ApplicationDescriptor []appDescs = registerBundle( bundleID, true );
    if( !bundleIDs.contains( Long.toString( bundleID ) ) )
    {
      bundleIDs.add( Long.toString( bundleID ) );
      saveVector( bundleIDs, "BundleIDs" );
    }
    
    if( appDescs == null )
      throw new Exception( "Not a valid MEG bundle!" );
      
    for( int i=0; i!= appDescs.length; i++ )
    {
      if( appDescs[ i ].getProperties( "en" ).get( "autostart" ).equals( "true" ) )
      {
        ServiceReference appManReference = bc.getServiceReference( "org.osgi.service.application.ApplicationManager" );
        if( appManReference == null )
          throw new Exception( "AppManager service is not running!" );
          
        ApplicationManager appMan = (ApplicationManager)bc.getService( appManReference );
        appMan.launchApplication( appDescs[ i ], null );
        bc.ungetService( appManReference );
      }
    }    
    return appDescs;
  }

  public ApplicationDescriptor[] uninstallApplication(ApplicationDescriptor appDescriptor, boolean force) throws IOException, Exception
  {
    uninstalledAppUIDs.add( appDescriptor.getUniqueID() );
    saveVector( uninstalledAppUIDs, "UninstalledAppUIDs" );
    long bundleId = Long.parseLong( (String)(appDescriptor.getProperties( "" ).get( "bundle_id" ) ) );
    registerApplicationDescriptors( bundleId );
    return null;
  }

  public ApplicationDescriptor[] upgradeApplication(ApplicationDescriptor appDesc, InputStream inputStream, boolean force) throws IOException, Exception
  {
    throw new Exception( "Upgrading from stream is not allowed for MEG applications!" );
  }

  public Application createApplication(ApplicationContext appContext, ApplicationHandle appHandle) throws Exception
  {
    ApplicationDescriptor appDesc = appHandle.getAppDescriptor();
    if( uninstalledAppUIDs.contains( appDesc.getUniqueID() ) )
      throw new Exception ( "Can't launch unistalled application!" );

    long bundleID = Long.parseLong( (String)(appDesc.getProperties( "" ).get( "bundle_id" ) ) );
    
    boolean foundApp = false;
    MEGBundleDescriptor desc = (MEGBundleDescriptor)bundleHash.get( new Long( bundleID ) );
    if( desc == null )
      throw new Exception ( "Application doesn't installed onto the MEG container!" );
    
    int i;
    for( i=0; i != desc.applications.length; i++ )
      if( desc.applications[ i ] == appDesc )
      {        
        foundApp = true;
        break;
      }
    if( !foundApp )  
      throw new Exception ( "Application doesn't installed onto the MEG container!" );
      
    if( !checkDependencies( desc.dependencies[ i ] ) )
      throw new Exception ( "Can't start the application because of failed dependencies!" );
      
    MEGApplicationContext megAppContext = new MEGApplicationContextImpl( bc, appContext.getLaunchArgs() );
    String megclass = ((MEGApplicationDescriptor)(appHandle.getAppDescriptor())).getStartClass();
    
    Class applicationClass = Class.forName( megclass );
    Constructor constructor = applicationClass.getConstructor( new Class [] { MEGApplicationContext.class } );
    MEGApplication app = (MEGApplication)constructor.newInstance( new Object [] { megAppContext } );

    if( desc.eventSubscribes[ i ] != null && desc.eventSubscribes[ i ].eventTopic != null )
    {
      for( int j=0; j != desc.eventSubscribes[ i ].eventTopic.length; j++ )
        if( desc.eventSubscribes[ i ].eventAction[ j ] == EventSubscribe.LISTENER )
        {
          /* TODO TODO TODO TODO TODO */
          Hashtable props = new Hashtable();
          props.put( "topic", desc.eventSubscribes[ i ].eventTopic[ j ] );
          bc.registerService(ChannelListener.class.getName(), app, props);
        }
    }
    return app;
  }
  
  public void unregisterAllApplications() throws Exception
  {
    for( int i=0; i != bundleIDs.size(); i++ )
      unregisterApplicationDescriptors( Long.parseLong( (String)bundleIDs.get( i ) ) );
  }
  
  private boolean checkDependencies( Dependencies deps )
  {
    try
    {
      if( deps.requiredServices != null )
      {
        for( int i=0; i != deps.requiredServices.length; i++ )
        {
            ServiceReference servRef = bc.getServiceReference( deps.requiredServices[ i ] );
            if( servRef == null )
            {
              return false;
            }
        }
      }
      if( deps.requiredPackages != null )
      {    
        ServiceReference pkgAdminSrv = bc.getServiceReference( "org.osgi.service.packageadmin.PackageAdmin" );
        if( pkgAdminSrv == null )
          return true;
      
        PackageAdmin pkgAdmin = (PackageAdmin)bc.getService( pkgAdminSrv );        
        for( int j=0; j != deps.requiredPackages.length; j++ )
          if( pkgAdmin.getExportedPackage( deps.requiredPackages[ j ] ) == null )
          {
            bc.ungetService( pkgAdminSrv );
            return false;
          }
        bc.ungetService( pkgAdminSrv );
      }
      return true;      
    }catch( Exception e )
    {
      return false;
    }
  }
  
  private ApplicationDescriptor[] registerBundle( long bundleID, boolean force )
  {
    ApplicationDescriptor[] appDescs = parseApplicationXML( bundleID );
    if( appDescs == null )
      return null;
      
    if( force )
      for( int i=0; i != appDescs.length; i++ )
        uninstalledAppUIDs.remove( appDescs[ i ]. getUniqueID() );
    
    registerApplicationDescriptors( bundleID );
    return appDescs;
  }
  
  private void registerApplicationDescriptors( long bundleID )
  {
    MEGBundleDescriptor desc = (MEGBundleDescriptor)bundleHash.get( new Long( bundleID ) );
    if( desc == null )
      return;
      
    for( int i=0; i != desc.applications.length; i++ )
    {
      if( desc.serviceRegistrations[ i ] != null )
      {
        desc.serviceRegistrations[ i ].unregister();
        desc.serviceRegistrations[ i ] = null;
      }
    
      if( uninstalledAppUIDs.contains( desc.applications[i].getUniqueID() ) )
        continue;
            
      Dictionary properties = new Hashtable( desc.applications[i].getProperties( (Locale.getDefault()).getLanguage() ) );
      properties.put( "unique_id", desc.applications[i].getUniqueID() );
      properties.remove( "locked" );
      desc.serviceRegistrations[ i ] = bc.registerService("org.osgi.service.application.ApplicationDescriptor", desc.applications[ i ], properties);
    }
  }
  
  private void unregisterApplicationDescriptors( long bundleID )
  {
    MEGBundleDescriptor desc = (MEGBundleDescriptor)bundleHash.get( new Long( bundleID ) );
    if( desc == null )
      return;
      
    for( int i=0; i != desc.serviceRegistrations.length; i++ )
    {
      if( desc.serviceRegistrations[ i ] != null )
      {
        desc.serviceRegistrations[ i ].unregister();
        desc.serviceRegistrations[ i ] = null;
      }
    }
  }
  
  public void bundleChanged( BundleEvent event )
  {
    long bundleID = event.getBundle().getBundleId();
    String bundleStr = Long.toString( bundleID );
    
    if( bundleIDs.contains( bundleStr ) )
    {
      switch( event.getType() )
      {
      case BundleEvent.STARTED:
        registerApplicationDescriptors( bundleID );
        break;
      case BundleEvent.STOPPED:
        unregisterApplicationDescriptors( bundleID );
        break;
      case BundleEvent.UNINSTALLED:
        MEGBundleDescriptor desc = (MEGBundleDescriptor)bundleHash.get( new Long( bundleID ) );
        if( desc != null )
        {
          for( int i=0; i != desc.serviceRegistrations.length; i++ )
            uninstalledAppUIDs.remove( desc.applications[ i ].getUniqueID() );
          saveVector( uninstalledAppUIDs, "UninstalledAppUIDs" );
        }        
        bundleIDs.remove( bundleStr );
        saveVector( bundleIDs, "BundleIDs" );
        unregisterApplicationDescriptors( bundleID );        
        bundleHash.remove( new Long( bundleID ) );
        break;
      case BundleEvent.UPDATED:
        unregisterApplicationDescriptors( bundleID );
        registerApplicationDescriptors( bundleID );
        break;
      }
    }
  }
  
  private Vector loadVector( String fileName )
  {
    Vector resultVector = new Vector();
    
    try 
    {    
      File vectorFile = bc.getDataFile( fileName );
      if( vectorFile.exists() )
      {
        FileInputStream stream = new FileInputStream( vectorFile );  
        String codedIds = "";
      
        byte []buffer = new byte [ 1024 ];
        int length;
      
        while( ( length = stream.read( buffer, 0, buffer.length ) ) > 0 )
          codedIds += new String( buffer );            
        stream.close();      
      
        if( !codedIds.equals("") )
        {
          int index = 0;
          while( index != -1 )
          {
            int comma = codedIds.indexOf( ',', index );
          
            String name;
            if( comma >= 0 )
              name = codedIds.substring( index, comma );
            else
              name = codedIds.substring( index );
          
            resultVector.add( name.trim() );          
            index = comma;
          }
        }
      }
    }catch( Exception e ) {}
    return resultVector;
  }
  
  private void saveVector( Vector vector, String fileName )
  {
    try
    {
      File vectorFile = bc.getDataFile( fileName );
      FileOutputStream stream = new FileOutputStream( vectorFile );
      
      for( int i=0; i != vector.size(); i++ )
        stream.write( ( ( (i == 0) ? "" : "," ) + (String)vector.get( i ) ).getBytes() );
      
      stream.close();
    }catch( Exception e ) {}
  }
    
  private ApplicationDescriptor[] parseApplicationXML( long bundleID )
  {
    try
    {
      URL url = bc.getBundle( bundleID ).getResource("META-INF/applications.xml");

      InputStream in = url.openStream();
      String xml = "";
        
      while (in.available() > 0) 
      {
        byte[] b = new byte[in.available()];
        in.read(b);
        xml = xml + new String(b);
      }        
      in.close();
                  
      XMLElement applicationXML = new XMLElement();
      applicationXML.parseString(xml, 0);
      
      if( !applicationXML.getTagName().equals("descriptor") )
        throw new Exception( "One descriptor must be present in the applications.xml file!" );
              
      LinkedList appVector = new LinkedList();
      LinkedList eventVector = new LinkedList();
      LinkedList dependencyVector = new LinkedList();
        
      Enumeration enum = applicationXML.enumerateChildren();        
      while (enum.hasMoreElements()) {
        XMLElement application = (XMLElement)(enum.nextElement());
        
        if( application.getTagName().equals( "application" ) )
        {
          Properties props = new Properties();
          Hashtable  names = new Hashtable();
          Hashtable  icons = new Hashtable();
          String startClass = null;
          
          props.setProperty( "bundle_id", Long.toString( bundleID ) );
          
          LinkedList eventTopic = new LinkedList();
          LinkedList eventAction = new LinkedList();
          LinkedList requiredServices = new LinkedList();
          LinkedList requiredPackages = new LinkedList();
          
          Enumeration childrenEnum = application.enumerateChildren();        
          while (childrenEnum.hasMoreElements()) 
          {
            XMLElement propertyElement = (XMLElement)(childrenEnum.nextElement());
            
            if( propertyElement.getTagName().equals( "singleton" ) )
              props.setProperty( "singleton", propertyElement.getProperty( "value" ) );
            if( propertyElement.getTagName().equals( "version" ) )
              props.setProperty( "version", propertyElement.getProperty( "value" ) );
            if( propertyElement.getTagName().equals( "autostart" ) )
              props.setProperty( "autostart", propertyElement.getProperty( "value" ) );
            if( propertyElement.getTagName().equals( "category" ) )
              props.setProperty( "category", propertyElement.getProperty( "value" ) );
            if( propertyElement.getTagName().equals( "class" ) )
              startClass = propertyElement.getProperty( "value" );
            if( propertyElement.getTagName().equals( "required_services" ) || 
                propertyElement.getTagName().equals( "required_physical_resource" ) )
            {
              String services = propertyElement.getProperty( "value" );
              int ndx = 0;
              while( ndx != -1 )
              {
                int nextNdx = services.indexOf( ',', ndx );
                String splitted;
                  
                if( nextNdx == -1 )
                  splitted = services.substring( ndx ).trim();
                else
                  splitted = services.substring( ndx,  nextNdx++ ).trim();
                  
                if( splitted.length() != 0 )
                  requiredServices.add( splitted );
                ndx = nextNdx;
              }
            }
            if( propertyElement.getTagName().equals( "required_logical_resource" ) )              
            {
              String packages = propertyElement.getProperty( "value" );
              int ndx = 0;
              while( ndx != -1 )
              {
                int nextNdx = packages.indexOf( ',', ndx );
                String splitted;
                 
                if( nextNdx == -1 )
                  splitted = packages.substring( ndx ).trim();
                else
                  splitted = packages.substring( ndx,  nextNdx++ ).trim();
                  
                if( splitted.length() != 0 )
                  requiredPackages.add( splitted );
                ndx = nextNdx;
              }
            }
            if( propertyElement.getTagName().equals( "subscribe" ) )
            {
              String topicString = propertyElement.getProperty( "event" );
              String actionString = propertyElement.getProperty( "action" );
                
              if( topicString == null || actionString == null )
                throw new Exception( "Invalid Subscribe" );
                  
              int action;
              if( actionString.equals( "start" ) )
                action = EventSubscribe.START;
              else if( actionString.equals( "stop" ) )
                action = EventSubscribe.STOP;
              else if( actionString.equals( "suspend" ) )
                action = EventSubscribe.SUSPEND;
              else if( actionString.equals( "resume" ) )
                action = EventSubscribe.RESUME;
              else if( actionString.equals( "listener" ) )
                action = EventSubscribe.LISTENER;
              else
                throw new Exception( "Invalid Action" );
                  
              eventTopic.add( topicString );
              eventAction.add( new Integer( action ) );
            }
            if( propertyElement.getTagName().equals( "locale" ) )
            {
              String lang = propertyElement.getProperty( "name" );
          
              Enumeration childNodes = propertyElement.enumerateChildren();        
              while ( childNodes.hasMoreElements() ) 
              {
                XMLElement childNode = (XMLElement)(childNodes.nextElement());
             
                if( childNode.getTagName().equals( "name" ) )
                  names.put( lang, childNode.getProperty( "value" ) );
                if( childNode.getTagName().equals( "icon" ) )
                {
                  String sizeX = childNode.getProperty( "sizex" );
                  String sizeY = childNode.getProperty( "sizey" );
                  if( sizeX == null || sizeX.length() == 0 )
                    sizeX = "8";
                  if( sizeY == null || sizeY.length() == 0 )
                    sizeY = "8";
                  icons.put( lang + "_" + sizeX + "x" + sizeY, childNode.getProperty( "value" ) );
                }
              }
            }
          }
          
          if( startClass != null )
          {
            EventSubscribe subscribe = new EventSubscribe();
            
            if( eventTopic.size() != 0 )
            {            
              subscribe.eventTopic = new String [ eventTopic.size() ];
              subscribe.eventAction = new int [ eventTopic.size() ];
              
              int topicNumber = eventTopic.size();
              for( int q = 0; q != topicNumber; q++ )
              {
                subscribe.eventTopic[ q ] = (String)eventTopic.removeFirst();
                subscribe.eventAction[ q ] = ((Integer)eventAction.removeFirst()).intValue();
              }
            }
            
            Dependencies deps = new Dependencies();
            int m;
            deps.requiredServices = new String [ requiredServices.size() ];
            for( m=0; m != requiredServices.size(); m++ )
              deps.requiredServices[ m ] = (String)requiredServices.get( m );
            deps.requiredPackages = new String [ requiredPackages.size() ];
            for( m=0; m != requiredPackages.size(); m++ )
              deps.requiredPackages[ m ] = (String)requiredPackages.get( m );
            
            eventVector.add( subscribe );
            appVector.add( new MEGApplicationDescriptor( bc, props, names, icons, startClass ) );
            dependencyVector.add( deps );
          }
        }
      }

      ApplicationDescriptor []descs = new ApplicationDescriptor[ appVector.size() ];
      int applicationNum = appVector.size();
      for( int k=0; k != applicationNum; k++ )
        descs[ k ] = (ApplicationDescriptor)appVector.removeFirst();
      
      MEGBundleDescriptor descriptor = new MEGBundleDescriptor();
      descriptor.applications = new ApplicationDescriptor[ applicationNum ];
      descriptor.eventSubscribes = new EventSubscribe[ applicationNum ];
      descriptor.dependencies = new Dependencies[ applicationNum ];
      for( int l=0; l != applicationNum; l++ )
      {
        descriptor.applications[ l ] = descs[ l ];
        descriptor.eventSubscribes[ l ] = (EventSubscribe)eventVector.removeFirst();
        descriptor.dependencies[ l ] = (Dependencies)dependencyVector.removeFirst();
      }
      descriptor.serviceRegistrations = new ServiceRegistration[ applicationNum ];
      descriptor.bundleID = bundleID;
      
      bundleHash.put( new Long( bundleID ), descriptor );      
      return descs;
      
    }catch( Exception e )
    {
      return null;
    }
  }
  
  public void channelEvent(ChannelEvent event)
  {
    try
    {
      Enumeration megBundles = bundleHash.keys();
    
      while( megBundles.hasMoreElements() )
      {
        Object key = megBundles.nextElement();
    
        MEGBundleDescriptor bundleDesc = (MEGBundleDescriptor)bundleHash.get( key );
      
        for( int i=0; i != bundleDesc.eventSubscribes.length; i++ )
        {
          if( bundleDesc.eventSubscribes[ i ] != null && bundleDesc.eventSubscribes[ i ].eventTopic != null )
            for( int j=0; j != bundleDesc.eventSubscribes[ i ].eventTopic.length; j++ )
            {
              Filter topicFilter = bc.createFilter( "(topic=" + 
                                   bundleDesc.eventSubscribes[ i ].eventTopic[ j ] + ")" );
              if( event.matches( topicFilter ) )
              {
                switch( bundleDesc.eventSubscribes[ i ].eventAction[ j ] )
                {
                case EventSubscribe.START:
                  ServiceReference appManReference = bc.getServiceReference( "org.osgi.service.application.ApplicationManager" );
                  if( appManReference == null )
                    break; 
          
                  ApplicationManager appMan = (ApplicationManager)bc.getService( appManReference );
                  appMan.launchApplication( bundleDesc.applications[ i ], null );
                  bc.ungetService( appManReference );
                  break;
                case EventSubscribe.STOP:
                case EventSubscribe.SUSPEND:
                case EventSubscribe.RESUME:
                  ServiceReference []references = bc.getServiceReferences( "org.osgi.service.application.ApplicationHandle", 
                                                  "(unique_id="+ bundleDesc.applications[ i ].getUniqueID() + ")" );
                  if( references == null || references.length == 0 )
                    break;
      
                  for( int k=0; k != references.length; k++ )
                  {
                    ApplicationHandle handle = (ApplicationHandle)bc.getService( references [ k ] );
                    
                    switch( bundleDesc.eventSubscribes[ i ].eventAction[ j ] )
                    {
                    case EventSubscribe.STOP:
                      handle.destroyApplication();
                      break;
                    case EventSubscribe.SUSPEND:
                      handle.suspendApplication();
                      break;
                    case EventSubscribe.RESUME:
                      handle.resumeApplication();
                      break;
                    }
                    
                    bc.ungetService( references [ k ] );
                  }
                  break;
                }
              }
            }
        }
      }
    }catch( Exception e ) {}
  }
}
