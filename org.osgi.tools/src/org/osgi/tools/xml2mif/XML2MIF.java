package org.osgi.tools.xml2mif;
/**
 * KEEP THIS FILE IN UTF-8!!!! If you cannot edit UTF-8 OR have no idea
 * what I am talking about, then DO NOT SAVE this file.
 */
import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XML2MIF extends DefaultHandler {	
	boolean			para;
	boolean			inPara;
	boolean			auto;
	boolean			trimStartPara;
	boolean			doubleSpace;
	boolean			cellContent;
	
	static String	symbols =
		"Tab"
		+	"HardSpace"			// Nonbreaking space
		+	"SoftHyphen"		// Soft hyphen
		+	"HardHyphen"		// Nonbreaking hyphen
		+	"DiscHyphen"		// Discretionary hyphen
		+	"NoHyphen"			// Suppress hyphenation
		+	"Cent"				// Cent (¢)
		+	"Pound Sterling"	// (£)
		+	"Yen"				// Yen (¥)
		+	"EnDash"			// En dash (–)
		+	"EmDash"			// Em dash (—)
		+	"Dagger"			// Dagger (†)
		+	"DoubleDagger"		// Double dagger (‡)
		+	"Bullet"			// Bullet (•)
		+	"NumberSpace"		// Numeric space
		+	"ThinSpace"			// Thin space
		+	"EnSpace"			// En space
		+	"EmSpace"			// Em space
		;
	
	static String translations[] = new String[] {
		"\u007E \\x7e ",
		"\u00A0 <Char HardSpace>",
		"Ä \\x80 ",
		"Å \\x81 ",
		"Ç \\x82 ",
		"É \\x83 ",
		"Ñ \\x84 ",
		"Ö \\x85 ",
		"Ü \\x86 ",
		"á \\x87 ",
		"à \\x88 ",
		"â \\x89 ",
		"ä \\x8a ",
		"ã \\x8b ",
		"å \\x8c ",
		"ç \\x8d ",
		"é \\x8e ",
		"è \\x8f ",
		"ê \\x90 ",
		"ë \\x91 ",
		"í \\x92 ",
		"ì \\x93 ",
		"î \\x94 ",
		"ï \\x95 ",
		"ñ \\x96 ",
		"ó \\x97 ",
		"ò \\x98 ",
		"ô \\x99 ",
		"ö \\x9a ",
		"õ \\x9b ",
		"ú \\x9c ",
		"ù \\x9d ",
		"û \\x9e ",
		"ü \\x9f ",
		"† \\xa0 ",
		"° \\xa1 ",
		"¢ <Char Cent>",
		"£ <Char Pound>",
		"§ \\xa4 ",
		"• <Char Bullet>",
		"¶ \\xa6 ",
		"ß \\xa7 ",
		"® \\xa8 ",
		"© \\xa9 ",
		"™ \\xaa ",
		"´ \\xab ",
		"¨ \\xac ",
		"¦ \\xad ",
		"Æ \\xae ",
		"Ø \\xaf ",
		"× \\xb0 ",
		"± \\xb1 ",
		"ð \\xb2 ",
		"Š \\xb3 ",
		"¥ <Char Yen>",
		"µ \\xb5 ",
		"¹ \\xb6 ",
		"² \\xb7 ",
		"³ \\xb8 ",
		"¼ \\xb9 ",
		"½ \\xba ",
		"ª \\xbb ",
		"º \\xbc ",
		"¾ \\xbd ",
		"æ \\xbe ",
		"ø \\xbf ",
		"¿ \\xc0 ",
		"¡ \\xc1 ",
		"¬ \\xc2 ",
		"Ð \\xc3 ",
		"ƒ \\xc4 ",
		"Ý \\xc5 ",
		"ý \\xc6 ",
		"« \\xc7 ",
		"» \\xc8 ",
		"… \\xc9 ",
		"þ \\xca ",
		"À \\xcb ",
		"Ã \\xcc ",
		"Õ \\xcd ",
		"Œ \\xce ",
		"œ \\xcf ",
		"– \\xd0 ",
		"— \\xd1 ",
		"“ \\xd2 ",
		"” \\xd3 ",
		"‘ \\xd4 ",
		"’ \\xd5 ",
		"÷ \\xd6 ",
		"Þ \\xd7 ",
		"ÿ \\xd8 ",
		"Ÿ \\xd9 ",
		"/ \\xda ",
		"¤ \\xdb ",
		"‹ \\xdc ",
		"› \\xdd ",
		"? \\xde ",
		"? \\xdf ",
		"‡ <Char DoubleDagger>",
		"· \\xe1 ",
		"‚ \\xe2 ",
		"„ \\xe3 ",
		"‰ \\xe4 ",
		"Â \\xe5 ",
		"Ê \\xe6 ",
		"Á \\xe7 ",
		"Ë \\xe8 ",
		"È \\xe9 ",
		"Í \\xea ",
		"Î \\xeb ",
		"Ï \\xec ",
		"Ì \\xed ",
		"Ó \\xee ",
		"Ô \\xef ",
		"š \\xf0 ",
		"Ò \\xf1 ",
		"Ú \\xf2 ",
		"Û \\xf3 ",
		"Ù \\xf4 ",
		"€ \\xf5 ",
		"ˆ \\xf6 ",
		"˜ \\xf7 ",
		"¯ \\xf8 ",
		"° \\xfb ",
		"¸ \\xfc ",
	};
	
	static PrintStream			out = System.out;
	
	
	public static void main( String args[] ) throws Exception {
		InputStream		in = System.in;
		
		for ( int i=0; i<args.length; i++ ) {
			if ( args[i].equals("-o"))
				out = new PrintStream(new FileOutputStream(args[++i]));
			else if ( args[i].equals("-i"))
				in = new FileInputStream(args[++i]);
			else
				throw new RuntimeException("Do not understand " + args[i]);
		}
		XML2MIF handler = new XML2MIF();
		
		SAXParserFactory sp = SAXParserFactory.newInstance();
		SAXParser parser = sp.newSAXParser();	
		parser.parse(in, handler );
	}
	
	
	public void startDocument () { out.println("<MIFFile 6.00>"); }
	public void endDocument () {out.println("# eod"); }
	
	public void startElement (String uri, String elname, String qname, Attributes attributes ) {
		if (elname == null || elname.trim().length() == 0 )
			elname = qname;
		
		if ( elname.equals("MIFFile") )
			return;
		if ( elname.equals("String") ) {
			para = true;
			return;
		}
		
		if ( elname.equals("CellContent")) {
			cellContent = true;
		}
		
		if ( elname.equals("OpenPara")  ) {
			if ( ! auto && !inPara ) {
				out.println( "<Para \n <PgfTag `Body'> #AUTO" );
				auto=true;
				trimStartPara = true;
			}
			return;
		}
		
		if ( elname.equals( "Tab" ) ) {
			trimStartPara = true;
		}
		
		if ( elname.equals( "Para" ) ) {
			trimStartPara = true;
			inPara=true;
			if ( auto ) {
				auto=false;
				
				out.println( "> #AUTO" );
			}
		}
		
		if ( elname.equals("Test") ) {
			for (char c =0x80; c <0xFF; c++ ) {
				out.println( "<Para \n<PgfTag `Body'> <ParaLine" );
				out.println( "<String `\"" + "  \\x" +  Integer.toString(c,16) + "  /x" + Integer.toString(c,16) + "\",'> \n<Char HardReturn>" );
				out.println( "> >" );
			}
			return;
		}
		
		if ( symbols.indexOf(elname)>=0 ) {
			out.println( "<Char " + elname + ">" );
			return;
		}
		if ( elname.equals("br") ) {
			out.println( "<Char HardReturn>" );
			out.println( "> # ParaLine" );
			out.println( "<ParaLine" );
			return;
		}
		if ( elname.equals("HardReturn") ) {
			out.println( "<Char HardReturn>" );
			return;
		}
		
		out.println( "<" + elname );
		
		for ( int i=0; i<attributes.getLength(); i++ ) {
			String key = attributes.getLocalName(i);
			if ( key == null || key.trim().length()==0)
				key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if ( value.indexOf(' ')>=0)
				value = "`" + value + "'";

			value = value.replaceAll(">","\\\\>");
			value = value.replaceAll("<","\\\\<");
			if ( ! value.startsWith("`"))
				value = value.replaceAll("'","\\\\'");
			
			out.println( "<" + key +" " + value + ">" );		
		}
	}
	public void endElement (String uri, String elname, String qname) { 
		if (elname == null || elname.trim().length() == 0 )
			elname = qname;
		
		if ( elname.equals("MIFFile") )
			return;
		if ( elname.equals("HardReturn") )
			return;
		if ( elname.equals("String") ) {
			para = false;
			return;
		}
		if ( elname.equals("CellContent")) {
			cellContent = false;
		}
		if ( elname.equals("OpenPara") ) {
			return;
		}
		if ( elname.equals("Para") ) {
			inPara = false;
		}
		if ( elname.equals("Tab") ) {
			return;
		}
		if ( para && symbols.indexOf(elname)>=0 )
			return;
		
		if ( elname.equals("br") ) {
			return;
		}
		out.println( "> #" + elname );
	}
	
	
	public void characters (char ch[], int start, int length) {
		if ( !para ) {
			String s = new String(ch, start, length ).trim();
			out.println( s );
			return;
		}
		doubleSpace = false;
		int i=0;
		while ( i < length ) {
			StringBuffer sb = new StringBuffer();
			char prev = ' ';
			while ( i<length && (ch[i+start] >= 32 && ch[i+start] < 0x7F || ch[i+start]=='\n' || ch[i+start]=='\t' || ch[i+start]=='\r') ) {
				switch( ch[start+i] ) {
				case 0x27:
					if (prev==' ')
						sb.append( "\\xd4 " ); 
					else
						sb.append( "\\xd5 " ); 
					break;
					
				case 0x22:
					if (prev==' ')
						sb.append( "\\xd2 " ); 
					else
						sb.append( "\\xd3 " ); 
					break;
					
				case '<':   sb.append( "\\x3c " ); break;
				case '>': 	sb.append( "\\x3e " ); break;
				case '\\': 	sb.append( "\\x5c " ); break;
					
				case '\t':
				case '\n': 
				case '\r':
					ch[i+start]=' ';
					// FALL THROUGH
				case ' ': 
					if ( ! trimStartPara && ! doubleSpace) {
						sb.append( ch[start+i] );
						doubleSpace = true;
					}
					break;
					
				default:
					trimStartPara = false;
					doubleSpace = false;
					sb.append( ch[start+i] );
				}
				prev = ch[start+i];
				i++;
			}
			
			String s = sb.toString();
			if ( s.length() > 0 )
				out.println( "<String `" + s + "'>" );
			
			while ( i<length && (ch[start+i] < 32 || ch[start+i] >= 0x7F) ) {
				switch( ch[start+i] ) {
				default:
					String t = find( ch[start+i] );
					if ( t != null ) {
						if ( t.startsWith("<") )
							out.println( t );
						else
							out.println( "<String `" + t + " '>" );
					}
					else {
						switch( ch[start+i] ) {
						case '\t': 
						case '\n': 
						case '\r': break;
						default:
							System.err.println( "No translation for " + ch[start+i] + "  " +Integer.toString(ch[start+i],16) );
							System.err.println( "Context " + (start+i) + new String(ch,start,length));
						}
					}
				}
				i++;
			}
		}
	}
	
	String find( char c ) {
		for ( int i=0; i<translations.length; i++ ) {
			if ( c == translations[i].charAt(0) )
				return translations[i].substring(2);
		}
		return null;
	}


}

