/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.util.concurrent;
public abstract class RecursiveAction extends java.util.concurrent.ForkJoinTask<java.lang.Void> {
	public RecursiveAction() { } 
	protected abstract void compute();
	protected final boolean exec() { return false; }
	public final java.lang.Void getRawResult() { return null; }
	protected final void setRawResult(java.lang.Void var0) { }
}

