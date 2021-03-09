/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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
