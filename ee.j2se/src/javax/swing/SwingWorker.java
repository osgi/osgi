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

package javax.swing;
public abstract class SwingWorker<T,V> implements java.util.concurrent.RunnableFuture<T> {
	public enum StateValue {
		DONE,
		PENDING,
		STARTED;
	}
	public SwingWorker() { } 
	public final void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public final boolean cancel(boolean var0) { return false; }
	protected abstract T doInBackground() throws java.lang.Exception;
	protected void done() { }
	public final void execute() { }
	public final void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public final T get() throws java.lang.InterruptedException, java.util.concurrent.ExecutionException { return null; }
	public final T get(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException { return null; }
	public final int getProgress() { return 0; }
	public final java.beans.PropertyChangeSupport getPropertyChangeSupport() { return null; }
	public final javax.swing.SwingWorker.StateValue getState() { return null; }
	public final boolean isCancelled() { return false; }
	public final boolean isDone() { return false; }
	protected void process(java.util.List<V> var0) { }
	protected final void publish(V... var0) { }
	public final void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public final void run() { }
	protected final void setProgress(int var0) { }
}

