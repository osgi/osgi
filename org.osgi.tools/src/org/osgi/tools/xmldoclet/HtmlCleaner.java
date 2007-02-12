package org.osgi.tools.xmldoclet;

import java.util.*;

public class HtmlCleaner {
	StringTokenizer		in;
	Stack pushed = new Stack();
	int    pushedType = 0;
	String pushedTag = null;
	String source;
	int cnt =0;
	int line =0;
	String tokens[] = new String[16];
	int rover;
	
	int 		type = 0;
	String		tag = null;
	String		token = null;
	boolean		eof;
	
	final static int CHAR = 1;
	final static int PARA = 2;
	final static int LIST = 4;
	final static int TABLE = 8;
	final static int ROW = 16;
	final static int COLUMN = 32;
	final static int TEXT = 64;
	final static int ITEM = 128;
	final static int CLOSE = 256;
	final static int SINGLE = 512;
	
	
	static Hashtable		descr = new Hashtable();
	
	static {
		descr.put( "h1", new Integer(PARA) );
		descr.put( "h2", new Integer(PARA) );
		descr.put( "h3", new Integer(PARA) );
		descr.put( "h4", new Integer(PARA) );
		descr.put( "h5", new Integer(PARA) );
		descr.put( "h6", new Integer(CHAR) );
		descr.put( "h7", new Integer(PARA) );
		descr.put( "h8", new Integer(PARA) );
		descr.put( "p", new Integer(PARA) );
		descr.put( "pre", new Integer(PARA) );
		descr.put( "em", new Integer(CHAR) );
		descr.put( "i", new Integer(CHAR) );
		descr.put( "b", new Integer(CHAR) );
		descr.put( "strong", new Integer(CHAR) );
		descr.put( "tt", new Integer(CHAR) );
		descr.put( "code", new Integer(CHAR) );
		descr.put( "big", new Integer(CHAR) );
		descr.put( "small", new Integer(CHAR) );
		descr.put( "sub", new Integer(CHAR) );
		descr.put( "sup", new Integer(CHAR) );
		descr.put( "ul", new Integer(LIST) );
		descr.put( "ol", new Integer(LIST) );
		descr.put( "dl", new Integer(LIST) );
		descr.put( "menu", new Integer(LIST) );
		descr.put( "li", new Integer(ITEM) );
		descr.put( "dd", new Integer(ITEM) );
		descr.put( "dt", new Integer(ITEM) );
		descr.put( "table", new Integer(TABLE) );
		descr.put( "tr", new Integer(ROW) );
		descr.put( "th", new Integer(COLUMN) );
		descr.put( "td", new Integer(COLUMN) );
		descr.put( "br", new Integer(SINGLE) );
		descr.put( "a", new Integer(CHAR) );
	}
	
	StringBuffer result = new StringBuffer();
	
	public HtmlCleaner( String s  ) {
		source = s;
		in = new StringTokenizer(s, "<>", true );
	}
	
	public String clean() {
		try {
			result = new StringBuffer();
			while( ! eof ) {
				next();
				element( -1);
				if ( !eof ) {
					StringBuffer sb = new StringBuffer();
					for ( int i= 0; i<tokens.length; i++ ) {
						sb.append(" ");
						String tmp = tokens[(i+rover)%tokens.length];
						if ( tmp != null )
							sb.append(tmp);
					}
					next();
					sb.append(token);
					error( "Invalid tag on top level " + sb  );
				}
			}
			return result.toString();
		}
		catch( Exception e ) {
			e.printStackTrace();
			error( "Cannot parse text" );
			return "XXX" + escape( source );
		}
	}
	
