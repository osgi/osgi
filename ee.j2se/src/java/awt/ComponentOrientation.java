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

package java.awt;
public final class ComponentOrientation implements java.io.Serializable {
	public final static java.awt.ComponentOrientation LEFT_TO_RIGHT; static { LEFT_TO_RIGHT = null; }
	public final static java.awt.ComponentOrientation RIGHT_TO_LEFT; static { RIGHT_TO_LEFT = null; }
	public final static java.awt.ComponentOrientation UNKNOWN; static { UNKNOWN = null; }
	public static java.awt.ComponentOrientation getOrientation(java.util.Locale var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public static java.awt.ComponentOrientation getOrientation(java.util.ResourceBundle var0) { return null; }
	public boolean isHorizontal() { return false; }
	public boolean isLeftToRight() { return false; }
	private ComponentOrientation() { } /* generated constructor to prevent compiler adding default public constructor */
}

