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

package java.awt.image;
public abstract interface ImageObserver {
	public abstract boolean imageUpdate(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5);
	public final static int ABORT = 128;
	public final static int ALLBITS = 32;
	public final static int ERROR = 64;
	public final static int FRAMEBITS = 16;
	public final static int HEIGHT = 2;
	public final static int PROPERTIES = 4;
	public final static int SOMEBITS = 8;
	public final static int WIDTH = 1;
}

