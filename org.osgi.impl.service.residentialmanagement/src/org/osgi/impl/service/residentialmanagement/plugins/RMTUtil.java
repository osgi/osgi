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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Iterator;
import java.util.Vector;

public class RMTUtil {
	
	protected static String[] shapedPath(String[] nodePath, int i) {
		int size = nodePath.length;
		int srcPos = i;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}
	
	protected static String[] pathToArrayUri(String path) {
		Vector<String> vector = new Vector<>();
		while (path.indexOf("/") != -1) {
			String start_path = path.substring(0, path.indexOf("/"));
			vector.add(start_path);
			path = path.substring(path.indexOf("/") + 1, path.length());
		}
		String[] arrayPath = new String[vector.size()];
		int i = 0;
		for (Iterator<String> it = vector.iterator(); it.hasNext(); i++) {
			arrayPath[i] = it.next();
		}
		return arrayPath;
	}

	protected static String arrayToPathUri(String[] path) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length; i++) {
			sb.append(path[i]);
			sb.append("/");
		}
		return sb.toString();
	}
	
	protected static String arrayToPathUriWithoutSlash(String[] path) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length; i++) {
			sb.append(path[i]);
			sb.append("/");
		}
		String str = sb.toString();
		if (str.lastIndexOf("/") == str.length() - 1) {
			str = str.substring(0,str.lastIndexOf("/"));
		}
		return str;
	}

	
}
