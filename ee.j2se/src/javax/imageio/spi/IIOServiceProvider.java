/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.imageio.spi;
public abstract class IIOServiceProvider implements javax.imageio.spi.RegisterableService {
	public IIOServiceProvider() { }
	public IIOServiceProvider(java.lang.String var0, java.lang.String var1) { }
	public abstract java.lang.String getDescription(java.util.Locale var0);
	public java.lang.String getVendorName() { return null; }
	public java.lang.String getVersion() { return null; }
	public void onDeregistration(javax.imageio.spi.ServiceRegistry var0, java.lang.Class var1) { }
	public void onRegistration(javax.imageio.spi.ServiceRegistry var0, java.lang.Class var1) { }
	protected java.lang.String vendorName;
	protected java.lang.String version;
}