	String escape( String s ) {
		StringBuffer sb = new StringBuffer();
		for ( int i=0; i<s.length(); i++ ) {
			char c = s.charAt( i );
			switch( c ) {
			case '<': sb.append( "&lt;" ); break;
			case '>': sb.append( "&gt;" ); break;
			case '&': sb.append( "&amp;" ); break;
			case '\'': sb.append( "&quot;" ); break;
			case '"': sb.append( "&quot;" ); break;
			default: sb.append( c );
			}
		}
		return sb.toString().trim();
	}
	
	
	// on entry = token is first element
	void element( int allowed ) {
		String current = tag;
		
		while ( ! eof ) {
			if ( type == CLOSE ) {
				return;
			}
			
			if ( (type & allowed) == 0 ) {
				//System.out.println( "Not allowed now " + token + " in " + current );
				push();
				return;
			}
			
			switch( type ) {					
			case SINGLE:
				result.append( "<" + tag.toLowerCase() + "/>" );
				break;
				
			case TEXT:
				result.append(token);
				break;
				
			case PARA:
				element( token, CHAR + TEXT + SINGLE );
				break;
				
			case CHAR:
				element( token, TEXT + SINGLE);
				break;
				
			case TABLE:
				element( token, ROW + TEXT );
				break;
				
			case ROW:
				element( token, COLUMN + TEXT );
				break;
				
			case COLUMN:
				element( token, CHAR + TEXT );
				break;
				
			case LIST:
				element( token, ITEM + TEXT   );
				break;
				
			case ITEM:
				element( token, CHAR + TEXT + SINGLE + LIST );
				break;
				
			default:
				error( "Unexpected element "  );
				return;
			}
			next();
		}
	}
	
	void element( String tag, int allowed ) {
		//System.out.print( "<" + tag + ">" );
		String current = tag;
		result.append( "<" + getTag(current) + getRemainder(current) + ">" );
		next();
		if ( tag.equalsIgnoreCase( "pre" ) ) {
			StringBuffer		previous = result;
			result = new StringBuffer();
			element( allowed );
			previous.append( pre( result.toString() ) );
			result = previous;
		}
		else
			element( allowed );
		result.append( "</" + getTag(current) + ">" );
		//System.out.print( "</" + current + ">" );
	}
	
	
	void next() {
		tokens[rover++%tokens.length] = token;
		if ( ! pushed.empty() ) {
			token = (String) pushed.pop();
			//System.out.println( "pushed " + token );
			return;
		}
		else if ( ! in.hasMoreTokens() ) {
			token = "";
			eof = true;
			//System.out.println( "eof" );
			return;
		} else {
			token = in.nextToken();
			count(token);
		}
		//result.append( "<!-- TOKEN " + token + "-->" );
		
		if ( token.equals( ">" )  ) {
			type = TEXT;
			token = "&gt;";
			return;
		}
		
		if ( ! token.equals( "<" )  ) {
			type = TEXT;
			return;
		}
		
		String content = in.nextToken();
		count(content);
		//result.append( "<!-- TAG '" + content + "'-->" );
		char first = content.charAt(0);
		if ( ! Character.isLetter(first) && first != '/' ) {
			type= TEXT;
			token = "&lt;" + escape( content );
			return;
		}
		
		String stop = in.nextToken();
		count(stop);
		//result.append( "<!-- STOP '" + stop + "'-->" );
		if ( ! stop.equals( ">" ) ) {
			error( "Expected close" );
			token = escape( "<" + content + stop );
			type = TEXT;
			return;
		}
		
		if ( content.startsWith( "/" ) ) {
			type = CLOSE;
			token = content.substring(1).trim();
			tag = getTag( token );
			//System.out.println( "close " + tag );
			return;
		}
		else if ( content.endsWith( "/" ) ) {
			type = SINGLE;
			token = tidy(content.substring( 0, content.length()-1));
			tag = getTag( token );
			//System.out.println( "single " + tag );
			return;
		} else {
			token = tidy(content);
			tag = getTag( token );
			Integer t = (Integer) descr.get( tag );
			if ( t != null )
				type = t.intValue();
			else {
				error( "Unexpected tag, assume para" );
				type = PARA;
			}
			//System.out.println( "tag " + tag + " " + t );
			return;
		}			
	}
	
