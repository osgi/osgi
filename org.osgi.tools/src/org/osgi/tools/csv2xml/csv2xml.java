package org.osgi.tools.csv2xml;

import java.util.*;
import java.io.*;

public class csv2xml {

	public final static int OUTSIDE	= 1;
	public final static int INSIDE = 2;
	public final static int  QUOTE = 3;

	static Vector 		record;
	static Vector 		names;
	static StringBuffer field; 
	static String		encoding = "ISO-8859-1";
	static String		stylesheet;
	static String		maintag = "cvs";
	static String		recordtag = "record";
	static boolean		first	= true;
	static int			length = 0;
	static boolean		autonames;
	static PrintWriter	pw;

	public static void main( String args[] ) throws Exception {
		for ( int i= 0; i<args.length; i++ ) {
			if ( args[i].equals( "-encoding" ) )
				encoding = args[++i];
			else
			if ( args[i].equals( "-autonames" ) )
				autonames = true;
			else
			if ( args[i].equals( "-stylesheet" ) )
				stylesheet = args[++i];
			else
			if ( args[i].equals( "-maintag" ) )
				maintag = args[++i];
			else
			if ( args[i].equals( "-recordtag" ) )
				recordtag = args[++i];
			else
			if ( args[i].equals( "-help" ) )
				System.out.println( "[ -encoding iso-8859-1 ][ -stylesheet xyz.xsl ] [-maintag csv] [-recordtag record][-column name ...]" );
			else
			if ( args[i].equals( "-column" ) ) {
				if ( names == null ) 
					names = new Vector();
				names.addElement( args[++i] );
			}
			else {
				Reader reader = new BufferedReader( new InputStreamReader( new FileInputStream( args[i] ), encoding ) );
				int c = reader.read();
				int state = OUTSIDE;
				record = new Vector();
				field = new StringBuffer();
				
				while ( c >= 0 ) {
					switch( state ) {
					case OUTSIDE:
						switch( c ) {
							case '"': state=INSIDE; break;
							case '\n':
							case '\r': record(); break;
							case ',': field(); break;
							case '&': field.append( "&amp;" ); break;
							case '>': field.append( "&gt;" ); break;
							case '<': field.append( "&lt;" ); break;
							default:
								field.append( (char) c );
						}
						break;
					case INSIDE:
						switch( c ) {
							case '"': state=QUOTE; break;
							case '&': field.append( "&amp;" ); break;
							case '>': field.append( "&gt;" ); break;
							case '<': field.append( "&lt;" ); break;
							default:
								field.append( (char) c );
						}
						break;
					case QUOTE:
						switch( c ) {
							case '"': state=INSIDE; field.append( '"' ); break;							
							case ',': state=OUTSIDE; field(); break;
							case '\n':
							case '\r': state=OUTSIDE; record(); break;
							default:
								throw new RuntimeException( "Quote followed by '" + (char) c + "' " + c);
						}
						break;
					}
					c = reader.read();
				}			
			}
		}
		if ( ! first ) {
			pw.println( "</" + maintag + ">" );
			pw.close();
		}
			
	}
	
	static void field() {
		String content = field.toString();
		record.addElement( field.toString() );
		length += content.length();
		field = new StringBuffer();
	}
	
	static void record() throws UnsupportedEncodingException {
		field();
		if ( record.size() != 0 && length > 0 ) {
			if ( first ) {
				pw = new PrintWriter(
					new OutputStreamWriter( System.out, "ISO-8859-1" )
				);
				if ( autonames )
					names = record;
				pw.println( "<?xml version='1.0' encoding='iso-8859-1'?>" );
				if ( stylesheet != null )
					pw.println( "<?xml-stylesheet type='text/xsl' href='" + stylesheet + "'?>" );
				pw.println( "<" + maintag + ">" );
				first = false;
			}
			else {
				pw.println( "    <" + recordtag + ">" );
				for ( int i=0; i<record.size(); i++ ) {
					String field = (String) record.elementAt(i);
					String name = "col" + i;
					if ( names != null && names.size() >= i + 1 )
						name = (String) names.elementAt(i);

					if ( field.trim().length() > 0 ) {
						pw.println( "  	<" + name + ">" );
						pw.println( field );
						pw.println( "  	</" + name + ">" );
					}
				}
				pw.println( "    </" + recordtag + ">" );
			}
		}
		record = new Vector();
		length = 0;
	}
	

	static String escape( String s ) {
		return s;
	}
}

