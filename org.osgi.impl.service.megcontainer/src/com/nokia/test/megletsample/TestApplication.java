/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package com.nokia.test.megletsample;

import java.io.*;
import java.util.Map;

import org.osgi.meglet.Meglet;
import org.osgi.service.event.Event;

public class TestApplication extends Meglet
{
  String fileName = null;
  String storedString = null;

  public TestApplication()
  {
    super();    
  }
  
  public void start( Map args ) throws Exception
  {
    if( args != null )
      fileName = (String)args.get( "TestResult" );
    
    writeResult( "START" );
  }

  public void stop() throws Exception
  {
    writeResult( "STOP" );
  }

  public void suspend( OutputStream stateStorage ) throws Exception
	{
    storedString = "StorageTestString";      
    ObjectOutputStream ois = new ObjectOutputStream( stateStorage );
    ois.writeObject( fileName );
    ois.writeObject( storedString );
    
    writeResult( "SUSPEND:" + storedString );	
	}
  
  public void resume( InputStream stateStorage ) throws Exception
	{
    ObjectInputStream ois = new ObjectInputStream( stateStorage );
    fileName = (String)ois.readObject();
    storedString = (String)ois.readObject();
    writeResult( "RESUME:" + storedString );  	
	}
  
  
  public void handleEvent(Event event)
  {
  }
  
  private void writeResult( String result )
  {
    try {
      if( fileName == null )
        return;
      File file = new File( fileName );
      FileOutputStream stream = new FileOutputStream( file );
    
      stream.write( result.getBytes() );
      stream.close();      
    }catch( IOException e ) {}
  }
}
