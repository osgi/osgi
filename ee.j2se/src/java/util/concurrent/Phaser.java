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
public class Phaser {
	public Phaser() { } 
	public Phaser(int var0) { } 
	public Phaser(java.util.concurrent.Phaser var0) { } 
	public Phaser(java.util.concurrent.Phaser var0, int var1) { } 
	public int arrive() { return 0; }
	public int arriveAndAwaitAdvance() { return 0; }
	public int arriveAndDeregister() { return 0; }
	public int awaitAdvance(int var0) { return 0; }
	public int awaitAdvanceInterruptibly(int var0) throws java.lang.InterruptedException { return 0; }
	public int awaitAdvanceInterruptibly(int var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException, java.util.concurrent.TimeoutException { return 0; }
	public int bulkRegister(int var0) { return 0; }
	public void forceTermination() { }
	public int getArrivedParties() { return 0; }
	public java.util.concurrent.Phaser getParent() { return null; }
	public final int getPhase() { return 0; }
	public int getRegisteredParties() { return 0; }
	public java.util.concurrent.Phaser getRoot() { return null; }
	public int getUnarrivedParties() { return 0; }
	public boolean isTerminated() { return false; }
	protected boolean onAdvance(int var0, int var1) { return false; }
	public int register() { return 0; }
}

