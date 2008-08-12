package org.osgi.tools.indexer;

import java.io.File;
import java.util.*;
import java.util.zip.*;

public class Index {

	static void p( String hdr, Object value ) {
		if ( value != null ) {
			if ( value instanceof Object[] )
			{
				Object[] list = (Object[]) value;
				for ( int i=0; i<list.length; i++)
					p( hdr, list[i] );
			}
			else {
				if ( value instanceof org.osgi.tools.btool.Package ) {
					org.osgi.tools.btool.Package pack = (org.osgi.tools.btool.Package) value;
					if ( pack.getVersion().length() != 0 )
						System.out.print( "<" + hdr + " specification-version='" + pack.getVersion() + "'>" );
					else
						System.out.print( "<" + hdr + ">" );
					System.out.print( pack.getName() );
					System.out.println( "</" + hdr + ">" );
				}
				else {
					System.out.print( "<" + hdr + ">" );
					System.out.print( clean(value.toString()) );
					System.out.println( "</" + hdr + ">" );
				}
			}
		}
	}
	
	static String clean( String s ) {
		StringBuffer sb = new StringBuffer();
		for ( int i=0; i < s.length(); i++ )
		{
			char c = s.charAt(i);
			switch( c ) {
			case '&': sb.append( "&amp;" ); break;
			case '<': sb.append( "&lt;" ); break;
			case '>': sb.append( "&gt;" ); break;
			case '\'':
			case '\n':
			case '\r':
			case '\\':
				sb.append( '\\' );
				// Fall through!
				
			default:
				sb.append( c );
			}   		
		}
		return sb.toString();
	}
	
	
	public static void main( String args[] ) throws Exception {
		try {
			System.err.println( "Bundle Indexer | v1.1" );
			System.err.println( "(c) 2001 OSGi, All Rights Reserved" );
			
			TreeMap 	primary = new TreeMap();
			
			for ( int i=0; i<args.length; i++)
			try
			{
				File				file = new File( args[i] );
				if ( !file.exists() )
				{
					System.err.println( "File does not exist " + file );
					continue;
				}
				
				ZipFile 			zip = new ZipFile( file );
				ZipEntry			entry = zip.getEntry( "META-INF/MANIFEST.MF" );
				
				if ( entry == null )
				{
					System.err.println( "No manifest entry: " + file );
					continue;
				}
				
				org.osgi.tools.btool.Manifest			m  = new org.osgi.tools.btool.Manifest( null, zip.getInputStream(entry) );
				Hashtable   		properties = new Hashtable();
				String name = (String) m.get( "Bundle-Name" );
				if ( name == null )
					name = file.getName();
					
				int n = 1;
					
				put( properties,  "name",   		name );
				put( properties,  "file",   		file.getName() );
				put( properties,  "modified",   	new Date( file.lastModified() ) );
				put( properties,  "vendor", 		m.get( "Bundle-Vendor" ) );
				put( properties,  "version",		m.get( "Bundle-Version" ) );
				put( properties,  "doc",			m.get( "Bundle-DocURL" ) );
				put( properties,  "contact",		m.get( "Bundle-ContactAddress" ) );
				put( properties,  "description",   	m.get( "Bundle-Description" ) );
				put( properties,  "category", 	list(m.get( "Bundle-Category" )) );
				put( properties,  "impservice",	list(m.get( "Import-Service" )) );
				put( properties,  "expservice",	list(m.get( "Export-Service" )) );
				put( properties,  "copyright",  	m.get( "Bundle-Copyright" ) );
				put( properties,  "size",   	"" + file.length() );
				put( properties,  "import",			m.getImports() );
				put( properties,  "export",			m.getExports() );
				
				primary.put( file.getName(), properties );				
				zip.close();
			}
			catch( Exception e ) {
				System.err.println( "Error " + e );
				e.printStackTrace();
			}

			// Now we start to print the input
			
			System.out.println( "<?xml version='1.0' encoding='ISO-8859-1'?>" ); 
			System.out.println( "<bundles>" );
			for ( Iterator i = primary.keySet().iterator(); i.hasNext(); )
			{
				System.out.println( "<bundle>" );
				String name = (String) i.next();
				
				Hashtable ht = (Hashtable) primary.get( name );
				for ( Enumeration e = ht.keys(); e.hasMoreElements(); )
				{
					String key = (String) e.nextElement();
					p( key, ht.get(key) );
				}
				System.out.println( "</bundle>" );
			}   		
			System.out.println( "</bundles>" );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	static void put( Hashtable pt, String key, Object value ) {
		if (value == null )
			return;
			
		pt.put( key, value );
	}
	
	static Object [] list( Object l ) {
		if ( l== null )
			return null;
		
		StringTokenizer st = new StringTokenizer( "" + l, "," );
		String result[] = new String[ st.countTokens() ];
		for ( int i=0; i< result.length; i++)
			result[i] = st.nextToken().trim();
		
		return result;
	}
	
}

