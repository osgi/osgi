package org.osgi.tools.properties;

import org.osgi.framework.*;

public class Properties implements BundleActivator 
{
	
	public void start( BundleContext context ) 
	{
		formatOutput( "os.name=", System.getProperty( "os.name" ) );
		formatOutput( "os.arch=", System.getProperty( "os.arch" ) );
		formatOutput( "os.version=", System.getProperty( "os.version" ) );
		formatOutput( "java.vendor=", System.getProperty( "java.vendor" ) );
		formatOutput( "java.class.version=", System.getProperty( "java.class.version" ) );
		formatOutput( "java.version=", System.getProperty( "java.version" ) );
		formatOutput( "file.separator=", System.getProperty( "file.separator" ) );
		formatOutput( "path.separator=", System.getProperty( "path.separator" ) );
		formatOutput( "org.osgi.framework.bootdelegation=", System.getProperty( "org.osgi.framework.bootdelegation" ) );
		formatOutput( "org.osgi.framework.system.packages=", System.getProperty( "org.osgi.framework.system.packages" ) );
		formatOutput( "org.osgi.supports.framework.extension=", System.getProperty( "org.osgi.supports.framework.extension" ) );
		formatOutput( "microedition.configuration=", System.getProperty( "microedition.configuration" ) );
		formatOutput( "microedition.profiles=", System.getProperty( "microedition.profiles" ) );
		formatOutput( "org.osgi.framework.executionenvironment=", System.getProperty( "org.osgi.framework.executionenvironment" ) );
		formatOutput( "org.osgi.supports.bootclasspath.extension=", System.getProperty( "org.osgi.supports.bootclasspath.extension" ) );
		formatOutput( "org.osgi.supports.framework.fragment=", System.getProperty( "org.osgi.supports.framework.fragment" ) );
		formatOutput( "org.osgi.supports.framework.requirebundle=", System.getProperty( "org.osgi.supports.framework.requirebundle" ) );
		
		
		formatOutput( "org.osgi.framework.version=", context.getProperty( "org.osgi.framework.version" ) );
		formatOutput( "org.osgi.framework.language=", context.getProperty( "org.osgi.framework.language" ) );
		formatOutput( "org.osgi.framework.vendor=", context.getProperty( "org.osgi.framework.vendor" ) );
		formatOutput( "org.osgi.framework.os.name=", context.getProperty( "org.osgi.framework.os.name" ) );
		formatOutput( "org.osgi.framework.os.version=", context.getProperty( "org.osgi.framework.os.version" ) );
		formatOutput( "org.osgi.framework.processor=", context.getProperty( "org.osgi.framework.processor" ) );
		
		
		
	}

	public void stop( BundleContext c ) {}

	void formatOutput( String title, String val ) {
		int	        len;
		val = val + "";
		len = title.length();
		System.out.print( title );
		while ( len++ < 20 )
			System.out.print( " " );

		System.out.print( "\"" + val + "\"" );
		len += val.length();

		while ( len++ < 50 )
			System.out.print( " " );

		System.out.println( "\"" + val.toLowerCase() + "\"" );
	}

}


