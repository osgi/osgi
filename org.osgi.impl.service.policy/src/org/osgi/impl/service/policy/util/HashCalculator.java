/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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
