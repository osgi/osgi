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

package javax.lang.model;
public enum SourceVersion {
	RELEASE_0,
	RELEASE_1,
	RELEASE_2,
	RELEASE_3,
	RELEASE_4,
	RELEASE_5,
	RELEASE_6;
	public static boolean isIdentifier(java.lang.CharSequence var0) { return false; }
	public static boolean isKeyword(java.lang.CharSequence var0) { return false; }
	public static boolean isName(java.lang.CharSequence var0) { return false; }
	public static javax.lang.model.SourceVersion latest() { return null; }
	public static javax.lang.model.SourceVersion latestSupported() { return null; }
}

