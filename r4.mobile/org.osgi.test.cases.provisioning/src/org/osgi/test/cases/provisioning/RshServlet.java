/*
 * $Header$
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

package org.osgi.test.cases.provisioning;
import java.io.*;
import java.security.*;
import java.util.zip.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.servlet.http.*;


/**
 * This servlet can deliver an ipa file for initial provisioning
 * encoded for rsh.
 */

public class RshServlet extends HttpServlet {	
	static public  byte [] E = { b(0x05), b(0x36), b(0x54), b(0x70), b(0x00) };
	static public  byte [] A = { b(0x00), b(0x4F), b(0x53), b(0x47), b(0x49) };
	static final public  byte b(int x) { return (byte) x; }
	
	public void doGet( HttpServletRequest rq, HttpServletResponse rsp ) throws IOException {
		String	spid = rq.getParameter( "service_platform_id" );
		String	clientfg = rq.getParameter( "clientfg" );
		String	time = rq.getParameter( "time" )+"";
		String	debug = rq.getParameter( "debug" );
		
		try {
			xassert( "SPID is required", spid != null && spid.length() > 1 );

			byte [] r = buildZip( new String[] {
					"rsh.ipa", 	"text/plain;charset=utf-8", "rsh.ipa",
					"provisioning.bundle.start", 	"text/plain;charset=utf-8", "sample.jar",
					"time", 	"text/plain;charset=utf-8", time,
					"clientfg", "text/plain;charset=utf-8", clientfg,
					"spid", 	"text/plain;charset=utf-8", spid } );
			if ( debug != null ) {
				FileOutputStream tout = new FileOutputStream( "director-test.zip");
				tout.write( r );
				tout.close();
			}
			
			//
			// The following section uses exactly the
			// names and sequence used in the spec. It is thus
			// not optimized in any way
			//
			byte [] d		= getSecret(spid);	
			
			byte [] c		= decode64( clientfg );					// 2. c(s)=c(c)
			byte [] s		= getNonce();							// 3. s=nonce
			byte [] Ka 		= sha1( cat( d, c, s, A ) );			// 4. Ka=SHA1({d,c,s,A})
			byte [] M01to20	= sha1( cat( d, c, s, E ) );			// 5. calc M for Ke			
			byte [] M21to40	= sha1( cat( d, M01to20, c, s, E ) );			
			byte []	M 		= cat( M01to20, M21to40 );
			
			byte [] Ke 		= extract( M, 0, 24 );					// 6. Key & IV for DES
			byte [] IV 		= extract( M, 24, 8 );
			for ( int i=0; i<Ke.length; i++ ) 						//    Set parity
				Ke[i] = parity(Ke[i]);		
			byte [] e 		= DES3( Ke, IV, r ); 					//    Encrypt
			
			byte [] mac 	= HMAC( Ka, e );						// 7. Mac calculation
			
			mac = extract( mac, 0, 16 ); //### This is NOT specified anywhere
				
			xassert( "M01to20 length must be 20",  	M01to20.length == 20 );
			xassert( "M21to40 length must be 20",  	M21to40.length == 20 );
			xassert( "Ke length must be 24",  		Ke.length == 24 );
			xassert( "IV length must be 8",  		IV.length == 8 );
			xassert( "MAC length must be 16",  		mac.length == 16 );
			xassert( "Server nonce must be 16", 	s.length == 16 );
			//xassert( "Secret > 160 bits", 			d.length >= 20 ); //
			
			rsp.setHeader( "Expires", "0" );
			rsp.setHeader( "X-Nonces", hex(s) + " " + hex(c));
			rsp.setContentType( "application/x-rsh" );
			rsp.setContentLength( 46 + e.length );
			
			DataOutputStream		out = new DataOutputStream( rsp.getOutputStream() );
			out.writeInt( 46 );					// header length
			out.writeShort( 0x0100 );				// version
			out.write( s );						// server nonce
			out.writeInt( 16 );					// nr of bytes in MAC
			out.write( mac );					// The MAC
			out.writeInt( e.length );			// Zip file length
			out.write( e );						// Encrypted data
			out.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
			rsp.sendError( HttpServletResponse.SC_BAD_REQUEST, "Exception " + e );
		}
	}
	
	
	byte parity( byte data ) {
		byte		toggle = 1;
		
		for ( int mask=0x80; mask!=0; mask/=2 )
			if ( (data & mask) != 0 )
				toggle ^= 1;
		
		return (byte) (data ^ toggle);
	}
	
	
	
	byte [] sha1( byte [] data ) throws NoSuchAlgorithmException {
		MessageDigest	sha1 = MessageDigest.getInstance( "SHA1" );
		sha1.update( data );
		return sha1.digest();
	}
	
	
	byte [] cat( byte [] a, byte [] b ) {
		byte r[] = new byte[ a.length + b.length ];
		System.arraycopy( a, 0, r, 0, a.length );
		System.arraycopy( b, 0, r, a.length, b.length );
		return r;
	}
	
