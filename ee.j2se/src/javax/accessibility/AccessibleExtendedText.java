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

package javax.accessibility;
public interface AccessibleExtendedText {
	public final static int ATTRIBUTE_RUN = 5;
	public final static int LINE = 4;
	java.awt.Rectangle getTextBounds(int var0, int var1);
	java.lang.String getTextRange(int var0, int var1);
	javax.accessibility.AccessibleTextSequence getTextSequenceAfter(int var0, int var1);
	javax.accessibility.AccessibleTextSequence getTextSequenceAt(int var0, int var1);
	javax.accessibility.AccessibleTextSequence getTextSequenceBefore(int var0, int var1);
}

