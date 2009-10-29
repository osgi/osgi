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

package javax.swing.text;
abstract class GapVector implements java.io.Serializable {
	public GapVector() { }
	public GapVector(int var0) { }
	protected abstract java.lang.Object allocateArray(int var0);
	protected final java.lang.Object getArray() { return null; }
	protected abstract int getArrayLength();
	protected final int getGapEnd() { return 0; }
	protected final int getGapStart() { return 0; }
	protected void replace(int var0, int var1, java.lang.Object var2, int var3) { }
	protected void shiftEnd(int var0) { }
	protected void shiftGap(int var0) { }
	protected void shiftGapEndUp(int var0) { }
	protected void shiftGapStartDown(int var0) { }
}