	byte [] cat( byte [] a, byte [] b, byte [] c, byte[] d, byte[] e ) {
		return cat( cat(a,b,c,d), e );
	}
	byte [] cat( byte [] a, byte [] b, byte [] c, byte[] d ) {
		byte r[] = new byte[ a.length + b.length + c.length + d.length ];
		
		System.arraycopy( a, 0, r, 0, a.length );
		System.arraycopy( b, 0, r, a.length, b.length );
		System.arraycopy( c, 0, r, a.length + b.length, c.length );
		System.arraycopy( d, 0, r, a.length + b.length + c.length, d.length );
		return r;
	}
	
	
	byte [] extract( byte data[], int where, int length ) {
		byte r[] = new byte[ length ];
		System.arraycopy( data, where, r, 0, length );
		return r;
	}
	
	
	
	byte [] DES3( byte [] Ke, byte [] IV, byte [] r ) throws Exception {
		Cipher DESede = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		
		DESedeKeySpec desKeySpec 	= new DESedeKeySpec(Ke);		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey key 				= keyFactory.generateSecret(desKeySpec);
		
		DESede.init(
			Cipher.ENCRYPT_MODE,
			key,
			new IvParameterSpec(IV) );
		
		return DESede.doFinal( r );
	}

	
	byte [] HMAC( byte [] Ka, byte [] e ) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec macKey = new SecretKeySpec(Ka, "HmacSHA1");
		mac.init(macKey);
		return mac.doFinal(e);
	}
	
	
	byte [] getNonce() {
		byte [] nonce = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		return nonce;
	}
	
	String hex(byte data[] ) {
		StringBuffer sb = new StringBuffer( data.length * 2 );
		for ( int i=0; i<data.length; i++ ) {
			sb.append( hex( 0xFF & data[i]) );
		}
		return sb.toString();
	}
	
	String hex( int i ) {
		String		hex =Integer.toHexString(i).toUpperCase();
		if ( i >= 16 )
			return hex;
		else	
			return "0" + hex;
	}
	
	
	
	byte[] getSecret(String spid) {
		if ( spid.indexOf("large") > 0 )
			return RSHTest.largesecret;
		if ( spid.indexOf("small") > 0 )
			return RSHTest.smallsecret;
		return RSHTest.secret;
	}
	
	
	
	byte [] buildZip( String [] fields ) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ZipOutputStream	out = new ZipOutputStream(bout);
		
		InputStream in = getClass().getResourceAsStream("/www/simple.jar");
		byte bundle[] = collect(in,0);
		in.close();
		addEntry( out, "simple.jar", "application/x-osgi-bundle", bundle );
		
		for ( int i=0; fields!=null && i<fields.length; i+=3 ) {
			addEntry( out, fields[i], fields[i+1], fields[i+2].getBytes() );
		}
		out.close();
		bout.close();
		return bout.toByteArray();
	}


	void addEntry( ZipOutputStream out, String path, String extra, byte [] content ) throws IOException {
		CRC32 checksum = new CRC32();
		checksum.update(content);
		ZipEntry ze = new ZipEntry( path );
		ze.setSize(content.length);
		ze.setCrc(checksum.getValue());
		ze.setExtra( extra.getBytes() );
		out.putNextEntry(ze);
		out.write(content, 0, content.length);
		out.closeEntry();
	}


	byte[] collect( InputStream in, int size ) throws IOException {
		byte buf[] = new byte[1024];
		int sz= in.read( buf );
		if ( sz <=0 ) {
			return new byte[size];
		}
		byte buffer[] = collect( in, size + sz );
		System.arraycopy( buf, 0, buffer, size, sz );
		return buffer;
	}
	
	byte[] decode64(String s) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		byte[] c = s.getBytes();
		int endchar = -1;
		for(int j = 0; j < c.length && endchar == -1; j++) {
			if (c[j] >= 'A' && c[j] <= 'Z') {
				c[j] -= 'A';
			} else if (c[j] >= 'a' && c[j] <= 'z') {
				c[j] = (byte) (c[j] + 26 - 'a');
			} else if (c[j] >= '0' && c[j] <= '9') {
				c[j] = (byte) (c[j] + 52 - '0');
			} else if (c[j] == '+') {
				c[j] = 62;
			} else if (c[j] == '/') {
				c[j] = 63;
			} else if (c[j] == '=') {
				endchar = j;
			} else
				throw new IllegalArgumentException("Invalid base 64 " + s );
		}
		
		int remaining = endchar == -1 ? c.length : endchar;
		int i = 0;
		while (remaining > 0) {
			// Four input chars (6 bits) are decoded as three bytes as
			// 000000 001111 111122 222222
			byte b0 = (byte) (c[i] << 2);
			if (remaining >= 2) {
				b0 += (c[i+1] & 0x30) >> 4;
			}
			bs.write(b0);
			if (remaining >= 3) {
				byte b1 = (byte) ((c[i+1] & 0x0F) << 4);
				b1 += (byte) ((c[i+2] & 0x3C) >> 2);
				bs.write(b1);
			}
			if (remaining >= 4) {
				byte b2 = (byte) ((c[i+2] & 0x03) << 6);
				b2 += c[i+3];
				bs.write(b2);
			}
			i += 4;
			remaining -= 4;
		}
		return bs.toByteArray();
	}
	
	
	
	void xassert( String msg, boolean result ) {
		if ( !result )
			throw new RuntimeException("Assert: "+ msg);
	}
}

