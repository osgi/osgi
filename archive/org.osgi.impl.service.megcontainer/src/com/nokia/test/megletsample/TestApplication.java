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

import org.osgi.service.application.*;
import org.osgi.service.event.*;
import java.io.*;
import java.util.*;

public class TestApplication extends Meglet
{
  String fileName = null;

  public TestApplication()
  {
    super();    
  }
  
  protected void start( Map args, InputStream stateStorage ) throws Exception
  {
    if( args != null )
      fileName = (String)args.get( "TestResult" );  
    writeResult( "START" );
  }

  protected void stop( OutputStream stateStorage ) throws Exception
  {
    writeResult( "STOP" );
  }

  public void suspendApplication() throws Exception
  {
    writeResult( "SUSPEND" );
  }

  public void resumeApplication() throws Exception
  {
    writeResult( "RESUME" );
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
