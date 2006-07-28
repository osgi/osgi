/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.service.navigation;

import java.net.URL;

// ### I am at loss what the purpose of this interface is? If we need
// a hint and image, can they not be included in the RouteSegment?

public interface Hint extends RouteSegment {

	String getHint();
	
	// You can not use images in an OSGi device
	// because maybe there is no graphic env. or there
	// is a custom env.
	URL getImage();
}
