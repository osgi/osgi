/*
 * $Id$
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

package org.osgi.test.cases.provisioning.junit;

public class RSHTest {
	static public  byte[] 				seed = { 0 };
	static public  byte[]              secret = { 
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
		b(10),	b(11), 	b(12), 	b(13), 	b(14), 	b(15), 	b(16), 	b(17), 	b(18), 	b(19),
		b(20),	b(21), 	b(22), 	b(23), 	b(24), 	b(25), 	b(26), 	b(27), 	b(28), 	b(29),
	};
	static public  byte[]              smallsecret = { 
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
	};
	
	static public  byte[]              largesecret = { 
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
		b(10),	b(11), 	b(12), 	b(13), 	b(14), 	b(15), 	b(16), 	b(17), 	b(18), 	b(19),
		b(20),	b(21), 	b(22), 	b(23), 	b(24), 	b(25), 	b(26), 	b(27), 	b(28), 	b(29),
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
		b(10),	b(11), 	b(12), 	b(13), 	b(14), 	b(15), 	b(16), 	b(17), 	b(18), 	b(19),
		b(20),	b(21), 	b(22), 	b(23), 	b(24), 	b(25), 	b(26), 	b(27), 	b(28), 	b(29),
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
		b(10),	b(11), 	b(12), 	b(13), 	b(14), 	b(15), 	b(16), 	b(17), 	b(18), 	b(19),
		b(20),	b(21), 	b(22), 	b(23), 	b(24), 	b(25), 	b(26), 	b(27), 	b(28), 	b(29),
	};
	
	static public  byte[] 				shortsecret = {
		b(0), 	b(1), 	b(2), 	b(3), 	b(4), 	b(5), 	b(6), 	b(7), 	b(8), 	b(9), 
		b(10),	b(11), 	b(12), 	b(13), 	b(14), 	b(15), 	b(16), 	b(17), 	b(18), 
	};
	static public  byte b(int x) { return (byte) x; }
	
	static public  byte [] E = { b(0x05), b(0x36), b(0x54), b(0x70), b(0x00) }; 
	static public  byte [] A = { b(0x00), b(0x4F), b(0x53), b(0x47), b(0x49) };
	
	
}
