/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.tools.syncdb;

import java.io.*;

public class Main {
	public static void main(String args[]) {
		SyncDb syncDb = new SyncDb();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-password")) {
				syncDb.setPassword(args[++i]);
			}
			else
				if (args[i].equals("-principal")) {
					syncDb.setPrincipal(args[++i]);
				}
				else
					if (args[i].equals("-providerUrl")) {
						syncDb.setProviderUrl(args[++i]);
					}
					else
						if (args[i].equals("-file")) {
							File f = new File(args[++i]);
							if ( f.exists() ) 
								syncDb.setFile(f.getAbsolutePath());
							else {
								System.err.println("No such file: " + f );
							}
						}
						else
							throw new RuntimeException("Invalid option "
									+ args[i]);
		}
		syncDb.run();
	}
}