	String getTag( String s ) {
		StringTokenizer st = new StringTokenizer(s," ");
		return st.nextToken().toLowerCase();
	}
	
	String getRemainder( String s ) {
		int n = s.indexOf( " " );
		if ( n < 0 )
			return "";
		
		return s.substring(n);
	}
	
	
	void push() {
		pushed.push(token);
	}
	
	void error( String msg ) {
		//System.out.println( msg );
		result.append( "\n<formattingerror msg='" + msg + "' line='" + line + "' cnt='" + cnt + "' token='" + escape(token) 
			+ "'><![CDATA[" +  source + "]]></formattingerror>\n" );
	}
	
	
	
	String pre( String s ) {
		StringBuffer sb = new StringBuffer();
		while ( s.startsWith( "\n") || s.startsWith( "\r" ) )
			s = s.substring( 1 );
		while ( s.endsWith( "\n") || s.endsWith( "\r" ) || s.endsWith(" ") )
			s = s.substring( 0, s.length() - 1  );
		
		for ( int i=0; i<s.length(); i++ ) {
			char c= s.charAt(i);
			switch( c ) {
			case ' ':
				sb.append( "&#160;" );
				break;
			case '\n':
				sb.append( "<br/>" );
				break;
				
			default:
				sb.append( c );
			}
		}
		return sb.toString();
	}
	
	
	static String tidy( String in ) {
		StringBuffer sb = new StringBuffer();
		in = in.replace( '\t', ' ').trim();
		int n = in.indexOf( ' ' );
		if ( n < 0 )
			n = in.length();
		
		sb.append( in.substring(0,n).trim() );
		
		String remainder = in.substring(n).trim();
		while ( remainder.length() > 0 ) {
			int k = remainder.indexOf( '=' );
			if ( k > 0 ) {
				sb.append( " " );
				sb.append( remainder.substring( 0,k).trim() );
				sb.append( "=" );
				
				remainder = remainder.substring( k+1 ).trim();
				char del = remainder.charAt(0);
				char use = del;
				if ( del=='\'' || del=='"' )
					remainder = remainder.substring(1);
				else {
					del = ' ';
					use = '"';
				}
				int e = remainder.indexOf( del );
				if ( e == -1 )
					e = remainder.length();
				
				sb.append( use );
				sb.append( remainder.substring( 0, e ) );
				sb.append( use );
				if ( e >= remainder.length() )
					break;
				
				remainder = remainder.substring(e+1).trim();
			}
			else
				throw new RuntimeException( "Not a valid element " + in );
		}
		return sb.toString();
	}
	
	
	void count( String s ) {
		cnt += s.length();
		for ( int i=0; i<s.length(); i++ )
			if (s.charAt(i)=='\n') {
				line++;
				cnt = 0;
			}
			else
				cnt++;
	}
	
	
	public static void main( String args[] ) {
		
		String t1 = " abc def = ghi";
		String t2 = " abc def = 'ghi' klm=0";
		String t3 = " abc def =\"g'h'i\"";
		String t4 = " abc def =\"g'h'i\"";
		String t5 = "abc def =\"g'h'i\"";
		String t6 = "ab-c def=\"g'h'i\"";
		System.out.println( t1 + " - " + tidy( t1 ) );
		System.out.println( t2 + " - " + tidy( t2 ) );
		System.out.println( t3 + " - " + tidy( t3 ) );
		System.out.println( t4 + " - " + tidy( t4 ) );
		System.out.println( t5 + " - " + tidy( t5 ) );
		System.out.println( t6 + " - " + tidy( t6 ) );
		
		StringBuffer sb = new StringBuffer();
		for ( int i=0; i<args.length; i++ ) {
			sb.append( args[i] );
			sb.append( " " );
		}
		System.out.println( new HtmlCleaner( sb.toString() ).clean() );
	}
	
	
	
}

