/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
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
package org.osgi.impl.service.policy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * Utility class for calculating base64 encoded sha-1 values from strings
 * 
 * @version $Revision$
 */
public final class HashCalculator {
	private static final char base64table[] = {
			'A','B','C','D','E','F','G','H',
			'I','J','K','L','M','N','O','P',
			'Q','R','S','T','U','V','W','X',
			'Y','Z','a','b','c','d','e','f',
			'g','h','i','j','k','l','m','n',
			'o','p','q','r','s','t','u','v',
			'w','x','y','z','0','1','2','3',
			'4','5','6','7','8','9','+','_', // !!! this differs from base64
			
	};
	private MessageDigest	md;

	public HashCalculator() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("SHA");
	}

	public String getHash(String from) {
		byte[] byteStream;
		try {
			byteStream = from.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// There's no way UTF-8 encoding is not implemented...
			throw new IllegalStateException("there's no UTF-8 encoder here!");
		}
		byte[] digest = md.digest(byteStream);
		
		// very dumb base64 encoder code. There is no need for multiple lines
		// or trailing '='-s....
		// also, we hardcoded the fact that sha-1 digests are 20 bytes long
		StringBuffer sb = new StringBuffer(digest.length*2);
		for(int i=0;i<6;i++) {
			int d0 = digest[i*3]&0xff;
			int d1 = digest[i*3+1]&0xff;
			int d2 = digest[i*3+2]&0xff;
			sb.append(base64table[d0>>2]);
			sb.append(base64table[(d0<<4|d1>>4)&63]);
			sb.append(base64table[(d1<<2|d2>>6)&63]);
			sb.append(base64table[d2&63]);
		}
		int d0 = digest[18]&0xff;
		int d1 = digest[19]&0xff;
		sb.append(base64table[d0>>2]);
		sb.append(base64table[(d0<<4|d1>>4)&63]);
		sb.append(base64table[(d1<<2)&63]);

		return sb.toString();
		
	}
}
