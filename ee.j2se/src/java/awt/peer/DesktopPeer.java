/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package java.awt.peer;
public interface DesktopPeer {
	void browse(java.net.URI var0) throws java.io.IOException;
	void edit(java.io.File var0) throws java.io.IOException;
	boolean isSupported(java.awt.Desktop.Action var0);
	void mail(java.net.URI var0) throws java.io.IOException;
	void open(java.io.File var0) throws java.io.IOException;
	void print(java.io.File var0) throws java.io.IOException;
}

