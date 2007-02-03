/*******************************************************************************
 * $Header$
 *  
 * Copyright (c) OSGi Alliance (2006, 2007). All Rights Reserved.
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
 *******************************************************************************/
package org.osgi.service.navigation.control;

import org.osgi.service.navigation.Path;

public class NavigationEvent {
	final static int GUIDANCE_STARTED = 1;
	final static int GUIDANCE_STOPPED = 2;
	final static int DEVIATED_FROM_PATH = 4;
	final static int ADVICE_PREPARE = 8; // prepare to turn left
	final static int ADVICE_IMMINENT = 16; // turn left in 200m
	final static int ADVICE_NOW = 32; // turn left	
	final static int ADVICE_COMPLETED = 64; 
	final static int NEXT_SEGMENT = 128;
	
	Path	path;
	
	public NavigationEvent(int type, Path path ) {
		this.path = path;
	}
	
	
	public Path getPath() {
		return path;
	}
}
