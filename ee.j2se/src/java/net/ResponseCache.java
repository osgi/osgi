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

package java.net;
public abstract class ResponseCache {
	public ResponseCache() { } 
	public abstract java.net.CacheResponse get(java.net.URI var0, java.lang.String var1, java.util.Map<java.lang.String,java.util.List<java.lang.String>> var2) throws java.io.IOException;
	public static java.net.ResponseCache getDefault() { return null; }
	public abstract java.net.CacheRequest put(java.net.URI var0, java.net.URLConnection var1) throws java.io.IOException;
	public static void setDefault(java.net.ResponseCache var0) { }
}

